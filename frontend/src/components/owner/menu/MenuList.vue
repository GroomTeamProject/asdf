<!-- src/components/owner/menu/MenuList.vue -->
<template>
  <div class="menu-management">
    <!-- 헤더 -->
    <div class="management-header">
      <h2>메뉴 관리</h2>
      <div class="header-actions">
        <button @click="showCategoryManager = !showCategoryManager" class="category-btn">
          📁 카테고리 관리
        </button>
        <button @click="showAddMenuModal = true" class="add-menu-btn">
          ➕ 메뉴 추가
        </button>
      </div>
    </div>

    <!-- 카테고리 관리 -->
    <div v-if="showCategoryManager" class="category-section">
      <MenuCategoryManager @refresh="loadData" />
    </div>

    <!-- 필터 및 검색 -->
    <div class="filters">
      <div class="filter-group">
        <label>카테고리:</label>
        <select v-model="selectedCategoryId" @change="loadMenus" class="filter-select">
          <option value="">전체 카테고리</option>
          <option v-for="category in categories" :key="category.id" :value="category.id">
            {{ category.name }} ({{ category.activeMenuCount }}/{{ category.menuCount }})
          </option>
        </select>
      </div>

      <div class="filter-group">
        <label>상태:</label>
        <select v-model="selectedStatus" @change="filterMenus" class="filter-select">
          <option value="">전체</option>
          <option value="AVAILABLE">판매중</option>
          <option value="SOLD_OUT">품절</option>
          <option value="HIDDEN">숨김</option>
        </select>
      </div>

      <div class="search-group">
        <input 
          v-model="searchKeyword" 
          @input="filterMenus"
          placeholder="메뉴명 검색..."
          class="search-input"
        >
      </div>
    </div>

    <!-- 메뉴 리스트 -->
    <div class="menu-grid">
      <div v-if="filteredMenus.length === 0" class="no-menus">
        <p v-if="allMenus.length === 0">등록된 메뉴가 없습니다.</p>
        <p v-else>검색 조건에 맞는 메뉴가 없습니다.</p>
      </div>

      <div 
        v-for="(menu, index) in filteredMenus" 
        :key="menu.id"
        class="menu-card"
        :class="{ 'inactive': menu.status !== 'AVAILABLE' }"
      >
        <!-- 메뉴 이미지 -->
        <div class="menu-image">
          <img 
            v-if="menu.imageUrl" 
            :src="menu.imageUrl" 
            :alt="menu.name"
            class="menu-img"
          >
          <div v-else class="no-image">
            <span>이미지 없음</span>
          </div>
          
          <!-- 이미지 업로드 버튼 -->
          <button @click="openImageUpload(menu)" class="image-upload-btn">
            📷
          </button>
        </div>

        <!-- 메뉴 정보 -->
        <div class="menu-info">
          <div class="menu-header">
            <h3 class="menu-name">{{ menu.name }}</h3>
            <div class="menu-badges">
              <span v-if="menu.isPopular" class="badge popular">인기</span>
              <span v-if="menu.isRecommended" class="badge recommended">추천</span>
            </div>
          </div>

          <p class="menu-description">{{ menu.description || '설명 없음' }}</p>
          
          <div class="menu-details">
            <div class="price">{{ formatPrice(menu.price) }}원</div>
            <div class="category">{{ menu.categoryName }}</div>
          </div>

          <div class="menu-status">
            <span :class="['status-badge', getStatusClass(menu.status)]">
              {{ getStatusText(menu.status) }}
            </span>
            <span class="order">순서: {{ menu.displayOrder }}</span>
          </div>

          <!-- 옵션 정보 -->
          <div v-if="menu.options && menu.options.length > 0" class="options-info">
            <small>옵션 {{ menu.options.length }}개</small>
          </div>
        </div>

        <!-- 메뉴 컨트롤 (순서 변경 버튼 추가) -->
        <div class="menu-controls">
          <!-- 순서 변경 버튼들 -->
          <div class="order-controls">
            <button 
              @click="moveMenuUp(menu, index)"
              :disabled="index === 0 || !canMoveUp(menu, index)"
              class="order-btn up"
              title="위로 이동"
            >
              ⬆️
            </button>
            <button 
              @click="moveMenuDown(menu, index)"
              :disabled="index === filteredMenus.length - 1 || !canMoveDown(menu, index)"
              class="order-btn down"
              title="아래로 이동"
            >
              ⬇️
            </button>
          </div>

          <!-- 기존 컨트롤 버튼들 -->
          <div class="action-controls">
            <button @click="editMenu(menu)" class="control-btn edit" title="수정">
              ✏️
            </button>
            <button @click="manageOptions(menu)" class="control-btn options" title="옵션 관리">
              ⚙️
            </button>
            <button @click="toggleMenuStatus(menu)" class="control-btn toggle" title="상태 변경">
              {{ menu.status === 'AVAILABLE' ? '⏸️' : '▶️' }}
            </button>
            <button @click="deleteMenu(menu)" class="control-btn delete" title="삭제">
              🗑️
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 순서 정규화 버튼 (개발자용) -->
    <div v-if="allMenus.length > 0" class="utility-section">
      <button @click="normalizeMenuOrders" class="normalize-btn">
        🔧 메뉴 순서 정규화
      </button>
      <small class="help-text">
        메뉴 순서를 1부터 연속적으로 정리합니다 (개발자용)
      </small>
    </div>

    <!-- 숨겨진 파일 입력 -->
    <input 
      ref="fileInput"
      type="file" 
      accept="image/*" 
      @change="handleImageUpload"
      style="display: none"
    >

    <!-- 모달들 -->
    <MenuModal
  v-if="showAddMenuModal || showEditMenuModal"
  :menu="editingMenu"
  :categories="categories"
  :is-edit="showEditMenuModal"
  @close="closeMenuModal"
  @save="saveMenu"
  @categoryAdded="handleCategoryAdded"
