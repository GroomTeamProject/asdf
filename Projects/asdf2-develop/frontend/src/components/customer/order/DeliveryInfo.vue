<template>
  <div class="bg-white border-b border-gray-200 p-4">
    <div class="flex items-center justify-between">
      <div class="flex-1">
        <div class="flex items-center gap-2 mb-2">
          <span class="text-lg">📍</span>
          <span class="font-medium text-gray-800">배달 주소</span>
        </div>
        <p class="text-gray-600 text-sm">{{ deliveryAddress || '배달받을 주소를 입력해주세요' }}</p>
      </div>
      <button
        @click="editDeliveryInfo"
        class="px-4 py-2 text-blue-600 text-sm font-medium hover:bg-blue-50 rounded-lg transition-colors"
      >
        수정
      </button>
    </div>
    
    <div class="mt-3 pt-3 border-t border-gray-100">
      <div class="flex items-center gap-2">
        <span class="text-lg">📞</span>
        <span class="text-gray-600 text-sm">{{ phoneNumber || '연락처를 입력해주세요' }}</span>
      </div>
    </div>

    <!-- 예상 배달 시간 -->
    <div class="mt-3 pt-3 border-t border-gray-100">
      <div class="flex items-center gap-2">
        <span class="text-lg">⏰</span>
        <span class="text-gray-600 text-sm">예상 배달 시간: {{ estimatedDeliveryTime }}</span>
      </div>
    </div>
  </div>

  <!-- 배달 정보 수정 모달 -->
  <div v-if="showDeliveryModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
    <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4 max-h-[80vh] overflow-y-auto">
      <h3 class="text-lg font-semibold text-gray-800 mb-4">배달 주소 선택</h3>
      
      <!-- 등록된 배송지 목록 -->
      <div v-if="userAddresses.length > 0" class="space-y-3 mb-4">
        <h4 class="text-sm font-medium text-gray-700">등록된 배송지</h4>
        <div 
          v-for="address in userAddresses" 
          :key="address.id"
          @click="selectAddress(address)"
          :class="[
            'p-3 border rounded-lg cursor-pointer transition-colors',
            selectedAddressId === address.id 
              ? 'border-blue-500 bg-blue-50' 
              : 'border-gray-200 hover:border-gray-300'
          ]"
        >
          <div class="flex items-center justify-between">
            <div class="flex-1">
              <div class="flex items-center gap-2">
                <span class="font-medium text-gray-900">{{ address.addressName }}</span>
                <span v-if="address.isDefault" class="px-2 py-1 text-xs bg-blue-100 text-blue-700 rounded-full">
                  기본
                </span>
              </div>
              <p class="text-sm text-gray-600 mt-1">
                {{ address.address }} {{ address.detailAddress }}
              </p>
              <p class="text-xs text-gray-500">{{ address.zipcode }}</p>
            </div>
            <div v-if="selectedAddressId === address.id" class="text-blue-600">
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path>
              </svg>
            </div>
          </div>
        </div>
      </div>

      <!-- 배송지가 없는 경우 -->
      <div v-else class="text-center py-8 mb-4">
        <div class="text-gray-400 mb-2">
          <svg class="w-12 h-12 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
          </svg>
        </div>
        <p class="text-gray-500 text-sm">등록된 배송지가 없습니다</p>
        <button
          @click="goToAddressManagement"
          class="mt-2 text-blue-600 text-sm hover:underline"
        >
          배송지 추가하기
        </button>
      </div>

      <!-- 직접 입력 옵션 -->
      <div class="border-t pt-4">
        <button
          @click="showManualInput = !showManualInput"
          class="w-full text-left text-sm text-gray-600 hover:text-gray-800 flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
          </svg>
          직접 입력하기
        </button>
        
        <div v-if="showManualInput" class="mt-4 space-y-3">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">배달 주소</label>
            <input
              v-model="tempDeliveryAddress"
              type="text"
              placeholder="배달받을 주소를 입력해주세요"
              class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">상세 주소</label>
            <input
              v-model="tempDeliveryDetailAddress"
              type="text"
              placeholder="동/호수, 건물명 등 (선택사항)"
              class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
        </div>
      </div>

      <!-- 연락처 -->
      <div class="mt-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">연락처</label>
        <input
          v-model="tempPhoneNumber"
          type="tel"
          placeholder="010-0000-0000"
          class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        />
      </div>
      
      <div class="flex gap-3 mt-6">
        <button
          @click="cancelDeliveryEdit"
          class="flex-1 py-2 px-4 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
        >
          취소
        </button>
        <button
          @click="saveDeliveryInfo"
          class="flex-1 py-2 px-4 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          저장
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/customer/order'
import { storeToRefs } from 'pinia'
import { userApi } from '@/api/customer/userApi'

