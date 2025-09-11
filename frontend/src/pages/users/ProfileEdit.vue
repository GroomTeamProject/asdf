<template>
  <div class="p-6 max-w-md mx-auto">
    <h1 class="text-2xl font-bold mb-4">내 정보 수정</h1>

    <form @submit.prevent="updateProfile" class="space-y-4">
      <div>
        <label class="block mb-1 font-medium">이름</label>
        <input type="text" v-model="form.name" class="w-full border p-2 rounded" />
      </div>

      <div>
        <label class="block mb-1 font-medium">이메일</label>
        <input type="email" v-model="form.email" class="w-full border p-2 rounded" />
      </div>

      <div>
        <label class="block mb-1 font-medium">휴대폰</label>
        <input type="text" v-model="form.phone" class="w-full border p-2 rounded" />
      </div>

      <div class="flex justify-between items-center">
        <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded">저장</button>
        <button type="button" @click="goToChangePassword" class="bg-gray-300 px-4 py-2 rounded">비밀번호 변경</button>
      </div>
    </form>
  </div>
</template>

<script>
import api from '../../api/auth/axios.js';

export default {
  name: 'Profile',
  data() {
    return {
      form: {
        name: '',
        email: '',
        phone: '',
      },
    };
  },
  methods: {
    async loadProfile() {
      try {
        const response = await api.get('/users/me');
        this.form = response.data;
      } catch (error) {
        console.error('프로필 조회 실패:', error);
        alert('프로필 조회 실패');
      }
    },
    async updateProfile() {
      try {
        const response = await api.put('/users/me', this.form);
        alert('내 정보가 업데이트되었습니다.');
        this.form = response.data; // 서버에서 업데이트된 데이터 반영
      } catch (error) {
        console.error('프로필 업데이트 실패:', error);
        alert('프로필 업데이트 실패');
      }
    },
    goToChangePassword() {
      this.$router.push('/change-password');
    },
  },
  mounted() {
    this.loadProfile();
  },
};
</script>

<style scoped>
/* TailwindCSS 기본 스타일 사용, 필요 시 추가 커스터마이징 가능 */
</style>
