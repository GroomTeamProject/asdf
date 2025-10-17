<template>
  <div class="grid grid-cols-4 gap-0 bg-white border-2 border-gray-400 rounded-lg overflow-hidden mb-6">
    <button 
      v-for="tab in tabs" 
      :key="tab.key"
      :class="['tab-button', { active: activeTab === tab.key }]"
      @click="$emit('update:active-tab', tab.key)"
    >
      {{ tab.name }}
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  activeTab: {
    type: String,
    required: true
  }
})

const emit = defineEmits(['update:active-tab'])

const tabs = [
  { key: 'dashboard', name: '대시보드' },
  { key: 'orders', name: '주문 관리' },
  { key: 'menu', name: '메뉴 관리' },
  { key: 'restaurant', name: '가게 정보' }
]
</script>

<style scoped>
.tab-button {
  padding: 0.5rem 1rem; /* px-4 py-2 */
  border: 1px solid #9ca3af; /* border-gray-400 */
  color: #374151; /* text-gray-700 */
  background-color: #f3f4f6; /* bg-gray-100 */
  transition: background-color 0.2s; /* transition-colors */
}

.tab-button:hover {
  background-color: #e5e7eb; /* hover:bg-gray-200 */
}

.tab-button.active {
  background-color: #e5e7eb; /* bg-gray-200 */
  border-color: #4b5563; /* border-gray-600 */
}
</style>