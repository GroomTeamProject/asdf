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
    <div class="bg-white rounded-lg p-6 w-full max-w-md mx-4">
      <h3 class="text-lg font-semibold text-gray-800 mb-4">배달 정보 수정</h3>
      
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">배달 주소</label>
          <input
            v-model="tempDeliveryAddress"
            type="text"
            placeholder="배달받을 주소를 입력해주세요"
            class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">연락처</label>
          <input
            v-model="tempPhoneNumber"
            type="tel"
            placeholder="010-0000-0000"
            class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
      </div>
      
      <div class="flex gap-3 mt-6">
        <button
          @click="cancelDeliveryEdit"
          class="flex-1 py-2 px-4 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50"
        >
          취소
        </button>
        <button
          @click="saveDeliveryInfo"
          class="flex-1 py-2 px-4 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          저장
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'DeliveryInfo',
  props: {
    deliveryAddress: {
      type: String,
      required: true,
    },
    phoneNumber: {
      type: String,
      required: true,
    },
    estimatedDeliveryTime: {
      type: String,
      default: '약 30분 후',
    },
  },
  emits: ['update-delivery-info'],
  setup(props, { emit }) {
    const showDeliveryModal = ref(false)
    const tempDeliveryAddress = ref('')
    const tempPhoneNumber = ref('')

    // 배달 정보 수정
    const editDeliveryInfo = () => {
      tempDeliveryAddress.value = props.deliveryAddress
      tempPhoneNumber.value = props.phoneNumber
      showDeliveryModal.value = true
    }

    const saveDeliveryInfo = () => {
      emit('update-delivery-info', {
        deliveryAddress: tempDeliveryAddress.value,
        phoneNumber: tempPhoneNumber.value,
      })
      showDeliveryModal.value = false
    }

    const cancelDeliveryEdit = () => {
      showDeliveryModal.value = false
    }

    return {
      showDeliveryModal,
      tempDeliveryAddress,
      tempPhoneNumber,
      editDeliveryInfo,
      saveDeliveryInfo,
      cancelDeliveryEdit,
    }
  },
}
</script>
