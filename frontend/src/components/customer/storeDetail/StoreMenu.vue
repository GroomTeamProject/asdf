<template>
  <section>
    <h2 class="mb-4 text-gray-800">메뉴</h2>
    <div class="grid gap-4">
      <div v-for="menuItem in menuItems" :key="menuItem.id" class="border-2 border-gray-400 bg-white rounded-lg">
        <div class="p-4">
          <div class="flex gap-4">
            <div class="image-placeholder w-20 h-20 flex-shrink-0">
              <span>IMAGE</span>
            </div>
            <div class="flex-1 cursor-pointer hover:bg-gray-50 p-2 rounded transition-colors" @click="goToMenuDetail(menuItem.id)">
              <h3 class="mb-1 text-gray-800">{{ menuItem.name }}</h3>
              <p class="text-sm text-gray-600 mb-2">{{ menuItem.description }}</p>
              <p class="text-gray-800">{{ menuItem.price.toLocaleString() }}원</p>
            </div>
            <button
              @click="addToCart(menuItem.id)"
              class="border-2 border-gray-400 bg-gray-200 text-gray-800 hover:bg-gray-300 w-10 h-10 rounded flex items-center justify-center transition-colors"
            >
              <i data-lucide="plus" class="w-4 h-4"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'StoreMenu',
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
  emits: ['add-to-cart'],
  setup(props, { emit }) {
    const router = useRouter()
    
    const addToCart = (menuItemId) => {
      emit('add-to-cart', menuItemId)
    }

    const goToMenuDetail = (menuItemId) => {
      router.push(`/customer/stores/${props.storeId}/menu/${menuItemId}`)
    }

    return {
      addToCart,
      goToMenuDetail,
    }
  },
}
</script>
