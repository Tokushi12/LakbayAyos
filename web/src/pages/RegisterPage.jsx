import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import AuthLayout from '../components/AuthLayout'
import { registerUser } from '../api/authApi'

export default function RegisterPage() {
  const navigate = useNavigate()
  const [fullName, setFullName] = useState('')
  const [email, setEmail] = useState('')
  const [phoneNumber, setPhoneNumber] = useState('')
  const [password, setPassword] = useState('')
  const [role, setRole] = useState('customer')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      await registerUser({ email, password, fullName, phoneNumber, role })
      navigate('/login')
    } catch (err) {
      setError(err.message || 'Something went wrong. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <AuthLayout
      activePage="register"
      eyebrow="Step one of two"
      title="Create an account"
      description="Register with your details to get started."
      switchText="Already have an account?"
      switchLinkText="Log in"
      switchLinkTo="/login"
    >
      <form onSubmit={handleSubmit}>
        {error && <p className="global-error">{error}</p>}

        <div className="field">
          <label htmlFor="fullName">Full name</label>
          <input
            type="text"
            id="fullName"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            placeholder="Juan Dela Cruz"
            autoComplete="name"
            required
          />
        </div>

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
          <label htmlFor="phoneNumber">Phone number</label>
          <input
            type="tel"
            id="phoneNumber"
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
            placeholder="09XX XXX XXXX"
            autoComplete="tel"
          />
        </div>

        <div className="field">
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="At least 6 characters"
            autoComplete="new-password"
            minLength={6}
            required
          />
        </div>

        <div className="field">
          <label htmlFor="role">I am a</label>
          <select id="role" value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="customer">Motorcycle owner</option>
            <option value="admin">Shop administrator</option>
          </select>
        </div>

        <button type="submit" className="btn" disabled={loading}>
          {loading ? 'Creating account...' : 'Create account'}
        </button>
      </form>
    </AuthLayout>
  )
}