<!-- src/components/owner/menu/OptionGroupModal.vue -->
<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ isEdit ? '옵션 그룹 수정' : '옵션 그룹 추가' }}</h3>
        <button @click="$emit('close')" class="close-btn">✕</button>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-form">
        <div class="form-group">
          <label for="groupName">그룹명 *</label>
          <input
            id="groupName"
            v-model="formData.name"
            type="text"
            maxlength="50"
            required
            class="form-input"
            placeholder="예: 사이즈, 맛 선택, 추가 옵션 등"
          >
          <small class="char-count">{{ formData.name.length }}/50</small>
        </div>

        <div class="form-group">
          <label for="groupType">옵션 타입 *</label>
          <select
            id="groupType"
            v-model="formData.type"
            required
            class="form-select"
          >
            <option value="">타입 선택</option>
            <option value="SINGLE">단일 선택 (라디오 버튼)</option>
            <option value="MULTIPLE">다중 선택 (체크박스)</option>
          </select>
          <small class="help-text">
            단일 선택: 하나만 선택 가능 | 다중 선택: 여러 개 선택 가능
          </small>
        </div>

        <div class="form-group">
          <label class="checkbox-label">
            <input
              v-model="formData.isRequired"
              type="checkbox"
              class="checkbox-input"
            >
            <span class="checkbox-text">필수 옵션</span>
          </label>
          <small class="help-text">
            필수 옵션으로 설정하면 고객이 반드시 선택해야 합니다
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

        <!-- 새 그룹 추가 시에만 기본 아이템 설정 -->
        <div v-if="!isEdit" class="form-section">
          <h4>기본 옵션 아이템</h4>
          <p class="section-desc">그룹 생성과 함께 추가할 기본 아이템들을 설정하세요.</p>
          
          <div class="items-list">
            <div 
              v-for="(item, index) in formData.items" 
              :key="index"
              class="item-input-group"
            >
              <div class="item-inputs">
                <input
                  v-model="item.name"
                  type="text"
                  placeholder="아이템명 (예: 대, 중, 소)"
                  maxlength="50"
                  required
                  class="item-name-input"
                >
                <input
                  v-model.number="item.additionalPrice"
                  type="number"
                  min="0"
                  max="100000"
                  placeholder="추가금액"
                  class="item-price-input"
                >
              </div>
              <button 
                type="button" 
                @click="removeItem(index)"
                :disabled="formData.items.length <= 1"
                class="remove-item-btn"
                title="아이템 제거"
              >
                ❌
              </button>
            </div>
          </div>

          <button type="button" @click="addItem" class="add-item-btn">
            ➕ 아이템 추가
          </button>
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
  group: {
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
  type: '',
  isRequired: false,
  displayOrder: 0,
  items: [
    { name: '', additionalPrice: 0, displayOrder: 0, isActive: true }
  ]
})

// 수정 모드일 때 기존 데이터 로드
watch(() => props.group, (newGroup) => {
  if (newGroup) {
    formData.name = newGroup.name || ''
    formData.type = newGroup.type || ''
    formData.isRequired = newGroup.isRequired || false
    formData.displayOrder = newGroup.displayOrder || 0
    // 수정 모드에서는 아이템 목록 초기화하지 않음
    formData.items = []
  } else {
    // 새 그룹 추가 모드
    formData.items = [
      { name: '', additionalPrice: 0, displayOrder: 0, isActive: true }
    ]
  }
}, { immediate: true })

// 아이템 관리
const addItem = () => {
  formData.items.push({
    name: '',
    additionalPrice: 0,
    displayOrder: formData.items.length,
    isActive: true
  })
}

const removeItem = (index) => {
  if (formData.items.length > 1) {
    formData.items.splice(index, 1)
    // 순서 재정렬
    formData.items.forEach((item, idx) => {
      item.displayOrder = idx
    })
  }
}

// 폼 제출 처리
const handleSubmit = () => {
  // 유효성 검사
  if (!formData.name.trim()) {
    alert('그룹명을 입력해주세요.')
    return
  }

  if (!formData.type) {
    alert('옵션 타입을 선택해주세요.')
    return
  }

  // 새 그룹 추가 시 아이템 검사
  if (!props.isEdit) {
    const validItems = formData.items.filter(item => item.name.trim())
    if (validItems.length === 0) {
      alert('최소 1개의 옵션 아이템을 입력해주세요.')
      return
    }

    // 중복 아이템명 검사
    const itemNames = validItems.map(item => item.name.trim().toLowerCase())
    const uniqueNames = [...new Set(itemNames)]
    if (itemNames.length !== uniqueNames.length) {
      alert('중복된 아이템명이 있습니다.')
      return
    }
  }

  // 데이터 정제
  const groupData = {
    name: formData.name.trim(),
    type: formData.type,
    isRequired: formData.isRequired,
    displayOrder: Number(formData.displayOrder) || 0
  }

  // 새 그룹 추가 시에만 아이템 포함
  if (!props.isEdit) {
    groupData.items = formData.items
      .filter(item => item.name.trim())
      .map((item, index) => ({
        name: item.name.trim(),
        additionalPrice: Number(item.additionalPrice) || 0,
        displayOrder: index,
        isActive: true
      }))
  }

  emit('save', groupData)
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
  z-index: 1100;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
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

.form-input, .form-select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus, .form-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
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

.form-section {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.form-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 0.5rem 0;
}

.section-desc {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 1rem 0;
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.item-input-group {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.item-inputs {
  display: flex;
  gap: 0.5rem;
  flex: 1;
}

.item-name-input {
  flex: 2;
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

.item-price-input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  min-width: 100px;
}

.remove-item-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 0.25rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.remove-item-btn:hover:not(:disabled) {
  background: #fee2e2;
}

.remove-item-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.add-item-btn {
  background: #10b981;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.add-item-btn:hover {
  background: #059669;
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
  
  .item-input-group {
    flex-direction: column;
  }
  
  .item-inputs {
    width: 100%;
  }
}
</style>