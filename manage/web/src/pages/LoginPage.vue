<template>
  <div class="login-page">
    <div class="login-panel">
      <div class="login-brand">
        <p class="login-kicker">管理入口</p>
        <h1>塔防管理后台</h1>
      </div>

      <el-form :model="loginForm" label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="账号">
          <el-input v-model="loginForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="loginForm.password"
            placeholder="请输入密码"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-button class="login-button" type="primary" @click="handleLogin">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { login } from '../api'

const emit = defineEmits(['login-success'])

const loginForm = reactive({
  username: '',
  password: ''
})

async function handleLogin() {
  try {
    const result = await login(loginForm)
    ElMessage.success('登录成功')
    emit('login-success', result)
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(59, 130, 246, 0.18), transparent 28%),
    radial-gradient(circle at bottom right, rgba(16, 185, 129, 0.16), transparent 24%),
    #f3f6fb;
}

.login-panel {
  width: 100%;
  max-width: 460px;
  padding: 36px 32px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(229, 231, 235, 0.9);
  border-radius: 20px;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(10px);
}

.login-brand {
  margin-bottom: 24px;
}

.login-kicker {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.18em;
  color: #2563eb;
}

.login-brand h1 {
  margin: 0 0 10px;
  font-size: 32px;
  line-height: 1.2;
}

.login-tip {
  margin: 0;
  color: #64748b;
  line-height: 1.6;
}

.login-button {
  width: 100%;
  margin-top: 8px;
}
</style>
