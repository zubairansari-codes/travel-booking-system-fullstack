import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './HomePage.css';

interface TourListing {
  id: number;
  name: string;
  location: string;
  price: number;
  image: string;
  rating: number;
  type: 'tour' | 'hotel';
  description: string;
}

const HomePage: React.FC = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [filterType, setFilterType] = useState<'all' | 'tour' | 'hotel'>('all');

  const sampleListings: TourListing[] = [
    {
      id: 1,
      name: 'Paris City Tour',
      location: 'Paris, France',
      price: 299,
      image: '/images/paris.jpg',
      rating: 4.8,
      type: 'tour',
      description: 'Explore the romantic city of Paris with guided tours to Eiffel Tower, Louvre, and more.'
    },
    {
      id: 2,
      name: 'Grand Hotel Plaza',
      location: 'New York, USA',
      price: 450,
      image: '/images/newyork.jpg',
      rating: 4.5,
      type: 'hotel',
      description: 'Luxury hotel in the heart of Manhattan with stunning city views and world-class amenities.'
    },
    {
      id: 3,
      name: 'Bali Beach Resort',
      location: 'Bali, Indonesia',
      price: 350,
      image: '/images/bali.jpg',
      rating: 4.9,
      type: 'hotel',
      description: 'Beachfront resort with private villas, spa services, and breathtaking ocean views.'
    },
    {
      id: 4,
      name: 'Tokyo Cultural Experience',
      location: 'Tokyo, Japan',
      price: 399,
      image: '/images/tokyo.jpg',
      rating: 4.7,
      type: 'tour',
      description: 'Immerse yourself in Japanese culture with temple visits, traditional tea ceremonies, and more.'
    },
    {
      id: 5,
      name: 'Swiss Alpine Lodge',
      location: 'Interlaken, Switzerland',
      price: 520,
      image: '/images/swiss.jpg',
      rating: 4.9,
      type: 'hotel',
      description: 'Cozy mountain lodge with panoramic views of the Swiss Alps and skiing access.'
    },
    {
      id: 6,
      name: 'Amazon Rainforest Adventure',
      location: 'Manaus, Brazil',
      price: 599,
      image: '/images/amazon.jpg',
      rating: 4.6,
      type: 'tour',
      description: 'Experience the wonder of the Amazon with guided jungle treks and wildlife spotting.'
    }
  ];

  const filteredListings = sampleListings.filter(listing => {
    const matchesSearch = listing.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         listing.location.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesFilter = filterType === 'all' || listing.type === filterType;
    return matchesSearch && matchesFilter;
  });

  return (
    <div className="home-page">
      <header className="hero-section">
        <div className="hero-content">
          <h1>Discover Your Next Adventure</h1>
          <p>Explore amazing tours and hotels around the world</p>
        </div>
      </header>

      <div className="search-section">
        <div className="search-container">
          <input
            type="text"
            className="search-bar"
            placeholder="Search destinations, tours, or hotels..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button className="search-button">🔍 Search</button>
        </div>
        
        <div className="filter-buttons">
          <button 
            className={`filter-btn ${filterType === 'all' ? 'active' : ''}`}
            onClick={() => setFilterType('all')}
          >
            All
          </button>
          <button 
            className={`filter-btn ${filterType === 'tour' ? 'active' : ''}`}
            onClick={() => setFilterType('tour')}
          >
            Tours
          </button>
          <button 
            className={`filter-btn ${filterType === 'hotel' ? 'active' : ''}`}
            onClick={() => setFilterType('hotel')}
          >
            Hotels
          </button>
        </div>
      </div>

      <div className="listings-container">
        <h2>Featured Listings</h2>
        <div className="listings-grid">
          {filteredListings.map(listing => (
            <div key={listing.id} className="listing-card">
              <div className="card-image">
                <img src={listing.image} alt={listing.name} />
                <span className="card-badge">{listing.type}</span>
              </div>
              <div className="card-content">
                <h3>{listing.name}</h3>
                <p className="location">📍 {listing.location}</p>
                <p className="description">{listing.description}</p>
                <div className="card-footer">
                  <div className="rating">
                    ⭐ {listing.rating}
                  </div>
                  <div className="price">
                    <span className="price-label">From</span>
                    <span className="price-amount">${listing.price}</span>
                  </div>
                </div>
                <Link to={`/booking/${listing.id}`} className="book-button">
                  Book Now
                </Link>
              </div>
            </div>
          ))}
        </div>
        {filteredListings.length === 0 && (
          <div className="no-results">
            <p>No listings found. Try adjusting your search or filters.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default HomePage;
