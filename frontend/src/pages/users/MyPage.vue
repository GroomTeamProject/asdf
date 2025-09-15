<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <h1 class="text-3xl font-bold mb-6">마이페이지</h1>
    <p class="text-gray-600 mb-8">내 정보를 관리할 수 있는 페이지입니다.</p>

    <div class="grid grid-cols-1 gap-4 max-w-xs">
      <button
        class="w-full px-4 py-3 bg-gray-500 text-white rounded-lg hover:bg-green-600 shadow"
        @click="goToAddress"
      >
        주소관리
      </button>

      <button
        class="w-full px-4 py-3 bg-gray-500 text-white rounded-lg hover:bg-yellow-600 shadow"
        @click="goToProfile"
      >
        내 정보 수정
      </button>

      <button
        class="w-full px-4 py-3 bg-gray-500 text-white rounded-lg hover:bg-yellow-600 shadow"
        @click="deleteAccount"
      >
        계정 탈퇴
      </button>

      <button
        class="w-full px-4 py-3 bg-white-500 text-black rounded-lg hover:bg-yellow-600 shadow"
        @click="logout"
      >
        로그아웃
      </button>
    </div>
  </div>
</template>

<script>
import api from '../../api/auth/axios.js';
export default {
  name: 'MyPage',
  methods: {
    goToAddress() {
      this.$router.push('/profile/addresses'); // 주소관리 페이지 경로
    },
    goToProfile() {
      this.$router.push('/profile'); // 내정보수정 페이지 경로
    },
    logout(){
      // 토큰 삭제
      localStorage.removeItem('jwt');
      // 로그인 페이지로 이동
      this.$router.push('/login');
    },
    async deleteAccount() {
      if (!confirm('정말 계정을 삭제하시겠습니까?')) return;
      try {
        await api.delete('/users/me/deactivate'); // 서버에서 계정 삭제 API
        alert('계정이 삭제되었습니다.');
        localStorage.removeItem('jwt');
        this.$router.push('/');
      } catch (error) {
        console.error('계정 삭제 실패', error);
        alert('계정 삭제에 실패했습니다.');
      }
    },
  },
};
</script>
