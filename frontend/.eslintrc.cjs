module.exports = {
  root: true,
  env: { browser: true, es2022: true },
  extends: ['eslint:recommended','plugin:vue/vue3-recommended'],
  plugins: ['security','no-unsanitized'],
  rules: {
    'vue/no-v-html': 'error',
    'no-eval': 'error',
    'no-new-func': 'error',
    'no-unsanitized/method': 'error',
    'no-unsanitized/property': 'error',
    'security/detect-eval-with-expression': 'error',
    'security/detect-new-buffer': 'error',
    'security/detect-non-literal-regexp': 'warn',
    'security/detect-unsafe-regex': 'warn'
  }
}

