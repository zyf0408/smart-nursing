# 项目交接文档

> 最后更新：2026-07-21
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

**第六阶段（7/20）**：移动端 modal 弹窗系列问题集中修复——包括 hook 破坏 uni.showModal、modal 重复弹出无法取消、ghost modal 导航后不消失等 4 个衍生 bug。

**第七阶段（7/21）**：考试判分系列问题集中修复——包括答案格式不匹配导致0分、判断题答案未归一化、多选题脏数据判错、结果页字段名不匹配、管理端试题选择弹窗不加载数据等 5 个独立 bug。

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

### 7/21 考试判分系列修复详情

本次集中修复了考试结果0分及判分异常的多个问题，共涉及 5 个独立 bug，横跨后端、PC 管理端、移动端三层：

**Bug 1 - 答案格式不匹配导致0分**：移动端交卷时前端提交的 answers 是数组格式 `[{questionId:123, answer:"A"}]`，但后端 `parseAnswers` 用 `Map<String,String>` 反序列化，Jackson 解析失败后 catch 返回空 Map，所有题目 `userAnswer=null` → 全部判错0分。修复为前端提交 Map 格式 `{"123":"A"}`。

**Bug 2 - 判断题答案未归一化**：数据库判断题 `answer='false'`（小写字符串），前端提交 `'F'`。`'F'.equalsIgnoreCase('false')` 因长度不同返回 false，导致答对被判错。修复为后端新增 `normalizeJudgeAnswer` 方法，将 true/false/T/F/正确/错误/对/错 统一转为 T/F 后再比较。

**Bug 3 - 多选题脏数据判错**：管理端试题编辑页 `handleEdit` 用 `split(',')` 拆分 answer，但 init.sql 里多选题 answer 是 `"ABCD"`（无分隔符），拆分得到 `["ABCD"]`（一个元素）不匹配 checkbox，编辑保存后 answer 变成 `"ABCD,A,B,C,D"`（畸形数据）。`sortAnswer("ABCD,A,B,C,D")` 排序后得到 `"AABBCCDD"`，与用户提交的 `"ABCD"` 不匹配，判错。修复方案：① 后端 `sortAnswer` 改用 TreeSet 去重只保留 A-Z 字母；② 前端 `handleEdit` 用正则 `match(/[A-Za-z]/g)` 提取字母；③ 前端 `handleSubmit` 多选题用 `join('')` 无分隔符；④ 数据库脏数据已清理（第4、9题 answer 从 `"ABCD,A,B,C,D"` 修正为 `"ABCD"`）。

**Bug 4 - 结果页字段名不匹配**：后端 `ExamResultVo` 返回 `answerDetails` 字段，但移动端结果页读的是 `result.answers || result.details`，字段名不匹配导致答题统计全0。同时后端 answerDetails 里 `questionType` 是数字（1/2/3/4），前端 `getTypeText` 期望字符串（single/multiple/judge/essay）。修复为前端读取 `answerDetails` 字段并做题型映射、选项构建、多选答案转换。

**Bug 5 - 管理端试题选择弹窗不加载数据**：`ExamEdit.vue` 点击"添加试题"按钮只设 `questionSelectVisible=true`，没有调用 `loadQuestions()`，弹窗打开时显示"暂无数据"。修复为 el-dialog 添加 `@open="loadQuestions"`，弹窗打开时自动加载试题列表。

**涉及文件**：
- `smart-nursing-backend/src/main/java/com/smart/nursing/service/impl/ExamRecordServiceImpl.java` - 答案归一化、判分日志、多选去重、answerDetails 补充选项字段
- `smart-nursing-mobile/src/pages/exam/exam-paper.vue` - 答案格式从数组改为 Map
- `smart-nursing-mobile/src/pages/exam/exam-result.vue` - 字段名映射、题型转换、判断题归一化显示、多选选项状态增强
- `smart-nursing-admin/src/views/exam/ExamEdit.vue` - 试题选择弹窗 @open 加载
- `smart-nursing-admin/src/views/exam/QuestionBank.vue` - 多选题答案拆分和拼接修复

### 数据库调整（7/21 测试期间）

测试过程中清理了 `nursing_question` 表中多选题的脏数据：将第4、9题的 `answer` 字段从 `"ABCD,A,B,C,D"` 修正为 `"ABCD"`，与 init.sql 原始格式一致。这不是代码变更，但如果重新初始化数据库需要注意。

### 代码提交状态

