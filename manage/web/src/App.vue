<template>
  <LoginPage v-if="!loggedIn" @login-success="handleLoginSuccess" />
  <MainLayout
    v-else
    v-model:active-menu="activeMenu"
    :role="currentUser && currentUser.role"
    :page-permissions="currentUser && currentUser.pagePermissions"
    @logout="handleLogout"
  >
    <component v-if="currentPageComponent" :is="currentPageComponent" />
    <section v-else class="page-card">
      <div class="page-header">
        <h2>暂无可访问页面</h2>
      </div>
    </section>
  </MainLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import MainLayout from './components/MainLayout.vue'
import AccountPage from './pages/AccountPage.vue'
import AccountPermissionPage from './pages/AccountPermissionPage.vue'
import LoginPage from './pages/LoginPage.vue'
import EnemyPage from './pages/EnemyPage.vue'
import GameConfigPage from './pages/GameConfigPage.vue'
import LevelPage from './pages/LevelPage.vue'
import WavePage from './pages/WavePage.vue'
import { getCurrentUser, logout } from './api'
import { clearStoredAuth, getStoredToken, getStoredUser, isLoggedIn, setStoredAuth } from './utils/auth'

const adminMenus = ['account', 'permission']
const loggedIn = ref(isLoggedIn())
const currentUser = ref(getStoredUser())
const activeMenu = ref(getDefaultMenu(currentUser.value))

const pageMap = {
  config: GameConfigPage,
  enemy: EnemyPage,
  level: LevelPage,
  wave: WavePage,
  account: AccountPage,
  permission: AccountPermissionPage
}

onMounted(refreshCurrentUser)

const currentPageComponent = computed(() => {
  const allowedMenus = getAllowedMenus(currentUser.value)
  if (!allowedMenus.length) {
    return null
  }
  const menu = allowedMenus.includes(activeMenu.value) ? activeMenu.value : allowedMenus[0]
  return pageMap[menu]
})

async function handleLoginSuccess(payload) {
  setStoredAuth(payload.token, {
    id: payload.id,
    username: payload.username,
    role: payload.role,
    pagePermissions: payload.pagePermissions || []
  })
  currentUser.value = await getCurrentUser()
  setStoredAuth(payload.token, currentUser.value)
  loggedIn.value = true
  activeMenu.value = getDefaultMenu(currentUser.value)
}

async function handleLogout() {
  try {
    await logout()
  } catch (error) {
    ElMessage.error(error.message || '退出登录失败')
  }
  clearStoredAuth()
  currentUser.value = null
  loggedIn.value = false
  activeMenu.value = 'config'
}

async function refreshCurrentUser() {
  if (!loggedIn.value) {
    return
  }
  try {
    currentUser.value = await getCurrentUser()
    setStoredAuth(getStoredToken(), currentUser.value)
    activeMenu.value = getDefaultMenu(currentUser.value)
  } catch (error) {
    clearStoredAuth()
    currentUser.value = null
    loggedIn.value = false
  }
}

function getDefaultMenu(user) {
  const allowedMenus = getAllowedMenus(user)
  return allowedMenus.length ? allowedMenus[0] : 'config'
}

function getAllowedMenus(user) {
  if (user && user.role === 'ADMIN') {
    return adminMenus
  }
  return (user && user.pagePermissions) || []
}
</script>
