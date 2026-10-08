const TOKEN_KEY = 'td-manage-token'
const USER_KEY = 'td-manage-user'

export function getStoredToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function getStoredUser() {
  const value = localStorage.getItem(USER_KEY)
  if (!value) {
    return null
  }
  try {
    return JSON.parse(value)
  } catch (error) {
    clearStoredAuth()
    return null
  }
}

export function isLoggedIn() {
  return !!getStoredToken()
}

export function setStoredAuth(token, user) {
  localStorage.setItem(TOKEN_KEY, token)
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearStoredAuth() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}
