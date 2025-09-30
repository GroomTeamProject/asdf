<script setup>
import { ref, computed, onMounted } from "vue";
import { ShoppingCart, Plus, Minus, ArrowLeft } from "lucide-vue-next";
import { useRouter } from "vue-router";

const router = useRouter();
const cart = ref([]);

// 장바구니 로드
const loadCart = () => {
    cart.value = JSON.parse(localStorage.getItem("cart") || "[]");
};

// 총 금액 계산
const totalPrice = computed(() =>
    cart.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
);

// 수량 업데이트
const updateQuantity = (itemId, newQuantity) => {
    if (newQuantity <= 0) {
        cart.value = cart.value.filter((item) => item.id !== itemId);
    } else {
        const item = cart.value.find((item) => item.id === itemId);
        if (item) item.quantity = newQuantity;
    }
    localStorage.setItem("cart", JSON.stringify(cart.value));
};

// 주문하기 (결제 페이지로 이동)
const placeOrder = async () => {
    if (cart.value.length === 0) {
        alert("장바구니가 비어있습니다.");
        return;
    }

    const confirmed = confirm(`총 ${totalPrice.value.toLocaleString()}원을 주문하시겠습니까?`);
    if (confirmed) {
        // 결제 페이지에서 사용할 데이터 저장
        localStorage.setItem("cartForCheckout", JSON.stringify(cart.value));
        router.push({
            path: "/ordercheck",
            query: { amount: totalPrice.value.toString() }
        });
    }
};

// 뒤로가기
const goBack = () => {
    router.push("/customer");
};

// 페이지 마운트 시 장바구니 로드
onMounted(loadCart);
</script>

<template>
    <div class="min-h-screen bg-gray-100 p-4">
        <!-- Header -->
        <header class="bg-white border-b-2 border-gray-400 p-4 flex items-center gap-4">
            <button @click="goBack" class="flex items-center gap-2 border px-2 py-1 rounded hover:bg-gray-200">
                <ArrowLeft class="w-4 h-4" />
                돌아가기
            </button>
            <h1 class="text-xl font-semibold text-gray-800">장바구니</h1>
        </header>

        <!-- 장바구니 내용 -->
        <main class="mt-4">
            <div v-if="cart.length === 0" class="text-center py-12 text-gray-600">
                <ShoppingCart class="w-12 h-12 mx-auto mb-4" />
                장바구니가 비어있습니다.
            </div>

            <div v-else class="space-y-4">
                <div v-for="item in cart" :key="item.id" class="flex items-center gap-4 border bg-white rounded-lg p-4">
                    <div class="w-16 h-16 bg-gray-200 flex items-center justify-center text-gray-500">
                        IMAGE
                    </div>
                    <div class="flex-1">
                        <h3 class="text-gray-800">{{ item.name }}</h3>
                        <p class="text-gray-800">{{ item.price.toLocaleString() }}원</p>
                    </div>
                    <div class="flex items-center gap-2">
                        <button @click="updateQuantity(item.id, item.quantity - 1)"
                            class="w-8 h-8 border rounded flex items-center justify-center hover:bg-gray-200">
                            <Minus class="w-4 h-4" />
                        </button>
                        <span class="w-8 text-center text-gray-800">{{ item.quantity }}</span>
                        <button @click="updateQuantity(item.id, item.quantity + 1)"
                            class="w-8 h-8 border rounded flex items-center justify-center hover:bg-gray-200">
                            <Plus class="w-4 h-4" />
                        </button>
                    </div>
                </div>

                <!-- 주문 요약 -->
                <div class="mt-6 flex justify-between items-center border-t pt-4">
                    <p class="text-lg font-semibold">총 금액: {{ totalPrice.toLocaleString() }}원</p>
                    <button @click="placeOrder" class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
                        주문하기
                    </button>
                </div>
            </div>
        </main>
    </div>
</template>
