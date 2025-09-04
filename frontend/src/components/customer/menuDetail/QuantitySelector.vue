<template>
  <div class="bg-white rounded-lg border border-gray-200 shadow-sm p-6">
    <div class="space-y-4">
      <div>
        <h3 class="text-lg font-semibold text-gray-800 mb-3">수량</h3>
        <div class="flex items-center gap-4">
          <button
            @click="decreaseQuantity"
            class="w-10 h-10 border border-gray-300 rounded-lg flex items-center justify-center hover:bg-gray-100 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-1"
            :disabled="quantity <= 1"
          >
            <span class="text-gray-600 text-lg">-</span>
          </button>
          <span class="text-xl font-semibold text-gray-800 min-w-[3rem] text-center">{{ quantity }}</span>
          <button
            @click="increaseQuantity"
            class="w-10 h-10 border border-gray-300 rounded-lg flex items-center justify-center hover:bg-gray-100 transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-1"
          >
            <span class="text-gray-600 text-lg">+</span>
          </button>
        </div>
      </div>

      <div class="border-t pt-4">
        <div class="flex justify-between items-center mb-4">
          <span class="font-semibold text-gray-800">총 주문 금액</span>
          <span class="text-xl font-bold text-blue-600">{{ totalPrice.toLocaleString() }}원</span>
        </div>

        <button
          @click="addToCart"
          :disabled="!allRequiredOptionsSelected"
          :class="[
            'w-full h-12 text-base font-semibold rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2',
            allRequiredOptionsSelected
              ? 'bg-gradient-to-r from-blue-600 to-blue-700 text-white hover:shadow-lg cursor-pointer hover:from-blue-700 hover:to-blue-800'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          ]"
        >
          {{ allRequiredOptionsSelected ? '장바구니에 담기' : '필수 옵션을 선택해주세요' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'QuantitySelector',
  props: {
    price: {
      type: Number,
      required: true
    },
    selectedOptions: {
      type: Object,
      required: true
    },
    menuOptions: {
      type: Array,
      required: true
    },
    menuId: {
      type: String,
      required: true
    }
  },
  emits: ['addToCart'],
  data() {
    return {
      quantity: 1
    }
  },
  computed: {
    totalPrice() {
      let total = this.price * this.quantity

      // 선택된 옵션들의 가격 추가
      Object.values(this.selectedOptions).forEach((optionItemId) => {
        // 새로운 구조에서는 menuOptions의 items에서 찾아야 함
        this.menuOptions.forEach(option => {
          const optionItem = option.items?.find(item => item.id === optionItemId)
          if (optionItem) {
            total += optionItem.additionalPrice * this.quantity
          }
        })
      })

      return total
    },
    // 필수 옵션이 모두 선택되었는지 확인
    allRequiredOptionsSelected() {
      const requiredOptions = this.menuOptions.filter(option => option.isRequired)
      
      return requiredOptions.every(option => {
        return this.selectedOptions[option.id] !== undefined
      })
    },
    // 미선택된 필수 옵션들
    missingRequiredOptions() {
      const requiredOptions = this.menuOptions.filter(option => option.isRequired)
      
      return requiredOptions.filter(option => {
        return this.selectedOptions[option.id] === undefined
      }).map(option => option.name)
    }
  },
  methods: {
    increaseQuantity() {
      this.quantity++
    },
    decreaseQuantity() {
      if (this.quantity > 1) {
        this.quantity--
      }
    },
    addToCart() {
      // 필수 옵션 검증
      if (!this.allRequiredOptionsSelected) {
        const missingOptions = this.missingRequiredOptions.join(', ')
        alert(`다음 필수 옵션을 선택해주세요: ${missingOptions}`)
        return
      }

      const cartItem = {
        menuId: this.menuId,
        quantity: this.quantity,
        selectedOptions: this.selectedOptions,
        totalPrice: this.totalPrice,
      }

      console.log('장바구니에 추가:', cartItem)
      this.$emit('addToCart', cartItem)
    }
  }
}
</script> 