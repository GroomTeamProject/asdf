<template>
  <div
    @click="onClick"
    class="cursor-pointer border-2 border-gray-400 hover:bg-gray-200 transition-colors bg-white rounded-lg overflow-hidden"
  >
    <div class="image-placeholder aspect-video w-full">
      <span>IMAGE</span>
    </div>
    <div class="p-4">
      <h3 class="mb-2 text-gray-800">{{ store.name }}</h3>
      <div class="flex items-center gap-2 text-sm text-gray-600 mb-2">
        <i data-lucide="star" class="w-4 h-4 text-gray-600"></i>
        <span>{{ store.rating }}</span>
        <i data-lucide="clock" class="w-4 h-4 ml-2 text-gray-600"></i>
        <span>{{ store.deliveryTime }}</span>
      </div>
      <p class="text-sm text-gray-600">{{ store.description }}</p>
      <p class="text-sm mt-2 text-gray-800">배달비 {{ store.deliveryFee.toLocaleString() }}원</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'StoreCard',
  props: {
    store: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const onClick = () => {
      console.log('Selected store:', props.store.id, props.store.name)
    }

    return {
      onClick,
    }
  },
}
</script>
