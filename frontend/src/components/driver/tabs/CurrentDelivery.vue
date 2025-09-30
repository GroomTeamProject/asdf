<script>
import dayjs from 'dayjs'
import api from '@/api/index.js'
const API_BASE = import.meta.env.VITE_API_URL

export default {
  props: {
    activeTab: { type: String, required: true },
    riderInfo: { type: Object, required: true },
  },
  emits: ['refresh-parent'],
  data() {
    return {
      currentOrder: null,
      loading: false,
    }
  },
  computed: {
    statusLabel() {
      if(this.currentOrder?.pickedUpAt === null){
        return '수락됨';
      }else if(this.currentOrder?.deliveredAt === null){
        return '픽업완료';
      }else{
        return '대기';
      }
     },
    statusBadgeClass() {
      return this.currentOrder?.pickedAt === null
          ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
          : 'bg-blue-50 text-blue-700 border-blue-200'
    },
    actionText() {
      return this.currentOrder?.pickedUpAt === null
          ? '픽업 장소 도착하기'
          : '배달 장소 도착하기'
    }
  },
  mounted() {
    console.log(`[${this.activeTab}] mounted  riderName=${this.riderInfo.riderName}  riderId=${this.riderInfo.riderId}`);

    this.getCurrentOrder()
  },
  methods: {
    formatDate(data){
      return dayjs(data).format('YYYY년 M월 D일 HH시 mm분')
    },
    async getCurrentOrder() {
      this.loading = true;
       try{
        const resp = await api.get(`/rider/${this.riderInfo.riderId}/currentDelivery`);
        console.log(`[${this.activeTab}] getCurrentOrder->data: `, resp.data)
        this.currentOrder = resp.data;
      }catch(e){
        console.log(`[${this.activeTab}] getCurrentOrder error: `,e)
      }finally {
        this.loading = false;
      }
    },
    async onPrimary() {

      console.log(`[${this.activeTab}] : onPrimary:${this.currentOrder?.status}`);
      if (this.currentOrder?.pickedUpAt === null) {
        // TODO: 픽업 도착 처리
         try {
          const resp = await api.put(`${API_BASE}/rider/${this.riderInfo.riderId}/pickup`);
          console.log(`[${this.activeTab} onPrimary->data:`, resp.data);
          await this.getCurrentOrder();
        } catch (e){
          console.log(`[${this.activeTab}] onPrimary: pickup error`);
        }
      } else if (this.currentOrder?.pickedUpAt !== null) {
        // TODO: 배달 도착 처리
         try{
          const resp = await api.put(`${API_BASE}/rider/${this.riderInfo.riderId}/complete`);
          console.log(`[${this.activeTab} onPrimary: complete ${resp.data}`);
          await this.getCurrentOrder();
          this.$emit('refresh-parent')
        }catch(e){

        }
      }
    },
    onCancel() {
      // TODO: 취소 처리
    }
  }
}
</script>

<template>
  <!-- 로딩 상태 -->
  <div v-if="loading" class="p-4">
    <div class="animate-pulse border rounded-xl bg-white p-5 shadow">
      <div class="h-4 w-32 bg-gray-200 rounded mb-3"></div>
      <div class="h-4 w-64 bg-gray-200 rounded mb-2"></div>
      <div class="h-4 w-56 bg-gray-200 rounded mb-2"></div>
      <div class="h-4 w-72 bg-gray-200 rounded"></div>
    </div>
  </div>

  <!-- 비어있음 -->
  <div v-else-if="!currentOrder" class="p-4">
    <div class="border-2 border-dashed rounded-xl bg-gray-50 p-8 text-center text-gray-500">
      진행 중인 배달이 없습니다.
    </div>
  </div>

  <!-- 진행 중 -->
  <div v-else class="p-4">
    <article class="border rounded-xl bg-white p-5 shadow hover:shadow-md transition">
      <!-- 헤더 -->
      <div class="flex flex-wrap items-center gap-2 text-sm">
        <span class="inline-flex items-center px-2 py-0.5 rounded-full border" :class="statusBadgeClass">
          {{ statusLabel }}
        </span>
        <span class="text-gray-500">발생</span>
        <span class="text-gray-900 font-medium">{{ formatDate(currentOrder.acceptedAt) }}</span>
      </div>

      <!-- 상세 -->
      <dl class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-x-6 gap-y-2 text-sm">
        <div class="flex">
          <dt class="w-16 shrink-0 text-gray-500">픽업</dt>
          <dd class="text-gray-900 break-words">{{ currentOrder.pickupAddress }}</dd>
        </div>
        <div class="flex">
          <dt class="w-16 shrink-0 text-gray-500">배달</dt>
          <dd class="text-gray-900 break-words">{{ currentOrder.completeAddress }}</dd>
        </div>
      </dl>

      <!-- 배달료 -->
      <div class="flex">
        <dt class="w-24 shrink-0 text-gray-500">배달료</dt>
        <dd class="text-base font-bold">{{ currentOrder.deliveryFee }}원</dd>
      </div>

      <!-- 액션 -->
      <div class="mt-5 flex gap-2">
        <button
            class="flex-1 bg-blue-600 text-white border border-blue-700 hover:bg-blue-700 py-2 rounded-lg transition-colors disabled:opacity-50"
            @click="onPrimary"
        >{{ actionText }}</button>

        <button
            class="flex-1 bg-white text-gray-800 border border-gray-300 hover:bg-gray-100 py-2 rounded-lg transition-colors"
            @click="onCancel"
        >취소</button>
      </div>
    </article>
  </div>
</template>
