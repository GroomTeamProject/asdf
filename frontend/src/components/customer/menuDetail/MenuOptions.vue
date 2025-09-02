<template>
  <div class="bg-white rounded-lg p-6 shadow-sm">
    <h2 class="text-lg font-semibold text-gray-800 mb-4">옵션 선택</h2>

    <!-- 옵션 그룹 1 -->
    <div class="mb-6">
      <h3 class="text-md font-medium text-gray-700 mb-3 flex items-center">
        <span class="w-2 h-2 bg-red-400 rounded-full mr-2"></span>
        매운맛
      </h3>
      <div class="space-y-2">
        <div
          v-for="option in spicyOptions"
          :key="option.id"
          class="option-item p-3 border rounded-lg transition-all duration-200 cursor-pointer"
          :class="[selectedOptions[option.group] === option.id ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-red-300']"
          @click="updateOption(option.group, option.id)"
        >
          <div class="flex items-center justify-between">
            <div>
              <span class="font-medium" :class="selectedOptions[option.group] === option.id ? 'text-red-800' : 'text-gray-700'">
                {{ option.name }}
              </span>
              <span v-if="option.price > 0" class="text-red-600 ml-2"> +{{ option.price.toLocaleString() }}원 </span>
            </div>
            <div
              class="w-4 h-4 rounded-full border-2 flex items-center justify-center"
              :class="selectedOptions[option.group] === option.id ? 'border-red-400 bg-red-400' : 'border-gray-300'"
            >
              <div v-if="selectedOptions[option.group] === option.id" class="w-2 h-2 bg-white rounded-full"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 사이즈 옵션 그룹 -->
    <div>
      <h3 class="text-md font-medium text-gray-700 mb-3 flex items-center">
        <span class="w-2 h-2 bg-blue-400 rounded-full mr-2"></span>
        사이즈
      </h3>
      <div class="space-y-2">
        <div
          v-for="option in sizeOptions"
          :key="option.id"
          class="option-item p-3 border rounded-lg transition-all duration-200 cursor-pointer"
          :class="[selectedOptions[option.group] === option.id ? 'border-blue-400 bg-blue-50' : 'border-gray-200 hover:border-blue-300']"
          @click="updateOption(option.group, option.id)"
        >
          <div class="flex items-center justify-between">
            <div>
              <span class="font-medium" :class="selectedOptions[option.group] === option.id ? 'text-blue-800' : 'text-gray-700'">
                {{ option.name }}
              </span>
              <span v-if="option.price > 0" class="text-blue-600 ml-2"> +{{ option.price.toLocaleString() }}원 </span>
            </div>
            <div
              class="w-4 h-4 rounded-full border-2 flex items-center justify-center"
              :class="selectedOptions[option.group] === option.id ? 'border-blue-400 bg-blue-400' : 'border-gray-300'"
            >
              <div v-if="selectedOptions[option.group] === option.id" class="w-2 h-2 bg-white rounded-full"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MenuOptions',
  props: {
    menuOptions: {
      type: Array,
      required: true,
    },
  },
  emits: ['optionsChanged'],
  data() {
    return {
      selectedOptions: {},
    }
  },
  computed: {
    spicyOptions() {
      return this.menuOptions.filter((option) => option.group === 'spicy')
    },
    sizeOptions() {
      return this.menuOptions.filter((option) => option.group === 'size')
    },
  },
  mounted() {
    // 옵션 그룹별로 기본값 설정
    const initialOptions = {}
    this.menuOptions.forEach((option) => {
      if (!initialOptions[option.group]) {
        initialOptions[option.group] = option.id
      }
    })
    this.selectedOptions = initialOptions
  },
  methods: {
    updateOption(group, value) {
      console.log('옵션 선택:', group, value)
      this.selectedOptions[group] = value
      // 부모에게 변경사항 알림
      this.$emit('optionsChanged', this.selectedOptions)
    },
  },
}
</script>

<style scoped>
/* 커스텀 라디오 버튼 스타일링은 이미 템플릿에서 처리됨 */
</style>
