<template>
  <CustomerContainer>
    <div class="grid lg:grid-cols-2 gap-8 py-6">
      <div class="bg-white rounded-lg overflow-hidden shadow-sm">
        <MenuInfo :menuItem="menuItem" />
      </div>

      <div class="space-y-6">
        <MenuOptions :menuOptions="menuOptions" @optionsChanged="handleOptionsChanged" />
        <QuantitySelector
          :basePrice="menuItem.basePrice"
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
import { mocks } from './mock'
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
    const menuItem = ref(mocks.menuDetails[menuId.value])
    const menuOptions = ref(mocks.menuOptions)

    // methods
    const loadMenuData = (id) => {
      menuId.value = id

      // mocks에서 메뉴 정보 가져오기
      const menuDetail = mocks.menuDetails[id]
      if (menuDetail) {
        menuItem.value = menuDetail
      } else {
        console.error('메뉴 정보를 찾을 수 없습니다:', id)
        router.push('/customer/stores')
      }

      console.log('메뉴 데이터 로드됨:', menuItem.value)
    }

    const handleAddToCart = (cartItem) => {
      console.log('장바구니에 추가됨:', cartItem)
      // TODO: 실제 장바구니 추가 로직 구현
    }

    const handleOptionsChanged = (newOptions) => {
      console.log('옵션 변경됨:', newOptions)
      selectedOptions.value = newOptions
    }

    onMounted(() => {
      // URL 파라미터에서 메뉴 ID 가져오기
      loadMenuData(route.params.menuId)
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
