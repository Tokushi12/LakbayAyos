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
