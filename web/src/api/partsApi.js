const API_BASE_URL = import.meta.env.VITE_API_BASE_URL

async function handleResponse(response) {
    if (!response.ok) {
        const errorBody = await response.json().catch(() => ({}))
        throw new Error(errorBody.message || 'Something went wrong.')
    }
    return response.json()
}

export async function getAllParts() {
    const response = await fetch(`${API_BASE_URL}/api/parts`)
    return handleResponse(response)
}

export async function getAvailableParts() {
    const response = await fetch(`${API_BASE_URL}/api/parts/available`)
    return handleResponse(response)
}

export async function searchParts(query) {
    const response = await fetch(`${API_BASE_URL}/api/parts/search?query=${encodeURIComponent(query)}`)
    return handleResponse(response)
}

export async function createPart({ name, category, stockQuantity, isAvailable }) {
    const response = await fetch(`${API_BASE_URL}/api/parts`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, category, stockQuantity, isAvailable }),
    })
    return handleResponse(response)
}

export async function updatePart(id, { name, category, stockQuantity, isAvailable }) {
    const response = await fetch(`${API_BASE_URL}/api/parts/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, category, stockQuantity, isAvailable }),
    })
    return handleResponse(response)
}

export async function updatePartAvailability(id, isAvailable) {
    const response = await fetch(`${API_BASE_URL}/api/parts/${id}/availability`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ isAvailable }),
    })
    return handleResponse(response)
}

export async function updatePartStock(id, stockQuantity) {
    const response = await fetch(`${API_BASE_URL}/api/parts/${id}/stock`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ stockQuantity }),
    })
    return handleResponse(response)
}

export async function deletePart(id) {
    const response = await fetch(`${API_BASE_URL}/api/parts/${id}`, {
        method: 'DELETE',
    })
    if (!response.ok) {
        const errorBody = await response.json().catch(() => ({}))
        throw new Error(errorBody.message || 'Failed to delete part.')
    }
}