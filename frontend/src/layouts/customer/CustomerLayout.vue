<template>
  <div class="flex min-h-screen flex-col items-center justify-center">
    <CustomerHeader />

    <main class="flex w-full flex-grow flex-col bg-neutral-50">
      <router-view />
    </main>

    <Footer />
    
    <!-- 플로팅 장바구니 버튼 (장바구니, 주문, 주문완료 페이지에서는 숨김) -->
    <FloatingCartButton v-if="shouldShowFloatingCart" />
    
    <!-- 알림 토스트 -->
    <NotificationToast 
      :notifications="notifications" 
      :remove-notification="removeNotification" 
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import CustomerHeader from './CustomerHeader.vue'
import Footer from '../Footer.vue'
import FloatingCartButton from '@/components/customer/cart/FloatingCartButton.vue'
import NotificationToast from '@/components/customer/NotificationToast.vue'
import { useCustomerSSE } from '@/composables/useCustomerSSE.js'

// SSE 훅 초기화 (Customer 페이지에서만 작동)
const { notifications, removeNotification } = useCustomerSSE()

// 현재 라우트
const route = useRoute()

// 플로팅 장바구니 버튼 표시 여부
const shouldShowFloatingCart = computed(() => {
  const currentPath = route.path
  
  // 숨길 페이지들
  const hiddenPages = [
    '/customer/cart',
    '/customer/order',
    '/customer/order-complete'
  ]
  
  return !hiddenPages.includes(currentPath)
})
</script>
