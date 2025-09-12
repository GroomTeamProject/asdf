import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/Home.vue'
import Signup from '../pages/auth/Signup.vue'
import Login from '../pages/auth/Login.vue'
import MyPage from '../pages/users/MyPage.vue'
import ProfileEdit from '../pages/users/ProfileEdit.vue'
import ChangePassword from '../pages/users/ChangePassword.vue'
import AddressManagement from '../pages/users/AddressManagement.vue';
//import Dashboard from '../pages/auth/Dashboard.vue'

// 권한별 메인 페이지
import CustomerMain from '../pages/customer/CustomerMain.vue'
import OwnerMain from '../pages/owner/OwnerMain.vue'
import DriverMain from '../pages/driver/DriverMain.vue'

import StoreRegistration from '../pages/owner/StoreRegistration.vue'


// 라우트 경로 추가
const routes = [
  { path: '/', component: Home },
  { path: '/signup', component: Signup },
  { path: '/login', component: Login },
  { path: '/my-page', name: 'MyPage', component: MyPage },
  { path: '/profile', name: 'ProfileEdit', component: ProfileEdit },
  { path: '/change-password', component: ChangePassword },
  { path: '/profile/addresses', component: AddressManagement, name: 'AddressManagement' },
  { path: '/customer-main', component: CustomerMain, meta: { role: 'CUSTOMER' } },
  { path: '/owner-main', component: OwnerMain, meta: { role: 'OWNER' } },
  { path: '/store-registration', component: StoreRegistration, meta: { role: 'OWNER' } },
  { path: '/driver-main', component: DriverMain, meta: { role: 'RIDER' } }
  //{ path: '/dashboard', component: Dashboard }, //로그인후 이동하는 임시 페이지
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

//  라우터 가드 설정
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('jwt')
  const userType = localStorage.getItem('userType')

  // 로그인 필요 없는 페이지
  if (['/', '/login', '/signup'].includes(to.path)) {
    return next()
  }

  // 로그인 안 돼 있으면 로그인 페이지로
  if (!token) {
    return next('/login')
  }

  // 라우트 메타에 role이 있으면 체크
  if (to.meta.role && to.meta.role !== userType) {
    alert('접근 권한이 없습니다!')
    return next('/')
  }

  next()
})

export default router

