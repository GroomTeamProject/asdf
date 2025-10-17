<template>
    <form @submit.prevent="handleSubmit">
        <input v-model="orderId" type="number" placeholder="Order ID" required />
        <input v-model="amount" type="number" placeholder="Amount" required />

        <select v-model="method">
            <option value="CARD">CARD</option>
            <option value="TRANSFER">TRANSFER</option>
            <option value="KAKAO_PAY">KAKAO_PAY</option>
            <option value="NAVER_PAY">NAVER_PAY</option>
            <option value="TOSS">TOSS</option>
            <option value="CASH">CASH</option>
        </select>

        <button type="submit">결제 생성</button>
    </form>
</template>

<script>
import { createPayment } from "../api/paymentApi";

export default {
    data() {
        return {
            orderId: "",
            amount: "",
            method: "CARD"
        };
    },
    methods: {
        async handleSubmit() {
            const data = {
                orderId: Number(this.orderId),
                paymentKey: "uniqueKey123",
                paymentMethod: this.method,
                amount: Number(this.amount),
                pgProvider: "TOSS",
                pgTid: "T123456789"
            };
            const result = await createPayment(data);
            console.log("결제 생성 결과:", result);
        }
    }
};
</script>
