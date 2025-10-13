import apiClient from './client';

export interface Booking {
  id?: string;
  userId: string;
  destination: string;
  startDate: string;
  endDate: string;
  travelers: number;
  roomType: string;
  totalPrice: number;
  status?: 'pending' | 'confirmed' | 'cancelled';
  paymentStatus?: 'pending' | 'completed' | 'failed';
  createdAt?: string;
}

export interface BookingResponse {
  booking: Booking;
  message: string;
}

export const bookingService = {
  // Create a new booking
  createBooking: async (bookingData: Omit<Booking, 'id' | 'createdAt'>): Promise<BookingResponse> => {
    const response = await apiClient.post('/bookings', bookingData);
    return response.data;
  },

  // Get all bookings for a user
  getUserBookings: async (userId: string): Promise<Booking[]> => {
    const response = await apiClient.get(`/bookings/user/${userId}`);
    return response.data;
  },

  // Get a single booking by ID
  getBookingById: async (bookingId: string): Promise<Booking> => {
    const response = await apiClient.get(`/bookings/${bookingId}`);
    return response.data;
  },

  // Update booking status
  updateBookingStatus: async (bookingId: string, status: string): Promise<BookingResponse> => {
    const response = await apiClient.patch(`/bookings/${bookingId}/status`, { status });
    return response.data;
  },

  // Cancel a booking
  cancelBooking: async (bookingId: string): Promise<BookingResponse> => {
    const response = await apiClient.delete(`/bookings/${bookingId}`);
    return response.data;
  },

  // Get all bookings (admin)
  getAllBookings: async (): Promise<Booking[]> => {
    const response = await apiClient.get('/bookings');
    return response.data;
  },

  // Get booking statistics (admin)
  getBookingStats: async (): Promise<any> => {
    const response = await apiClient.get('/bookings/stats');
    return response.data;
  },
};

export default bookingService;
