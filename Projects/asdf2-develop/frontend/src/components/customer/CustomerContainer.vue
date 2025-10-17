<template>
  <!-- 페이지별 레이아웃 컨테이너 -->
  <div class="w-full h-full" :class="containerClass">
    <slot />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  // 컨테이너 너비 설정
  maxWidth: {
    type: String,
    default: '6xl', // max-w-6xl
    validator: (value) => ['2xl', '4xl', '6xl', 'full'].includes(value)
  },
  // 패딩 설정
  padding: {
    type: String,
    default: '4', // p-4
    validator: (value) => ['2', '4', '6', '8'].includes(value)
  },
  // 추가 클래스
  customClass: {
    type: String,
    default: ''
  }
})

// 컨테이너 클래스 계산
const containerClass = computed(() => {
  const classes = []
  
  // 최대 너비 - 자주 사용하는 것만
  const maxWidthClasses = {
    '2xl': 'max-w-2xl',
    '4xl': 'max-w-4xl', 
    '6xl': 'max-w-6xl',
    'full': ''
  }
  
  if (props.maxWidth !== 'full' && maxWidthClasses[props.maxWidth]) {
    classes.push(maxWidthClasses[props.maxWidth])
  }
  classes.push('mx-auto')
  
  // 패딩 - 자주 사용하는 것만
  const paddingClasses = {
    '2': 'p-2',
    '4': 'p-4',
    '6': 'p-6',
    '8': 'p-8'
  }
  
  if (paddingClasses[props.padding]) {
    classes.push(paddingClasses[props.padding])
  }
  
  // 커스텀 클래스
  if (props.customClass) {
    classes.push(props.customClass)
  }
  
  return classes.join(' ')
})
</script>