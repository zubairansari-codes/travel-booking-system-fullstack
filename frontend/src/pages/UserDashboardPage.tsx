import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import bookingService, { Booking } from '../services/api/booking';
import './UserDashboardPage.css';

interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  address: string;
}

const UserDashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<'profile' | 'bookings'>('bookings');
  const [isEditingProfile, setIsEditingProfile] = useState(false);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  const [user, setUser] = useState<User>({
    id: '1',
    name: 'John Doe',
    email: 'john.doe@example.com',
    phone: '+1234567890',
    address: '123 Main Street, City, Country'
  });

  // Fetch user bookings on component mount
  useEffect(() => {
    fetchUserBookings();
  }, []);

  const fetchUserBookings = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await bookingService.getUserBookings(user.id);
      setBookings(data);
    } catch (err: any) {
      console.error('Error fetching bookings:', err);
      setError(err.response?.data?.message || 'Failed to load bookings');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelBooking = async (bookingId: string) => {
    if (!window.confirm('Are you sure you want to cancel this booking?')) {
      return;
    }

    try {
      await bookingService.cancelBooking(bookingId);
      // Refresh bookings list
      await fetchUserBookings();
      alert('Booking cancelled successfully');
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to cancel booking');
    }
  };

  const handleProfileUpdate = (e: React.FormEvent) => {
    e.preventDefault();
    // In a real app, this would call an API to update the user profile
    setIsEditingProfile(false);
    alert('Profile updated successfully!');
  };

  const getBookingStatusBadge = (status?: string) => {
    const statusClass = {
      pending: 'status-badge status-pending',
      confirmed: 'status-badge status-confirmed',
      cancelled: 'status-badge status-cancelled',
    }[status || 'pending'];

    return <span className={statusClass}>{status || 'pending'}</span>;
  };

  const getPaymentStatusBadge = (status?: string) => {
    const statusClass = {
      pending: 'payment-badge payment-pending',
      completed: 'payment-badge payment-completed',
      failed: 'payment-badge payment-failed',
    }[status || 'pending'];

    return <span className={statusClass}>{status || 'pending'}</span>;
  };

  const upcomingBookings = bookings.filter(
    (booking) => new Date(booking.startDate) >= new Date() && booking.status !== 'cancelled'
  );

  const pastBookings = bookings.filter(
    (booking) => new Date(booking.endDate) < new Date() || booking.status === 'cancelled'
  );

  return (
    <div className="user-dashboard">
      <div className="dashboard-header">
        <h1>My Dashboard</h1>
        <p>Welcome back, {user.name}!</p>
      </div>

      <div className="dashboard-tabs">
        <button
          className={`tab ${activeTab === 'bookings' ? 'active' : ''}`}
          onClick={() => setActiveTab('bookings')}
        >
          My Bookings ({bookings.length})
        </button>
        <button
          className={`tab ${activeTab === 'profile' ? 'active' : ''}`}
          onClick={() => setActiveTab('profile')}
        >
          Profile Settings
        </button>
      </div>

      <div className="dashboard-content">
        {activeTab === 'bookings' && (
          <div className="bookings-section">
            <div className="bookings-header">
              <h2>My Bookings</h2>
              <button onClick={() => navigate('/booking')} className="btn btn-primary">
                + New Booking
              </button>
            </div>

            {loading ? (
              <div className="loading">Loading bookings...</div>
            ) : error ? (
              <div className="error-message">
                <p>{error}</p>
                <button onClick={fetchUserBookings} className="btn btn-secondary">
                  Retry
                </button>
              </div>
            ) : bookings.length === 0 ? (
              <div className="no-bookings">
                <p>You don't have any bookings yet.</p>
                <button onClick={() => navigate('/booking')} className="btn btn-primary">
                  Create Your First Booking
                </button>
              </div>
            ) : (
              <>
                {upcomingBookings.length > 0 && (
                  <div className="bookings-group">
                    <h3>Upcoming Trips</h3>
                    <div className="bookings-list">
                      {upcomingBookings.map((booking) => (
                        <div key={booking.id} className="booking-card">
                          <div className="booking-header">
                            <h4>{booking.destination}</h4>
                            <div className="booking-badges">
                              {getBookingStatusBadge(booking.status)}
                              {getPaymentStatusBadge(booking.paymentStatus)}
                            </div>
                          </div>
                          <div className="booking-details">
                            <p><strong>Booking ID:</strong> {booking.id}</p>
                            <p><strong>Check-in:</strong> {new Date(booking.startDate).toLocaleDateString()}</p>
                            <p><strong>Check-out:</strong> {new Date(booking.endDate).toLocaleDateString()}</p>
                            <p><strong>Travelers:</strong> {booking.travelers}</p>
                            <p><strong>Room Type:</strong> {booking.roomType}</p>
                            <p><strong>Total Price:</strong> ${booking.totalPrice.toFixed(2)}</p>
                          </div>
                          <div className="booking-actions">
                            {booking.paymentStatus !== 'completed' && (
                              <button
                                onClick={() => navigate(`/payment/${booking.id}`)}
                                className="btn btn-primary"
                              >
                                Complete Payment
                              </button>
                            )}
                            <button
                              onClick={() => handleCancelBooking(booking.id!)}
                              className="btn btn-danger"
                              disabled={booking.status === 'cancelled'}
                            >
                              Cancel Booking
                            </button>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {pastBookings.length > 0 && (
                  <div className="bookings-group">
                    <h3>Past & Cancelled Bookings</h3>
                    <div className="bookings-list">
                      {pastBookings.map((booking) => (
                        <div key={booking.id} className="booking-card booking-past">
                          <div className="booking-header">
                            <h4>{booking.destination}</h4>
                            <div className="booking-badges">
                              {getBookingStatusBadge(booking.status)}
                              {getPaymentStatusBadge(booking.paymentStatus)}
                            </div>
                          </div>
                          <div className="booking-details">
                            <p><strong>Booking ID:</strong> {booking.id}</p>
                            <p><strong>Check-in:</strong> {new Date(booking.startDate).toLocaleDateString()}</p>
                            <p><strong>Check-out:</strong> {new Date(booking.endDate).toLocaleDateString()}</p>
                            <p><strong>Travelers:</strong> {booking.travelers}</p>
                            <p><strong>Room Type:</strong> {booking.roomType}</p>
                            <p><strong>Total Price:</strong> ${booking.totalPrice.toFixed(2)}</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </>
            )}
          </div>
        )}

        {activeTab === 'profile' && (
          <div className="profile-section">
            <h2>Profile Settings</h2>
            <form onSubmit={handleProfileUpdate} className="profile-form">
              <div className="form-group">
                <label htmlFor="name">Name</label>
                <input
                  type="text"
                  id="name"
                  value={user.name}
                  onChange={(e) => setUser({ ...user, name: e.target.value })}
                  disabled={!isEditingProfile}
                />
              </div>
              <div className="form-group">
                <label htmlFor="email">Email</label>
                <input
                  type="email"
                  id="email"
                  value={user.email}
                  onChange={(e) => setUser({ ...user, email: e.target.value })}
                  disabled={!isEditingProfile}
                />
              </div>
              <div className="form-group">
                <label htmlFor="phone">Phone</label>
                <input
                  type="tel"
                  id="phone"
                  value={user.phone}
                  onChange={(e) => setUser({ ...user, phone: e.target.value })}
                  disabled={!isEditingProfile}
                />
              </div>
              <div className="form-group">
                <label htmlFor="address">Address</label>
                <textarea
                  id="address"
                  value={user.address}
                  onChange={(e) => setUser({ ...user, address: e.target.value })}
                  disabled={!isEditingProfile}
                  rows={3}
                />
              </div>
              <div className="form-actions">
                {isEditingProfile ? (
                  <>
                    <button type="submit" className="btn btn-primary">
                      Save Changes
                    </button>
                    <button
                      type="button"
                      onClick={() => setIsEditingProfile(false)}
                      className="btn btn-secondary"
                    >
                      Cancel
                    </button>
                  </>
                ) : (
                  <button
                    type="button"
                    onClick={() => setIsEditingProfile(true)}
                    className="btn btn-primary"
                  >
                    Edit Profile
                  </button>
                )}
              </div>
            </form>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserDashboardPage;
