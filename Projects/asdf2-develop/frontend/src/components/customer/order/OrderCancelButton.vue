<template>
  <button
    @click="handleCancel"
    :disabled="!canCancel"
    :class="[
      'w-full h-14 text-lg font-semibold rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2',
      canCancel
        ? 'bg-red-100 text-red-700 hover:bg-red-200 focus:ring-red-500 border border-red-300'
        : 'bg-gray-100 text-gray-400 cursor-not-allowed border border-gray-200'
    ]"
  >
    주문 취소
  </button>

  <!-- 취소 확인 모달 -->
  <div
    v-if="showCancelModal"
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    @click="closeModal"
  >
    <div
      class="bg-white rounded-lg p-6 max-w-md w-full mx-4"
      @click.stop
    >
      <h3 class="text-lg font-semibold text-gray-800 mb-4">주문 취소</h3>
      
      <p class="text-gray-600 mb-4">
        정말로 이 주문을 취소하시겠습니까?
      </p>

      <!-- 취소 사유 선택 -->
      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-2">
          취소 사유
        </label>
        <select
          v-model="selectedCancelReason"
          class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500"
        >
          <option value="">취소 사유를 선택해주세요</option>
          <option value="단순 변심">단순 변심</option>
          <option value="배달 지연">배달 지연</option>
          <option value="메뉴 변경">메뉴 변경</option>
          <option value="가게 사정">가게 사정</option>
          <option value="기타">기타</option>
        </select>
      </div>

      <!-- 기타 사유 입력 -->
      <div v-if="selectedCancelReason === '기타'" class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-2">
          상세 사유
        </label>
        <textarea
          v-model="customCancelReason"
          placeholder="취소 사유를 입력해주세요"
          class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-red-500"
          rows="3"
        ></textarea>
      </div>

      <!-- 버튼들 -->
      <div class="flex gap-3">
        <button
          @click="closeModal"
          class="flex-1 px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
        >
          취소
        </button>
        <button
          @click="confirmCancel"
          :disabled="!selectedCancelReason || isCancelling"
          :class="[
            'flex-1 px-4 py-2 rounded-lg font-medium transition-colors',
            selectedCancelReason && !isCancelling
              ? 'bg-red-600 text-white hover:bg-red-700'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          ]"
        >
          {{ isCancelling ? '취소 중...' : '확인' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { customerApi } from '@/api/customer/customerApi'

export default {
  name: 'OrderCancelButton',
  props: {
    orderId: {
      type: [String, Number],
      required: true,
    },
    orderStatus: {
      type: String,
      required: true,
    },
  },
  emits: ['order-cancelled'],
  setup(props, { emit }) {
    const router = useRouter()
    const showCancelModal = ref(false)
    const selectedCancelReason = ref('')
    const customCancelReason = ref('')
    const isCancelling = ref(false)

    // 취소 가능한 상태인지 확인
    const canCancel = computed(() => {
      const cancellableStatuses = ['PENDING', 'ACCEPTED', 'COOKING']
      return cancellableStatuses.includes(props.orderStatus)
    })

    // 취소 모달 열기
    const handleCancel = () => {
      if (!canCancel.value) return
      showCancelModal.value = true
      selectedCancelReason.value = ''
      customCancelReason.value = ''
    }

    // 취소 모달 닫기
    const closeModal = () => {
      showCancelModal.value = false
      selectedCancelReason.value = ''
      customCancelReason.value = ''
    }

    // 취소 확인
    const confirmCancel = async () => {
      if (!selectedCancelReason.value || isCancelling.value) return

      isCancelling.value = true

      try {
        const cancelData = {
          cancelReason: selectedCancelReason.value === '기타' 
            ? customCancelReason.value 
            : selectedCancelReason.value
        }

        await customerApi.cancelOrder(props.orderId, cancelData)
        
        alert('주문이 성공적으로 취소되었습니다.')
        
        // 부모 컴포넌트에 취소 완료 알림
        emit('order-cancelled')
        
        // 주문 내역 페이지로 이동
        router.push('/customer/order-history')
        
      } catch (error) {
        console.error('주문 취소 실패:', error)
        
        const errorMessage = error.response?.data?.message || 
                           error.message || 
                           '주문 취소 중 오류가 발생했습니다.'
        alert(errorMessage)
      } finally {
        isCancelling.value = false
        closeModal()
      }
    }

    return {
      showCancelModal,
      selectedCancelReason,
      customCancelReason,
      isCancelling,
      canCancel,
      handleCancel,
      closeModal,
      confirmCancel,
    }
  },
}
</script>

<style scoped>
/* 추가 스타일이 필요한 경우 여기에 작성 */
</style>
