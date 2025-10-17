<template>
  <!-- 모달 오버레이 -->
  <div class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h2>{{ menu.name }} - 옵션 관리</h2>
        <button @click="$emit('close')" class="close-btn">&times;</button>
      </div>
      
      <div class="modal-content">
        <!-- 옵션 그룹 목록 -->
        <div class="option-groups-section">
          <div class="section-header">
            <h3>옵션 그룹</h3>
            <button @click="showAddGroupForm = true" class="add-group-btn">
              + 그룹 추가
            </button>
          </div>
          
          <!-- 옵션 그룹 추가 폼 - 상세 설정 -->
          <div v-if="showAddGroupForm" class="add-group-form">
            <!-- 그룹명 입력 -->
            <div class="form-section">
              <label class="form-label">그룹명 *</label>
              <input 
                v-model="newGroup.name" 
                placeholder="그룹명 (예: 사이즈, 토핑, 매운맛 정도)" 
                class="form-input full-width"
              />
            </div>
            
            <!-- 선택 방식 설정 -->
            <div class="form-section">
              <label class="form-label">선택 방식</label>
              <div class="radio-group">
                <label class="radio-option">
                  <input 
                    type="radio" 
                    v-model="newGroup.type" 
                    value="SINGLE" 
                    name="groupType"
                  />
                  <span class="radio-text">
                    <strong>단일 선택</strong>
                    <small>하나만 선택 가능 (예: 사이즈 - 소/중/대 중 하나)</small>
                  </span>
                </label>
                
                <label class="radio-option">
                  <input 
                    type="radio" 
                    v-model="newGroup.type" 
                    value="MULTIPLE" 
                    name="groupType"
                  />
                  <span class="radio-text">
                    <strong>다중 선택</strong>
                    <small>여러 개 선택 가능 (예: 토핑 - 치즈, 베이컨 등 복수 선택)</small>
                  </span>
                </label>
              </div>
            </div>
            
            <!-- 필수 선택 여부 -->
            <div class="form-section">
              <label class="checkbox-container">
                <input 
                  type="checkbox" 
                  v-model="newGroup.isRequired"
                  class="checkbox-input"
                />
                <span class="checkbox-text">
                  <strong>필수 선택</strong>
                  <small>고객이 반드시 선택해야 하는 옵션입니다</small>
                </span>
              </label>
            </div>
            
            <!-- 옵션 항목 미리 설정 -->
            <div class="form-section">
              <div class="section-title">
                <label class="form-label">옵션 항목 미리 설정 (선택사항)</label>
                <button 
                  type="button"
                  @click="addPreOption" 
                  class="add-option-btn"
                >
                  + 옵션 추가
                </button>
              </div>
              
              <div v-if="newGroup.preOptions.length === 0" class="no-pre-options">
                그룹 생성 후 개별적으로 옵션을 추가하거나, 여기서 미리 설정할 수 있습니다.
              </div>
              
              <div 
                v-for="(option, index) in newGroup.preOptions" 
                :key="index"
                class="pre-option-row"
              >
                <input 
                  v-model="option.name" 
                  placeholder="옵션명 (예: 라지, 치즈 추가)"
                  class="pre-option-name"
                />
                <input 
                  v-model.number="option.additionalPrice" 
                  type="number"
                  placeholder="추가금액"
                  min="0"
                  class="pre-option-price"
                />
                <button 
                  type="button"
                  @click="removePreOption(index)" 
                  class="remove-option-btn"
                >
                  ✕
                </button>
              </div>
            </div>
            
            <!-- 저장/취소 버튼 -->
            <div class="form-buttons">
              <button type="button" @click="cancelAddGroup" class="cancel-btn">
                취소
              </button>
              <button type="button" @click="addOptionGroup" class="save-btn">
                {{ editingGroup ? '수정' : '그룹 생성' }}
              </button>
            </div>
          </div>
          
          <!-- 기존 옵션 그룹들 -->
          <div v-if="optionGroups.length === 0 && !showAddGroupForm" class="no-groups">
            옵션 그룹이 없습니다. '+ 그룹 추가'를 클릭해서 새 그룹을 만들어보세요.
          </div>
          
          <div 
    v-for="(group, groupIndex) in optionGroups" 
    :key="group.id"
    class="option-group-card"
  >
    <div class="group-header">
      <div class="group-info">
        <h4>{{ group.name }}</h4>
        <div class="group-meta">
          <span class="type-badge" :class="group.type.toLowerCase()">
            {{ group.type === 'SINGLE' ? '단일선택' : '다중선택' }}
          </span>
          <span v-if="group.isRequired" class="required-badge">필수</span>
        </div>
      </div>
      <div class="group-controls">
        <!-- 👇 순서 조정 버튼 추가 -->
        <div class="order-controls">
          <button 
            @click="moveGroupUp(groupIndex)" 
            :disabled="groupIndex === 0"
            class="order-btn up-btn"
            title="위로 이동"
          >
            ↑
          </button>
          <button 
            @click="moveGroupDown(groupIndex)" 
            :disabled="groupIndex === optionGroups.length - 1"
            class="order-btn down-btn"
            title="아래로 이동"
          >
            ↓
          </button>
        </div>
        
        <button @click="editGroup(group)" class="edit-btn">✏️</button>
        <button @click="deleteGroup(group)" class="delete-btn">🗑️</button>
      </div>
    </div>
            
            <!-- 옵션 아이템들 -->
    <div class="option-items">
      <div class="items-header">
        <span>옵션 항목 ({{ group.items?.length || 0 }}개)</span>
        <button @click="addOptionItem(group)" class="add-item-btn">+ 항목 추가</button>
      </div>
      
      <div v-if="!group.items || group.items.length === 0" class="no-items">
        옵션 항목이 없습니다.
      </div>
      
      <div 
        v-for="(item, itemIndex) in group.items" 
        :key="item.id"
        class="option-item"
        :class="{ inactive: !item.isActive }"
      >
        <div class="item-info">
          <span class="item-name">{{ item.name }}</span>
          <span class="item-price">+{{ formatPrice(item.additionalPrice) }}원</span>
        </div>
        <div class="item-controls">
          <!-- 👇 옵션 아이템 순서 조정 버튼 추가 -->
          <div class="order-controls item-order">
            <button 
              @click="moveItemUp(groupIndex, itemIndex)" 
              :disabled="itemIndex === 0"
              class="order-btn up-btn small"
              title="위로 이동"
            >
              ↑
            </button>
            <button 
              @click="moveItemDown(groupIndex, itemIndex)" 
              :disabled="itemIndex === group.items.length - 1"
              class="order-btn down-btn small"
              title="아래로 이동"
            >
              ↓
            </button>
          </div>
                  <button @click="editOptionItem(group, item)" class="edit-item-btn">✏️</button>
                  <button @click="toggleItemStatus(group, item)" class="toggle-item-btn">
                    {{ item.isActive ? '⏸️' : '▶️' }}
                  </button>
                  <button @click="deleteOptionItem(group, item)" class="delete-item-btn">🗑️</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button @click="$emit('close')" class="close-modal-btn">완료</button>
      </div>
    </div>
  </div>
  
  <!-- 옵션 아이템 추가/수정 모달 -->
  <div v-if="showItemModal" class="modal-overlay" @click="closeItemModal">
    <div class="item-modal" @click.stop>
      <div class="item-modal-header">
        <h3>{{ editingItem ? '옵션 수정' : '옵션 추가' }}</h3>
        <button @click="closeItemModal" class="close-btn">&times;</button>
      </div>
      
      <div class="item-modal-content">
        <div class="form-group">
          <label>옵션명 *</label>
          <input 
            v-model="itemForm.name" 
            placeholder="예: 라지 사이즈, 치즈 추가"
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <label>추가 금액 (원)</label>
          <input 
            v-model.number="itemForm.additionalPrice" 
            type="number"
            min="0"
            placeholder="0"
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <label class="checkbox-label">
            <input v-model="itemForm.isActive" type="checkbox" />
            활성화
          </label>
        </div>
      </div>
      
      <div class="item-modal-footer">
        <button @click="closeItemModal" class="cancel-btn">취소</button>
        <button @click="saveOptionItem" class="save-btn">
          {{ editingItem ? '수정' : '추가' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { menuApi } from '@/api/owner/menuApi'

const props = defineProps({
  menu: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'refresh'])

// 상태 관리
const optionGroups = ref([])
const showAddGroupForm = ref(false)
const showItemModal = ref(false)
const editingGroup = ref(null)
const editingItem = ref(null)
const currentGroup = ref(null)

// 폼 데이터
const newGroup = ref({
  name: '',
  type: 'SINGLE',
  isRequired: false,
  preOptions: []
})

const itemForm = ref({
  name: '',
  additionalPrice: 0,
  isActive: true
})

// 옵션 미리 설정 관련 함수들
const addPreOption = () => {
  newGroup.value.preOptions.push({
    name: '',
    additionalPrice: 0,
    isActive: true
  })
}

const removePreOption = (index) => {
  newGroup.value.preOptions.splice(index, 1)
}

// 데이터 로딩
const loadOptionGroups = async () => {
  try {
    optionGroups.value = await menuApi.getOptionGroups(props.menu.id)
    
    // 각 그룹의 옵션 아이템들도 로드
    for (const group of optionGroups.value) {
      const items = await menuApi.getOptionItems(props.menu.id, group.id)
      group.items = items
    }
  } catch (error) {
    console.error('옵션 그룹 로드 실패:', error)
  }
}

// 👇 새로운 순서 변경 함수들 (정규화 API 대신 순서 변경 API 사용)
const moveGroupUp = async (index) => {
  console.log('🔄 moveGroupUp 함수 호출됨, index:', index)
  
  if (index === 0) {
    console.log('⚠️ 이미 첫 번째 그룹입니다')
    return
  }
  
  try {
    const targetGroup = optionGroups.value[index]
    const newPosition = index // 위로 이동하므로 현재 인덱스가 새로운 위치 (1부터 시작하므로)
    
    console.log('📝 그룹 위로 이동:', {
      groupName: targetGroup.name,
      groupId: targetGroup.id,
      currentPosition: index + 1,
      newPosition: newPosition
    })
    
    // 로컬에서 순서 변경 (UI 즉시 반영)
    const temp = optionGroups.value[index]
    optionGroups.value[index] = optionGroups.value[index - 1]
    optionGroups.value[index - 1] = temp
    
    // 🌐 새로운 순서 변경 API 호출
    await menuApi.updateOptionGroupOrder(props.menu.id, targetGroup.id, {
      newPosition: newPosition,
      reason: '관리자에 의한 순서 조정'
    })
    
    console.log('✅ 옵션 그룹 순서 변경 완료')
    
    // 최신 데이터로 다시 로드
    await loadOptionGroups()
    
  } catch (error) {
    console.error('❌ 옵션 그룹 순서 변경 실패:', error)
    alert('순서 변경에 실패했습니다: ' + (error.response?.data?.message || error.message))
    
    // 실패 시 원복
    await loadOptionGroups()
  }
}

const moveGroupDown = async (index) => {
  console.log('🔄 moveGroupDown 함수 호출됨, index:', index)
  
  if (index === optionGroups.value.length - 1) {
    console.log('⚠️ 이미 마지막 그룹입니다')
    return
  }
  
  try {
    const targetGroup = optionGroups.value[index]
    const newPosition = index + 2 // 아래로 이동하므로 +2 (1부터 시작)
    
    console.log('📝 그룹 아래로 이동:', {
      groupName: targetGroup.name,
      groupId: targetGroup.id,
      currentPosition: index + 1,
      newPosition: newPosition
    })
    
    // 로컬에서 순서 변경 (UI 즉시 반영)
    const temp = optionGroups.value[index]
    optionGroups.value[index] = optionGroups.value[index + 1]
    optionGroups.value[index + 1] = temp
    
    // 🌐 새로운 순서 변경 API 호출
    await menuApi.updateOptionGroupOrder(props.menu.id, targetGroup.id, {
      newPosition: newPosition,
      reason: '관리자에 의한 순서 조정'
    })
    
    console.log('✅ 옵션 그룹 순서 변경 완료')
    
    // 최신 데이터로 다시 로드
    await loadOptionGroups()
    
  } catch (error) {
    console.error('❌ 옵션 그룹 순서 변경 실패:', error)
    alert('순서 변경에 실패했습니다: ' + (error.response?.data?.message || error.message))
    
    // 실패 시 원복
    await loadOptionGroups()
  }
}

const moveItemUp = async (groupIndex, itemIndex) => {
  console.log('🔄 moveItemUp 함수 호출됨, groupIndex:', groupIndex, 'itemIndex:', itemIndex)
  
  if (itemIndex === 0) {
    console.log('⚠️ 이미 첫 번째 아이템입니다')
    return
  }
  
  try {
    const group = optionGroups.value[groupIndex]
    const targetItem = group.items[itemIndex]
    const newPosition = itemIndex // 위로 이동하므로 현재 인덱스가 새로운 위치 (1부터 시작하므로)
    
    console.log('📝 아이템 위로 이동:', {
      groupName: group.name,
      itemName: targetItem.name,
      itemId: targetItem.id,
      currentPosition: itemIndex + 1,
      newPosition: newPosition
    })
    
    // 로컬에서 순서 변경 (UI 즉시 반영)
    const temp = group.items[itemIndex]
    group.items[itemIndex] = group.items[itemIndex - 1]
    group.items[itemIndex - 1] = temp
    
    // 🌐 새로운 순서 변경 API 호출
    await menuApi.updateOptionItemOrder(props.menu.id, group.id, targetItem.id, {
      newPosition: newPosition,
      reason: '관리자에 의한 순서 조정'
    })
    
    console.log('✅ 옵션 아이템 순서 변경 완료')
    
    // 최신 데이터로 다시 로드
    await loadOptionGroups()
    
  } catch (error) {
    console.error('❌ 옵션 아이템 순서 변경 실패:', error)
    alert('순서 변경에 실패했습니다: ' + (error.response?.data?.message || error.message))
    
    // 실패 시 원복
    await loadOptionGroups()
  }
}

const moveItemDown = async (groupIndex, itemIndex) => {
  console.log('🔄 moveItemDown 함수 호출됨, groupIndex:', groupIndex, 'itemIndex:', itemIndex)
  
  const group = optionGroups.value[groupIndex]
  if (itemIndex === group.items.length - 1) {
    console.log('⚠️ 이미 마지막 아이템입니다')
    return
  }
  
  try {
    const targetItem = group.items[itemIndex]
    const newPosition = itemIndex + 2 // 아래로 이동하므로 +2 (1부터 시작)
    
    console.log('📝 아이템 아래로 이동:', {
      groupName: group.name,
      itemName: targetItem.name,
      itemId: targetItem.id,
      currentPosition: itemIndex + 1,
      newPosition: newPosition
    })
    
    // 로컬에서 순서 변경 (UI 즉시 반영)
    const temp = group.items[itemIndex]
    group.items[itemIndex] = group.items[itemIndex + 1]
    group.items[itemIndex + 1] = temp
    
    // 🌐 새로운 순서 변경 API 호출
    await menuApi.updateOptionItemOrder(props.menu.id, group.id, targetItem.id, {
      newPosition: newPosition,
      reason: '관리자에 의한 순서 조정'
    })
    
    console.log('✅ 옵션 아이템 순서 변경 완료')
    
    // 최신 데이터로 다시 로드
    await loadOptionGroups()
    
  } catch (error) {
    console.error('❌ 옵션 아이템 순서 변경 실패:', error)
    alert('순서 변경에 실패했습니다: ' + (error.response?.data?.message || error.message))
    
    // 실패 시 원복
    await loadOptionGroups()
  }
}

// 옵션 그룹 관리
const addOptionGroup = async () => {
  if (!newGroup.value.name.trim()) {
    alert('그룹명을 입력해주세요.')
    return
  }

  if (newGroup.value.preOptions.length === 0) {
    alert('옵션 항목을 최소 1개 이상 추가해주세요.')
    return
  }

  const validOptions = newGroup.value.preOptions.filter(option => 
    option.name && option.name.trim()
  )
  
  if (validOptions.length === 0) {
    alert('유효한 옵션 항목을 최소 1개 이상 입력해주세요.')
    return
  }
  
  try {
    const items = validOptions.map((option, index) => ({
      name: option.name.trim(),
      additionalPrice: option.additionalPrice || 0,
      displayOrder: index,
      isActive: option.isActive !== undefined ? option.isActive : true
    }))
    
    const groupData = {
      name: newGroup.value.name.trim(),
      type: newGroup.value.type,
      isRequired: newGroup.value.isRequired || false,
      displayOrder: 0,
      items: items
    }
    
    const createdGroup = await menuApi.createOptionGroup(props.menu.id, groupData)
    alert('옵션 그룹이 추가되었습니다!')
    cancelAddGroup()
    await loadOptionGroups()
    
  } catch (error) {
    console.error('❌ 옵션 그룹 추가 실패:', error)
    let errorMessage = '옵션 그룹 추가에 실패했습니다.'
    if (error.response?.data?.message) {
      errorMessage += '\n' + error.response.data.message
    } else if (error.response?.data?.errors) {
      const errors = error.response.data.errors
      errorMessage += '\n' + errors.map(err => err.defaultMessage).join('\n')
    }
    alert(errorMessage)
  }
}

const cancelAddGroup = () => {
  showAddGroupForm.value = false
  editingGroup.value = null
  newGroup.value = {
    name: '',
    type: 'SINGLE',
    isRequired: false,
    preOptions: []
  }
}

const editGroup = (group) => {
  editingGroup.value = group
  newGroup.value = {
    name: group.name,
    type: group.type,
    isRequired: group.isRequired,
    preOptions: []
  }
  showAddGroupForm.value = true
}

const deleteGroup = async (group) => {
  if (!confirm(`"${group.name}" 그룹을 삭제하시겠습니까?\n포함된 모든 옵션이 함께 삭제됩니다.`)) {
    return
  }
  
  try {
    await menuApi.deleteOptionGroup(props.menu.id, group.id)
    alert('옵션 그룹이 삭제되었습니다!')
    await loadOptionGroups()
    
  } catch (error) {
    console.error('옵션 그룹 삭제 실패:', error)
    alert('옵션 그룹 삭제에 실패했습니다: ' + error.message)
  }
}

// 옵션 아이템 관리
const addOptionItem = (group) => {
  currentGroup.value = group
  editingItem.value = null
  itemForm.value = {
    name: '',
    additionalPrice: 0,
    isActive: true
  }
  showItemModal.value = true
}

const editOptionItem = (group, item) => {
  currentGroup.value = group
  editingItem.value = item
  itemForm.value = {
    name: item.name,
    additionalPrice: item.additionalPrice,
    isActive: item.isActive
  }
  showItemModal.value = true
}

const saveOptionItem = async () => {
  if (!itemForm.value.name.trim()) {
    alert('옵션명을 입력해주세요.')
    return
  }
  
  try {
    if (editingItem.value) {
      await menuApi.updateOptionItem(
        props.menu.id, 
        currentGroup.value.id, 
        editingItem.value.id, 
        itemForm.value
      )
      alert('옵션이 수정되었습니다!')
    } else {
      await menuApi.createOptionItem(
        props.menu.id, 
        currentGroup.value.id, 
        itemForm.value
      )
      alert('옵션이 추가되었습니다!')
    }
    
    closeItemModal()
    await loadOptionGroups()
    
  } catch (error) {
    console.error('옵션 저장 실패:', error)
    alert('옵션 저장에 실패했습니다: ' + error.message)
  }
}

const deleteOptionItem = async (group, item) => {
  if (!confirm(`"${item.name}" 옵션을 삭제하시겠습니까?`)) {
    return
  }
  
  try {
    await menuApi.deleteOptionItem(props.menu.id, group.id, item.id)
    alert('옵션이 삭제되었습니다!')
    await loadOptionGroups()
    
  } catch (error) {
    console.error('옵션 삭제 실패:', error)
    alert('옵션 삭제에 실패했습니다: ' + error.message)
  }
}

const toggleItemStatus = async (group, item) => {
  try {
    await menuApi.updateOptionItemStatus(props.menu.id, group.id, item.id, {
      isActive: !item.isActive,
      reason: '관리자에 의한 상태 변경'
    })
    
    await loadOptionGroups()
    
  } catch (error) {
    console.error('옵션 상태 변경 실패:', error)
    alert('옵션 상태 변경에 실패했습니다: ' + error.message)
  }
}

const closeItemModal = () => {
  showItemModal.value = false
  editingItem.value = null
  currentGroup.value = null
}

const handleOverlayClick = () => {
  emit('close')
}

// 유틸리티
const formatPrice = (price) => {
  return Number(price).toLocaleString()
}

// 컴포넌트 마운트
onMounted(() => {
  loadOptionGroups()
})
</script>

<style scoped>
/* 기본 모달 스타일 */
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
  z-index: 1000;
  padding: 1rem;
}

.modal-container {
  background: white;
  border-radius: 12px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #111827;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6b7280;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-content {
  flex: 1;
  overflow-y: auto;
  padding: 1.5rem;
  max-height: calc(90vh - 160px); /* 헤더/푸터 공간 제외 */
}
/* 순서 조정 버튼 스타일 */
.order-controls {
  display: flex;
  flex-direction: column;
  gap: 2px;
  margin-right: 0.5rem;
}

.order-controls.item-order {
  flex-direction: row;
  gap: 2px;
}

.order-btn {
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  padding: 0.25rem;
  border-radius: 3px;
  cursor: pointer;
  font-size: 10px;
  font-weight: bold;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.order-btn.small {
  width: 18px;
  height: 18px;
  font-size: 9px;
}

.order-btn:hover:not(:disabled) {
  background: #e5e7eb;
  border-color: #9ca3af;
}

.order-btn:disabled {
  background: #f9fafb;
  color: #d1d5db;
  cursor: not-allowed;
  border-color: #e5e7eb;
}

.up-btn:hover:not(:disabled) {
  background: #dbeafe;
  border-color: #3b82f6;
  color: #1e40af;
}

.down-btn:hover:not(:disabled) {
  background: #fef3c7;
  border-color: #f59e0b;
  color: #92400e;
}

/* 그룹 컨트롤 레이아웃 조정 */
.group-controls {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

/* 아이템 컨트롤 레이아웃 조정 */
.item-controls {
  display: flex;
  gap: 0.25rem;
  align-items: center;
}

/* 스크롤바 스타일링 */
.modal-content::-webkit-scrollbar {
  width: 6px;
}

.modal-content::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.modal-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 반응형 - 모바일에서 순서 버튼 크기 조정 */
@media (max-width: 768px) {
  .order-controls {
    flex-direction: row;
  }
  
  .order-btn {
    width: 24px;
    height: 24px;
    font-size: 12px;
  }
  
  .order-btn.small {
    width: 20px;
    height: 20px;
    font-size: 10px;
  }
  
  .modal-content {
    max-height: calc(95vh - 120px);
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.section-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #374151;
}

.add-group-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
}

.add-group-btn:hover {
  background: #2563eb;
}

/* 상세 폼 스타일 */
.add-group-form {
  background: #f9fafb;
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  margin-bottom: 1.5rem;
}

.form-section {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.5rem;
  font-size: 14px;
}

.form-input {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
}

.full-width {
  width: 100%;
}

.radio-group {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.radio-option {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.radio-option:hover {
  background: #f9fafb;
  border-color: #3b82f6;
}

.radio-option input[type="radio"] {
  margin-top: 0.25rem;
}

.radio-text {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.radio-text strong {
  color: #111827;
  font-size: 14px;
}

.radio-text small {
  color: #6b7280;
  font-size: 12px;
}

.checkbox-container {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.checkbox-container:hover {
  background: #f9fafb;
  border-color: #10b981;
}

.checkbox-input {
  margin-top: 0.25rem;
}

.checkbox-text {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.checkbox-text strong {
  color: #111827;
  font-size: 14px;
}

.checkbox-text small {
  color: #6b7280;
  font-size: 12px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.add-option-btn {
  background: #8b5cf6;
  color: white;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.add-option-btn:hover {
  background: #7c3aed;
}

.no-pre-options {
  padding: 1rem;
  background: #f8fafc;
  border: 1px dashed #cbd5e1;
  border-radius: 4px;
  color: #64748b;
  font-size: 13px;
  text-align: center;
}

.pre-option-row {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  margin-bottom: 0.5rem;
  padding: 0.5rem;
  background: #f8fafc;
  border-radius: 4px;
}

.pre-option-name {
  flex: 2;
  padding: 0.4rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 13px;
}

.pre-option-price {
  flex: 1;
  padding: 0.4rem;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 13px;
}

.remove-option-btn {
  background: #ef4444;
  color: white;
  border: none;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.remove-option-btn:hover {
  background: #dc2626;
}

.form-buttons {
  display: flex;
  gap: 0.75rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
}

.save-btn, .cancel-btn {
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  border: 1px solid;
  font-weight: 500;
}

.save-btn {
  background: #10b981;
  color: white;
  border-color: #10b981;
}

.save-btn:hover {
  background: #059669;
}

.cancel-btn {
  background: white;
  color: #374151;
  border-color: #d1d5db;
}

.cancel-btn:hover {
  background: #f9fafb;
}

/* 기존 그룹 목록 스타일 */
.no-groups {
  text-align: center;
  padding: 3rem;
  color: #6b7280;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px dashed #d1d5db;
}

.option-group-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  margin-bottom: 1rem;
  overflow: hidden;
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.group-info h4 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  color: #111827;
}

.group-meta {
  display: flex;
  gap: 0.5rem;
}

.type-badge, .required-badge {
  padding: 0.125rem 0.5rem;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.type-badge.single {
  background: #dbeafe;
  color: #1e40af;
}

.type-badge.multiple {
  background: #fef3c7;
  color: #92400e;
}

.required-badge {
  background: #fee2e2;
  color: #991b1b;
}

.group-controls {
  display: flex;
  gap: 0.5rem;
}

.edit-btn, .delete-btn {
  background: none;
  border: 1px solid #d1d5db;
  padding: 0.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.edit-btn:hover {
  background: #dbeafe;
  border-color: #3b82f6;
}

.delete-btn:hover {
  background: #fee2e2;
  border-color: #ef4444;
}

.option-items {
  padding: 1rem;
}

.items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.add-item-btn {
  background: #8b5cf6;
  color: white;
  border: none;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.add-item-btn:hover {
  background: #7c3aed;
}

.no-items {
  text-align: center;
  padding: 1rem;
  color: #6b7280;
  font-style: italic;
}

.option-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  margin-bottom: 0.5rem;
  background: white;
}

.option-item.inactive {
  opacity: 0.6;
  background: #f9fafb;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.item-name {
  font-weight: 500;
  color: #111827;
}

.item-price {
  font-size: 14px;
  color: #059669;
  font-weight: 500;
}

.item-controls {
  display: flex;
  gap: 0.5rem;
}

.edit-item-btn, .toggle-item-btn, .delete-item-btn {
  background: none;
  border: 1px solid #d1d5db;
  padding: 0.25rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-item-btn:hover {
  background: #dbeafe;
  border-color: #3b82f6;
}

.toggle-item-btn:hover {
  background: #fef3c7;
  border-color: #f59e0b;
}

.delete-item-btn:hover {
  background: #fee2e2;
  border-color: #ef4444;
}

.modal-footer {
  padding: 1rem 1.5rem;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  display: flex;
  justify-content: flex-end;
}

.close-modal-btn {
  background: #6b7280;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

.close-modal-btn:hover {
  background: #4b5563;
}

/* 옵션 아이템 모달 스타일 */
.item-modal {
  background: white;
  border-radius: 8px;
  max-width: 400px;
  width: 100%;
}

.item-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.item-modal-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #111827;
}

.item-modal-content {
  padding: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #374151;
  font-size: 14px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
}

.item-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding: 1rem;
  border-top: 1px solid #e5e7eb;
}

/* 반응형 스타일 */
@media (max-width: 768px) {
  .modal-overlay {
    padding: 0.5rem;
  }
  
  .modal-container {
    max-height: 95vh;
  }
  
  .modal-header {
    padding: 1rem;
  }
  
  .modal-content {
    padding: 1rem;
  }
  
  .add-group-form {
    padding: 1rem;
  }
  
  .radio-option, .checkbox-container {
    padding: 0.5rem;
  }
  
  .form-buttons {
    flex-direction: column-reverse;
    gap: 0.5rem;
  }
  
  .save-btn, .cancel-btn {
    width: 100%;
    justify-content: center;
  }
  
  .group-header {
    flex-direction: column;
    gap: 0.75rem;
    align-items: stretch;
  }
  
  .group-controls {
    justify-content: center;
  }
  
  .option-item {
    flex-direction: column;
    gap: 0.75rem;
    align-items: stretch;
  }
  
  .item-controls {
    justify-content: center;
  }
  
  .pre-option-row {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .pre-option-name, .pre-option-price {
    flex: none;
  }
  
  .remove-option-btn {
    align-self: center;
  }
  
  .section-title {
    flex-direction: column;
    gap: 0.5rem;
    align-items: stretch;
  }
  
  .items-header {
    flex-direction: column;
    gap: 0.5rem;
    align-items: stretch;
  }
}

@media (max-width: 480px) {
  .modal-overlay {
    padding: 0.25rem;
  }
  
  .modal-header h2 {
    font-size: 1.2rem;
  }
  
  .radio-text strong, .checkbox-text strong {
    font-size: 13px;
  }
  
  .radio-text small, .checkbox-text small {
    font-size: 11px;
  }
  
  .form-label {
    font-size: 13px;
  }
  
  .form-input, .pre-option-name, .pre-option-price {
    font-size: 13px;
    padding: 0.4rem;
  }
}
</style>