# 项目交接文档

> 最后更新：2026-07-19
> 适用对象：完全没有任何上下文的新会话

---

## 1 这个项目是什么

「互联网+智慧护理线上实训平台」，一个面向护理专业学生的在线学习+考试系统，包含三端：

| 子项目 | 路径 | 技术栈 | 端口 |
|--------|------|--------|------|
| 后端 | `d:\practice\smart-nursing-backend` | Java 17 + Spring Boot 3.5.13 + MyBatis-Plus 3.5.12 + MySQL 8 + Redis | 8888 |
| 移动端 | `d:\practice\smart-nursing-mobile` | uni-app + Vue 3 | 5174 |
| PC管理后台 | `d:\practice\smart-nursing-admin` | Vue 3 + Vite + Element Plus + Pinia | 5173 |

数据库：MySQL，库名 `smart_nursing`，账号 `root`，密码 `123456`。初始化脚本在 `d:\practice\sql\init.sql`，简答题迁移脚本 `d:\practice\sql\migration_question_essay.sql`。

GitHub 仓库：`https://github.com/zyf0408/smart-nursing.git`，分支 `main`。

---

## 2 服务器启动方式

### 后端

```
# 方式一：IntelliJ IDEA
# 打开 smart-nursing-backend 项目，运行 SmartNursingApplication 主类
# 必须在 Run Configuration → Environment variables 中配置：
#   DASHSCOPE_API_KEY=sk-xxx
#   SPRING_AI_OPENAI_BASE_URL=https://ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com/compatible-mode
# 注意：base-url 不要带 /v1 后缀，Spring AI 会自动追加

# 方式二：命令行
$env:DASHSCOPE_API_KEY = "sk-xxx"
$env:SPRING_AI_OPENAI_BASE_URL = "https://ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com/compatible-mode"
cd d:\practice\smart-nursing-backend
mvn spring-boot:run -DskipTests
```

启动成功标志：控制台输出 `Started SmartNursingApplication in X.XXX seconds`，监听 8888 端口。

如果遇到 `java.net.BindException: Address already in use: bind`，说明端口被占用，执行：
```powershell
Get-NetTCPConnection -LocalPort 8888 -State Listen | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

### PC管理后台

```
cd d:\practice\smart-nursing-admin
npm run dev
```

启动后访问 `http://localhost:5173`，登录账号 `admin` / 密码 `123456`。

### 移动端

```
cd d:\practice\smart-nursing-mobile
npm run dev:h5
```

启动后访问 `http://localhost:5174`（H5 模式）。测试账号：`nurse01` / `123456`、`nurse02` / `123456`。

> 启动时控制台会出现 `Deprecation Warning [legacy-js-api]`，这是 Dart Sass 弃用警告，不影响运行，无需处理。

---

## 3 项目开发历程

整个项目经历了多个阶段的开发：

**第一阶段**：后端 API 全部开发完成，移动端（uni-app）所有页面开发完成。

**第二阶段**：移动端 UI 视觉全面重构——按照用户提供的详细设计规范（雾柔医护蓝 `#D1E4F5`、浅医护苔青 `#93B4B8`、暖陶土橙 `#C77A60` 等配色方案），重新美化所有移动端页面。

**第三阶段**：PC 管理后台的 Bug 修复与功能验证。修复了登录失败、Dashboard 全零、用户管理空白、侧边栏菜单不展开、路由动态加载失败等一系列问题。15 个管理页面全部验证通过。

**第四阶段（7/18）**：路由跳转、AI 功能、日志管理、Long 精度丢失等关键 Bug 修复。

**第五阶段（7/19）**：移动端考试流程灰屏问题修复，nurse02 权限问题修复。

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

**解决方案**：彻底放弃 el-menu/el-sub-menu，在 `HomeView.vue` 中用原生 div + CSS 实现自定义侧边栏菜单。同时从 `vite.config.js` 中移除了 `Components` 插件，只保留 `AutoImport`。

#### 路由动态加载修复（关键修复）

**问题**：直接在浏览器地址栏输入 `/role` 等动态路由 URL，页面卡在「加载中」空白页。

**根因**：`router/index.js` 导航守卫中 `next({ ...to, replace: true })` 会把 `to.matched`（旧的匹配记录，即 CatchAll 兜底路由）一起传递，导致路由器复用旧的匹配结果而非重新解析路径。

