<template>
  <div class="p-6 max-w-3xl mx-auto">
    <h1 class="text-2xl font-bold mb-6">주소 관리</h1>

    <!-- 새 주소 등록 -->
    <div class="mb-6 p-4 border rounded shadow-sm">
      <h2 class="text-lg font-medium mb-2">새 주소 등록</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <input v-model="newAddress.addressName" placeholder="주소 이름" class="border p-2 rounded"/>
        <input v-model="newAddress.address" placeholder="주소" class="border p-2 rounded"/>
        <input v-model="newAddress.detailAddress" placeholder="상세 주소" class="border p-2 rounded"/>
        <input v-model="newAddress.zipcode" placeholder="우편번호" class="border p-2 rounded"/>
        <label class="flex items-center gap-2">
          <input type="checkbox" v-model="newAddress.isDefault"/>
          기본 주소
        </label>
      </div>
      <button @click="addAddress" class="mt-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
        등록
      </button>
    </div>

    <!-- 주소 목록 -->
    <div>
      <h2 class="text-lg font-medium mb-2">등록된 주소</h2>
      <div v-if="addresses.length === 0" class="text-gray-500">등록된 주소가 없습니다.</div>
      <ul class="space-y-4">
        <li v-for="addr in addresses" :key="addr.id" class="p-4 border rounded flex justify-between items-center">
          <div>
            <p class="font-semibold">{{ addr.addressName }} <span v-if="addr.isDefault" class="text-sm text-blue-500">(기본)</span></p>
            <p>{{ addr.address }} {{ addr.detailAddress }} {{ addr.zipcode }}</p>
          </div>
          <div class="flex gap-2">
            <button @click="editAddress(addr)" class="bg-yellow-400 px-3 py-1 rounded hover:bg-yellow-500">수정</button>
            <button @click="deleteAddress(addr.id)" class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600">삭제</button>
          </div>
        </li>
      </ul>
    </div>

    <!-- 수정 모달 -->
    <div v-if="editingAddress" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
      <div class="bg-white p-6 rounded shadow-md w-96">
        <h3 class="text-lg font-medium mb-4">주소 수정</h3>
        <div class="grid grid-cols-1 gap-3">
          <input v-model="editingAddress.addressName" placeholder="주소 이름" class="border p-2 rounded"/>
          <input v-model="editingAddress.address" placeholder="주소" class="border p-2 rounded"/>
          <input v-model="editingAddress.detailAddress" placeholder="상세 주소" class="border p-2 rounded"/>
          <input v-model="editingAddress.zipcode" placeholder="우편번호" class="border p-2 rounded"/>
          <label class="flex items-center gap-2">
            <input type="checkbox" v-model="editingAddress.isDefault"/>
            기본 주소
          </label>
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <button @click="editingAddress=null" class="bg-gray-300 px-3 py-1 rounded">취소</button>
          <button @click="updateAddress" class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600">저장</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '../../api/auth/axios.js';

export default {
  name: 'AddressManagement',
  data() {
    return {
      addresses: [],
      newAddress: {
        addressName: '',
        address: '',
        detailAddress: '',
        zipcode: '',
        isDefault: false,
      },
      editingAddress: null, // 수정할 주소 객체
    };
  },
  methods: {
    async loadAddresses() {
      try {
        const res = await api.get('/users/me/addresses');
        this.addresses = res.data;
      } catch (err) {
        console.error('주소 조회 실패:', err);
        alert('주소 조회에 실패했습니다.');
      }
    },
    async addAddress() {
      try {
        const res = await api.post('/users/me/addresses', this.newAddress);
        this.addresses.push(res.data);
        this.newAddress = { addressName:'', address:'', detailAddress:'', zipcode:'', isDefault:false };
      } catch (err) {
        console.error('주소 등록 실패:', err);
        alert('주소 등록에 실패했습니다.');
      }
    },
    editAddress(addr) {
      // 주소 객체를 복사해서 수정 모달에 바인딩
      this.editingAddress = { ...addr };
    },
    async updateAddress() {
      try {
        const res = await api.patch(`/users/me/addresses/${this.editingAddress.id}`, this.editingAddress);
        // 목록 업데이트
        const index = this.addresses.findIndex(a => a.id === res.data.id);
        if (index !== -1) this.addresses.splice(index, 1, res.data);
        this.editingAddress = null;
      } catch (err) {
        console.error('주소 수정 실패:', err);
        alert('주소 수정에 실패했습니다.');
      }
    },
    async deleteAddress(id) {
      if (!confirm('정말 삭제하시겠습니까?')) return;
      try {
        await api.delete(`/users/me/addresses/${id}`);
        this.addresses = this.addresses.filter(a => a.id !== id);
      } catch (err) {
        console.error('주소 삭제 실패:', err);
        alert('주소 삭제에 실패했습니다.');
      }
    },
  },
  mounted() {
    this.loadAddresses();
  },
};
</script>

<style scoped>
/* 필요한 경우 TailwindCSS 외 커스텀 스타일 */
</style>
