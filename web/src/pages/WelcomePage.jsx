import { Link } from 'react-router-dom'
import './WelcomePage.css'

export default function WelcomePage() {
  return (
    <div className="welcome-page">
      <header className="topbar">
        <span className="wordmark">Lakbay Ayos</span>
        <nav>
          <Link to="/login">Log in</Link>
          <Link to="/register" className="nav-cta">Register</Link>
        </nav>
      </header>

      <main className="welcome-main">
        <p className="welcome-eyebrow">Motorcycle Maintenance, Simplified</p>
        <h1>Welcome to Lakbay Ayos</h1>
        <p className="welcome-description">
          Looking for new parts? Find it here. Search inventory by name or category,
          book a service date in seconds, and track every appointment from request to completion.
        </p>

        <div className="welcome-actions">
          <Link to="/register" className="btn">Get Started</Link>
          <Link to="/login" className="btn-outline">I already have an account</Link>
        </div>
      </main>

      <footer>&copy; SimpleProject for System Integration</footer>
    </div>
  )
}