**解决方案**：
1. 将 `next({ ...to, replace: true })` 改为 `next({ path: to.fullPath, replace: true })`
2. 增加兜底检测：当动态路由已加载但目标仅匹配到 CatchAll 时，跳转回首页
3. 添加 CatchAll 兜底路由 `/:pathMatch(.*)*`

#### 编辑/详情页路由注册（7/18 修复）

**问题**：文章/视频/PPT/考试/学习记录的编辑页和详情页无法跳转，URL 被重定向回 dashboard。

**根因**：这些页面是动态路由，但 `router/index.js` 的 `generateRoutes` 只处理了列表页，没有注册编辑/详情子路由。

**解决方案**：在 `router/index.js` 静态路由部分添加编辑/详情页路由配置。

### 4.2 后端关键修复（7/18）

#### AI 功能修复

**问题**：AI 助手调用报 404，流式响应 loading 状态无法清除。

**根因**：
1. `SPRING_AI_OPENAI_BASE_URL` 配置了 `/v1` 后缀，Spring AI 又自动追加 `/v1/chat/completions`，导致路径变成 `/v1/v1/chat/completions` → 404
2. 流式响应完成后，`loading` 状态未在 `finally` 块中清除

**解决方案**：
1. `base-url` 不要带 `/v1` 后缀，直接用 `https://ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com/compatible-mode`
2. 流式响应代码用 `try/finally` 确保 loading 清除

#### 日志管理修复

**问题**：日志管理页面有表头列（请求地址、耗时），但 LogAspect 没有保存这些字段到数据库，导致列为空。

**根因**：
1. `LogAspect` 只输出日志到控制台，没有调用 `logService.save()` 持久化
2. `LogEntity` 缺少 `url` 和 `costTime` 字段
3. 数据库表 `sys_log` 缺少对应列

**解决方案**：
1. 数据库表 `sys_log` 添加 `url VARCHAR(500)` 和 `cost_time BIGINT` 列
2. `LogEntity` 添加 `url` 和 `costTime` 字段
3. `LogAspect.saveLog()` 方法设置 `setUrl(url)` 和 `setCostTime(costTime)`

#### Long 精度丢失修复（关键修复）

**问题**：点击删除按钮后用户还在，删除不生效。

**根因**：MyBatis-Plus 雪花算法生成的 ID 是 19 位长整型（如 `2078366489249439746`），超过了 JavaScript 的 `Number.MAX_SAFE_INTEGER`（`9007199254740991`，16 位）。前端 JS 解析时精度丢失，变成 `2078366489249439700`，删除请求传了错误 ID。

**解决方案**：新增 `JacksonConfig.java`，全局将 `Long` 和 `BigInteger` 序列化为 String：

```java
@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer longToStringCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
            module.addSerializer(BigInteger.class, ToStringSerializer.instance);
            // 必须用 modulesToInstall，不能是 modules，否则会覆盖 JavaTimeModule
            builder.modulesToInstall(module);
        };
    }
}
```

#### 考试流程修复（7/19）

**问题 1**：nurse01 点击"开始考试"后页面灰屏，不跳转。

**根因**（三层叠加）：
1. **后端 - 考试次数计算错误**：`startExam` 统计了所有考试记录（含进行中 status=1），nurse01 有 1 条完成 + 1 条进行中 = 2 次，达到 `max_attempts=2` 被拒绝 → 修复为只统计已交卷记录（status=2）
2. **后端 - 唯一键冲突**：修复次数计算后，新记录 `attemptCount=2` 但已有 `attempt_count=2` 的进行中记录，触发唯一键 `uk_exam_user_attempt` 冲突 → 修复为先删除旧的进行中记录，再用 `max(attempt_count)+1` 计算编号
3. **前端 - 灰屏**：`uni.showModal` 关闭动画还没完成就调用 `uni.showLoading({ mask: true })`，H5 模式下 modal 遮罩和 loading 遮罩叠加成两层，`hideLoading` 只关掉一层 → 修复为 modal 确认后加 300ms 延迟，并用 `finally` 确保 loading 清理

**问题 2**：nurse02 登录后显示"暂无学习内容"。

**根因**：nurse02 没有在 `sys_user_role` 表中分配角色，`AroundCut` 拦截器检查 `/mobile/` 路径需要 `NURSE` 角色，返回 403。

