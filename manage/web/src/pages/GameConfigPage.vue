<template>
  <section class="page-card">
    <div class="page-header">
      <h2>全局数值管理</h2>
      <el-button type="primary" @click="saveConfig">保存配置</el-button>
    </div>
    <el-form :model="gameConfig" label-width="140px" class="grid-form">
      <el-form-item label="初始金币">
        <el-input-number v-model="gameConfig.initialMoney" :min="0" />
      </el-form-item>
      <el-form-item label="初始生命值">
        <el-input-number v-model="gameConfig.initialHealth" :min="0" />
      </el-form-item>
      <el-form-item label="击杀奖励">
        <el-input-number v-model="gameConfig.killReward" :min="0" />
      </el-form-item>
      <el-form-item label="防御塔价格">
        <el-input-number v-model="gameConfig.towerCost" :min="0" />
      </el-form-item>
      <el-form-item label="防御塔射程">
        <el-input-number v-model="gameConfig.towerRange" :min="1" />
      </el-form-item>
      <el-form-item label="路障价格">
        <el-input-number v-model="gameConfig.blockerCost" :min="0" />
      </el-form-item>
      <el-form-item label="路障最大血量">
        <el-input-number v-model="gameConfig.blockerMaxHealth" :min="1" />
      </el-form-item>
      <el-form-item label="路障反伤">
        <el-input-number v-model="gameConfig.blockerReturnDamage" :min="0" />
      </el-form-item>
      <el-form-item label="敌人基础速度">
        <el-input-number v-model="gameConfig.enemyBaseSpeed" :min="1" />
      </el-form-item>
      <el-form-item label="敌人基础血量">
        <el-input-number v-model="gameConfig.enemyBaseHealth" :min="1" />
      </el-form-item>
      <el-form-item label="敌人基础攻击">
        <el-input-number v-model="gameConfig.enemyAttackDamage" :min="0" />
      </el-form-item>
    </el-form>
  </section>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getGameConfig, updateGameConfig } from '../api'

const gameConfig = reactive(createDefaultConfig())

onMounted(async () => {
  Object.assign(gameConfig, await getGameConfig())
})

async function saveConfig() {
  await updateGameConfig(gameConfig)
  ElMessage.success('保存成功')
}

function createDefaultConfig() {
  return {
    id: 1,
    initialMoney: 600,
    initialHealth: 10,
    killReward: 50,
    towerCost: 100,
    towerRange: 250,
    blockerCost: 150,
    blockerMaxHealth: 1500,
    blockerReturnDamage: 1,
    enemyBaseSpeed: 150,
    enemyBaseHealth: 10,
    enemyAttackDamage: 50
  }
}
</script>
