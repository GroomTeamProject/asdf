<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center">
    <div class="w-full max-w-md bg-white p-8 rounded-lg shadow-lg">
      <!-- Header -->
      <h1 class="text-2xl font-bold mb-6 text-center text-gray-800">QuickDeliver 로그인</h1>

      <!-- Login Form -->
      <form @submit.prevent="onSubmit" class="space-y-4">
        <div>
          <label for="email" class="block text-gray-700 mb-1">이메일</label>
          <input type="email" id="email" v-model="form.email" required
            class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
        </div>

        <div>
          <label for="password" class="block text-gray-700 mb-1">비밀번호</label>
          <input type="password" id="password" v-model="form.password" required
            class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
        </div>

        <button type="submit"
          class="w-full bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">로그인</button>
      </form>

      <!-- 링크 -->
      <p class="mt-4 text-center text-gray-600">
        아직 계정이 없으신가요?
        <a href="/signup" class="text-green-500 hover:underline">회원가입</a>
      </p>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Login',
  data() {
    return {
      form: {
        email: '',
        password: ''
      }
    }
  },
  methods: {
    async onSubmit() {
      this.loading = true
      try {
        // 1️⃣ 로그인 API 호출
        const response = await axios.post('http://localhost:8080/api/auth/login', this.form)

        // 2️⃣ JWT 토큰과 사용자 정보 저장 (로컬스토리지)
        const { token, email, name, userType } = response.data
        localStorage.setItem('jwt', token)
        localStorage.setItem('userEmail', email)
        localStorage.setItem('userName', name)
        localStorage.setItem('userType', userType)

        console.log('토큰 저장 완료:', token);

        alert(`로그인 성공! 환영합니다, ${name}님.`)

        // 3️⃣ 로그인 후 권한에 맞는 페이지로 이동 (예: 대시보드)
        //this.$router.push('/dashboard') // /dashboard 라우트는 나중에 만들어 주세요
        if (userType === 'CUSTOMER') {
          this.$router.push('/customer-main')
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

<style scoped>
/* 추가 스타일 필요 시 작성 */
</style>
