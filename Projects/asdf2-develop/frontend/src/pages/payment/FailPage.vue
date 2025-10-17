<template>
  <div class="fail-page">
    <h2>결제 실패</h2>
    <p v-if="code"><strong>에러코드:</strong> {{ code }}</p>
    <p v-if="message"><strong>실패 사유:</strong> {{ message }}</p>
    <p v-else>알 수 없는 이유로 결제에 실패했습니다.</p>
    <button @click="goHome">메인으로 돌아가기</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();

const code = ref('');
const message = ref('');

onMounted(() => {
  code.value = route.query.code || '';
  message.value = route.query.message || '';
});

const goHome = () => {
  router.push('/');
};
</script>

<style scoped>
.fail-page {
  margin: 40px;
  text-align: center;
}

button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
</style>
