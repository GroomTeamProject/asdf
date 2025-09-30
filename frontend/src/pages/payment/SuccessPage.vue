<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";

const router = useRouter();
const route = useRoute();

const amount = ref(0);
const message = ref("결제를 확인 중입니다...");
const orderItems = ref([]);
const orderId = ref(null);

onMounted(async () => {
    const orderInfo = JSON.parse(localStorage.getItem("orderInfo") || "{}");
    orderItems.value = orderInfo.items || [];

    const paymentKey = route.query.paymentKey;
    const orderIdParam = route.query.orderId;
    amount.value = parseInt(route.query.amount || "0", 10);

    if (!paymentKey) {
        message.value = "결제 키가 존재하지 않습니다. 다시 시도해주세요.";
        return;
    }

    try {
        await axios.post(`${import.meta.env.VITE_API_URL}/payments/callback`, {
            userId: localStorage.getItem("userId"),
            paymentKey: paymentKey,
            amount: amount.value,
            pgProvider: "tosspay",
            paymentMethod: "CARD",
            pgTid: null
        }, {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${localStorage.getItem("jwt")}`
            }
        });

    } catch (err) {
        console.warn("Toss API 호출 실패:", err.message);
    } finally {
        orderId.value = orderIdParam || null;
        ["cart", "orderInfo"].forEach(key => localStorage.removeItem(key));

        message.value =
            "주문과 결제가 완료되었습니다! 5초 후 고객 페이지로 이동합니다.";
        setTimeout(() => router.push("/customer"), 5000);
    }
});
</script>

<template>
  <div class="payment-success p-6 text-center">
    <h2 class="text-2xl font-bold mb-4">결제 성공</h2>

    <p v-if="orderId !== null" class="mb-2">주문 번호: {{ orderId }}</p>
    <p class="mb-2">결제금액: {{ amount.toLocaleString() }}원</p>

    <!-- 주문 상품 목록 -->
    <div v-if="orderItems.length > 0" class="mt-4 text-left">
      <h3 class="font-semibold mb-2">주문 상품</h3>
      <ul>
        <li v-for="(item, index) in orderItems" :key="index" class="flex justify-between mb-1">
          <span>{{ item.productName }} x {{ item.quantity }}</span>
          <span>{{ (item.price * item.quantity).toLocaleString() }}원</span>
        </li>
      </ul>
    </div>

    <p class="text-lg text-blue-600 mt-4">{{ message }}</p>
  </div>
</template>

<style scoped>
.payment-success {
  max-width: 500px;
  margin: 50px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}
</style>
