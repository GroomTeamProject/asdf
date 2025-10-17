import api from '../index.js'
import apiManager from './apiManager.js'

/**
 * 리뷰 관련 API
 */
export const reviewApi = {
  /**
   * 리뷰 작성
   */
  createReview: (reviewData) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 리뷰 작성 중...')
      const response = await api.post('/reviews', reviewData)
      console.log('✅ 리뷰 작성 성공')
      return response.data
    }),

  /**
   * 가게별 리뷰 목록 조회
   */
  getReviewsByStore: (storeId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 가게 리뷰 목록 조회 중...')
      const response = await api.get(`/reviews/store?storeId=${storeId}`)
      console.log('✅ 가게 리뷰 목록 조회 성공')
      return response.data
    }),

  /**
   * 사용자별 리뷰 목록 조회
   */
  getReviewsByUser: (userId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 사용자 리뷰 목록 조회 중...')
      const response = await api.get(`/reviews/user?userId=${userId}`)
      console.log('✅ 사용자 리뷰 목록 조회 성공')
      return response.data
    }),

  /**
   * 리뷰 상세 조회
   */
  getReviewById: (reviewId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 리뷰 상세 조회 중...')
      const response = await api.get(`/reviews/${reviewId}`)
      console.log('✅ 리뷰 상세 조회 성공')
      return response.data
    }),

  /**
   * 리뷰 수정
   */
  updateReview: (reviewId, reviewData) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 리뷰 수정 중...')
      const response = await api.put(`/reviews/${reviewId}`, reviewData)
      console.log('✅ 리뷰 수정 성공')
      return response.data
    }),

  /**
   * 리뷰 삭제
   */
  deleteReview: (reviewId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 리뷰 삭제 중...')
      const response = await api.delete(`/reviews/${reviewId}`)
      console.log('✅ 리뷰 삭제 성공')
      return response.data
    }),

  /**
   * 사장님 답글 작성
   */
  addOwnerReply: (reviewId, reply) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 사장님 답글 작성 중...')
      const response = await api.post(`/reviews/${reviewId}/reply`, { reply })
      console.log('✅ 사장님 답글 작성 성공')
      return response.data
    }),

  /**
   * 리뷰 신고
   */
  reportReview: (reviewId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 리뷰 신고 중...')
      const response = await api.post(`/reviews/${reviewId}/report`)
      console.log('✅ 리뷰 신고 성공')
      return response.data
    }),

  /**
   * 가게 평균 평점 조회
   */
  getStoreAverageRating: (storeId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 가게 평균 평점 조회 중...')
      const response = await api.get(`/reviews/store/rating?storeId=${storeId}`)
      console.log('✅ 가게 평균 평점 조회 성공')
      return response.data
    }),

  /**
   * 가게 리뷰 개수 조회
   */
  getStoreReviewCount: (storeId) =>
    apiManager.queueRequest(async () => {
      console.log('🔄 가게 리뷰 개수 조회 중...')
      const response = await api.get(`/reviews/store/count?storeId=${storeId}`)
      console.log('✅ 가게 리뷰 개수 조회 성공')
      return response.data
    })
}
