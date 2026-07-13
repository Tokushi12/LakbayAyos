const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

async function handleResponse(response) {
  if (!response.ok) {
    const errorBody = await response.json().catch(() => ({}))
    throw new Error(errorBody.message || 'Something went wrong.')
  }
  return response.json()
}

export async function createBooking({ userId, bookingDate, items }) {
  const response = await fetch(`${API_BASE_URL}/api/bookings`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ userId, bookingDate, items }),
  })
  return handleResponse(response)
}

export async function getAllBookings() {
  const response = await fetch(`${API_BASE_URL}/api/bookings`)
  return handleResponse(response)
}

export async function getBookingsByStatus(status) {
  const response = await fetch(`${API_BASE_URL}/api/bookings/status/${status}`)
  return handleResponse(response)
}

export async function getBookingsByUser(userId) {
  const response = await fetch(`${API_BASE_URL}/api/bookings/user/${userId}`)
  return handleResponse(response)
}

export async function getBookingById(id) {
  const response = await fetch(`${API_BASE_URL}/api/bookings/${id}`)
  return handleResponse(response)
}

export async function updateBookingStatus(id, { status, adminNotes }) {
  const response = await fetch(`${API_BASE_URL}/api/bookings/${id}/status`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status, adminNotes }),
  })
  return handleResponse(response)
}

export async function cancelBookingByUser(id, userId) {
  const response = await fetch(
    `${API_BASE_URL}/api/bookings/${id}/cancel?userId=${encodeURIComponent(userId)}`,
    { method: 'PATCH' }
  )
  return handleResponse(response)
}
