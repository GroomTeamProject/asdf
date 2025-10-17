<!-- src/components/owner/menu/MenuCategoryManager.vue -->
<template>
  <div class="category-manager">
    <div class="manager-header">
      <h3>메뉴 카테고리 관리</h3>
      <button @click="showAddModal = true" class="add-btn">
        ➕ 카테고리 추가
      </button>
    </div>

    <!-- 카테고리 목록 -->
    <div class="category-list">
      <div v-if="categories.length === 0" class="no-categories">
        등록된 카테고리가 없습니다.
      </div>
      
      <div 
        v-for="(category, index) in categories" 
        :key="category.id"
        class="category-item"
        :class="{ 'inactive': !category.isActive }"
      >
        <div class="category-info">
          <div class="category-main">
            <span class="category-name">{{ category.name }}</span>
            <div class="category-stats">
              <span class="menu-count">메뉴 {{ category.activeMenuCount }}/{{ category.menuCount }}개</span>
              <span class="status-badge" :class="category.isActive ? 'active' : 'inactive'">
                {{ category.isActive ? '활성' : '비활성' }}
              </span>
            </div>
          </div>
          <div class="category-order">
            순서: {{ category.displayOrder }}
          </div>
        </div>

        <div class="category-controls">
          <!-- 순서 변경 버튼 -->
          <div class="order-controls">
            <button 
              @click="moveCategory(category.id, index - 1)"
              :disabled="index === 0"
              class="order-btn"
              title="위로 이동"
            >
              ⬆️
            </button>
            <button 
              @click="moveCategory(category.id, index + 1)"
              :disabled="index === categories.length - 1"
              class="order-btn"
              title="아래로 이동"
            >
              ⬇️
            </button>
          </div>

          <!-- 액션 버튼 -->
          <div class="action-controls">
            <button 
              @click="editCategory(category)" 
              class="edit-btn"
              title="수정"
            >
              ✏️
            </button>
            <button 
              @click="toggleCategoryStatus(category)"
              class="toggle-btn"
              :title="category.isActive ? '비활성화' : '활성화'"
            >
              {{ category.isActive ? '🔒' : '🔓' }}
            </button>
            <button 
              @click="deleteCategory(category)"
              class="delete-btn"
              title="삭제"
            >
              🗑️
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 카테고리 추가/수정 모달 -->
    <CategoryModal
      v-if="showAddModal || showEditModal"
      :category="editingCategory"
      :is-edit="showEditModal"
      @close="closeModal"
      @save="saveCategory"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { menuApi } from '@/api/owner/menuApi'
import CategoryModal from './CategoryModal.vue'

const categories = ref([])
const showAddModal = ref(false)
const showEditModal = ref(false)
const editingCategory = ref(null)

const emit = defineEmits(['refresh'])

// 카테고리 목록 로드
const loadCategories = async () => {
  try {
    categories.value = await menuApi.getCategories()
    // 순서대로 정렬
    categories.value.sort((a, b) => a.displayOrder - b.displayOrder)
  } catch (error) {
    console.error('카테고리 목록 로드 실패:', error)
    alert('카테고리 목록을 불러오는데 실패했습니다.')
  }
}

// 카테고리 추가/수정
const saveCategory = async (categoryData) => {
  try {
    if (showEditModal.value) {
      // 수정
      await menuApi.updateCategory(editingCategory.value.id, categoryData)
      alert('카테고리가 수정되었습니다!')
    } else {
      // 추가
      await menuApi.createCategory(categoryData)
      alert('카테고리가 추가되었습니다!')
    }
    
    closeModal()
    await loadCategories()
    emit('refresh')
    
  } catch (error) {
    console.error('카테고리 저장 실패:', error)
    alert('카테고리 저장에 실패했습니다: ' + error.message)
  }
}

// 카테고리 수정 모달 열기
const editCategory = (category) => {
  editingCategory.value = { ...category }
  showEditModal.value = true
}

