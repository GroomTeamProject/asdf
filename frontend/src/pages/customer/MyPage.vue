<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-2xl mx-auto p-4 space-y-6">
      <!-- 프로필 섹션 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div v-if="loading" class="flex justify-center items-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          <span class="ml-2 text-gray-600">사용자 정보를 불러오는 중...</span>
        </div>
        
        <div v-else>
          <div class="flex items-center gap-4 mb-6">
            <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center">
              <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
            </div>
            <div>
              <h2 class="text-xl font-semibold text-gray-900">{{ userProfile?.name || '사용자' }}님, 안녕하세요!</h2>
            </div>
          </div>
          
          <div class="grid grid-cols-2 gap-4 text-sm">
            <div>
              <p class="text-gray-600">이메일</p>
              <p class="font-medium text-gray-900">{{ userProfile?.email || 'user@example.com' }}</p>
            </div>
            <div>
              <p class="text-gray-600">전화번호</p>
              <p class="font-medium text-gray-900">{{ userProfile?.phone || '010-1234-5678' }}</p>
            </div>
          </div>
        </div>
      </div>


      <!-- 바로가기 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">바로가기</h3>
        <div class="space-y-3">
          <button
            @click="goToEditProfile"
            class="w-full flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-yellow-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                </svg>
              </div>
              <div class="text-left">
                <div class="font-medium text-gray-900">회원정보 수정</div>
                <div class="text-sm text-gray-600">이름, 이메일, 전화번호 수정</div>
              </div>
            </div>
            <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>

          <button
            @click="goToChangePassword"
            class="w-full flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                        d="M12 11c0-1.105-.895-2-2-2s-2 .895-2 2 .895 2 2 2 2-.895 2-2zm0 0v1m6-5v4a2 2 0 01-2 2H6a2 2 0 01-2-2V7a2 2 0 012-2h8l4 4z" />
                </svg>
              </div>
              <div class="text-left">
                <div class="font-medium text-gray-900">비밀번호 변경</div>
                <div class="text-sm text-gray-600">현재 비밀번호를 확인 후 변경</div>
              </div>
            </div>
            <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>


          <button


            @click="goToAddressManagement"
            class="w-full flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-indigo-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
                </svg>
              </div>
              <div class="text-left">
                <div class="font-medium text-gray-900">배송지 관리</div>
                <div class="text-sm text-gray-600">배송지 목록 보기 및 관리</div>
              </div>
            </div>
            <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>

          <button
            @click="goToOrderHistory"
            class="w-full flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
                </svg>
              </div>
              <div class="text-left">
                <div class="font-medium text-gray-900">주문 내역</div>
                <div class="text-sm text-gray-600">이전 주문을 확인해보세요</div>
              </div>
            </div>
            <svg class="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>
        </div>
      </div>

      <!-- 설정 및 기타 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 class="text-lg font-semibold text-gray-900 mb-4">설정</h3>
        <div class="space-y-3">
          
          <button
            @click="handleLogout"
            class="w-full flex items-center justify-between p-4 bg-red-50 rounded-lg hover:bg-red-100 transition-colors"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"></path>
                </svg>
              </div>
              <div class="text-left">
                <div class="font-medium text-red-700">로그아웃</div>
                <div class="text-sm text-red-600">계정에서 로그아웃합니다</div>
              </div>
            </div>
            <svg class="w-5 h-5 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>

          <button
            @click="handleDeleteAccount"
            class="w-full flex items-center justify-between p-4 bg-red-50 rounded-lg hover:bg-red-100 transition-colors"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-red-200 rounded-full flex items-center justify-center">
                <svg class="w-5 h-5 text-red-700" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M6 18L18 6M6 6l12 12"></path>
                </svg>
              </div>
              <div class="text-left">
                <div class="font-medium text-red-700">계정 탈퇴</div>
                <div class="text-sm text-red-600">계정을 영구적으로 삭제합니다</div>
              </div>
            </div>
            <svg class="w-5 h-5 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>


  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { customerService } from '@/services/customer/customerService'

export default {
  name: 'MyPage',
  setup() {
    const router = useRouter()
    const userProfile = ref(null)
    const loading = ref(true)
    

    const goToOrderHistory = () => {
      router.push('/customer/order-history')
    }

    const goToCart = () => {
      router.push('/customer/cart')
    }

    const goToStores = () => {
      router.push('/customer/stores')
    }

    const goToEditProfile = () => {
      router.push('/customer/edit-profile')
    }

    const goToAddressManagement = () => {
      router.push('/customer/address-management')
    }


    const goToChangePassword = () => {
      router.push('/customer/change-password')
    }


    const handleLogout = async () => {
      if (confirm('정말로 로그아웃하시겠습니까?')) {
        try {
          await customerService.logout()
          router.push('/login')
        } catch (error) {
          console.error('로그아웃 실패:', error)
          // 에러가 발생해도 로컬 스토리지는 정리하고 로그인 페이지로 이동
          localStorage.removeItem('jwt')
          localStorage.removeItem('userType')
          localStorage.removeItem('userId')
          localStorage.removeItem('userEmail')
          localStorage.removeItem('userName')
          router.push('/login')
        }
      }
    }

    const handleDeleteAccount = async () => {
      if (confirm('정말로 계정을 탈퇴하시겠습니까? 삭제된 계정은 복구할 수 없습니다.')) {
        try {
          await customerService.deleteAccount()
          alert('계정이 삭제되었습니다.')
          localStorage.clear()
          router.push('/')
        } catch (error) {
          console.error('계정 탈퇴 실패:', error)
          alert('계정 탈퇴에 실패했습니다.')
        }
      }
    }

    const fetchUserData = async () => {
      try {
        loading.value = true
        const userId = localStorage.getItem('userId')
        
        if (userId) {
          const result = await customerService.getUserProfile(userId)
          userProfile.value = result.data
        }
      } catch (error) {
        console.error('사용자 데이터 조회 실패:', error)
        throw error
      } finally {
        loading.value = false
      }
    }



    onMounted(() => {
      fetchUserData()
    })

    return {
      userProfile,
      loading,
      goToOrderHistory,
      goToCart,
      goToStores,
      goToChangePassword,
      goToEditProfile,
      goToAddressManagement,
      handleLogout,
      handleDeleteAccount
    }
  },
}
</script>
