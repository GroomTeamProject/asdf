<template>
  <div class="p-6 max-w-md mx-auto">
    <h1 class="text-2xl font-bold mb-6">비밀번호 변경</h1>
    <form @submit.prevent="changePassword" class="space-y-4">
      <div>
        <label class="block mb-1 font-medium">현재 비밀번호</label>
        <input 
          type="password" 
          v-model="currentPassword" 
          class="w-full border p-2 rounded"
          required
        />
      </div>

      <div>
        <label class="block mb-1 font-medium">새 비밀번호</label>
        <input 
          type="password" 
          v-model="newPassword" 
          class="w-full border p-2 rounded"
          required
        />
      </div>

      <div>
        <label class="block mb-1 font-medium">새 비밀번호 확인</label>
        <input 
          type="password" 
          v-model="confirmPassword" 
          class="w-full border p-2 rounded"
          required
        />
      </div>

      <div class="flex justify-between mt-6">
        <button 
          type="button" 
          @click="$router.back()" 
          class="bg-gray-300 px-4 py-2 rounded"
        >
          취소
        </button>
        <button 
          type="submit" 
          class="bg-blue-500 text-white px-4 py-2 rounded"
        >
          변경하기
        </button>
      </div>
    </form>
  </div>
</template>

<script>
import api from '../../api/auth/axios.js';

export default {
  name: 'ChangePassword',
  data() {
    return {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    };
  },
  methods: {
    async changePassword() {
      if (this.newPassword !== this.confirmPassword) {
        alert('새 비밀번호가 일치하지 않습니다.');
        return;
      }

      try {
        await api.patch('/users/me/password', {
          currentPassword: this.currentPassword,
          newPassword: this.newPassword
        });
        alert('비밀번호가 성공적으로 변경되었습니다.');
        this.$router.push('/profile');
      } catch (error) {
        if (error.response) {
          console.error('서버 응답:', error.response.data);
          alert(`비밀번호 변경 실패: ${error.response.data.message || '알 수 없는 오류'}`);
        } else {
          console.error('요청 실패:', error);
          alert('서버에 연결할 수 없습니다.');
        }
      }
    }
  }
};
</script>