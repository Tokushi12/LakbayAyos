import { supabase } from './supabaseClient'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

export async function registerUser({ email, password, fullName, phoneNumber, role }) {
  // Step 1: create the auth user in Supabase
  const { data, error } = await supabase.auth.signUp({ email, password })

  if (error) {
    throw new Error(error.message)
  }

  const userId = data.user?.id
  if (!userId) {
    throw new Error('Registration did not return a user id. Check Supabase email confirmation settings.')
  }

  // Step 2: save the profile fields to our own backend
  const response = await fetch(`${API_BASE_URL}/api/users/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      id: userId,
      email,
      fullName,
      phoneNumber,
      role,
    }),
  })

  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Failed to save user profile.')
  }

  return response.json()
}

export async function loginUser({ email, password }) {
  const { data, error } = await supabase.auth.signInWithPassword({ email, password })

  if (error) {
    throw new Error(error.message)
  }

  return data
}

export async function logoutUser() {
  const { error } = await supabase.auth.signOut()
  if (error) {
    throw new Error(error.message)
  }
}