/>

    <MenuOptionsModal
      v-if="showOptionsModal"
      :menu="selectedMenu"
      @close="showOptionsModal = false"
      @refresh="loadMenus"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { menuApi } from '@/api/owner/menuApi'
import MenuCategoryManager from './MenuCategoryManager.vue'
import MenuModal from './MenuModal.vue'
import MenuOptionsModal from './MenuOptionsModal.vue'

// 상태 관리
const categories = ref([])
const allMenus = ref([])
const selectedCategoryId = ref('')
const selectedStatus = ref('')
const searchKeyword = ref('')
const showCategoryManager = ref(false)
const showAddMenuModal = ref(false)
const showEditMenuModal = ref(false)
const showOptionsModal = ref(false)
const editingMenu = ref(null)
const selectedMenu = ref(null)
const fileInput = ref(null)
const uploadingMenu = ref(null)

// 필터링된 메뉴 목록
const filteredMenus = computed(() => {
  let filtered = [...allMenus.value]

  // 상태 필터
  if (selectedStatus.value) {
    filtered = filtered.filter(menu => menu.status === selectedStatus.value)
  }

  // 검색 필터
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(menu => 
      menu.name.toLowerCase().includes(keyword) ||
      (menu.description && menu.description.toLowerCase().includes(keyword))
    )
  }

  // 정렬 (카테고리별, 순서별)
  filtered.sort((a, b) => {
    if (a.categoryName !== b.categoryName) {
      return a.categoryName.localeCompare(b.categoryName)
    }
    return (a.displayOrder || 0) - (b.displayOrder || 0)
  })

  return filtered
})

// 순서 변경 가능 여부 체크
const canMoveUp = (menu, index) => {
  if (index === 0) return false
  const prevMenu = filteredMenus.value[index - 1]
  return menu.categoryId === prevMenu.categoryId
}

const canMoveDown = (menu, index) => {
  if (index === filteredMenus.value.length - 1) return false
  const nextMenu = filteredMenus.value[index + 1]
  return menu.categoryId === nextMenu.categoryId
}

// 메뉴 위로 이동
const moveMenuUp = async (menu, currentIndex) => {
  if (currentIndex === 0) return
  
  const targetMenu = filteredMenus.value[currentIndex - 1]
  
  // 같은 카테고리인지 확인
  if (menu.categoryId !== targetMenu.categoryId) {
    alert('같은 카테고리 내에서만 순서 변경이 가능합니다.')
    return
  }
  
  await swapMenuOrder(menu, targetMenu)
}

// 메뉴 아래로 이동
const moveMenuDown = async (menu, currentIndex) => {
  if (currentIndex === filteredMenus.value.length - 1) return
  
  const targetMenu = filteredMenus.value[currentIndex + 1]
  
  // 같은 카테고리인지 확인
  if (menu.categoryId !== targetMenu.categoryId) {
    alert('같은 카테고리 내에서만 순서 변경이 가능합니다.')
    return
  }
  
  await swapMenuOrder(menu, targetMenu)
}

