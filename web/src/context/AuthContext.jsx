import { createContext, useContext, useState, useEffect } from 'react'
import { supabase } from '../api/supabaseClient'
import { getUserById } from '../api/userApi'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [session, setSession] = useState(null)
  const [profile, setProfile] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let isMounted = true

    async function loadSession() {
      const { data } = await supabase.auth.getSession()
      if (!isMounted) return

      setSession(data.session)

      if (data.session?.user?.id) {
        try {
          const userProfile = await getUserById(data.session.user.id)
          if (isMounted) setProfile(userProfile)
        } catch {
          if (isMounted) setProfile(null)
        }
      }

      if (isMounted) setLoading(false)
    }

    loadSession()

    const { data: listener } = supabase.auth.onAuthStateChange(async (_event, newSession) => {
      setSession(newSession)

      if (newSession?.user?.id) {
        try {
          const userProfile = await getUserById(newSession.user.id)
          setProfile(userProfile)
        } catch {
          setProfile(null)
        }
      } else {
        setProfile(null)
      }
    })

    return () => {
      isMounted = false
      listener.subscription.unsubscribe()
    }
  }, [])

  // Logs in, then fetches the profile and updates context state
  // BEFORE returning, so ProtectedRoute sees the correct session
  // immediately on the very first navigation attempt.
  async function login({ email, password }) {
    const { data, error } = await supabase.auth.signInWithPassword({ email, password })

    if (error) {
      throw new Error(error.message)
    }

    setSession(data.session)

    const userProfile = await getUserById(data.user.id)
    setProfile(userProfile)

    return userProfile
  }

  async function logout() {
    const { error } = await supabase.auth.signOut()
    if (error) {
      throw new Error(error.message)
    }
    setSession(null)
    setProfile(null)
  }

  const value = {
    session,
    profile,
    role: profile?.role || null,
    loading,
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}