| 提交 | 内容 |
|------|------|
| `462111f` | 修复 modal 导航后不消失（ghost modal）- 去掉 CSS display !important |
| `056bd75` | 修复开始考试/提交答卷 modal 重复弹出无法取消 - 添加防重复点击锁 |
| `0802709` | 修复提交答卷无反应 - 移除破坏 uni.showModal 的 hook 和 DOM 清理 |
| `ee1b055` | 修复登录 toast 残留导致后续页面按钮无法点击 |
| `4cc996d` | 修复提交答卷无反应 - 移除破坏 uni.showModal 的 hook（早期尝试） |
| `04ee0db` | 移动端全面优化 + 搜索功能修复 + 清理指导文档 |
| `8a0c2ca` | 添加项目 README 文档 |
| `4c11027` | 修复路由跳转、AI功能、日志管理和Long精度丢失问题 |
| `d47252d` | 修复移动端多模块问题并添加项目文档 |
| `fe093cc` | 智慧护理培训系统全栈代码（初始版本） |

### 7/20 modal 弹窗系列修复详情

本次集中修复了 `uni.showModal` 在 H5 模式下的多个衍生问题，共涉及 5 次提交、4 个独立 bug：

**Bug 1 - 登录 toast 残留**：登录成功后的 `uni.showToast` 遮罩未清除，残留在后续页面上拦截点击。修复为登录跳转前调用 `uni.hideToast()`。

**Bug 2 - 提交答卷无反应（hook 破坏 uni.showModal）**：之前在 `App.vue` 中 hook 了 `history.pushState`/`replaceState` 并在路由变化时调用 `cleanupResidual` 直接移除 `uni-modal` DOM 元素（`el.remove()`），破坏了 uni-app 内部状态管理。uni-app 内部有一个状态标志跟踪 modal 是否正在显示，当 DOM 被直接移除时这个状态没有被重置，导致后续 `uni.showModal` 调用被静默忽略。修复为完全移除 hook 和 DOM 清理代码，改用 CSS `:has()` 选择器隐藏残留的空 modal（`uni-modal:not(:has(.uni-modal)) { display: none }`）。

**Bug 3 - modal 重复弹出无法取消**：`@tap` 事件可能重复触发（事件冒泡/快速双击）导致 `uni.showModal` 被调用多次，第二个 modal 因 uni-app 内部状态冲突，按钮事件无法正确绑定，表现为"取消不掉"。修复为在 `handleStartExam` 和 `confirmSubmit` 中添加防重复点击锁（`isStarting`/`isConfirming`），在 modal 的 `success`（确认/取消）和 `fail` 回调中释放锁。

**Bug 4 - ghost modal（导航后不消失）**：`App.vue` 中 CSS `uni-modal { display: flex !important }` 的 `!important` 强制所有 modal 可见，阻止了 uni-app 通过内联 `style="display:none"` 隐藏 modal。用户点击"开始"后跳转到答题页，但原 modal 仍被强制显示。修复为去掉 `display` 的 `!important`（`display: flex !important` → `display: flex`），让 uni-app 能通过内联样式覆盖来控制 modal 显隐，同时默认值 `flex` 仍然修复了 `uni-modal` 自定义元素默认 `display:inline` 的问题。

**涉及文件**：
- `smart-nursing-mobile/src/App.vue` - 移除 hook/DOM 清理，CSS 修复
- `smart-nursing-mobile/src/pages/exam/exam-detail.vue` - 添加 `isStarting` 防重复锁
- `smart-nursing-mobile/src/pages/exam/exam-paper.vue` - 添加 `isConfirming` 防重复锁
- `smart-nursing-mobile/src/pages/login/login.vue` - 登录前 `hideToast()`

### 数据库调整（7/20 测试期间）

测试过程中将 `nursing_exam` 表中 `exam_id=1` 和 `exam_id=2` 的 `max_attempts` 从默认值调整为 100，方便反复测试考试流程。这不是代码变更，但如果重新初始化数据库需要注意。

---

## 6 下一步计划

1. **生产构建验证**——运行 `npm run build` 确认生产构建无报错
2. **各管理页面的「新增」「编辑」弹窗交互**——逐个点击测试表单提交
3. **移动端真机测试**——在手机浏览器/微信内置浏览器中验证响应式
4. **数据库 `max_attempts` 配置**——正式环境部署时，根据实际需求设置考试最大次数（测试期间临时设为 100）

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

### 7.16 不要直接移除 uni-modal DOM 元素

**现象**：点击"提交答卷"后没有任何反应，`uni.showModal` 的 `success`/`fail` 回调都不触发。

**根因**：之前在 `App.vue` 的 `cleanupResidual` 函数中用 `document.querySelectorAll('uni-modal').forEach(el => el.remove())` 直接移除 `uni-modal` DOM 元素。uni-app 内部有一个状态标志跟踪 modal 是否正在显示，当 DOM 被直接移除时这个状态没有被重置（uni-app 以为 modal 还在显示），导致后续 `uni.showModal` 调用被静默忽略——modal 不被创建，回调也不触发。

