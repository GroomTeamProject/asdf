<template>
  <div class="border-2 border-gray-400 bg-white rounded-lg">
    <div class="border-b border-gray-300 p-4">
      <h2 class="text-gray-800">최근 주문</h2>
    </div>
    <div class="p-4">
      <div class="space-y-4">
        <div 
          v-for="order in recentOrders" 
          :key="order.id"
          class="flex items-center justify-between p-4 border-2 border-gray-300 rounded-lg"
        >
          <div>
            <p class="text-gray-800">주문 #{{ order.id }} - {{ order.customerName }}</p>
            <p class="text-sm text-gray-600">
              {{ order.items.map(item => `${item.name} x${item.quantity}`).join(', ') }}
            </p>
            <p class="text-sm text-gray-600">{{ order.orderTime }}</p>
          </div>
          <div class="text-right">
            <span class="inline-flex px-2 py-1 text-xs rounded border border-gray-400 bg-gray-200 text-gray-800">
              {{ getStatusText(order.status) }}
            </span>
            <p class="mt-1 text-gray-800">{{ formatPrice(order.total) }}</p>
          </div>
        </div>
        
        <!-- 주문이 없는 경우 -->
        <div v-if="!recentOrders || recentOrders.length === 0" class="text-center py-8 text-gray-500">
          <p>최근 주문이 없습니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  recentOrders: {
    type: Array,
    default: () => []
  }
})

const formatPrice = (price) => {
  return `${(price || 0).toLocaleString()}원`
}

const getStatusText = (status) => {
  const statusMap = {
    'pending': '주문 접수',
    'preparing': '조리 중',
    'ready': '조리 완료',
    'delivered': '배달 완료'
  }
  return statusMap[status] || status
}
</script>