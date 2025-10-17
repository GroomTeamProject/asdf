<template>
  <div class="relative min-h-screen bg-gradient-to-br from-blue-400 to-indigo-600 flex items-center justify-center">
    <!-- 배경 그림자 -->
    <div class="absolute inset-0 bg-black/20"></div>

    <!-- 로그인 카드 -->
    <div class="relative z-10 bg-white rounded-2xl shadow-xl p-10 w-full max-w-md">
      <!-- 로고 및 홍보 멘트 -->
      <div class="flex flex-col items-center mb-6">
        <div class="w-16 h-16 bg-blue-500 text-white flex items-center justify-center rounded-full text-2xl font-bold">
          QD
        </div>
        <h1 class="mt-4 text-2xl font-bold text-gray-800">QuickDeliver</h1>
        <p class="text-gray-500 text-center text-sm mt-2">
          집 앞까지 빠르고 편리하게! <br />
          원하는 음식을 언제든 즐겨보세요.
        </p>
      </div>

      <!-- 로그인 폼 -->
      <form @submit.prevent="onSubmit" class="space-y-4">
        <div>
          <label class="block text-gray-600 text-sm mb-1">이메일</label>
          <input
            v-model="form.email"
            type="email"
            required
            placeholder="이메일 입력"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
          />
        </div>

        <div class="relative">
          <label class="block text-gray-600 text-sm mb-1">비밀번호</label>
          <input
            :type="showPassword ? 'text' : 'password'"
            v-model="form.password"
            required
            placeholder="비밀번호 입력"
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none pr-10"
          />
          <button
            type="button"
            class="absolute inset-y-0 right-0 px-3 flex items-center text-gray-500"
            @click="togglePassword"
          >
            <svg v-if="showPassword" xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.477 0-8.268-2.943-9.542-7a9.956 9.956 0 012.042-3.368m2.122-2.122A9.956 9.956 0 0112 5c4.477 0 8.268 2.943 9.542 7a9.956 9.956 0 01-1.046 1.927M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3l18 18"/>
            </svg>
          </button>
        </div>

        <button
          type="submit"
          class="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 font-semibold transition-colors"
          :disabled="loading"
        >
          {{ loading ? '로그인 중...' : '로그인하고 시작' }}
        </button>
      </form>

      <!-- 회원가입 -->
      <p class="text-center text-gray-500 text-sm mt-6">
        아직 QuickDeliver 계정이 없으신가요?
        <router-link to="/signup" class="text-blue-500 font-medium hover:underline">회원가입</router-link>
      </p>

      <!-- 홍보 배너 -->
      <div class="mt-6 bg-yellow-100 text-yellow-800 p-3 rounded-lg text-center text-sm shadow-inner">
        지금 가입하고 혜택을 누려보세요!
      </div>
    </div>
  </div>
</template>

<script>
//import axios from 'axios'
import api from '../../api/index.js'

export default {
  name: 'LoginPage',
  data() {
    return {
      form: { email: '', password: '' },
      showPassword: false,
      loading: false
    }
  },
  methods: {
    togglePassword() {
      this.showPassword = !this.showPassword
    },
    async onSubmit() {
      this.loading = true
      try {
        //const response = await api.post(`${import.meta.env.VITE_API_URL}/auth/login`, this.form)
        const response = await api.post('/auth/login', this.form)
        const { token, id, email, name, userType } = response.data
        localStorage.setItem('jwt', token)
        //localStorage.setItem('refreshToken', refreshtoken)
        localStorage.setItem('userId', id)
        localStorage.setItem('userEmail', email)
        localStorage.setItem('userName', name)
        localStorage.setItem('userType', userType)

        alert(`로그인 성공! 환영합니다, ${name}님.`)
        //this.$router.push('/main-page')

        if (userType === 'CUSTOMER') {
          this.$router.push('/customer')
        } else if (userType === 'OWNER') {
          this.$router.push('/owner-main')
        } else if (userType === 'RIDER') {
          this.$router.push('/driver-main')
        }
      } catch (error) {
        console.error('로그인 실패:', error.response?.data || error)
        alert('로그인 실패: ' + (error.response?.data?.error || error.message))
      } finally {
        this.loading = false
      }
    }
  }
}
</script>