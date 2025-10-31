<template>
  <div class="min-h-screen bg-gray-100 flex flex-col">
    <!-- 헤더 -->
    <header class="bg-white shadow p-4 flex justify-between items-center">
      <h1 class="text-2xl font-bold text-blue-600">QuickDeliver</h1>
    </header>

    <!-- 메인 콘텐츠 -->
    <main class="flex-grow flex flex-col items-center justify-center text-center px-4">
      <h2 class="text-3xl font-bold mb-6 text-gray-800">
        빠르고 간편한 배달 서비스 🚀
      </h2>
      <p class="text-gray-600 mb-10 max-w-2xl">
        QuickDeliver는 고객에게는 편리한 주문을, 점주에게는 손쉬운 매장 관리,
        라이더에게는 안정적인 배달 기회를 제공합니다.
      </p>

      <!-- 역할 선택 -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 w-full max-w-4xl">
        <!-- 고객 -->
        <div
          class="bg-white rounded-2xl shadow p-6 flex flex-col items-center hover:shadow-lg transition"
        >
          <h3 class="text-xl font-bold mb-4 text-gray-800">고객</h3>
          <p class="text-gray-600 mb-6">맛집을 검색하고 간편하게 주문하세요.</p>
          <button
            class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            @click="$router.push('/customer-main')"
          >
            음식점 둘러보기
          </button>
        </div>

        <!-- 점주 -->
        <div
          class="bg-white rounded-2xl shadow p-6 flex flex-col items-center hover:shadow-lg transition"
        >
          <h3 class="text-xl font-bold mb-4 text-gray-800">점주</h3>
          <p class="text-gray-600 mb-6">가게를 등록하고 주문을 관리하세요.</p>
          <button
            class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
            @click="$router.push('/owner-main')"
          >
            가게 등록하기
          </button>
        </div>

        <!-- 라이더 -->
        <div
          class="bg-white rounded-2xl shadow p-6 flex flex-col items-center hover:shadow-lg transition"
        >
          <h3 class="text-xl font-bold mb-4 text-gray-800">라이더</h3>
          <p class="text-gray-600 mb-6">배달 파트너로 참여해 수익을 올리세요.</p>
          <button
            class="px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600"
            @click="$router.push('/driver-main')"
          >
            라이더 등록하기
          </button>
        </div>
      </div>
    </main>

    <!-- 푸터 -->
    <footer class="bg-white text-center p-4 shadow-inner">
      <p class="text-gray-500 text-sm">© 2025 QuickDeliver. All rights reserved.</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: "Main",
};
</script>