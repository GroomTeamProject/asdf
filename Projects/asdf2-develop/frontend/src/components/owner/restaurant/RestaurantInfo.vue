<template>
  <div class="restaurant-info-container">
    <!-- 가게 기본 정보 섹션 -->
    <div class="info-section">
      <div class="section-header">
        <h3>가게 정보</h3>
        <button @click="$emit('edit')" class="edit-button">기본 정보 수정</button>
      </div>
      
      <div class="info-content">
        <!-- 가게 이미지 -->
        <div class="image-section">
          <img 
            v-if="restaurant.imageUrl" 
            :src="restaurant.imageUrl" 
            :alt="restaurant.name"
            class="store-image"
          >
          <div v-else class="image-placeholder">
            <span>이미지 없음</span>
          </div>
          
          <!-- 이미지 업로드 버튼 -->
          <div class="image-controls">
            <input 
              ref="fileInput"
              type="file" 
              accept="image/*" 
              @change="handleImageUpload"
              style="display: none"
            >
            <button @click="$refs.fileInput.click()" class="upload-btn">
              📷 사진 변경
            </button>
          </div>
        </div>
        
        <!-- 가게 정보 텍스트 -->
        <div class="info-details">
          <h2>{{ restaurant.name || '가게명 없음' }}</h2>
          <p class="description">{{ restaurant.description || '설명 없음' }}</p>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">카테고리:</span>
              <span class="value">{{ restaurant.category || '미설정' }}</span>
            </div>
            <div class="info-item">
              <span class="label">전화번호:</span>
              <span class="value">{{ restaurant.phone || '미설정' }}</span>
            </div>
            <div class="info-item">
              <span class="label">주소:</span>
              <span class="value">{{ restaurant.address || '미설정' }}</span>
            </div>
            <div class="info-item">
              <span class="label">상세주소:</span>
              <span class="value">{{ restaurant.detailAddress || '미설정' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 영업 상태 관리 섹션 -->
    <div class="info-section">
      <div class="section-header">
        <h3>영업 상태</h3>
      </div>
      
      <div class="status-content">
        <div class="current-status">
          <span class="status-label">현재 상태:</span>
          <span :class="['status-badge', statusClass]">{{ statusText }}</span>
          <span v-if="storeStatus.isCurrentlyOpen !== null" class="open-status">
            {{ storeStatus.isCurrentlyOpen ? '(영업시간)' : '(영업시간 외)' }}
          </span>
        </div>
        
        <div v-if="storeStatus.currentDayStatus" class="day-status">
          {{ storeStatus.currentDayStatus }}
        </div>

        <div v-if="storeStatus.lastUpdated" class="last-updated">
          마지막 업데이트: {{ formatDateTime(storeStatus.lastUpdated) }}
        </div>
        
        <div class="status-controls">
          <select v-model="selectedStatus" class="status-select">
            <option value="OPEN">영업중</option>
            <option value="CLOSED">마감</option>
            <option value="TEMPORARILY_CLOSED">임시 휴무</option>
          </select>
          <input 
            v-model="statusMessage" 
            placeholder="상태 변경 사유 (선택사항)"
            class="status-message-input"
            maxlength="100"
          >
          <button @click="changeStatus" class="change-status-btn">상태 변경</button>
        </div>

        <div v-if="lastStatusChange" class="recent-change">
          <small>
            최근 변경: {{ lastStatusChange.message || '사유 없음' }}
          </small>
        </div>
      </div>
    </div>

    <!-- 휴무일 관리 섹션 -->
    <div class="info-section">
      <div class="section-header">
        <h3>휴무일 관리</h3>
      </div>
      
      <div class="holiday-content">
        <!-- 휴무일 추가 -->
        <div class="add-holiday">
          <input 
            v-model="newHolidayDate" 
            type="date" 
            class="holiday-date-input"
          >
          <input 
            v-model="newHolidayReason" 
            placeholder="휴무 사유 (선택사항)"
            class="holiday-reason-input"
          >
          <label class="recurring-checkbox">
            <input 
              v-model="newHolidayRecurring" 
              type="checkbox"
            >
            매년 반복
          </label>
          <button @click="addHoliday" class="add-holiday-btn">휴무일 추가</button>
        </div>
        
        <!-- 휴무일 목록 -->
        <div class="holiday-list">
          <div v-if="holidays.length === 0" class="no-holidays">
            등록된 휴무일이 없습니다.
          </div>
          <div 
            v-for="holiday in holidays" 
            :key="holiday.id"
            class="holiday-item"
          >
            <div class="holiday-info">
              <span class="holiday-date">{{ formatDate(holiday.date) }}</span>
              <span class="holiday-reason">{{ holiday.reason || '휴무' }}</span>
              <span v-if="holiday.isRecurring" class="recurring-badge">매년 반복</span>
            </div>
            <button @click="removeHoliday(holiday.id)" class="remove-holiday-btn">
              ❌
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 운영시간 정보 -->
    <div class="info-section">
      <div class="section-header">
        <h3>운영시간</h3>
        <button @click="$emit('edit-hours')" class="edit-button">운영시간 수정</button>
      </div>
      
      <div class="hours-content">
        <div v-if="storeHours.length === 0" class="no-hours">
          운영시간이 설정되지 않았습니다.
        </div>
        <div v-else class="hours-list">
          <div 
            v-for="hour in storeHours" 
            :key="hour.id"
            class="hour-item"
          >
            <span class="day-name">{{ getDayName(hour.dayOfWeek) }}</span>
            <span v-if="hour.isClosed" class="closed">휴무</span>
            <span v-else class="time">{{ formatTime(hour.openTime) }} - {{ formatTime(hour.closeTime) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { storeApi } from '@/api/owner/storeApi'

const props = defineProps({
  restaurant: {
    type: Object,
    required: true
  },
  storeHours: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['edit', 'refresh', 'edit-hours'])

// 상태 관리
const selectedStatus = ref('OPEN')
const statusMessage = ref('')
const newHolidayDate = ref('')
const newHolidayReason = ref('')
const newHolidayRecurring = ref(false)
const holidays = ref([])
const storeStatus = ref({
  isCurrentlyOpen: null,
  currentDayStatus: null,
  lastUpdated: null
})
const lastStatusChange = ref(null)
const fileInput = ref(null)

// 계산된 속성
const statusClass = computed(() => {
  const status = props.restaurant.status || storeStatus.value.status
  switch (status) {
    case 'OPEN': return 'status-open'
    case 'CLOSED': return 'status-closed'
    case 'TEMPORARILY_CLOSED': return 'status-temp-closed'
    default: return 'status-unknown'
  }
})

const statusText = computed(() => {
  const status = props.restaurant.status || storeStatus.value.status
  return getStatusText(status)
})

// 메서드
const getStatusText = (status) => {
  switch (status) {
    case 'OPEN': return '영업중'
    case 'CLOSED': return '마감'
    case 'TEMPORARILY_CLOSED': return '임시 휴무'
    default: return '알 수 없음'
  }
}

const handleImageUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
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
    const imageUrl = await storeApi.uploadImage(file)
    
    // 가게 정보 업데이트 (이미지 URL만)
    await storeApi.updateStore({
      name: props.restaurant.name,
      description: props.restaurant.description,
      category: props.restaurant.category,
      imageUrl: imageUrl
    })
    
    alert('가게 사진이 성공적으로 변경되었습니다!')
    emit('refresh')
    
  } catch (error) {
    console.error('이미지 업로드 실패:', error)
    alert('이미지 업로드에 실패했습니다: ' + error.message)
  } finally {
    // 파일 입력 초기화
    if (fileInput.value) {
      fileInput.value.value = ''
    }
  }
}

const changeStatus = async () => {
  if (!statusMessage.value.trim()) {
    if (!confirm('상태 변경 사유를 입력하지 않았습니다. 계속하시겠습니까?')) {
      return
    }
  }

  try {
    const response = await storeApi.updateStoreStatus({
      status: selectedStatus.value,
      message: statusMessage.value.trim()
    })
    
    console.log('상태 변경 응답:', response)
    
    // 마지막 변경 정보 업데이트
    lastStatusChange.value = {
      status: response.status,
      message: response.message,
      timestamp: new Date()
    }
    
    const successMessage = `영업 상태가 '${getStatusText(response.status)}'로 변경되었습니다!`
    alert(successMessage)
    
    statusMessage.value = ''
    await loadStoreStatus()
    emit('refresh')
    
  } catch (error) {
    console.error('상태 변경 실패:', error)
    alert('상태 변경에 실패했습니다: ' + error.message)
  }
}

const addHoliday = async () => {
  if (!newHolidayDate.value) {
    alert('휴무일을 선택해주세요.')
    return
  }
  
  // 과거 날짜 체크
  const selectedDate = new Date(newHolidayDate.value)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  
  if (selectedDate < today && !newHolidayRecurring.value) {
    if (!confirm('과거 날짜를 선택하셨습니다. 계속하시겠습니까?')) {
      return
    }
  }
  
  try {
    await storeApi.createHoliday({
      date: newHolidayDate.value,
      reason: newHolidayReason.value.trim(),
      isRecurring: newHolidayRecurring.value
    })
    
    alert('휴무일이 추가되었습니다!')
    newHolidayDate.value = ''
    newHolidayReason.value = ''
    newHolidayRecurring.value = false
    await loadHolidays()
    
  } catch (error) {
    console.error('휴무일 추가 실패:', error)
    alert('휴무일 추가에 실패했습니다: ' + error.message)
  }
}

const removeHoliday = async (holidayId) => {
  if (!confirm('이 휴무일을 삭제하시겠습니까?')) return
  
  try {
    await storeApi.deleteHoliday(holidayId)
    alert('휴무일이 삭제되었습니다!')
    await loadHolidays()
    
  } catch (error) {
    console.error('휴무일 삭제 실패:', error)
    alert('휴무일 삭제에 실패했습니다: ' + error.message)
  }
}

const loadStoreStatus = async () => {
  try {
    const status = await storeApi.getStoreStatus()
    storeStatus.value = status
    selectedStatus.value = status.status || 'OPEN'
  } catch (error) {
    console.error('가게 상태 조회 실패:', error)
  }
}

const loadHolidays = async () => {
  try {
    holidays.value = await storeApi.getHolidays()
  } catch (error) {
    console.error('휴무일 목록 로드 실패:', error)
  }
}

const formatTime = (timeString) => {
  if (!timeString) return '미설정'
  return timeString.substring(0, 5)
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return ''
  return new Date(dateTimeString).toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getDayName = (dayOfWeek) => {
  const days = ['일', '월', '화', '수', '목', '금', '토']
  return days[dayOfWeek] + '요일'
}

// 컴포넌트 마운트 시 데이터 로드
onMounted(async () => {
  await loadStoreStatus()
  await loadHolidays()
})
</script>

<style scoped>
.restaurant-info-container {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  border: 1px solid #e5e7eb;
}

.info-section {
  margin-bottom: 2rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 0.5rem;
}

.section-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #111827;
  margin: 0;
}

.edit-button {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.edit-button:hover {
  background: #2563eb;
}

.info-content {
  display: flex;
  gap: 1.5rem;
  align-items: flex-start;
}

.image-section {
  flex-shrink: 0;
}

.store-image {
  width: 180px !important;
  height: 120px !important;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.image-placeholder {
  width: 180px;
  height: 120px;
  background-color: #f9fafb;
  border: 2px dashed #d1d5db;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: #6b7280;
  font-size: 14px;
}

.image-controls {
  margin-top: 0.5rem;
  text-align: center;
}

.upload-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: background-color 0.2s;
}

.upload-btn:hover {
  background: #2563eb;
}

.info-details {
  flex: 1;
}

.info-details h2 {
  font-size: 24px;
  font-weight: 700;
  color: #111827;
  margin: 0 0 0.5rem 0;
}

.description {
  color: #6b7280;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.label {
  font-size: 12px;
  font-weight: 600;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.value {
  font-size: 14px;
  color: #111827;
  font-weight: 500;
}

/* 상태 관련 스타일 */
.status-content {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.current-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-open { background: #dcfce7; color: #166534; }
.status-closed { background: #fee2e2; color: #991b1b; }
.status-temp-closed { background: #fde68a; color: #92400e; }

.open-status {
  font-size: 12px;
  color: #6b7280;
  margin-left: 0.5rem;
}

.day-status {
  background: #f0f9ff;
  border: 1px solid #0ea5e9;
  padding: 0.5rem;
  border-radius: 6px;
  font-size: 14px;
  color: #0c4a6e;
}

.last-updated {
  font-size: 12px;
  color: #6b7280;
}

.status-controls {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  flex-wrap: wrap;
}

.status-select, .status-message-input {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

.status-message-input {
  flex: 1;
  min-width: 200px;
}

.change-status-btn {
  background: #10b981;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.change-status-btn:hover {
  background: #059669;
}

.recent-change {
  margin-top: 0.5rem;
  padding: 0.5rem;
  background: #f9fafb;
  border-radius: 4px;
  border-left: 3px solid #3b82f6;
}

.recent-change small {
  color: #4b5563;
  font-size: 12px;
}

/* 휴무일 관련 스타일 */
.add-holiday {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
  flex-wrap: wrap;
  align-items: center;
}

.holiday-date-input, .holiday-reason-input {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
}

.recurring-checkbox {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 14px;
  color: #374151;
  cursor: pointer;
}

.add-holiday-btn {
  background: #8b5cf6;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.add-holiday-btn:hover {
  background: #7c3aed;
}

.holiday-list {
  max-height: 300px;
  overflow-y: auto;
}

.holiday-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  margin-bottom: 0.5rem;
  background: #fafafa;
}

.holiday-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.holiday-date {
  font-weight: 600;
  color: #111827;
  font-size: 14px;
}

.holiday-reason {
  font-size: 12px;
  color: #6b7280;
}

.recurring-badge {
  background: #dbeafe;
  color: #1e40af;
  padding: 0.125rem 0.5rem;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 500;
  align-self: flex-start;
  margin-top: 0.25rem;
}

.remove-holiday-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 14px;
  padding: 0.25rem;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.remove-holiday-btn:hover {
  background: #fee2e2;
}

/* 운영시간 관련 스타일 */
.hours-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 0.75rem;
}

.hour-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: #fafafa;
}

.day-name {
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.closed {
  color: #dc2626;
  font-weight: 500;
  font-size: 13px;
}

.time {
  color: #059669;
  font-weight: 500;
  font-size: 13px;
}

/* 공통 요소 */
.no-holidays, .no-hours {
  text-align: center;
  color: #6b7280;
  padding: 2rem;
  border: 1px dashed #d1d5db;
  border-radius: 6px;
  background: #fafafa;
  font-style: italic;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .info-content {
    flex-direction: column;
    align-items: center;
  }
  
  .store-image {
    width: 150px !important;
    height: 100px !important;
  }
  
  .image-placeholder {
    width: 150px;
    height: 100px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .status-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .status-controls > * {
    margin-bottom: 0.5rem;
  }
  
  .add-holiday {
    flex-direction: column;
    align-items: stretch;
  }
  
  .hours-list {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .restaurant-info-container {
    padding: 1rem;
  }
  
  .section-header {
    flex-direction: column;
    gap: 0.5rem;
    align-items: stretch;
  }
  
  .holiday-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .remove-holiday-btn {
    align-self: flex-end;
  }
}
</style>