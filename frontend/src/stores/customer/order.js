import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useOrderStore = defineStore('order', () => {
  // 배달 정보
  const deliveryAddress = ref('')
  const phoneNumber = ref('')
  const estimatedDeliveryTime = ref('약 30분 후')

  // 요청사항
  const customMemo = ref('')
  const selectedMemo = ref('')
  const quickMemos = ref([
    '문 앞에 놓아주세요',
    '벨을 누르지 말아주세요',
    '경비실에 맡겨주세요',
    '조심스럽게 배달해주세요',
    '포크/수저 추가로 주세요',
  ])

  // 결제 정보
  const selectedPaymentMethod = ref('카드 결제')
  const isSubmitting = ref(false)

  // 최종 주문 메모 계산
  const finalOrderMemo = computed(() => {
    return customMemo.value || selectedMemo.value
  })

  // 배달 정보 업데이트
  const updateDeliveryInfo = (info) => {
    deliveryAddress.value = info.deliveryAddress
    phoneNumber.value = info.phoneNumber
  }

  // 빠른 메모 선택
  const selectQuickMemo = (memo) => {
    if (selectedMemo.value === memo) {
      selectedMemo.value = ''
      customMemo.value = ''
    } else {
      selectedMemo.value = memo
      customMemo.value = memo
    }
  }

  // 결제수단 변경
  const changePaymentMethod = (method) => {
    selectedPaymentMethod.value = method
  }

  // 주문 제출 상태 설정
  const setSubmitting = (submitting) => {
    isSubmitting.value = submitting
  }

  // 초기 배달 정보 설정
  const initializeDeliveryInfo = (address, phone) => {
    deliveryAddress.value = address
    phoneNumber.value = phone
  }

  // 상태 초기화
  const resetOrderState = () => {
    deliveryAddress.value = ''
    phoneNumber.value = ''
    customMemo.value = ''
    selectedMemo.value = ''
    selectedPaymentMethod.value = '카드 결제'
    isSubmitting.value = false
  }

  return {
    // 상태
    deliveryAddress,
    phoneNumber,
    estimatedDeliveryTime,
    customMemo,
    selectedMemo,
    quickMemos,
    selectedPaymentMethod,
    isSubmitting,
    
    // getters
    finalOrderMemo,
    
    // actions
    updateDeliveryInfo,
    selectQuickMemo,
    changePaymentMethod,
    setSubmitting,
    initializeDeliveryInfo,
    resetOrderState,
  }
}, {
  persist: {
    key: 'order',
    storage: localStorage,
    paths: ['deliveryAddress', 'phoneNumber', 'customMemo', 'selectedMemo', 'selectedPaymentMethod']
  }
})