export default {
  name: 'DeliveryInfo',
  setup() {
    const router = useRouter()
    const orderStore = useOrderStore()
    
    // store에서 필요한 상태들만 추출
    const {
      deliveryAddress,
      deliveryDetailAddress,
      phoneNumber,
      estimatedDeliveryTime,
    } = storeToRefs(orderStore)

    const showDeliveryModal = ref(false)
    const tempDeliveryAddress = ref('')
    const tempDeliveryDetailAddress = ref('')
    const tempPhoneNumber = ref('')
    const userAddresses = ref([])
    const selectedAddressId = ref(null)
    const showManualInput = ref(false)
    const loading = ref(false)

    // 등록된 배송지 목록 불러오기
    const fetchUserAddresses = async () => {
      try {
        loading.value = true
        const userId = localStorage.getItem('userId')
        if (userId) {
          const addresses = await userApi.getUserAddresses(userId)
          userAddresses.value = addresses || []
          
          // 현재 선택된 배송지가 있는지 확인
          const currentSelectedAddress = addresses?.find(addr => 
            addr.address === tempDeliveryAddress.value && 
            addr.detailAddress === tempDeliveryDetailAddress.value
          )
          
          if (currentSelectedAddress) {
            // 현재 선택된 배송지가 있으면 그것을 선택
            selectAddress(currentSelectedAddress)
          } else {
            // 현재 선택된 배송지가 없으면 기본 배송지 선택
            const defaultAddress = addresses?.find(addr => addr.isDefault)
            if (defaultAddress) {
              selectAddress(defaultAddress)
            }
          }
        }
      } catch (error) {
        console.error('배송지 목록 조회 실패:', error)
        userAddresses.value = []
      } finally {
        loading.value = false
      }
    }

    // 배송지 선택
    const selectAddress = (address) => {
      selectedAddressId.value = address.id
      tempDeliveryAddress.value = address.address
      tempDeliveryDetailAddress.value = address.detailAddress
      showManualInput.value = false
    }

    // 배송지 관리 페이지로 이동
    const goToAddressManagement = () => {
      showDeliveryModal.value = false
      router.push('/customer/address-management')
    }

    // 배달 정보 수정
    const editDeliveryInfo = () => {
      tempDeliveryAddress.value = deliveryAddress.value
      tempDeliveryDetailAddress.value = deliveryDetailAddress.value
      tempPhoneNumber.value = phoneNumber.value
      selectedAddressId.value = null
      showManualInput.value = false
      showDeliveryModal.value = true
      fetchUserAddresses()
    }

    const saveDeliveryInfo = () => {
      orderStore.updateDeliveryInfo({
        deliveryAddress: tempDeliveryAddress.value,
        deliveryDetailAddress: tempDeliveryDetailAddress.value,
        phoneNumber: tempPhoneNumber.value,
      })
      showDeliveryModal.value = false
    }

    const cancelDeliveryEdit = () => {
      showDeliveryModal.value = false
    }

    return {
      deliveryAddress,
      deliveryDetailAddress,
      phoneNumber,
      estimatedDeliveryTime,
      showDeliveryModal,
      tempDeliveryAddress,
      tempDeliveryDetailAddress,
      tempPhoneNumber,
      userAddresses,
      selectedAddressId,
      showManualInput,
      loading,
      editDeliveryInfo,
      saveDeliveryInfo,
      cancelDeliveryEdit,
      selectAddress,
      goToAddressManagement,
    }
  },
}
</script>
