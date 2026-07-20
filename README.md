# 互联网+智慧护理培训系统

> 基于 Spring Boot + Vue3 的企业级智慧护理培训平台，涵盖 PC 管理端、移动端 H5 和后端 API 服务，为老年护理专业护士提供完整的培训、学习、考试闭环。

---

## 项目简介

**互联网+智慧护理培训系统**（Smart Nursing Training System，简称 SNTS）旨在通过数字化手段提升老年护理专业护士的培训效率。系统提供文章、视频、PPT 等多媒体培训内容管理，在线考试与自动评分，学习进度跟踪，以及基于 AI 的智能护理助手，帮助护士规范、专业地为老年患者提供护理服务。

### 核心价值

- **内容管理闭环**：文章、视频、PPT 多形式培训内容，从创建、发布到学习追踪一站式管理
- **在线考试评估**：客观题自动判分 + 主观题 AI 智能评分，支持成绩统计与错题回顾
- **学习数据可视化**：Echarts 数据大屏，实时掌握学员学习进度与内容分布
- **AI 护理助手**：基于阿里云百炼大模型，提供 7×24 小时老年护理知识问答

---

## 技术栈

### 后端（smart-nursing-backend）

| 类别 | 技术 | 版本 |
|------|------|------|
| 开发语言 | Java | 17 |
| 核心框架 | Spring Boot | 3.5.13 |
| ORM 框架 | MyBatis-Plus | 3.5.12 |
| 数据库 | MySQL | 8.0 |
| 缓存中间件 | Redis | 5.x+ |
| AI 集成 | Spring AI (OpenAI 兼容) | 1.0.0 |
| API 文档 | SpringDoc OpenAPI | 2.8.x |
| 构建工具 | Maven | 3.9+ |

### PC 管理端（smart-nursing-admin）

| 类别 | 技术 | 版本 |
|------|------|------|
| 核心框架 | Vue | ^3.5.13 |
| UI 组件库 | Element Plus | ^2.9.1 |
| 构建工具 | Vite | ^7.0.0 |
| 路由 | Vue Router | ^4.6.3 |
| 状态管理 | Pinia | ^2.2.6 |
| HTTP 库 | Axios | ^1.18.1 |
| 图表库 | Echarts | ^6.0.0 |

### 移动端（smart-nursing-mobile）

| 类别 | 技术 | 版本 |
|------|------|------|
| 核心框架 | uni-app | ^3.0 |
| UI 组件 | uni-ui | ^2.0 |
| 编译目标 | H5 响应式 | - |

---

## 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                     PC 管理端 (Vue3)                         │
│   管理后台 · 培训师/管理员使用                                  │
│   内容管理 · 考试管理 · 成绩管理 · 统计分析 · 系统管理          │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP / Token 认证
┌────────────────────────┴────────────────────────────────────┐
│                   后端 API 服务 (Spring Boot)                │
│   统一 API 服务，同时为 PC 端和移动端提供接口                   │
│   · 用户认证 · 权限控制 (RBAC)                                │
│   · 内容 CRUD · 考试评分 · 学习记录                            │
│   · AI 护理助手 (Spring AI + 阿里云百炼)                       │
│   · 操作日志 · 文件上传                                       │
└────────────────────────┬────────────────────────────────────┘
                         │
          ┌──────────────┼──────────────┐
          │              │              │
   ┌──────┴──────┐ ┌─────┴─────┐ ┌──────┴──────┐
   │   MySQL     │ │   Redis   │ │  阿里云百炼  │
   │  数据存储    │ │ Token/缓存 │ │  AI 大模型  │
   └─────────────┘ └───────────┘ └─────────────┘
                         ▲
┌────────────────────────┴────────────────────────────────────┐
│                    移动端 H5 (uni-app)                       │
│   护士学员使用 · 手机浏览器 / 微信内置浏览器直接访问             │
│   学习中心 · 考试中心 · 个人中心 · 学习跟踪                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 功能模块

系统共包含 18 个功能模块，覆盖培训业务全流程：

