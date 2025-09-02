<template>
  <div
    @click="onClick"
    class="cursor-pointer border-2 border-gray-400 hover:bg-gray-200 transition-colors rounded-lg p-4 text-center bg-white"
  >
    <div class="w-8 h-8 bg-gray-300 border border-gray-400 mx-auto mb-2 flex items-center justify-center">
      <span class="text-xs text-gray-600"> {{ storeCategory.icon }}</span>
    </div>
    <p class="text-gray-800">{{ storeCategory.name }}</p>
  </div>
</template>

<script>
export default {
  name: 'StoreCategoryCard',
  props: {
    storeCategory: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    const onClick = () => {
      console.log('Selected category:', props.storeCategory.id, props.storeCategory.name)
    }

    return {
      onClick,
    }
  },
}
</script>
