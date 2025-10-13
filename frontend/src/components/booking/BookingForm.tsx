import React, { useState } from 'react';

export type BookingFormValues = {
  origin: string;
  destination: string;
  departDate: string;
  returnDate?: string;
  passengers: number;
  classType: 'economy' | 'business' | 'first';
};

type BookingFormProps = {
  initial?: Partial<BookingFormValues>;
  onSubmit: (values: BookingFormValues) => void;
  loading?: boolean;
};

const defaultValues: BookingFormValues = {
  origin: '',
  destination: '',
  departDate: '',
  returnDate: '',
  passengers: 1,
  classType: 'economy',
};

export const BookingForm: React.FC<BookingFormProps> = ({ initial, onSubmit, loading }) => {
  const [form, setForm] = useState<BookingFormValues>({ ...defaultValues, ...initial });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: name === 'passengers' ? Number(value) : value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: add validation rules
    onSubmit(form);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-3">
      <div className="grid grid-cols-2 gap-3">
        <input name="origin" value={form.origin} onChange={handleChange} placeholder="From" required />
        <input name="destination" value={form.destination} onChange={handleChange} placeholder="To" required />
      </div>
      <div className="grid grid-cols-2 gap-3">
        <input type="date" name="departDate" value={form.departDate} onChange={handleChange} required />
        <input type="date" name="returnDate" value={form.returnDate} onChange={handleChange} />
      </div>
      <div className="grid grid-cols-2 gap-3">
        <select name="classType" value={form.classType} onChange={handleChange}>
          <option value="economy">Economy</option>
          <option value="business">Business</option>
          <option value="first">First</option>
        </select>
        <input type="number" min={1} max={9} name="passengers" value={form.passengers} onChange={handleChange} />
      </div>
      <button type="submit" disabled={loading} className="btn btn-primary w-full">
        {loading ? 'Searching…' : 'Search'}
      </button>
    </form>
  );
};

export default BookingForm;
