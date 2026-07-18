# 项目交接文档

> 最后更新：2026-07-17
> 适用对象：完全没有任何上下文的新会话

---

## 1 这个项目是什么

「互联网+智慧护理线上实训平台」，一个面向护理专业学生的在线学习+考试系统，包含三端：

| 子项目 | 路径 | 技术栈 | 端口 |
|--------|------|--------|------|
| 后端 | `d:\practice\smart-nursing-backend` | Java 17 + Spring Boot + MyBatis-Plus + MySQL | 8888 |
| 移动端 | `d:\practice\smart-nursing-mobile` | uni-app + Vue 3 | 5174 |
| PC管理后台 | `d:\practice\smart-nursing-admin` | Vue 3 + Vite + Element Plus + Pinia | 5173 |

数据库：MySQL，库名 `smart_nursing`，账号 `root`，密码 `123456`。初始化脚本在 `d:\practice\sql\init.sql`。

工程文档（需求规约、架构设计、测试计划等）在 `d:\practice\工程文档-指导版\` 目录下。

---

## 2 服务器启动方式

### 后端

```
# 在 smart-nursing-backend 目录下
# 用 IntelliJ IDEA 打开项目，运行 SmartNursingApplication 主类
# 或命令行：
mvn spring-boot:run
```

启动成功标志：控制台输出 `Started SmartNursingApplication in X.XXX seconds`，监听 8888 端口。

### PC管理后台

```
cd d:\practice\smart-nursing-admin
npm run dev
```

启动后访问 `http://localhost:5173`，登录账号 `admin` / 密码 `123456`。

### 移动端

```
cd d:\practice\smart-nursing-mobile
npm run dev
```

启动后访问 `http://localhost:5174`（H5 模式）。

---

## 3 最近在做什么任务

整个项目经历了多个阶段的开发：

**第一阶段**：后端 API 全部开发完成，移动端（uni-app）所有页面开发完成。

**第二阶段**：移动端 UI 视觉全面重构——按照用户提供的详细设计规范（雾柔医护蓝 `#D1E4F5`、浅医护苔青 `#93B4B8`、暖陶土橙 `#C77A60` 等配色方案），重新美化所有移动端页面，所有功能和 API 接口保持不变。

**第三阶段（近期重点）**：PC 管理后台的 Bug 修复与功能验证。用户反馈「该有的功能一个也没有呈现出来，而且还会反复出现红色的请求失败」。经过排查，发现并修复了登录失败、Dashboard 全零、用户管理空白、侧边栏菜单不展开、路由动态加载失败等一系列问题。

---

## 4 已经完成了什么

### 4.1 PC 管理后台修复清单

#### 登录系统修复

登录接口使用查询参数（不是 JSON body）：`POST /api/login?username=admin&password=123456`。之前前端发送格式与后端不匹配导致登录失败，已修复 `src/api/auth.js` 和 `src/views/login/LoginView.vue`。

#### Dashboard 数据展示

Dashboard 之前显示全零。原因是 token 过期/无效，后端返回 500。修复后 Dashboard 正常展示：
- 用户数 4、文章数 9、视频数 11、考试数 2
- 类别内容分布甜甜圈图（8 个护理类别）
- 学习趋势折线图

后端接口：`GET /api/admin/dataCount/dashboard`

#### 侧边栏菜单展开问题（关键修复）

**问题**：el-sub-menu 点击后不展开，子菜单始终隐藏。

**根因**：Element Plus 存在双重注册——`main.js` 中 `app.use(ElementPlus)` 全局注册了一遍，同时 `vite.config.js` 中 `unplugin-vue-components` 的 `ElementPlusResolver()` 又自动导入了一遍。双重注册破坏了 el-menu/el-sub-menu 的 provide/inject 机制。

