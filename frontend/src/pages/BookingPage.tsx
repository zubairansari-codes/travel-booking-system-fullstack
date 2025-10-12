import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import './BookingPage.css';

interface BookingFormData {
  checkInDate: string;
  checkOutDate: string;
  adults: number;
  children: number;
  specialRequests: string;
}

const BookingPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  const [currentStep, setCurrentStep] = useState<number>(1);
  const [formData, setFormData] = useState<BookingFormData>({
    checkInDate: '',
    checkOutDate: '',
    adults: 1,
    children: 0,
    specialRequests: ''
  });

  // Sample listing data (in real app, this would come from an API)
  const listing = {
    id: parseInt(id || '1'),
    name: 'Paris City Tour',
    location: 'Paris, France',
    price: 299,
    image: '/images/paris.jpg',
    rating: 4.8,
    type: 'tour',
    description: 'Explore the romantic city of Paris with guided tours to Eiffel Tower, Louvre, and more.'
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleNumberChange = (field: 'adults' | 'children', increment: boolean) => {
    setFormData(prev => {
      const currentValue = prev[field];
      const newValue = increment ? currentValue + 1 : Math.max(0, currentValue - 1);
      if (field === 'adults' && newValue < 1) return prev; // At least 1 adult required
      return {
        ...prev,
        [field]: newValue
      };
    });
  };

  const calculateTotalPrice = (): number => {
    const basePrice = listing.price;
    const days = formData.checkInDate && formData.checkOutDate 
      ? Math.max(1, Math.ceil((new Date(formData.checkOutDate).getTime() - new Date(formData.checkInDate).getTime()) / (1000 * 3600 * 24)))
      : 1;
    const guestMultiplier = formData.adults + (formData.children * 0.5);
    return basePrice * days * guestMultiplier;
  };

  const canProceedToNextStep = (): boolean => {
    if (currentStep === 1) {
      return formData.checkInDate !== '' && formData.checkOutDate !== '';
    }
    if (currentStep === 2) {
      return formData.adults >= 1;
    }
    return true;
  };

  const handleNext = () => {
    if (canProceedToNextStep() && currentStep < 3) {
      setCurrentStep(currentStep + 1);
    }
  };

  const handleBack = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const handleSubmit = () => {
    // In a real app, this would submit to an API
    alert('Booking submitted successfully!');
    navigate('/');
  };

  const getTodayDate = (): string => {
    return new Date().toISOString().split('T')[0];
  };

  return (
    <div className="booking-page">
      <div className="booking-container">
        <div className="booking-header">
          <button className="back-button" onClick={() => navigate('/')}>
            ← Back to Home
          </button>
          <h1>Complete Your Booking</h1>
        </div>

        {/* Progress Indicator */}
        <div className="progress-bar">
          <div className={`progress-step ${currentStep >= 1 ? 'active' : ''} ${currentStep > 1 ? 'completed' : ''}`}>
            <div className="step-circle">1</div>
            <span>Dates</span>
          </div>
          <div className="progress-line"></div>
          <div className={`progress-step ${currentStep >= 2 ? 'active' : ''} ${currentStep > 2 ? 'completed' : ''}`}>
            <div className="step-circle">2</div>
            <span>Guests</span>
          </div>
          <div className="progress-line"></div>
          <div className={`progress-step ${currentStep >= 3 ? 'active' : ''}`}>
            <div className="step-circle">3</div>
            <span>Review</span>
          </div>
        </div>

        <div className="booking-content">
          {/* Listing Summary Sidebar */}
          <div className="listing-summary">
            <img src={listing.image} alt={listing.name} className="summary-image" />
            <h3>{listing.name}</h3>
            <p className="summary-location">📍 {listing.location}</p>
            <p className="summary-type">{listing.type}</p>
            <div className="summary-rating">⭐ {listing.rating}</div>
            <div className="price-breakdown">
              <h4>Price Summary</h4>
              <div className="price-row">
                <span>Base Price:</span>
                <span>${listing.price}</span>
              </div>
              <div className="price-row">
                <span>Guests:</span>
                <span>{formData.adults} adults, {formData.children} children</span>
              </div>
              {formData.checkInDate && formData.checkOutDate && (
                <div className="price-row">
                  <span>Duration:</span>
                  <span>
                    {Math.max(1, Math.ceil((new Date(formData.checkOutDate).getTime() - new Date(formData.checkInDate).getTime()) / (1000 * 3600 * 24)))} days
                  </span>
                </div>
              )}
              <hr />
              <div className="price-row total">
                <strong>Total:</strong>
                <strong>${calculateTotalPrice().toFixed(2)}</strong>
              </div>
            </div>
          </div>

          {/* Booking Form Steps */}
          <div className="booking-form">
            {/* Step 1: Date Selection */}
            {currentStep === 1 && (
              <div className="form-step">
                <h2>Select Your Dates</h2>
                <div className="form-group">
                  <label htmlFor="checkInDate">Check-in Date</label>
                  <input
                    type="date"
                    id="checkInDate"
                    name="checkInDate"
                    value={formData.checkInDate}
                    onChange={handleInputChange}
                    min={getTodayDate()}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="checkOutDate">Check-out Date</label>
                  <input
                    type="date"
                    id="checkOutDate"
                    name="checkOutDate"
                    value={formData.checkOutDate}
                    onChange={handleInputChange}
                    min={formData.checkInDate || getTodayDate()}
                    required
                  />
                </div>
                <div className="form-actions">
                  <button 
                    className="btn-primary" 
                    onClick={handleNext}
                    disabled={!canProceedToNextStep()}
                  >
                    Next Step →
                  </button>
                </div>
              </div>
            )}

            {/* Step 2: Guest Selection */}
            {currentStep === 2 && (
              <div className="form-step">
                <h2>Number of Guests</h2>
                <div className="guest-selector">
                  <div className="guest-row">
                    <div className="guest-info">
                      <strong>Adults</strong>
                      <span>Ages 13 or above</span>
                    </div>
                    <div className="guest-controls">
                      <button 
                        className="guest-btn" 
                        onClick={() => handleNumberChange('adults', false)}
                        disabled={formData.adults <= 1}
                      >
                        -
                      </button>
                      <span className="guest-count">{formData.adults}</span>
                      <button 
                        className="guest-btn" 
                        onClick={() => handleNumberChange('adults', true)}
                      >
                        +
                      </button>
                    </div>
                  </div>
                  <div className="guest-row">
                    <div className="guest-info">
                      <strong>Children</strong>
                      <span>Ages 2-12</span>
                    </div>
                    <div className="guest-controls">
                      <button 
                        className="guest-btn" 
                        onClick={() => handleNumberChange('children', false)}
                        disabled={formData.children <= 0}
                      >
                        -
                      </button>
                      <span className="guest-count">{formData.children}</span>
                      <button 
                        className="guest-btn" 
                        onClick={() => handleNumberChange('children', true)}
                      >
                        +
                      </button>
                    </div>
                  </div>
                </div>
                <div className="form-actions">
                  <button className="btn-secondary" onClick={handleBack}>
                    ← Back
                  </button>
                  <button 
                    className="btn-primary" 
                    onClick={handleNext}
                    disabled={!canProceedToNextStep()}
                  >
                    Next Step →
                  </button>
                </div>
              </div>
            )}

            {/* Step 3: Review & Confirm */}
            {currentStep === 3 && (
              <div className="form-step">
                <h2>Review Your Booking</h2>
                <div className="review-section">
                  <div className="review-item">
                    <strong>Dates:</strong>
                    <p>{formData.checkInDate} to {formData.checkOutDate}</p>
                  </div>
                  <div className="review-item">
                    <strong>Guests:</strong>
                    <p>{formData.adults} adults, {formData.children} children</p>
                  </div>
                  <div className="review-item">
                    <strong>Total Price:</strong>
                    <p className="review-price">${calculateTotalPrice().toFixed(2)}</p>
                  </div>
                </div>
                <div className="form-group">
                  <label htmlFor="specialRequests">Special Requests (Optional)</label>
                  <textarea
                    id="specialRequests"
                    name="specialRequests"
                    value={formData.specialRequests}
                    onChange={handleInputChange}
                    rows={4}
                    placeholder="Any special requirements or requests..."
                  />
                </div>
                <div className="form-actions">
                  <button className="btn-secondary" onClick={handleBack}>
                    ← Back
                  </button>
                  <button className="btn-success" onClick={handleSubmit}>
                    Confirm Booking ✓
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default BookingPage;
