<template>
  <section class="page-card">
    <div class="page-header">
      <h2>账号管理</h2>
      <el-button type="primary" @click="openCreateDialog">新增账号</el-button>
    </div>
    <el-table :data="accountList" border>
      <el-table-column prop="username" label="账号" min-width="160" />
      <el-table-column prop="role" label="角色" width="120">
        <template #default="{ row }">
          {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
        </template>
      </el-table-column>
      <el-table-column prop="enabled" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="页面权限" min-width="220">
        <template #default="{ row }">
          {{ getPermissionText(row) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button link type="primary" @click="openUpdateDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="confirmDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>

  <el-dialog v-model="createDialogVisible" title="新增账号" width="520px">
    <el-form :model="createForm" label-width="110px">
      <el-form-item label="账号">
        <el-input v-model="createForm.username" />
      </el-form-item>
      <el-form-item label="密码">
        <el-input v-model="createForm.password" show-password />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="createForm.role" style="width: 100%">
          <el-option label="管理员" value="ADMIN" />
          <el-option label="普通用户" value="USER" />
        </el-select>
      </el-form-item>
      <el-form-item label="启用">
        <el-switch v-model="createForm.enabled" />
      </el-form-item>
      <el-form-item v-if="createForm.role === 'USER'" label="页面权限">
        <el-checkbox-group v-model="createForm.pagePermissions">
          <el-checkbox
            v-for="item in userPages"
            :key="item.value"
            :label="item.value"
          >
            {{ item.label }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="createDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitCreate">保存</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="updateDialogVisible" title="编辑账号" width="520px">
    <el-form :model="updateForm" label-width="110px">
      <el-form-item label="账号">
        <el-input :model-value="updateForm.username" disabled />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="updateForm.password" show-password placeholder="留空表示不修改密码" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="updateForm.role" style="width: 100%">
          <el-option label="管理员" value="ADMIN" />
          <el-option label="普通用户" value="USER" />
        </el-select>
      </el-form-item>
      <el-form-item label="启用">
        <el-switch v-model="updateForm.enabled" />
      </el-form-item>
      <el-form-item v-if="updateForm.role === 'USER'" label="页面权限">
        <el-checkbox-group v-model="updateForm.pagePermissions">
          <el-checkbox
            v-for="item in userPages"
            :key="item.value"
            :label="item.value"
          >
            {{ item.label }}
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="updateDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitUpdate">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createAccount, deleteAccount, getAccounts, updateAccount } from '../api'

const accountList = ref([])
const createDialogVisible = ref(false)
const updateDialogVisible = ref(false)
const currentAccountId = ref(null)
const userPages = [
  { label: '全局数值', value: 'config' },
  { label: '怪物类型', value: 'enemy' },
  { label: '关卡管理', value: 'level' },
  { label: '波次管理', value: 'wave' }
]

const createForm = reactive(createCreateForm())
const updateForm = reactive(createUpdateForm())

onMounted(loadAccounts)

async function loadAccounts() {
  accountList.value = await getAccounts()
}

function openCreateDialog() {
  Object.assign(createForm, createCreateForm())
  createDialogVisible.value = true
}

function openUpdateDialog(row) {
  currentAccountId.value = row.id
  Object.assign(updateForm, createUpdateForm(), {
    username: row.username,
    role: row.role,
    enabled: row.enabled,
    pagePermissions: row.pagePermissions || []
  })
  updateDialogVisible.value = true
}

async function submitCreate() {
  await createAccount(createForm)
  createDialogVisible.value = false
  await loadAccounts()
  ElMessage.success('保存成功')
}

async function submitUpdate() {
  await updateAccount(currentAccountId.value, updateForm)
  updateDialogVisible.value = false
  await loadAccounts()
  ElMessage.success('保存成功')
}

async function confirmDelete(row) {
  await ElMessageBox.confirm(`确认删除账号「${row.username}」？`, '删除账号', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
  await deleteAccount(row.id)
  await loadAccounts()
  ElMessage.success('删除成功')
}

function createCreateForm() {
  return {
    username: '',
    password: '',
    role: 'USER',
    enabled: true,
    pagePermissions: []
  }
}

function createUpdateForm() {
  return {
    username: '',
    password: '',
    role: 'USER',
    enabled: true,
    pagePermissions: []
  }
}

function getPermissionText(row) {
  if (row.role === 'ADMIN') {
    return '账号管理、账号权限'
  }
  const permissions = row.pagePermissions || []
  const labels = userPages.filter((item) => permissions.includes(item.value)).map((item) => item.label)
  return labels.length ? labels.join('、') : '无'
}
</script>
