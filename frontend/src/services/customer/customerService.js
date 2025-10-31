import { userApi } from '@/api/customer/userApi'

export class CustomerService {
  // 사용자 프로필 조회
  async getUserProfile(userId) {
    try {
      if (!userId) {
        throw new Error('사용자 ID가 필요합니다.')
      }
      
      const profile = await userApi.getUserProfile(userId)
      return {
        success: true,
        data: profile
      }
    } catch (error) {
      console.error('사용자 프로필 조회 실패:', error)
      throw error
    }
  }

  // 사용자 주소 목록 조회
  async getUserAddresses(userId) {
    try {
      if (!userId) {
        throw new Error('사용자 ID가 필요합니다.')
      }
      
      const addresses = await userApi.getUserAddresses(userId)
      return {
        success: true,
        data: addresses
      }
    } catch (error) {
      console.error('사용자 주소 목록 조회 실패:', error)
      throw error
    }
  }

  // 기본 배송지 및 사용자 정보 조회 (Order.vue에서 사용)
  async getDefaultAddressAndUserInfo(userId) {
    try {
      if (!userId) {
        throw new Error('사용자 ID가 필요합니다.')
      }

      // 사용자 정보와 배송지 정보를 병렬로 불러오기
      const [userProfile, addresses] = await Promise.all([
        userApi.getUserProfile(userId),
        userApi.getUserAddresses(userId)
      ])
      
      const defaultAddress = addresses?.find(addr => addr.isDefault)
      const userPhone = userProfile?.phone
      
      return {
        success: true,
        data: {
          userProfile,
          addresses,
          defaultAddress,
          userPhone
        }
      }
    } catch (error) {
      console.error('기본 배송지 및 사용자 정보 조회 실패:', error)
      throw error
    }
  }

  // 사용자 프로필 수정
  async updateUserProfile(userId, userData) {
    try {
      if (!userId) {
        throw new Error('사용자 ID가 필요합니다.')
      }
      
      if (!userData || Object.keys(userData).length === 0) {
        throw new Error('수정할 사용자 데이터가 필요합니다.')
      }

      const updatedProfile = await userApi.updateUserProfile(userId, userData)
      return {
        success: true,
        data: updatedProfile
      }
    } catch (error) {
      console.error('사용자 프로필 수정 실패:', error)
      throw error
    }
  }

  // 비밀번호 변경
  async changePassword(passwordData) {
    try {
      if (!passwordData || !passwordData.currentPassword || !passwordData.newPassword) {
        throw new Error('현재 비밀번호와 새 비밀번호가 필요합니다.')
      }

      const result = await userApi.changePassword(passwordData)
      return {
        success: true,
        data: result
      }
    } catch (error) {
      console.error('비밀번호 변경 실패:', error)
      throw error
    }
  }

  // 로그아웃
  async logout() {
    try {
      const result = await userApi.logout()
      return {
        success: true,
        data: result
      }
    } catch (error) {
      console.error('로그아웃 실패:', error)
      throw error
    }
  }

  // 계정 삭제
  async deleteAccount() {
    try {
      const result = await userApi.deleteAccount()
      return {
        success: true,
        data: result
      }
    } catch (error) {
      console.error('계정 삭제 실패:', error)
      throw error
    }
  }

  // 주소 생성
  async createUserAddress(userId, addressData) {
    try {
      if (!userId) {
        throw new Error('사용자 ID가 필요합니다.')
      }
      
      if (!addressData || !addressData.address) {
        throw new Error('주소 정보가 필요합니다.')
      }

      const address = await userApi.createUserAddress(userId, addressData)
      return {
        success: true,
        data: address
      }
    } catch (error) {
      console.error('주소 생성 실패:', error)
      throw error
    }
  }

  // 주소 수정
  async updateUserAddress(userId, addressId, addressData) {
    try {
      if (!userId || !addressId) {
        throw new Error('사용자 ID와 주소 ID가 필요합니다.')
      }
      
      if (!addressData || !addressData.address) {
        throw new Error('주소 정보가 필요합니다.')
      }

      const address = await userApi.updateUserAddress(userId, addressId, addressData)
      return {
        success: true,
        data: address
      }
    } catch (error) {
      console.error('주소 수정 실패:', error)
      throw error
    }
  }

  // 주소 삭제
  async deleteUserAddress(userId, addressId) {
    try {
      if (!userId || !addressId) {
        throw new Error('사용자 ID와 주소 ID가 필요합니다.')
      }

      const result = await userApi.deleteUserAddress(userId, addressId)
      return {
        success: true,
        data: result
      }
    } catch (error) {
      console.error('주소 삭제 실패:', error)
      throw error
    }
  }

  // 기본 주소 설정
  async setDefaultAddress(userId, addressId) {
    try {
      if (!userId || !addressId) {
        throw new Error('사용자 ID와 주소 ID가 필요합니다.')
      }

      const result = await userApi.setDefaultAddress(userId, addressId)
      return {
        success: true,
        data: result
      }
    } catch (error) {
      console.error('기본 주소 설정 실패:', error)
      throw error
    }
  }
}

// 싱글톤 인스턴스 생성
export const customerService = new CustomerService()
