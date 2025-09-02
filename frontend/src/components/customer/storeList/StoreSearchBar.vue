<template>
  <div class="mb-8">
    <div class="relative">
      <i data-lucide="search" class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-500 w-4 h-4"></i>
      <input
        v-model="searchQuery"
        type="text"
        :placeholder="placeholder"
        class="w-full pl-10 h-12 border-2 border-gray-400 bg-white rounded-md px-3 py-2 focus:outline-none focus:border-gray-600"
        @focus="onFocus"
        @blur="onBlur"
        @input="onInput"
      />

      <!-- Dropdown Search Results -->
      <div v-if="isSearchOpen" class="absolute top-full left-0 right-0 bg-white border-2 border-gray-400 rounded-md shadow-lg z-10 mt-1">
        <div class="p-4">
          <div class="text-gray-600 text-center py-8">검색 결과가 여기에 표시됩니다</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'StoreSearchBar',
  props: {
    placeholder: {
      type: String,
      default: '음식점이나 음식을 검색해보세요',
    },
    modelValue: {
      type: String,
      default: '',
    },
  },
  emits: ['update:modelValue', 'focus', 'blur', 'input'],
  setup(props, { emit }) {
    const searchQuery = ref(props.modelValue)
    const isSearchOpen = ref(false)

    const onFocus = () => {
      isSearchOpen.value = true
      emit('focus')
    }

    const onBlur = () => {
      isSearchOpen.value = false
      emit('blur')
    }

    const onInput = () => {
      emit('update:modelValue', searchQuery.value)
      emit('input', searchQuery.value)
    }

    return {
      searchQuery,
      isSearchOpen,
      onFocus,
      onBlur,
      onInput,
    }
  },
}
</script>
