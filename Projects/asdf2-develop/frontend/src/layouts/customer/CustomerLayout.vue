<template>
  <div class="flex min-h-screen flex-col items-center justify-center">
    <CustomerHeader />

    <main class="flex w-full flex-grow flex-col bg-neutral-50">
      <router-view />
    </main>

    <Footer />
    
    <!-- 플로팅 장바구니 버튼 -->
    <FloatingCartButton />
    
    <!-- 알림 토스트 -->
    <NotificationToast 
      :notifications="notifications" 
      :remove-notification="removeNotification" 
    />
  </div>
</template>

<script setup>
import CustomerHeader from './CustomerHeader.vue'
import Footer from '../Footer.vue'
import FloatingCartButton from '@/components/customer/cart/FloatingCartButton.vue'
import NotificationToast from '@/components/customer/NotificationToast.vue'
import { useCustomerSSE } from '@/composables/useCustomerSSE.js'

// SSE 훅 초기화 (Customer 페이지에서만 작동)
const { notifications, removeNotification } = useCustomerSSE()
</script>
