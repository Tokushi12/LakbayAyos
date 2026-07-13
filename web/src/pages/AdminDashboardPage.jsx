import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './DashboardPage.css'

export default function AdminDashboardPage() {
  const navigate = useNavigate()
  const { logout } = useAuth()

  async function handleLogout() {
    try {
      await logout()
    } finally {
      navigate('/login')
    }
  }

  return (
    <div className="dashboard-page">
      <header className="dashboard-topbar">
        <span className="wordmark">Lakbay Ayos</span>
        <button className="logout-btn" onClick={handleLogout}>
          Log out
        </button>
      </header>

      <main className="dashboard-main">
        <h1 className="dashboard-title">Dashboard</h1>
        <p className="dashboard-eyebrow">Admin</p>

        <div className="dashboard-grid">
          <Link to="/admin/inventory" className="dashboard-card">
            <h2>Motor Parts Inventory</h2>
            <p>Add new parts, adjust stock, and toggle availability for booking.</p>
          </Link>

          <Link to="/admin/bookings" className="dashboard-card">
            <h2>Manage Bookings</h2>
            <p>Review appointment requests, view customer details, and confirm or decline.</p>
          </Link>
        </div>
      </main>
    </div>
  )
}