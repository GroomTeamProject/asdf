<script>
import api from '@/api/index.js'
import dayjs from 'dayjs'
const API_BASE = import.meta.env.VITE_API_URL;
export default{
  data() {
    return {
      rider_id: null,
      deliveries: null,
    }
  },
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
  mounted() {
    this.rider_id = this.riderInfo.riderId;
    console.log(`[${this.activeTab}] mounted  riderName=${this.riderInfo.riderName}  riderId=${this.riderInfo.riderId}`);
    this.loadHistory()
  },
  methods:{
    formatDate(data){
      return dayjs(data).format('YYYY년 M월 D일 HH시 mm분')
    },
    async loadHistory(){
      try{
        const resp = await api.get(`/rider/${this.rider_id}/history`)
        this.deliveries = resp.data;
      }catch(e){
        console.log(`[${this.activeTab}] loadHistory 실패`,e);
      }finally {
        console.log([`${this.activeTab} loadHistory: `],...this.deliveries)
      }
    }
  },
}
</script>
<template>
  <div v-if="deliveries?.length">
    <div class = "w-auto h-screen bg-gray-50 p-4 overflow-y-scroll shadow-lg">
      <div class="sticky top-0 z-10 bg-gray-50 pb-2">
        <h2 class="text-xl font-semibold text-gray-800">배달 내역</h2>
      </div>
      <ul class="mt-2 space y-3">
        <li v-for="d in deliveries" :key="d.id">
          <article class="rounded-xl border bg-white p-4 shadow hover:shadow-md transition">
            <!-- 헤더 메타 -->
            <div class="flex flex-wrap items-center gap-2 text-sm">
            <span class="inline-flex items-center px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 border border-blue-200">
              발생일시 {{ formatDate(d.acceptedAt) }}
            </span>
              <span class="text-gray-500">주문번호</span>
              <span class="font-medium text-gray-900">{{ d.orderId }}</span>
            </div>

            <!-- 상세: 정렬 깔끔한 키-값 -->
            <dl class="mt-3 grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-2 text-sm">
              <div class="flex">
                <dt class="w-24 shrink-0 text-gray-500">픽업 시간</dt>
                <dd class="text-gray-900 font-medium">{{ formatDate(d.pickedUpAt) }}</dd>
              </div>
              <div class="flex">
                <dt class="w-24 shrink-0 text-gray-500">픽업 주소</dt>
                <dd class="text-black font-semibold">{{ (d.pickupAddress) }}</dd>
              </div>

              <div class="flex">
                <dt class="w-24 shrink-0 text-gray-500">배달 시간</dt>
                <dd class="text-gray-900 font-medium">{{ formatDate(d.deliveredAt) }}</dd>
              </div>
              <div class="flex">
                <dt class="w-24 shrink-0 text-gray-500">배달 주소</dt>
                <dd class="text-black font-semibold">{{ (d.completeAddress) }}</dd>
              </div>

              <div class="flex">
                <dt class="w-24 shrink-0 text-gray-500">배달료</dt>
                <dd class="text-base font-bold">{{ d.deliveryFee }}원</dd>
              </div>

            </dl>
          </article>
        </li>
      </ul>
    </div>
  </div>

</template>