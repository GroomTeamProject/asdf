<template>
  <div class="bg-white rounded-lg p-6 shadow-sm">
    <h2 class="text-lg font-semibold text-gray-800 mb-4">옵션 선택</h2>

    <!-- 동적 옵션 그룹들 -->
    <div v-for="(optionGroup, index) in menuOptions" :key="optionGroup.id" :class="{ 'mb-6': index < menuOptions.length - 1 }">
      <h3 class="text-md font-medium mb-3 flex items-center" :class="getOptionGroupTitleClass(optionGroup)">
        <span class="w-2 h-2 rounded-full mr-2" :class="getOptionGroupColor(optionGroup.name)"></span>
        {{ optionGroup.name }}
        <span v-if="optionGroup.isRequired" class="text-red-500 ml-1">*</span>
        <span v-if="optionGroup.isRequired && !selectedOptions[optionGroup.id]" class="text-xs text-red-500 ml-2 bg-red-50 px-2 py-1 rounded">
          선택 필요
        </span>
      </h3>
      <div class="space-y-2">
        <div
          v-for="item in optionGroup.items"
          :key="item.id"
          class="option-item p-3 border rounded-lg transition-all duration-200 cursor-pointer"
          :class="getOptionItemClass(optionGroup.id, item.id, optionGroup.name)"
          @click="updateOption(optionGroup.id, item.id)"
        >
          <div class="flex items-center justify-between">
            <div>
              <span class="font-medium" :class="getOptionTextClass(optionGroup.id, item.id, optionGroup.name)">
                {{ item.name }}
              </span>
              <span v-if="item.additionalPrice > 0" :class="getOptionPriceClass(optionGroup.name)"> 
                +{{ item.additionalPrice.toLocaleString() }}원 
              </span>
            </div>
            <div
              class="w-4 h-4 rounded-full border-2 flex items-center justify-center"
              :class="getOptionRadioClass(optionGroup.id, item.id, optionGroup.name)"
            >
              <div v-if="selectedOptions[optionGroup.id] === item.id" class="w-2 h-2 bg-white rounded-full"></div>
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
  mounted() {
    // 필수 옵션만 기본값 설정 (각 그룹의 첫 번째 아이템 선택)
    const initialOptions = {}
    this.menuOptions.forEach((optionGroup) => {
      if (optionGroup.isRequired && optionGroup.items && optionGroup.items.length > 0) {
        initialOptions[optionGroup.id] = optionGroup.items[0].id
      }
      // 필수가 아닌 옵션은 초기값을 설정하지 않음 (사용자가 직접 선택해야 함)
    })
    this.selectedOptions = initialOptions
    this.$emit('optionsChanged', JSON.parse(JSON.stringify(this.selectedOptions)))
  },
  methods: {
    updateOption(optionGroupId, itemId) {
      console.log('옵션 선택:', optionGroupId, itemId)
      this.selectedOptions[optionGroupId] = itemId
      // 부모에게 변경사항 알림 (깊은 복사로 전달)
      this.$emit('optionsChanged', JSON.parse(JSON.stringify(this.selectedOptions)))
    },
    getOptionGroupColor(optionName) {
      if (optionName.includes('맵기')) return 'bg-red-400'
      if (optionName.includes('사이즈')) return 'bg-blue-400'
      return 'bg-gray-400'
    },
    getOptionItemClass(optionGroupId, itemId, optionName) {
      const isSelected = this.selectedOptions[optionGroupId] === itemId
      if (optionName.includes('맵기')) {
        return isSelected ? 'border-red-400 bg-red-50' : 'border-gray-200 hover:border-red-300'
      }
      if (optionName.includes('사이즈')) {
        return isSelected ? 'border-blue-400 bg-blue-50' : 'border-gray-200 hover:border-blue-300'
      }
      return isSelected ? 'border-gray-400 bg-gray-50' : 'border-gray-200 hover:border-gray-300'
    },
    getOptionTextClass(optionGroupId, itemId, optionName) {
      const isSelected = this.selectedOptions[optionGroupId] === itemId
      if (optionName.includes('맵기')) {
        return isSelected ? 'text-red-800' : 'text-gray-700'
      }
      if (optionName.includes('사이즈')) {
        return isSelected ? 'text-blue-800' : 'text-gray-700'
      }
      return isSelected ? 'text-gray-800' : 'text-gray-700'
    },
    getOptionPriceClass(optionName) {
      if (optionName.includes('맵기')) return 'text-red-600 ml-2'
      if (optionName.includes('사이즈')) return 'text-blue-600 ml-2'
      return 'text-gray-600 ml-2'
    },
    getOptionRadioClass(optionGroupId, itemId, optionName) {
      const isSelected = this.selectedOptions[optionGroupId] === itemId
      if (optionName.includes('맵기')) {
        return isSelected ? 'border-red-400 bg-red-400' : 'border-gray-300'
      }
      if (optionName.includes('사이즈')) {
        return isSelected ? 'border-blue-400 bg-blue-400' : 'border-gray-300'
      }
      return isSelected ? 'border-gray-400 bg-gray-400' : 'border-gray-300'
    },
    getOptionGroupTitleClass(optionGroup) {
      const isRequired = optionGroup.isRequired
      const isSelected = this.selectedOptions[optionGroup.id] !== undefined
      
      if (isRequired && !isSelected) {
        return 'text-red-700'
      }
      return 'text-gray-700'
    },
  },
}
</script>

<style scoped>
/* 커스텀 라디오 버튼 스타일링은 이미 템플릿에서 처리됨 */
</style>
