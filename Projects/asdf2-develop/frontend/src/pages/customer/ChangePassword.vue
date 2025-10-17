<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center">
    <div class="bg-white shadow-lg rounded-lg p-8 w-full max-w-md">
      <h2 class="text-xl font-semibold text-gray-900 mb-6">비밀번호 변경</h2>
      
      <form @submit.prevent="handleChangePassword" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700">현재 비밀번호</label>
          <input v-model="currentPassword" type="password" 
                 class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:ring-blue-500 focus:border-blue-500"/>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">새 비밀번호</label>
          <input v-model="newPassword" type="password" 
                 class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:ring-blue-500 focus:border-blue-500"/>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">새 비밀번호 확인</label>
          <input v-model="confirmPassword" type="password" 
                 class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:ring-blue-500 focus:border-blue-500"/>
        </div>

        <button type="submit" 
                class="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700">
          변경하기
        </button>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api/customer/userApi'

export default {
  name: 'ChangePassword',
  setup() {
    const router = useRouter()
    const currentPassword = ref('')
    const newPassword = ref('')
    const confirmPassword = ref('')

    const handleChangePassword = async () => {
      if (newPassword.value !== confirmPassword.value) {
        alert('새 비밀번호가 일치하지 않습니다.')
        return
      }
      try {
        await userApi.changePassword({
          currentPassword: currentPassword.value,
          newPassword: newPassword.value
        })
        alert('비밀번호가 변경되었습니다. 다시 로그인해주세요.')
        localStorage.clear()
        router.push('/login')
      } catch (err) {
        console.error('비밀번호 변경 실패:', err)
        alert('비밀번호 변경에 실패했습니다.')
      }
    }

    return {
      currentPassword,
      newPassword,
      confirmPassword,
      handleChangePassword
    }
  }
}
</script>
