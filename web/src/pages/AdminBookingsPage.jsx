import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import BackButton from '../components/BackButton'
import { getAllBookings, updateBookingStatus } from '../api/bookingsApi'
import './AdminBookingsPage.css'

const STATUS_FILTERS = ['all', 'pending', 'approved', 'declined', 'cancelled']

export default function AdminBookingsPage() {
  const navigate = useNavigate()
  const { logout } = useAuth()
  const [bookings, setBookings] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [statusFilter, setStatusFilter] = useState('all')

  const [selectedBooking, setSelectedBooking] = useState(null)
  const [adminNotes, setAdminNotes] = useState('')
  const [actionError, setActionError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    loadBookings()
  }, [])

  async function loadBookings() {
    setLoading(true)
    setError('')
    try {
      const data = await getAllBookings()
      setBookings(data)
    } catch (err) {
      setError(err.message || 'Failed to load bookings.')
    } finally {
      setLoading(false)
    }
  }

  function openDetails(booking) {
    setSelectedBooking(booking)
    setAdminNotes(booking.adminNotes || '')
    setActionError('')
  }

  function closeDetails() {
    setSelectedBooking(null)
    setActionError('')
  }

  async function handleStatusChange(newStatus) {
    if (!selectedBooking) return
    setActionError('')
    setSubmitting(true)

    try {
      await updateBookingStatus(selectedBooking.id, { status: newStatus, adminNotes })
      await loadBookings()
      closeDetails()
    } catch (err) {
      setActionError(err.message || 'Failed to update booking.')
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

  const filteredBookings =
    statusFilter === 'all' ? bookings : bookings.filter((b) => b.status === statusFilter)

  return (
    <div className="admin-page">
      <header className="admin-topbar">
        <span className="wordmark">Lakbay Ayos</span>
        <button className="logout-btn" onClick={handleLogout}>
          Log out
        </button>
      </header>

      <main className="admin-main">
        <BackButton />
        <div className="page-header">
          <h1>Manage Appointments</h1>
          <p className="admin-eyebrow">Bookings</p>
        </div>

        <div className="filter-tabs">
          {STATUS_FILTERS.map((status) => (
            <button
              key={status}
              className={`filter-tab ${statusFilter === status ? 'is-active' : ''}`}
              onClick={() => setStatusFilter(status)}
            >
              {status.charAt(0).toUpperCase() + status.slice(1)}
            </button>
          ))}
        </div>

        {error && <p className="admin-error">{error}</p>}

        {loading ? (
          <p className="admin-loading">Loading bookings...</p>
        ) : filteredBookings.length === 0 ? (
          <p className="admin-empty">No bookings found for this filter.</p>
        ) : (
          <table className="parts-table">
            <thead>
              <tr>
                <th>Customer</th>
                <th>Date</th>
                <th>Items</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredBookings.map((booking) => (
                <tr key={booking.id}>
                  <td>{booking.userFullName}</td>
                  <td>{booking.bookingDate}</td>
                  <td>{booking.items.length} item(s)</td>
                  <td>
                    <span className={`status-badge status-${booking.status}`}>
                      {booking.status}
                    </span>
                  </td>
                  <td>
                    <button className="link-btn" onClick={() => openDetails(booking)}>
                      View Details
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </main>

      {selectedBooking && (
        <div className="modal-overlay" onClick={closeDetails}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <h2>Booking Details</h2>

            {actionError && <p className="global-error">{actionError}</p>}

            <div className="detail-row">
              <span className="detail-label">Customer</span>
              <span>{selectedBooking.userFullName}</span>
            </div>

            <div className="detail-row">
              <span className="detail-label">Date</span>
              <span>{selectedBooking.bookingDate}</span>
            </div>

            <div className="detail-row">
              <span className="detail-label">Status</span>
              <span className={`status-badge status-${selectedBooking.status}`}>
                {selectedBooking.status}
              </span>
            </div>

            <div className="detail-row detail-row-stacked">
              <span className="detail-label">Parts Requested</span>
              <ul className="item-list">
                {selectedBooking.items.map((item) => (
                  <li key={item.id}>
                    {item.partName} ({item.partCategory}) &times; {item.quantity}
                  </li>
                ))}
              </ul>
            </div>

            <div className="field">
              <label htmlFor="adminNotes">Admin Notes</label>
              <textarea
                id="adminNotes"
                value={adminNotes}
                onChange={(e) => setAdminNotes(e.target.value)}
                placeholder="Optional note, e.g. reason for declining"
                rows={3}
              />
            </div>

            <div className="modal-actions">
              <button type="button" className="secondary-btn" onClick={closeDetails}>
                Close
              </button>

              {selectedBooking.status === 'pending' && (
                <>
                  <button
                    type="button"
                    className="decline-btn"
                    disabled={submitting}
                    onClick={() => handleStatusChange('declined')}
                  >
                    Decline
                  </button>
                  <button
                    type="button"
                    className="btn"
                    disabled={submitting}
                    onClick={() => handleStatusChange('approved')}
                  >
                    Approve
                  </button>
                </>
              )}

              {selectedBooking.status === 'approved' && (
                <button
                  type="button"
                  className="decline-btn"
                  disabled={submitting}
                  onClick={() => handleStatusChange('cancelled')}
                >
                  Cancel Booking
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}