import { Link } from 'react-router-dom'
import './AuthLayout.css'

export default function AuthLayout({ activePage, eyebrow, title, description, switchText, switchLinkText, switchLinkTo, children }) {
  return (
    <div className="auth-page">
      <header className="topbar">
        <Link className="wordmark" to="/">Lakbay Ayos</Link>
        <nav>
          <Link to="/login" className={activePage === 'login' ? 'is-active nav-cta' : ''}>Log in</Link>
          <Link to="/register" className={activePage === 'register' ? 'is-active nav-cta' : ''}>Register</Link>
        </nav>
      </header>

      <main>
        <div className="auth-grid">
          <div className="auth-intro">
            <p className="eyebrow">{eyebrow}</p>
            <h1>{title}</h1>
            <p>{description}</p>
            <p className="switch">
              {switchText} <Link to={switchLinkTo}>{switchLinkText} &rarr;</Link>
            </p>
          </div>

          <div className="auth-card">
            {children}
          </div>
        </div>
      </main>

      <footer>&copy; SimpleProject for System Integration</footer>
    </div>
  )
}