| 序号 | 功能模块 | 所属端 | 说明 |
|------|---------|--------|------|
| 1 | 用户登录认证 | PC + 移动端 | 账号密码登录、Token 认证、退出登录 |
| 2 | 用户管理 | PC 管理端 | 学员/培训师/管理员增删改查、分页、状态控制 |
| 3 | 角色权限管理 | PC 管理端 | 角色 CRUD、角色-菜单关联、数据权限分配 |
| 4 | 菜单管理 | PC 管理端 | 菜单树 CRUD、图标管理、排序、按角色控制可见性 |
| 5 | 培训类别管理 | PC 管理端 | 树形分类（老年综合征、老年疾病、心理护理等） |
| 6 | 培训标签管理 | PC 管理端 | 标签 CRUD、与文章/视频/PPT 多对多关联 |
| 7 | 文章管理 | PC 管理端 | 富文本编辑、封面图、摘要、发布/下架/草稿 |
| 8 | 视频管理 | PC 管理端 | 视频上传、封面图、时长、发布/下架 |
| 9 | PPT 管理 | PC 管理端 | PPT 文件管理、在线预览、发布/下架 |
| 10 | 考试管理 | PC 管理端 | 试题 CRUD（单选/多选/判断/简答）、组卷、发布 |
| 11 | 成绩管理 | PC 管理端 | 成绩查询、统计、导出、及格率分析 |
| 12 | 学习记录管理 | PC + 移动端 | 学习进度记录、时长统计、完成状态跟踪 |
| 13 | 学习进度跟踪 | PC + 移动端 | 个人仪表盘、进度条、完成率、学习趋势图 |
| 14 | 首页数据统计 | PC 管理端 | 内容数量、类别分布图、学习趋势 Echarts |
| 15 | 系统日志管理 | PC 管理端 | 操作日志、登录日志、异常日志查询 |
| 16 | 移动端学习中心 | 移动端 H5 | 文章阅读、视频播放、PPT 查看、分类浏览 |
| 17 | 移动端考试中心 | 移动端 H5 | 在线答题、交卷、查看成绩、错题回顾 |
| 18 | AI 护理助手 | PC + 移动端 | 基于大模型的流式问答，专注老年护理知识 |

---

## 项目结构

```
smart-nursing/
├── smart-nursing-admin/          # PC 管理端 (Vue3 + Element Plus)
│   ├── src/
│   │   ├── api/                  # API 接口封装
│   │   ├── components/           # 公共组件 (文件上传、菜单树等)
│   │   ├── router/               # 路由配置
│   │   ├── stores/               # Pinia 状态管理
│   │   ├── utils/                # 工具函数 (request 封装等)
│   │   └── views/                # 页面视图
│   │       ├── ai/               # AI 助手
│   │       ├── content/          # 内容管理
│   │       ├── dashboard/        # 首页统计
│   │       ├── exam/             # 考试管理
│   │       ├── home/             # 主框架
│   │       └── system/           # 系统管理
│   └── vite.config.js
│
├── smart-nursing-backend/        # 后端 API 服务 (Spring Boot)
│   ├── src/main/java/com/smart/nursing/
│   │   ├── aop/                  # 切面 (操作日志、登录校验)
│   │   ├── common/
│   │   │   ├── config/           # 配置类 (Jackson、Redis、Web 等)
│   │   │   ├── enums/           # 枚举
│   │   │   └── exception/        # 全局异常处理
│   │   ├── controller/
│   │   │   ├── admin/            # PC 管理端接口
│   │   │   ├── common/           # 公共接口 (登录等)
│   │   │   └── mobile/           # 移动端接口
│   │   ├── dao/                  # MyBatis Mapper
│   │   ├── dto/                  # 数据传输对象
│   │   ├── entity/               # 实体类
│   │   ├── service/              # 业务逻辑层
│   │   └── vo/                   # 视图对象
│   └── src/main/resources/
│       ├── application.yml        # 应用配置
│       └── mapper/               # MyBatis XML
│
├── smart-nursing-mobile/         # 移动端 H5 (uni-app)
│   ├── src/
│   ├── App.vue
│   ├── pages.json                # 页面路由配置
│   └── manifest.json
│
├── sql/                          # 数据库脚本
│   ├── init.sql                  # 初始化建表脚本
│   └── migration_question_essay.sql  # 简答题迁移脚本
│
├── plan.md                       # 项目实施方案
├── requirement.md                # 工程标准文档
└── README.md                     # 项目说明文档
```

---

## 快速开始

### 环境要求

- **JDK**: 17+
- **Node.js**: 18+
- **MySQL**: 8.0
- **Redis**: 5.x+
- **Maven**: 3.9+
- **HBuilderX**: 4.76+（仅移动端开发需要）

### 1. 数据库初始化

```bash
# 登录 MySQL
mysql -uroot -p

# 创建数据库
CREATE DATABASE smart_nursing DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

# 导入初始化脚本
USE smart_nursing;
SOURCE sql/init.sql;
```

### 2. 启动后端服务