**解决方案**：给 nurse02 用户分配 NURSE 角色（role_id=3）。

### 4.3 全部 15 个管理页面验证通过

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
| 日志管理 | `/log` | 正常 | 请求地址/耗时/IP 都已展示 |
| 成绩管理 | `/examRecord/list` | 正常 | 搜索/按钮就绪 |
| 学习记录 | `/learning` | 正常 | 搜索/按钮就绪 |
| AI助手 | `/myai` | 正常 | 流式对话正常 |

---

## 5 当前状态

**项目已全部修复并推送到 GitHub。** 三端（后端、PC 管理端、移动端）功能正常，关键流程（登录、学习、考试、AI 助手、日志审计）均已实测通过。

### 代码提交状态

| 提交 | 内容 |
|------|------|
| `04ee0db` | 移动端全面优化 + 搜索功能修复 + 清理指导文档 |
| `8a0c2ca` | 添加项目 README 文档 |
| `4c11027` | 修复路由跳转、AI功能、日志管理和Long精度丢失问题 |
| `d47252d` | 修复移动端多模块问题并添加项目文档 |
| `fe093cc` | 智慧护理培训系统全栈代码（初始版本） |

### 未提交的本地修改（7/19 考试流程修复）

以下 5 个文件是 7/19 修复考试灰屏问题的代码，尚未提交到 Git：
- `smart-nursing-backend/.../ExamRecordServiceImpl.java` - 修复考试次数计算和 attemptCount 逻辑
- `smart-nursing-mobile/src/api/index.js` - submitExam 添加 hideLoading
- `smart-nursing-mobile/src/api/request.js` - hideLoading 时不弹 toast
- `smart-nursing-mobile/src/pages/exam/exam-detail.vue` - modal 后加 300ms 延迟，finally 清理
- `smart-nursing-mobile/src/pages/exam/exam-paper.vue` - 交卷确认 modal 后加延迟，finally 清理

---

## 6 下一步计划

1. **提交 7/19 修复到 Git**——考试流程灰屏和 nurse02 权限修复
2. **生产构建验证**——运行 `npm run build` 确认生产构建无报错
3. **各管理页面的「新增」「编辑」弹窗交互**——逐个点击测试表单提交
4. **移动端真机测试**——在手机浏览器/微信内置浏览器中验证响应式

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

### 7.9 Long 精度丢失导致删除/更新失败

**现象**：点击删除按钮后用户还在，删除不生效；或更新了错误的记录。

**根因**：MyBatis-Plus 雪花算法生成的 ID 是 19 位长整型（如 `2078366489249439746`），超过 JavaScript `Number.MAX_SAFE_INTEGER`（`9007199254740991`，16 位）。前端 JS 解析时精度丢失，变成 `2078366489249439700`，传给后端的是错误 ID。

**教训**：后端必须通过 `JacksonConfig` 将 `Long` 和 `BigInteger` 全局序列化为 String，前端收到的 ID 是字符串，不再有精度问题。**注意**：`builder.modulesToInstall(module)` 而非 `builder.modules(module)`，否则会覆盖 `JavaTimeModule` 导致 `LocalDateTime` 序列化失败报 500。

### 7.10 uni-app H5 模式下 showModal + showLoading 遮罩叠加导致灰屏

**现象**：移动端点击"开始考试"或"交卷"后页面灰屏，不跳转。

**根因**：`uni.showModal` 弹出确认框（带半透明遮罩 backdrop），用户点确认后，**模态框关闭动画还没完成**就调用 `uni.showLoading({ mask: true })`。H5 模式下 modal 遮罩和 loading 遮罩**叠加成两层**，`uni.hideLoading()` 只关掉 loading 层，modal 遮罩层永久残留 → 灰屏。

**教训**：`uni.showModal` 的 `success` 回调中调用 `uni.showLoading` 前，必须加 300ms 延迟等遮罩关闭：
```javascript
success: async (res) => {
  if (res.confirm) {
    await new Promise(r => setTimeout(r, 300))
    uni.showLoading({ title: '准备中...', mask: true })
    // ... 业务逻辑
    // 用 finally 确保 hideLoading 一定被调用
  }
}
```
同时，调用方自己管理 loading 时（`hideLoading: true`），`request.js` 不要弹 toast，避免与 loading mask 冲突。

### 7.11 Spring AI base-url 不能带 /v1

