<template>
  <div
    @click="onClick"
    class="cursor-pointer bg-white rounded-lg border border-gray-200 shadow-sm hover:shadow-md transition-all duration-200 p-4 text-center"
  >
    <div class="w-8 h-8 bg-gray-100 border border-gray-300 mx-auto mb-2 flex items-center justify-center rounded-full">
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
