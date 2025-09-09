<template>
  <div class="min-h-screen bg-gray-100">
    <!-- 헤더 -->
    <header class="flex justify-between items-center p-4 bg-white shadow">
      <h1 class="text-xl font-bold">{{ title }}</h1>
      <button
        class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
        @click="$router.push('/my-page')"
      >
        마이페이지
      </button>
    </header>

    <!-- 콘텐츠 -->
    <main class="p-6">
      <slot />
    </main>
  </div>
</template>

<script>
export default {
  name: 'BaseLayout',
  props: {
    title: { type: String, default: '메인 페이지' },
  },
};
</script>
