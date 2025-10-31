<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const orderInfo = JSON.parse(localStorage.getItem("orderInfo") || "{}");
const user = ref(null);
const orderId = `Order-${Date.now()}`;
//const orderIdString = orderInfo.orderIdString;
const amount = ref(orderInfo.totalAmount || 0);
const widgetsReady = ref(false);
let widgets = null;

onMounted(async () => {
  if (!window.TossPayments) return;

  const res = await axios.get("/api/user/me");
  user.value = res.data;

  const tossPayments = window.TossPayments("test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm");
  const customerKey = "Pu_CmJW3lO06qzdfilC8J";
  widgets = tossPayments.widgets({ customerKey });

  await widgets.setAmount({ currency: "KRW", value: amount.value });

  await Promise.all([
    widgets.renderPaymentMethods({ selector: "#payment-method", variantKey: "DEFAULT" }),
    widgets.renderAgreement({ selector: "#agreement", variantKey: "AGREEMENT" }),
  ]);

  widgetsReady.value = true;
});

const requestPayment = async () => {
  if (!widgets) return;

  if (!orderInfo.orderIdString) {
    alert("주문 정보가 없습니다!");
    return;
  }


  await widgets.requestPayment({
    orderId: orderId,
    orderName: orderInfo.items.map(item => item.name).join(", "),
    successUrl: window.location.origin + "/success",
    failUrl: window.location.origin + "/fail",
    customerEmail: user.value.email,
    customerName: user.value.name,
    customerMobilePhone: orderInfo.phoneNumber,
  });
};
</script>

<template>
  <div class="payment-container">
    <!-- 결제 UI -->
    <div id="payment-method"></div>

    <!-- 이용약관 UI -->
    <div id="agreement"></div>

    <!-- 결제하기 버튼 -->
    <button class="button" :disabled="!widgetsReady" @click="requestPayment">
      결제하기 ({{ amount.toLocaleString() }}원)
    </button>
  </div>
</template>

<style scoped>
.payment-container {
  margin: 20px;
}

.button {
  margin-top: 30px;
  padding: 10px 20px;
  background: #0064ff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.button:disabled {
  background: #999;
  cursor: not-allowed;
}
</style>
