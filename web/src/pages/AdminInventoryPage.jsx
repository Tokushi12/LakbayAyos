import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import BackButton from '../components/BackButton'
import {
  getAllParts,
  createPart,
  updatePart,
  updatePartAvailability,
  updatePartStock,
} from '../api/partsApi'
import { uploadPartImage } from '../api/storageApi'
import './AdminInventoryPage.css'

export default function AdminInventoryPage() {
  const navigate = useNavigate()
  const { logout } = useAuth()
  const [parts, setParts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [name, setName] = useState('')
  const [category, setCategory] = useState('')
  const [stockQuantity, setStockQuantity] = useState('')
  const [isAvailable, setIsAvailable] = useState(true)
  const [imageUrl, setImageUrl] = useState('')
  const [imageFile, setImageFile] = useState(null)
  const [uploadingImage, setUploadingImage] = useState(false)
  const [formError, setFormError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    loadParts()
  }, [])

  async function loadParts() {
    setLoading(true)
    setError('')
    try {
      const data = await getAllParts()
      setParts(data)
    } catch (err) {
      setError(err.message || 'Failed to load parts.')
    } finally {
      setLoading(false)
    }
  }

  function openAddForm() {
    setEditingId(null)
    setName('')
    setCategory('')
    setStockQuantity('')
    setIsAvailable(true)
    setImageUrl('')
    setImageFile(null)
    setFormError('')
    setShowForm(true)
  }

  function openEditForm(part) {
    setEditingId(part.id)
    setName(part.name)
    setCategory(part.category)
    setStockQuantity(String(part.stockQuantity))
    setIsAvailable(part.isAvailable)
    setImageUrl(part.imageUrl || '')
    setImageFile(null)
    setFormError('')
    setShowForm(true)
  }

  function handleImageFileChange(e) {
    const file = e.target.files[0]
    if (!file) return
    setImageFile(file)
    setImageUrl(URL.createObjectURL(file)) // local preview only
  }

  async function handleFormSubmit(e) {
    e.preventDefault()
    setFormError('')
    setSubmitting(true)

    try {
      let finalImageUrl = imageUrl.trim() || null

      // If a new file was picked, upload it to Supabase Storage first
      // and use the resulting public URL instead of the local preview.
      if (imageFile) {
        setUploadingImage(true)
        finalImageUrl = await uploadPartImage(imageFile)
        setUploadingImage(false)
      }

      const payload = {
        name,
        category,
        stockQuantity: Number(stockQuantity),
        isAvailable,
        imageUrl: finalImageUrl,
      }

      if (editingId) {
        await updatePart(editingId, payload)
      } else {
        await createPart(payload)
      }
      setShowForm(false)
      await loadParts()
    } catch (err) {
      setUploadingImage(false)
      setFormError(err.message || 'Failed to save part.')
    } finally {
      setSubmitting(false)
    }
  }

  async function handleToggleAvailability(part) {
    try {
      await updatePartAvailability(part.id, !part.isAvailable)
      await loadParts()
    } catch (err) {
      setError(err.message || 'Failed to update availability.')
    }
  }

  async function handleStockChange(part, newStock) {
    if (newStock < 0) return
    try {
      await updatePartStock(part.id, newStock)
      await loadParts()
    } catch (err) {
      setError(err.message || 'Failed to update stock.')
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
    <div className="admin-page">
      <header className="admin-topbar">
        <span className="wordmark">Lakbay Ayos</span>
        <button className="logout-btn" onClick={handleLogout}>
          Log out
        </button>
      </header>

      <main className="admin-main">
        <BackButton />
        <div className="page-header">
          <h1>Motor Parts</h1>
          <p className="admin-eyebrow">Inventory</p>
        </div>

        <div className="action-row">
          <button className="primary-btn" onClick={openAddForm}>
            + Add Part
          </button>
        </div>

        {error && <p className="admin-error">{error}</p>}

        {loading ? (
          <p className="admin-loading">Loading parts...</p>
        ) : parts.length === 0 ? (
          <p className="admin-empty">No parts in inventory yet. Add your first one.</p>
        ) : (
          <table className="parts-table">
            <thead>
              <tr>
                <th>Image</th>
                <th>Name</th>
                <th>Category</th>
                <th>Stock</th>
                <th>Available</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {parts.map((part) => (
                <tr key={part.id}>
                  <td>
                    {part.imageUrl ? (
                      <img src={part.imageUrl} alt={part.name} className="part-thumb" />
                    ) : (
                      <div className="part-thumb part-thumb-empty" />
                    )}
                  </td>
                  <td>{part.name}</td>
                  <td>{part.category}</td>
                  <td>
                    <div className="stock-adjust">
                      <button onClick={() => handleStockChange(part, part.stockQuantity - 1)}>
                        -
                      </button>
                      <span>{part.stockQuantity}</span>
                      <button onClick={() => handleStockChange(part, part.stockQuantity + 1)}>
                        +
                      </button>
                    </div>
                  </td>
                  <td>
                    <label className="toggle">
                      <input
                        type="checkbox"
                        checked={part.isAvailable}
                        onChange={() => handleToggleAvailability(part)}
                      />
                      <span className="toggle-slider"></span>
                    </label>
                  </td>
                  <td>
                    <button className="link-btn" onClick={() => openEditForm(part)}>
                      Edit
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </main>

      {showForm && (
        <div className="modal-overlay" onClick={() => setShowForm(false)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <h2>{editingId ? 'Edit Part' : 'Add New Part'}</h2>
            <form onSubmit={handleFormSubmit}>
              {formError && <p className="global-error">{formError}</p>}

              <div className="field">
                <label htmlFor="name">Name</label>
                <input
                  type="text"
                  id="name"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Brake pad"
                  required
                />
              </div>

              <div className="field">
                <label htmlFor="category">Category</label>
                <input
                  type="text"
                  id="category"
                  value={category}
                  onChange={(e) => setCategory(e.target.value)}
                  placeholder="Brakes"
                  required
                />
              </div>

              <div className="field">
                <label htmlFor="stockQuantity">Stock Quantity</label>
                <input
                  type="number"
                  id="stockQuantity"
                  value={stockQuantity}
                  onChange={(e) => setStockQuantity(e.target.value)}
                  min="0"
                  required
                />
              </div>

              <div className="field">
                <label htmlFor="imageFile">Photo</label>
                <input
                  type="file"
                  id="imageFile"
                  accept="image/*"
                  onChange={handleImageFileChange}
                />
                {imageUrl && (
                  <img src={imageUrl} alt="Preview" className="image-preview" />
                )}
              </div>

              <div className="field checkbox-field">
                <label>
                  <input
                    type="checkbox"
                    className="small-checkbox"
                    checked={isAvailable}
                    onChange={(e) => setIsAvailable(e.target.checked)}
                  />
                  Available for booking
                </label>
              </div>

              <div className="modal-actions">
                <button type="button" className="secondary-btn" onClick={() => setShowForm(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn" disabled={submitting}>
                  {uploadingImage ? 'Uploading photo...' : submitting ? 'Saving...' : 'Save'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}