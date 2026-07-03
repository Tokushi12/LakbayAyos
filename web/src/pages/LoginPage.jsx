import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import { loginUser } from '../api/authApi'

export default function LoginPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      await loginUser({ email, password })
      navigate('/dashboard')
    } catch (err) {
      setError(err.message || 'Incorrect email or password. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout
      activePage="login"
      eyebrow="Step two of two"
      title="Welcome back"
      description="Log in with the email and password you registered with."
      switchText="New here?"
      switchLinkText="Create an account"
      switchLinkTo="/register"
    >
      <form onSubmit={handleSubmit}>
        {error && <p className="global-error">{error}</p>}

        <div className="field">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="juan.delacruz@email.com"
            autoComplete="email"
            required
          />
        </div>

        <div className="field">
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Your password"
            autoComplete="current-password"
            required
          />
        </div>

        <button type="submit" className="btn" disabled={loading}>
          {loading ? 'Logging in...' : 'Log in'}
        </button>
      </form>
    </AuthLayout>
  )
}