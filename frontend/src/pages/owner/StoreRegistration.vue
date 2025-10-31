
<template>
  <div class="min-h-screen bg-gray-50 py-8">
    <div class="max-w-4xl mx-auto px-4">
      <!-- 헤더 -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900">가게 등록</h1>
        <p class="text-gray-600 mt-2">QuickDeliver에서 사업을 시작해보세요!</p>
      </div>

      <!-- 등록 폼 -->
      <div class="bg-white rounded-lg shadow-md p-6">
        <form @submit.prevent="onSubmit" class="space-y-6">
          <!-- 기본 정보 -->
          <div class="border-b border-gray-200 pb-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">기본 정보</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  사업자등록번호 *
                </label>
                <input
                  type="text"
                  v-model="form.businessNumber"
                  required
                  placeholder="000-00-00000"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  가게명 *
                </label>
                <input
                  type="text"
                  v-model="form.name"
                  required
                  placeholder="우리집 치킨"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>

              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  가게 설명
                </label>
                <textarea
                  v-model="form.description"
                  rows="3"
                  placeholder="맛있는 치킨을 판매하는 가게입니다."
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                ></textarea>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  전화번호 *
                </label>
                <input
                  type="tel"
                  v-model="form.phone"
                  required
                  placeholder="02-0000-0000"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  카테고리 *
                </label>
                <select
                  v-model="form.category"
                  required
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  <option value="">카테고리 선택</option>
                  <option value="KOREAN">한식</option>
                  <option value="CHINESE">중식</option>
                  <option value="JAPANESE">일식</option>
                  <option value="WESTERN">양식</option>
                  <option value="CHICKEN">치킨</option>
                  <option value="PIZZA">피자</option>
                  <option value="BURGER">버거</option>
                  <option value="CAFE">카페</option>
                  <option value="DESSERT">디저트</option>
                  <option value="OTHER">기타</option>
                </select>
              </div>
            </div>
          </div>

          <!-- 주소 정보 -->
          <div class="border-b border-gray-200 pb-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">주소 정보</h2>
            
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  주소 *
                </label>
                <input
                  type="text"
                  v-model="form.address"
                  required
                  placeholder="서울특별시 강남구 테헤란로 123"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  상세주소
                </label>
                <input
                  type="text"
                  v-model="form.detailAddress"
                  placeholder="1층 101호"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>

              <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    위도
                  </label>
                  <input
                    type="number"
                    step="any"
                    v-model="form.latitude"
                    placeholder="37.5665"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                </div>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">
                    경도
                  </label>
                  <input
                    type="number"
                    step="any"
                    v-model="form.longitude"
                    placeholder="126.9780"
                    class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- 배달 정보 -->
          <div class="border-b border-gray-200 pb-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">배달 정보</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  최소주문금액 (원)
                </label>
                <input
                  type="number"
                  v-model="form.minOrderAmount"
                  min="0"
                  placeholder="10000"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  배달료 (원)
                </label>
                <input
                  type="number"
                  v-model="form.deliveryFee"
                  min="0"
                  placeholder="3000"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  최소 배달시간 (분)
                </label>
                <input
                  type="number"
                  v-model="form.deliveryTimeMin"
                  min="1"
                  placeholder="30"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">
                  최대 배달시간 (분)
                </label>
                <input
                  type="number"
                  v-model="form.deliveryTimeMax"
                  min="1"
                  placeholder="60"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>
            </div>
          </div>

          <!-- 이미지 -->
          <div class="pb-6">
            <h2 class="text-xl font-semibold text-gray-900 mb-4">가게 이미지</h2>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-2">
                이미지 URL
              </label>
              <input
                type="url"
                v-model="form.imageUrl"
                placeholder="https://example.com/image.jpg"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
          </div>

          <!-- 버튼 -->
          <div class="flex justify-end space-x-4">
            <button
              type="button"
              @click="$router.push('/login')"
              class="px-6 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
            >
              취소
            </button>
            <button
              type="submit"
              :disabled="loading"
              class="px-6 py-2 bg-green-500 text-white rounded-md hover:bg-green-600 disabled:opacity-50"
            >
              {{ loading ? '등록 중...' : '가게 등록' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { storeApi } from '../../api/owner/storeApi.js'

export default {
  name: 'StoreRegistration',
  data() {
    return {
      loading: false,
      form: {
        businessNumber: '',
        name: '',
        description: '',
        phone: '',
        address: '',
        detailAddress: '',
        latitude: null,
        longitude: null,
        category: '',
        minOrderAmount: 10000,
        deliveryFee: 3000,
        deliveryTimeMin: 30,
        deliveryTimeMax: 60,
        imageUrl: ''
      }
    }
  },
  methods: {
    async onSubmit() {
      this.loading = true
      try {
        await storeApi.createStore(this.form)
        alert('가게 등록이 완료되었습니다!')
        this.$router.push('/owner-main')
      } catch (error) {
        console.error('가게 등록 실패:', error)
        alert('가게 등록에 실패했습니다: ' + (error.response?.data?.message || error.message))
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
