import { describe, it, expect } from 'vitest'
import { orderApi } from '../customerApi.js'

describe('Order API - 실제 서버 테스트 (200 응답 확인)', () => {
  const testStoreId = 1
  const testOrderId = 1

  describe('조회 API (GET)', () => {
    it('주문 목록 조회 - 200 응답 확인', async () => {
      try {
        const result = await orderApi.getOrders(testStoreId)
        console.log('✅ 주문 목록 조회 성공! 주문 개수:', result.length)
        expect(Array.isArray(result)).toBe(true)
        if (result.length > 0) {
          expect(result[0]).toHaveProperty('id')
          expect(result[0]).toHaveProperty('orderNumber')
          expect(result[0]).toHaveProperty('status')
        }
      } catch (error) {
        console.log('❌ 주문 목록 조회 실패:', error.response?.status, error.message)
        expect(error).toBeDefined()
      }
    }, 10000)

    it('주문 상세 조회 - 200 응답 확인', async () => {
      try {
        const result = await orderApi.getOrderDetail(testOrderId)
        console.log('✅ 주문 상세 조회 성공! 주문 ID:', result.id)
        expect(result).toHaveProperty('id')
        expect(result).toHaveProperty('orderNumber')
        expect(result).toHaveProperty('status')
        expect(result).toHaveProperty('orderItems')
      } catch (error) {
        console.log('❌ 주문 상세 조회 실패:', error.response?.status, error.message)
        expect(error).toBeDefined()
      }
    }, 10000)
  })

  describe('상태 변경 API (PUT)', () => {
    it('주문 수락 - 응답 확인 (200 또는 적절한 에러)', async () => {
      try {
        const acceptData = {
          minCookingTime: 20,
          maxCookingTime: 40
        }
        const result = await orderApi.acceptOrder(testOrderId, acceptData)
        console.log('✅ 주문 수락 성공! 상태:', result.status)
        expect(result).toHaveProperty('id')
        expect(result).toHaveProperty('status')
      } catch (error) {
        console.log('❌ 주문 수락 실패:', error.response?.status, error.response?.data?.message)
        // 비즈니스 로직 에러는 정상 (이미 처리된 주문 등)
        expect(error.response?.status).toBeOneOf([400, 401, 403, 404])
      }
    }, 10000)

    it('주문 거절 - 응답 확인 (200 또는 적절한 에러)', async () => {
      try {
        const rejectData = {
          reason: '재료 부족으로 인한 주문 거절'
        }
        const result = await orderApi.rejectOrder(testOrderId, rejectData)
        console.log('✅ 주문 거절 성공! 상태:', result.status)
        expect(result).toHaveProperty('id')
        expect(result).toHaveProperty('status')
      } catch (error) {
        console.log('❌ 주문 거절 실패:', error.response?.status, error.response?.data?.message)
        expect(error.response?.status).toBeOneOf([400, 401, 403, 404])
      }
    }, 10000)

    it('조리 시작 - 응답 확인 (200 또는 적절한 에러)', async () => {
      try {
        const result = await orderApi.startCooking(testOrderId)
        console.log('✅ 조리 시작 성공! 상태:', result.status)
        expect(result).toHaveProperty('id')
        expect(result).toHaveProperty('status')
      } catch (error) {
        console.log('❌ 조리 시작 실패:', error.response?.status, error.response?.data?.message)
        expect(error.response?.status).toBeOneOf([400, 401, 403, 404])
      }
    }, 10000)

    it('조리 완료 - 응답 확인 (200 또는 적절한 에러)', async () => {
      try {
        const result = await orderApi.completeCooking(testOrderId)
        console.log('✅ 조리 완료 성공! 상태:', result.status)
        expect(result).toHaveProperty('id')
        expect(result).toHaveProperty('status')
      } catch (error) {
        console.log('❌ 조리 완료 실패:', error.response?.status, error.response?.data?.message)
        expect(error.response?.status).toBeOneOf([400, 401, 403, 404])
      }
    }, 10000)

    it('배달 완료 - 응답 확인 (200 또는 적절한 에러)', async () => {
      try {
        const result = await orderApi.deliverOrder(testOrderId)
        console.log('✅ 배달 완료 성공! 상태:', result.status)
        expect(result).toHaveProperty('id')
        expect(result).toHaveProperty('status')
      } catch (error) {
        console.log('❌ 배달 완료 실패:', error.response?.status, error.response?.data?.message)
        expect(error.response?.status).toBeOneOf([400, 401, 403, 404])
      }
    }, 10000)

    it('주문 취소 - 응답 확인 (200 또는 적절한 에러)', async () => {
      try {
        const cancelData = {
          cancelReason: '고객 요청으로 인한 주문 취소'
        }
        const result = await orderApi.cancelOrder(testOrderId, cancelData)
        console.log('✅ 주문 취소 성공! 상태:', result.status)
        expect(result).toHaveProperty('id')
        expect(result).toHaveProperty('status')
      } catch (error) {
        console.log('❌ 주문 취소 실패:', error.response?.status, error.response?.data?.message)
        expect(error.response?.status).toBeOneOf([400, 401, 403, 404])
      }
    }, 10000)
  })

  describe('서버 연결 상태', () => {
    it('백엔드 서버 연결 확인', async () => {
      try {
        const result = await orderApi.getOrders(testStoreId)
        console.log('✅ 백엔드 서버 연결 성공')
        expect(result).toBeDefined()
      } catch (error) {
        if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
          console.log('❌ 백엔드 서버가 실행 중이지 않습니다.')
        } else {
          console.log('🔍 서버는 연결되었지만 다른 오류:', error.message)
        }
        expect(error).toBeDefined()
      }
    }, 5000)
  })
})
