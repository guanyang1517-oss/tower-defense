# td-manage-after

塔防游戏的管理后台后端：基于 Spring Boot 2.7 + Spring Data JPA + MySQL 8，**需要 JDK 17**。

## 目录

- `src/main/java`：后端代码
- `src/main/resources/application.yml`：运行配置
- `src/main/resources/schema_mysql.sql`：MySQL 初始化脚本

## 功能

- 全局数值配置
- 怪物类型管理
- 关卡管理
- 波次管理
- 波次刷怪明细管理
- 账号与页面权限管理（仅 `ADMIN` 角色可调用账号接口）
- 游戏运行时配置接口（免登录只读，供游戏客户端拉取）

## 启动前准备

1. 创建 MySQL 数据库并执行 `src/main/resources/schema_mysql.sql`
2. 通过环境变量提供数据库凭据（`application.yml` 中不再保存任何密码）：

   | 变量 | 默认值 | 说明 |
   | --- | --- | --- |
   | `DB_URL` | `jdbc:mysql://127.0.0.1:3306/td_manage?...` | JDBC 连接串 |
   | `DB_USERNAME` | `root` | MySQL 账号 |
   | `DB_PASSWORD` | *（空）* | MySQL 密码，必须显式提供 |

   PowerShell 示例：

   ```powershell
   $env:DB_PASSWORD = "你的密码"
   mvn spring-boot:run
   ```

   （本模块只有 `pom.xml`，没有 Maven Wrapper，请确保本机已安装 Maven 3.6+。）

3. 使用 JDK 17 运行（`pom.xml` 声明 `<java.version>17</java.version>`）

初始化脚本会植入一个默认管理员账号 `root` / `root`（库中存的是无盐 SHA-256 哈希），
**首次登录后请立即修改密码**。

## 主要接口

面向管理后台（需要 `Authorization` 请求头携带登录返回的 token）：

- `POST /api/auth/login`、`GET /api/auth/me`、`POST /api/auth/logout`
- `GET|PUT /api/game-config`
- `GET|POST /api/enemy-types`、`GET|PUT|DELETE /api/enemy-types/{id}`
- `GET|POST /api/levels`、`GET|PUT|DELETE /api/levels/{id}`
- `GET|POST /api/levels/{levelId}/waves`、`PUT|DELETE /api/waves/{id}`
- `GET|POST /api/waves/{waveId}/spawns`、`PUT|DELETE /api/spawns/{id}`
- `GET|POST /api/accounts`、`PUT|DELETE /api/accounts/{id}`（仅 `ADMIN`）

面向游戏客户端（免登录只读，仅返回已启用数据）：

- `GET /api/runtime/game-config`
- `GET /api/runtime/enemy-types`
- `GET /api/runtime/levels`
- `GET /api/runtime/levels/{levelCode}/waves`
