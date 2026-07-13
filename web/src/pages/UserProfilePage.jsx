import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import BackButton from '../components/BackButton'
import { supabase } from '../api/supabaseClient'
import { updateUser } from '../api/userApi'
import './UserProfilePage.css'

export default function UserProfilePage() {
  const navigate = useNavigate()
  const { session, profile, logout } = useAuth()

  const [fullName, setFullName] = useState('')
  const [phoneNumber, setPhoneNumber] = useState('')
  const [profileError, setProfileError] = useState('')
  const [profileSuccess, setProfileSuccess] = useState('')
  const [savingProfile, setSavingProfile] = useState(false)

  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [passwordError, setPasswordError] = useState('')
  const [passwordSuccess, setPasswordSuccess] = useState('')
  const [savingPassword, setSavingPassword] = useState(false)

  useEffect(() => {
    if (profile) {
      setFullName(profile.fullName || '')
      setPhoneNumber(profile.phoneNumber || '')
    }
  }, [profile])

  async function handleProfileSubmit(e) {
    e.preventDefault()
    setProfileError('')
    setProfileSuccess('')
    setSavingProfile(true)

    try {
      await updateUser(session.user.id, { fullName, phoneNumber })
      setProfileSuccess('Profile updated successfully.')
    } catch (err) {
      setProfileError(err.message || 'Failed to update profile.')
    } finally {
      setSavingProfile(false)
    }
  }

  async function handlePasswordSubmit(e) {
    e.preventDefault()
    setPasswordError('')
    setPasswordSuccess('')

    if (newPassword.length < 6) {
      setPasswordError('Password must be at least 6 characters.')
      return
    }

    if (newPassword !== confirmPassword) {
      setPasswordError('Passwords do not match.')
      return
    }

    setSavingPassword(true)

    try {
      const { error } = await supabase.auth.updateUser({ password: newPassword })
      if (error) throw new Error(error.message)

      setPasswordSuccess('Password updated successfully.')
      setNewPassword('')
      setConfirmPassword('')
    } catch (err) {
      setPasswordError(err.message || 'Failed to update password.')
    } finally {
      setSavingPassword(false)
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
    <div className="profile-page">
      <header className="profile-topbar">
        <span className="wordmark">Lakbay Ayos</span>
        <button className="logout-btn" onClick={handleLogout}>
          Log out
        </button>
      </header>

      <main className="profile-main">
        <BackButton />
        <h1>Edit Profile</h1>
        <p className="profile-eyebrow">Account</p>

        <section className="profile-section">
          <h2>Personal Details</h2>
          <form onSubmit={handleProfileSubmit}>
            {profileError && <p className="global-error">{profileError}</p>}
            {profileSuccess && <p className="global-success">{profileSuccess}</p>}

            <div className="field">
              <label htmlFor="fullName">Full name</label>
              <input
                type="text"
                id="fullName"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
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
              />
            </div>

            <div className="field">
              <label>Email</label>
              <input type="email" value={session?.user?.email || ''} disabled />
            </div>

            <button type="submit" className="btn" disabled={savingProfile}>
              {savingProfile ? 'Saving...' : 'Save Changes'}
            </button>
          </form>
        </section>

        <section className="profile-section">
          <h2>Reset Password</h2>
          <form onSubmit={handlePasswordSubmit}>
            {passwordError && <p className="global-error">{passwordError}</p>}
            {passwordSuccess && <p className="global-success">{passwordSuccess}</p>}

            <div className="field">
              <label htmlFor="newPassword">New password</label>
              <input
                type="password"
                id="newPassword"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="At least 6 characters"
                minLength={6}
                required
              />
            </div>

            <div className="field">
              <label htmlFor="confirmPassword">Confirm new password</label>
              <input
                type="password"
                id="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>

            <button type="submit" className="btn" disabled={savingPassword}>
              {savingPassword ? 'Updating...' : 'Update Password'}
            </button>
          </form>
        </section>
      </main>
    </div>
  )
}