// 카테고리 상태 토글
const toggleCategoryStatus = async (category) => {
  const action = category.isActive ? '비활성화' : '활성화'
  let confirmMessage = `"${category.name}" 카테고리를 ${action}하시겠습니까?`
  
  if (category.isActive && category.activeMenuCount > 0) {
    confirmMessage += `\n\n⚠️ 이 카테고리에는 ${category.activeMenuCount}개의 활성 메뉴가 있습니다.\n카테고리를 비활성화하면 포함된 메뉴들도 숨김 처리됩니다.`
  }
  
  if (!confirm(confirmMessage)) return
  
  try {
    await menuApi.updateCategory(category.id, {
      isActive: !category.isActive,
      forceDeactivate: !category.isActive ? false : true
    })
    
    alert(`카테고리가 ${action}되었습니다!`)
    await loadCategories()
    emit('refresh')
    
  } catch (error) {
    console.error('카테고리 상태 변경 실패:', error)
    alert('상태 변경에 실패했습니다: ' + error.message)
  }
}

// 카테고리 삭제
const deleteCategory = async (category) => {
  if (category.menuCount > 0) {
    alert(`"${category.name}" 카테고리에 메뉴가 ${category.menuCount}개 있습니다.\n먼저 메뉴를 다른 카테고리로 이동하거나 삭제해주세요.`)
    return
  }
  
  if (!confirm(`"${category.name}" 카테고리를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.`)) {
    return
  }
  
  try {
    await menuApi.deleteCategory(category.id)
    alert('카테고리가 삭제되었습니다!')
    await loadCategories()
    emit('refresh')
    
  } catch (error) {
    console.error('카테고리 삭제 실패:', error)
    alert('카테고리 삭제에 실패했습니다: ' + error.message)
  }
}

// 카테고리 순서 이동
const moveCategory = async (categoryId, newIndex) => {
  if (newIndex < 0 || newIndex >= categories.value.length) return
  
  try {
    await menuApi.updateCategoryOrder(categoryId, newIndex + 1)
    await loadCategories()
    emit('refresh')
    
  } catch (error) {
    console.error('카테고리 순서 변경 실패:', error)
    alert('순서 변경에 실패했습니다: ' + error.message)
  }
}

// 모달 닫기
const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  editingCategory.value = null
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-manager {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  border: 1px solid #e5e7eb;
}

.manager-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 1rem;
}

.manager-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.add-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.add-btn:hover {
  background: #2563eb;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.no-categories {
  text-align: center;
  color: #6b7280;
  padding: 3rem;
  border: 1px dashed #d1d5db;
  border-radius: 6px;
  background: #fafafa;
  font-style: italic;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  transition: all 0.2s;
}

.category-item:hover {
  border-color: #3b82f6;
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.1);
}

.category-item.inactive {
  opacity: 0.6;
  background: #f9fafb;
}

.category-info {
  flex: 1;
}

.category-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.category-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.category-stats {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.menu-count {
  font-size: 13px;
  color: #6b7280;
}

.status-badge {
  padding: 0.125rem 0.5rem;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.status-badge.active {
  background: #dcfce7;
  color: #166534;
}

.status-badge.inactive {
  background: #fee2e2;
  color: #991b1b;
}

.category-order {
  font-size: 12px;
  color: #6b7280;
}

.category-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.order-controls {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.order-btn {
  background: none;
  border: 1px solid #d1d5db;
  padding: 0.25rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.order-btn:hover:not(:disabled) {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.order-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.action-controls {
  display: flex;
  gap: 0.5rem;
}

.edit-btn, .toggle-btn, .delete-btn {
  background: none;
  border: 1px solid #d1d5db;
  padding: 0.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.edit-btn:hover {
  background: #dbeafe;
  border-color: #3b82f6;
}

.toggle-btn:hover {
  background: #fef3c7;
  border-color: #f59e0b;
}

.delete-btn:hover {
  background: #fee2e2;
  border-color: #ef4444;
}

/* 반응형 */
@media (max-width: 768px) {
  .category-item {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .category-controls {
    justify-content: space-between;
  }
  
  .manager-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
}
</style>