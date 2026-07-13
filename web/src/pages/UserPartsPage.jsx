import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import BackButton from '../components/BackButton'
import { getAvailableParts, searchParts } from '../api/partsApi'
import { createBooking } from '../api/bookingsApi'
import './UserPartsPage.css'

export default function UserPartsPage() {
  const navigate = useNavigate()
  const { session, logout } = useAuth()

  const [query, setQuery] = useState('')
  const [parts, setParts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [cart, setCart] = useState([])
  const [showDatePicker, setShowDatePicker] = useState(false)
  const [bookingDate, setBookingDate] = useState('')
  const [bookingError, setBookingError] = useState('')
  const [bookingSuccess, setBookingSuccess] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    loadParts()
  }, [])

  async function loadParts() {
    setLoading(true)
    setError('')
    try {
      const data = await getAvailableParts()
      setParts(data)
    } catch (err) {
      setError(err.message || 'Failed to load parts.')
    } finally {
      setLoading(false)
    }
  }

  async function handleSearch(e) {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const data = query.trim() ? await searchParts(query.trim()) : await getAvailableParts()
      setParts(data.filter((p) => p.isAvailable))
    } catch (err) {
      setError(err.message || 'Search failed.')
    } finally {
      setLoading(false)
    }
  }

  function addToCart(part) {
    setCart((prev) => {
      const existing = prev.find((item) => item.partId === part.id)
      if (existing) {
        return prev.map((item) =>
          item.partId === part.id ? { ...item, quantity: item.quantity + 1 } : item
        )
      }
      return [...prev, { partId: part.id, name: part.name, category: part.category, quantity: 1 }]
    })
  }

  function updateQuantity(partId, quantity) {
    if (quantity < 1) return
    setCart((prev) =>
      prev.map((item) => (item.partId === partId ? { ...item, quantity } : item))
    )
  }

  function removeFromCart(partId) {
    setCart((prev) => prev.filter((item) => item.partId !== partId))
  }

  function openDatePicker() {
    setBookingError('')
    setBookingSuccess('')
    setShowDatePicker(true)
  }

  function cancelDatePicker() {
    setShowDatePicker(false)
    setBookingDate('')
    setBookingError('')
  }

  async function handleConfirmBooking() {
    if (!bookingDate) {
      setBookingError('Please select a date.')
      return
    }

    setBookingError('')
    setSubmitting(true)

    try {
      await createBooking({
        userId: session.user.id,
        bookingDate,
        items: cart.map((item) => ({ partId: item.partId, quantity: item.quantity })),
      })
      setCart([])
      setShowDatePicker(false)
      setBookingDate('')
      setBookingSuccess('Booking request submitted. You can track its status in My Bookings.')
    } catch (err) {
      setBookingError(err.message || 'Failed to submit booking.')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleLogout() {
    try {
      await logout()
    } finally {
      navigate('/login')
    }
  }

  const today = new Date().toISOString().split('T')[0]

  return (
    <div className="parts-page">
      <header className="parts-topbar">
        <span className="wordmark">Lakbay Ayos</span>
        <button className="logout-btn" onClick={handleLogout}>
          Log out
        </button>
      </header>

      <main className="parts-main">
        <div className="parts-content">
          <BackButton />
          <h1>Search Parts</h1>
          <p className="parts-eyebrow">Motor Parts</p>

          <form className="search-bar" onSubmit={handleSearch}>
            <input
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Search by name or category"
            />
            <button type="submit" className="btn">
              Search
            </button>
          </form>

          {error && <p className="parts-error">{error}</p>}
          {bookingSuccess && <p className="parts-success">{bookingSuccess}</p>}

          {loading ? (
            <p className="parts-loading">Loading parts...</p>
          ) : parts.length === 0 ? (
            <p className="parts-empty">No parts found.</p>
          ) : (
            <div className="parts-grid">
              {parts.map((part) => (
                <div key={part.id} className="part-card">
                  {part.imageUrl ? (
                    <img src={part.imageUrl} alt={part.name} className="part-card-image" />
                  ) : (
                    <div className="part-card-image part-card-image-empty" />
                  )}
                  <h3>{part.name}</h3>
                  <p className="part-category">{part.category}</p>
                  <p className="part-stock">{part.stockQuantity} in stock</p>
                  <button className="secondary-btn" onClick={() => addToCart(part)}>
                    Add to Booking
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>

        <aside className="cart-panel">
          <h2>Your Selection</h2>

          {cart.length === 0 ? (
            <p className="cart-empty">No parts selected yet.</p>
          ) : (
            <>
              <ul className="cart-list">
                {cart.map((item) => (
                  <li key={item.partId} className="cart-item">
                    <div className="cart-item-info">
                      <span className="cart-item-name">{item.name}</span>
                      <span className="cart-item-category">{item.category}</span>
                    </div>
                    <div className="cart-item-controls">
                      <button onClick={() => updateQuantity(item.partId, item.quantity - 1)}>
                        -
                      </button>
                      <span>{item.quantity}</span>
                      <button onClick={() => updateQuantity(item.partId, item.quantity + 1)}>
                        +
                      </button>
                      <button
                        className="remove-btn"
                        onClick={() => removeFromCart(item.partId)}
                        aria-label="Remove"
                      >
                        &times;
                      </button>
                    </div>
                  </li>
                ))}
              </ul>

              {!showDatePicker ? (
                <button className="btn cart-book-btn" onClick={openDatePicker}>
                  Choose Date to Book
                </button>
              ) : (
                <div className="date-picker">
                  {bookingError && <p className="global-error">{bookingError}</p>}

                  <label htmlFor="bookingDate">Select a date</label>
                  <input
                    type="date"
                    id="bookingDate"
                    min={today}
                    value={bookingDate}
                    onChange={(e) => setBookingDate(e.target.value)}
                  />

                  <div className="date-picker-actions">
                    <button className="secondary-btn" onClick={cancelDatePicker}>
                      Cancel
                    </button>
                    <button className="btn" onClick={handleConfirmBooking} disabled={submitting}>
                      {submitting ? 'Booking...' : 'Book'}
                    </button>
                  </div>
                </div>
              )}
            </>
          )}
        </aside>
      </main>
    </div>
  )
}