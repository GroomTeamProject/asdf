<!-- src/components/owner/menu/OptionItemModal.vue -->
<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ isEdit ? '옵션 아이템 수정' : '옵션 아이템 추가' }}</h3>
        <button @click="$emit('close')" class="close-btn">✕</button>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-form">
        <div class="form-group">
          <label for="itemName">아이템명 *</label>
          <input
            id="itemName"
            v-model="formData.name"
            type="text"
            maxlength="50"
            required
            class="form-input"
            placeholder="예: 대, 중, 소 또는 콜라, 사이다, 커피 등"
          >
          <small class="char-count">{{ formData.name.length }}/50</small>
        </div>

        <div class="form-group">
          <label for="additionalPrice">추가 금액</label>
          <div class="price-input-group">
            <input
              id="additionalPrice"
              v-model.number="formData.additionalPrice"
              type="number"
              min="0"
              max="100000"
              step="100"
              class="form-input"
              placeholder="0"
            >
            <span class="currency">원</span>
          </div>
          <small class="help-text">
            기본 메뉴 가격에 추가될 금액입니다. 0원이면 추가 비용이 없습니다.
          </small>
        </div>

        <div class="form-group">
          <label for="displayOrder">표시 순서</label>
          <input
            id="displayOrder"
            v-model.number="formData.displayOrder"
            type="number"
            min="0"
            class="form-input"
            placeholder="0"
          >
          <small class="help-text">숫자가 작을수록 위에 표시됩니다</small>
        </div>

        <div class="form-group">
          <label class="checkbox-label">
            <input
              v-model="formData.isActive"
              type="checkbox"
              class="checkbox-input"
            >
            <span class="checkbox-text">활성화</span>
          </label>
          <small class="help-text">
            비활성화하면 고객에게 보이지 않습니다
          </small>
        </div>

        <!-- 미리보기 -->
        <div class="preview-section">
          <h4>미리보기</h4>
          <div class="item-preview" :class="{ 'inactive': !formData.isActive }">
            <div class="preview-content">
              <span class="preview-name">
                {{ formData.name || '아이템명' }}
              </span>
              <span class="preview-price">
                <span v-if="formData.additionalPrice > 0">
                  +{{ formatPrice(formData.additionalPrice) }}원
                </span>
                <span v-else class="free">무료</span>
              </span>
            </div>
            <div v-if="!formData.isActive" class="inactive-badge">
              비활성
            </div>
          </div>
        </div>

        <div class="modal-actions">
          <button type="button" @click="$emit('close')" class="cancel-btn">
            취소
          </button>
          <button type="submit" class="save-btn">
            {{ isEdit ? '수정' : '추가' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    default: null
  },
  isEdit: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'save'])

// 폼 데이터
const formData = reactive({
  name: '',
  additionalPrice: 0,
  displayOrder: 0,
  isActive: true
})

// 수정 모드일 때 기존 데이터 로드
watch(() => props.item, (newItem) => {
  if (newItem) {
    formData.name = newItem.name || ''
    formData.additionalPrice = Number(newItem.additionalPrice) || 0
    formData.displayOrder = newItem.displayOrder || 0
    formData.isActive = newItem.isActive !== undefined ? newItem.isActive : true
  }
}, { immediate: true })

// 폼 제출 처리
const handleSubmit = () => {
  // 유효성 검사
  if (!formData.name.trim()) {
    alert('아이템명을 입력해주세요.')
    return
  }

  if (formData.additionalPrice < 0 || formData.additionalPrice > 100000) {
    alert('추가 금액은 0원 이상 10만원 이하로 입력해주세요.')
    return
  }

  // 데이터 정제
  const itemData = {
    name: formData.name.trim(),
    additionalPrice: Number(formData.additionalPrice) || 0,
    displayOrder: Number(formData.displayOrder) || 0,
    isActive: formData.isActive
  }

  emit('save', itemData)
}

// 유틸리티 함수
const formatPrice = (price) => {
  return Number(price).toLocaleString()
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1200;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #6b7280;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background: #f3f4f6;
}

.modal-form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.price-input-group {
  position: relative;
  display: flex;
  align-items: center;
}

.currency {
  position: absolute;
  right: 0.75rem;
  color: #6b7280;
  font-size: 14px;
  pointer-events: none;
}

.char-count {
  display: block;
  text-align: right;
  margin-top: 0.25rem;
  font-size: 12px;
  color: #6b7280;
}

.help-text {
  display: block;
  margin-top: 0.25rem;
  font-size: 12px;
  color: #6b7280;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  margin-bottom: 0 !important;
}

.checkbox-input {
  width: auto;
  margin: 0;
}

.checkbox-text {
  font-size: 14px;
  color: #374151;
}

.preview-section {
  margin-top: 2rem;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.preview-section h4 {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin: 0 0 0.75rem 0;
}

.item-preview {
  background: white;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 0.75rem;
  position: relative;
  transition: all 0.2s;
}

.item-preview.inactive {
  opacity: 0.6;
  background: #f3f4f6;
}

.preview-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-name {
  font-size: 14px;
  font-weight: 500;
  color: #111827;
}

.preview-price {
  font-size: 13px;
  font-weight: 600;
  color: #059669;
}

.preview-price .free {
  color: #6b7280;
}

.inactive-badge {
  position: absolute;
  top: -0.5rem;
  right: -0.5rem;
  background: #ef4444;
  color: white;
  font-size: 10px;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
}

.modal-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
}

.cancel-btn, .save-btn {
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn {
  background: white;
  border: 1px solid #d1d5db;
  color: #374151;
}

.cancel-btn:hover {
  background: #f9fafb;
}

.save-btn {
  background: #3b82f6;
  border: 1px solid #3b82f6;
  color: white;
}

.save-btn:hover {
  background: #2563eb;
}

/* 반응형 */
@media (max-width: 768px) {
  .modal-content {
    width: 95%;
  }
  
  .preview-content {
    flex-direction: column;
    gap: 0.5rem;
    align-items: flex-start;
  }
}
</style>