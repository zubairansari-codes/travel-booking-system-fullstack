import React, { useState, useEffect } from 'react';
import bookingService from '../services/api/booking';
import './AdminDashboardPage.css';

interface DashboardStats {
  totalBookings: number;
  totalRevenue: number;
  activeUsers: number;
  completedPayments: number;
  pendingBookings: number;
  cancelledBookings: number;
}

const AdminDashboardPage: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats>({
    totalBookings: 0,
    totalRevenue: 0,
    activeUsers: 0,
    completedPayments: 0,
    pendingBookings: 0,
    cancelledBookings: 0,
  });
  const [loading, setLoading] = useState(true);

  // Dummy data for charts (in production, this would come from backend)
  const bookingsByMonth = [
    { month: 'Jan', bookings: 45, revenue: 12500 },
    { month: 'Feb', bookings: 52, revenue: 15800 },
    { month: 'Mar', bookings: 61, revenue: 18200 },
    { month: 'Apr', bookings: 48, revenue: 14600 },
    { month: 'May', bookings: 73, revenue: 22100 },
    { month: 'Jun', bookings: 89, revenue: 28900 },
    { month: 'Jul', bookings: 102, revenue: 35400 },
    { month: 'Aug', bookings: 95, revenue: 31200 },
    { month: 'Sep', bookings: 78, revenue: 24800 },
    { month: 'Oct', bookings: 84, revenue: 27300 },
    { month: 'Nov', bookings: 91, revenue: 29800 },
    { month: 'Dec', bookings: 107, revenue: 38600 },
  ];

  const destinationPopularity = [
    { destination: 'Paris', count: 145, percentage: 22 },
    { destination: 'Tokyo', count: 132, percentage: 20 },
    { destination: 'New York', count: 118, percentage: 18 },
    { destination: 'London', count: 97, percentage: 15 },
    { destination: 'Dubai', count: 84, percentage: 13 },
    { destination: 'Other', count: 79, percentage: 12 },
  ];

  const paymentStatusBreakdown = [
    { status: 'Completed', count: 542, percentage: 68 },
    { status: 'Pending', count: 198, percentage: 25 },
    { status: 'Failed', count: 55, percentage: 7 },
  ];

  const recentBookings = [
    { id: 'BK001', user: 'John Doe', destination: 'Paris', amount: 1250, status: 'confirmed', date: '2024-10-12' },
    { id: 'BK002', user: 'Jane Smith', destination: 'Tokyo', amount: 1580, status: 'pending', date: '2024-10-12' },
    { id: 'BK003', user: 'Bob Johnson', destination: 'London', amount: 980, status: 'confirmed', date: '2024-10-11' },
    { id: 'BK004', user: 'Alice Brown', destination: 'Dubai', amount: 2100, status: 'confirmed', date: '2024-10-11' },
    { id: 'BK005', user: 'Charlie Davis', destination: 'New York', amount: 1750, status: 'cancelled', date: '2024-10-10' },
  ];

  useEffect(() => {
    fetchDashboardStats();
  }, []);

  const fetchDashboardStats = async () => {
    try {
      setLoading(true);
      // In production, fetch real stats from backend
      // const data = await bookingService.getBookingStats();
      
      // Using dummy data for now
      setStats({
        totalBookings: 825,
        totalRevenue: 299200,
        activeUsers: 1247,
        completedPayments: 542,
        pendingBookings: 198,
        cancelledBookings: 85,
      });
    } catch (error) {
      console.error('Error fetching stats:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadgeClass = (status: string) => {
    return {
      confirmed: 'status-badge status-confirmed',
      pending: 'status-badge status-pending',
      cancelled: 'status-badge status-cancelled',
    }[status] || 'status-badge';
  };

  if (loading) {
    return <div className="loading">Loading dashboard...</div>;
  }

  return (
    <div className="admin-dashboard">
      <div className="dashboard-header">
        <h1>Admin Dashboard</h1>
        <p>Overview of bookings, revenue, and system analytics</p>
      </div>

      {/* KPI Cards */}
      <div className="kpi-grid">
        <div className="kpi-card kpi-primary">
          <div className="kpi-icon">📊</div>
          <div className="kpi-content">
            <h3>Total Bookings</h3>
            <p className="kpi-value">{stats.totalBookings.toLocaleString()}</p>
            <span className="kpi-trend positive">+12.5% from last month</span>
          </div>
        </div>

        <div className="kpi-card kpi-success">
          <div className="kpi-icon">💰</div>
          <div className="kpi-content">
            <h3>Total Revenue</h3>
            <p className="kpi-value">${stats.totalRevenue.toLocaleString()}</p>
            <span className="kpi-trend positive">+18.3% from last month</span>
          </div>
        </div>

        <div className="kpi-card kpi-info">
          <div className="kpi-icon">👥</div>
          <div className="kpi-content">
            <h3>Active Users</h3>
            <p className="kpi-value">{stats.activeUsers.toLocaleString()}</p>
            <span className="kpi-trend positive">+7.8% from last month</span>
          </div>
        </div>

        <div className="kpi-card kpi-warning">
          <div className="kpi-icon">⏳</div>
          <div className="kpi-content">
            <h3>Pending Bookings</h3>
            <p className="kpi-value">{stats.pendingBookings.toLocaleString()}</p>
            <span className="kpi-trend neutral">Requires attention</span>
          </div>
        </div>
      </div>

      {/* Charts Section */}
      <div className="charts-grid">
        {/* Bookings Over Time Chart */}
        <div className="chart-card">
          <h3>Monthly Bookings & Revenue Trend</h3>
          <div className="bar-chart">
            {bookingsByMonth.map((item, index) => {
              const maxBookings = Math.max(...bookingsByMonth.map(b => b.bookings));
              const heightPercentage = (item.bookings / maxBookings) * 100;
              return (
                <div key={index} className="bar-group">
                  <div 
                    className="bar" 
                    style={{ height: `${heightPercentage}%` }}
                    title={`${item.bookings} bookings, $${item.revenue.toLocaleString()}`}
                  >
                    <span className="bar-value">{item.bookings}</span>
                  </div>
                  <span className="bar-label">{item.month}</span>
                </div>
              );
            })}
          </div>
        </div>

        {/* Destination Popularity Chart */}
        <div className="chart-card">
          <h3>Popular Destinations</h3>
          <div className="horizontal-bar-chart">
            {destinationPopularity.map((item, index) => (
              <div key={index} className="h-bar-group">
                <div className="h-bar-label">{item.destination}</div>
                <div className="h-bar-container">
                  <div 
                    className="h-bar" 
                    style={{ width: `${item.percentage}%` }}
                  >
                    <span className="h-bar-value">{item.count}</span>
                  </div>
                </div>
                <span className="h-bar-percentage">{item.percentage}%</span>
              </div>
            ))}
          </div>
        </div>

        {/* Payment Status Breakdown */}
        <div className="chart-card">
          <h3>Payment Status Distribution</h3>
          <div className="donut-chart-container">
            <div className="donut-chart">
              {paymentStatusBreakdown.map((item, index) => {
                const statusColors = {
                  'Completed': '#10b981',
                  'Pending': '#f59e0b',
                  'Failed': '#ef4444',
                };
                return (
                  <div key={index} className="donut-segment" style={{
                    backgroundColor: statusColors[item.status as keyof typeof statusColors] || '#6b7280'
                  }}>
                    <div className="segment-label">
                      {item.status}: {item.count} ({item.percentage}%)
                    </div>
                  </div>
                );
              })}
            </div>
            <div className="donut-legend">
              {paymentStatusBreakdown.map((item, index) => (
                <div key={index} className="legend-item">
                  <span className={`legend-color ${item.status.toLowerCase()}`}></span>
                  <span>{item.status}: {item.count} ({item.percentage}%)</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Revenue Breakdown */}
        <div className="chart-card">
          <h3>Revenue Analysis</h3>
          <div className="revenue-stats">
            <div className="revenue-item">
              <span className="revenue-label">Average Booking Value</span>
              <span className="revenue-value">${(stats.totalRevenue / stats.totalBookings).toFixed(2)}</span>
            </div>
            <div className="revenue-item">
              <span className="revenue-label">Completed Payments</span>
              <span className="revenue-value">${(stats.totalRevenue * 0.68).toLocaleString()}</span>
            </div>
            <div className="revenue-item">
              <span className="revenue-label">Pending Payments</span>
              <span className="revenue-value">${(stats.totalRevenue * 0.25).toLocaleString()}</span>
            </div>
            <div className="revenue-item">
              <span className="revenue-label">Failed/Refunded</span>
              <span className="revenue-value">${(stats.totalRevenue * 0.07).toLocaleString()}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Bookings Table */}
      <div className="recent-bookings">
        <h3>Recent Bookings</h3>
        <table className="bookings-table">
          <thead>
            <tr>
              <th>Booking ID</th>
              <th>User</th>
              <th>Destination</th>
              <th>Amount</th>
              <th>Status</th>
              <th>Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {recentBookings.map((booking) => (
              <tr key={booking.id}>
                <td>{booking.id}</td>
                <td>{booking.user}</td>
                <td>{booking.destination}</td>
                <td>${booking.amount.toLocaleString()}</td>
                <td>
                  <span className={getStatusBadgeClass(booking.status)}>
                    {booking.status}
                  </span>
                </td>
                <td>{new Date(booking.date).toLocaleDateString()}</td>
                <td>
                  <button className="btn btn-sm btn-secondary">View</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Additional Stats */}
      <div className="additional-stats">
        <div className="stat-card">
          <h4>Cancellation Rate</h4>
          <p className="stat-large">{((stats.cancelledBookings / stats.totalBookings) * 100).toFixed(1)}%</p>
          <span className="stat-info">Within acceptable range</span>
        </div>
        <div className="stat-card">
          <h4>Conversion Rate</h4>
          <p className="stat-large">73.2%</p>
          <span className="stat-info positive">+5.4% from last month</span>
        </div>
        <div className="stat-card">
          <h4>Avg. Processing Time</h4>
          <p className="stat-large">2.4h</p>
          <span className="stat-info positive">-0.8h improvement</span>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboardPage;
