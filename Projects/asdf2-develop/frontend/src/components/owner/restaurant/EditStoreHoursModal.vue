<template>
  <div class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>운영시간 설정</h2>
        <button @click="$emit('close')" class="close-btn">&times;</button>
      </div>
      
      <div class="modal-body">
        <!-- 👈 hoursForm이 초기화될 때까지 로딩 표시 -->
        <div v-if="!isInitialized" class="loading-state">
          <p>운영시간 정보를 불러오는 중...</p>
        </div>
        
        <div v-else class="hours-form">
          <div class="form-section">
            <div class="section-header">
              <h3>요일별 운영시간 설정</h3>
              <div class="quick-actions">
                <button @click="applyToAll" class="quick-btn">전체 동일 적용</button>
                <button @click="setWeekdaysSame" class="quick-btn">평일 동일</button>
                <button @click="setWeekendsRest" class="quick-btn">주말 휴무</button>
              </div>
            </div>
            
            <div class="days-list">
              <div 
                v-for="(day, index) in daysOfWeek" 
                :key="index"
                class="day-row"
                :class="{ 'weekend': index === 0 || index === 6 }"
              >
                <div class="day-info">
                  <span class="day-name">{{ day.name }}</span>
                  <label class="rest-checkbox">
                    <input 
                      v-model="hoursForm[index].isClosed" 
                      type="checkbox"
                      @change="toggleDayRest(index)"
                    />
                    휴무
                  </label>
                </div>
                
                <div v-if="!hoursForm[index].isClosed" class="time-inputs">
                  <div class="time-group">
                    <label>개점</label>
                    <input 
                      v-model="hoursForm[index].openTime" 
                      type="time"
                      class="time-input"
                    />
                  </div>
                  <span class="time-separator">~</span>
                  <div class="time-group">
                    <label>마감</label>
                    <input 
                      v-model="hoursForm[index].closeTime" 
                      type="time"
                      class="time-input"
                    />
                  </div>
                  <div class="all-day-option">
                    <label class="checkbox-label">
                      <input 
                        v-model="hoursForm[index].is24Hours" 
                        type="checkbox"
                        @change="toggle24Hours(index)"
                      />
                      24시간
                    </label>
                  </div>
                </div>
                
                <div v-else class="rest-day">
                  <span class="rest-text">휴무일</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 템플릿 설정 -->
          <div class="form-section">
            <h3>빠른 설정 템플릿</h3>
            <div class="template-buttons">
              <button @click="applyTemplate('normal')" class="template-btn">
                일반 매장 (9:00-22:00, 일요일 휴무)
              </button>
              <button @click="applyTemplate('restaurant')" class="template-btn">
                음식점 (11:00-21:00, 연중무휴)
              </button>
              <button @click="applyTemplate('cafe')" class="template-btn">
                카페 (8:00-20:00, 연중무휴)
              </button>
              <button @click="applyTemplate('convenience')" class="template-btn">
                편의점 (24시간, 연중무휴)
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button @click="$emit('close')" class="cancel-btn">취소</button>
        <button @click="saveHours" :disabled="loading || !isInitialized" class="save-btn">
          {{ loading ? '저장 중...' : '저장' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { storeApi } from '@/api/owner/storeApi'

const props = defineProps({
  storeHours: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'save'])

// 요일 정보
const daysOfWeek = [
  { name: '일요일', key: 'sunday' },
  { name: '월요일', key: 'monday' },
  { name: '화요일', key: 'tuesday' },
  { name: '수요일', key: 'wednesday' },
  { name: '목요일', key: 'thursday' },
  { name: '금요일', key: 'friday' },
  { name: '토요일', key: 'saturday' }
]

// 👈 ref로 반응형 변수들 선언
const hoursForm = ref([])
const loading = ref(false)
const isInitialized = ref(false)  // 👈 ref로 선언

// 기본 시간 데이터 생성
const createDefaultDay = (dayIndex) => ({
  dayOfWeek: dayIndex,
  openTime: '09:00',
  closeTime: '18:00',
  isClosed: false,
  is24Hours: false
})

// 초기화
const initializeForm = () => {
  console.log('🔄 운영시간 폼 초기화 시작...', props.storeHours)
  
  // 👈 먼저 7일치 기본 데이터로 초기화
  hoursForm.value = Array.from({ length: 7 }, (_, index) => createDefaultDay(index))
  
  // 기존 데이터가 있으면 덮어쓰기
  if (props.storeHours && props.storeHours.length > 0) {
    props.storeHours.forEach(existingHour => {
      const dayIndex = existingHour.dayOfWeek
      if (dayIndex >= 0 && dayIndex <= 6 && hoursForm.value[dayIndex]) {  // 👈 안전성 체크 추가
        hoursForm.value[dayIndex] = {
          dayOfWeek: dayIndex,
          openTime: existingHour.openTime || '09:00',
          closeTime: existingHour.closeTime || '18:00',
          isClosed: existingHour.isClosed || false,
          is24Hours: existingHour.openTime === '00:00' && existingHour.closeTime === '23:59'
        }
      }
    })
  }
  
  // 👈 초기화 완료 후 상태 변경
  isInitialized.value = true
  
  console.log('✅ 운영시간 폼 초기화 완료:', hoursForm.value)
}

// 24시간 토글
const toggle24Hours = (dayIndex) => {
  if (!isInitialized.value || !hoursForm.value[dayIndex]) return
  
  if (hoursForm.value[dayIndex].is24Hours) {
    hoursForm.value[dayIndex].openTime = '00:00'
    hoursForm.value[dayIndex].closeTime = '23:59'
  } else {
    hoursForm.value[dayIndex].openTime = '09:00'
    hoursForm.value[dayIndex].closeTime = '18:00'
  }
}

// 휴무일 토글
const toggleDayRest = (dayIndex) => {
  if (!isInitialized.value || !hoursForm.value[dayIndex]) return
  
  const day = hoursForm.value[dayIndex]
  if (day.isClosed) {
    day.is24Hours = false
  }
}

// 빠른 설정 기능들
const applyToAll = () => {
  if (!isInitialized.value || !hoursForm.value[1]) {
    alert('아직 초기화 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }
  
  const baseDay = hoursForm.value[1] // 월요일 기준
  if (!baseDay.isClosed) {
    hoursForm.value.forEach((day, index) => {
      if (index !== 1 && day) {
        day.openTime = baseDay.openTime
        day.closeTime = baseDay.closeTime
        day.isClosed = false
        day.is24Hours = baseDay.is24Hours
      }
    })
    alert('월요일 시간을 전체 요일에 적용했습니다.')
  } else {
    alert('월요일이 휴무로 설정되어 있어 적용할 수 없습니다.')
  }
}

const setWeekdaysSame = () => {
  if (!isInitialized.value || !hoursForm.value[1]) {
    alert('아직 초기화 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }
  
  const mondayHour = hoursForm.value[1]
  if (!mondayHour.isClosed) {
    // 월-금요일에 동일 적용
    for (let i = 1; i <= 5; i++) {
      if (hoursForm.value[i]) {
        hoursForm.value[i].openTime = mondayHour.openTime
        hoursForm.value[i].closeTime = mondayHour.closeTime
        hoursForm.value[i].isClosed = false
        hoursForm.value[i].is24Hours = mondayHour.is24Hours
      }
    }
    alert('평일(월-금)에 동일한 시간을 적용했습니다.')
  } else {
    alert('월요일이 휴무로 설정되어 있어 적용할 수 없습니다.')
  }
}

const setWeekendsRest = () => {
  if (!isInitialized.value) {
    alert('아직 초기화 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }
  
  if (hoursForm.value[0]) hoursForm.value[0].isClosed = true // 일요일
  if (hoursForm.value[6]) hoursForm.value[6].isClosed = true // 토요일
  alert('주말을 휴무로 설정했습니다.')
}

// 템플릿 적용
const applyTemplate = (templateType) => {
  if (!isInitialized.value) {
    alert('아직 초기화 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }
  
  switch (templateType) {
    case 'normal':
      hoursForm.value.forEach((day, index) => {
        if (!day) return
        if (index === 0) { // 일요일 휴무
          day.isClosed = true
          day.is24Hours = false
        } else {
          day.openTime = '09:00'
          day.closeTime = '22:00'
          day.isClosed = false
          day.is24Hours = false
        }
      })
      break
      
    case 'restaurant':
      hoursForm.value.forEach(day => {
        if (!day) return
        day.openTime = '11:00'
        day.closeTime = '21:00'
        day.isClosed = false
        day.is24Hours = false
      })
      break
      
    case 'cafe':
      hoursForm.value.forEach(day => {
        if (!day) return
        day.openTime = '08:00'
        day.closeTime = '20:00'
        day.isClosed = false
        day.is24Hours = false
      })
      break
      
    case 'convenience':
      hoursForm.value.forEach(day => {
        if (!day) return
        day.openTime = '00:00'
        day.closeTime = '23:59'
        day.isClosed = false
        day.is24Hours = true
      })
      break
  }
  
  alert(`${templateType} 템플릿이 적용되었습니다.`)
}

// 저장
const saveHours = async () => {
  if (!isInitialized.value) {
    alert('데이터 초기화 중입니다. 잠시 후 다시 시도해주세요.')
    return
  }
  
  // 유효성 검사
  for (let i = 0; i < hoursForm.value.length; i++) {
    const day = hoursForm.value[i]
    if (!day) continue
    
    if (!day.isClosed) {
      if (!day.openTime || !day.closeTime) {
        alert(`${daysOfWeek[i].name}의 운영시간을 입력해주세요.`)
        return
      }
      
      if (!day.is24Hours && day.openTime >= day.closeTime) {
        alert(`${daysOfWeek[i].name}의 마감시간이 개점시간보다 늦어야 합니다.`)
        return
      }
    }
  }
  
  const hoursData = hoursForm.value
    .filter(day => day)
    .map(day => ({
      dayOfWeek: day.dayOfWeek,
      openTime: day.isClosed ? null : day.openTime,
      closeTime: day.isClosed ? null : day.closeTime,
      isClosed: day.isClosed
    }))
  
  emit('save', hoursData)
}

const handleOverlayClick = () => {
  if (!loading.value) {
    emit('close')
  }
}

// 👈 즉시 초기화 (onMounted 대신)
initializeForm()

// onMounted에서는 디버깅만
onMounted(() => {
  console.log('🔧 EditStoreHoursModal 마운트됨')
  console.log('📊 props.storeHours:', props.storeHours)
  console.log('📊 isInitialized:', isInitialized.value)
  console.log('📊 hoursForm:', hoursForm.value)
})
</script>

<style>
/* 👈 scoped 제거하여 전역 스타일로 변경 */
.modal-overlay {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  width: 100% !important;
  height: 100% !important;
  background: rgba(0, 0, 0, 0.5) !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  z-index: 10000 !important; /* 👈 매우 높은 z-index */
  padding: 1rem !important;
}

.modal-content {
  background: white !important;
  border-radius: 12px !important;
  max-width: 800px !important;
  width: 100% !important;
  max-height: 90vh !important;
  overflow: hidden !important;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2) !important;
  display: flex !important;
  flex-direction: column !important;
  position: relative !important;
}

.modal-header {
  display: flex !important;
  justify-content: space-between !important;
  align-items: center !important;
  padding: 1.5rem !important;
  border-bottom: 1px solid #e5e7eb !important;
  background: #f9fafb !important;
}

.modal-header h2 {
  margin: 0 !important;
  font-size: 1.5rem !important;
  color: #111827 !important;
  font-weight: 600 !important;
}

.close-btn {
  background: none !important;
  border: none !important;
  font-size: 1.5rem !important;
  cursor: pointer !important;
  color: #6b7280 !important;
  padding: 0 !important;
  width: 32px !important;
  height: 32px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  border-radius: 4px !important;
  transition: background-color 0.2s !important;
}

.close-btn:hover {
  background: #f3f4f6 !important;
  color: #374151 !important;
}

.modal-body {
  flex: 1 !important;
  overflow-y: auto !important;
  padding: 1.5rem !important;
}

.loading-state {
  text-align: center !important;
  padding: 3rem !important;
  color: #6b7280 !important;
}

.form-section {
  margin-bottom: 2rem !important;
}

.section-header {
  display: flex !important;
  justify-content: space-between !important;
  align-items: center !important;
  margin-bottom: 1rem !important;
  flex-wrap: wrap !important;
  gap: 1rem !important;
}

.section-header h3 {
  margin: 0 !important;
  font-size: 1.2rem !important;
  color: #374151 !important;
}

.quick-actions {
  display: flex !important;
  gap: 0.5rem !important;
  flex-wrap: wrap !important;
}

.quick-btn {
  background: #f3f4f6 !important;
  color: #374151 !important;
  border: 1px solid #d1d5db !important;
  padding: 0.25rem 0.75rem !important;
  border-radius: 4px !important;
  cursor: pointer !important;
  font-size: 12px !important;
  transition: background-color 0.2s !important;
}

.quick-btn:hover {
  background: #e5e7eb !important;
}

.days-list {
  space-y: 1rem !important;
}

.day-row {
  display: flex !important;
  align-items: center !important;
  justify-content: space-between !important;
  padding: 1rem !important;
  border: 1px solid #e5e7eb !important;
  border-radius: 8px !important;
  background: white !important;
  margin-bottom: 0.75rem !important;
}

.day-row.weekend {
  background: #fef3c7 !important;
  border-color: #fbbf24 !important;
}

.day-info {
  display: flex !important;
  align-items: center !important;
  gap: 1rem !important;
  min-width: 150px !important;
}

.day-name {
  font-weight: 600 !important;
  color: #111827 !important;
  font-size: 14px !important;
  min-width: 60px !important;
}

.rest-checkbox {
  display: flex !important;
  align-items: center !important;
  gap: 0.5rem !important;
  font-size: 14px !important;
  color: #dc2626 !important;
  cursor: pointer !important;
}

.time-inputs {
  display: flex !important;
  align-items: center !important;
  gap: 1rem !important;
  flex: 1 !important;
}

.time-group {
  display: flex !important;
  flex-direction: column !important;
  gap: 0.25rem !important;
}

.time-group label {
  font-size: 12px !important;
  color: #6b7280 !important;
  font-weight: 500 !important;
}

.time-input {
  padding: 0.5rem !important;
  border: 1px solid #d1d5db !important;
  border-radius: 4px !important;
  font-size: 14px !important;
  width: 100px !important;
}

.time-separator {
  font-size: 18px !important;
  color: #6b7280 !important;
  font-weight: 500 !important;
  margin-top: 1rem !important;
}

.all-day-option {
  display: flex !important;
  align-items: center !important;
  margin-top: 1rem !important;
}

.checkbox-label {
  display: flex !important;
  align-items: center !important;
  gap: 0.5rem !important;
  font-size: 14px !important;
  color: #059669 !important;
  cursor: pointer !important;
  font-weight: 500 !important;
}

.rest-day {
  flex: 1 !important;
  text-align: center !important;
}

.rest-text {
  color: #dc2626 !important;
  font-weight: 500 !important;
  font-style: italic !important;
}

.template-buttons {
  display: grid !important;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)) !important;
  gap: 0.75rem !important;
}

.template-btn {
  background: white !important;
  color: #374151 !important;
  border: 1px solid #d1d5db !important;
  padding: 0.75rem !important;
  border-radius: 6px !important;
  cursor: pointer !important;
  font-size: 14px !important;
  text-align: left !important;
  transition: all 0.2s !important;
}

.template-btn:hover {
  background: #f9fafb !important;
  border-color: #3b82f6 !important;
  color: #3b82f6 !important;
}

.modal-footer {
  display: flex !important;
  justify-content: flex-end !important;
  gap: 0.75rem !important;
  padding: 1.5rem !important;
  border-top: 1px solid #e5e7eb !important;
  background: #f9fafb !important;
}

.cancel-btn,
.save-btn {
  padding: 0.75rem 1.5rem !important;
  border-radius: 6px !important;
  font-weight: 500 !important;
  cursor: pointer !important;
  transition: all 0.2s !important;
  font-size: 14px !important;
}

.cancel-btn {
  background: white !important;
  color: #374151 !important;
  border: 1px solid #d1d5db !important;
}

.cancel-btn:hover {
  background: #f3f4f6 !important;
  border-color: #9ca3af !important;
}

.save-btn {
  background: #3b82f6 !important;
  color: white !important;
  border: none !important;
}

.save-btn:hover:not(:disabled) {
  background: #2563eb !important;
}

.save-btn:disabled {
  opacity: 0.6 !important;
  cursor: not-allowed !important;
}

/* 반응형 */
@media (max-width: 768px) {
  .modal-overlay {
    padding: 0.5rem !important;
  }
  
  .day-row {
    flex-direction: column !important;
    gap: 1rem !important;
    align-items: stretch !important;
  }
  
  .day-info {
    justify-content: space-between !important;
    min-width: auto !important;
  }
  
  .time-inputs {
    justify-content: center !important;
    flex-wrap: wrap !important;
  }
  
  .quick-actions {
    width: 100% !important;
    justify-content: center !important;
  }
  
  .template-buttons {
    grid-template-columns: 1fr !important;
  }
  
  .modal-footer {
    flex-direction: column !important;
  }
}
</style>