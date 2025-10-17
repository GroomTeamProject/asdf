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
        <h1 class="text-xl font-semibold text-gray-900">배송지 관리</h1>
      </div>

      <!-- 배송지 목록 -->
      <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-900">등록된 배송지</h2>
          <button
            @click="openAddAddressModal"
            class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium"
          >
            + 배송지 추가
          </button>
        </div>

        <div v-if="loading" class="flex justify-center items-center py-8">
          <div class="animate-spin rounded-full h-6 w-6 border-b-2 border-blue-600"></div>
          <span class="ml-2 text-gray-600">배송지를 불러오는 중...</span>
        </div>

        <div v-else-if="userAddresses.length === 0" class="text-center py-12">
          <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
          </svg>
          <p class="text-gray-500 mb-2">등록된 배송지가 없습니다.</p>
          <p class="text-sm text-gray-400">새로운 배송지를 추가해보세요.</p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="address in userAddresses"
            :key="address.id"
            class="p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-2 mb-2">
                  <h3 class="font-medium text-gray-900">{{ address.addressName }}</h3>
                  <span
                    v-if="address.isDefault"
                    class="px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full"
                  >
                    기본
                  </span>
                </div>
                <p class="text-sm text-gray-600 mb-1">{{ address.address }}</p>
                <p class="text-sm text-gray-600">{{ address.detailAddress }}</p>
                <p class="text-xs text-gray-500 mt-1">우편번호: {{ address.zipcode }}</p>
              </div>
              <div class="flex gap-2 ml-4">
                <button
                  @click="setDefaultAddress(address.id)"
                  :disabled="address.isDefault"
                  class="px-3 py-1 text-xs bg-gray-100 text-gray-700 rounded hover:bg-gray-200 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                  {{ address.isDefault ? '기본' : '기본 설정' }}
                </button>
                <button
                  @click="editAddress(address)"
                  class="px-3 py-1 text-xs bg-blue-100 text-blue-700 rounded hover:bg-blue-200 transition-colors"
                >
                  수정
                </button>
                <button
                  @click="deleteAddress(address.id)"
                  class="px-3 py-1 text-xs bg-red-100 text-red-700 rounded hover:bg-red-200 transition-colors"
                >
                  삭제
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 배송지 추가/수정 모달 -->
    <div
      v-if="showAddressModal"
      class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4"
    >
      <div class="bg-white rounded-lg shadow-xl w-full max-w-md">
        <div class="p-6">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-semibold text-gray-900">
              {{ editingAddress ? '배송지 수정' : '배송지 추가' }}
            </h3>
            <button
              @click="closeAddressModal"
              class="text-gray-400 hover:text-gray-600"
            >
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
              </svg>
            </button>
          </div>

          <form @submit.prevent="saveAddress" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">배송지명</label>
              <input
                v-model="addressForm.addressName"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="예: 집, 회사"
                required
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">주소</label>
              <div class="flex gap-2">
                <input
                  v-model="addressForm.address"
                  type="text"
                  class="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50"
                  placeholder="주소를 검색해주세요."
                  required
                  readonly
                />
                <button
                  type="button"
                  @click="searchZipcode"
                  class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm font-medium"
                >
                  주소 검색
                </button>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">상세주소</label>
              <input
                v-model="addressForm.detailAddress"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="상세주소를 입력하세요 (예: 101호, 2층)"
                required
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">우편번호</label>
              <input
                v-model="addressForm.zipcode"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 bg-gray-50"
                placeholder="주소 검색 시 자동으로 입력됩니다"
                required
                readonly
              />
            </div>

            <div class="flex items-center">
              <input
                v-model="addressForm.isDefault"
                type="checkbox"
                id="isDefault"
                class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
              />
              <label for="isDefault" class="ml-2 text-sm text-gray-700">
                기본 배송지로 설정
              </label>
            </div>

            <div class="flex gap-3 pt-4">
              <button
                type="button"
                @click="closeAddressModal"
                class="flex-1 px-4 py-2 text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 transition-colors"
              >
                취소
              </button>
              <button
                type="submit"
                :disabled="isSaving"
                class="flex-1 px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:opacity-50 transition-colors"
              >
                {{ isSaving ? '저장 중...' : '저장' }}
              </button>
            </div>
          </form>
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
  name: 'AddressManagement',
  setup() {
    const router = useRouter()
    const userAddresses = ref([])
    const loading = ref(true)
    const showAddressModal = ref(false)
    const editingAddress = ref(null)
    const isSaving = ref(false)
    
    const addressForm = ref({
      addressName: '',
      zipcode: '',
      address: '',
      detailAddress: '',
      isDefault: false
    })

    const goBack = () => {
      router.push('/customer/mypage')
    }

    const fetchUserAddresses = async () => {
      try {
        loading.value = true
        const userId = localStorage.getItem('userId')
        
        if (userId) {
          const addresses = await userApi.getUserAddresses(userId)
          userAddresses.value = addresses || []
        }
      } catch (error) {
        console.error('배송지 목록 조회 실패:', error)
        userAddresses.value = []
      } finally {
        loading.value = false
      }
    }

    const openAddAddressModal = () => {
      editingAddress.value = null
      addressForm.value = {
        addressName: '',
        zipcode: '',
        address: '',
        detailAddress: '',
        isDefault: false
      }
      showAddressModal.value = true
    }

    const editAddress = (address) => {
      editingAddress.value = address
      addressForm.value = {
        addressName: address.addressName,
        zipcode: address.zipcode,
        address: address.address,
        detailAddress: address.detailAddress,
        isDefault: address.isDefault
      }
      showAddressModal.value = true
    }

    const closeAddressModal = () => {
      showAddressModal.value = false
      editingAddress.value = null
      addressForm.value = {
        addressName: '',
        zipcode: '',
        address: '',
        detailAddress: '',
        isDefault: false
      }
    }

    const saveAddress = async () => {
      // 폼 유효성 검사
      if (!addressForm.value.addressName.trim()) {
        alert('배송지명을 입력해주세요.')
        return
      }
      
      if (!addressForm.value.zipcode.trim()) {
        alert('우편번호를 검색해주세요.')
        return
      }
      
      if (!addressForm.value.address.trim()) {
        alert('주소를 입력해주세요.')
        return
      }
      
      if (!addressForm.value.detailAddress.trim()) {
        alert('상세주소를 입력해주세요.')
        return
      }

      try {
        isSaving.value = true
        const userId = localStorage.getItem('userId')
        
        if (editingAddress.value) {
          // 배송지 수정
          await userApi.updateUserAddress(userId, editingAddress.value.id, addressForm.value)
          alert('배송지가 성공적으로 수정되었습니다.')
        } else {
          // 배송지 추가
          await userApi.createUserAddress(userId, addressForm.value)
          alert('배송지가 성공적으로 추가되었습니다.')
        }
        
        closeAddressModal()
        await fetchUserAddresses()
      } catch (error) {
        console.error('배송지 저장 실패:', error)
        alert(error.response?.data?.message || '배송지 저장에 실패했습니다.')
      } finally {
        isSaving.value = false
      }
    }

    const setDefaultAddress = async (addressId) => {
      try {
        const userId = localStorage.getItem('userId')
        await userApi.setDefaultAddress(userId, addressId)
        alert('기본 배송지가 설정되었습니다.')
        await fetchUserAddresses()
      } catch (error) {
        console.error('기본 배송지 설정 실패:', error)
        alert(error.response?.data?.message || '기본 배송지 설정에 실패했습니다.')
      }
    }

    const deleteAddress = async (addressId) => {
      if (confirm('정말로 이 배송지를 삭제하시겠습니까?')) {
        try {
          const userId = localStorage.getItem('userId')
          await userApi.deleteUserAddress(userId, addressId)
          alert('배송지가 성공적으로 삭제되었습니다.')
          await fetchUserAddresses()
        } catch (error) {
          console.error('배송지 삭제 실패:', error)
          alert(error.response?.data?.message || '배송지 삭제에 실패했습니다.')
        }
      }
    }

    const searchZipcode = () => {
      new daum.Postcode({
        oncomplete: function(data) {
          // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드
          let addr = '' // 주소 변수
          let extraAddr = '' // 참고항목 변수

          // 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
          if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
            addr = data.roadAddress
          } else { // 사용자가 지번 주소를 선택했을 경우(J)
            addr = data.jibunAddress
          }

          // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
          if(data.userSelectedType === 'R'){
            // 법정동명이 있을 경우 추가한다. (법정리는 제외)
            // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
            if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
              extraAddr += data.bname
            }
            // 건물명이 있고, 공동주택일 경우 추가한다.
            if(data.buildingName !== '' && data.apartment === 'Y'){
              extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName)
            }
            // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
            if(extraAddr !== ''){
              extraAddr = ' (' + extraAddr + ')'
            }
          }

          // 우편번호와 주소 정보를 해당 필드에 넣는다.
          addressForm.value.zipcode = data.zonecode
          addressForm.value.address = addr
          
          // 참고항목 문자열이 있을 경우 해당 필드에 넣는다.
          if(extraAddr !== ''){
            addressForm.value.address += extraAddr
          }
        }
      }).open()
    }

    onMounted(() => {
      fetchUserAddresses()
    })

    return {
      userAddresses,
      loading,
      showAddressModal,
      editingAddress,
      isSaving,
      addressForm,
      goBack,
      openAddAddressModal,
      editAddress,
      closeAddressModal,
      saveAddress,
      setDefaultAddress,
      deleteAddress,
      searchZipcode,
    }
  },
}
</script>
