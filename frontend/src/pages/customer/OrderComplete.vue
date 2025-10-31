<template>
  <!-- 헤더 배너 -->
  <HeaderBanner 
    title="주문 완료"
  />

  <!-- 페이지 컨테이너 -->
  <CustomerContainer max-width="2xl" padding="4" custom-class="space-y-6">
      <!-- 주문 완료 메시지 -->
      <div class="text-center py-8">
        <div class="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <span class="text-4xl">✅</span>
        </div>
        <h1 class="text-2xl font-bold text-gray-800 mb-2">주문이 완료되었습니다!</h1>
        <p class="text-gray-600">주문해주셔서 감사합니다.</p>
      </div>

      <!-- 주문 정보 카드 -->
      <div class="bg-white rounded-lg border border-gray-200 shadow-sm p-6">
        <h2 class="text-lg font-semibold text-gray-800 mb-4">주문 정보</h2>
        
        <div class="space-y-4">
          <!-- 주문 번호 -->
          <div class="flex justify-between items-center py-2">
            <span class="text-gray-600">주문 번호</span>
            <span class="font-medium text-gray-800">{{ orderNumber }}</span>
          </div>

          <!-- 주문 시간 -->
          <div class="flex justify-between items-center py-2">
            <span class="text-gray-600">주문 시간</span>
            <span class="font-medium text-gray-800">{{ orderTime }}</span>
          </div>

          <!-- 가게명 -->
          <div class="flex justify-between items-center py-2">
            <span class="text-gray-600">가게명</span>
            <span class="font-medium text-gray-800">{{ storeName }}</span>
          </div>

          <!-- 총 주문 금액 -->
          <div class="flex justify-between items-center py-2">
            <span class="text-gray-600">총 주문 금액</span>
            <span class="font-medium text-gray-800">{{ totalAmount.toLocaleString() }}원</span>
          </div>
        </div>
      </div>

      <!-- 배달 정보 카드 -->
      <div class="bg-white rounded-lg border border-gray-200 shadow-sm p-6">
        <h2 class="text-lg font-semibold text-gray-800 mb-4">배달 정보</h2>
        
        <div class="space-y-4">
          <!-- 배달 주소 -->
          <div class="py-2">
            <span class="text-gray-600 block mb-1">배달 주소</span>
            <span class="font-medium text-gray-800">{{ deliveryAddress }}</span>
          </div>

          <!-- 연락처 -->
          <div class="py-2">
            <span class="text-gray-600 block mb-1">연락처</span>
            <span class="font-medium text-gray-800">{{ phoneNumber }}</span>
          </div>

          <!-- 예상 배달 시간 -->
          <div class="py-2">
            <span class="text-gray-600 block mb-1">예상 배달 시간</span>
            <span class="font-medium text-gray-800">{{ estimatedDeliveryTime }}</span>
          </div>
        </div>
      </div>

      <!-- 주문 상태 카드 -->
      <div class="bg-blue-50 border border-blue-200 rounded-lg p-6">
        <div class="flex items-center gap-3 mb-3">
          <div class="w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center">
            <span class="text-white text-sm">⏰</span>
          </div>
          <h3 class="font-semibold text-blue-800">주문 상태</h3>
        </div>
        
        <div class="space-y-3">
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 bg-blue-500 rounded-full"></div>
            <span class="text-sm text-blue-700">주문 접수 대기</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 bg-gray-300 rounded-full"></div>
            <span class="text-sm text-gray-500">주문 접수됨</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 bg-gray-300 rounded-full"></div>
            <span class="text-sm text-gray-500">조리 중</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 bg-gray-300 rounded-full"></div>
            <span class="text-sm text-gray-500">배달 출발</span>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-3 h-3 bg-gray-300 rounded-full"></div>
            <span class="text-sm text-gray-500">배달 완료</span>
          </div>
        </div>
      </div>

      <!-- 안내 메시지 -->
      <div class="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
        <div class="flex items-start gap-3">
          <span class="text-yellow-600 text-lg">💡</span>
          <div class="text-sm text-yellow-800">
            <p class="font-medium mb-1">주문 확인 및 문의</p>
            <p>주문 상태나 배달 관련 문의사항이 있으시면 가게에 직접 연락해주세요.</p>
          </div>
        </div>
      </div>

      <!-- 액션 버튼들 -->
      <div class="space-y-3">
        <button
          @click="goToStores"
          class="w-full h-12 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 transition-colors mb-3"
        >
          다른 가게 둘러보기
        </button>
        
        <button
          @click="goToOrderHistory"
          class="w-full h-12 bg-gray-100 text-gray-700 font-semibold rounded-lg hover:bg-gray-200 transition-colors"
        >
          주문 내역 확인
        </button>
      </div>
  </CustomerContainer>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/customer/cart'
import { orderService } from '@/services/customer/orderService'
import CustomerContainer from '@/components/customer/CustomerContainer.vue'
import HeaderBanner from '@/components/common/HeaderBanner.vue'

export default {
  name: 'OrderComplete',
  components: {
    CustomerContainer,
    HeaderBanner,
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const cartStore = useCartStore()

    // 주문 정보 (실제로는 API에서 받아와야 함)
    const orderNumber = ref('')
    const orderTime = ref('')
    const storeName = ref('')
    const totalAmount = ref(0)
    const deliveryAddress = ref('')
    const phoneNumber = ref('')
    const estimatedDeliveryTime = ref('')

    // 페이지 이동
    const goToStores = () => {
      router.push('/customer/stores')
    }

    const goToOrderHistory = () => {
      router.push('/customer/order-history')
    }

    // 페이지 로드 시 주문 정보 설정
    onMounted(async () => {
      try {
        // route query에서 주문 정보 가져오기
        if (route.query.orderNumber) {
          // 서버로부터 받은 실제 데이터 사용
          orderNumber.value = route.query.orderNumber
          totalAmount.value = parseInt(route.query.totalAmount) || 0
          storeName.value = route.query.storeName
          deliveryAddress.value = route.query.deliveryAddress
          phoneNumber.value = route.query.phoneNumber
          
          // 현재 시간으로 주문 시간 설정
          orderTime.value = new Date().toLocaleString('ko-KR')
          estimatedDeliveryTime.value = '약 30분 후'
        } else {
          // 주문 번호가 없으면 에러 처리
          console.error('주문 번호가 없습니다.')
          router.push('/customer/stores')
        }
      } catch (error) {
        console.error('주문 완료 정보 처리 실패:', error)
        router.push('/customer/stores')
      }
    })

    return {
      orderNumber,
      orderTime,
      storeName,
      totalAmount,
      deliveryAddress,
      phoneNumber,
      estimatedDeliveryTime,
      goToStores,
      goToOrderHistory,
    }
  },
}
</script>

<style scoped>
/* 추가 스타일이 필요한 경우 여기에 작성 */
</style>
