<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-2xl mx-auto p-4">
      <!-- 헤더 -->
      <div class="flex items-center gap-4 mb-6">
        <button
          @click="goBack"
          class="p-2 text-gray-600 hover:text-gray-900 hover:bg-gray-100 rounded-full transition-colors"
        >
          <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
        </button>
        <h1 class="text-xl font-semibold text-gray-900">회원정보 수정</h1>
      </div>

      <!-- 회원정보 수정 폼 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <form @submit.prevent="updateProfile" class="space-y-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">이메일</label>
            <input
              v-model="editForm.email"
              type="email"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg bg-gray-100 text-gray-500 cursor-not-allowed"
              placeholder="이메일"
              disabled
              readonly
            />
            <p class="text-xs text-gray-500 mt-1">이메일은 수정할 수 없습니다.</p>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">이름</label>
            <input
              v-model="editForm.name"
              type="text"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="이름을 입력하세요"
              required
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">전화번호</label>
            <input
              v-model="editForm.phone"
              type="tel"
              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              placeholder="전화번호를 입력하세요"
              required
            />
          </div>

          <div class="flex gap-3 pt-4">
            <button
              type="button"
              @click="goBack"
              class="flex-1 px-6 py-3 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors font-medium"
            >
              취소
            </button>
            <button
              type="submit"
              :disabled="isUpdating"
              class="flex-1 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium"
            >
              {{ isUpdating ? '수정 중...' : '수정 완료' }}
            </button>
          </div>
        </form>
      </div>

      <!-- 추가 정보 (읽기 전용) -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mt-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">추가 정보</h3>
        <div class="space-y-3 text-sm">
          <div class="flex justify-between">
            <span class="text-gray-600">회원 유형</span>
            <span class="font-medium text-gray-900">{{ userProfile?.userType || 'CUSTOMER' }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">가입일</span>
            <span class="font-medium text-gray-900">{{ formatDate(userProfile?.createdAt) }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">이메일 인증</span>
            <span class="font-medium" :class="userProfile?.emailVerified ? 'text-green-600' : 'text-red-600'">
              {{ userProfile?.emailVerified ? '인증 완료' : '미인증' }}
            </span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-600">전화번호 인증</span>
            <span class="font-medium" :class="userProfile?.phoneVerified ? 'text-green-600' : 'text-red-600'">
              {{ userProfile?.phoneVerified ? '인증 완료' : '미인증' }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { userApi } from '@/api/customer/userApi'

export default {
  name: 'EditProfile',
  setup() {
    const router = useRouter()
    const userProfile = ref(null)
    const isUpdating = ref(false)
    
    const editForm = ref({
      name: '',
      email: '',
      phone: ''
    })

    const goBack = () => {
      router.push('/customer/mypage')
    }

    const updateProfile = async () => {
      try {
        isUpdating.value = true
        const userId = localStorage.getItem('userId')
        
        if (userId) {
          const updatedProfile = await userApi.updateUserProfile(userId, editForm.value)
          userProfile.value = updatedProfile
          alert('회원정보가 성공적으로 수정되었습니다.')
          goBack()
        }
      } catch (error) {
        console.error('회원정보 수정 실패:', error)
        alert('회원정보 수정에 실패했습니다. 다시 시도해주세요.')
      } finally {
        isUpdating.value = false
      }
    }

    const fetchUserData = async () => {
      try {
        const userId = localStorage.getItem('userId')
        
        if (userId) {
          const profile = await userApi.getUserProfile(userId)
          userProfile.value = profile
          
          // 폼에 현재 데이터 설정
          editForm.value = {
            name: profile.name || '',
            email: profile.email || '',
            phone: profile.phone || ''
          }
        }
      } catch (error) {
        console.error('사용자 데이터 조회 실패:', error)
        alert('사용자 정보를 불러오는데 실패했습니다.')
        goBack()
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    onMounted(() => {
      fetchUserData()
    })

    return {
      userProfile,
      editForm,
      isUpdating,
      goBack,
      updateProfile,
      formatDate,
    }
  },
}
</script>