// 두 메뉴의 순서를 바꾸는 함수
const swapMenuOrder = async (menu1, menu2) => {
  try {
    const newPosition1 = menu2.displayOrder
    const newPosition2 = menu1.displayOrder
    
    // 첫 번째 메뉴 순서 변경
    await menuApi.updateMenuOrder(
      menu1.id, 
      newPosition1, 
      menu1.categoryId, 
      false // 카테고리 내 순서 변경
    )
    
    // 잠시 대기
    await new Promise(resolve => setTimeout(resolve, 100))
    
    // 두 번째 메뉴 순서 변경
    await menuApi.updateMenuOrder(
      menu2.id, 
      newPosition2, 
      menu2.categoryId, 
      false
    )
    
    await loadMenus()
    
  } catch (error) {
    console.error('메뉴 순서 변경 실패:', error)
    alert('순서 변경에 실패했습니다: ' + error.message)
  }
}

// 순서 정규화 (개발자용 유틸리티)

const handleCategoryAdded = (newCategory) => {
  // 카테고리 목록에 새 카테고리 추가
  categories.value.push(newCategory)
  
  // 카테고리 목록 재정렬
  categories.value.sort((a, b) => {
    if (a.isActive && !b.isActive) return -1
    if (!a.isActive && b.isActive) return 1
    return a.displayOrder - b.displayOrder
  })
  
  console.log('새 카테고리 추가됨:', newCategory)
}

// 데이터 로딩
const loadData = async () => {
  await Promise.all([loadCategories(), loadMenus()])
}

const loadCategories = async () => {
  try {
    categories.value = await menuApi.getCategories()
    categories.value.sort((a, b) => a.displayOrder - b.displayOrder)
  } catch (error) {
    console.error('카테고리 로드 실패:', error)
  }
}

const loadMenus = async () => {
  try {
    allMenus.value = await menuApi.getMenus(selectedCategoryId.value || null)
  } catch (error) {
    console.error('메뉴 로드 실패:', error)
  }
}

const filterMenus = () => {
  // computed에서 자동으로 처리됨
}

// 메뉴 관리
const editMenu = (menu) => {
  editingMenu.value = { ...menu }
  showEditMenuModal.value = true
}

const saveMenu = async (menuData) => {
  try {
    if (showEditMenuModal.value) {
      await menuApi.updateMenu(editingMenu.value.id, menuData)
      alert('메뉴가 수정되었습니다!')
    } else {
      await menuApi.createMenu(menuData)
      alert('메뉴가 추가되었습니다!')
    }
    
    closeMenuModal()
    await loadMenus()
    
  } catch (error) {
    console.error('메뉴 저장 실패:', error)
    alert('메뉴 저장에 실패했습니다: ' + error.message)
  }
}

const deleteMenu = async (menu) => {
  if (!confirm(`"${menu.name}" 메뉴를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.`)) {
    return
  }
  
  try {
    await menuApi.deleteMenu(menu.id)
    alert('메뉴가 삭제되었습니다!')
    await loadMenus()
    
  } catch (error) {
    console.error('메뉴 삭제 실패:', error)
    alert('메뉴 삭제에 실패했습니다: ' + error.message)
  }
}

const toggleMenuStatus = async (menu) => {
  const newStatus = menu.status === 'AVAILABLE' ? 'HIDDEN' : 'AVAILABLE'
  const action = newStatus === 'AVAILABLE' ? '판매 시작' : '판매 중단'
  
  if (!confirm(`"${menu.name}" 메뉴의 판매를 ${action}하시겠습니까?`)) {
    return
  }
  
  try {
    await menuApi.updateMenuStatus(menu.id, { 
      status: newStatus,
      reason: `관리자에 의한 ${action}`
    })
    
    alert(`메뉴 ${action}이 완료되었습니다!`)
    await loadMenus()
    
  } catch (error) {
    console.error('메뉴 상태 변경 실패:', error)
    alert('상태 변경에 실패했습니다: ' + error.message)
  }
}

// 이미지 관리
const openImageUpload = (menu) => {
  uploadingMenu.value = menu
  fileInput.value.click()
}

