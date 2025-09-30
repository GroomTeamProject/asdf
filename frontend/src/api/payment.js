import axios from "axios";

// PaymentRequest 형태로 서버에 전송
export async function sendPaymentCallback(paymentData) {
    return axios.post("http://localhost:8080/api/payments/callback", paymentData, {
        headers: { "Content-Type": "application/json" },
    });
}
