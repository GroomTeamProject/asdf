<template>
  <div class="flex min-h-screen flex-col items-center justify-center">
    <CustomerHeader />

    <main class="flex w-full flex-grow flex-col bg-neutral-50 p-4">
      <router-view />
    </main>

    <Footer />
  </div>
</template>

<script setup>
import CustomerHeader from './CustomerHeader.vue'
import Footer from '../Footer.vue'
</script>
