import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import BackButton from '../components/BackButton'
import { getBookingsByUser, cancelBookingByUser } from '../api/bookingsApi'
import './UserBookingsPage.css'

export default function UserBookingsPage() {
  const navigate = useNavigate()
  const { session, logout } = useAuth()

  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [cancellingId, setCancellingId] = useState(null)

  useEffect(() => {
    if (session?.user?.id) {
      loadBookings()
    }
  }, [session])

  async function loadBookings() {
    setLoading(true)
    setError('')
    try {
      const data = await getBookingsByUser(session.user.id)
      setBookings(data)
    } catch (err) {
      setError(err.message || 'Failed to load bookings.')
    } finally {
      setLoading(false)
    }
  }

  async function handleCancel(bookingId) {
    setCancellingId(bookingId)
    setError('')
    try {
      await cancelBookingByUser(bookingId, session.user.id)
      await loadBookings()
    } catch (err) {
      setError(err.message || 'Failed to cancel booking.')
    } finally {
      setCancellingId(null)
    }
  }

  async function handleLogout() {
    try {
      await logout()
    } finally {
      navigate('/login')
    }
  }

  return (
    <div className="bookings-page">
      <header className="bookings-topbar">
        <span className="wordmark">Lakbay Ayos</span>
        <button className="logout-btn" onClick={handleLogout}>
          Log out
        </button>
      </header>

      <main className="bookings-main">
        <BackButton />
        <h1>Your Appointments</h1>
        <p className="bookings-eyebrow">My Bookings</p>

        {error && <p className="bookings-error">{error}</p>}

        {loading ? (
          <p className="bookings-loading">Loading bookings...</p>
        ) : bookings.length === 0 ? (
          <p className="bookings-empty">You have no bookings yet.</p>
        ) : (
          <div className="bookings-list">
            {bookings.map((booking) => (
              <div key={booking.id} className="booking-card">
                <div className="booking-card-header">
                  <div>
                    <p className="booking-date">{booking.bookingDate}</p>
                    <span className={`status-badge status-${booking.status}`}>
                      {booking.status}
                    </span>
                  </div>
                  {booking.status === 'pending' && (
                    <button
                      className="cancel-btn"
                      onClick={() => handleCancel(booking.id)}
                      disabled={cancellingId === booking.id}
                    >
                      {cancellingId === booking.id ? 'Cancelling...' : 'Cancel'}
                    </button>
                  )}
                </div>

                <ul className="booking-items">
                  {booking.items.map((item) => (
                    <li key={item.id}>
                      {item.partName} ({item.partCategory}) &times; {item.quantity}
                    </li>
                  ))}
                </ul>

                {booking.adminNotes && (
                  <p className="booking-notes">
                    <strong>Note:</strong> {booking.adminNotes}
                  </p>
                )}
              </div>
            ))}
          </div>
        )}
      </main>
    </div>
  )
}