**现象**：AI 助手调用报 404。

**根因**：`SPRING_AI_OPENAI_BASE_URL` 配置了 `/v1` 后缀，Spring AI 内部会自动追加 `/v1/chat/completions`，导致最终路径变成 `/v1/v1/chat/completions` → 404。

**教训**：`base-url` 配置为 `https://xxx.aliyuncs.com/compatible-mode`，不要带 `/v1` 后缀。

### 7.12 端口被占用导致 IJ 启动失败

**现象**：IntelliJ IDEA 启动后端报 `java.net.BindException: Address already in use: bind`。

**根因**：之前用命令行 `mvn spring-boot:run` 启动的后端进程还在运行，占着 8888 端口。IJ 再启动就冲突。

**教训**：命令行启动的后端和 IJ 启动的后端共用 8888 端口，二者只能运行一个。切换启动方式前，先杀掉旧进程：
```powershell
Get-NetTCPConnection -LocalPort 8888 -State Listen | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }
```

### 7.13 JacksonConfig modules() 会覆盖默认模块

**现象**：配置 `JacksonConfig` 后，API 调用报 500，日志显示 `InvalidDefinitionException: Java 8 date/time type ... not supported by default`。

**根因**：`builder.modules(module)` 会**替换**所有 Jackson 模块，包括处理 `LocalDateTime` 的 `JavaTimeModule`。

**教训**：用 `builder.modulesToInstall(module)` 添加模块，不要用 `builder.modules(module)`。前者是追加，后者是替换。

### 7.14 考试次数统计要把进行中记录排除

**现象**：nurse01 有 1 条已完成 + 1 条进行中的考试记录，`startExam` 统计为 2 次，达到 `max_attempts=2` 被拒绝，无法重考。

**根因**：`startExam` 统计考试次数时没有过滤 status，把"进行中"(status=1)的记录也计入次数。

**教训**：只统计已交卷的记录（status=2），未完成的进行中记录不计入次数。同时创建新记录前，先删除旧的进行中记录，避免唯一键 `uk_exam_user_attempt` 冲突。

### 7.15 移动端接口需要 NURSE 角色

**现象**：nurse02 登录后访问学习内容显示"暂无学习内容"。

**根因**：nurse02 没有在 `sys_user_role` 表中分配角色，`AroundCut` 拦截器检查 `/mobile/` 路径需要 `NURSE` 角色，返回 403。

**教训**：新增护士用户时，必须在 `sys_user_role` 表中分配 `NURSE` 角色（role_id=3），否则无法访问移动端接口。

---

## 8 关键文件清单

### 后端（`smart-nursing-backend/src/main/java/com/smart/nursing/`）

| 文件 | 作用 | 备注 |
|------|------|------|
| `common/config/JacksonConfig.java` | Long 序列化为 String | 7/18 新增，解决前端精度丢失 |
| `aop/LogAspect.java` | 操作日志切面 | 7/18 修复，持久化到数据库并保存 url/costTime |
| `aop/AroundCut.java` | Token 校验切面 | 检查 `/mobile/` 路径需要 NURSE 角色 |
| `service/impl/ExamRecordServiceImpl.java` | 考试记录服务 | 7/19 修复，考试次数计算和 attemptCount 逻辑 |
| `entity/LogEntity.java` | 日志实体 | 7/18 添加 url 和 costTime 字段 |
| `controller/admin/` | PC 管理端接口 | 15 个 Controller |
| `controller/mobile/` | 移动端接口 | 5 个 Controller |
| `service/impl/AiServiceImpl.java` | AI 服务 | 流式对话 + 简答题评分 |

### PC 管理后台（`smart-nursing-admin/src/`）

| 文件 | 作用 | 备注 |
|------|------|------|
| `router/index.js` | 路由配置 + 动态路由加载 + 导航守卫 | 含 CatchAll 兜底和动态路由注入逻辑 |
| `stores/user.js` | Pinia 用户状态（token/userInfo/menuList） | token 和 menuList 持久化到 localStorage |
| `views/home/HomeView.vue` | 主布局 + 自定义侧边栏菜单 | 用 div + CSS 替代 el-menu |
| `views/login/LoginView.vue` | 登录页 | 查询参数方式登录 |
| `views/dashboard/DashboardView.vue` | 首页仪表盘 | 统计卡片 + 甜甜圈图 + 趋势图 |
| `utils/request.js` | axios 封装 | 请求前缀 `/api`，响应拦截器处理 token 过期 |
| `api/auth.js` | 认证相关 API | login/logout/getUserInfo/getMenuList |
| `main.js` | 应用入口 | `app.use(ElementPlus)` 全局注册 |
| `vite.config.js` | Vite 配置 | 只有 AutoImport 插件，**没有** Components 插件 |

