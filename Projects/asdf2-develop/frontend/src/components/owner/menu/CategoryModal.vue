<!-- src/components/owner/menu/CategoryModal.vue -->
<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ isEdit ? '카테고리 수정' : '카테고리 추가' }}</h3>
        <button @click="$emit('close')" class="close-btn">✕</button>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-form">
        <div class="form-group">
          <label for="categoryName">카테고리명 *</label>
          <input
            id="categoryName"
            v-model="formData.name"
            type="text"
            maxlength="50"
            required
            class="form-input"
            placeholder="예: 메인메뉴, 사이드메뉴, 음료 등"
          >
          <small class="char-count">{{ formData.name.length }}/50</small>
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
            <span class="checkbox-text">카테고리 활성화</span>
          </label>
          <small class="help-text">
            비활성화하면 고객에게 보이지 않습니다
          </small>
        </div>

        <!-- 수정 시에만 강제 비활성화 옵션 표시 -->
        <div v-if="isEdit && !formData.isActive" class="form-group">
          <label class="checkbox-label">
            <input
              v-model="formData.forceDeactivate"
              type="checkbox"
              class="checkbox-input"
            >
            <span class="checkbox-text">포함된 메뉴도 함께 숨김 처리</span>
          </label>
          <small class="help-text warning">
            ⚠️ 이 카테고리의 모든 메뉴가 숨김 처리됩니다
          </small>
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
  category: {
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
  displayOrder: 0,
  isActive: true,
  forceDeactivate: false
})

// 수정 모드일 때 기존 데이터 로드
watch(() => props.category, (newCategory) => {
  if (newCategory) {
    formData.name = newCategory.name || ''
    formData.displayOrder = newCategory.displayOrder || 0
    formData.isActive = newCategory.isActive !== undefined ? newCategory.isActive : true
    formData.forceDeactivate = false
  }
}, { immediate: true })

// 폼 제출 처리
const handleSubmit = () => {
  if (!formData.name.trim()) {
    alert('카테고리명을 입력해주세요.')
    return
  }

  if (formData.name.length > 50) {
    alert('카테고리명은 50자를 초과할 수 없습니다.')
    return
  }

  // 데이터 정제
  const categoryData = {
    name: formData.name.trim(),
    displayOrder: formData.displayOrder || 0,
    isActive: formData.isActive
  }

  // 수정 모드일 때만 forceDeactivate 추가
  if (props.isEdit) {
    categoryData.forceDeactivate = formData.forceDeactivate
  }

  emit('save', categoryData)
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
  z-index: 1000;
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

.help-text.warning {
  color: #dc2626;
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

.modal-actions {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
  margin-top: 2rem;
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
</style>