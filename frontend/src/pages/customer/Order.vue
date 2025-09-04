<template>
  <div class="max-w-2xl mx-auto bg-gray-50 min-h-screen">
    <!-- 배달 정보 -->
    <DeliveryInfo
      :delivery-address="deliveryAddress"
      :phone-number="phoneNumber"
      :estimated-delivery-time="estimatedDeliveryTime"
      @update-delivery-info="updateDeliveryInfo"
    />

    <!-- 선택한 메뉴 -->
    <OrderSummary
      :grouped-by-store="groupedByStore"
      :total-item-count="totalItemCount"
      @increase-quantity="increaseQuantity"
      @decrease-quantity="decreaseQuantity"
      @remove-item="removeItem"
      @edit-options="editOptions"
    />

    <!-- 주문금액 -->
    <OrderPricing
      :total-amount="totalAmount"
      :delivery-fee="deliveryFee"
      :discount-amount="discountAmount"
      :final-amount="finalAmount"
    />

    <!-- 결제수단 -->
    <PaymentMethod />

    <!-- 요청사항 -->
    <OrderMemo />

    <!-- 결제 버튼 -->
    <PaymentButton
      :can-submit-order="canSubmitOrder"
      :final-amount="finalAmount"
      @payment="submitOrder"
    />
  </div>
</template>

<script>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { cartService } from '@/services/customer/cartService'
import { orderService } from '@/services/customer/orderService'
import { useOrderStore } from '@/stores/customer/order'
import DeliveryInfo from '@/components/customer/order/DeliveryInfo.vue'
import OrderSummary from '@/components/customer/order/OrderSummary.vue'
import OrderPricing from '@/components/customer/order/OrderPricing.vue'
import OrderMemo from '@/components/customer/order/OrderMemo.vue'
import PaymentMethod from '@/components/customer/order/PaymentMethod.vue'
import PaymentButton from '@/components/customer/order/PaymentButton.vue'

export default {
  name: 'Order',
  components: {
    DeliveryInfo,
    OrderSummary,
    OrderPricing,
    OrderMemo,
    PaymentMethod,
    PaymentButton,
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const orderStore = useOrderStore()

    // store에서 필요한 상태들만 추출
    const {
      deliveryAddress,
      phoneNumber,
      estimatedDeliveryTime,
      selectedPaymentMethod,
      isSubmitting,
    } = storeToRefs(orderStore)

    // 장바구니에서 주문 정보 가져오기
    const cartState = computed(() => cartService.getCartState())
    const orderItems = computed(() => cartState.value.items)
    const totalAmount = computed(() => cartState.value.totalPrice)

    // 가게별로 메뉴들을 그룹화
    const groupedByStore = computed(() => {
      return orderService.groupItemsByStore(orderItems.value)
    })

    // 총 아이템 개수
    const totalItemCount = computed(() => {
      return orderService.calculateTotalItemCount(orderItems.value)
    })

    // 배달비, 할인, 최종 금액 계산
    const deliveryFee = computed(() => {
      // TODO: 가게별 배달비 설정
      return totalAmount.value >= 15000 ? 0 : 3000
    })

    const discountAmount = computed(() => {
      // TODO: 할인 정책 적용
      return 0
    })

    const finalAmount = computed(() => {
      return totalAmount.value + deliveryFee.value - discountAmount.value
    })

    // 주문 제출 가능 여부
    const canSubmitOrder = computed(() => {
      return (
        deliveryAddress.value.trim() &&
        phoneNumber.value.trim() &&
        orderItems.value.length > 0 &&
        !isSubmitting.value
      )
    })

    // 수량 조작
    const increaseQuantity = (item) => {
      const result = orderService.updateItemQuantity(item, item.quantity + 1)
      if (!result.success) {
        alert('수량 업데이트에 실패했습니다: ' + result.message)
      }
    }

    const decreaseQuantity = (item) => {
      if (item.quantity > 1) {
        const result = orderService.updateItemQuantity(item, item.quantity - 1)
        if (!result.success) {
          alert('수량 업데이트에 실패했습니다: ' + result.message)
        }
      }
    }

    // 아이템 삭제
    const removeItem = (item) => {
      if (confirm('이 메뉴를 장바구니에서 제거하시겠습니까?')) {
        const result = orderService.removeItem(item)
        if (!result.success) {
          alert('메뉴 제거에 실패했습니다: ' + result.message)
        }
      }
    }

    // 옵션 변경
    const editOptions = (item) => {
      // TODO: 옵션 변경 페이지로 이동
      router.push(`/customer/stores/${item.storeId}/menu/${item.id}`)
    }

    // 배달 정보 업데이트
    const updateDeliveryInfo = (info) => {
      orderStore.updateDeliveryInfo(info)
    }

    // 주문 제출
    const submitOrder = async () => {
      if (!canSubmitOrder.value) return

      const confirmed = confirm(
        `총 ${finalAmount.value.toLocaleString()}원을 주문하시겠습니까?`
      )
      if (!confirmed) return

      orderStore.setSubmitting(true)

      try {
        // 주문 데이터 구성
        const orderData = orderService.buildOrderData(
          orderItems.value,
          totalAmount.value,
          deliveryFee.value,
          discountAmount.value,
          finalAmount.value,
          orderStore.finalOrderMemo,
          deliveryAddress.value,
          phoneNumber.value,
          selectedPaymentMethod.value
        )

        // 주문 제출
        const result = await orderService.submitOrder(orderData)

        if (result.success) {
          alert(result.message)

          // 장바구니 비우기
          orderService.clearCartAfterOrder()

          // 주문 완료 페이지로 이동
          router.push({
            path: '/customer/order-complete',
            query: {
              orderNumber: result.orderNumber,
              totalAmount: finalAmount.value,
              storeName: orderItems.value[0]?.storeName || '알 수 없는 가게',
              deliveryAddress: deliveryAddress.value,
              phoneNumber: phoneNumber.value,
            },
          })
        }
      } catch (error) {
        console.error('주문 제출 실패:', error)
        alert('주문 처리 중 오류가 발생했습니다. 다시 시도해주세요.')
      } finally {
        orderStore.setSubmitting(false)
      }
    }

    // 페이지 진입 시 장바구니 확인
    onMounted(() => {
      if (orderItems.value.length === 0) {
        alert('장바구니가 비어있습니다.')
        router.push('/customer/stores')
        return
      }

      // 임시 배달 정보 설정 (실제로는 사용자 정보에서 가져와야 함)
      orderStore.initializeDeliveryInfo(
        '서울시 강남구 테헤란로 123',
        '010-1234-5678'
      )
    })

    return {
      orderItems,
      totalAmount,
      deliveryFee,
      discountAmount,
      finalAmount,
      canSubmitOrder,
      groupedByStore,
      totalItemCount,
      increaseQuantity,
      decreaseQuantity,
      removeItem,
      editOptions,
      updateDeliveryInfo,
      submitOrder,
    }
  },
}
</script>

<style scoped>
/* 추가 스타일이 필요한 경우 여기에 작성 */
</style>
