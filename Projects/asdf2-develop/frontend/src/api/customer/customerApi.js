import api from '../index.js'
import apiManager from './apiManager.js'

export const customerApi = {
  /**
   * 가게 관련 API
   */
  getStores: () =>
    apiManager.queueRequest(async () => {
      const response = await api.get('/stores')
      console.log('✅ 가게 목록 조회 성공')
      return response.data
    }),

  getStoreById: (storeId) =>
    apiManager.queueRequest(async () => {
      const response = await api.get(`/stores/${storeId}`)
      console.log('✅ 가게 상세 조회 성공')
      return response.data
    }),

  getMenusByStoreId: (storeId) =>
    apiManager.queueRequest(async () => {
      const response = await api.get(`/stores/${storeId}/menus`)
      console.log('✅ 메뉴 목록 조회 성공')
      return response.data
    }),

  getMenuById: (storeId, menuId) =>
    apiManager.queueRequest(async () => {
      const response = await api.get(`/stores/${storeId}/menus/${menuId}`)
      console.log('✅ 메뉴 상세 조회 성공')
      return response.data
    }),

  /**
   * 주문 관련 API
   */
  createOrder: (orderData) =>
    apiManager.queueRequest(async () => {
      const response = await api.post('/orders', orderData)
      console.log('✅ 주문 생성 성공')
      return response.data
    }),

  getOrders: (storeId) =>
    apiManager.queueRequest(async () => {
      const response = await api.get('/orders', {
        params: { storeId },
      })
      console.log('✅ 주문 목록 조회 성공')
      return response.data
    }),

  // 사용자 주문 내역 조회 (storeId 없이)
  getMyOrders: (page = 0, size = 20) =>
    apiManager.queueRequest(async () => {
      const response = await api.get(`/orders?page=${page}&size=${size}`)
      console.log('✅ 내 주문 내역 조회 성공')
      return response.data
    }),

  getOrderDetail: (orderId) =>
    apiManager.queueRequest(async () => {
      const response = await api.get(`/orders/${orderId}`)
      console.log('✅ 주문 상세 조회 성공')
      return response.data
    }),

  acceptOrder: (orderId, acceptData) =>
    apiManager.queueRequest(async () => {
      const response = await api.put(`/orders/${orderId}/accept`, {
        minCookingTime: acceptData.minCookingTime,
        maxCookingTime: acceptData.maxCookingTime,
      })
      console.log('✅ 주문 수락 성공')
      return response.data
    }),

  rejectOrder: (orderId, rejectData) =>
    apiManager.queueRequest(async () => {
      const response = await api.put(`/orders/${orderId}/reject`, {
        reason: rejectData.reason,
      })
      console.log('✅ 주문 거절 성공')
      return response.data
    }),

  startCooking: (orderId) =>
    apiManager.queueRequest(async () => {
      const response = await api.put(`/orders/${orderId}/start-cooking`)
      console.log('✅ 조리 시작 성공')
      return response.data
    }),

  completeCooking: (orderId) =>
    apiManager.queueRequest(async () => {
      const response = await api.put(`/orders/${orderId}/complete-cooking`)
      console.log('✅ 조리 완료 성공')
      return response.data
    }),

  deliverOrder: (orderId) =>
    apiManager.queueRequest(async () => {
      const response = await api.put(`/orders/${orderId}/deliver`)
      console.log('✅ 배달 완료 성공')
      return response.data
    }),

  cancelOrder: (orderId, cancelData) =>
    apiManager.queueRequest(async () => {
      const response = await api.put(`/orders/${orderId}/cancel`, {
        cancelReason: cancelData.cancelReason,
      })
      console.log('✅ 주문 취소 성공')
      return response.data
    }),
}
