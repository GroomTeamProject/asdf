<template>
  <!-- 모달 오버레이 추가 -->
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal show" @click.stop>
      <div class="modal-content max-w-2xl">
        <div class="border-b border-gray-300 p-4">
          <div class="flex justify-between items-center">
            <h3 class="text-gray-800">가게 정보 수정</h3>
            <!-- 닫기 버튼 추가 -->
            <button @click="$emit('close')" class="text-gray-500 hover:text-gray-700 text-xl">
              ×
            </button>
          </div>
        </div>
        
        <div class="p-4 space-y-4 max-h-96 overflow-y-auto">
          <!-- 기본 정보 -->
          <div class="space-y-4">
            <h4 class="font-semibold text-gray-700 border-b pb-2">기본 정보</h4>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm text-gray-700 mb-1">가게명 *</label>
                <input 
                  v-model="editData.restaurant.name"
                  type="text" 
                  class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
                  placeholder="가게명을 입력하세요"
                />
              </div>
              <div>
                <label class="block text-sm text-gray-700 mb-1">카테고리 *</label>
                <select 
                  v-model="editData.restaurant.category"
                  class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
                >
                  <option value="">카테고리 선택</option>
                  <option value="KOREAN">한식</option>
                  <option value="CHINESE">중식</option>
                  <option value="JAPANESE">일식</option>
                  <option value="WESTERN">양식</option>
                  <option value="CHICKEN">치킨</option>
                  <option value="PIZZA">피자</option>
                  <option value="BURGER">버거</option>
                  <option value="DESSERT">디저트</option>
                  <option value="CAFE">카페</option>
                  <option value="OTHER">기타</option>
                </select>
              </div>
            </div>
            <div>
              <label class="block text-sm text-gray-700 mb-1">가게 설명</label>
              <textarea 
                v-model="editData.restaurant.description"
                class="w-full border border-gray-400 rounded px-3 py-2 h-20 resize-none focus:outline-none focus:border-gray-600"
                placeholder="가게 설명을 입력하세요"
              ></textarea>
            </div>
          </div>

          <!-- 연락처 정보 -->
          <div class="space-y-4">
            <h4 class="font-semibold text-gray-700 border-b pb-2">연락처 정보</h4>
            <div>
              <label class="block text-sm text-gray-700 mb-1">전화번호</label>
              <input 
                v-model="editData.contact.phone"
                type="tel" 
                class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
                placeholder="010-1234-5678"
              />
            </div>
          </div>

          <!-- 위치 정보 -->
          <div class="space-y-4">
            <h4 class="font-semibold text-gray-700 border-b pb-2">위치 정보</h4>
            <div>
              <label class="block text-sm text-gray-700 mb-1">주소</label>
              <input 
                v-model="editData.location.address"
                type="text" 
                class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
                placeholder="기본 주소를 입력하세요"
              />
            </div>
            <div>
              <label class="block text-sm text-gray-700 mb-1">상세 주소</label>
              <input 
                v-model="editData.location.detailAddress"
                type="text" 
                class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
                placeholder="상세 주소를 입력하세요"
              />
            </div>
          </div>

          <!-- 배달 설정 -->
          <div class="space-y-4">
            <h4 class="font-semibold text-gray-700 border-b pb-2">배달 설정</h4>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm text-gray-700 mb-1">배달비 (원)</label>
                <input 
                  v-model.number="editData.delivery.deliveryFee"
                  type="number" 
                  class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
                  placeholder="0"
                  min="0"
                />
              </div>
              <div>
                <label class="block text-sm text-gray-700 mb-1">최소주문금액 (원)</label>
                <input 
                  v-model.number="editData.delivery.minOrderAmount"
                  type="number" 
                  class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
                  placeholder="0"
                  min="0"
                />
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm text-gray-700 mb-1">최소 배달시간 (분)</label>
                <input 
                  v-model.number="editData.delivery.deliveryTimeMin"
                  type="number" 
                  class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
                  placeholder="30"
                  min="0"
                />
              </div>
              <div>
                <label class="block text-sm text-gray-700 mb-1">최대 배달시간 (분)</label>
                <input 
                  v-model.number="editData.delivery.deliveryTimeMax"
                  type="number" 
                  class="w-full border border-gray-400 rounded px-3 py-2 focus:outline-none focus:border-gray-600"
                  placeholder="60"
                  min="0"
                />
              </div>
            </div>
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
            @click="handleSave"
            :disabled="loading"
            class="bg-gray-600 text-white px-4 py-2 rounded hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ loading ? '저장 중...' : '저장' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'

const props = defineProps({
  restaurant: {
    type: Object,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'save'])

const editData = reactive({
  restaurant: {
    name: '',
    description: '',
    category: '',
    imageUrl: ''
  },
  contact: {
    phone: ''
  },
  location: {
    address: '',
    detailAddress: ''
  },
  delivery: {
    deliveryFee: 0,
    minOrderAmount: 0,
    deliveryTimeMin: 0,
    deliveryTimeMax: 0
  }
})

const handleSave = () => {
  if (!editData.restaurant.name.trim()) {
    alert('가게명을 입력해주세요.')
    return
  }
  
  if (!editData.restaurant.category) {
    alert('카테고리를 선택해주세요.')
    return
  }

  emit('save', editData)
}

// 컴포넌트 마운트 시 기존 데이터로 초기화
onMounted(() => {
  editData.restaurant.name = props.restaurant.name || ''
  editData.restaurant.description = props.restaurant.description || ''
  editData.restaurant.category = props.restaurant.category || ''
  editData.restaurant.imageUrl = props.restaurant.imageUrl || ''
  
  editData.contact.phone = props.restaurant.phone || ''
  
  editData.location.address = props.restaurant.address || ''
  editData.location.detailAddress = props.restaurant.detailAddress || ''
  
  editData.delivery.deliveryFee = props.restaurant.deliveryFee || 0
  editData.delivery.minOrderAmount = props.restaurant.minOrderAmount || 0
  editData.delivery.deliveryTimeMin = props.restaurant.deliveryTimeMin || 0
  editData.delivery.deliveryTimeMax = props.restaurant.deliveryTimeMax || 0
})
</script>

<!-- 모달 스타일 추가 -->
<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9000;
}

.modal {
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  animation: modalFadeIn 0.2s ease-out;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.modal-content {
  width: 100%;
  max-width: 42rem; /* max-w-2xl */
}

@media (max-width: 768px) {
  .modal-overlay {
    padding: 1rem;
    align-items: flex-start;
    padding-top: 2rem;
  }
  
  .modal-content {
    max-width: 100%;
  }
}
</style>