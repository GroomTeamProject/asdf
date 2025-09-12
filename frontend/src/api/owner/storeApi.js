// src/api/owner/storeApi.js
import api from '../index.js'  //JWT 설정된 axios 인스턴스 import

class StoreApiManager {
  constructor() {
    this.requestQueue = [];
    this.isProcessing = false;
  }

  async processQueue() {
    if (this.isProcessing) return;
    
    this.isProcessing = true;
    
    while (this.requestQueue.length > 0) {
      const { apiFunction, resolve, reject } = this.requestQueue.shift();
      
      try {
        const result = await apiFunction();
        resolve(result);
        await new Promise(resolve => setTimeout(resolve, 100));
      } catch (error) {
        reject(error);
      }
    }
    
    this.isProcessing = false;
  }

  async queueRequest(apiFunction) {
    return new Promise((resolve, reject) => {
      this.requestQueue.push({ apiFunction, resolve, reject });
      this.processQueue();
    });
  }
}

const apiManager = new StoreApiManager();



export const storeApi = {
  // 가게 정보 조회
  getMyStore: () => apiManager.queueRequest(async () => {
    console.log('🔄 가게 정보 조회 중...');
    const response = await api.get('/owner/store');   
    console.log('✅ 가게 정보 조회 성공');
    return response.data;   
  }),

  // 🔥 큐 없이 직접 호출하는 대시보드 함수 추가
  getDashboardDirect: async () => {
    console.log('🔄 대시보드 데이터 조회 중... (직접 호출)');
    try {
      const response = await api.get('/owner/store/dashboard');   
      console.log('✅ 대시보드 데이터 조회 성공 (직접 호출)');
      return response.data;
    } catch (error) {
      console.error('❌ 대시보드 API 호출 실패 (직접):', error);
      throw error;
    }
  },

  // 가게 정보 수정
  updateStore: (storeData) => apiManager.queueRequest(async () => {
    console.log('🔄 가게 정보 수정 중...');
    const response = await api.put('/owner/store', {   
      name: storeData.name,
      description: storeData.description,
      category: storeData.category,
      imageUrl: storeData.imageUrl
    });
    console.log('✅ 가게 정보 수정 성공');
    return response.data;   
  }),

  // 연락처 수정
  updateContact: (contactData) => apiManager.queueRequest(async () => {
    console.log('🔄 연락처 수정 중...');
    const response = await api.put('/owner/store/contact', {   
      phone: contactData.phone
    });
    console.log('✅ 연락처 수정 성공');
    return response.data;   
  }),

  // 배달 설정 수정
  updateDelivery: (deliveryData) => apiManager.queueRequest(async () => {
    console.log('🔄 배달 설정 수정 중...');
    const response = await api.put('/owner/store/delivery', {   
      deliveryFee: deliveryData.deliveryFee,
      minOrderAmount: deliveryData.minOrderAmount,
      deliveryTimeMin: deliveryData.deliveryTimeMin,
      deliveryTimeMax: deliveryData.deliveryTimeMax
    });
    console.log('✅ 배달 설정 수정 성공');
    return response.data;   
  }),

  // 영업 상태 변경
  updateStoreStatus: (statusData) => apiManager.queueRequest(async () => {
    console.log('🔄 영업 상태 변경 중...');
    const response = await api.put('/owner/store/status', {   
      status: statusData.status,
      message: statusData.message
    });
    console.log('✅ 영업 상태 변경 성공');
    return response.data;   
  }),

  // 위치 정보 수정
  updateLocation: (locationData) => apiManager.queueRequest(async () => {
    console.log('🔄 위치 정보 수정 중...');
    const response = await api.put('/owner/store/location', {   
      address: locationData.address,
      detailAddress: locationData.detailAddress
    });
    console.log('✅ 위치 정보 수정 성공');
    return response.data;   
  }),

  // 운영시간 조회
  getStoreHours: () => apiManager.queueRequest(async () => {
    console.log('🔄 운영시간 조회 중...');
    const response = await api.get('/owner/store/hours');   
    console.log('✅ 운영시간 조회 성공');
    return response.data;   
  }),

  // 가게 이미지 업로드
  uploadImage: (file) => apiManager.queueRequest(async () => {
    console.log('🔄 가게 이미지 업로드 중...');
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await api.post('/owner/store/images', formData, {   
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    console.log('✅ 가게 이미지 업로드 성공');
    return response.data;    // (서버에서 JSON으로 반환한다면)
  }),

  // 가게 상태 조회
  getStoreStatus: () => apiManager.queueRequest(async () => {
    console.log('🔄 가게 상태 조회 중...');
    const response = await api.get('/owner/store/status');   
    console.log('✅ 가게 상태 조회 성공');
    return response.data;   
  }),

  // 휴무일 등록
  createHoliday: (holidayData) => apiManager.queueRequest(async () => {
    console.log('🔄 휴무일 등록 중...');
    const response = await api.post('/owner/store/holidays', {   
      date: holidayData.date,
      reason: holidayData.reason,
      isRecurring: holidayData.isRecurring || false
    });
    console.log('✅ 휴무일 등록 성공');
    return response.data;   
  }),

  // 휴무일 목록 조회
  getHolidays: () => apiManager.queueRequest(async () => {
    console.log('🔄 휴무일 목록 조회 중...');
    const response = await api.get('/owner/store/holidays');   
    console.log('✅ 휴무일 목록 조회 성공');
    return response.data;   
  }),

  // 휴무일 삭제
  deleteHoliday: (holidayId) => apiManager.queueRequest(async () => {
    console.log('🔄 휴무일 삭제 중...');
    const response = await api.delete(`/owner/store/holidays/${holidayId}`);   
    console.log('✅ 휴무일 삭제 성공');
    return response.data;   
  }),

  // 운영시간 업데이트
  updateStoreHours: (hoursData) => apiManager.queueRequest(async () => {
    console.log('🔄 운영시간 업데이트 중...');
    const response = await api.put('/owner/store/hours', hoursData);
    console.log('✅ 운영시간 업데이트 성공');
    return response.data;
  }),

  // 특정 요일 운영시간 업데이트
  updateDayHours: (dayOfWeek, hourData) => apiManager.queueRequest(async () => {
    console.log('🔄 특정 요일 운영시간 업데이트 중...');
    const response = await api.put(`/owner/store/hours/${dayOfWeek}`, hourData);
    console.log('✅ 특정 요일 운영시간 업데이트 성공');
    return response.data;
  }),

  // 운영시간 초기 설정
  createStoreHours: (hoursData) => apiManager.queueRequest(async () => {
    console.log('🔄 운영시간 초기 설정 중...');
    const response = await api.post('/owner/store/hours', hoursData);
    console.log('✅ 운영시간 초기 설정 성공');
    return response.data;
  }),
   /**
   * 통합 대시보드 정보 조회 (기존 분리된 API들을 대체)
   */
  async getDashboard() {
    try {
      const response = await api.get('/owner/store/dashboard')
      return response.data
    } catch (error) {
      console.error('대시보드 조회 실패:', error)
      throw error
    }
  },

  createStore: (storeData) => apiManager.queueRequest(async () => {
    console.log('🔄 가게 등록 중...');
    const response = await api.post('/owner/store', {
      businessNumber: storeData.businessNumber,
      name: storeData.name,
      description: storeData.description,
      phone: storeData.phone,
      address: storeData.address,
      detailAddress: storeData.detailAddress,
      latitude: storeData.latitude,
      longitude: storeData.longitude,
      category: storeData.category,
      minOrderAmount: storeData.minOrderAmount,
      deliveryFee: storeData.deliveryFee,
      deliveryTimeMin: storeData.deliveryTimeMin,
      deliveryTimeMax: storeData.deliveryTimeMax,
      imageUrl: storeData.imageUrl
    });
    console.log('✅ 가게 등록 성공');
    return response.data;
  }),
};