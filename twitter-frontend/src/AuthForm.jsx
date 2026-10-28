import { useState } from 'react'
import { login, register } from './api.js'

export default function AuthForm({ onAuth }) {
  const [mode, setMode] = useState('login') // 'login' | 'register'
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      if (mode === 'register') {
        await register(form)
      }
      // Kayit sonrasi da ayni bilgilerle otomatik giris yapilir
      const auth = await login(form.username, form.password)
      onAuth(auth)
    } catch (err) {
      const details = err.errors ? ' ' + Object.values(err.errors).join(', ') : ''
      setError(err.status === 401 ? 'Kullanıcı adı veya şifre hatalı' : err.message + details)
    } finally {
      setLoading(false)
    }
  }

  return (
    <form className="card" onSubmit={handleSubmit}>
      <div className="tabs">
        <button type="button" className={mode === 'login' ? 'tab active' : 'tab'} onClick={() => setMode('login')}>
          Giriş
        </button>
        <button type="button" className={mode === 'register' ? 'tab active' : 'tab'} onClick={() => setMode('register')}>
          Kayıt ol
        </button>
      </div>

      <input placeholder="Kullanıcı adı" value={form.username} onChange={update('username')} required minLength={3} maxLength={30} />
      {mode === 'register' && (
        <input type="email" placeholder="E-posta" value={form.email} onChange={update('email')} required />
      )}
      <input type="password" placeholder="Şifre" value={form.password} onChange={update('password')} required minLength={mode === 'register' ? 6 : undefined} />

      {error && <p className="error">{error}</p>}
      <button type="submit" disabled={loading}>
        {loading ? 'Bekleyin…' : mode === 'login' ? 'Giriş yap' : 'Kayıt ol ve giriş yap'}
      </button>
    </form>
  )
}
