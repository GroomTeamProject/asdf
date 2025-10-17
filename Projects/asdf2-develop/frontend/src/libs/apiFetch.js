// src/lib/apiFetch.js
const API_BASE = import.meta.env.VITE_API_URL
let refreshing = null

async function refreshAccessToken() {
  const refreshToken = localStorage.getItem('refreshToken')  // 로그인 시 같이 저장해야 함
  if (!refreshToken) throw new Error('no refresh token')

  const resp = await fetch(`${API_BASE}/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken })
  })

  if (!resp.ok) throw new Error(`refresh 실패 ${resp.status}`)
  const data = await resp.json()

  // 새 accessToken 저장
  localStorage.setItem('jwt', data.accessToken)

  // refreshToken도 응답에 있으면 갱신
  if (data.refreshToken) {
    localStorage.setItem('refreshToken', data.refreshToken)
  }

  return data.accessToken
}


export async function apiFetch(input, init = {}) {
  const headers = new Headers(init.headers || {})
  const t = localStorage.getItem('jwt')
  if (t && !headers.has('Authorization')) headers.set('Authorization', `Bearer ${t}`)

  let res = await fetch(input, { ...init, headers, credentials: 'include' })

  if (res.status === 401 || res.status === 403) {
    console.warn('[apiFetch] got', res.status, '→ try refresh')
    try {
      const newToken = await refreshAccessToken()
      headers.set('Authorization', `Bearer ${newToken}`)
      res = await fetch(input, { ...init, headers, credentials: 'include' })
      console.warn('[apiFetch] retried. status=', res.status)
    } catch (e) {
      console.error('[apiFetch] refresh failed', e)
      window.location.href = '/login'
      throw e
    }
  }
  return res
}
