<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <h1 class="text-3xl font-bold mb-6">마이페이지</h1>
    <p class="text-gray-600 mb-8">내 정보를 관리할 수 있는 페이지입니다.</p>

    <div class="grid grid-cols-1 gap-4 max-w-xs">
      <button
        class="w-full px-4 py-3 bg-green-500 text-white rounded-lg hover:bg-green-600 shadow"
        @click="goToAddress"
      >
        주소관리
      </button>

      <button
        class="w-full px-4 py-3 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 shadow"
        @click="goToProfile"
      >
        내 정보 수정
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MyPage',
  methods: {
    goToAddress() {
      this.$router.push('/profile/addresses'); // 주소관리 페이지 경로
    },
    goToProfile() {
      this.$router.push('/profile'); // 내정보수정 페이지 경로
    },
  },
};
</script>
