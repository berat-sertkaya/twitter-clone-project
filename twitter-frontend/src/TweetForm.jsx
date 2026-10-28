import { useState } from 'react'
import { createTweet } from './api.js'

const MAX_LENGTH = 280

export default function TweetForm({ onCreated }) {
  const [content, setContent] = useState('')
  const [location, setLocation] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      await createTweet(content.trim(), location.trim())
      setContent('')
      setLocation('')
      onCreated() // listeyi yeniden getirtir
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <textarea
        placeholder="Neler oluyor?"
        value={content}
        onChange={(e) => setContent(e.target.value)}
        maxLength={MAX_LENGTH}
        rows={3}
        required
      />
      <input placeholder="Konum (isteğe bağlı)" value={location} onChange={(e) => setLocation(e.target.value)} maxLength={100} />
      <div className="form-row">
        <span className="muted">{content.length}/{MAX_LENGTH}</span>
        <button type="submit" disabled={loading || !content.trim()}>
          {loading ? 'Gönderiliyor…' : 'Tweetle'}
        </button>
      </div>
      {error && <p className="error">{error}</p>}
    </form>
  )
}
