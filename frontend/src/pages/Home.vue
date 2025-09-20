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
      <form @submit.prevent="login" class="space-y-4">
        <div>
          <label class="block text-gray-600 text-sm mb-1">이메일</label>
          <input
            v-model="email"
            type="email"
            required
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
            placeholder="이메일 입력"
          />
        </div>

        <div>
          <label class="block text-gray-600 text-sm mb-1">비밀번호</label>
          <input
            v-model="password"
            type="password"
            required
            class="w-full border border-gray-300 rounded-lg px-4 py-2 focus:ring-2 focus:ring-blue-400 focus:outline-none"
            placeholder="비밀번호 입력"
          />
        </div>

        <button
          type="submit"
          class="w-full bg-blue-500 text-white py-2 rounded-lg hover:bg-blue-600 transition-colors font-semibold"
        >
          로그인하고 시작
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

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const email = ref('')
const password = ref('')

const login = () => {
  console.log('로그인 시도', email.value, password.value)
  // TODO: API 호출 후 인증 처리
  router.push('/main-page')
}
</script>
