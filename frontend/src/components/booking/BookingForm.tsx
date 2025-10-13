import React, { useState } from 'react';
import bookingService, { Booking } from '../../services/api/booking';

export type BookingFormValues = {
  destination: string;
  startDate: string;
  endDate: string;
  travelers: number;
  roomType: 'standard' | 'deluxe' | 'suite';
  userId: string;
};

type BookingFormProps = {
  initial?: Partial<BookingFormValues>;
  onSubmit?: (booking: Booking) => void;
  loading?: boolean;
};

const defaultValues: BookingFormValues = {
  destination: '',
  startDate: '',
  endDate: '',
  travelers: 1,
  roomType: 'standard',
  userId: '',
};

export const BookingForm: React.FC<BookingFormProps> = ({ initial, onSubmit, loading }) => {
  const [form, setForm] = useState<BookingFormValues>({ ...defaultValues, ...initial });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const calculatePrice = (): number => {
    const basePrice = 100;
    const days = form.startDate && form.endDate
      ? Math.ceil((new Date(form.endDate).getTime() - new Date(form.startDate).getTime()) / (1000 * 60 * 60 * 24))
      : 1;
    
    const roomMultiplier = {
      standard: 1,
      deluxe: 1.5,
      suite: 2.5,
    }[form.roomType];

    return basePrice * days * form.travelers * roomMultiplier;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: name === 'travelers' ? Number(value) : value,
    }));
    setError(null);
  };

  const validateForm = (): boolean => {
    if (!form.destination || !form.startDate || !form.endDate || !form.userId) {
      setError('Please fill in all required fields');
      return false;
    }

    if (new Date(form.endDate) <= new Date(form.startDate)) {
      setError('End date must be after start date');
      return false;
    }

    if (form.travelers < 1) {
      setError('Number of travelers must be at least 1');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);
    setError(null);

    try {
      const totalPrice = calculatePrice();
      const bookingData = {
        ...form,
        totalPrice,
      };

      const response = await bookingService.createBooking(bookingData);
      setSuccess(true);
      
      if (onSubmit) {
        onSubmit(response.booking);
      }

      // Reset form after successful submission
      setTimeout(() => {
        setForm(defaultValues);
        setSuccess(false);
      }, 2000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create booking. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="booking-form">
      <h2>Book Your Trip</h2>
      
      {error && <div className="alert alert-error">{error}</div>}
      {success && <div className="alert alert-success">Booking created successfully!</div>}

      <div className="form-group">
        <label htmlFor="destination">Destination *</label>
        <input
          type="text"
          id="destination"
          name="destination"
          value={form.destination}
          onChange={handleChange}
          placeholder="Enter destination"
          required
        />
      </div>

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="startDate">Start Date *</label>
          <input
            type="date"
            id="startDate"
            name="startDate"
            value={form.startDate}
            onChange={handleChange}
            min={new Date().toISOString().split('T')[0]}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="endDate">End Date *</label>
          <input
            type="date"
            id="endDate"
            name="endDate"
            value={form.endDate}
            onChange={handleChange}
            min={form.startDate || new Date().toISOString().split('T')[0]}
            required
          />
        </div>
      </div>

      <div className="form-row">
        <div className="form-group">
          <label htmlFor="travelers">Number of Travelers *</label>
          <input
            type="number"
            id="travelers"
            name="travelers"
            value={form.travelers}
            onChange={handleChange}
            min="1"
            max="10"
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="roomType">Room Type *</label>
          <select
            id="roomType"
            name="roomType"
            value={form.roomType}
            onChange={handleChange}
            required
          >
            <option value="standard">Standard</option>
            <option value="deluxe">Deluxe</option>
            <option value="suite">Suite</option>
          </select>
        </div>
      </div>

      <div className="form-group">
        <label htmlFor="userId">User ID *</label>
        <input
          type="text"
          id="userId"
          name="userId"
          value={form.userId}
          onChange={handleChange}
          placeholder="Enter your user ID"
          required
        />
      </div>

      <div className="price-summary">
        <h3>Total Price: ${calculatePrice().toFixed(2)}</h3>
      </div>

      <button 
        type="submit" 
        disabled={isSubmitting || loading}
        className="btn btn-primary"
      >
        {isSubmitting || loading ? 'Processing...' : 'Book Now'}
      </button>
    </form>
  );
};

export default BookingForm;