```bash
cd smart-nursing-backend

# 配置 AI 相关环境变量（必需，不配置会导致 Spring AI Bean 创建失败）
# Windows PowerShell
$env:DASHSCOPE_API_KEY = "sk-your-dashscope-api-key"
$env:SPRING_AI_OPENAI_BASE_URL = "https://dashscope.aliyuncs.com/compatible-mode"

# Linux / macOS
export DASHSCOPE_API_KEY=sk-your-dashscope-api-key
export SPRING_AI_OPENAI_BASE_URL=https://dashscope.aliyuncs.com/compatible-mode

# 启动（默认端口 8888）
mvn spring-boot:run
```

> **重要**：
> - `base-url` **不要**带 `/v1` 后缀，Spring AI 会自动追加 `/v1/chat/completions`，否则会导致 404 错误。
> - IntelliJ IDEA 启动前，在 Run Configuration → Environment variables 中配置上述两个变量。
> - 如果遇到 `BindException: Address already in use`，先杀掉占用 8888 端口的旧进程。

后端启动后访问 API 文档：http://localhost:8888/swagger-ui.html

### 3. 启动 PC 管理端

```bash
cd smart-nursing-admin

# 安装依赖
npm install

# 启动开发服务器（默认端口 5173）
npm run dev
```

访问地址：http://localhost:5173

默认管理员账号：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 系统管理员 |

### 4. 启动移动端 H5

```bash
cd smart-nursing-mobile

# 安装依赖
npm install

# 编译 H5
# 方式一：使用 HBuilderX 打开项目，点击"运行" → "运行到浏览器" → "Chrome"
# 方式二：使用命令行
npm run dev:h5
```

启动后访问 `http://localhost:5174`。测试账号：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| nurse01 | 123456 | 护士学员 |
| nurse02 | 123456 | 护士学员 |

> 启动时控制台会出现 `Deprecation Warning [legacy-js-api]`，这是 Dart Sass 弃用警告，不影响运行。

---

## 关键特性

### AI 智能护理助手

集成 Spring AI 与阿里云百炼大模型，提供流式对话响应，专注老年护理领域知识问答。支持会话上下文管理，可同时为 PC 端和移动端提供智能问答服务。

### AI 智能评分

对考试中的简答题进行 AI 自动评分，基于标准答案与评分要点，结合语义理解给出合理分数与评分说明，大幅减轻教师阅卷负担。

### 操作日志审计

通过 AOP 切面自动记录所有关键操作，包括操作人、操作类型、请求方法、请求地址、IP、耗时等，支持按操作用户和操作类型筛选，便于安全审计与问题追溯。

### 数据权限控制

基于 RBAC（基于角色的访问控制）模型，实现用户-角色-菜单三级权限体系，支持按钮级别权限控制，确保数据访问安全性。

### Long 精度安全

针对 MyBatis-Plus 雪花算法生成的 19 位长整型 ID，通过 Jackson 全局配置将 Long 序列化为 String，避免前端 JavaScript 精度丢失导致的 ID 错误。

---

## 配置说明

### 后端配置 (`application.yml`)

```yaml
server:
  port: 8888

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_nursing?...
    username: root
    password: 123456
  data:
    redis:
      host: localhost
      port: 6379
  ai:
    openai:
      api-key: ${DASHSCOPE_API_KEY:dummy-key}
      base-url: ${SPRING_AI_OPENAI_BASE_URL:https://dashscope.aliyuncs.com/compatible-mode}
```

### 环境变量

| 变量名 | 必填 | 说明 |
|--------|------|------|
| `DASHSCOPE_API_KEY` | 否 | 阿里云百炼 API Key，不配置则 AI 功能不可用 |
| `SPRING_AI_OPENAI_BASE_URL` | 否 | OpenAI 兼容地址，默认为阿里云 DashScope |

> **注意**：`base-url` 不要带 `/v1` 后缀，Spring AI 会自动追加 `/v1/chat/completions`，否则会导致 404 错误。

---

## 部署

### 生产环境部署

