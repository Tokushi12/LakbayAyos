import { useNavigate } from 'react-router-dom'

export default function BackButton({ className = 'back-btn' }) {
  const navigate = useNavigate()

  return (
    <button className={className} onClick={() => navigate(-1)}>
      &larr; Back
    </button>
  )
}
