// Stripe payment integration with comprehensive payment flow logic
import apiClient from '../api/client';
import { Booking } from '../api/booking';

export interface PaymentIntent {
  id: string;
  clientSecret: string;
  amount: number;
  currency: string;
  status: 'requires_payment_method' | 'requires_confirmation' | 'requires_action' | 'processing' | 'succeeded' | 'canceled';
}

export interface PaymentMethod {
  id: string;
  type: string;
  card?: {
    brand: string;
    last4: string;
    expMonth: number;
    expYear: number;
  };
}

export interface PaymentDetails {
  bookingId: string;
  amount: number;
  currency: string;
  paymentMethodId?: string;
  metadata?: Record<string, string>;
}

export interface PaymentResult {
  success: boolean;
  paymentIntent?: PaymentIntent;
  error?: string;
}

// Initialize Stripe (Note: In production, load Stripe.js from CDN)
let stripeInstance: any = null;

export const initializeStripe = (publishableKey: string) => {
  if (typeof window !== 'undefined' && (window as any).Stripe) {
    stripeInstance = (window as any).Stripe(publishableKey);
  }
  return stripeInstance;
};

// Create a payment intent for a booking
export const createPaymentIntent = async (paymentDetails: PaymentDetails): Promise<PaymentIntent> => {
  try {
    const response = await apiClient.post('/payments/create-intent', {
      amount: Math.round(paymentDetails.amount * 100), // Convert to cents
      currency: paymentDetails.currency || 'usd',
      bookingId: paymentDetails.bookingId,
      metadata: paymentDetails.metadata || {},
    });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Failed to create payment intent');
  }
};

// Confirm a payment with Stripe
export const confirmPayment = async (
  clientSecret: string,
  paymentMethodId: string
): Promise<PaymentResult> => {
  try {
    if (!stripeInstance) {
      throw new Error('Stripe not initialized. Call initializeStripe first.');
    }

    const { error, paymentIntent } = await stripeInstance.confirmCardPayment(clientSecret, {
      payment_method: paymentMethodId,
    });

    if (error) {
      return {
        success: false,
        error: error.message,
      };
    }

    return {
      success: true,
      paymentIntent,
    };
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Payment confirmation failed',
    };
  }
};

// Process a complete payment flow
export const processPayment = async (
  booking: Booking,
  paymentMethodId: string
): Promise<PaymentResult> => {
  try {
    // Step 1: Create payment intent
    const paymentIntent = await createPaymentIntent({
      bookingId: booking.id!,
      amount: booking.totalPrice,
      currency: 'usd',
      metadata: {
        bookingId: booking.id!,
        destination: booking.destination,
        userId: booking.userId,
      },
    });

    // Step 2: Confirm payment with Stripe
    const result = await confirmPayment(paymentIntent.clientSecret, paymentMethodId);

    if (result.success) {
      // Step 3: Update booking payment status
      await updateBookingPaymentStatus(booking.id!, 'completed');
    }

    return result;
  } catch (error: any) {
    return {
      success: false,
      error: error.message || 'Payment processing failed',
    };
  }
};

// Update booking payment status
const updateBookingPaymentStatus = async (
  bookingId: string,
  status: 'pending' | 'completed' | 'failed'
): Promise<void> => {
  try {
    await apiClient.patch(`/bookings/${bookingId}/payment-status`, { paymentStatus: status });
  } catch (error: any) {
    console.error('Failed to update payment status:', error);
    throw error;
  }
};

// Get payment history for a booking
export const getPaymentHistory = async (bookingId: string): Promise<any[]> => {
  try {
    const response = await apiClient.get(`/payments/booking/${bookingId}`);
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Failed to fetch payment history');
  }
};

// Create a payment method (card details)
export const createPaymentMethod = async (cardDetails: {
  number: string;
  expMonth: number;
  expYear: number;
  cvc: string;
}): Promise<PaymentMethod> => {
  try {
    if (!stripeInstance) {
      throw new Error('Stripe not initialized');
    }

    const { error, paymentMethod } = await stripeInstance.createPaymentMethod({
      type: 'card',
      card: {
        number: cardDetails.number,
        exp_month: cardDetails.expMonth,
        exp_year: cardDetails.expYear,
        cvc: cardDetails.cvc,
      },
    });

    if (error) {
      throw new Error(error.message);
    }

    return paymentMethod;
  } catch (error: any) {
    throw new Error(error.message || 'Failed to create payment method');
  }
};

// Refund a payment
export const refundPayment = async (paymentIntentId: string, amount?: number): Promise<any> => {
  try {
    const response = await apiClient.post('/payments/refund', {
      paymentIntentId,
      amount: amount ? Math.round(amount * 100) : undefined,
    });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Failed to process refund');
  }
};

// Retrieve payment intent details
export const retrievePaymentIntent = async (paymentIntentId: string): Promise<PaymentIntent> => {
  try {
    const response = await apiClient.get(`/payments/intent/${paymentIntentId}`);
    return response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Failed to retrieve payment intent');
  }
};

// Helper function to format amount for display
export const formatAmount = (amount: number, currency: string = 'USD'): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: currency.toUpperCase(),
  }).format(amount);
};

// Helper function to validate card number (basic Luhn algorithm)
export const validateCardNumber = (cardNumber: string): boolean => {
  const cleaned = cardNumber.replace(/\s/g, '');
  if (!/^\d+$/.test(cleaned)) return false;

  let sum = 0;
  let isEven = false;

  for (let i = cleaned.length - 1; i >= 0; i--) {
    let digit = parseInt(cleaned[i], 10);

    if (isEven) {
      digit *= 2;
      if (digit > 9) digit -= 9;
    }

    sum += digit;
    isEven = !isEven;
  }

  return sum % 10 === 0;
};

// Export stripe service object
export const stripeService = {
  initializeStripe,
  createPaymentIntent,
  confirmPayment,
  processPayment,
  getPaymentHistory,
  createPaymentMethod,
  refundPayment,
  retrievePaymentIntent,
  formatAmount,
  validateCardNumber,
};

export default stripeService;
