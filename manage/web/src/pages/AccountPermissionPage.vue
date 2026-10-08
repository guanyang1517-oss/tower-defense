<template>
  <section class="page-card">
    <div class="page-header">
      <h2>账号权限</h2>
    </div>

    <el-table :data="userAccounts" border>
      <el-table-column prop="username" label="账号" min-width="160" />
      <el-table-column label="可见页面" min-width="360">
        <template #default="{ row }">
          <el-checkbox-group v-model="row.pagePermissions">
            <el-checkbox
              v-for="item in userPages"
              :key="item.value"
              :label="item.value"
            >
              {{ item.label }}
            </el-checkbox>
          </el-checkbox-group>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="savePermissions(row)">保存</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAccounts, updateAccount } from '../api'

const userPages = [
  { label: '全局数值', value: 'config' },
  { label: '怪物类型', value: 'enemy' },
  { label: '关卡管理', value: 'level' },
  { label: '波次管理', value: 'wave' }
]

const accountList = ref([])
const userAccounts = computed(() => accountList.value.filter((item) => item.role === 'USER'))

onMounted(loadAccounts)

async function loadAccounts() {
  accountList.value = (await getAccounts()).map((item) => ({
    ...item,
    pagePermissions: item.pagePermissions || []
  }))
}

async function savePermissions(row) {
  await updateAccount(row.id, {
    password: '',
    role: row.role,
    enabled: row.enabled,
    pagePermissions: row.pagePermissions
  })
  ElMessage.success('保存成功')
}
</script>