**教训**：**永远不要直接移除 uni-app 管理的 DOM 元素**（`uni-modal`、`uni-toast`、`.uni-mask` 等）。如果需要处理残留的遮罩，用 CSS 隐藏（如 `uni-modal:not(:has(.uni-modal)) { display: none }`），而不是移除 DOM。uni-app 的内部状态管理依赖于 DOM 的存在，直接移除会破坏状态机。

### 7.17 uni-modal CSS 的 display 不要用 !important

**现象**：点击"开始考试"的"开始"按钮后，页面跳转到了答题页，但"确认开始考试"的 modal 仍然显示在上面（ghost modal），无法取消。

**根因**：`App.vue` 中 CSS `uni-modal { display: flex !important }` 的 `!important` 强制所有 modal 可见。uni-app 在用户点击按钮后会通过内联 `style="display:none"` 隐藏 modal，但 `!important` 的优先级高于内联样式，导致 uni-app 无法隐藏 modal。

**教训**：**不要用 `!important` 强制 uni-app 组件的 `display` 属性**。uni-app 需要通过内联样式动态控制组件显隐，`!important` 会阻止这一机制。正确做法是 `display: flex`（不带 `!important`），这样既能在默认情况下让 `uni-modal` 自定义元素正确显示为 flex（覆盖浏览器默认的 `display:inline`），又能让 uni-app 通过内联样式覆盖来隐藏它。

### 7.18 uni.showModal 可能因 @tap 重复触发而被调用多次

**现象**：点击"开始考试"后，"确认开始考试"的 modal 弹出了两次，第二个 modal 无法取消（点击"取消"和"开始"都没反应）。

**根因**：uni-app H5 模式下 `@tap` 事件可能重复触发（事件冒泡或快速双击），导致 `uni.showModal` 被调用多次。第二个 modal 因 uni-app 内部状态冲突（uni-app 认为 modal 已经在显示了），按钮事件无法正确绑定，表现为"取消不掉"。

**教训**：**所有调用 `uni.showModal` 的按钮事件都要加防重复点击锁**。用一个 ref 标志位（如 `isStarting`/`isConfirming`），进入流程时置 `true`，在 modal 的 `success`（无论确认还是取消）和 `fail` 回调中释放锁。确认后的业务流程结束后也要延迟释放锁（如 `setTimeout(() => { isStarting.value = false }, 500)`），避免导航后立即重复触发。

### 7.19 考试答案必须用 Map 格式提交，不能用数组

**现象**：移动端交卷后考试结果0分，所有题目都判错，但用户明明选了正确答案。

**根因**：移动端 `exam-paper.vue` 提交的 answers 是数组格式 `[{questionId:123, answer:"A"}]`，但后端 `parseAnswers` 用 `objectMapper.readValue(answers, new TypeReference<Map<String, String>>() {})` 反序列化为 `Map<String,String>`。Jackson 无法将 JSON 数组反序列化为 Map，抛异常后被 catch，返回空 Map，导致所有题目 `userAnswer=null`，全部判错0分。

**教训**：前后端答案格式必须严格对齐。后端 `parseAnswers` 期望 `{"questionId": "userAnswer"}` 的 Map 格式，前端必须提交 Map 而非数组。格式不匹配时 Jackson 静默失败（catch 后返回空 Map），不会抛出可见错误，极易被忽略。

### 7.20 判断题答案必须归一化后比较

**现象**：判断题用户选了"错误"，正确答案也是"错误"，但被判错0分。

**根因**：数据库判断题 `answer` 字段值可能是 `'false'`、`'错'`、`'F'` 等多种格式，前端提交的是 `'F'`。`'F'.equalsIgnoreCase('false')` 因字符串长度不同（1 vs 5）返回 false，`'F'.equalsIgnoreCase('错')` 也返回 false。表面看用户答案和正确答案都是"错误"，但实际比较的字符串值不同。

**教训**：判断题答案比较前必须做归一化。本项目新增 `normalizeJudgeAnswer` 方法，将 true/false/T/F/正确/错误/对/错 统一转为 T/F 后再用 `equalsIgnoreCase` 比较。前端显示也必须归一化（`formatJudgeText`），否则会出现"正确答案:错误，你的答案:错误"但被判错的迷惑性表现。

### 7.21 多选题答案格式必须统一，编辑页拆分要兼容

**现象**：多选题用户全选 ABCD，正确答案也是 ABCD，但被判错0分。后端日志显示 `correctAnswer=[ABCD,A,B,C,D]`（畸形数据）。