1. **后端**：`mvn clean package -DskipTests` 生成 jar 包，通过 `java -jar` 运行
2. **PC 管理端**：`npm run build` 生成静态文件，部署到 Nginx
3. **移动端**：编译为 H5 静态文件，部署到 Nginx
4. **Nginx**：配置反向代理，将 `/api` 转发到后端服务

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # PC 管理端
    location / {
        root /path/to/smart-nursing-admin/dist;
        try_files $uri $uri/ /index.html;
    }

    # 移动端
    location /mobile {
        root /path/to/smart-nursing-mobile/dist;
        try_files $uri $uri/ /mobile/index.html;
    }

    # API 反向代理
    location /api/ {
        proxy_pass http://localhost:8888/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 开发约定

- **后端命名规范**：类名 PascalCase，方法名 camelCase，包名全小写
- **前端命名规范**：组件名 PascalCase，变量/方法 camelCase，CSS kebab-case
- **API 路径规范**：`/admin/*` 为 PC 管理端接口，`/mobile/*` 为移动端接口
- **统一返回格式**：`{ "code": 200, "msg": "success", "data": ... }`
- **认证方式**：请求头携带 `token` 字段，值为登录返回的 JWT Token

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0.0 | 2026-07-18 | 初始版本：完成全部 18 个功能模块，含 AI 助手与智能评分 |
| 1.0.1 | 2026-07-18 | 修复路由跳转、AI 功能 404、日志管理字段缺失、Long 精度丢失 |
| 1.0.2 | 2026-07-19 | 修复移动端考试流程灰屏、nurse02 权限拒绝、考试次数统计错误 |
| 1.0.3 | 2026-07-20 | 修复移动端 modal 弹窗系列问题（ghost modal、重复弹出、取消不掉） |

### 1.0.3 详细变更

**移动端 modal 弹窗系列修复**：本次集中修复了 `uni.showModal` 在 H5 模式下的多个衍生问题，涉及 4 个独立 bug：

1. **登录 toast 残留导致后续页面按钮无法点击**：登录成功后的 `uni.showToast` 遮罩未清除，残留在后续页面上拦截点击。修复为登录跳转前调用 `uni.hideToast()`。

2. **提交答卷无反应（hook 破坏 uni.showModal）**：之前在 `App.vue` 中 hook 了 `history.pushState`/`replaceState` 并在路由变化时调用 `cleanupResidual` 直接移除 `uni-modal` DOM 元素，破坏了 uni-app 内部状态管理，导致后续 `uni.showModal` 调用被静默忽略。修复为完全移除 hook 和 DOM 清理代码，改用 CSS `:has()` 选择器隐藏残留的空 modal。

3. **开始考试/提交答卷 modal 重复弹出无法取消**：`@tap` 事件可能重复触发导致 `uni.showModal` 被调用多次，第二个 modal 因内部状态冲突无法取消。修复为在 `handleStartExam` 和 `confirmSubmit` 中添加防重复点击锁（`isStarting`/`isConfirming`）。

4. **modal 导航后不消失（ghost modal）**：`App.vue` 中 CSS `uni-modal { display: flex !important }` 的 `!important` 强制所有 modal 可见，阻止了 uni-app 通过内联 `style="display:none"` 隐藏 modal。修复为去掉 `display` 的 `!important`，让 uni-app 能正常控制显隐。

### 1.0.2 详细变更

**移动端考试流程灰屏修复**：点击"开始考试"或"交卷"后页面灰屏。根因是 `uni.showModal` 关闭动画未完成就调用 `uni.showLoading`，H5 模式下两层遮罩叠加。修复为 modal 确认后加 300ms 延迟，并用 `finally` 确保 loading 清理。

**nurse02 权限修复**：nurse02 登录后显示"暂无学习内容"。根因是 nurse02 没有分配 NURSE 角色，移动端接口返回 403。修复为在 `sys_user_role` 表分配角色。

**考试次数统计修复**：nurse01 已有进行中记录导致无法重考。根因是 `startExam` 把进行中记录也计入次数。修复为只统计已交卷记录（status=2），创建新记录前先删除旧的进行中记录，用 `max(attempt_count)+1` 计算编号避免唯一键冲突。

### 1.0.1 详细变更

**路由跳转**：文章/视频/PPT/考试/学习记录的编辑页和详情页无法跳转。根因是 `router/index.js` 的 `generateRoutes` 只处理列表页，没注册编辑/详情子路由。

**AI 功能 404**：`SPRING_AI_OPENAI_BASE_URL` 配置了 `/v1` 后缀，Spring AI 又自动追加 `/v1/chat/completions`，导致路径变成 `/v1/v1/chat/completions`。修复为 base-url 不带 `/v1`。

**日志管理字段缺失**：`LogAspect` 没有持久化 `url` 和 `costTime` 到数据库。修复为数据库表添加列、实体添加字段、切面设置字段。

**Long 精度丢失**：MyBatis-Plus 雪花 ID 是 19 位长整型，超过 JavaScript `Number.MAX_SAFE_INTEGER`，前端精度丢失导致删除/更新失败。修复为新增 `JacksonConfig.java`，全局将 Long 序列化为 String。

---

## 许可证

本项目仅用于教学与学习目的。

---

## 联系方式

- **仓库地址**：https://github.com/zyf0408/smart-nursing
- **问题反馈**：[Issues](https://github.com/zyf0408/smart-nursing/issues)
