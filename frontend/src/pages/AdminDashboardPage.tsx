import React from 'react';

const AdminDashboardPage: React.FC = () => {
  // TODO: Protect route (admin only)
  return (
    <div className="p-6 space-y-4">
      <h1 className="text-2xl font-bold">Admin Dashboard</h1>
      <ul className="grid grid-cols-2 gap-4">
        <li className="card">Bookings Overview {/* TODO: charts */}</li>
        <li className="card">Revenue Metrics {/* TODO: charts */}</li>
        <li className="card">Inventory Manager {/* TODO: CRUD for flights/hotels */}</li>
        <li className="card">Users Management {/* TODO: list/search/suspend */}</li>
      </ul>
    </div>
  );
};

export default AdminDashboardPage;