const handleImageUpload = async (event) => {
  const file = event.target.files[0]
  if (!file || !uploadingMenu.value) return
  
  // 파일 크기 체크 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert('파일 크기는 5MB 이하여야 합니다.')
    return
  }
  
  // 파일 형식 체크
  if (!file.type.startsWith('image/')) {
    alert('이미지 파일만 업로드 가능합니다.')
    return
  }
  
  try {
    const imageUrl = await menuApi.uploadMenuImage(uploadingMenu.value.id, file)
    
    // 메뉴 정보 업데이트 (이미지 URL만)
    await menuApi.updateMenu(uploadingMenu.value.id, {
      imageUrl: imageUrl
    })
    
    alert('메뉴 이미지가 업로드되었습니다!')
    await loadMenus()
    
  } catch (error) {
    console.error('이미지 업로드 실패:', error)
    alert('이미지 업로드에 실패했습니다: ' + error.message)
  } finally {
    uploadingMenu.value = null
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

// 고급 순서 변경 함수 (특정 위치로 이동)
const moveMenuToPosition = async (menu, newPosition) => {
  try {
    await menuApi.updateMenuOrder(
      menu.id,
      newPosition,
      selectedCategoryId.value || menu.categoryId,
      !selectedCategoryId.value // 전체 보기일 때는 글로벌 순서
    )
    
    await loadMenus()
    
  } catch (error) {
    console.error('메뉴 위치 변경 실패:', error)
    alert('위치 변경에 실패했습니다: ' + error.message)
  }
}

// 순서 정규화 (개발자용 유틸리티)
const normalizeMenuOrders = async () => {
  if (!confirm('메뉴 순서를 정규화하시겠습니까?\n현재 순서를 유지하면서 1부터 연속적으로 정리됩니다.')) {
    return
  }
  
  try {
    // 카테고리별로 그룹핑하여 순서 정규화
    const categorizedMenus = {}
    
    allMenus.value.forEach(menu => {
      if (!categorizedMenus[menu.categoryId]) {
        categorizedMenus[menu.categoryId] = []
      }
      categorizedMenus[menu.categoryId].push(menu)
    })
    
    // 각 카테고리별로 순서 정규화
    for (const categoryId in categorizedMenus) {
      const menus = categorizedMenus[categoryId]
      menus.sort((a, b) => a.displayOrder - b.displayOrder)
      
      for (let i = 0; i < menus.length; i++) {
        const menu = menus[i]
        if (menu.displayOrder !== i + 1) {
          await menuApi.updateMenu(menu.id, {
            displayOrder: i + 1
          })
          await new Promise(resolve => setTimeout(resolve, 50)) // 짧은 대기
        }
      }
    }
    
    await loadMenus()
    alert('메뉴 순서 정규화가 완료되었습니다!')
    
  } catch (error) {
    console.error('순서 정규화 실패:', error)
    alert('순서 정규화에 실패했습니다: ' + error.message)
  }
}

// 옵션 관리
const manageOptions = (menu) => {
  selectedMenu.value = menu
  showOptionsModal.value = true
}

// 모달 관리
const closeMenuModal = () => {
  showAddMenuModal.value = false
  showEditMenuModal.value = false
  editingMenu.value = null
}

// 유틸리티 함수
const formatPrice = (price) => {
  return Number(price).toLocaleString()
}

const getStatusClass = (status) => {
  switch (status) {
    case 'AVAILABLE': return 'available'
    case 'SOLD_OUT': return 'sold-out'
    case 'HIDDEN': return 'hidden'
    default: return 'unknown'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'AVAILABLE': return '판매중'
    case 'SOLD_OUT': return '품절'
    case 'HIDDEN': return '숨김'
    default: return '알 수 없음'
  }
}

// 컴포넌트 마운트
onMounted(() => {
  loadData()
})

// 부모 컴포넌트에서 호출할 수 있도록 expose
defineExpose({
  refresh: loadData
})
</script>

<style scoped>
.menu-management {
  padding: 1.5rem;
}
.menu-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
  gap: 0.75rem;
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
  width: 32px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.order-btn:hover:not(:disabled) {
  background: #f3f4f6;
  border-color: #9ca3af;
  transform: scale(1.05);
}

.order-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.action-controls {
  display: flex;
  gap: 0.5rem;
}

.control-btn {
  background: none;
  border: 1px solid #d1d5db;
  padding: 0.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.control-btn:hover {
  transform: scale(1.05);
}

.control-btn.edit:hover {
  background: #dbeafe;
  border-color: #3b82f6;
}

.control-btn.options:hover {
  background: #f3e8ff;
  border-color: #8b5cf6;
}

.control-btn.toggle:hover {
  background: #fef3c7;
  border-color: #f59e0b;
}

.control-btn.delete:hover {
  background: #fee2e2;
  border-color: #ef4444;
}

.utility-section {
  margin-top: 2rem;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  text-align: center;
}

.normalize-btn {
  background: #6b7280;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: background-color 0.2s;
  margin-bottom: 0.5rem;
}

.normalize-btn:hover {
  background: #4b5563;
}

.utility-section .help-text {
  display: block;
  font-size: 11px;
  color: #6b7280;
}

.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 1rem;
}

.management-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
}

