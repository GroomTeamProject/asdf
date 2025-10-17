import api from '../index.js'
import apiManager from './apiManager.js'

export const userApi = {
  /**
   * 사용자 정보 관련 API
   */
  getUserProfile: (userId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 사용자 정보 조회 중...')
      const response = await api.get(`/users/${userId}`)
      console.log('✅ 사용자 정보 조회 성공')
      return response.data
    }),

  getUserAddresses: (userId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 사용자 주소 목록 조회 중...')
      const response = await api.get(`/users/${userId}/addresses`)
      console.log('✅ 사용자 주소 목록 조회 성공')
      return response.data
    }),

  /**
   * 배송지 CRUD API
   */
  createUserAddress: (userId, addressData) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 배송지 추가 중...')
      const response = await api.post(`/users/${userId}/addresses`, addressData)
      console.log('✅ 배송지 추가 성공')
      return response.data
    }),

  updateUserAddress: (userId, addressId, addressData) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 배송지 수정 중...')
      const response = await api.put(`/users/${userId}/addresses/${addressId}`, addressData)
      console.log('✅ 배송지 수정 성공')
      return response.data
    }),

  deleteUserAddress: (userId, addressId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 배송지 삭제 중...')
      const response = await api.delete(`/users/${userId}/addresses/${addressId}`)
      console.log('✅ 배송지 삭제 성공')
      return response.data
    }),

  setDefaultAddress: (userId, addressId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 기본 배송지 설정 중...')
      const response = await api.put(`/users/${userId}/addresses/${addressId}/default`)
      console.log('✅ 기본 배송지 설정 성공')
      return response.data
    }),

  /**
   * 사용자 정보 수정 API
   */
  updateUserProfile: (userId, userData) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 사용자 정보 수정 중...')
      const response = await api.put(`/users/${userId}`, userData)
      console.log('✅ 사용자 정보 수정 성공')
      return response.data
    }),

  /**
   * 비밀번호 변경
   */
  changePassword: (data) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 비밀번호 변경 요청 중...')
      // data: { email, currentPassword, newPassword }
      const response = await api.patch(`/users/me/password`, data)
      console.log('✅ 비밀번호 변경 성공')

      localStorage.removeItem('jwt')
      localStorage.removeItem('userType')
      localStorage.removeItem('userId')
      localStorage.removeItem('userName')
      localStorage.removeItem('userEmail')

      
      return response.data
    }),

  /**

   * 로그아웃 API
   */
  logout: () =>
    apiManager.queueRequest(async () => {
      console.log('🔄 로그아웃 중...')
      // TODO: 실제 API 엔드포인트 구현 후 연결
      const response = await api.post('/auth/logout')
      console.log('✅ 로그아웃 성공')
      
      // 현재는 프론트엔드에서만 처리
      localStorage.removeItem('jwt')
      localStorage.removeItem('userType')
      localStorage.removeItem('userId')
      localStorage.removeItem('userName')
      localStorage.removeItem('userEmail')
      //return { success: true, message: '로그아웃되었습니다.' }

      return response.data
    }),

  deleteAccount: () =>
    apiManager.queueRequest(async () => {
      console.log('🗑️ 계정 탈퇴 중...')
      const response = await api.delete('/users/me/deactivate')
      return { success: true, message: '계정이 삭제되었습니다.' }
    }),


}