### 移动端（`smart-nursing-mobile/src/`）

| 文件 | 作用 | 备注 |
|------|------|------|
| `api/request.js` | 请求封装 | 401 防抖锁，hideLoading 时不弹 toast |
| `api/index.js` | API 接口 | startExam/submitExam 都传 hideLoading:true |
| `pages/exam/exam-detail.vue` | 考试详情页 | modal 后加 300ms 延迟，finally 清理 loading |
| `pages/exam/exam-paper.vue` | 答题页 | 交卷确认同样处理灰屏问题 |
| `pages/exam/exam-result.vue` | 考试结果页 | - |
| `pages/learn/index.vue` | 学习中心 | Banner + 分类筛选 + 内容列表 |
| `pages/login/login.vue` | 登录页 | 记住用户名功能 |
| `pages/user/index.vue` | 个人中心 | 学习记录、考试记录、收藏 |

---

## 9 重要约束与约定

这些是项目中的硬性约定，修改代码时必须遵守：

### 前后端通信

- 后端 API 响应必须使用 UTF-8 编码
- 前端请求必须加 `/api` 前缀，Vite 代理重写时去掉 `/api`
- 登录接口：`POST /api/login?username=admin&password=123456`，参数在 URL 查询串中
- 收藏操作需要 `contentType` + `contentId` 两个参数
- 考试提交用 URL 编码的查询参数，answers 为 JSON 字符串
- 学习进度上报用查询参数，包含 `contentType` 和 `studyDuration`
- **Long 类型 ID 序列化为 String**：通过 `JacksonConfig` 全局处理，前端收到的所有 ID 都是字符串

### 内容类型映射

- 1 = 文章，2 = 视频，3 = 课件（PPT）
- 考试状态：0 = 未开始，1 = 可考试，2 = 已结束
- 考试记录状态：1 = 进行中，2 = 已交卷
- 类别筛选必须用数字 categoryId（1=基础护理, 2=专科护理, etc.）

### 内容要求

- 文章内容至少 3000 字
- 视频时长至少 10 分钟

### 路由约定

- `router/index.js` 的 `generateRoutes` 中跳过已定义的基础路由路径（如 `/dashboard`），防止覆盖
- 跳过 path 为 null/empty 的菜单目录节点，只处理子菜单
- 动态路由通过 `router.addRoute('Home', route)` 注入为 Home 路由的子路由
- Home 路由必须有 `name: 'Home'`，否则 `addRoute('Home', ...)` 找不到父路由

### 移动端约定

- `uni.showModal` 后调用 `uni.showLoading` 前必须加 300ms 延迟
- 调用方自己管理 loading 时，传 `{ hideLoading: true }` 给 `request.js`
- `request.js` 在 `hideLoading=true` 时不弹 toast，由调用方 catch 块处理

### 技术版本

- JDK 17
- Spring Boot 3.5.13
- MyBatis-Plus 3.5.12
- Vue 3
- Element Plus（全局注册，不使用按需导入）
- Pinia
- Vue Router 4
- uni-app 5.21（Vue3）

---

## 10 AI 环境变量配置

后端 AI 功能（智能助手、简答题评分）依赖阿里云百炼大模型。**不配置环境变量会导致 Spring AI Bean 创建失败，后端无法启动。**

### IntelliJ IDEA 配置方式

1. 打开 Run Configuration（运行配置）
2. 选择 `SmartNursingApplication`
3. Environment variables 中添加：
   - `DASHSCOPE_API_KEY=sk-xxx`
   - `SPRING_AI_OPENAI_BASE_URL=https://ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com/compatible-mode`
4. 注意：base-url **不要**带 `/v1` 后缀

### 命令行配置方式

```powershell
$env:DASHSCOPE_API_KEY = "sk-xxx"
$env:SPRING_AI_OPENAI_BASE_URL = "https://ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com/compatible-mode"
mvn spring-boot:run -DskipTests
```