**解决方案**：彻底放弃 el-menu/el-sub-menu，在 `HomeView.vue` 中用原生 div + CSS 实现自定义侧边栏菜单。使用 `max-height` 过渡动画控制展开/折叠，用 Vue 的 `ref(new Set())` 追踪展开的菜单组。同时从 `vite.config.js` 中移除了 `Components` 插件，只保留 `AutoImport`。

#### 路由动态加载修复（关键修复）

**问题**：直接在浏览器地址栏输入 `/role` 等动态路由 URL，页面卡在「加载中」空白页。

**根因**：`router/index.js` 导航守卫中 `next({ ...to, replace: true })` 会把 `to.matched`（旧的匹配记录，即 CatchAll 兜底路由）一起传递，导致路由器复用旧的匹配结果而非重新解析路径。

**解决方案**：
1. 将 `next({ ...to, replace: true })` 改为 `next({ path: to.fullPath, replace: true })`，只传路径不传 `matched`，强制路由器重新解析
2. 增加兜底检测：当动态路由已加载但目标仅匹配到 CatchAll 时，跳转回首页
3. 添加 CatchAll 兜底路由 `/:pathMatch(.*)*`，使用 `component: { render: () => null }` 空渲染组件，避免「No match found」警告的同时保留原始路径供导航守卫重新解析

#### 其他修复

- `generateRoutes` 函数增加 `menuList` 为 null 的防御性检查
- 跳过 path 为 null/empty 的目录节点，只处理其子菜单
- 跳过已存在的基础路由（如 `/dashboard`），防止覆盖
- 15 个 API 文件全部重写，确保字段名与后端实体对齐

### 4.2 全部 15 个管理页面验证通过

通过浏览器直接 URL 导航（完整页面刷新）逐页验证：

| 页面 | 路径 | 状态 | 数据 |
|------|------|------|------|
| 首页 | `/dashboard` | 正常 | 4用户/9文章/11视频/2考试 + 图表 |
| 用户管理 | `/user` | 正常 | 4 条用户记录 |
| 角色管理 | `/role` | 正常 | 3 条角色记录 |
| 菜单管理 | `/menu` | 正常 | 树形 4 组 24 项 |
| 类别管理 | `/category` | 正常 | 树形 3 组 8 类 |
| 标签管理 | `/tag` | 正常 | 搜索/按钮就绪 |
| 文章管理 | `/article/list` | 正常 | 9 篇文章 |
| 视频管理 | `/video/list` | 正常 | 10 个视频（多页） |
| PPT管理 | `/ppt/list` | 正常 | 搜索/按钮就绪 |
| 考试列表 | `/exam/list` | 正常 | 2 场考试 |
| 试题库 | `/exam/question` | 正常 | 搜索/按钮就绪 |
| 日志管理 | `/log` | 正常 | 搜索/按钮就绪 |
| 成绩管理 | `/examRecord/list` | 正常 | 搜索/按钮就绪 |
| 学习记录 | `/learning` | 正常 | 搜索/按钮就绪 |
| AI助手 | `/myai` | 正常 | 聊天界面就绪 |

---

## 5 当前状态

**PC 管理后台已全部修复并验证通过。** 所有页面可以通过侧边栏点击导航，也可以通过直接输入 URL 访问。Dashboard 展示真实数据，各管理页面数据加载正常，增删改查按钮就绪。

**尚未完成的事项：**
- 代码尚未提交到 Git（用户之前提到要上传 GitHub，但尚未执行）
- 各管理页面的「新增」「编辑」弹窗的具体交互未逐一点击测试（只验证了页面加载和数据展示）
- 移动端和后端在本轮会话中未做改动，状态应与之前一致

---

## 6 下一步计划

1. **提交代码到 Git**——用户之前提到要上传 GitHub，这是下一个待办事项
2. **深入测试管理页面交互**——逐个点击「新增」「编辑」「删除」按钮，验证弹窗和表单提交是否正常
3. **移动端回归测试**——确认移动端功能没有被本轮修改影响
4. **生产构建验证**——运行 `npm run build` 确认生产构建无报错

