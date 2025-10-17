<template>
  <div class="min-h-screen bg-gray-100 flex items-center justify-center p-4">
    <!-- 메인 역할 선택 카드 -->
    <div class="w-full max-w-md wireframe-card">
      <!-- 헤더 -->
      <div class="text-center border-b border-gray-300 p-6">
        <h1 class="text-3xl text-gray-800 mb-2">QuickDeliver</h1>
        <p class="text-gray-600">배달 서비스 플랫폼</p>
      </div>

      <!-- 역할 선택 버튼 -->
      <div class="space-y-4 p-6">
        <div class="grid gap-3">
          <button
            @click="navigateToLogin('customer')"
            class="w-full h-12 wireframe-button rounded-md"
          >
            고객으로 시작하기
          </button>

          <button
            @click="navigateToLogin('owner')"
            class="w-full h-12 wireframe-button rounded-md"
          >
            점주로 시작하기
          </button>

          <button
            @click="navigateToLogin('driver')"
            class="w-full h-12 wireframe-button rounded-md"
          >
            배달원으로 시작하기
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Main',
  methods: {
    navigateToLogin(role) {
      localStorage.setItem('selectedRole', role)
      this.$router.push('/login')
    },
  },
  mounted() {
    console.log('QuickDeliver 메인 페이지가 로드되었습니다.')
  },
}
</script>

