<template>
  <div class="p-8">
    <h1 class="text-2xl font-bold mb-4">대시보드</h1>
    <p>환영합니다, {{ userName }}님! ({{ userEmail }}, {{ userType }})</p>
    <button @click="logout" class="mt-4 bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">
      로그아웃
    </button>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  data() {
    return {
      userName: localStorage.getItem('userName'),
      userEmail: localStorage.getItem('userEmail'),
      userType: localStorage.getItem('userType')
    }
  },
  methods: {
    logout() {
      localStorage.clear()
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
/* 필요 시 추가 스타일 */
</style>
