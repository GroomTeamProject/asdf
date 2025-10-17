<template>
  <div class="modal show">
    <div class="modal-content">
      <div class="border-b border-gray-300 p-4">
        <h3 class="text-gray-800">새 메뉴 추가</h3>
      </div>
      <div class="p-4 space-y-4">
        <div>
          <label class="block text-sm text-gray-700 mb-1">메뉴명 *</label>
          <input 
            v-model="newMenu.name"
            type="text" 
            class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
            placeholder="메뉴 이름을 입력하세요"
          />
        </div>
        <div>
          <label class="block text-sm text-gray-700 mb-1">설명</label>
          <textarea 
            v-model="newMenu.description"
            class="w-full border border-gray-400 rounded px-3 py-2 h-20 resize-none focus:outline-none focus:border-gray-600"
            placeholder="메뉴 설명을 입력하세요"
          ></textarea>
        </div>
        <div>
          <label class="block text-sm text-gray-700 mb-1">가격 *</label>
          <input 
            v-model.number="newMenu.price"
            type="number" 
            class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
            placeholder="0"
            min="0"
          />
        </div>
        <div>
          <label class="block text-sm text-gray-700 mb-1">카테고리 *</label>
          <select 
            v-model="newMenu.category"
            class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
          >
            <option value="">카테고리 선택</option>
            <option value="치킨">치킨</option>
            <option value="사이드">사이드</option>
            <option value="음료">음료</option>
            <option value="디저트">디저트</option>
          </select>
        </div>
      </div>
      <div class="border-t border-gray-300 p-4 flex justify-end gap-2">
        <button 
          @click="$emit('close')"
          class="border border-gray-400 text-gray-700 px-4 py-2 rounded hover:bg-gray-200"
        >
          취소
        </button>
        <button 
          @click="handleAdd"
          class="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700"
        >
          추가
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'

const emit = defineEmits(['close', 'add'])

const newMenu = reactive({
  name: '',
  description: '',
  price: 0,
  category: ''
})

const handleAdd = () => {
  if (!newMenu.name.trim()) {
    alert('메뉴명을 입력해주세요.')
    return
  }
  
  if (!newMenu.price || newMenu.price <= 0) {
    alert('올바른 가격을 입력해주세요.')
    return
  }
  
  if (!newMenu.category) {
    alert('카테고리를 선택해주세요.')
    return
  }

  emit('add', {
    name: newMenu.name,
    description: newMenu.description,
    price: newMenu.price,
    category: newMenu.category
  })
  
  // 폼 초기화
  newMenu.name = ''
  newMenu.description = ''
  newMenu.price = 0
  newMenu.category = ''
}
</script>