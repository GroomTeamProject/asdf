<template>
  <div class="flex min-h-screen flex-col items-center justify-center">
    <Header />

    <main class="flex w-full flex-grow flex-col bg-gray-100 p-4">
      <router-view />
    </main>

    <Footer />
  </div>
</template>

<script setup>
import Header from './Header.vue'
import Footer from './Footer.vue'
</script>
