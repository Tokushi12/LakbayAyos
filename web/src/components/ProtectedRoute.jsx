import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function ProtectedRoute({ children, allowedRole }) {
  const { session, role, loading } = useAuth()

  if (loading) {
    return null
  }

  if (!session) {
    return <Navigate to="/login" replace />
  }

  if (allowedRole && role !== allowedRole) {
    return <Navigate to="/login" replace />
  }

  return children
}
