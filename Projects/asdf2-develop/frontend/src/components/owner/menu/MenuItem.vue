<template>
  <div class="border-2 border-gray-400 bg-white rounded-lg">
    <div class="p-4">
      <div class="flex gap-4">
        <div class="image-placeholder w-20 h-20 flex-shrink-0">
          <span>IMAGE</span>
        </div>
        <div class="flex-1">
          <div class="flex items-center gap-2 mb-1">
            <h3 class="text-gray-800">{{ item.name }}</h3>
            <span :class="[
              'inline-flex px-2 py-1 text-xs rounded border border-gray-400',
              item.available ? 'bg-gray-200 text-gray-800' : 'bg-gray-400 text-gray-600'
            ]">
              {{ item.available ? '판매 중' : '품절' }}
            </span>
          </div>
          <p class="text-sm text-gray-600 mb-2">{{ item.description }}</p>
          <p class="text-gray-800">{{ formatPrice(item.price) }}</p>
          <p v-if="item.category" class="text-xs text-gray-500 mt-1">카테고리: {{ item.category }}</p>
        </div>
        <div class="flex gap-2">
          <button
            @click="$emit('toggle-availability', item.id)"
            class="border border-gray-400 text-gray-700 hover:bg-gray-200 px-3 py-1 rounded text-sm"
          >
            {{ item.available ? '품절 처리' : '판매 재개' }}
          </button>
          <button class="border border-gray-400 text-gray-700 hover:bg-gray-200 p-2 rounded">
            <Edit class="w-4 h-4" />
          </button>
          <button 
            @click="handleDelete"
            class="border border-gray-400 text-gray-700 hover:bg-gray-200 p-2 rounded"
          >
            <Trash2 class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Edit, Trash2 } from 'lucide-vue-next'

const props = defineProps({
  item: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['toggle-availability', 'delete'])

const formatPrice = (price) => {
  return `${(price || 0).toLocaleString()}원`
}

const handleDelete = () => {
  if (confirm(`'${props.item.name}' 메뉴를 삭제하시겠습니까?`)) {
    emit('delete', props.item.id)
  }
}
</script>