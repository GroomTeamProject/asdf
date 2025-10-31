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
import { menuService } from '@/services/customer/menuService'
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

    const loadMenuData = async (storeId, id) => {
      try {
        const result = await menuService.getMenuById(storeId, id)
        menuItem.value = result.data
      } catch (error) {
        console.error('메뉴 정보를 찾을 수 없습니다:', storeId, id)
        router.push('/customer/stores')
      }
    }
    
    const handleAddToCart = (cartItem) => {
      console.log('=== 장바구니 추가 디버깅 ===')
      console.log('1. menuItem.value:', menuItem.value)
      console.log('2. storeInfo.value:', storeInfo.value)
      console.log('3. route.params:', route.params)
      
      const storeId = menuItem.value.store?.id || menuItem.value.storeId || route.params.id
      const storeName = storeInfo.value?.name || menuItem.value.store?.name
      
      console.log('4. 계산된 storeId:', storeId)
      console.log('5. 계산된 storeName:', storeName)
      
      const cartData = {
        id: menuItem.value.id,
        name: menuItem.value.name,
        price: menuItem.value.price,
        selectedOptions: JSON.parse(JSON.stringify(cartItem.selectedOptions)), // 깊은 복사
        menuOptions: JSON.parse(JSON.stringify(menuOptions.value)), // 깊은 복사
        quantity: cartItem.quantity,
        totalPrice: cartItem.totalPrice,
        // 가게 정보 추가 (엔티티 구조에 맞게)
        storeId: storeId,
        storeName: storeName,
        storeInfo: JSON.parse(JSON.stringify(storeInfo.value)) // 깊은 복사
      }
      
      console.log('6. 장바구니에 추가할 데이터:', cartData)
      
      // cartService를 통해 장바구니에 추가 (깊은 복사로 참조 문제 해결)
      const result = cartService.addMenuToCart(cartData)
      
      if (result.success) {
        console.log('장바구니에 추가됨:', result.message)
        // TODO: 성공 토스트 표시
      } else {
        console.error('장바구니 추가 실패:', result.message)
        // TODO: 에러 토스트 표시
      }
    }

    const handleOptionsChanged = (newOptions) => {
      console.log('옵션 변경됨:', newOptions)
      console.log('옵션 키들:', Object.keys(newOptions))
      console.log('옵션 값들:', Object.values(newOptions))
      selectedOptions.value = newOptions
    }

    onMounted(async () => {
      await loadMenuData(route.params.storeId, route.params.menuId)
      
      // 백엔드에서 메뉴 조회 시 옵션과 하위 항목을 함께 반환하므로
      // menuItem.value.options에서 직접 사용
      if (menuItem.value.options && menuItem.value.options.length > 0) {
        menuOptions.value = menuItem.value.options
      } else {
        // 옵션이 없는 경우 빈 배열
        menuOptions.value = []
      }
      
      // 가게 정보도 로드
      if (menuItem.value.store && menuItem.value.store.id) {
        storeInfo.value = menuItem.value.store
      } else {
        // 메뉴에 가게 정보가 없으면 URL에서 storeId를 가져와서 조회
        // 라우터에서 :id가 storeId가 됨
        const storeId = route.params.id || menuItem.value.storeId
        
        if (storeId) {
          const result = await menuService.getStoreById(storeId)
          storeInfo.value = result.data
        }
      }
      
    })

    return {
      selectedOptions,
      menuItem,
      menuOptions,
      menuId,
      storeInfo,
      handleOptionsChanged,
      handleAddToCart,
    }
  },
}
</script>
