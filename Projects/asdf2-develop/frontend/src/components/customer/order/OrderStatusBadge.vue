<template>
  <span 
    :class="statusClasses"
    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
  >
    {{ statusText }}
  </span>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'OrderStatusBadge',
  props: {
    status: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const statusConfig = {
      PENDING: {
        text: '주문 대기',
        classes: 'bg-yellow-100 text-yellow-800'
      },
      ACCEPTED: {
        text: '주문 수락',
        classes: 'bg-blue-100 text-blue-800'
      },
      COOKING: {
        text: '조리 중',
        classes: 'bg-orange-100 text-orange-800'
      },
      READY: {
        text: '조리 완료',
        classes: 'bg-purple-100 text-purple-800'
      },
      DELIVERED: {
        text: '배달 완료',
        classes: 'bg-green-100 text-green-800'
      },
      CANCELLED: {
        text: '주문 취소',
        classes: 'bg-red-100 text-red-800'
      },
      REJECTED: {
        text: '주문 거절',
        classes: 'bg-gray-100 text-gray-800'
      }
    }

    const statusText = computed(() => {
      return statusConfig[props.status]?.text || props.status
    })

    const statusClasses = computed(() => {
      return statusConfig[props.status]?.classes || 'bg-gray-100 text-gray-800'
    })

    return {
      statusText,
      statusClasses
    }
  }
}
</script>
