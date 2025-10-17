<script setup>

import { ref, onMounted } from "vue";
import axios from "axios";

const orders = ref([]);

const fetchOrders = async () => {
    try {
        const response = await axios.get("http://localhost:8080/api/orders/all");

        orders.value = response.data.filter(order => order.status !== "CANCELED");

        console.log("주문 내역:", orders.value);
    } catch (err) {
        console.error("주문 내역 불러오기 실패:", err);
    }
};

// 주문 취소
const cancelOrder = async (orderId) => {
    if (!confirm("정말로 주문을 취소하시겠습니까?")) return;

    try {
        await axios.delete(`http://localhost:8080/api/orders/${orderId}`);
        alert("주문이 취소되고 결제가 환불되었습니다.");
        fetchOrders();
    } catch (err) {
        alert("주문 취소 실패: " + (err.response?.data || err.message));
    }
};

onMounted(() => {
    fetchOrders();
});
</script>


<template>
    <div class="orders p-4 max-w-md mx-auto">
        <h1 class="text-2xl font-bold mb-4">내 주문 내역</h1>

        <div v-if="orders.length === 0">주문 내역이 없습니다.</div>

        <ul v-else>
            <li v-for="order in orders" :key="order.id" class="border p-2 mb-2 rounded">
                <p>주문 번호: {{ order.id }}</p>
                <p>총 금액: {{ order.totalAmount?.toLocaleString() || 0 }}원</p>
                <p>
                    상태:
                    <span v-if="order.status === 'CANCELED'" class="text-red-600 font-bold">주문 취소됨</span>
                    <span v-else class="text-green-600 font-bold">{{ order.status }}</span>
                </p>

                <ul class="ml-4">
                    <li v-for="item in order.items" :key="item.id">
                        {{ item.productName }} x {{ item.quantity }} ({{ (item.price * item.quantity).toLocaleString()
                            || 0 }}원)
                    </li>
                </ul>

                <button v-if="order.status !== 'CANCELED'" @click="cancelOrder(order.id, order.status)"
                    class="mt-2 bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600">
                    주문 취소
                </button>
            </li>
        </ul>
    </div>
</template>