---

## 7 踩坑记录（绝对不要再踩）

### 7.1 Element Plus 双重注册破坏 provide/inject

**现象**：el-menu 中的 el-sub-menu 点击不展开，子菜单始终隐藏。

**根因**：`app.use(ElementPlus)` 全局注册 + `unplugin-vue-components` 的 `ElementPlusResolver()` 自动导入，双重注册导致 provide/inject 上下文断裂。

**教训**：Element Plus 要么全局注册，要么按需自动导入，**不能两者同时使用**。本项目最终选择全局注册（`main.js` 中 `app.use(ElementPlus)`），从 `vite.config.js` 中移除了 `Components` 插件。并且彻底放弃 el-menu，改用自定义 div 菜单。

### 7.2 Vue Router 导航守卫 next() 不能传整个 to 对象

**现象**：直接 URL 访问动态路由（如 `/role`）卡在空白页，但从 Dashboard 点击侧边栏跳转正常。

**根因**：`next({ ...to, replace: true })` 展开了整个 `to` 对象，包括 `to.matched`（旧的匹配记录）。路由器在第二次守卫执行时复用这些旧匹配记录，不会重新解析路径。

**教训**：添加动态路由后重新导航时，**只传 path**：`next({ path: to.fullPath, replace: true })`。不要传 `...to`。

### 7.3 后端菜单树中 path 为 null 的节点会导致前端崩溃

**现象**：前端 `router.addRoute()` 时抛异常，路由守卫进入 catch，清除 token，跳回登录页。

**根因**：后端返回的菜单树中，目录节点（如「系统管理」）的 path 字段为 null。前端直接用这个 null path 注册路由，Vue Router 报错。

**教训**：`generateRoutes` 函数中必须跳过 path 为 null/empty 的目录节点，只处理它们的子菜单。

### 7.4 前后端字段名不一致导致数据显示空白

**现象**：表格数据已加载但字段显示为空。

**根因**：前端用 `userId` 但后端返回 `id`；前端用 `realName` 但后端返回 `nickname` 等。

**教训**：前后端字段名必须严格对齐。关键映射：`userId`（不是 `id`）、`realName`（不是 `nickname`）、`menuId`、`sortOrder`、`visible`、`categoryName`、`examName`、`maxAttempts`、`questionType`。

### 7.5 登录接口用查询参数不是 JSON Body

**现象**：登录请求失败，返回 415 或 500。

**根因**：后端 `LoginController` 的 `/login` 接口使用 `@RequestParam` 接收参数，前端却用 JSON Body 发送。

**教训**：登录请求格式是 `POST /api/login?username=admin&password=123456`，参数在 URL 查询串中，不是 JSON Body。

### 7.6 Vite HMR 频繁修改文件后会崩

**现象**：控制台出现大量 Vue 警告、「Something went wrong during Vue component hot-reload」、页面白屏。

**根因**：快速连续修改多个文件时，HMR 状态不一致，尤其是修改 `router/index.js` 这种核心文件时。

**教训**：修改核心文件后如果页面异常，直接杀掉 Vite 进程重启，不要试图通过继续修改文件来修复 HMR 状态。重启命令：`taskkill /PID <pid> /F` 然后 `npm run dev`。

### 7.7 BCrypt 密码哈希必须匹配

**现象**：用 `admin` / `123456` 登录失败，提示密码错误。

**根因**：`init.sql` 中的 BCrypt 哈希值不是 `123456` 的有效哈希。

**教训**：`init.sql` 中的密码哈希必须是用 BCrypt 对 `123456` 加密后的真实值，不能随便填一个哈希字符串。

### 7.8 前端硬编码类别字符串导致后端 NumberFormatException

**现象**：按类别筛选内容时后端报 500 错误。

