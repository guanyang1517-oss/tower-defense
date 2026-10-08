<template>
  <section class="page-card">
    <div class="page-header">
      <h2>怪物类型管理</h2>
      <el-button type="primary" @click="openEnemyDialog()">新增怪物</el-button>
    </div>
    <el-table :data="enemyList" border>
      <el-table-column prop="enemyCode" label="编码" min-width="100" />
      <el-table-column prop="enemyName" label="名称" min-width="120" />
      <el-table-column prop="health" label="血量" width="90" />
      <el-table-column prop="speed" label="速度" width="90" />
      <el-table-column prop="attackDamage" label="攻击" width="90" />
      <el-table-column prop="reward" label="奖励" width="90" />
      <el-table-column label="启用" width="80">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEnemyDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="removeEnemy(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>

  <el-dialog v-model="enemyDialogVisible" :title="enemyForm.id ? '编辑怪物' : '新增怪物'" width="560px">
    <el-form :model="enemyForm" label-width="120px">
      <el-form-item label="编码"><el-input v-model="enemyForm.enemyCode" /></el-form-item>
      <el-form-item label="名称"><el-input v-model="enemyForm.enemyName" /></el-form-item>
      <el-form-item label="贴图路径"><el-input v-model="enemyForm.texturePath" /></el-form-item>
      <el-form-item label="攻击贴图"><el-input v-model="enemyForm.attackTexturePath" /></el-form-item>
      <el-form-item label="血量"><el-input-number v-model="enemyForm.health" :min="1" /></el-form-item>
      <el-form-item label="速度"><el-input-number v-model="enemyForm.speed" :min="1" /></el-form-item>
      <el-form-item label="攻击"><el-input-number v-model="enemyForm.attackDamage" :min="0" /></el-form-item>
      <el-form-item label="奖励"><el-input-number v-model="enemyForm.reward" :min="0" /></el-form-item>
      <el-form-item label="启用"><el-switch v-model="enemyForm.enabled" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="enemyDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitEnemy">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createEnemyType, deleteEnemyType, getEnemyTypes, updateEnemyType } from '../api'

const enemyList = ref([])
const enemyDialogVisible = ref(false)
const enemyForm = reactive(createEnemyForm())

onMounted(loadEnemyList)

async function loadEnemyList() {
  enemyList.value = await getEnemyTypes()
}

function openEnemyDialog(row) {
  Object.assign(enemyForm, createEnemyForm(), row || {})
  enemyDialogVisible.value = true
}

async function submitEnemy() {
  if (enemyForm.id) {
    await updateEnemyType(enemyForm.id, enemyForm)
  } else {
    await createEnemyType(enemyForm)
  }
  enemyDialogVisible.value = false
  await loadEnemyList()
  ElMessage.success('保存成功')
}

async function removeEnemy(row) {
  await ElMessageBox.confirm(`确认删除怪物 ${row.enemyName} 吗？`, '提示', { type: 'warning' })
  await deleteEnemyType(row.id)
  await loadEnemyList()
  ElMessage.success('删除成功')
}

function createEnemyForm() {
  return {
    id: null,
    enemyCode: '',
    enemyName: '',
    texturePath: '',
    attackTexturePath: '',
    health: 10,
    speed: 150,
    attackDamage: 50,
    reward: 50,
    enabled: true
  }
}
</script>
