// Tum backend istekleri burada toplanir (base URL + auth header yardimcisi)
const BASE_URL = 'http://localhost:3000'
const STORAGE_KEY = 'twitter-auth'

export class ApiError extends Error {
  constructor(message, status, errors) {
    super(message)
    this.status = status
    this.errors = errors
  }
}

// ---- Kimlik bilgisi (sessionStorage: sekme kapaninca silinir) ----
export function getAuth() {
  try {
    return JSON.parse(sessionStorage.getItem(STORAGE_KEY))
  } catch {
    return null
  }
}

function saveAuth(auth) {
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(auth))
}

export function logout() {
  sessionStorage.removeItem(STORAGE_KEY)
}

// username:password -> base64 (Turkce karakterler icin UTF-8 guvenli)
function basicToken(username, password) {
  const bytes = new TextEncoder().encode(`${username}:${password}`)
  let binary = ''
  bytes.forEach((b) => (binary += String.fromCharCode(b)))
  return btoa(binary)
}

// Giris yapildiysa Authorization basligini dondurur
function authHeader(token) {
  return token ? { Authorization: `Basic ${token}`, 'X-Requested-With': 'XMLHttpRequest' } : {}
}

async function request(path, { method = 'GET', body, token = getAuth()?.token } = {}) {
  let res
  try {
    res = await fetch(`${BASE_URL}${path}`, {
      method,
      headers: {
        ...(body !== undefined ? { 'Content-Type': 'application/json' } : {}),
        ...authHeader(token),
      },
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch {
    // Sunucu kapali veya CORS engeli: tarayici ayrintiyi JS'e vermez
    throw new ApiError('Sunucuya ulaşılamadı (backend kapalı veya CORS engeli olabilir)', 0)
  }

  if (res.status === 204) return null
  const data = await res.json().catch(() => null)
  if (!res.ok) {
    throw new ApiError(data?.message || `Hata (${res.status})`, res.status, data?.errors)
  }
  return data
}

// ---- Auth ----
export function register({ username, email, password }) {
  return request('/register', { method: 'POST', body: { username, email, password }, token: null })
}

export async function login(username, password) {
  const token = basicToken(username, password)
  const user = await request('/login', { method: 'POST', token })
  const auth = { token, user }
  saveAuth(auth)
  return auth
}

// ---- Tweet ----
export function fetchTweets(userId, page = 0, size = 5) {
  return request(`/tweet/findByUserId?userId=${encodeURIComponent(userId)}&page=${page}&size=${size}`)
}

export function createTweet(content, location) {
  return request('/tweet', { method: 'POST', body: { content, location: location || null } })
}

// ---- Like ----
export function likeTweet(tweetId) {
  return request('/like', { method: 'POST', body: { tweetId } })
}

export function dislikeTweet(tweetId) {
  return request('/dislike', { method: 'POST', body: { tweetId } })
}
