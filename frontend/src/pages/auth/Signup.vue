<!-- frontend/src/pages/Signup.vue -->
<template>
  <div class="min-h-screen bg-gray-100">
    <!-- Header -->
    <header class="bg-white border-b-2 border-gray-400 p-4">
      <div class="max-w-6xl mx-auto flex items-center">
        <h1 class="text-2xl text-gray-800">QuickDeliver</h1>
      </div>
    </header>

    <!-- Main Content -->
    <main class="max-w-6xl mx-auto p-4">
      <h1 class="text-xl font-bold mb-6 text-gray-800">회원가입</h1>
      <form @submit.prevent="onSubmit" class="space-y-4 bg-white p-6 rounded-lg border border-gray-300">
        <!-- 기본 정보 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-gray-700 mb-1" for="email">이메일</label>
            <input type="email" id="email" v-model="form.email" required
              class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
          </div>
          <div>
            <label class="block text-gray-700 mb-1" for="phone">전화번호</label>
            <input type="tel" id="phone" v-model="form.phone" required
              class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
          </div>
          <div>
            <label class="block text-gray-700 mb-1" for="password">비밀번호</label>
            <input type="password" id="password" v-model="form.password" required
              class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
          </div>
          <div>
            <label class="block text-gray-700 mb-1" for="passwordCheck">비밀번호 확인</label>
            <input type="password" id="passwordCheck" v-model="form.passwordCheck" required
              class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
          </div>
          <div>
            <label class="block text-gray-700 mb-1" for="name">이름</label>
            <input type="text" id="name" v-model="form.name" required
              class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
          </div>
          <div>
            <label class="block text-gray-700 mb-1" for="userType">유저 유형</label>
            <select id="userType" v-model="form.userType" required
              class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600">
              <option value="CUSTOMER">CUSTOMER</option>
              <option value="OWNER">OWNER</option>
              <option value="RIDER">RIDER</option>
            </select>
          </div>
        </div>

        <!-- 주소 정보 -->
        <h2 class="text-gray-800 font-semibold mt-4 mb-2">주소 정보</h2>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="block text-gray-700 mb-1" for="addressName">주소 이름</label>
            <input type="text" id="addressName" v-model="form.addressName"
              class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
          </div>
          <div>
            <label class="block text-gray-700 mb-1" for="zipcode">우편번호</label>
            <input type="text" id="zipcode" v-model="form.zipcode"
              class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
          </div>
          <div>
            <label class="block text-gray-700 mb-1" for="address">주소</label>
            <input type="text" id="address" v-model="form.address"
              class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
          </div>
          <div>
            <label class="block text-gray-700 mb-1" for="detailAddress">상세주소</label>
            <input type="text" id="detailAddress" v-model="form.detailAddress"
              class="w-full border border-gray-400 px-3 py-2 rounded focus:outline-none focus:border-gray-600" />
          </div>
        </div>
        <div class="flex items-center gap-2">
          <input type="checkbox" id="isDefault" v-model="form.isDefault" class="h-4 w-4" />
          <label for="isDefault" class="text-gray-700">기본 주소로 설정</label>
        </div>

        <button type="submit" class="mt-4 bg-green-500 text-white px-6 py-2 rounded hover:bg-green-600">
          회원가입
        </button>
      </form>
    </main>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Signup',
  data() {
    return {
      form: {
        email: '',
        password: '',
        passwordCheck: '',
        name: '',
        phone: '',
        userType: 'RIDER',
        addressName: '',
        address: '',
        detailAddress: '',
        zipcode: '',
        isDefault: true
      }
    }
  },
  methods: {
    async onSubmit() {
      try {
        //API 요청
        const response = await axios.post(`${import.meta.env.VITE_API_URL}/auth/signup`, this.form)
        console.log('회원가입 성공:', response.data)
        alert('회원가입 성공!')
        this.$router.push('/login')
      } catch (error) {
        console.error(
          "회원가입 실패:",
          error.response ? error.response.data : error.message
        );

        // 백엔드에서 내려주는 메시지가 있으면 표시
        if (error.response && error.response.data && error.response.data.message) {
          alert("회원가입 실패: " + error.response.data.message);
        } else {
          alert("회원가입 실패: 알 수 없는 오류가 발생했습니다.");
        }
      }
    }
  }
}

</script>
