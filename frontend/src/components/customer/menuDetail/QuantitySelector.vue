<template>
  <div class="bg-white rounded-lg p-6 shadow-sm">
    <div class="space-y-4">
      <div>
        <h3 class="text-lg font-semibold text-gray-800 mb-3">수량</h3>
        <div class="flex items-center gap-4">
          <button
            @click="decreaseQuantity"
            class="w-10 h-10 border-2 border-gray-300 rounded-lg flex items-center justify-center hover:bg-gray-100 transition-colors"
            :disabled="quantity <= 1"
          >
            <span class="text-gray-600 text-lg">-</span>
          </button>
          <span class="text-xl font-semibold text-gray-800 min-w-[3rem] text-center">{{ quantity }}</span>
          <button
            @click="increaseQuantity"
            class="w-10 h-10 border-2 border-gray-300 rounded-lg flex items-center justify-center hover:bg-gray-100 transition-colors"
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
          class="w-full h-12 text-base font-semibold bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:shadow-lg transition-all duration-200"
        >
          장바구니에 담기
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'QuantitySelector',
  props: {
    basePrice: {
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
      let total = this.basePrice * this.quantity

      // 선택된 옵션들의 가격 추가
      Object.values(this.selectedOptions).forEach((optionId) => {
        const option = this.menuOptions.find((opt) => opt.id === optionId)
        if (option) {
          total += option.price * this.quantity
        }
      })

      return total
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