**根因**：前端硬编码了类别名称字符串（如「基础护理」），后端期望的是数字 categoryId。

**教训**：类别筛选必须用动态加载的数字 categoryId（1=基础护理, 2=专科护理, etc.），不能硬编码字符串。

---

## 8 关键文件清单

### PC 管理后台（`smart-nursing-admin/src/`）

| 文件 | 作用 | 备注 |
|------|------|------|
| `router/index.js` | 路由配置 + 动态路由加载 + 导航守卫 | 本轮重点修改，含 CatchAll 兜底和动态路由注入逻辑 |
| `stores/user.js` | Pinia 用户状态（token/userInfo/menuList） | token 和 menuList 持久化到 localStorage |
| `views/home/HomeView.vue` | 主布局 + 自定义侧边栏菜单 | 用 div + CSS 替代 el-menu，max-height 过渡动画 |
| `views/login/LoginView.vue` | 登录页 | 查询参数方式登录 |
| `views/dashboard/DashboardView.vue` | 首页仪表盘 | 统计卡片 + 甜甜圈图 + 趋势图 |
| `utils/request.js` | axios 封装 | 请求前缀 `/api`，响应拦截器处理 token 过期 |
| `api/auth.js` | 认证相关 API | login/logout/getUserInfo/getMenuList |
| `main.js` | 应用入口 | `app.use(ElementPlus)` 全局注册 |
| `vite.config.js` | Vite 配置 | 只有 AutoImport 插件，**没有** Components 插件 |

### 15 个 API 文件（`smart-nursing-admin/src/api/`）

`ai.js` `article.js` `auth.js` `category.js` `dataCount.js` `exam.js` `examRecord.js` `learning.js` `log.js` `menu.js` `ppt.js` `question.js` `role.js` `tag.js` `user.js` `video.js`

全部已重写，字段名与后端实体对齐。

### 15 个视图页面（`smart-nursing-admin/src/views/`）

- `system/` — UserView, RoleView, MenuView, LogView
- `content/` — ArticleList, ArticleEdit, VideoList, VideoEdit, PptList, PptEdit, CategoryView, TagView
- `exam/` — ExamList, ExamEdit, QuestionBank, RecordList, RecordDetail
- `learning/` — LearningRecord
- `ai/` — AiView
- `dashboard/` — DashboardView
- `login/` — LoginView
- `home/` — HomeView

---

## 9 重要约束与约定

这些是项目中的硬性约定，修改代码时必须遵守：

### 前后端通信

- 后端 API 响应必须使用 UTF-8 编码
- 前端请求必须加 `/api` 前缀，Vite 代理重写时去掉 `/api`
- 登录接口：`POST /auth/login`（或 `/login`），查询参数格式
- 收藏操作需要 `contentType` + `contentId` 两个参数
- 考试提交用 URL 编码的查询参数，answers 为 JSON 字符串
- 学习进度上报用查询参数，包含 `contentType` 和 `studyDuration`

### 内容类型映射

- 1 = 文章，2 = 视频，3 = 课件（PPT）
- 考试状态：0 = 未开始，1 = 可考试，2 = 已结束
- 类别筛选必须用数字 categoryId（1=基础护理, 2=专科护理, etc.）

### 内容要求

- 文章内容至少 3000 字
- 视频时长至少 10 分钟

### 路由约定

- `router/index.js` 的 `generateRoutes` 中跳过已定义的基础路由路径（如 `/dashboard`），防止覆盖
- 跳过 path 为 null/empty 的菜单目录节点，只处理子菜单
- 动态路由通过 `router.addRoute('Home', route)` 注入为 Home 路由的子路由
- Home 路由必须有 `name: 'Home'`，否则 `addRoute('Home', ...)` 找不到父路由

### 技术版本

- JDK 17
- Vite 6.4.3
- Vue 3
- Element Plus（全局注册，不使用按需导入）
- Pinia
- Vue Router 4
