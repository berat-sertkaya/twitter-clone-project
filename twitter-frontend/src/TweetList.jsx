import { useEffect, useState } from 'react'
import { dislikeTweet, fetchTweets, likeTweet } from './api.js'

const PAGE_SIZE = 5

export default function TweetList({ auth, refreshKey }) {
  const [inputUserId, setInputUserId] = useState(String(auth?.user.id ?? 1))
  const [userId, setUserId] = useState(String(auth?.user.id ?? 1))
  const [page, setPage] = useState(0)
  const [reload, setReload] = useState(0)
  const [data, setData] = useState(null) // { content, page, totalPages, last, ... }
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [actionError, setActionError] = useState('')

  // userId, sayfa veya yenileme tetikleyicisi degisince liste backend'den tekrar cekilir
  useEffect(() => {
    let cancelled = false
    setLoading(true)
    setError('')
    fetchTweets(userId, page, PAGE_SIZE)
      .then((res) => !cancelled && setData(res))
      .catch((err) => {
        if (!cancelled) {
          setData(null)
          setError(err.message)
        }
      })
      .finally(() => !cancelled && setLoading(false))
    return () => {
      cancelled = true
    }
  }, [userId, page, reload, refreshKey])

  const handleSearch = (e) => {
    e.preventDefault()
    setPage(0)
    setUserId(inputUserId)
  }

  // Kalp butonu: like at; zaten begenilmisse (409) begeniyi geri al
  const handleLike = async (tweetId) => {
    setActionError('')
    try {
      try {
        await likeTweet(tweetId)
      } catch (err) {
        if (err.status !== 409) throw err
        await dislikeTweet(tweetId)
      }
      setReload((r) => r + 1) // islemden sonra listeyi yenile
    } catch (err) {
      setActionError(err.message)
    }
  }

  return (
    <section>
      <form className="toolbar" onSubmit={handleSearch}>
        <input
          type="number"
          min="1"
          value={inputUserId}
          onChange={(e) => setInputUserId(e.target.value)}
          placeholder="Kullanıcı ID"
        />
        <button type="submit" disabled={loading || !inputUserId}>
          Tweetleri getir
        </button>
      </form>

      {error && <p className="error">{error}</p>}
      {actionError && <p className="error">{actionError}</p>}
      {loading && <p className="muted">Yükleniyor…</p>}
      {!loading && !error && data?.content.length === 0 && <p className="muted">Gösterilecek tweet yok.</p>}

      <ul className="tweets">
        {data?.content.map((t) => (
          <li key={t.id} className="tweet">
            <header>
              <strong>@{t.username}</strong>
              {t.parentTweetId && <span className="badge">retweet</span>}
              <time>{t.createdAt ? new Date(t.createdAt).toLocaleString('tr-TR') : ''}</time>
            </header>
            <p>{t.content}</p>
            <footer>
              <button
                className="like"
                onClick={() => handleLike(t.id)}
                disabled={!auth}
                title={auth ? 'Beğen / beğenmekten vazgeç' : 'Beğenmek için giriş yap'}
              >
                ♥ {t.likeCount}
              </button>
              <span>💬 {t.commentCount}</span>
              <span>🔁 {t.retweetCount}</span>
              <span>👁 {t.viewCount}</span>
              {t.location && <span>📍 {t.location}</span>}
            </footer>
          </li>
        ))}
      </ul>

      {data && data.totalPages > 1 && (
        <nav className="pager">
          <button className="secondary" onClick={() => setPage((p) => p - 1)} disabled={loading || page === 0}>
            ← Önceki
          </button>
          <span className="muted">
            Sayfa {data.page + 1} / {data.totalPages}
          </span>
          <button className="secondary" onClick={() => setPage((p) => p + 1)} disabled={loading || data.last}>
            Sonraki →
          </button>
        </nav>
      )}
    </section>
  )
}