.category-btn, .add-menu-btn {
  padding: 0.75rem 1rem;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid;
}

.category-btn {
  background: white;
  border-color: #d1d5db;
  color: #374151;
}

.category-btn:hover {
  background: #f9fafb;
}

.add-menu-btn {
  background: #3b82f6;
  border-color: #3b82f6;
  color: white;
}

.add-menu-btn:hover {
  background: #2563eb;
}

.category-section {
  margin-bottom: 2rem;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  flex-wrap: wrap;
}

.filter-group, .search-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-group label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  min-width: 60px;
}

.filter-select, .search-input {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  min-width: 150px;
}

.search-input {
  min-width: 200px;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.no-menus {
  grid-column: 1 / -1;
  text-align: center;
  padding: 4rem 2rem;
  color: #6b7280;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px dashed #d1d5db;
}

.menu-card {
  background: white;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  overflow: hidden;
  transition: all 0.3s;
  position: relative;
}

.menu-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.menu-card.inactive {
  opacity: 0.7;
  background: #f9fafb;
}

.menu-image {
  position: relative;
  height: 200px;
  background: #f3f4f6;
  overflow: hidden;
}

.menu-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-image {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #6b7280;
  font-size: 14px;
}

.image-upload-btn {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  border: none;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.2s;
}

.image-upload-btn:hover {
  background: rgba(0, 0, 0, 0.8);
}

.menu-info {
  padding: 1rem;
}

.menu-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}

.menu-name {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
  flex: 1;
}

.menu-badges {
  display: flex;
  gap: 0.25rem;
  flex-shrink: 0;
  margin-left: 0.5rem;
}

.badge {
  padding: 0.125rem 0.5rem;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 600;
}

.badge.popular {
  background: #fef3c7;
  color: #92400e;
}

.badge.recommended {
  background: #dbeafe;
  color: #1e40af;
}

.menu-description {
  font-size: 14px;
  color: #6b7280;
  margin: 0.5rem 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.menu-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 1rem 0;
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: #059669;
}

.category {
  font-size: 12px;
  color: #6b7280;
  background: #f3f4f6;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
}

.menu-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-badge.available {
  background: #dcfce7;
  color: #166534;
}

.status-badge.sold-out {
  background: #fef3c7;
  color: #92400e;
}

.status-badge.hidden {
  background: #fee2e2;
  color: #991b1b;
}

.order {
  font-size: 12px;
  color: #6b7280;
}

.options-info {
  margin-top: 0.5rem;
  color: #6b7280;
}

.menu-controls {
  display: flex;
  justify-content: space-around;
  padding: 0.75rem;
  background: #f9fafb;
  border-top: 1px solid #e5e7eb;
}

.control-btn {
  background: none;
  border: 1px solid #d1d5db;
  padding: 0.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.control-btn:hover {
  transform: scale(1.05);
}

.control-btn.edit:hover {
  background: #dbeafe;
  border-color: #3b82f6;
}

.control-btn.options:hover {
  background: #f3e8ff;
  border-color: #8b5cf6;
}

.control-btn.toggle:hover {
  background: #fef3c7;
  border-color: #f59e0b;
}

.control-btn.delete:hover {
  background: #fee2e2;
  border-color: #ef4444;
}

@media (max-width: 768px) {
  .management-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .filters {
    flex-direction: column;
    gap: 1rem;
  }
  
  .filter-group, .search-group {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-group label {
    min-width: auto;
  }
  
  .filter-select, .search-input {
    min-width: auto;
  }
  
  .menu-grid {
    grid-template-columns: 1fr;
  }
  
  .menu-header {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .menu-badges {
    margin-left: 0;
    align-self: flex-start;
  }

  /* 순서 변경 관련 반응형 스타일 추가 */
  .menu-controls {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .order-controls {
    flex-direction: row;
    justify-content: center;
  }
  
  .action-controls {
    justify-content: center;
    flex-wrap: wrap;
  }
}
</style>