<!-- src/components/owner/menu/MenuModal.vue -->
<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ isEdit ? '메뉴 수정' : '메뉴 추가' }}</h3>
        <button @click="$emit('close')" class="close-btn">✕</button>
      </div>

      <form @submit.prevent="handleSubmit" class="modal-form">
        <!-- 기본 정보 -->
        <div class="form-section">
          <h4>기본 정보</h4>
          
          <div class="form-group">
            <label for="menuName">메뉴명 *</label>
            <input
              id="menuName"
              v-model="formData.name"
              type="text"
              maxlength="100"
              required
              class="form-input"
              placeholder="예: 김치찌개, 불고기버거 등"
            >
            <small class="char-count">{{ formData.name.length }}/100</small>
          </div>

          <div class="form-group">
            <label for="menuDescription">메뉴 설명</label>
            <textarea
              id="menuDescription"
              v-model="formData.description"
              maxlength="1000"
              rows="3"
              class="form-textarea"
              placeholder="메뉴에 대한 상세한 설명을 입력해주세요"
            ></textarea>
            <small class="char-count">{{ formData.description.length }}/1000</small>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="menuPrice">가격 *</label>
              <input
                id="menuPrice"
                v-model.number="formData.price"
                type="number"
                min="0"
                max="1000000"
                required
                class="form-input"
                placeholder="0"
              >
            </div>

            <div class="form-group">
              <label for="menuCategory">카테고리 *</label>
              <div class="category-input-group">
                <select
                  id="menuCategory"
                  v-model="formData.categoryId"
                  required
                  class="form-select category-select"
                >
                  <option value="">카테고리 선택</option>
                  <option 
                    v-for="category in categories" 
                    :key="category.id" 
                    :value="category.id"
                    :disabled="!category.isActive"
                  >
                    {{ category.name }} {{ !category.isActive ? '(비활성)' : '' }}
                  </option>
                </select>
                <button 
                  type="button"
                  @click="showCategoryModal = true"
                  class="add-category-btn"
                  title="카테고리 추가"
                >
                  ➕
                </button>
              </div>
            </div>
          </div>

          <div class="form-group">
            <label for="menuImage">이미지 URL</label>
            <input
              id="menuImage"
              v-model="formData.imageUrl"
              type="url"
              maxlength="500"
              class="form-input"
              placeholder="https://example.com/image.jpg"
            >
            <small class="help-text">
              이미지 URL을 입력하거나, 메뉴 저장 후 이미지 업로드 버튼을 이용하세요
            </small>
          </div>
        </div>

        <!-- 메뉴 옵션 -->
        <div class="form-section">
          <h4>메뉴 옵션</h4>
          
          <div class="option-checkboxes">
            <label class="checkbox-label">
              <input
                v-model="formData.isPopular"
                type="checkbox"
                class="checkbox-input"
              >
              <span class="checkbox-text">인기 메뉴</span>
            </label>

            <label class="checkbox-label">
              <input
                v-model="formData.isRecommended"
                type="checkbox"
                class="checkbox-input"
              >
              <span class="checkbox-text">추천 메뉴</span>
            </label>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="menuStatus">판매 상태</label>
              <select
                id="menuStatus"
                v-model="formData.status"
                class="form-select"
              >
                <option value="AVAILABLE">판매중</option>
                <option value="SOLD_OUT">품절</option>
                <option value="HIDDEN">숨김</option>
              </select>
            </div>

            <div class="form-group">
              <label for="displayOrder">표시 순서</label>
              <div class="order-input-group">
                <input
                  id="displayOrder"
                  v-model.number="formData.displayOrder"
                  type="number"
                  min="0"
                  max="999"
                  class="form-input"
                  placeholder="자동"
                >
                <button 
                  type="button" 
                  @click="setToLastOrder" 
                  class="auto-order-btn"
                  title="마지막 순서로 설정"
                >
                  📌
                </button>
              </div>
              <small class="help-text">
                0 또는 비워두면 자동으로 마지막 순서로 설정됩니다
              </small>
            </div>
          </div>
        </div>

        <!-- 이미지 삭제 옵션 (수정 시에만) -->
        <div v-if="isEdit && menu?.imageUrl" class="form-section">
          <h4>이미지 관리</h4>
          
          <div class="current-image">
            <img :src="menu.imageUrl" :alt="menu.name" class="preview-image">
            <label class="checkbox-label">
              <input
                v-model="formData.removeImage"
                type="checkbox"
                class="checkbox-input"
              >
              <span class="checkbox-text">현재 이미지 삭제</span>
            </label>
          </div>
        </div>

        <!-- 모달 액션 -->
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

    <!-- 카테고리 추가 모달 -->
    <CategoryModal
      v-if="showCategoryModal"
      @close="showCategoryModal = false"
      @save="handleCategoryAdd"
    />
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { menuApi } from '@/api/owner/menuApi'
import CategoryModal from './CategoryModal.vue'

