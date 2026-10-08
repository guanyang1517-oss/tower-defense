<template>
  <el-container class="app-shell">
    <el-aside width="220px" class="app-aside">
      <div class="brand">塔防管理后台</div>
      <el-menu :default-active="activeMenu" @select="handleMenuChange">
        <el-menu-item
          v-for="item in visibleMenus"
          :key="item.value"
          :index="item.value"
        >
          {{ item.label }}
        </el-menu-item>
      </el-menu>
      <div class="aside-footer">
        <div class="user-role">{{ role === 'ADMIN' ? '管理员' : '普通用户' }}</div>
        <el-button plain @click="$emit('logout')">退出登录</el-button>
      </div>
    </el-aside>

    <el-main class="app-main">
      <slot />
    </el-main>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  activeMenu: {
    type: String,
    default: 'config'
  },
  role: {
    type: String,
    default: 'USER'
  },
  pagePermissions: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:activeMenu', 'logout'])
const userMenus = [
  { label: '全局数值', value: 'config' },
  { label: '怪物类型', value: 'enemy' },
  { label: '关卡管理', value: 'level' },
  { label: '波次管理', value: 'wave' }
]
const adminMenus = [
  { label: '账号管理', value: 'account' },
  { label: '账号权限', value: 'permission' }
]

const visibleMenus = computed(() => {
  if (props.role === 'ADMIN') {
    return adminMenus
  }
  const permissions = props.pagePermissions || []
  return userMenus.filter((item) => permissions.includes(item.value))
})

function handleMenuChange(value) {
  if (value !== props.activeMenu) {
    emit('update:activeMenu', value)
  }
}
</script>

<style scoped>
.aside-footer {
  padding: 16px 20px;
  margin-top: auto;
  border-top: 1px solid #e5e7eb;
}

.user-role {
  margin-bottom: 12px;
  color: #64748b;
  font-size: 13px;
}

.app-aside {
  display: flex;
  flex-direction: column;
}

.aside-footer :deep(.el-button) {
  width: 100%;
}
</style>