**根因**：管理端试题编辑页 `QuestionBank.vue` 的 `handleEdit` 用 `row.answer.split(',')` 拆分多选题答案。但 init.sql 里多选题 answer 是 `"ABCD"`（无分隔符），`split(',')` 得到 `["ABCD"]`（一个元素），不匹配任何 checkbox（value 是单字母 A/B/C/D），所以编辑时 checkbox 全部未选中。用户重新勾选后，旧的 `"ABCD"` 未清除 + 新勾选的 A/B/C/D，`answerList` 变成 `["ABCD","A","B","C","D"]`，`join(',')` = `"ABCD,A,B,C,D"`（畸形数据）。后端 `sortAnswer` 对 `"ABCD,A,B,C,D"` 排序得到 `"AABBCCDD"`，与用户提交的 `"ABCD"` 不匹配，判错。

**教训**：① 多选题 answer 格式必须全项目统一（推荐无分隔符 `"ABCD"`）；② 编辑页拆分答案不能用简单 `split(',')`，要用正则 `match(/[A-Za-z]/g)` 提取字母，兼容各种格式；③ 后端 `sortAnswer` 要做防御性处理（去重、只保留字母），即使数据库有脏数据也能正确判分；④ 保存时用 `join('')` 无分隔符，与 init.sql 格式一致。

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
| `App.vue` | 全局入口 + uni-modal CSS 样式 | 7/20 修复：移除 hook/DOM 清理，CSS display 去掉 !important |
| `api/request.js` | 请求封装 | 401 防抖锁，hideLoading 时不弹 toast |
| `api/index.js` | API 接口 | startExam/submitExam 都传 hideLoading:true |
| `pages/exam/exam-detail.vue` | 考试详情页 | 7/20 添加 isStarting 防重复锁；modal 后加 300ms 延迟，finally 清理 loading |
| `pages/exam/exam-paper.vue` | 答题页 | 7/20 添加 isConfirming 防重复锁；交卷确认同样处理灰屏问题 |
| `pages/exam/exam-result.vue` | 考试结果页 | - |
| `pages/learn/index.vue` | 学习中心 | Banner + 分类筛选 + 内容列表 |
| `pages/login/login.vue` | 登录页 | 7/20 添加登录前 hideToast()；记住用户名功能 |
| `pages/user/index.vue` | 个人中心 | 学习记录、考试记录、收藏 |

---

## 9 重要约束与约定

这些是项目中的硬性约定，修改代码时必须遵守：

### 前后端通信

- 后端 API 响应必须使用 UTF-8 编码
- 前端请求必须加 `/api` 前缀，Vite 代理重写时去掉 `/api`
- 登录接口：`POST /api/login?username=admin&password=123456`，参数在 URL 查询串中
- 收藏操作需要 `contentType` + `contentId` 两个参数
- 考试提交用 URL 编码的查询参数，answers 为 JSON 字符串，格式必须是 `{"questionId":"userAnswer"}` 的 Map（**不能是数组**）
- 学习进度上报用查询参数，包含 `contentType` 和 `studyDuration`
- **Long 类型 ID 序列化为 String**：通过 `JacksonConfig` 全局处理，前端收到的所有 ID 都是字符串

### 考试判分约定

- **答案格式**：前端提交的 answers JSON 必须是 `{"questionId":"userAnswer"}` 的 Map 格式，后端 `parseAnswers` 用 `Map<String,String>` 反序列化
- **判断题归一化**：数据库 answer 可能是 true/false/T/F/正确/错误/对/错，后端 `normalizeJudgeAnswer` 统一转为 T/F 后比较，前端 `formatJudgeText` 统一显示
- **多选题格式**：answer 字段统一用无分隔符格式（如 `"ABCD"`），编辑页用正则提取字母，保存用 `join('')`，后端 `sortAnswer` 用 TreeSet 去重只保留 A-Z
- **结果页字段对齐**：后端 `ExamResultVo.answerDetails` 返回答题详情，前端读取 `answerDetails` 字段（不是 `answers`/`details`）；`questionType` 是数字需映射为字符串（1→single, 2→multiple, 3→judge, 4→essay）
- **answerDetails 必须包含选项**：后端构建 answerDetails 时要补充 `optionA/B/C/D` 字段，供前端结果页渲染选项

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
- **不要直接移除 uni-app 管理的 DOM 元素**（`uni-modal`、`uni-toast`、`.uni-mask`），用 CSS 隐藏而非 `el.remove()`
- **uni-modal 的 CSS `display` 不要用 `!important`**，否则会阻止 uni-app 通过内联样式隐藏 modal
- **所有调用 `uni.showModal` 的按钮事件都要加防重复点击锁**，避免 `@tap` 重复触发导致 modal 弹出多次
- **登录/跳转前调用 `uni.hideToast()`**，避免 toast 遮罩残留在后续页面拦截点击

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
