import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BookingForm } from '../components/booking/BookingForm';
import { Booking } from '../services/api/booking';
import './BookingPage.css';

const BookingPage: React.FC = () => {
  const navigate = useNavigate();
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [createdBooking, setCreatedBooking] = useState<Booking | null>(null);

  const handleBookingSubmit = (booking: Booking) => {
    setCreatedBooking(booking);
    setShowConfirmation(true);

    // Redirect to user dashboard after 3 seconds
    setTimeout(() => {
      navigate('/dashboard');
    }, 3000);
  };

  const handleProceedToPayment = () => {
    if (createdBooking) {
      // Redirect to payment page with booking ID
      navigate(`/payment/${createdBooking.id}`);
    }
  };

  return (
    <div className="booking-page">
      <div className="booking-container">
        <div className="booking-header">
          <h1>Create Your Booking</h1>
          <p>Fill in the details below to book your travel experience</p>
        </div>

        {!showConfirmation ? (
          <div className="booking-form-section">
            <BookingForm onSubmit={handleBookingSubmit} />
          </div>
        ) : (
          <div className="booking-confirmation">
            <div className="confirmation-icon">✓</div>
            <h2>Booking Confirmed!</h2>
            <div className="confirmation-details">
              <p><strong>Booking ID:</strong> {createdBooking?.id}</p>
              <p><strong>Destination:</strong> {createdBooking?.destination}</p>
              <p><strong>Check-in:</strong> {new Date(createdBooking?.startDate || '').toLocaleDateString()}</p>
              <p><strong>Check-out:</strong> {new Date(createdBooking?.endDate || '').toLocaleDateString()}</p>
              <p><strong>Travelers:</strong> {createdBooking?.travelers}</p>
              <p><strong>Room Type:</strong> {createdBooking?.roomType}</p>
              <p className="total-price"><strong>Total:</strong> ${createdBooking?.totalPrice.toFixed(2)}</p>
            </div>
            <div className="confirmation-actions">
              <button onClick={handleProceedToPayment} className="btn btn-primary">
                Proceed to Payment
              </button>
              <button onClick={() => navigate('/dashboard')} className="btn btn-secondary">
                View My Bookings
              </button>
            </div>
            <p className="redirect-notice">Redirecting to dashboard in 3 seconds...</p>
          </div>
        )}
      </div>

      <div className="booking-info">
        <div className="info-card">
          <h3>🏨 About Your Stay</h3>
          <ul>
            <li>Free cancellation up to 24 hours before check-in</li>
            <li>24/7 customer support</li>
            <li>Instant booking confirmation</li>
            <li>Secure payment processing</li>
          </ul>
        </div>

        <div className="info-card">
          <h3>💳 Payment Options</h3>
          <ul>
            <li>Credit/Debit Cards</li>
            <li>Stripe Payment Gateway</li>
            <li>Secure SSL Encryption</li>
            <li>Multiple Currency Support</li>
          </ul>
        </div>

        <div className="info-card">
          <h3>📞 Need Help?</h3>
          <p>Our support team is available 24/7 to assist you with your booking.</p>
          <p><strong>Email:</strong> support@travelbooking.com</p>
          <p><strong>Phone:</strong> +1 (555) 123-4567</p>
        </div>
      </div>
    </div>
  );
};

export default BookingPage;