const props = defineProps({
  menu: {
    type: Object,
    default: null
  },
  categories: {
    type: Array,
    default: () => []
  },
  isEdit: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'save', 'categoryAdded'])

// 카테고리 모달 표시 상태
const showCategoryModal = ref(false)

// 폼 데이터
const formData = reactive({
  name: '',
  description: '',
  price: 0,
  categoryId: '',
  imageUrl: '',
  isPopular: false,
  isRecommended: false,
  status: 'AVAILABLE',
  displayOrder: 0,
  removeImage: false
})

// 수정 모드일 때 기존 데이터 로드
watch(() => props.menu, (newMenu) => {
  if (newMenu) {
    formData.name = newMenu.name || ''
    formData.description = newMenu.description || ''
    formData.price = Number(newMenu.price) || 0
    formData.categoryId = newMenu.categoryId || ''
    formData.imageUrl = newMenu.imageUrl || ''
    formData.isPopular = newMenu.isPopular || false
    formData.isRecommended = newMenu.isRecommended || false
    formData.status = newMenu.status || 'AVAILABLE'
    formData.displayOrder = newMenu.displayOrder || 0
    formData.removeImage = false
  }
}, { immediate: true })

// 카테고리 추가 처리
const handleCategoryAdd = async (categoryData) => {
  try {
    const newCategory = await menuApi.createCategory(categoryData)
    showCategoryModal.value = false
    
    // 부모 컴포넌트에 새 카테고리 정보 전달
    emit('categoryAdded', newCategory)
    
    // 새로 추가된 카테고리를 자동 선택
    formData.categoryId = newCategory.id
    
    alert('카테고리가 추가되었습니다!')
  } catch (error) {
    console.error('카테고리 추가 실패:', error)
    alert('카테고리 추가에 실패했습니다: ' + error.message)
  }
}

// 마지막 순서로 설정
const setToLastOrder = () => {
  formData.displayOrder = 999
}

// 폼 제출 처리
const handleSubmit = () => {
  // 유효성 검사
  if (!formData.name.trim()) {
    alert('메뉴명을 입력해주세요.')
    return
  }

  if (!formData.categoryId) {
    alert('카테고리를 선택해주세요.')
    return
  }

  if (formData.price < 0 || formData.price > 1000000) {
    alert('가격은 0원 이상 100만원 이하로 입력해주세요.')
    return
  }

  // 데이터 정제
  const menuData = {
    name: formData.name.trim(),
    description: formData.description.trim(),
    price: Number(formData.price),
    categoryId: Number(formData.categoryId),
    imageUrl: formData.imageUrl.trim(),
    isPopular: formData.isPopular,
    isRecommended: formData.isRecommended,
    status: formData.status,
    displayOrder: Number(formData.displayOrder) || 0
  }

  // 수정 모드일 때만 removeImage 추가
  if (props.isEdit) {
    menuData.removeImage = formData.removeImage
  }

  emit('save', menuData)
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
  max-width: 700px;
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
  font-size: 20px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
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

.form-section {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #f3f4f6;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
}

.form-section h4 {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin: 0 0 1rem 0;
}

.form-group {
  margin-bottom: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.form-input, .form-textarea, .form-select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-input:focus, .form-textarea:focus, .form-select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* 카테고리 입력 그룹 스타일 */
.category-input-group {
  display: flex;
  gap: 0.5rem;
  align-items: stretch;
}

.category-select {
  flex: 1;
}

.add-category-btn {
  background: #10b981;
  color: white;
  border: none;
  padding: 0.75rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
  white-space: nowrap;
  min-width: 45px;
}

.add-category-btn:hover {
  background: #059669;
}

/* 순서 입력 그룹 스타일 */
.order-input-group {
  display: flex;
  gap: 0.5rem;
  align-items: stretch;
}

.auto-order-btn {
  background: #6b7280;
  color: white;
  border: none;
  padding: 0.75rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
  white-space: nowrap;
  min-width: 45px;
}

.auto-order-btn:hover {
  background: #4b5563;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
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

.option-checkboxes {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-size: 14px;
  color: #374151;
}

.checkbox-input {
  width: auto;
  margin: 0;
}

.current-image {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.preview-image {
  width: 100px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
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
    max-height: 95vh;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .option-checkboxes {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .current-image {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .category-input-group {
    flex-direction: column;
  }
  
  .order-input-group {
    flex-direction: column;
  }
  
  .add-category-btn, .auto-order-btn {
    min-width: auto;
  }
}
</style>