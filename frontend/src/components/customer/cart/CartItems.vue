<template>
  <div class="space-y-4">
    <div v-for="item in cartItems" :key="item.id" class="border-2 border-gray-400 bg-white rounded-lg">
      <div class="p-4">
        <div class="flex items-center gap-4">
          <div class="image-placeholder w-16 h-16">
            <span>IMAGE</span>
          </div>
          <div class="flex-1">
            <h3 class="mb-1 text-gray-800">{{ item.name }}</h3>
            <p class="text-gray-800">{{ item.price.toLocaleString() }}원</p>
          </div>
          <div class="flex items-center gap-2">
            <button
              @click="updateQuantity(item.id, item.quantity - 1)"
              class="border border-gray-400 text-gray-700 w-8 h-8 rounded flex items-center justify-center hover:bg-gray-200 transition-colors"
            >
              <i data-lucide="minus" class="w-4 h-4"></i>
            </button>
            <span class="w-8 text-center text-gray-800">{{ item.quantity }}</span>
            <button
              @click="updateQuantity(item.id, item.quantity + 1)"
              class="border border-gray-400 text-gray-700 w-8 h-8 rounded flex items-center justify-center hover:bg-gray-200 transition-colors"
            >
              <i data-lucide="plus" class="w-4 h-4"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CartItems',
  props: {
    cartItems: {
      type: Array,
      required: true,
    },
  },
  emits: ['update-quantity'],
  setup(props, { emit }) {
    const updateQuantity = (itemId, newQuantity) => {
      emit('update-quantity', itemId, newQuantity)
    }

    return {
      updateQuantity,
    }
  },
}
</script>
