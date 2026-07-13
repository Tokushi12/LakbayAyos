import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './DashboardPage.css'

export default function UserDashboardPage() {
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
        <p className="dashboard-eyebrow">Welcome</p>

        <div className="dashboard-grid">
          <Link to="/parts" className="dashboard-card">
            <h2>Search Parts</h2>
            <p>Find motor parts by name or category and book a service date.</p>
          </Link>

          <Link to="/bookings" className="dashboard-card">
            <h2>My Bookings</h2>
            <p>View the status of your appointments and cancel pending ones.</p>
          </Link>

          <Link to="/profile" className="dashboard-card">
            <h2>Edit Profile</h2>
            <p>Update your name, phone number, or reset your password.</p>
          </Link>
        </div>
      </main>
    </div>
  )
}