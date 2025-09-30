import axios from 'axios'

// Axios 인스턴스 생성
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // 쿠키 전송 허용 (Refresh Token용)
})

// Access Token 만료 체크 함수
function isTokenExpired(token) {
  if (!token) return true
  const payload = JSON.parse(atob(token.split('.')[1]))
  const now = Math.floor(Date.now() / 1000)
  return payload.exp < now
}

// Refresh Token 요청
async function refreshToken() {
  try {
    // 쿠키 기반이므로 null body, withCredentials=true
    const response = await axios.post(
      `${import.meta.env.VITE_API_URL}/auth/refresh`,
      null,
      { withCredentials: true }
    )

    const { accessToken } = response.data
    if (!accessToken) throw new Error('Access Token 재발급 실패')

    localStorage.setItem('jwt', accessToken)
    return accessToken
  } catch (err) {
    console.error('Refresh Token 오류:', err)
    localStorage.clear()
    window.location.href = '/login'
    throw err
  }
}

// 요청 인터셉터
api.interceptors.request.use(
  async (config) => {
    let token = localStorage.getItem('jwt')
    console.log('🔍 JWT 토큰:', token ? '존재함' : '없음')

    if (token && isTokenExpired(token)) {
      token = await refreshToken()
      console.log('🔄 Access Token 갱신 완료')
    }

    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      console.log(
        '🔍 Authorization 헤더:',
        config.headers.Authorization.substring(0, 20) + '...'
      )
    }

    console.log('🔍 요청 URL:', config.baseURL + config.url)
    return config
  },
  (error) => {
    console.error('❌ 요청 인터셉터 오류:', error)
    return Promise.reject(error)
  }
)

// 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    console.log('✅ API 응답 성공:', response.status)
    return response
  },
  (error) => {
    console.error(
      '❌ API 응답 오류:',
      error.response?.status,
      error.message
    )

    if (error.response?.status === 401) {
      localStorage.removeItem('jwt')
      localStorage.removeItem('userEmail')
      localStorage.removeItem('userName')
      localStorage.removeItem('userType')

      alert('로그인이 만료되었습니다. 다시 로그인해주세요.')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
