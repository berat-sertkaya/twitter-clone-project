import { useState } from 'react'

const API_URL = 'http://localhost:3000'

export default function TweetList() {
  const [userId, setUserId] = useState('1')
  const [tweets, setTweets] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const fetchTweets = async () => {
    setLoading(true)
    setError('')
    try {
      // Backend CORS'a izin vermiyorsa tarayici bu istegi engeller ve fetch "Failed to fetch" ile reddedilir.
      const res = await fetch(`${API_URL}/tweet/findByUserId?userId=${userId}`)
      if (!res.ok) {
        const body = await res.json().catch(() => ({}))
        throw new Error(body.message || `Sunucu hatası (${res.status})`)
      }
      setTweets(await res.json())
    } catch (err) {
      setTweets([])
      setError(
        err.message === 'Failed to fetch'
          ? 'İstek başarısız: CORS engeli olabilir. Tarayıcı konsolunu kontrol et.'
          : err.message
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <section>
      <div className="toolbar">
        <input
          type="number"
          min="1"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
          placeholder="Kullanıcı ID"
        />
        <button onClick={fetchTweets} disabled={loading || !userId}>
          {loading ? 'Yükleniyor…' : 'Tweetleri getir'}
        </button>
      </div>

      {error && <p className="error">{error}</p>}
      {!loading && !error && tweets.length === 0 && <p className="muted">Gösterilecek tweet yok.</p>}

      <ul className="tweets">
        {tweets.map((t) => (
          <li key={t.id} className="tweet">
            <header>
              <strong>@{t.username}</strong>
              {t.parentTweetId && <span className="badge">retweet</span>}
              <time>{t.createdAt ? new Date(t.createdAt).toLocaleString('tr-TR') : ''}</time>
            </header>
            <p>{t.content}</p>
            <footer>
              <span>♥ {t.likeCount}</span>
              <span>💬 {t.commentCount}</span>
              <span>🔁 {t.retweetCount}</span>
              <span>👁 {t.viewCount}</span>
              {t.location && <span>📍 {t.location}</span>}
            </footer>
          </li>
        ))}
      </ul>
    </section>
  )
}
