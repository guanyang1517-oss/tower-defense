import axios from 'axios'
import { clearStoredAuth, getStoredToken } from './utils/auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json; charset=UTF-8'
  }
})

request.interceptors.request.use((config) => {
  const token = getStoredToken()
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

request.interceptors.response.use((response) => {
  const body = response.data
  if (!body.success) {
    return Promise.reject(new Error(body.message || '请求失败'))
  }
  return body.data
}, (error) => {
  const message = error.response && error.response.data && error.response.data.message
  if (message === '未登录或登录已失效') {
    clearStoredAuth()
  }
  return Promise.reject(error)
})

export const login = (data) => request.post('/auth/login', data)
export const getCurrentUser = () => request.get('/auth/me')
export const logout = () => request.post('/auth/logout')

export const getGameConfig = () => request.get('/game-config')
export const updateGameConfig = (data) => request.put('/game-config', data)

export const getEnemyTypes = () => request.get('/enemy-types')
export const createEnemyType = (data) => request.post('/enemy-types', data)
export const updateEnemyType = (id, data) => request.put(`/enemy-types/${id}`, data)
export const deleteEnemyType = (id) => request.delete(`/enemy-types/${id}`)

export const getLevels = () => request.get('/levels')
export const createLevel = (data) => request.post('/levels', data)
export const updateLevel = (id, data) => request.put(`/levels/${id}`, data)
export const deleteLevel = (id) => request.delete(`/levels/${id}`)

export const getWaves = (levelId) => request.get(`/levels/${levelId}/waves`)
export const createWave = (levelId, data) => request.post(`/levels/${levelId}/waves`, data)
export const updateWave = (id, data) => request.put(`/waves/${id}`, data)
export const deleteWave = (id) => request.delete(`/waves/${id}`)

export const getSpawns = (waveId) => request.get(`/waves/${waveId}/spawns`)
export const createSpawn = (waveId, data) => request.post(`/waves/${waveId}/spawns`, data)
export const updateSpawn = (id, data) => request.put(`/spawns/${id}`, data)
export const deleteSpawn = (id) => request.delete(`/spawns/${id}`)

export const getAccounts = () => request.get('/accounts')
export const createAccount = (data) => request.post('/accounts', data)
export const updateAccount = (id, data) => request.put(`/accounts/${id}`, data)
export const deleteAccount = (id) => request.delete(`/accounts/${id}`)
