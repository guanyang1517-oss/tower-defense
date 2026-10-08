# td-manage-web

最小可用的塔防管理后台前端，基于 Vue 3 + Element Plus + Vite。

## 功能

- 全局数值编辑
- 怪物类型管理
- 关卡管理
- 波次管理
- 波次刷怪明细管理

## 开发启动

1. 先启动 `manage/after` 后端，默认端口 `8080`
2. 在当前目录安装依赖
3. 运行开发服务器

```bash
npm install
npm run dev
```

默认开发地址：

- `http://127.0.0.1:5173`

前端已通过 Vite 代理转发 `/api` 到 `http://127.0.0.1:8080`。
