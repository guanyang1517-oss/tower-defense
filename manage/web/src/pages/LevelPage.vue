<template>
  <section class="page-card">
    <div class="page-header">
      <h2>关卡管理</h2>
      <el-button type="primary" @click="openLevelDialog()">新增关卡</el-button>
    </div>
    <el-table :data="levelList" border>
      <el-table-column prop="levelCode" label="编码" min-width="100" />
      <el-table-column prop="levelName" label="名称" min-width="120" />
      <el-table-column prop="mapPath" label="地图路径" min-width="180" />
      <el-table-column prop="maxWave" label="总波次" width="90" />
      <el-table-column label="启用" width="80">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="140" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openLevelDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="removeLevel(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>

  <el-dialog v-model="levelDialogVisible" :title="levelForm.id ? '编辑关卡' : '新增关卡'" width="560px">
    <el-form :model="levelForm" label-width="120px">
      <el-form-item label="编码"><el-input v-model="levelForm.levelCode" /></el-form-item>
      <el-form-item label="名称"><el-input v-model="levelForm.levelName" /></el-form-item>
      <el-form-item label="地图路径"><el-input v-model="levelForm.mapPath" /></el-form-item>
      <el-form-item label="总波次"><el-input-number v-model="levelForm.maxWave" :min="1" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="levelForm.remark" /></el-form-item>
      <el-form-item label="启用"><el-switch v-model="levelForm.enabled" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="levelDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitLevel">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createLevel, deleteLevel, getLevels, updateLevel } from '../api'

const levelList = ref([])
const levelDialogVisible = ref(false)
const levelForm = reactive(createLevelForm())

onMounted(loadLevelList)

async function loadLevelList() {
  levelList.value = await getLevels()
}

function openLevelDialog(row) {
  Object.assign(levelForm, createLevelForm(), row || {})
  levelDialogVisible.value = true
}

async function submitLevel() {
  if (levelForm.id) {
    await updateLevel(levelForm.id, levelForm)
  } else {
    await createLevel(levelForm)
  }
  levelDialogVisible.value = false
  await loadLevelList()
  ElMessage.success('保存成功')
}

async function removeLevel(row) {
  await ElMessageBox.confirm(`确认删除关卡 ${row.levelName} 吗？相关波次也会删除。`, '提示', { type: 'warning' })
  await deleteLevel(row.id)
  await loadLevelList()
  ElMessage.success('删除成功')
}

function createLevelForm() {
  return {
    id: null,
    levelCode: '',
    levelName: '',
    mapPath: '',
    maxWave: 10,
    enabled: true,
    remark: ''
  }
}
</script>
