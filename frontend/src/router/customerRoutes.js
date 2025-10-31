import CustomerMain from '../pages/customer/CustomerMain.vue'
import StoreList from '../pages/customer/StoreList.vue'
import StoreDetail from '../pages/customer/StoreDetail.vue'
import MenuDetail from '../pages/customer/MenuDetail.vue'
import Cart from '../pages/customer/Cart.vue'
import Order from '../pages/customer/Order.vue'
import OrderComplete from '../pages/customer/OrderComplete.vue'
import OrderHistory from '../pages/customer/OrderHistory.vue'
import OrderHistoryDetail from '../pages/customer/OrderHistoryDetail.vue'
import MyPage from '../pages/customer/MyPage.vue'
import EditProfile from '../pages/customer/EditProfile.vue'
import AddressManagement from '../pages/customer/AddressManagement.vue'
import WriteReview from '../pages/customer/WriteReview.vue'
import ReviewList from '../pages/customer/ReviewList.vue'
import ChangePassword from '../pages/customer/ChangePassword.vue'
import Notifications from '../pages/customer/Notifications.vue'

// 레이아웃 컴포넌트
import CustomerLayout from '../layouts/customer/CustomerLayout.vue'

/**
 * 고객 라우트 설정
 */
export const customerRoutes = [
  {
    path: '/customer',
    component: CustomerLayout,
    children: [
      { path: '', component: CustomerMain },
      { path: 'stores', component: StoreList },
      { path: 'stores/:storeId', component: StoreDetail },
      { path: 'stores/:storeId/menu/:menuId', component: MenuDetail },
      { path: 'stores/:storeId/reviews', component: ReviewList, name: 'StoreReviews' },
      { path: 'cart', component: Cart },
      { path: 'order', component: Order },
      { path: 'order-complete', component: OrderComplete },
      { path: 'order-history', component: OrderHistory },
      { path: 'order-history/:orderId', component: OrderHistoryDetail },
      { path: 'mypage', component: MyPage },
      { path: 'edit-profile', component: EditProfile },
      { path: 'address-management', component: AddressManagement },
      { path: 'write-review/:orderId', component: WriteReview, name: 'WriteReview' },
      { path: 'my-reviews', component: ReviewList, name: 'MyReviews' },
      { path: 'change-password', name: 'ChangePassword', component: ChangePassword },
      { path: 'notifications', component: Notifications },
    ],
  },
]
