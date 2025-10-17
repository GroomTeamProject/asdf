<script>
import dayjs from 'dayjs'
import api from '@/api/index.js'
const API_BASE = import.meta.env.VITE_API_URL;
export default{
  props: {
    activeTab: {
      type: String,
      required: true
    },
    riderInfo:{
      type: Object,
      required: true
    },

  },
  data(){
    return{
      availableDeliveries: null,
      rejectIds: new Set(),
      riderStatus: null,
    };
  },
  computed: {
    visibleDeliveries(){
      return this.availableDeliveries.filter(d => !this.rejectIds.has(d.id));
    },
  },
  mounted() {
    console.log(`[${this.activeTab}] mounted  riderName=${this.riderInfo.riderName}  riderId=${this.riderInfo.riderId}`);
    this.getAvailableDeliveries();
    this.getRiderStatus();
  },
  emits: ['accepted'],
  methods:{
    formatDate(data){
      return dayjs(data).format('YYYY년 M월 D일 HH시 mm분')
    },
    async accept(d) {
      try{
        const resp = await api.post(`/rider/${d.id}/accept`,
            {riderId: this.riderInfo.riderId}
        );
      }catch (e) {
        console.log(`[${this.activeTab}] accept 실패`, e)
      }finally {
        this.availableDeliveries = this.availableDeliveries.filter(
            x => x.id !== d.id
        )
        this.$emit('accepted',d)
        console.log(`${this.activeTab} accept rider_id=${this.riderInfo.riderId}`);
      }
    },
    reject(d){
      this.rejectIds.add(d.id);
    },
    async getRiderStatus() {
      try{
        const resp = await api.get(`/rider/${this.riderInfo.riderId}/status`);
        this.riderStatus = await resp.data;
      }catch(e){
        console.log(`[${this.activeTab} getRiderStatus: error`,e);
      }finally {
        console.log(`[${this.activeTab}] getRiderStatus: `,this.riderStatus);
      }
    },
    async getAvailableDeliveries(){
      try{
        const resp = await api.get('/orders/delivery/available');
        this.availableDeliveries = resp.data;
      }catch (e) {
        console.log(`[${this.activeTab} getAvailableDeliveries: getAvailableDeliveries: 배달 목록 오류`,e);
      }finally {
        console.log(`${this.activeTab} getAvailableDeliveries: `,this.availableDeliveries);
      }
    }
  },
}
</script>
<template>

  <div v-if="availableDeliveries?.length">
    <div class="w-auto h-screen bg-gray-50 p-4 overflow-y-scroll shadow-lg">
      <div class="sticky top-0 z-10 bg-gray-50 pb-2">
        <h2 class="text-xl font-semibold text-gray-800">배달 가능 목록</h2>
      </div>

      <ul class="mt-2 space-y-3">
        <li v-for="d in visibleDeliveries" :key="d.id">
          <article class="rounded-xl border bg-white p-4 shadow hover:shadow-md transition">
            <!-- 헤더 메타 -->
            <div class="flex flex-wrap items-center gap-2 text-sm">
            <span class="inline-flex items-center px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 border border-blue-200">
              발생일시 {{ formatDate(d.acceptedAt) }}
            </span>
              <span class="text-gray-500">주문번호</span>
              <span class="font-medium text-gray-900">{{ d.orderNumber }}</span>
            </div>

            <!-- 상세: 정렬 깔끔한 키-값 -->
            <dl class="mt-3 grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-2 text-sm">
              <div class="flex">
                <dt class="w-24 shrink-0 text-gray-500">가게명</dt>
                <dd class="text-gray-900 font-medium">{{ d.storeName }}</dd>
              </div>
              <div class="flex">
                <dt class="w-24 shrink-0 text-gray-500">배달료</dt>
                <dd class="text-black font-semibold">{{ d.deliveryFee }}원</dd>
              </div>
              <div class="md:col-span-2">
                <dt class="w-24 shrink-0 text-gray-500 inline-block align-top">가게 주소</dt>
                <dd class="inline-block text-gray-900 break-words">
                  {{ d.storeAddress }} {{ d.storeDetailAddress }}
                </dd>
              </div>
              <div class="md:col-span-2">
                <dt class="w-24 shrink-0 text-gray-500 inline-block align-top">배달 주소</dt>
                <dd class="inline-block text-gray-900 break-words">
                  {{ d.deliveryAddress }} {{ d.deliveryDetailAddress }}
                </dd>
              </div>
            </dl>

            <!-- 주문 메뉴 -->
            <div class="mt-4">
              <h3 class="text-sm font-semibold text-blue-700 border-b border-gray-200 pb-1">주문 메뉴</h3>
              <ul class="mt-2 space-y-1">
                <li v-for="it in d.orderItems" :key="it.id" class="flex items-baseline pl-4">
                  <span class="w-2 h-2 mr-2 rounded-full bg-gray-300"></span>
                  <span class="text-gray-800">{{ it.menuName }}</span>
                  <span class="ml-2 text-gray-500">{{ it.quantity }}개</span>
                </li>
              </ul>
            </div>

            <!-- 액션 -->
            <div class="mt-4 flex gap-2">
              <button
                  :disabled="riderStatus === 'ACCEPTED' || riderStatus ==='PICKED_UP'"
                  class="flex-1 bg-blue-600 text-white border border-blue-700 hover:bg-blue-700 py-2 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  @click="accept(d)"
              >
                배달 수락
              </button>
              <button
                  class="flex-1 bg-white text-gray-800 border border-gray-300 hover:bg-gray-100 py-2 rounded-lg transition-colors"
                  @click="reject(d)"
              >
                거절
              </button>
            </div>
          </article>
        </li>
      </ul>
    </div>
  </div>

</template>