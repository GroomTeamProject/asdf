<template>
  <CustomerContainer>
    <div class="grid lg:grid-cols-2 gap-8 py-6">
      <div class="bg-white rounded-lg overflow-hidden shadow-sm">
        <MenuInfo :menuItem="menuItem" />
      </div>

      <div class="space-y-6">
        <MenuOptions :menuOptions="menuOptions" @optionsChanged="handleOptionsChanged" />
        <QuantitySelector
          :price="menuItem.price"
          :selectedOptions="selectedOptions"
          :menuOptions="menuOptions"
          :menuId="menuId"
          @addToCart="handleAddToCart"
        />
      </div>
    </div>
  </CustomerContainer>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import CustomerContainer from '@/components/customer/CustomerContainer.vue'
import MenuInfo from '@/components/customer/menuDetail/MenuInfo.vue'
import MenuOptions from '@/components/customer/menuDetail/MenuOptions.vue'
import QuantitySelector from '@/components/customer/menuDetail/QuantitySelector.vue'
import { customerApi } from '@/apis/customerApi'
import { cartService } from '@/services/customer/cartService'
import router from '@/router'

export default {
  name: 'MenuDetail',
  components: {
    CustomerContainer,
    MenuInfo,
    MenuOptions,
    QuantitySelector,
  },
  setup() {
    const route = useRoute()

    // 반응형 데이터
    const selectedOptions = ref({})
    const menuId = ref(route.params.menuId)
    const menuItem = ref({})
    const menuOptions = ref([])
    const storeInfo = ref({})

    const loadMenuData = async (id) => {
      const menuData = await customerApi.getMenuById(id)
      if (menuData) {
        menuItem.value = menuData
      } else {
        console.error('메뉴 정보를 찾을 수 없습니다:', id)
        router.push('/customer/stores')
      }
    }
    
    const handleAddToCart = (cartItem) => {
      // cartService를 통해 장바구니에 추가 (깊은 복사로 참조 문제 해결)
      const result = cartService.addMenuToCart({
        id: menuItem.value.id,
        name: menuItem.value.name,
        price: menuItem.value.price,
        selectedOptions: JSON.parse(JSON.stringify(cartItem.selectedOptions)), // 깊은 복사
        menuOptions: JSON.parse(JSON.stringify(menuOptions.value)), // 깊은 복사
        quantity: cartItem.quantity,
        totalPrice: cartItem.totalPrice,
        // 가게 정보 추가
        storeId: menuItem.value.storeId,
        storeName: storeInfo.value.name,
        storeInfo: JSON.parse(JSON.stringify(storeInfo.value)) // 깊은 복사
      })
      
      if (result.success) {
        console.log('장바구니에 추가됨:', result.message)
        // TODO: 성공 토스트 표시
      } else {
        console.error('장바구니 추가 실패:', result.message)
        // TODO: 에러 토스트 표시
      }
    }

    const handleOptionsChanged = (newOptions) => {
      selectedOptions.value = newOptions
    }

    onMounted(async () => {
      await loadMenuData(route.params.menuId)
      menuOptions.value = await customerApi.getMenuOptions(route.params.menuId)
      
      // 가게 정보도 로드
      if (menuItem.value.storeId) {
        storeInfo.value = await customerApi.getStoreById(menuItem.value.storeId)
      }
    })

    return {
      selectedOptions,
      menuItem,
      menuOptions,
      menuId,
      handleOptionsChanged,
      handleAddToCart,
    }
  },
}
</script>
