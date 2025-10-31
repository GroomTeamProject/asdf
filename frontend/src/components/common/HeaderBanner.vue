<template>
  <div class="bg-gradient-to-r from-blue-600 to-purple-600 text-white p-6">
    <div class="max-w-6xl mx-auto">
      <h1 class="text-2xl font-bold mb-2">{{ title }}</h1>
      <p v-if="description" class="text-blue-100">{{ description }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HeaderBanner',
  props: {
    title: {
      type: String,
      required: true
    },
    description: {
      type: String,
      required: false,
    }
  }
}
</script>
