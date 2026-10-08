<template>
  <section class="page-card">
    <div class="page-header wave-header">
      <h2>波次管理</h2>
      <div class="wave-tools">
        <el-select v-model="selectedLevelId" placeholder="请选择关卡" style="width: 240px" @change="loadWaves">
          <el-option
            v-for="item in levelList"
            :key="item.id"
            :label="`${item.levelName} (${item.levelCode})`"
            :value="item.id"
          />
        </el-select>
        <el-button type="primary" :disabled="!selectedLevelId" @click="openWaveDialog()">新增波次</el-button>
      </div>
    </div>

    <el-empty v-if="!selectedLevelId" description="暂无关卡，请先到关卡管理中创建" />
    <el-table v-else :data="waveList" border>
      <el-table-column prop="waveNumber" label="波次" width="100" />
      <el-table-column prop="breakTime" label="间隔时间" width="120" />
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button link type="primary" @click="openWaveDialog(row)">编辑</el-button>
          <el-button link type="primary" @click="openSpawnPanel(row)">刷怪明细</el-button>
          <el-button link type="danger" @click="removeWave(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>

  <el-dialog v-model="waveDialogVisible" :title="waveForm.id ? '编辑波次' : '新增波次'" width="480px">
    <el-form :model="waveForm" label-width="120px">
      <el-form-item label="波次编号"><el-input-number v-model="waveForm.waveNumber" :min="1" /></el-form-item>
      <el-form-item label="波间间隔"><el-input-number v-model="waveForm.breakTime" :min="0" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="waveDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitWave">保存</el-button>
    </template>
  </el-dialog>

  <el-drawer v-model="spawnDrawerVisible" title="刷怪明细" size="720px">
    <div class="page-header">
      <div>
        <strong v-if="currentWave">第 {{ currentWave.waveNumber }} 波</strong>
      </div>
      <el-button type="primary" @click="openSpawnDialog()">新增刷怪</el-button>
    </div>
    <el-table :data="spawnList" border>
      <el-table-column prop="spawnOrder" label="顺序" width="80" />
      <el-table-column prop="enemyTypeId" label="怪物">
        <template #default="{ row }">
          {{ enemyNameMap[row.enemyTypeId] || row.enemyTypeId }}
        </template>
      </el-table-column>
      <el-table-column prop="spawnCount" label="数量" width="80" />
      <el-table-column prop="spawnInterval" label="间隔" width="90" />
      <el-table-column prop="pathIndex" label="路径" width="80" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openSpawnDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="removeSpawn(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-drawer>

  <el-dialog v-model="spawnDialogVisible" :title="spawnForm.id ? '编辑刷怪' : '新增刷怪'" width="520px">
    <el-form :model="spawnForm" label-width="120px">
      <el-form-item label="怪物类型">
        <el-select v-model="spawnForm.enemyTypeId" style="width: 100%">
          <el-option
            v-for="item in enemyList"
            :key="item.id"
            :label="`${item.enemyName} (${item.enemyCode})`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="数量"><el-input-number v-model="spawnForm.spawnCount" :min="1" /></el-form-item>
      <el-form-item label="间隔"><el-input-number v-model="spawnForm.spawnInterval" :min="0.1" :step="0.1" /></el-form-item>
      <el-form-item label="路径索引"><el-input-number v-model="spawnForm.pathIndex" :min="0" /></el-form-item>
      <el-form-item label="生成顺序"><el-input-number v-model="spawnForm.spawnOrder" :min="0" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="spawnDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitSpawn">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createSpawn,
  createWave,
  deleteSpawn,
  deleteWave,
  getEnemyTypes,
  getLevels,
  getSpawns,
  getWaves,
  updateSpawn,
  updateWave
} from '../api'

const enemyList = ref([])
const levelList = ref([])
const waveList = ref([])
const spawnList = ref([])

const selectedLevelId = ref(null)
const currentWave = ref(null)

const waveDialogVisible = ref(false)
const spawnDrawerVisible = ref(false)
const spawnDialogVisible = ref(false)

const waveForm = reactive(createWaveForm())
const spawnForm = reactive(createSpawnForm())

const enemyNameMap = computed(() => {
  const map = {}
  enemyList.value.forEach((item) => {
    map[item.id] = item.enemyName
  })
  return map
})

onMounted(async () => {
  await Promise.all([loadEnemyList(), loadLevelList()])
})

async function loadEnemyList() {
  enemyList.value = await getEnemyTypes()
}

async function loadLevelList() {
  levelList.value = await getLevels()
  if (!selectedLevelId.value && levelList.value.length > 0) {
    selectedLevelId.value = levelList.value[0].id
    await loadWaves()
  }
  if (selectedLevelId.value && !levelList.value.some((item) => item.id === selectedLevelId.value)) {
    selectedLevelId.value = levelList.value[0] ? levelList.value[0].id : null
    await loadWaves()
  }
}

async function loadWaves() {
  if (!selectedLevelId.value) {
    waveList.value = []
    return
  }
  waveList.value = await getWaves(selectedLevelId.value)
}

function openWaveDialog(row) {
  Object.assign(waveForm, createWaveForm(), row || {})
  waveDialogVisible.value = true
}

async function submitWave() {
  if (!selectedLevelId.value) {
    ElMessage.warning('请先选择关卡')
    return
  }
  if (waveForm.id) {
    await updateWave(waveForm.id, waveForm)
  } else {
    await createWave(selectedLevelId.value, waveForm)
  }
  waveDialogVisible.value = false
  await loadWaves()
  ElMessage.success('保存成功')
}

async function removeWave(row) {
  await ElMessageBox.confirm(`确认删除第 ${row.waveNumber} 波吗？`, '提示', { type: 'warning' })
  await deleteWave(row.id)
  if (currentWave.value && currentWave.value.id === row.id) {
    currentWave.value = null
    spawnList.value = []
    spawnDrawerVisible.value = false
  }
  await loadWaves()
  ElMessage.success('删除成功')
}

async function openSpawnPanel(row) {
  currentWave.value = row
  spawnDrawerVisible.value = true
  spawnList.value = await getSpawns(row.id)
}

function openSpawnDialog(row) {
  Object.assign(spawnForm, createSpawnForm(), row || {})
  spawnDialogVisible.value = true
}

async function submitSpawn() {
  if (!currentWave.value) {
    ElMessage.warning('请先选择波次')
    return
  }
  if (spawnForm.id) {
    await updateSpawn(spawnForm.id, spawnForm)
  } else {
    await createSpawn(currentWave.value.id, spawnForm)
  }
  spawnDialogVisible.value = false
  spawnList.value = await getSpawns(currentWave.value.id)
  ElMessage.success('保存成功')
}

async function removeSpawn(row) {
  await ElMessageBox.confirm('确认删除这条刷怪配置吗？', '提示', { type: 'warning' })
  await deleteSpawn(row.id)
  if (currentWave.value) {
    spawnList.value = await getSpawns(currentWave.value.id)
  }
  ElMessage.success('删除成功')
}

function createWaveForm() {
  return {
    id: null,
    waveNumber: 1,
    breakTime: 5
  }
}

function createSpawnForm() {
  return {
    id: null,
    enemyTypeId: null,
    spawnCount: 5,
    spawnInterval: 1.5,
    pathIndex: 0,
    spawnOrder: 0
  }
}
</script>
