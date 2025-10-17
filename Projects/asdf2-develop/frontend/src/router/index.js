// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/payment/Home.vue'
import Signup from '../pages/auth/Signup.vue'
import Login from '../pages/auth/Login.vue'
import Main from '../pages/auth/Main.vue'
import StoreRegistration from '../pages/owner/StoreRegistration.vue'

// 권한별 메인 페이지
import CustomerMain from '../pages/customer/CustomerMain.vue'
import OwnerMain from '../pages/owner/OwnerMain.vue'
import DriverMain from '../pages/driver/DriverMain.vue'

import DefaultLayout from '../layouts/DefaultLayout.vue'

import { customerRoutes } from './customerRoutes.js'
import CartPage from '../pages/payment/cartPage.vue'
import OrderCheck from '../pages/payment/OrderCheckout.vue'
import PaymentWidget from '../pages/payment/PaymentWidget.vue'
import SuccessPage from '../pages/payment/SuccessPage.vue'
import FailPage from '../pages/payment/FailPage.vue'
import OrderStatus from '../pages/payment/OrderStatus.vue'

import StoreList from '../pages/customer/StoreList.vue'
import StoreCard from '../components/customer/storeList/StoreCard.vue'

const routes = [
  {
    path: '/',
    redirect: '/main-page',
    component: DefaultLayout,
    children: [
      //{ path: '', component: Home },
      { path: '/signup', component: Signup },
      { path: '/login', component: Login },
      { path: '/main-page', component: Main },
      { path: '/store-card', component: StoreCard },
      { path: '/store-list', component: StoreList },
      { path: '/customer-main', component: CustomerMain, meta: { role: 'CUSTOMER' } },
      { path: '/owner-main', component: OwnerMain, meta: { role: 'OWNER' } },
      { path: '/store-registration', component: StoreRegistration, meta: { role: 'OWNER' } },
      { path: '/driver-main', component: DriverMain, meta: { role: 'RIDER' } },
      //{ path: '/dashboard', component: Dashboard }, //로그인후 이동하는 임시 페이지

      // TODO: 레이아웃에 맞춰서 수정해주세요
      { path: '/cart', component: CartPage },
      { path: '/ordercheck', component: OrderCheck },
      { path: '/payment', component: PaymentWidget },
      { path: '/success', component: SuccessPage },
      { path: '/fail', component: FailPage },
      { path: '/orderstatus', component: OrderStatus },
    ],
  },
  ...customerRoutes,
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})


//  라우터 가드 설정
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('jwt')
  const userType = localStorage.getItem('userType')

  /*// 로그인 필요 없는 페이지 : 초기화면 유저 화면으로 바꿀시
  if (['/','/main-page', '/login', '/signup','/customer-main','/store-card','/store-list'].includes(to.path)
  || to.path.startsWith('/customer/stores')) {
    return next()
  }*/

  // 로그인 필요 없는 페이지
  if (['/', '/login', '/signup','/main-page'].includes(to.path)) {
    return next()
  }

  // 로그인 안 돼 있으면 로그인 페이지로
  if (!token) {
    const answer = confirm('로그인이 필요한 페이지 입니다!\n로그인 하시겠습니까?')
    if (answer) {
      // 확인 → 로그인 페이지로 이동
      return next('/login')
    } else {
      // 취소 → 원래 가려던 페이지 머무르기
      return next(false)
    }
  }

  // 라우트 메타에 role이 있으면 체크
  if (to.meta.role && to.meta.role !== userType) {
    alert('접근 권한이 없습니다!')
    return next('/')
  }

  next()
})

export default router