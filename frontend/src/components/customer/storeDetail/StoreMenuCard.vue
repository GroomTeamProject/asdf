<template>
  <section>
    <h2 class="mb-4 text-gray-800">메뉴</h2>
    <div class="grid gap-4">
      <div v-for="menuItem in menuItems" :key="menuItem.id" class="bg-white rounded-lg border border-gray-200 shadow-sm cursor-pointer hover:shadow-md transition-all duration-200" @click="goToMenuDetail(menuItem.id)">
        <div class="p-4">
          <div class="flex gap-4">
            <div class="image-placeholder w-20 h-20 flex-shrink-0">
              <span>IMAGE</span>
            </div>
            <div class="flex-1">
              <h3 class="mb-1 text-gray-800">{{ menuItem.name }}</h3>
              <p class="text-sm text-gray-600 mb-2">{{ menuItem.description }}</p>
              <p class="text-gray-800">{{ menuItem.price.toLocaleString() }}원</p>
            </div>
            <div class="flex items-center">
              <i data-lucide="chevron-right" class="w-5 h-5 text-gray-400"></i>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'StoreMenuCard',
  props: {
    menuItems: {
      type: Array,
      required: true,
    },
    storeId: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const router = useRouter()

    const goToMenuDetail = (menuItemId) => {
      router.push(`/customer/stores/${props.storeId}/menu/${menuItemId}`)
    }

    return {
      goToMenuDetail,
    }
  },
}
</script>
