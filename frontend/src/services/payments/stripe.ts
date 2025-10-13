// Stripe payment integration scaffolding
// TODO: wire with backend payment intent endpoints
import { api } from '../services/api/client';

export type CreatePaymentIntentPayload = {
  amount: number; // in smallest currency unit
  currency: string;
  metadata?: Record<string, string>;
};

export async function createPaymentIntent(payload: CreatePaymentIntentPayload) {
  const { data } = await api.post('/payments/create-intent', payload);
  return data as { clientSecret: string };
}

export async function confirmPayment(clientSecret: string, paymentMethodId: string) {
  // Placeholder: real confirmation handled via Stripe JS on client
  // This function exists to mirror server confirmation when needed
  const { data } = await api.post('/payments/confirm', { clientSecret, paymentMethodId });
  return data as { status: string };
}
