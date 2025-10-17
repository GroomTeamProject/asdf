<script setup>
import { useRouter } from 'vue-router'
import { ShoppingCart } from 'lucide-vue-next'

const router = useRouter()

// 예시 상품
const products = [
    { id: 1, name: '상품 A', price: 10000 },
    { id: 2, name: '상품 B', price: 20000 },
    { id: 3, name: '상품 C', price: 15000 },
]

// 장바구니에 추가
const addToCart = (product) => {
    const cart = JSON.parse(localStorage.getItem('cart') || '[]')
    const existing = cart.find(item => item.id === product.id)
    if (existing) {
        existing.quantity += 1
    } else {
        cart.push({ ...product, quantity: 1 })
    }
    localStorage.setItem('cart', JSON.stringify(cart))
    alert(`${product.name}이 장바구니에 추가되었습니다!`)
}

// 장바구니 페이지 이동
const goCart = () => router.push('/cart')

// 주문 상태 확인 페이지 이동
const goOrderStatus = () => router.push('/orderstatus')
</script>

<template>
    <div class="p-4 space-y-4">
        <h1 class="text-2xl font-bold">상품 목록</h1>

        <div class="flex gap-2">
            <button @click="goCart" class="flex items-center gap-1 px-3 py-1 bg-gray-200 rounded hover:bg-gray-300">
                <ShoppingCart class="w-5 h-5" /> 장바구니
            </button>

            <button @click="goOrderStatus" class="px-3 py-1 bg-green-500 text-white rounded hover:bg-green-600">
                주문 확인
            </button>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mt-4">
            <div v-for="product in products" :key="product.id" class="p-4 border rounded bg-white flex flex-col gap-2">
                <div class="h-32 bg-gray-200 flex items-center justify-center text-gray-500">IMAGE</div>
                <h2 class="font-semibold">{{ product.name }}</h2>
                <p>{{ product.price.toLocaleString() }}원</p>
                <button @click="addToCart(product)" class="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600">
                    장바구니 담기
                </button>
            </div>
        </div>
    </div>
</template>
