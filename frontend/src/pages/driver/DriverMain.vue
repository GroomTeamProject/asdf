<!-- src/pages/driver/DriverMain.vue -->
<!-- 스크립트 영역 -->
<script>
import api from '@/api/index.js'
import DashBoard from "@/components/driver/tabs/DashBoard.vue";
import History from "@/components/driver/tabs/History.vue"
import CurrentDelivery from "@/components/driver/tabs/CurrentDelivery.vue";
const API_BASE = import.meta.env.VITE_API_URL;
const REJECT_KEY_PREFIX = 'rejectedOrders';
const TabStatus = Object.freeze(
    {
      CURRENT_DELIVERY: 'CURRENT_DELIVERY',
      DASH_BOARD: 'DASHBOARD',
      HISTORY: 'HISTORY',
    }
)
export default {
  components: { DashBoard, History, CurrentDelivery},
  data()
  {
    console.log('[DriverMain] data()')
    return {
      TabStatus,
      riderInfo: { riderName: '', riderId: null},
      activeTab: TabStatus.CURRENT_DELIVERY,
      riderAddress: '위치 확인 중…',
      today: {income:0,count:0,avg_time:0},
      currentDelivery: null,
    }
  },
  mounted() {
    const token = localStorage.getItem('jwt');  if (!token) { alert('로그인이 필요합니다.'); return; }

    console.log(`[DriverMain] mounted() token=${token}`);
    this.riderInfo = {
      riderId: Number(localStorage.getItem("userId")),
      riderName: localStorage.getItem("userName")
    };
    this.refreshLocation();
    this.getAvg();  this.getEarnings(); this.getCount();
  },
  methods: {
    onAccepted(d){
      this.currentDelivery = d;                      // 옵션: 상세에 전달할 데이터
      this.activeTab = TabStatus.CURRENT_DELIVERY;   // 탭 전환
      this.handleComplete();                         // 상단 카드 수치 갱신
      // this.currentDeliveryKey++;                  // 필요 시 리마운트
    },
    async handleComplete(){
      await this.getAvg();
      await this.getCount();
      await this.getEarnings();
    },
    async getAvg(){
      try{
        const resp = await api.get(`/deliveries/today-avg`);
        console.log("[DriverMain] getAvg:",resp.data.data);
        this.today.avg_time = resp.data.data;
      }catch(e){
        console.log("[DriverMain] getAvg:error",e);
      }

    },
    async getEarnings(){
      try{
        const resp = await api.get(`/deliveries/today-income`);

        console.log("[DriverMain] getEarnings: ",resp.data.data);
        this.today.income = resp.data.data;
      }catch (e){
        console.log("[DriverMain] getEarnings error: ",e);
      }
    },
    async getCount() {
      try{
        const resp = await api.get(`/deliveries/today-count`);

        console.log("[DriverMain] getCount: ",resp.data.data);
        this.today.count = resp.data.data;
      }catch (e){
        console.log("[DriverMain] getCount error: ",e);
      }
    },
    async refreshLocation() {
      console.log("[DriverMain] refreshLocation()");
      if (!navigator.geolocation) { this.riderAddress = '위치 지원 안됨'; return; }
      navigator.geolocation.getCurrentPosition(
          async (pos) => {
            const { latitude, longitude } = pos.coords;
            try {
              const res = await fetch(
                  `https://nominatim.openstreetmap.org/reverse?lat=${latitude}&lon=${longitude}&format=json&accept-language=ko`
              );
              const data = await res.json();
              this.riderAddress = data.display_name || `${latitude.toFixed(5)}, ${longitude.toFixed(5)}`;
            } catch {
              this.riderAddress = `${latitude.toFixed(5)}, ${longitude.toFixed(5)}`;
            }
          },
          () => { this.riderAddress = '위치 접근 거부됨' },
          { enableHighAccuracy: true, timeout: 10000 }
      );
    },
  },
}
</script>
<!-- 렌더링 영역-->
<template>
  <div class = "flex items-center justify-between">
    <!-- 왼쪽: 제목 + 주소 -->
    <div class="flex items-center gap-2">
      <i data-lucide="truck" class="w-6 h-6 text-gray-600"></i>
      <h1 class="text-2xl font-bold text-gray-800">
        {{ riderInfo.riderName }} 라이더
        <span class="ml-2 text-sm text-gray-500">({{ riderAddress }})</span>
      </h1>
    </div>
  </div>

  <div class=""><!-- delivery tab -->
    <div class="max-w-auto mx-auto p-2">
      <div class="grid grid-cols-3 gap-4">
        <div class="border-2 border-b-gray-400 bg-white rounded-lg p-4">
          <div class="flex items-center justify-start">
            <p class="text-m text-grey-300 font-bold">오늘 수익</p>
            <p class="text-m text-grey-300 px-4">{{this.today.income}}원</p>
          </div>
        </div>

        <div class="border-2 border-b-gray-400 bg-white rounded-lg p-4">
          <div class="flex items-center justify-start">
            <p class="text-m text-grey-300 font-bold">오늘 배달 횟수</p>
            <p class="text-m text-grey-300 px-4">{{this.today.count}}번</p>
          </div>
        </div>

        <div class="border-2 border-b-gray-400 bg-white rounded-lg p-4">
          <div class="flex items-center justify-start">
            <p class="text-m text-grey-300 font-bold">오늘 평균 배달 시간</p>
            <p class="text-m text-grey-300 px-4">{{this.today.avg_time}}분</p>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class = "w-full">
    <!-- 탭 버튼 영역 -->
    <div class="flex border-b border-gray-300">
      <button
          @click="activeTab = TabStatus.CURRENT_DELIVERY"
          :class="activeTab === TabStatus.CURRENT_DELIVERY
      ? 'border-b-2 border-blue-500 text-blue-600 font-bold'
      : 'text-gray-600'"
          class="px-4 py-2"
      >
        현재 배달
      </button>
      <button
          @click="activeTab = TabStatus.DASH_BOARD"
          :class="activeTab === TabStatus.DASH_BOARD
        ? 'border-b-2 border-blue-500 text-blue-600 font-bold'
      : 'text-gray-600'"
          class="px-4 py-2"
      >
        배달 요청
      </button>
      <button
          @click="activeTab = TabStatus.HISTORY"
          :class="activeTab === TabStatus.HISTORY
        ? 'border-b-2 border-blue-500 text-blue-600 font-bold'
      : 'text-gray-600'"
          class="px-4 py-2"
      >
        배달 기록
      </button>

    </div>


    <div class="p-4">
      <DashBoard v-if="activeTab === TabStatus.DASH_BOARD && riderInfo.riderId !==null"
                 :active-tab="activeTab"
                 :rider-info="riderInfo"
                 @accepted="onAccepted"
      />
      <History v-if="activeTab === TabStatus.HISTORY && riderInfo.riderId !==null"
               :active-tab="activeTab"
               :rider-info="riderInfo" />
      <CurrentDelivery v-if="activeTab === TabStatus.CURRENT_DELIVERY && riderInfo.riderId !==null"
                       :active-tab="activeTab"
                       :rider-info="riderInfo"
                       @refresh-parent="handleComplete"
      />
    </div>
  </div>
</template>

