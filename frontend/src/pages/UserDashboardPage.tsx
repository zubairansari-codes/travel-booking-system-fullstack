import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './UserDashboardPage.css';

interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  address: string;
}

interface Booking {
  id: string;
  destination: string;
  startDate: string;
  endDate: string;
  travelers: number;
  totalPrice: number;
  status: 'upcoming' | 'past' | 'cancelled';
  bookingDate: string;
  packageName: string;
}

const UserDashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<'profile' | 'upcoming' | 'past'>('profile');
  const [isEditingProfile, setIsEditingProfile] = useState(false);
  const [user, setUser] = useState<User>({
    id: '1',
    name: 'John Doe',
    email: 'john.doe@example.com',
    phone: '+1234567890',
    address: '123 Main Street, City, Country'
  });
  
  const [editedUser, setEditedUser] = useState<User>(user);
  const [bookings, setBookings] = useState<Booking[]>([
    {
      id: 'B001',
      destination: 'Paris, France',
      startDate: '2025-11-15',
      endDate: '2025-11-22',
      travelers: 2,
      totalPrice: 2500,
      status: 'upcoming',
      bookingDate: '2025-10-01',
      packageName: 'Romantic Paris Getaway'
    },
    {
      id: 'B002',
      destination: 'Tokyo, Japan',
      startDate: '2025-12-10',
      endDate: '2025-12-20',
      travelers: 3,
      totalPrice: 4500,
      status: 'upcoming',
      bookingDate: '2025-10-05',
      packageName: 'Tokyo Adventure Package'
    },
    {
      id: 'B003',
      destination: 'Bali, Indonesia',
      startDate: '2025-08-01',
      endDate: '2025-08-10',
      travelers: 2,
      totalPrice: 1800,
      status: 'past',
      bookingDate: '2025-07-01',
      packageName: 'Tropical Paradise Retreat'
    }
  ]);

  useEffect(() => {
    // Fetch user data from API
    // fetchUserData();
    
    // Fetch bookings from API
    // fetchBookings();
  }, []);

  const handleProfileEdit = () => {
    setEditedUser(user);
    setIsEditingProfile(true);
  };

  const handleProfileSave = () => {
    // API call to update user profile
    setUser(editedUser);
    setIsEditingProfile(false);
    alert('Profile updated successfully!');
  };

  const handleProfileCancel = () => {
    setEditedUser(user);
    setIsEditingProfile(false);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setEditedUser(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleCancelBooking = (bookingId: string) => {
    const confirmed = window.confirm('Are you sure you want to cancel this booking?');
    if (confirmed) {
      // API call to cancel booking
      setBookings(prev => prev.map(booking => 
        booking.id === bookingId 
          ? { ...booking, status: 'cancelled' as const }
          : booking
      ));
      alert('Booking cancelled successfully!');
    }
  };

  const handleModifyBooking = (bookingId: string) => {
    // Navigate to booking modification page
    navigate(`/modify-booking/${bookingId}`);
  };

  const upcomingBookings = bookings.filter(b => b.status === 'upcoming');
  const pastBookings = bookings.filter(b => b.status === 'past' || b.status === 'cancelled');

  const renderProfileTab = () => (
    <div className="profile-section">
      <div className="profile-header">
        <h2>Profile Information</h2>
        {!isEditingProfile && (
          <button className="btn-edit" onClick={handleProfileEdit}>
            Edit Profile
          </button>
        )}
      </div>

      <div className="profile-content">
        {!isEditingProfile ? (
          <div className="profile-display">
            <div className="profile-field">
              <label>Name:</label>
              <span>{user.name}</span>
            </div>
            <div className="profile-field">
              <label>Email:</label>
              <span>{user.email}</span>
            </div>
            <div className="profile-field">
              <label>Phone:</label>
              <span>{user.phone}</span>
            </div>
            <div className="profile-field">
              <label>Address:</label>
              <span>{user.address}</span>
            </div>
          </div>
        ) : (
          <div className="profile-edit">
            <div className="form-group">
              <label htmlFor="name">Name</label>
              <input
                type="text"
                id="name"
                name="name"
                value={editedUser.name}
                onChange={handleInputChange}
                className="form-control"
              />
            </div>
            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                name="email"
                value={editedUser.email}
                onChange={handleInputChange}
                className="form-control"
              />
            </div>
            <div className="form-group">
              <label htmlFor="phone">Phone</label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={editedUser.phone}
                onChange={handleInputChange}
                className="form-control"
              />
            </div>
            <div className="form-group">
              <label htmlFor="address">Address</label>
              <textarea
                id="address"
                name="address"
                value={editedUser.address}
                onChange={handleInputChange}
                className="form-control"
                rows={3}
              />
            </div>
            <div className="form-actions">
              <button className="btn-save" onClick={handleProfileSave}>
                Save Changes
              </button>
              <button className="btn-cancel" onClick={handleProfileCancel}>
                Cancel
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );

  const renderBookingCard = (booking: Booking) => (
    <div key={booking.id} className={`booking-card ${booking.status}`}>
      <div className="booking-header">
        <h3>{booking.packageName}</h3>
        <span className={`status-badge ${booking.status}`}>
          {booking.status.toUpperCase()}
        </span>
      </div>
      <div className="booking-details">
        <div className="detail-row">
          <span className="label">Booking ID:</span>
          <span className="value">{booking.id}</span>
        </div>
        <div className="detail-row">
          <span className="label">Destination:</span>
          <span className="value">{booking.destination}</span>
        </div>
        <div className="detail-row">
          <span className="label">Travel Dates:</span>
          <span className="value">
            {new Date(booking.startDate).toLocaleDateString()} - {new Date(booking.endDate).toLocaleDateString()}
          </span>
        </div>
        <div className="detail-row">
          <span className="label">Travelers:</span>
          <span className="value">{booking.travelers}</span>
        </div>
        <div className="detail-row">
          <span className="label">Total Price:</span>
          <span className="value price">${booking.totalPrice}</span>
        </div>
        <div className="detail-row">
          <span className="label">Booked On:</span>
          <span className="value">{new Date(booking.bookingDate).toLocaleDateString()}</span>
        </div>
      </div>
      {booking.status === 'upcoming' && (
        <div className="booking-actions">
          <button 
            className="btn-modify" 
            onClick={() => handleModifyBooking(booking.id)}
          >
            Modify Booking
          </button>
          <button 
            className="btn-cancel-booking" 
            onClick={() => handleCancelBooking(booking.id)}
          >
            Cancel Booking
          </button>
        </div>
      )}
    </div>
  );

  const renderUpcomingBookings = () => (
    <div className="bookings-section">
      <h2>Upcoming Bookings</h2>
      {upcomingBookings.length > 0 ? (
        <div className="bookings-grid">
          {upcomingBookings.map(renderBookingCard)}
        </div>
      ) : (
        <div className="no-bookings">
          <p>You have no upcoming bookings.</p>
          <button className="btn-primary" onClick={() => navigate('/search')}>
            Explore Destinations
          </button>
        </div>
      )}
    </div>
  );

  const renderPastBookings = () => (
    <div className="bookings-section">
      <h2>Past Bookings</h2>
      {pastBookings.length > 0 ? (
        <div className="bookings-grid">
          {pastBookings.map(renderBookingCard)}
        </div>
      ) : (
        <div className="no-bookings">
          <p>You have no past bookings.</p>
        </div>
      )}
    </div>
  );

  return (
    <div className="user-dashboard">
      <div className="dashboard-container">
        <div className="dashboard-header">
          <h1>My Dashboard</h1>
          <p>Welcome back, {user.name}!</p>
        </div>

        <div className="dashboard-tabs">
          <button
            className={`tab-button ${activeTab === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('profile')}
          >
            Profile
          </button>
          <button
            className={`tab-button ${activeTab === 'upcoming' ? 'active' : ''}`}
            onClick={() => setActiveTab('upcoming')}
          >
            Upcoming Bookings
            {upcomingBookings.length > 0 && (
              <span className="badge">{upcomingBookings.length}</span>
            )}
          </button>
          <button
            className={`tab-button ${activeTab === 'past' ? 'active' : ''}`}
            onClick={() => setActiveTab('past')}
          >
            Past Bookings
          </button>
        </div>

        <div className="dashboard-content">
          {activeTab === 'profile' && renderProfileTab()}
          {activeTab === 'upcoming' && renderUpcomingBookings()}
          {activeTab === 'past' && renderPastBookings()}
        </div>
      </div>
    </div>
  );
};

export default UserDashboardPage;
