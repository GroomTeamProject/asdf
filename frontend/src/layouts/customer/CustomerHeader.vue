<script setup>
import Logo from '@/components/Logo.vue'
import { LucideShoppingCart, LucideHome } from 'lucide-vue-next'
</script>

<template>
  <header class="flex h-16 w-full items-center justify-between border-b border-neutral-300 bg-white p-4">
    <div class="flex items-center gap-2">
      <Logo />
      <router-link to="/customer/stores" class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
        <lucide-home />
      </router-link>
    </div>

    <div class="flex items-center justify-between">
      <router-link to="/customer/cart" class="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-sm font-medium">
        <lucide-shopping-cart />
      </router-link>
    </div>
  </header>
</template>
