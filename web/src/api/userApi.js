const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

async function handleResponse(response) {
  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Something went wrong.')
  }
  return response.json()
}

export async function getUserById(id) {
  const response = await fetch(`${API_BASE_URL}/api/users/${id}`)
  return handleResponse(response)
}

export async function updateUser(id, { fullName, phoneNumber }) {
  const response = await fetch(`${API_BASE_URL}/api/users/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ fullName, phoneNumber }),
  })
  return handleResponse(response)
}