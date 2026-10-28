import { useState } from 'react'
import AuthForm from './AuthForm.jsx'
import TweetForm from './TweetForm.jsx'
import TweetList from './TweetList.jsx'
import { getAuth, logout } from './api.js'

export default function App() {
  const [auth, setAuth] = useState(getAuth())
  const [refreshKey, setRefreshKey] = useState(0)

  const handleLogout = () => {
    logout()
    setAuth(null)
  }

  return (
    <main className="container">
      <header className="topbar">
        <h1>Twitter Client</h1>
        {auth && (
          <div className="session">
            <span>@{auth.user.username}</span>
            <button className="secondary" onClick={handleLogout}>Çıkış</button>
          </div>
        )}
      </header>

      {auth ? (
        <TweetForm onCreated={() => setRefreshKey((k) => k + 1)} />
      ) : (
        <AuthForm onAuth={setAuth} />
      )}

      {/* key: giris/cikis yapinca liste sifirdan kurulur ve kullanicinin kendi tweetleri gelir */}
      <TweetList key={auth?.user.id ?? 'guest'} auth={auth} refreshKey={refreshKey} />
    </main>
  )
}
