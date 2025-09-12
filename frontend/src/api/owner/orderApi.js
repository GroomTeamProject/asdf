import api from '../index.js'
import { storeApi } from './storeApi.js'

class OrderApiManager {
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

const orderApiManager = new OrderApiManager();

export const orderApi = {

  getStoreInfo: () => orderApiManager.queueRequest(async () => {
    console.log('🔄 가게 정보 조회 중...');
    // storeApi에서 이미 구현된 getMyStore 사용
    const storeData = await storeApi.getMyStore();
    console.log('✅ 가게 정보 조회 성공');
    return storeData;
  }),

  // 주문 목록 조회 (가게별)
  getOrders: (storeId) => orderApiManager.queueRequest(async () => {
    console.log('🔄 주문 목록 조회 중..., 가게 ID:', storeId);
    const response = await api.get(`/orders?storeId=${storeId}`);
    console.log('✅ 주문 목록 조회 성공');
    return response.data;
  }),

  // 주문 상세 조회
  getOrderDetail: (orderId) => orderApiManager.queueRequest(async () => {
    console.log('🔄 주문 상세 조회 중..., 주문 ID:', orderId);
    const response = await api.get(`/orders/${orderId}`);
    console.log('✅ 주문 상세 조회 성공');
    return response.data;
  }),

  // 주문 수락 - 가게 배달시간 자동 적용 기능 추가
  acceptOrder: (orderId, acceptData, storeInfo = null) => orderApiManager.queueRequest(async () => {
    console.log('🔄 주문 수락 처리 중..., 주문 ID:', orderId);
    
    // 배달시간이 없고 가게 정보가 있으면 가게의 기본 배달시간 사용
    let finalAcceptData = { ...acceptData };
    
    if (!finalAcceptData.minCookingTime || !finalAcceptData.maxCookingTime) {
      if (storeInfo && storeInfo.deliveryTimeMin && storeInfo.deliveryTimeMax) {
        console.log('📋 가게 기본 배달시간 사용:', {
          min: storeInfo.deliveryTimeMin,
          max: storeInfo.deliveryTimeMax
        });
        
        finalAcceptData.minCookingTime = finalAcceptData.minCookingTime || storeInfo.deliveryTimeMin;
        finalAcceptData.maxCookingTime = finalAcceptData.maxCookingTime || storeInfo.deliveryTimeMax;
      } else {
        // 기본값 설정
        finalAcceptData.minCookingTime = finalAcceptData.minCookingTime || 20;
        finalAcceptData.maxCookingTime = finalAcceptData.maxCookingTime || 40;
        console.log('⚠️ 기본 배달시간 사용: 20-40분');
      }
    }
    
    console.log('📤 최종 수락 데이터:', finalAcceptData);
    const response = await api.put(`/orders/${orderId}/accept`, finalAcceptData);
    console.log('✅ 주문 수락 완료');
    return response.data;
  }),

  // 주문 거절
  rejectOrder: (orderId, rejectData) => orderApiManager.queueRequest(async () => {
    console.log('🔄 주문 거절 처리 중..., 주문 ID:', orderId);
    const response = await api.put(`/orders/${orderId}/reject`, rejectData);
    console.log('✅ 주문 거절 완료');
    return response.data;
  }),

  // 조리 시작
  startCooking: (orderId) => orderApiManager.queueRequest(async () => {
    console.log('🔄 조리 시작 처리 중..., 주문 ID:', orderId);
    const response = await api.put(`/orders/${orderId}/start-cooking`);
    console.log('✅ 조리 시작 완료');
    return response.data;
  }),

  // 조리 완료
  completeCooking: (orderId) => orderApiManager.queueRequest(async () => {
    console.log('🔄 조리 완료 처리 중..., 주문 ID:', orderId);
    const response = await api.put(`/orders/${orderId}/complete-cooking`);
    console.log('✅ 조리 완료');
    return response.data;
  }),

  // 배달 완료
  deliverOrder: (orderId) => orderApiManager.queueRequest(async () => {
    console.log('🔄 배달 완료 처리 중..., 주문 ID:', orderId);
    const response = await api.put(`/orders/${orderId}/deliver`);
    console.log('✅ 배달 완료');
    return response.data;
  })
};