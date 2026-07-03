import { useNavigate } from 'react-router-dom'
import { logoutUser } from '../api/authApi'
import './DashboardPage.css'

export default function DashboardPage() {
  const navigate = useNavigate()

  async function handleLogout() {
    try {
      await logoutUser()
    } finally {
      navigate('/login')
    }
  }

  return (
    <div className="dashboard-page">
      <header className="dashboard-topbar">
        <span className="wordmark">Lakbay Ayos</span>
        <button className="logout-btn" onClick={handleLogout}>Log out</button>
      </header>

      <main className="dashboard-main">
        {/* Dashboard content goes here */}
      </main>
    </div>
  )
}