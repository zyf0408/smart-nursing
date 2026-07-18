# 工程标准化交接文档 (handoff.md)

> 本文档基于现有项目架构总结提炼，作为后续所有工程的开发标准。所有新工程、新模块、新功能均需严格遵循本文档约定。

---

## 目录

- [一、项目总览与技术栈](#一项目总览与技术栈)
- [二、后端工程架构标准（Spring Boot）](#二后端工程架构标准spring-boot)
- [三、前端工程架构标准（Vue3）](#三前端工程架构标准vue3)
- [四、MyBatis / MyBatis-Plus 数据层标准](#四mybatis--mybatis-plus-数据层标准)
- [五、数据库设计标准](#五数据库设计标准)
- [六、API 接口设计标准](#六api-接口设计标准)
- [七、认证与权限机制](#七认证与权限机制)
- [八、统一返回结果与异常处理](#八统一返回结果与异常处理)
- [九、配置文件标准](#九配置文件标准)
- [十、命名规范总表](#十命名规范总表)
- [十一、编码约定](#十一编码约定)
- [十二、前后端联调约定](#十二前后端联调约定)
- [十三、日志规范](#十三日志规范)
- [十四、依赖版本锁定清单](#十四依赖版本锁定清单)
- [十五、工程目录速查模板](#十五工程目录速查模板)
- [十六、SpringAI 集成架构标准](#十六springai-集成架构标准)
- [十七、团队统一开发环境配置](#十七团队统一开发环境配置)

---

## 一、项目总览与技术栈

### 1.1 整体架构

本项目为前后端分离架构，采用 Spring Boot 3 后端 + Vue3 前端的技术体系：

```
┌─────────────────────────────────────────────────┐
│                   前端 (Vue3)                     │
│   Vue3 + Element Plus + Axios + Vue Router       │
│   Vite 构建 · localStorage 状态管理               │
└──────────────────────┬──────────────────────────┘
                       │ HTTP + Token Header
┌──────────────────────┴──────────────────────────┐
│                后端 (Spring Boot 3)               │
│   Controller → Service → DAO(MyBatis-Plus)       │
│   AOP切面(Token校验) · Redis · Swagger           │
└──────────────────────┬──────────────────────────┘
                       │
              ┌────────┴────────┐
              │   MySQL 数据库   │
              │   school_spring  │
              └─────────────────┘
```

### 1.2 技术栈版本锁定

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **后端框架** | Spring Boot | 3.5.13 | 父 POM 统一管理 |
| **Java** | JDK | 17 | 全项目统一 |
| **ORM** | MyBatis-Plus | 3.5.12 | spring-boot3-starter |
| **SQL解析** | mybatis-plus-jsqlparser | 3.5.12 | 分页插件必需 |
| **API文档** | SpringDoc OpenAPI | 2.8.4 | 适配 Spring Boot 3 |
| **缓存** | Spring Data Redis | Boot管理 | Lettuce 连接池 |
| **AOP** | spring-boot-starter-aop | Boot管理 | Token 切面 |
| **JSON** | FastJSON | 1.2.73 | Service 层数据组装 |
| **简化代码** | Lombok | Boot管理 | @Data/@ToString |
| **数据库** | MySQL | 8.x | 驱动 mysql-connector-j |
| **前端框架** | Vue | ^3.5.38 | Composition API |
| **前端UI** | Element Plus | ^2.14.3 | 全量引入 |
| **HTTP库** | Axios | ^1.18.1 | 拦截器封装 |
| **路由** | Vue Router | ^4.6.3 | History 模式 |
| **图表** | Echarts | ^6.0.0 | 数据可视化 |
| **构建工具** | Vite | ^8.0.16 | 前端构建 |
| **Node引擎** | Node.js | ^22.18.0 \|\| >=24.12.0 | — |
| **AI框架（核心）** | Spring AI | 1.1.2 | OpenAI 兼容模型支持 |
| **AI框架（阿里）** | Spring AI Alibaba | 1.1.2.0 | DashScope 通义大模型 |
| **AI SDK** | DashScope SDK | 2.16.7 | 原生文生图 SDK |
| **响应式** | spring-boot-starter-webflux | Boot管理 | 流式对话 SSE/Flux |
| **AI模型（主）** | 通义 DashScope | — | ChatModel + 文生图 |
| **AI模型（备）** | DeepSeek | — | OpenAI 兼容接口 |

---

## 二、后端工程架构标准（Spring Boot）

### 2.1 包结构

根包：`com.alex.swjtu.springboot`

```
com.alex.swjtu.springboot
├── SpringbootSwjtuApplication.java          # 启动类（@MapperScan 标注于此）
├── controller/                               # 控制层
│   └── XxxController.java
├── service/                                  # 业务逻辑层
│   ├── IXxxService.java                     # 接口（继承 IService<Entity>）
│   └── impl/
│       └── XxxServiceImpl.java              # 实现（继承 ServiceImpl<Mapper, Entity>）
├── dao/                                      # 数据访问层（注意：包名为 dao）
│   └── XxxMapper.java                       # Mapper 接口（继承 BaseMapper<Entity>）
├── entity/                                   # 实体层（数据库映射）
│   └── XxxEntity.java
├── dto/                                      # 数据传输对象
│   └── XxxDto.java                          # 继承 Entity，追加分页参数等
├── vo/                                       # 视图对象
│   └── XxxVo.java                           # 封装返回数据（如 LoginUserVo）
├── aop/                                      # AOP 切面
│   ├── AroundCut.java                       # Token 校验环绕切面
│   └── NoToken.java                         # 免 Token 注解
└── common/                                   # 公共模块
    ├── config/
    │   ├── SpringDocConfig.java             # Swagger/OpenAPI 配置
    │   ├── WebConfig.java                   # 跨域 CORS 配置
    │   └── MybatisPlusConfig.java           # MyBatis-Plus 配置 + @MapperScan
    ├── exception/
    │   └── GlobalExceptionHandler.java      # 全局异常处理器
    ├── result/
    │   ├── CommonResult.java                # 统一响应封装
    │   ├── ErrorCode.java                   # 错误码对象
    │   └── PageParam.java                   # 分页参数基类
    ├── enums/
    │   └── GlobalErrorCodeConstants.java    # 全局错误码常量
    └── utils/
        └── XxxUtils.java                    # 工具类
```

### 2.2 分层职责与调用链

```
请求 → AOP切面(Token校验) → Controller → Service → Mapper → MySQL
                                                        ↓
响应 ← Controller ← Service(封装CommonResult) ← Mapper ← MySQL
```

| 层级 | 职责 | 关键约定 |
|------|------|----------|
| **Controller** | 接收 HTTP 请求，参数校验，调用 Service，返回 `CommonResult` | `@RestController` + `@RequestMapping` + `@Tag` + `@Operation` |
| **Service** | 核心业务逻辑，事务控制，数据组装 | 接口 `IXxxService` 继承 `IService<Entity>`；实现继承 `ServiceImpl<Mapper, Entity>` |
| **DAO** | 数据库操作 | 包名统一用 `dao`（非 `mapper`），继承 `BaseMapper<Entity>`，靠 `@MapperScan` 扫描 |
| **Entity** | 数据库表映射 | `@Data` + `@ToString` + `@TableName` + `@TableId(type=IdType.AUTO)` |
| **DTO** | 接收前端参数，可继承 Entity 追加分页等字段 | `XxxDto extends XxxEntity`，追加 `pageNo`/`pageSize` |
| **VO** | 封装返回前端的视图数据 | 如 `LoginUserVo`（用户 + 菜单 + token） |

### 2.3 Controller 层规范

```java
@RestController
@RequestMapping("/student")
@Tag(name = "学生管理", description = "学生管理相关接口")
public class StudentController {

    @Autowired
    private IStudentService studentService;

    @RequestMapping("/add")
    @Operation(summary = "新增学生", description = "根据传入的学生信息新增一条记录")
    public CommonResult addStudent(@RequestBody StudentEntity student) {
        return studentService.addStudent(student);
    }

    @RequestMapping("/getStudentListByCondition")
    @Operation(summary = "条件分页查询", description = "根据条件分页查询学生列表")
    public CommonResult listStudentByCondition(@RequestBody StudentDto studentDto) {
        return studentService.listStudentByCondition(studentDto);
    }
}
```

**规范要点**：
- 类注解：`@RestController` + `@RequestMapping("/模块名")` + `@Tag`
- 方法注解：`@RequestMapping("/动作")` + `@Operation(summary=, description=)`
- 依赖注入：使用 `@Autowired` 注入 Service 接口
- 返回值：统一返回 `CommonResult<T>`
- 请求映射：统一用 `@RequestMapping`（不区分 GET/POST），路径风格为 `/模块/动作`
- 参数接收：JSON Body 用 `@RequestBody`，路径参数用 `@PathVariable`

### 2.4 Service 层规范

```java
// 接口
public interface IStudentService extends IService<StudentEntity> {
    CommonResult addStudent(StudentEntity student);
    CommonResult listStudentByCondition(StudentDto studentDto);
}

// 实现
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, StudentEntity>
        implements IStudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public CommonResult addStudent(StudentEntity student) {
        // 业务校验
        LambdaQueryWrapper<StudentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentEntity::getStudentNo, student.getStudentNo());
        if (studentMapper.selectCount(wrapper) > 0) {
            return CommonResult.error(GlobalErrorCodeConstants.STUDENT_NO_ERROR);
        }
        // 计算年龄
        student.setStudentAge(DateConvertAgeUtils.getAge(student.getStudentBirth()));
        // 插入
        int result = studentMapper.insert(student);
        return result > 0 ? CommonResult.success() : CommonResult.error(GlobalErrorCodeConstants.ADD_ERROR);
    }
}
```

**规范要点**：
- 接口必须以 `I` 开头，继承 MyBatis-Plus 的 `IService<Entity>`
- 实现类继承 `ServiceImpl<XxxMapper, Entity>`，获得内置 CRUD
- 业务逻辑中充分利用 MyBatis-Plus 的 `LambdaQueryWrapper` 进行条件构造
- 复杂关联查询结果用 FastJSON 的 `JSONObject`/`JSONArray` 组装
- 分页查询使用 MyBatis-Plus 的 `Page<T>` 对象

### 2.5 Entity 层规范

```java
@Data
@ToString
@TableName("student")
public class StudentEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long studentId;

    private String studentName;        // 驼峰自动映射 student_name

    @TableField(value = "student_no")  // 显式映射（可选，驼峰可自动映射时省略）
    private String studentNo;

    private Integer studentAge;
    private String studentBirth;
    private String studentAddress;
    private Long gradeId;
}
```

**规范要点**：
- 注解组合：`@Data` + `@ToString`（Lombok）+ `@TableName("表名")`
- 主键：`@TableId(type = IdType.AUTO)` 表示数据库自增
- 字段映射：依赖驼峰自动映射（`map-underscore-to-camel-case: true`），特殊字段用 `@TableField(value="列名")`
- 逻辑删除字段：`@TableLogic`
- 所有实体 `implements Serializable`
- 主键/外键类型用 `Long`（包装类型），年龄用 `Integer`，日期用 `String`

### 2.6 DAO 层规范

```java
public interface StudentMapper extends BaseMapper<StudentEntity> {
    // 单表 CRUD 由 BaseMapper 提供，无需写任何方法
    // 仅复杂多表关联查询才自定义方法 + XML
}
```

**规范要点**：
- 包名统一为 `dao`（通过 `@MapperScan("com.alex.swjtu.springboot.dao")` 扫描）
- 继承 `BaseMapper<Entity>`，获得全部单表 CRUD
- 仅有多表关联查询时才自定义方法，配合 `resources/mapper/XxxMapper.xml`
- 无需在接口上加 `@Mapper` 注解

### 2.7 DTO / VO 规范

```java
// DTO：继承 Entity，追加查询参数
@Data
@ToString(callSuper = true)
public class StudentDto extends StudentEntity {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}

// VO：封装返回数据
@Data
public class LoginUserVo implements Serializable {
    private UserEntity user;
    private List<MenuEntity> menus;
    private String token;

    public LoginUserVo(UserEntity user, List<MenuEntity> menus, String token) {
        this.user = user;
        this.menus = menus;
        this.token = token;
    }
}
```

---

## 三、前端工程架构标准（Vue3）

### 3.1 目录结构

```
project-name/
├── public/
│   └── favicon.ico
├── src/
│   ├── api/                        # API 接口层
│   │   └── getData.js              # 所有后端请求方法（按业务域命名导出）
│   ├── assets/                     # 静态资源
│   │   ├── base.css                # 基础样式（CSS 变量、reset）
│   │   ├── main.css                # 主样式
│   │   └── logo.svg
│   ├── components/                 # 公共可复用组件
│   │   └── XxxComponent.vue
│   ├── router/                     # 路由配置
│   │   └── index.js               # createRouter + createWebHistory
│   ├── utils/                      # 工具类
│   │   ├── request.js              # axios 封装（实例 + 拦截器）
│   │   └── localStorage.js         # 本地存储工具
│   ├── views/                      # 页面级视图组件
│   │   ├── SwjtuHome.vue           # 登录页
│   │   ├── HomeView.vue            # 主页布局（菜单 + 路由出口）
│   │   ├── MainView.vue            # 首页统计
│   │   └── StudentView.vue         # 学生管理（CRUD 范例）
│   ├── App.vue                     # 根组件（仅含 <RouterView>）
│   └── main.js                     # 入口（挂载 Vue、注册插件、全局注册图标）
├── index.html
├── jsconfig.json                   # 路径别名 @ -> ./src
├── package.json
├── vite.config.js
└── package-lock.json
```

### 3.2 各目录命名规范

| 目录 | 职责 | 命名规范 |
|------|------|----------|
| `api/` | 封装所有后端 HTTP 请求方法 | 文件名按业务域，方法名驼峰动词开头 |
| `utils/` | 通用工具类，与业务无关的纯函数 | 语义化命名 |
| `router/` | 路由配置，入口文件为 `index.js` | 统一 `index.js` |
| `views/` | 页面级路由组件 | 大驼峰 + `View` 后缀 |
| `components/` | 可复用公共组件 | 大驼峰命名 |
| `assets/` | 全局样式、图片等静态资源 | 小写连字符 |

### 3.3 应用入口（main.js）

```javascript
import './assets/base.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(router)
app.use(ElementPlus)

// 全局注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')
```

**规范要点**：
- Element Plus 全量引入（含 CSS）
- Element Plus 图标全局注册，模板中可直接使用图标组件名
- `base.css` 在入口引入
- `App.vue` 仅含 `<RouterView>`，无布局逻辑

### 3.4 API 请求封装（utils/request.js）

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getStorage } from '@/utils/localStorage.js'
import Router from '@/router'

const URL = 'http://localhost:8888'

const service = axios.create({
    baseURL: URL,
    timeout: 1000000,
    crossDomain: true
})

// 请求拦截器：自动注入 Token
service.interceptors.request.use(
    config => {
        const token = getStorage('Token')
        if (token) {
            config.headers['Token'] = token
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器：统一处理业务状态码
service.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code == 200) {
            return res
        } else if (res.code == 401) {
            ElMessage.error('登录已失效，请重新登录')
            Router.push('/')
            return -1
        } else {
            ElMessage.error(res.msg || '请求失败')
            return -1
        }
    },
    error => {
        ElMessage.error('网络请求异常')
        return -1
    }
)

export default service
```

**规范要点**：
- Token 注入到请求头 `Token` 字段（自定义 Header）
- 响应拦截器处理业务状态码：`200` 返回数据，`401` 跳转登录，其他返回 `-1`
- 调用方通过 `res && res != -1` 判断请求是否成功

### 3.5 API 方法定义（api/getData.js）

```javascript
import request from '@/utils/request.js'

// POST + data（提交表单/JSON）
export const loginUser = (data) => {
    return request({ url: `/login`, method: 'post', data })
}

// GET（无参或简单查询）
export const logoutUser = () => {
    return request({ url: `/logout`, method: 'get' })
}

// POST + data（条件分页查询）
export const listStudentByConditionPage = (data) => {
    return request({ url: `/student/getStudentListByCondition`, method: 'post', data })
}

// GET + 路径参数
export const getStudentById = (id) => {
    return request({ url: `/student/getStudentById/${id}`, method: 'get' })
}
```

**方法命名约定**：
- 动词开头：`login` / `logout` / `add` / `update` / `del` / `get` / `list`
- 业务实体跟随：`loginUser` / `addStudent` / `listStudentByConditionPage`

### 3.6 路由配置（router/index.js）

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import SwjtuHomeVue from '@/views/SwjtuHome.vue'
import HomeView from '@/views/HomeView.vue'
import MainView from '@/views/MainView.vue'
import StudentView from '@/views/StudentView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'login',
            component: SwjtuHomeVue,
            meta: { name: '登录页面' }
        },
        {
            path: '/home',
            name: 'home',
            component: HomeView,
            meta: { name: '主页' },
            children: [
                { path: '/main', name: 'main', component: MainView, meta: { name: '首页' } },
                { path: '/student', name: 'student', component: StudentView, meta: { name: '学生管理' } }
            ]
        }
    ]
})

export default router
```

**规范要点**：
- 使用 `createWebHistory`（History 模式）
- 每条路由包含 `path`、`name`、`component`、`meta` 四个字段
- `meta.name` 存储页面中文名称
- 嵌套路由：布局路由 `children` 定义子页面
- 登录页 `/` 为独立顶级路由

### 3.7 状态管理

采用 **localStorage + Vue3 响应式变量** 的轻量方案（暂不使用 Pinia/Vuex）：

```javascript
// utils/localStorage.js
export const setStorage = (name, data) => { localStorage.setItem(name, data) }
export const getStorage = (name) => { return localStorage.getItem(name) }
export const delStorage = (name) => { return localStorage.removeItem(name) }
```

- **持久化存储**：Token、用户信息（含菜单权限）存入 localStorage
- **运行时状态**：各页面用 `ref` / `reactive` 管理局部状态
- **跨页面传递**：登录成功后序列化存入 localStorage，主页面 `onMounted` 读取解析

### 3.8 组件开发规范

所有组件采用 Vue3 `<script setup>` 语法，三段式结构：

```vue
<template>
    <!-- HTML 模板 -->
</template>

<script setup>
    import { ref, reactive, onMounted } from 'vue'
    // 逻辑代码
</script>

<style scoped>
    /* 组件样式，使用 scoped 隔离 */
</style>
```

**响应式数据约定**：
- 简单值/对象：`ref()`，访问需 `.value`
- 复杂对象/集合：`reactive()`
- 表单数据统一用 `ref` 包裹对象

### 3.9 页面组件标准模式（CRUD 范例）

以学生管理页面为标准模板，包含以下完整要素：

| 要素 | 实现方式 |
|------|----------|
| **搜索表单** | `el-form :inline` + `el-input` / `el-select`，含查询/重置按钮 |
| **重置逻辑** | 重新赋值 `searchForm.value` 为初始空对象，再调用查询 |
| **数据表格** | `el-table` + `el-table-column`，操作列用 `#default="scope"` 插槽 |
| **分页** | `el-pagination`，`v-model:current-page` + `v-model:page-size` 双向绑定 |
| **分页参数** | `pageNo`（从1开始）+ `pageSize`，可选 `[10,20,30,50,100]` |
| **弹窗表单** | `el-dialog` + `el-form`，通过主键 ID 是否存在区分新增/编辑 |
| **表单校验** | `rules` 对象 + `ruleFormRef.value.validate()`，trigger 为 `blur` |
| **加载状态** | `ElLoading.service()` 全屏遮罩 |
| **操作反馈** | 成功用 `ElNotification`，确认用 `ElMessageBox.confirm`，即时提示用 `ElMessage` |
| **生命周期** | 数据初始化统一在 `onMounted` 中调用 |

### 3.10 菜单渲染规范

菜单数据来自登录接口返回的 `userInfo.menus` 数组：

```vue
<el-menu :default-active="activeIndex" mode="horizontal" router>
    <el-menu-item v-for="(item, index) in userInfo.menus"
                  :key="index" :index="item.path">
        <el-icon><component :is="item.icon"></component></el-icon>
        {{ item.title }}
    </el-menu-item>
</el-menu>
```

- `el-menu` 开启 `router` 属性，`index` 即路由 path，点击自动跳转
- 菜单图标通过 `<component :is="item.icon">` 动态渲染
- 菜单数据结构：`{ path, title, icon }`

### 3.11 Echarts 图表规范

```javascript
import * as echarts from 'echarts'

const initEcharts = () => {
    getGradeStudentCountData().then(res => {
        if (res && res != -1) {
            option.value.series[0].data = res.data
            echartsForEach.value.forEach(item => {
                const eNode = document.getElementById(`hwadeeEcharts-${item}`)
                const myChart = echarts.init(eNode)
                myChart.setOption(option.value)
            })
        }
    })
}

onMounted(() => { getDataCount(); initEcharts() })
```

### 3.12 Vite 配置

```javascript
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
```

- 路径别名 `@` → `./src`（与 `jsconfig.json` 中 `"@/*": ["./src/*"]` 对应）

---

## 四、MyBatis / MyBatis-Plus 数据层标准

### 4.1 原生 MyBatis 配置标准（mybatis-config.xml）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 1. 外置数据源属性 -->
    <properties resource="jdbc.properties"></properties>

    <!-- 2. 开启驼峰映射 -->
    <settings>
        <setting name="mapUnderscoreToCamelCase" value="true"/>
    </settings>

    <!-- 3. 类型别名 -->
    <typeAliases>
        <typeAlias type="com.alex.swjtu.entity.Student" alias="student"/>
    </typeAliases>

    <!-- 4. JDBC 连接 -->
    <environments default="connect1">
        <environment id="connect1">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${mysql.driver}"/>
                <property name="url" value="${mysql.url}"/>
                <property name="username" value="${mysql.username}"/>
                <property name="password" value="${mysql.password}"/>
            </dataSource>
        </environment>
    </environments>

    <!-- 5. 加载 Mapper XML -->
    <mappers>
        <mapper resource="mapper/StudentMapper.xml"/>
    </mappers>
</configuration>
```

**配置五要素**：properties（属性外置）→ settings（驼峰映射）→ typeAliases（类型别名）→ environments（数据源）→ mappers（映射文件加载）

### 4.2 Mapper 接口规范

```java
// 原生 MyBatis：XxxDao 后缀
public interface StudentDao {
    Student getStudentInfoById(Long studentId);
    List<Student> loadStudentInfoByName(Student student);
    int insertStudent(Student student);

    // 注解方式（简单 SQL 可直接写注解）
    @Select("select * from student where student_id=#{studentId}")
    Student getStudentByIdAnnotation(Long studentId);

    // 多参数必须用 @Param 绑定
    int updateStudentNameAndAddress(
        @Param("stu") Student student,
        @Param("stuAdd") String studentAddress,
        @Param("stuId") Long studentId
    );
}

// MyBatis-Plus：XxxMapper 后缀，继承 BaseMapper
public interface StudentMapper extends BaseMapper<StudentEntity> {
    // 单表 CRUD 由 BaseMapper 提供
    // 仅复杂查询才自定义方法
}
```

### 4.3 Mapper XML 规范

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- namespace = Mapper 接口全限定名 -->
<mapper namespace="com.alex.swjtu.dao.StudentDao">

    <!-- resultMap：多表关联映射 -->
    <resultMap id="studentGradeVo" type="com.alex.swjtu.vo.StudentGradeVo">
        <association property="student" javaType="com.alex.swjtu.entity.Student">
            <id property="studentId" column="student_id"/>
            <result property="studentName" column="student_name"/>
        </association>
        <association property="grade" javaType="com.alex.swjtu.entity.Grade">
            <id property="gradeId" column="grade_id"/>
            <result property="gradeName" column="grade_name"/>
        </association>
    </resultMap>

    <!-- 简单查询：resultType 可用类型别名 -->
    <select id="getStudentInfoById" resultType="student">
        select * from student where student_id=#{studentId}
    </select>

    <!-- LIKE 查询：用 concat 防注入 -->
    <select id="loadStudentInfoByName" resultType="student">
        select * from student where student_name like concat('%', #{studentName}, '%')
    </select>
</mapper>
```

**规范要点**：
- `namespace` 必须等于 Mapper 接口全限定名
- `id` 与接口方法名一一对应
- 参数引用统一用 `#{}`（预编译防注入），**禁止** `${}`
- LIKE 查询用 `concat('%', #{xxx}, '%')`
- `resultType` 可用类型别名或全限定名

### 4.4 动态 SQL 规范

**条件查询（`<where>` + `<if>`）**：

```xml
<select id="getStudentByCondition" resultMap="studentGradeVo">
    select * from student
    <where>
        <if test="studentName != null and studentName != ''">
            and student_name like concat('%', #{studentName}, '%')
        </if>
        <if test="studentNo != null and studentNo != ''">
            and student_no like concat('%', #{studentNo}, '%')
        </if>
    </where>
</select>
```

**条件更新（`<set>` + `<if>`）**：

```xml
<update id="updateStudentSelective">
    update student
    <set>
        <if test="studentName != null and studentName != ''">
            student_name = #{studentName},
        </if>
        <if test="studentAddress != null and studentAddress != ''">
            student_address = #{studentAddress},
        </if>
    </set>
    where student_id = #{studentId}
</update>
```

**批量操作（`<foreach>`）**：

```xml
<!-- 数组：collection="array" -->
<select id="getByIdsArray" resultType="student">
    select * from student where student_id in
    <foreach item="item" collection="array" open="(" separator="," close=")">
        #{item}
    </foreach>
</select>

<!-- List：collection="list" -->
<select id="getByIdsList" resultType="student">
    select * from student where student_id in
    <foreach item="item" collection="list" open="(" separator="," close=")">
        #{item}
    </foreach>
</select>
```

**判空规则**：
- 字符串：`!= null and != ''`
- 数值：`!= null and != 0`
- 也支持 OGNL 写法：`neq null and neq ''`

### 4.5 多表关联映射规范

**一对一（`<association>`）**：

```xml
<resultMap id="studentGradeVo" type="com.alex.swjtu.vo.StudentGradeVo">
    <association property="student" javaType="com.alex.swjtu.entity.Student">
        <id property="studentId" column="student_id"/>
        <result property="studentName" column="student_name"/>
    </association>
    <association property="grade" javaType="com.alex.swjtu.entity.Grade">
        <id property="gradeId" column="grade_id"/>
        <result property="gradeName" column="grade_name"/>
    </association>
</resultMap>
```

**一对多（`<association>` + `<collection>`）**：

```xml
<resultMap id="gradeStudentVo" type="com.alex.swjtu.vo.GradeStudentVo">
    <association property="grade" javaType="com.alex.swjtu.entity.Grade">
        <id property="gradeId" column="grade_id"/>
        <result property="gradeName" column="grade_name"/>
    </association>
    <collection property="students" ofType="com.alex.swjtu.entity.Student">
        <id property="studentId" column="student_id"/>
        <result property="studentName" column="student_name"/>
    </collection>
</resultMap>
```

- `<id>` 标注主键列（影响嵌套映射去重）
- `<result>` 标注普通列
- `javaType` 用于 association，`ofType` 用于 collection 元素类型

### 4.6 MyBatis-Plus 条件构造器规范

```java
// LambdaQueryWrapper（推荐，类型安全）
LambdaQueryWrapper<StudentEntity> wrapper = new LambdaQueryWrapper<>();
wrapper.like(StringUtils.isNotBlank(name), StudentEntity::getStudentName, name)
       .eq(gradeId != null, StudentEntity::getGradeId, gradeId)
       .orderByAsc(StudentEntity::getStudentAge);
studentMapper.selectList(wrapper);

// 分页查询
Page<StudentEntity> page = new Page<>(pageNo, pageSize);
studentMapper.selectPage(page, wrapper);
page.getRecords();  // 当前页数据
page.getTotal();    // 总条数
```

**规范要点**：
- 条件构造方法第一个参数为 `boolean`（条件是否生效），实现动态条件
- 优先用 Lambda 版本（`StudentEntity::getStudentName`）避免硬编码列名
- 判空用 `StringUtils.isNotBlank`（来自 MyBatis-Plus 工具包）

### 4.7 MyBatis-Plus 分页插件配置

```java
@Configuration
@MapperScan("com.alex.swjtu.springboot.dao")
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件，多插件时切记分页最后添加
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### 4.8 数据库连接配置

**原生 MyBatis（jdbc.properties）**：

```properties
mysql.driver=com.mysql.jdbc.Driver
mysql.url=jdbc:mysql://localhost:3306/school_spring?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
mysql.username=root
mysql.password=root
```

**Spring Boot / MyBatis-Plus（application.yml）**：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver    # MySQL 8.x 驱动
    url: jdbc:mysql://localhost:3306/school_spring?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
    username: root
    password: root
```

---

## 五、数据库设计标准

### 5.1 基本规范

| 维度 | 约定 |
|------|------|
| **数据库** | MySQL 8.x，默认库名 `school_spring` |
| **字符集** | UTF-8（`utf8` 或 `utf8mb4`） |
| **主键** | 自增（`IdType.AUTO`），类型 `BIGINT` |
| **表命名** | 下划线命名法（snake_case），如 `student`、`grade`、`menu` |
| **字段命名** | 下划线命名法，如 `student_name`、`student_no` |
| **Java映射** | 驼峰命名法（camelCase），靠 `map-underscore-to-camel-case: true` 自动映射 |
| **逻辑删除** | 字段 `is_delete`，配合 `@TableLogic` 注解 |
| **菜单权限** | `user_type` 字段存储角色类型，用 `FIND_IN_SET` 查询 |

### 5.2 SQL 日志

- 开发环境：控制台输出 SQL（`log-impl: StdOutImpl`）
- 生产环境：关闭 SQL 控制台输出，使用 Logback 文件日志

---

## 六、API 接口设计标准

### 6.1 URL 路径约定

```
/模块名/动作名

示例：
/login                          # 登录
/logout                         # 退出
/student/add                    # 新增学生
/student/update                 # 修改学生
/student/delete                 # 删除学生
/student/getStudentListByCondition  # 条件分页查询
/student/getStudentById/{id}    # 按ID查询
/grade/all                      # 年级下拉列表
/data/getMainPageCountData      # 首页统计
/data/getGradeStudentCountData  # 图表数据
```

### 6.2 请求方式约定

| 场景 | Method | 参数传递 |
|------|--------|----------|
| 登录/提交表单 | POST | `@RequestBody` JSON |
| 条件分页查询 | POST | `@RequestBody` JSON（含 pageNo/pageSize） |
| 简单查询/下拉列表 | GET | 无参或路径参数 |
| 按ID查询 | GET | `@PathVariable` 路径参数 |
| 新增/修改/删除 | POST | `@RequestBody` JSON |

> **注意**：本项目统一用 `@RequestMapping`（不区分 GET/POST），但前端调用时需明确指定 `method`。

### 6.3 分页参数约定

**请求参数**：

```json
{
    "studentName": "张",
    "gradeId": 1,
    "pageNo": 1,
    "pageSize": 10
}
```

**响应结构**：

```json
{
    "code": 200,
    "data": {
        "records": [...],
        "total": 100
    },
    "total": 100,
    "msg": ""
}
```

---

## 七、认证与权限机制

### 7.1 Token 认证流程

```
登录请求 → UserService 校验账号密码 → 生成 UUID Token
                                      ↓
                        Redis 存储：Key = "Token::{uuid}"
                        Value = 用户 JSON，有效期 600 分钟
                                      ↓
                        返回 LoginUserVo（含 user + menus + token）
                                      ↓
前端存储 Token → 后续请求 Header 携带 "Token: {uuid}"
                                      ↓
后端 AOP 切面 → 检查 @NoToken 注解 → 是则放行
             → 否则从 Header 取 Token → Redis 验证 → 有效则放行，无效返回 401
```

### 7.2 AOP 切面实现

```java
@Component
@Aspect
public class AroundCut {
    @Autowired
    StringRedisTemplate redisTemplate;

    // 切入点：controller 包下所有方法
    public static final String POINT_CUT =
        "execution(* com.alex.swjtu.springboot.controller.*.*(..))";

    @Around(AroundCut.POINT_CUT)
    public CommonResult checkToken(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        // 1. 检查 @NoToken 注解，有则放行
        if (method.isAnnotationPresent(NoToken.class)) {
            return (CommonResult) pjp.proceed();
        }

        // 2. 从请求头获取 Token
        HttpServletRequest request = ((ServletRequestAttributes)
            RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Token");

        // 3. Token 为空 → 401
        if (StringUtils.isBlank(token)) {
            return CommonResult.error(GlobalErrorCodeConstants.UNAUTHORIZED);
        }

        // 4. Redis 验证 Token
        String userInfoString = redisTemplate.opsForValue().get("Token::" + token);
        if (StringUtils.isBlank(userInfoString)) {
            return CommonResult.error(GlobalErrorCodeConstants.UNAUTHORIZED);
        }

        // 5. 放行
        return (CommonResult) pjp.proceed();
    }
}
```

### 7.3 免 Token 注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoToken {
}
```

- 用于标记不需要 Token 校验的接口（如 `/login`）
- 标注在 Controller 方法上

### 7.4 Token 存储

| 维度 | 约定 |
|------|------|
| **Key 格式** | `Token::{uuid}` |
| **Value** | 用户信息 JSON 字符串 |
| **有效期** | 600 分钟 |
| **存储介质** | Redis（database: 1） |
| **传递方式** | HTTP Header `Token` 字段 |
| **退出登录** | 从 Redis 删除对应 Key |

---

## 八、统一返回结果与异常处理

### 8.1 CommonResult 统一响应封装

```java
@Data
public class CommonResult<T> implements Serializable {
    private Integer code;      // 错误码
    private T data;            // 返回数据
    private Long total;        // 返回总数（分页场景）
    private String msg;        // 提示信息

    // 静态工厂方法
    public static <T> CommonResult<T> success(T data)              // 成功（带数据）
    public static <T> CommonResult<T> success()                    // 成功（无数据）
    public static <T> CommonResult<List<T>> successPageData(IPage<T> page)  // 分页成功
    public static <T> CommonResult<T> error(Integer code, String message)   // 错误（码+消息）
    public static <T> CommonResult<T> error(ErrorCode errorCode)             // 错误（ErrorCode）
    public static <T> CommonResult<T> error(CommonResult<?> result)         // 错误转换

    @JsonIgnore
    public boolean isSuccess()   // 成功判断（不序列化）
    @JsonIgnore
    public boolean isError()     // 错误判断（不序列化）
}
```

**设计要点**：
- 泛型设计 `CommonResult<T>`，支持任意数据类型
- 静态工厂方法模式，禁止直接 new
- `success` 时 code=200, msg=""
- `error` 方法有断言校验：code 不能是 200
- 分页场景额外返回 `total` 字段
- `isSuccess()`/`isError()` 标记 `@JsonIgnore` 避免序列化

### 8.2 响应 JSON 格式

```json
{
    "code": 200,
    "data": { ... },
    "total": null,
    "msg": ""
}
```

### 8.3 错误码体系

| 错误码 | 常量名 | 说明 |
|--------|--------|------|
| 200 | SUCCESS | 成功 |
| 400 | BAD_REQUEST | 请求参数不正确 |
| 401 | UNAUTHORIZED | 账号未登录 |
| 402 | LOGIN_ERROR | 账号或密码错误 |
| 403 | FORBIDDEN | 没有该操作权限 |
| 404 | NOT_FOUND | 请求未找到 |
| 405 | METHOD_NOT_ALLOWED | 请求方法不正确 |
| 423 | LOCKED | 并发请求不允许 |
| 429 | TOO_MANY_REQUESTS | 请求过于频繁 |
| 500 | INTERNAL_SERVER_ERROR | 操作有误 |
| 501 | NOT_IMPLEMENTED | 功能未实现 |
| 502 | ERROR_CONFIGURATION | 错误的配置项 |
| 500 | UPDATE_ERROR | 更新失败 |
| 500 | ADD_ERROR | 新增失败 |
| 500 | DELETE_ERROR | 删除失败 |
| 900 | REPEATED_REQUESTS | 重复请求 |
| 901 | DEMO_DENY | 演示模式禁止写操作 |
| 902 | STUDENT_NO_ERROR | 学号重复 |
| 999 | UNKNOWN | 未知错误 |

### 8.4 全局异常处理器

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResult exceptionHandler(Exception e) {
        System.err.println("捕获到异常");
        e.printStackTrace();
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
    }
}
```

- 使用 `@RestControllerAdvice` 全局捕获
- 捕获所有 `Exception`，统一返回 500 错误码
- 控制台打印异常堆栈

### 8.5 分页参数基类

```java
@Data
public class PageParam implements Serializable {
    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;
    public static final Integer PAGE_SIZE_NONE = -1;  // 不分页

    private Integer pageNo = PAGE_NO;
    private Integer pageSize = PAGE_SIZE;
}
```

---

## 九、配置文件标准

### 9.1 后端 application.yml

```yaml
server:
  port: 8888

logging:
  level:
    com.alex.swjtu.springboot: DEBUG
  config: classpath:logback-spring.xml

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/school_spring?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
    username: root
    password: root
  data:
    redis:
      host: localhost
      port: 6379
      password: 123456
      database: 1
      timeout: 1800000
      lettuce:
        pool:
          max-active: 20
          max-wait: -1
          max-idle: 5
          min-idle: 1

springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true

mybatis-plus:
  mapper-locations: classpath:/mapper/*.xml
  type-aliases-package: com.alex.swjtu.springboot.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
```

### 9.2 后端 logback-spring.xml

```xml
<configuration>
    <!-- 控制台日志 -->
    <appender name="consoleLog" class="ch.qos.logback.core.ConsoleAppender">
        <layout><pattern>[%p]%d - %msg%n</pattern></layout>
    </appender>

    <!-- 文件日志（按天滚动，过滤 debug 级别） -->
    <appender name="fileLog" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>debug</level>
            <onMatch>DENY</onMatch>
        </filter>
        <encoder><pattern>[%p]%d - %msg%n</pattern></encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/%d.log</fileNamePattern>
        </rollingPolicy>
    </appender>

    <root level="debug">
        <appender-ref ref="consoleLog"/>
        <appender-ref ref="fileLog"/>
    </root>
</configuration>
```

### 9.3 前端 package.json

```json
{
  "name": "swjtu-vue",
  "version": "0.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.5.38",
    "vue-router": "^4.6.3",
    "element-plus": "^2.14.3",
    "@element-plus/icons-vue": "^2.3.1",
    "axios": "^1.18.1",
    "echarts": "^6.0.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^6.0.7",
    "vite": "^8.0.16",
    "vite-plugin-vue-devtools": "^8.1.2"
  },
  "engines": {
    "node": "^22.18.0 || >=24.12.0"
  }
}
```

### 9.4 前端 jsconfig.json

```json
{
  "compilerOptions": {
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

---

## 十、命名规范总表

### 10.1 后端命名规范

| 类型 | 命名模式 | 示例 |
|------|----------|------|
| Controller | `XxxController` | `StudentController` |
| Service 接口 | `IXxxService` | `IStudentService` |
| Service 实现 | `XxxServiceImpl` | `StudentServiceImpl` |
| Mapper | `XxxMapper` | `StudentMapper` |
| Entity | `XxxEntity` | `StudentEntity` |
| DTO | `XxxDto` | `StudentDto` |
| VO | `XxxVo` | `LoginUserVo` |
| 配置类 | `XxxConfig` | `WebConfig` |
| 异常处理 | `GlobalExceptionHandler` | — |
| 切面 | `XxxCut` | `AroundCut` |
| 注解 | 语义化命名 | `NoToken` |
| 工具类 | `XxxUtils` | `DateConvertAgeUtils` |
| 错误码常量 | `GlobalErrorCodeConstants` | — |

### 10.2 前端命名规范

| 类型 | 命名模式 | 示例 |
|------|----------|------|
| 页面组件 | 大驼峰 + `View` 后缀 | `StudentView.vue` |
| 公共组件 | 大驼峰 | `SwUser.vue` |
| API 方法 | 驼峰动词开头 | `listStudentByConditionPage` |
| 工具文件 | 语义化小写 | `request.js` |
| 路由 name | 小写 | `student` |
| 路由 path | 小写斜杠 | `/student` |
| CSS 文件 | 小写连字符 | `base.css` |

### 10.3 数据库命名规范

| 类型 | 命名模式 | 示例 |
|------|----------|------|
| 表名 | 下划线小写 | `student`、`grade` |
| 字段名 | 下划线小写 | `student_name`、`student_no` |
| 主键 | `{表名}_id` | `student_id` |
| 外键 | `{关联表名}_id` | `grade_id` |

---

## 十一、编码约定

### 11.1 通用约定

- **Java 版本**：17，全项目统一
- **编码**：UTF-8
- **序列化**：所有实体/VO/DTO `implements Serializable`
- **参数绑定**：一律 `#{}`（预编译防注入），**禁止** `${}`
- **主键类型**：用 `Long`（包装类型），避免 0 值歧义
- **返回值**：增删改方法返回 `int`（受影响行数），业务层判断 `> 0` 判定成功

### 11.2 后端编码约定

- **注解组合**：Entity 用 `@Data` + `@ToString` + `@TableName` + `@TableId`
- **Controller**：`@RestController` + `@RequestMapping` + `@Tag` + `@Operation`
- **Service**：`@Service`，实现类继承 `ServiceImpl<Mapper, Entity>`
- **Mapper**：继承 `BaseMapper<Entity>`，无需 `@Mapper` 注解
- **配置类**：`@Configuration`
- **AOP**：`@Component` + `@Aspect`
- **Swagger**：使用 SpringDoc OpenAPI 3（非旧版 `@Api`/`@ApiOperation`）
- **JSON 处理**：Service 层可用 FastJSON 的 `JSONObject`/`JSONArray` 组装复杂数据

### 11.3 前端编码约定

- **语法风格**：Vue3 `<script setup>` Composition API，不使用 Options API
- **路径引用**：统一使用 `@/` 别名
- **API 调用**：通过 `@/api/getData` 统一导出
- **错误处理**：调用方判断 `res && res != -1`
- **状态共享**：localStorage 存储 Token + userInfo
- **表单校验**：Element Plus `rules` + `ruleFormRef.validate()`，trigger 为 `blur`
- **操作反馈**：成功用 `ElNotification`，确认用 `ElMessageBox.confirm`，即时提示用 `ElMessage`
- **组件样式**：使用 `<style scoped>` 隔离
- **生命周期**：数据初始化统一在 `onMounted` 中调用
- **新增/编辑复用**：同一 `el-dialog`，通过主键 ID 是否存在区分操作类型

### 11.4 跨域处理约定

- 后端通过 `WebConfig` 配置全局 `CorsFilter`
- 允许所有源（`*`）、所有请求头、所有方法
- 允许携带凭证（`setAllowCredentials(true)`）
- 过滤器优先级：99999（最低优先级）

---

## 十二、前后端联调约定

### 12.1 端口约定

| 服务 | 端口 |
|------|------|
| 后端 Spring Boot | 8888 |
| 前端 Vite Dev Server | 5173（默认） |
| MySQL | 3306 |
| Redis | 6379 |

### 12.2 认证联调

| 步骤 | 前端 | 后端 |
|------|------|------|
| 登录 | POST `/login`，Body 含账号密码 | 校验通过后生成 UUID Token，存入 Redis，返回 `LoginUserVo` |
| 存储 | `setStorage("Token", token)` + `setStorage("userInfo", JSON)` | — |
| 请求 | 拦截器自动注入 `Header: Token` | AOP 切面从 Header 取 Token，Redis 验证 |
| 失效 | 响应 401 → 跳转登录页 | Token 不存在或过期返回 401 |
| 退出 | GET `/logout` | 从 Redis 删除 Token |

### 12.3 分页联调

| 维度 | 前端参数 | 后端处理 |
|------|----------|----------|
| 请求 | `pageNo`（从1开始）+ `pageSize` | DTO 继承 Entity 追加分页字段 |
| 响应 | 读取 `data.records`（列表）+ `total`（总数） | `CommonResult.successPageData(IPage)` 或手动组装 |

---

## 十三、日志规范

### 13.1 后端日志

| 环境 | 日志级别 | 输出方式 |
|------|----------|----------|
| 开发 | DEBUG | 控制台（含 SQL） + 文件 |
| 生产 | INFO | 文件（按天滚动） |

- 配置文件：`logback-spring.xml`
- 日志格式：`[%p]%d - %msg%n`
- 文件路径：`logs/%d.log`（按天滚动）
- SQL 日志：开发环境用 `StdOutImpl` 输出到控制台

### 13.2 前端日志

- 开发环境：控制台输出（Vite 默认）
- 生产环境：构建时自动移除 console（如需）

---

## 十四、依赖版本锁定清单

### 14.1 后端 pom.xml 关键依赖

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.13</version>
</parent>

<properties>
    <java.version>17</java.version>
</properties>

<!-- 核心依赖（版本由 parent 管理，仅特殊依赖显式指定版本） -->
<dependencies>
    <!-- Web -->
    <dependency><artifactId>spring-boot-starter-web</artifactId></dependency>
    <dependency><artifactId>spring-boot-starter-data-jdbc</artifactId></dependency>

    <!-- 数据库 -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- MyBatis-Plus (Spring Boot 3 适配) -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>3.5.12</version>
    </dependency>
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-jsqlparser</artifactId>
        <version>3.5.12</version>
    </dependency>

    <!-- Redis -->
    <dependency><artifactId>spring-boot-starter-data-redis</artifactId></dependency>

    <!-- AOP -->
    <dependency><artifactId>spring-boot-starter-aop</artifactId></dependency>

    <!-- Swagger / OpenAPI -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.8.4</version>
    </dependency>

    <!-- JSON -->
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>fastjson</artifactId>
        <version>1.2.73</version>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- DevTools -->
    <dependency>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- Test -->
    <dependency>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- ========== SpringAI（AI 功能模块，按需引入） ========== -->
    <dependency>
        <groupId>com.alibaba.cloud.ai</groupId>
        <artifactId>spring-ai-alibaba-starter-dashscope</artifactId>
        <version>1.1.2.0</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud.ai</groupId>
        <artifactId>spring-ai-alibaba-agent-framework</artifactId>
        <version>1.1.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-openai</artifactId>
        <version>1.1.2</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>dashscope-sdk-java</artifactId>
        <version>2.16.7</version>
    </dependency>
</dependencies>
```

**构建插件**：
- `spring-boot-maven-plugin`：排除 Lombok 打包
- `maven-compiler-plugin`：配置 Lombok 注解处理器（compile + testCompile 两个 execution）

### 14.2 前端 package.json 关键依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| `vue` | ^3.5.38 | 核心框架 |
| `vue-router` | ^4.6.3 | 路由 |
| `element-plus` | ^2.14.3 | UI 组件库 |
| `@element-plus/icons-vue` | ^2.3.1 | 图标库 |
| `axios` | ^1.18.1 | HTTP 请求 |
| `echarts` | ^6.0.0 | 数据可视化 |
| `vite` | ^8.0.16 | 构建工具 |
| `@vitejs/plugin-vue` | ^6.0.7 | Vue 插件 |
| `vite-plugin-vue-devtools` | ^8.1.2 | DevTools |

> **SpringAI 前端说明**：AI 流式对话使用浏览器原生 Fetch + ReadableStream，无额外 npm 依赖。如需完整 Markdown 渲染，按需引入 `markdown-it` 或 `marked`。

---

## 十五、工程目录速查模板

### 15.1 后端新建工程模板

```
springboot-{项目名}/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/alex/swjtu/springboot/
│   │   │   ├── {项目名}Application.java
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   │   └── impl/
│   │   │   ├── dao/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── vo/
│   │   │   ├── aop/
│   │   │   │   ├── AroundCut.java
│   │   │   │   └── NoToken.java
│   │   │   └── common/
│   │   │       ├── config/
│   │   │       ├── exception/
│   │   │       ├── result/
│   │   │       ├── enums/
│   │   │       └── utils/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── logback-spring.xml
│   │       └── mapper/
│   └── test/
└── .gitignore
```

### 15.2 前端新建工程模板

```
{项目名}-vue/
├── public/
│   └── favicon.ico
├── src/
│   ├── api/
│   │   └── getData.js
│   ├── assets/
│   │   ├── base.css
│   │   └── main.css
│   ├── components/
│   ├── router/
│   │   └── index.js
│   ├── utils/
│   │   ├── request.js
│   │   ├── streamChatAPI.js
│   │   └── localStorage.js
│   ├── views/
│   │   ├── SwjtuHome.vue
│   │   ├── HomeView.vue
│   │   ├── MainView.vue
│   │   └── AiView.vue
│   ├── App.vue
│   └── main.js
├── index.html
├── jsconfig.json
├── package.json
├── vite.config.js
└── .gitignore
```

### 15.3 新增功能模块 Checklist

新增一个业务模块（如"课程管理"）时，按以下顺序创建文件：

**后端**：
1. `entity/CourseEntity.java` — 实体类（`@Data` + `@TableName`）
2. `dao/CourseMapper.java` — Mapper 接口（继承 `BaseMapper`）
3. `service/ICourseService.java` — Service 接口（继承 `IService`）
4. `service/impl/CourseServiceImpl.java` — Service 实现（继承 `ServiceImpl`）
5. `controller/CourseController.java` — Controller（`@RestController` + `@Tag`）
6. `dto/CourseDto.java` — DTO（如需分页查询，继承 Entity 追加 pageNo/pageSize）
7. `resources/mapper/CourseMapper.xml` — Mapper XML（仅复杂查询需要）
8. `common/enums/GlobalErrorCodeConstants.java` — 追加业务错误码（如 `COURSE_NO_ERROR`）

**前端**：
1. `api/getData.js` — 追加课程相关接口方法
2. `views/CourseView.vue` — 课程管理页面（参考 StudentView 模板）
3. `router/index.js` — 追加路由配置（`children` 中添加）
4. 后端菜单表追加课程管理菜单记录（含 path/title/icon）

---

## 附录：Swagger/OpenAPI 文档访问

| 项目 | URL |
|------|-----|
| API 文档 JSON | `http://localhost:8888/v3/api-docs` |
| Swagger UI | `http://localhost:8888/swagger-ui/index.html` |

> 使用 SpringDoc OpenAPI 3（非旧版 Swagger2），注解用 `@Tag` / `@Operation`（非 `@Api` / `@ApiOperation`）。

---

## 十六、SpringAI 集成架构标准

> 本节基于 `springboot-swjtu+SpringAI`（后端）与 `swjtu-vue+SpringAI集成`（前端）两个项目提炼，作为后续所有 AI 功能开发的标准。

### 16.1 SpringAI 集成总览

SpringAI 在现有 Spring Boot 业务系统上增量集成，不改动原有业务代码，仅新增 AI 相关的 Controller、Service 和配置：

```
┌──────────────────────────────────────────────────────────┐
│                    前端 (Vue3)                             │
│  AiView.vue（文本生成 + 流式对话 + 图文生成）               │
│  ├─ axios 通道：文本/图片/历史（走 request.js 拦截器）      │
│  └─ Fetch 通道：流式 SSE（走 streamChatAPI.js）             │
└────────────────────────┬─────────────────────────────────┘
                         │ POST + JSON Body
┌────────────────────────┴─────────────────────────────────┐
│                 后端 (Spring Boot 3 + SpringAI)           │
│  AIController                                            │
│  ├─ LanguageModelService  → DashScopeChatModel（同步）     │
│  ├─ ChatModelService      → DashScopeChatModel（流式Flux） │
│  │    └─ ConcurrentHashMap<sessionId, List<Message>>      │
│  └─ ImageGenerationService → DashScope 原生 SDK（文生图）  │
└────────────────────────┬─────────────────────────────────┘
                         │
              ┌──────────┴──────────┐
              │  通义 DashScope API  │
              │  DeepSeek API (预留) │
              └─────────────────────┘
```

**与业务系统的关系**：
- AI 模块与业务模块（Student/User/Grade 等）**完全独立**，互不影响
- AI Service 不继承 `IService`/`ServiceImpl`，直接 `@Service` 注解标注
- AI 请求 DTO 为 Controller 内部静态类，未放入 `dto` 包
- AOP Token 切面、全局异常处理、CommonResult 统一返回等公共机制**全部复用**

### 16.2 SpringAI 后端依赖（pom.xml 新增）

在原有业务 pom.xml 基础上，新增以下 5 个依赖：

```xml
<!-- Spring AI Alibaba Agent 框架 -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-agent-framework</artifactId>
    <version>1.1.2.0</version>
</dependency>

<!-- DashScope ChatModel 自动装配（核心） -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter-dashscope</artifactId>
    <version>1.1.2.0</version>
</dependency>

<!-- OpenAI 兼容模型支持（指向 DeepSeek） -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
    <version>1.1.2</version>
</dependency>

<!-- WebFlux：流式对话 SSE/Flux 必需 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>

<!-- DashScope 原生 SDK（文生图） -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dashscope-sdk-java</artifactId>
    <version>2.16.7</version>
</dependency>
```

**版本对齐规则**：Spring AI 主版本 `1.1.2` 与 Alibaba 版本 `1.1.2.0` 必须对齐，不可混用不同主版本。

### 16.3 SpringAI 配置（application.yml 新增）

在原有 `spring:` 节点下新增 `ai` 配置块：

```yaml
spring:
  ai:
    dashscope:
      api-key: ${DASHSCOPE_API_KEY}        # 通义 DashScope，生产环境用环境变量
    openai:
      api-key: ${DEEPSEEK_API_KEY}         # DeepSeek（OpenAI 兼容）
      base-url: https://api.deepseek.com
      chat:
        options:
          model: deepseek-v4-flash
```

**安全要求**：
- API Key **禁止明文提交到 Git**，必须使用环境变量或配置中心注入
- 本地开发可在 `application-local.yml`（不提交 Git）中覆盖
- 生产环境通过环境变量 `DASHSCOPE_API_KEY` / `DEEPSEEK_API_KEY` 注入

### 16.4 后端 AI 架构分层

```
com.alex.swjtu.springboot
├── controller/
│   └── AIController.java                 # AI 接口入口（@RestController + @Tag）
├── service/                              # AI Service（不继承 IService 体系）
│   ├── LanguageModelService.java         # 同步文本生成
│   ├── ChatModelService.java             # 流式对话 + 上下文管理
│   └── ImageGenerationService.java       # 文生图（DashScope 原生 SDK）
└── ...（原有业务代码不变）
```

**AI Service 与业务 Service 的区别**：

| 维度 | 业务 Service | AI Service |
|------|-------------|-----------|
| 继承 | `IService<Entity>` + `ServiceImpl<Mapper, Entity>` | 无继承，直接 `@Service` |
| 注入 | `@Autowired XxxMapper` | `@Autowired DashScopeChatModel` |
| 返回 | `CommonResult<T>` | 同步返回 `CommonResult<T>`，流式返回 `Flux<String>` |
| DTO 位置 | `dto/XxxDto.java` 独立文件 | Controller 内部静态类 |
| 数据库 | 通过 Mapper 操作 MySQL | 不操作数据库（上下文存内存） |

### 16.5 AIController 接口规范

```java
@RestController
@Tag(name = "AI")
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private LanguageModelService languageModelService;
    @Autowired
    private ChatModelService chatModelService;
    @Autowired
    private ImageGenerationService imageGenerationService;

    // ========== 文本生成（同步）==========
    @RequestMapping("/text/generate")
    @Operation(summary = "简单文本生成")
    public CommonResult generate(@RequestBody TextRequest request) { ... }

    @RequestMapping("/text/generate-template")
    @Operation(summary = "模板文本生成")
    public CommonResult generateTemplate(@RequestBody TemplateRequest request) { ... }

    @RequestMapping("/text/generate-chat")
    @Operation(summary = "ChatClient文本生成")
    public CommonResult generateChat(@RequestBody TextRequest request) { ... }

    // ========== 流式对话（SSE）==========
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式对话")
    public Flux<String> streamChat(@RequestBody ChatRequest request) { ... }

    @PostMapping(value = "/chat/stream-with-system", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "带系统提示词流式对话")
    public Flux<String> streamChatWithSystem(@RequestBody ChatWithSystemRequest request) { ... }

    // ========== 对话历史管理 ==========
    @DeleteMapping("/chat/history/{sessionId}")
    @Operation(summary = "清除会话历史")
    public CommonResult<Void> clearHistory(@PathVariable String sessionId) { ... }

    @GetMapping("/chat/history/{sessionId}")
    @Operation(summary = "查询会话历史")
    public CommonResult<List<Map<String, String>>> getHistory(@PathVariable String sessionId) { ... }

    // ========== 图像生成 ==========
    @PostMapping("/image/generate")
    @Operation(summary = "单图生成")
    public CommonResult<String> generateImage(@RequestBody ImageRequest request) { ... }

    @PostMapping("/image/generate-multiple")
    @Operation(summary = "多图生成")
    public CommonResult<List<String>> generateMultipleImages(@RequestBody ImageRequest request) { ... }

    // ========== 请求 DTO（内部静态类）==========
    @Data
    static class TextRequest { private String prompt; }

    @Data
    static class TemplateRequest { private String topic; private String style; }

    @Data
    static class ChatRequest { private String sessionId; private String message
    ; }

    @Data
    static class ChatWithSystemRequest { private String sessionId; private String systemPrompt; private String message; }

    @Data
    static class ImageRequest { private String prompt; private String size; private Integer number; }
}
```

### 16.6 AI 接口清单

| 方法 | 路径 | 入参 | 返回类型 | 说明 |
|------|------|------|---------|------|
| POST | `/ai/text/generate` | `{prompt}` | `CommonResult<String>` | 简单文本生成 |
| POST | `/ai/text/generate-template` | `{topic, style}` | `CommonResult<String>` | PromptTemplate 模板生成 |
| POST | `/ai/text/generate-chat` | `{prompt}` | `CommonResult<String>` | ChatClient 生成 |
| POST | `/ai/chat/stream` | `{sessionId, message}` | `Flux<String>` (SSE) | 流式单轮对话 |
| POST | `/ai/chat/stream-with-system` | `{sessionId, systemPrompt, message}` | `Flux<String>` (SSE) | 带系统提示词流式对话 |
| GET | `/ai/chat/history/{sessionId}` | 路径参数 | `CommonResult<List<{role,content}>>` | 查询对话历史 |
| DELETE | `/ai/chat/history/{sessionId}` | 路径参数 | `CommonResult<Void>` | 清除对话历史 |
| POST | `/ai/image/generate` | `{prompt, size, number}` | `CommonResult<String>` | 单图生成（url/base64） |
| POST | `/ai/image/generate-multiple` | `{prompt, size, number}` | `CommonResult<List<String>>` | 多图生成 |

### 16.7 Service 层实现规范

#### 16.7.1 LanguageModelService（同步文本生成）

注入 `DashScopeChatModel`，提供三种调用方式：

```java
@Service
public class LanguageModelService {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    // 方式一：最简调用
    public String generateText(String prompt) {
        return dashScopeChatModel.call(prompt);
    }

    // 方式二：PromptTemplate 模板（Java 文本块内联）
    public String generateTemplate(String topic, String style) {
        String templateText = """
            请为以下主题写一篇{style}风格的文章。
            主题：{topic}
            要求：内容有深度、结构清晰，字数200-400字。
            """;
        PromptTemplate template = new PromptTemplate(templateText);
        Prompt prompt = template.create(Map.of("topic", topic, "style", style));
        return dashScopeChatModel.call(prompt).getResult().getOutput().getText();
    }

    // 方式三：ChatClient 链式调用
    public String generateChatClient(String prompt) {
        ChatClient chatClient = ChatClient.create(dashScopeChatModel);
        return chatClient.prompt().user(prompt).call().content();
    }
}
```

#### 16.7.2 ChatModelService（流式对话 + 上下文管理）

```java
@Service
public class ChatModelService {

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    // 内存级会话历史（生产环境应迁移 Redis）
    private Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    // 流式对话
    public Flux<String> streamChat(String sessionId, String message) {
        List<Message> history = conversationHistory
            .computeIfAbsent(sessionId, k -> new ArrayList<>());
        history.add(new UserMessage(message));
        Prompt prompt = new Prompt(history);
        return dashScopeChatModel.stream(prompt)
            .map(response -> response.getResult().getOutput().getText())
            .doOnError(e -> {
                // 失败时回滚最后一条用户消息
                List<Message> msgs = conversationHistory.get(sessionId);
                if (msgs != null && !msgs.isEmpty()) msgs.remove(msgs.size() - 1);
            });
    }

    // 带系统提示词的流式对话（保存 AI 回复到历史）
    public Flux<String> streamChatWithSystem(String sessionId, String systemPrompt, String message) {
        List<Message> history = conversationHistory
            .computeIfAbsent(sessionId, k -> {
                List<Message> list = new ArrayList<>();
                list.add(new AssistantMessage(systemPrompt));  // 系统提示作为首条
                return list;
            });
        history.add(new UserMessage(message));
        Prompt prompt = new Prompt(history);
        StringBuilder fullResponse = new StringBuilder();
        return dashScopeChatModel.stream(prompt)
            .map(response -> {
                String text = response.getResult().getOutput().getText();
                fullResponse.append(text);
                return text;
            })
            .doOnComplete(() -> {
                // 保存完整 AI 回复到历史
                history.add(new AssistantMessage(fullResponse.toString()));
            });
    }

    // 清除历史
    public void clearHistory(String sessionId) {
        conversationHistory.remove(sessionId);
    }

    // 查询历史
    public List<Map<String, String>> getHistory(String sessionId) {
        List<Message> history = conversationHistory.get(sessionId);
        if (history == null) return Collections.emptyList();
        return history.stream().map(msg -> {
            Map<String, String> map = new HashMap<>();
            map.put("role", msg instanceof UserMessage ? "user" : "assistant");
            map.put("content", msg.getText());
            return map;
        }).collect(Collectors.toList());
    }
}
```

**技术债说明**：
- 上下文存储为内存 `ConcurrentHashMap`，进程重启后丢失，生产环境**必须迁移到 Redis**
- `streamChat`（无系统提示词）方法**未保存 AI 回复**到历史，仅 `streamChatWithSystem` 保存

#### 16.7.3 ImageGenerationService（文生图）

```java
@Service
public class ImageGenerationService {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    // 单图生成（模型：qwen-image-plus）
    public String generateImage(String prompt, String size, Integer number) {
        ImageSynthesisParam param = ImageSynthesisParam.builder()
            .apiKey(apiKey)
            .model("qwen-image-plus")
            .prompt(prompt)
            .n(number != null ? number : 1)
            .size(size != null ? size : "1024*1024")
            .build();
        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = imageSynthesis.call(param);
        // 优先返回 b64_json，否则返回 url
        return result.getOutput().getResults().get(0).get("b64_json") != null
            ? result.getOutput().getResults().get(0).get("b64_json")
            : result.getOutput().getResults().get(0).get("url");
    }

    // 多图生成（模型：wanx-v1 通义万相）
    public List<String> generateMultipleImages(String prompt, String size, Integer number) {
        // 同上，模型改为 "wanx-v1"，遍历 results 收集为 List
    }
}
```

**可选模型清单**：`qwen-image`、`qwen-image-plus`、`wanx-v1`、`wan2.2-t2i-plus`、`wan2.2-t2i-flash`

### 16.8 前端 AI 架构

#### 16.8.1 新增文件

| 文件 | 说明 |
|------|------|
| `src/utils/streamChatAPI.js` | SSE 流式请求工具（Fetch + ReadableStream + AbortController） |
| `src/views/AiView.vue` | AI 整合页面（文本生成 + 流式对话 + 图文生成） |

#### 16.8.2 流式请求工具（streamChatAPI.js）

**使用 Fetch API + ReadableStream**（非 EventSource），因为 AI 对话需要 POST + JSON Body，而 EventSource 仅支持 GET：

```javascript
const URL = "http://localhost:8888";

export const streamChatAPI = {
  async streamChat(url, body, onMessage, onError, onComplete) {
    const controller = new AbortController();

    try {
      const response = await fetch(`${URL}${url}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
        signal: controller.signal
      });

      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      while (true) {
        const { done, value } = await reader.read();
        if (done) { if (onComplete) onComplete(); break; }

        buffer += decoder.decode(value, { stream: true });

        // 解析 SSE 格式：按 \n 分割，匹配 data: 前缀
        const lines = buffer.split('\n');
        buffer = lines.pop() || '';

        for (const line of lines) {
          if (line.trim() && line.startsWith('data:')) {
            const data = line.substring(5).trim();
            if (data && data !== '[DONE]') {
              try {
                const parsed = JSON.parse(data);
                onMessage(typeof parsed === 'string' ? parsed : JSON.stringify(parsed));
              } catch {
                onMessage(data);  // 非 JSON，直接用文本
              }
            }
          }
        }
      }
    } catch (error) {
      if (error.name !== 'AbortError' && onError) onError(error);
    }
    return controller;  // 返回控制器供调用方取消
  }
};
```

**设计要点**：
- 三个回调：`onMessage(data)` 增量文本、`onError(error)` 错误、`onComplete()` 完成
- 返回 `AbortController` 供调用方取消请求（停止生成）
- SSE 解析：按 `\n` 分割行，匹配 `data:` 前缀，过滤 `[DONE]`
- buffer 机制处理跨 chunk 的不完整行

> **注意**：当前 `streamChatAPI.js` 的 Fetch 请求**未携带 Token Header**，与 axios 拦截器不一致。流式 AI 接口需后端标注 `@NoToken`，或前端补充 Token 注入。

#### 16.8.3 两套请求通道

| 通道 | 文件 | 用途 | 特点 |
|------|------|------|------|
| **axios** | `utils/request.js` | 文本生成、历史管理、图片生成 | 走拦截器，自动注入 Token，处理业务状态码 |
| **Fetch** | `utils/streamChatAPI.js` | 流式对话（SSE） | 不走 axios，手动解析 SSE，支持取消 |

#### 16.8.4 AI 接口定义（api/getData.js 新增）

```javascript
// 文本生成 API（走 axios）
export const languageModelAPI = {
  generateText(prompt) {
    return request.post('/ai/text/generate', { prompt })
  },
  generateTextWithTemplate(topic, style) {
    return request.post('/ai/text/generate-template', { topic, style })
  },
  generateTextWithChatClient(prompt) {
    return request.post('/ai/text/generate-chat', { prompt })
  }
}

// 对话管理 API（走 axios，流式除外）
export const chatModelAPI = {
  clearHistory(sessionId) {
    return request.delete(`/ai/chat/history/${sessionId}`)
  },
  getHistory(sessionId) {
    return request.get(`/ai/chat/history/${sessionId}`)
  }
}

// 图文生成 API（走 axios）
export const imageAPI = {
  generateImage(prompt, size, number) {
    return request.post('/ai/image/generate', { prompt, size, number })
  },
  generateMultipleImages(prompt, size, number) {
    return request.post('/ai/image/generate-multiple', { prompt, size, number })
  }
}
```

#### 16.8.5 路由配置（新增 /myai）

```javascript
import AIView from '@/views/AiView.vue'

// children 中追加
{ path: '/myai', name: 'myai', component: AIView, meta: { name: 'AI整合' } }
```

#### 16.8.6 AI 对话页面（AiView.vue）

页面分为三大区块：

| 区块 | 功能 | 请求方式 |
|------|------|---------|
| 文本生成 | 简单生成 / 模板生成 / ChatClient 生成 | axios POST（同步） |
| AI 对话（流式） | 流式 SSE 对话 + 系统提示词 + 历史管理 | Fetch SSE（流式） |
| 图文生成 | 单图 / 多图生成 | axios POST（同步） |

**对话消息数据结构**：

```javascript
// 消息对象
{
  role: 'user' | 'assistant',   // 角色
  content: 'string'              // 消息文本
}

// 运行时状态
const messages = ref([])              // 已完成的消息列表
const streamingMessage = ref('')      // 当前流式接收中的临时文本
const sessionId = ref('default-session')
const inputMessage = ref('')
const loading = ref(false)
const useSystemPrompt = ref(false)
const systemPrompt = ref('')
```

**流式对话调用逻辑**：

```javascript
const handleSendMessage = async () => {
  // 1. 添加用户消息
  messages.value.push({ role: 'user', content: inputMessage.value.trim() })
  inputMessage.value = ''
  loading.value = true
  streamingMessage.value = ''

  // 2. 构造请求
  const requestBody = { sessionId: sessionId.value, message: userMessage }
  let url = '/ai/chat/stream'
  if (useSystemPrompt.value && systemPrompt.value) {
    url = '/ai/chat/stream-with-system'
    requestBody.systemPrompt = systemPrompt.value
  }

  // 3. 流式回调
  abortController = await streamChatAPI.streamChat(
    url, requestBody,
    (data) => {                           // onMessage
      streamingMessage.value += data
      nextTick(() => scrollToBottom())
    },
    (error) => {                          // onError
      ElMessage.error('对话失败: ' + error.message)
      messages.value.pop()
      loading.value = false
    },
    () => {                               // onComplete
      if (streamingMessage.value) {
        messages.value.push({ role: 'assistant', content: streamingMessage.value })
      }
      streamingMessage.value = ''
      loading.value = false
    }
  )
}

// 停止生成
const handleStopGeneration = () => {
  if (abortController) {
    abortController.abort()
    if (streamingMessage.value) {
      messages.value.push({ role: 'assistant', content: streamingMessage.value + ' [已停止生成]' })
    }
    streamingMessage.value = ''
    loading.value = false
  }
}
```

**对话历史管理**：
- 历史存储在后端（按 `sessionId`），非前端 localStorage
- 页面 `onMounted` 时调用 `chatModelAPI.getHistory(sessionId)` 加载历史
- 清除历史调用 `chatModelAPI.clearHistory(sessionId)`

**Markdown 渲染**：

当前使用**手写正则替换函数**（未引入 markdown 库）：

```javascript
const formatMessage = (content) => {
  if (!content) return ''
  return content
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')  // 粗体
    .replace(/\*(.*?)\*/g, '<em>$1</em>')               // 斜体
    .replace(/`(.*?)`/g, '<code>$1</code>')             // 行内代码
    .replace(/\n/g, '<br>')                              // 换行
}
```

通过 `v-html="formatMessage(msg.content)"` 渲染。

> **待改进**：当前仅支持粗体、斜体、行内代码、换行。如需完整 Markdown 渲染（代码块、标题、列表、表格等），需引入 `markdown-it` 或 `marked` 库。

### 16.9 AI 模块开发 Checklist

新增 AI 功能时，按以下顺序创建文件：

**后端**：
1. `service/XxxAiService.java` — AI Service（直接 `@Service`，注入 `DashScopeChatModel`）
2. `controller/AIController.java` — 追加接口方法（内部静态类定义请求 DTO）
3. `application.yml` — 确认 `spring.ai.dashscope.api-key` 已配置
4. `pom.xml` — 确认 SpringAI 依赖已引入
5. 若接口无需 Token 校验，在 Controller 方法上标注 `@NoToken`

**前端**：
1. `api/getData.js` — 追加 AI 接口方法（同步走 axios，流式走 `streamChatAPI.js`）
2. `views/AiView.vue` — 在对应区块追加 UI 和逻辑
3. `router/index.js` — 确认 `/myai` 路由已配置
4. 流式接口调用 `streamChatAPI.streamChat()`，传入 `onMessage`/`onError`/`onComplete` 回调

### 16.10 SpringAI 能力边界

| 能力 | 状态 | 说明 |
|------|------|------|
| 同步文本生成 | 已实现 | `DashScopeChatModel.call()` |
| PromptTemplate | 已实现 | Java 文本块内联模板，`{占位符}` 传参 |
| ChatClient | 已实现 | `ChatClient.create(model).prompt().user().call().content()` |
| 流式对话（SSE） | 已实现 | `Flux<String>` + WebFlux + `TEXT_EVENT_STREAM_VALUE` |
| 多轮上下文 | 已实现（内存） | `ConcurrentHashMap<sessionId, List<Message>>`，待迁移 Redis |
| 系统提示词 | 已实现 | 作为 `AssistantMessage` 放入历史首位 |
| 文生图 | 已实现 | DashScope 原生 SDK，模型 `qwen-image-plus` / `wanx-v1` |
| 对话历史管理 | 已实现 | 查询/清除接口，`sessionId` 隔离 |
| RAG（检索增强生成） | **未实现** | 无 VectorStore / EmbeddingModel |
| Function Calling / Tool | **未实现** | 无 `@Tool` 注册（Agent 框架依赖已引入但未使用） |
| 向量库 | **未实现** | 无 PgVector / Milvus 依赖 |
| 外部 Prompt 模板文件 | **未实现** | 模板为 Java 代码内联 |

### 16.11 SpringAI 注意事项

1. **API Key 安全**：`application.yml` 中禁止明文存放，必须使用环境变量 `${DASHSCOPE_API_KEY}` 注入
2. **上下文存储迁移**：当前内存 `ConcurrentHashMap` 重启丢失，生产环境必须迁移到 Redis（Key 格式建议 `AIChat::{sessionId}`）
3. **流式接口 Token**：`streamChatAPI.js` 的 Fetch 请求未携带 Token，需后端 `@NoToken` 或前端补充 Header
4. **WebFlux 与 WebMVC 共存**：`spring-boot-starter-webflux` 与 `spring-boot-starter-web` 并存时，Spring Boot 优先使用 Servlet 容器（WebMVC），WebFlux 仅提供 `Flux` 响应式类型支持
5. **系统提示词实现**：当前将 systemPrompt 包装为 `AssistantMessage` 而非标准的 `SystemMessage`，后续应改为 `SystemMessage`
6. **流式对话历史保存**：`streamChat`（无系统提示词）未在 `doOnComplete` 中保存 AI 回复，仅 `streamChatWithSystem` 保存，需统一
7. **模型版本对齐**：Spring AI `1.1.2` 与 Alibaba `1.1.2.0` 必须版本对齐，不可混用主版本
8. **图片生成模型选择**：`qwen-image-plus` 质量高但慢，`wanx-v1` 快但质量略低，按场景选择

---

## 十七、团队统一开发环境配置

> 本节用于规范团队成员的开发环境，确保所有人使用一致的 JDK、Maven、Node.js、数据库、Redis 及 IDE 版本，避免因环境差异导致的"在我机器上能跑"问题。

### 17.1 本机环境检查结果（参考基线）

以下为本机当前已安装环境（2026-07-15 检查）：

| 环境项 | 本机当前版本 | 项目要求版本 | 是否满足 | 备注 |
|--------|-------------|-------------|---------|------|
| **JDK** | 17.0.16 (Microsoft) / 21.0.10 (Temurin，默认命令) | **17** | 满足 | 系统存在多版本 JDK，需统一指定 JAVA_HOME 为 JDK 17 |
| **Maven** | 3.9.15 | 3.6+ | 满足 | 使用独立安装的 Apache Maven |
| **Node.js** | 22.16.0 | ^22.18.0 \|\| >=24.12.0 | 接近满足 | 建议升级到 22.18.0 或 24.12.0+ |
| **npm** | 10.9.4 | 随 Node 自带 | 满足 | — |
| **Git** | 2.20.1 | 2.20+ | 满足 | 建议升级到 2.40+ 以获得更好性能 |
| **MySQL** | 8.0.45 | 8.x | 满足 | 社区版，端口 3306 |
| **Redis** | 3.2.100 | 3.0+ | 满足 | Windows 版 Redis，端口 6379 |
| **IDE（后端）** | IntelliJ IDEA 2025.2.4 | 2022+ | 满足 | Ultimate / Community 均可 |
| **数据库客户端** | Navicat Premium Lite 17.2 | — | 可选 | 用于可视化操作 MySQL |

**重点说明**：
- 本机 `java -version` 默认输出 **JDK 21**，但 Maven 实际使用的是 **JDK 17**。团队开发时务必将 `JAVA_HOME` 统一指向 JDK 17，避免 Maven 编译与 IDE 运行使用不同版本。
- 原生 MyBatis 教学项目使用 MySQL 5.x 驱动（`com.mysql.jdbc.Driver`），而 Spring Boot 3 / MyBatis-Plus 项目使用 MySQL 8.x 驱动（`com.mysql.cj.jdbc.Driver`）。运行时请确保数据库版本为 MySQL 8.x。

### 17.2 团队统一环境版本清单

所有团队成员必须安装以下版本，**不建议使用更高或更低的主版本**。

| 环境项 | 统一版本 | 下载/说明 |
|--------|---------|----------|
| **JDK** | **17 LTS**（推荐 Microsoft Build of OpenJDK 17 或 Eclipse Temurin 17） | 避免使用 JDK 8/11/21，防止 Spring Boot 3 编译异常 |
| **Maven** | **3.9.x** | 使用独立 Maven，不建议使用 IDE 内置 Maven |
| **Node.js** | **22.18.0 LTS** 或 **24.12.0+** | 前端 package.json 的 engines 要求 |
| **npm** | 随 Node.js 自带 | 建议使用 `npm 10.x` |
| **Git** | **2.40+** | 统一换行符处理策略 |
| **MySQL** | **8.0.x** | 统一使用 `school_spring` 数据库 |
| **Redis** | **5.x+**（Windows 可用 3.2.100 / 5.0.14） | 后端 Token 认证与缓存依赖 |
| **IDE（后端）** | **IntelliJ IDEA 2023+** | 推荐 Ultimate，Community 也可 |
| **IDE（前端）** | **VS Code 1.90+** 或 **WebStorm 2023+** | VS Code 需安装推荐插件 |
| **数据库客户端** | **Navicat Premium / DBeaver / DataGrip** | 推荐 Navicat Premium Lite 或 DBeaver |

### 17.3 环境变量配置（Windows 团队统一）

#### 17.3.1 JAVA_HOME 与 JDK 路径

```
JAVA_HOME = C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot
PATH 追加：%JAVA_HOME%\bin
```

> 若安装的是其他发行版 JDK 17，请按实际路径修改。`JAVA_HOME` 必须指向 JDK 17，不能指向 JRE 或 JDK 21。

#### 17.3.2 Maven 路径

```
MAVEN_HOME = D:\apache-maven-3.9.15-bin\apache-maven-3.9.15
PATH 追加：%MAVEN_HOME%\bin
```

#### 17.3.3 Node.js 路径

Node.js 安装程序会自动配置，通常无需手动修改。验证命令：

```powershell
node -v
npm -v
```

#### 17.3.4 MySQL 与 Redis 路径（可选）

```
# MySQL（安装时自动配置，可选追加）
C:\Program Files\MySQL\MySQL Server 8.0\bin

# Redis（解压版需手动追加）
D:\redis-3.2.100
```

### 17.4 验证命令清单

团队成员配置完环境后，请依次执行以下命令验证：

```powershell
# 1. 验证 Java 版本（必须为 17）
java -version

# 2. 验证 Maven 版本及使用的 JDK 版本
mvn -version

# 3. 验证 Node.js 与 npm
node -v
npm -v

# 4. 验证 Git
git --version

# 5. 验证 MySQL 服务
mysql --version
mysql -uroot -p -e "SELECT VERSION();"

# 6. 验证 Redis 服务
redis-cli --version
redis-cli ping
# 期望返回：PONG
```

### 16.5 IDE 统一配置

#### 17.5.1 IntelliJ IDEA（后端）

**必装插件**：
- Lombok（通常内置，若未内置请安装）
- MyBatisX（Mapper 接口与 XML 跳转）
- Maven Helper（依赖冲突分析）
- Spring Boot（内置）
- .env files support（可选）

**统一设置**：
- `File → Settings → Build → Build Tools → Maven`：
  - Maven home path 指向统一 Maven 3.9.x
  - User settings file 统一使用团队 `settings.xml`（如有私有仓库）
- `File → Settings → Build → Compiler → Java Compiler`：
  - Target bytecode version: **17**
- `File → Settings → Editor → File Encodings`：
  - Global Encoding: **UTF-8**
  - Project Encoding: **UTF-8**
  - Default encoding for properties files: **UTF-8**
- `File → Settings → Editor → Code Style → Java`：
  - 统一使用 **4 空格缩进**（或团队约定的 Tab）
- `File → Settings → Tools → Lombok`：
  - 确保启用 Annotation Processors

#### 17.5.2 VS Code（前端）

**推荐扩展**（可写入 `.vscode/extensions.json`）：

```json
{
  "recommendations": [
    "Vue.volar",
    "Vue.vscode-typescript-vue-plugin",
    "dbaeumer.vscode-eslint",
    "esbenp.prettier-vscode",
    "antfu.unocss",
    "christian-kohler.path-intellisense"
  ]
}
```

**统一设置**（`.vscode/settings.json`）：

```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.tabSize": 2,
  "editor.insertSpaces": true,
  "files.eol": "\n",
  "files.encoding": "utf8"
}
```

### 17.6 数据库初始化脚本（团队统一）

新建成员加入项目时，请执行以下步骤初始化本地数据库：

```sql
-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS school_spring
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 2. 创建用户（可选，也可直接使用 root）
CREATE USER IF NOT EXISTS 'swjtu'@'localhost' IDENTIFIED BY 'swjtu123';
GRANT ALL PRIVILEGES ON school_spring.* TO 'swjtu'@'localhost';
FLUSH PRIVILEGES;

-- 3. 切换数据库
USE school_spring;

-- 4. 执行项目提供的建表/初始化 SQL 脚本
-- source D:/practice/架构/sql/school_spring.sql;
```

**说明**：
- 数据库名统一为 `school_spring`
- 字符集统一为 `utf8mb4`，排序规则 `utf8mb4_unicode_ci`
- 后端 `application.yml` 中数据库账号/密码需与本地一致。若团队使用不同密码，可在本地创建 `application-local.yml` 覆盖，且 **不提交到 Git**

### 17.7 Redis 启动方式（Windows）

#### 方式一：直接启动（开发调试）

```powershell
# 进入 Redis 目录
cd D:\redis-3.2.100

# 启动 Redis 服务端
redis-server.exe redis.windows.conf
```

#### 方式二：注册为 Windows 服务（推荐）

```powershell
# 以管理员身份运行 PowerShell
cd D:\redis-3.2.100
redis-server.exe --service-install redis.windows.conf --loglevel verbose

# 启动服务
redis-server.exe --service-start

# 停止服务
redis-server.exe --service-stop

# 卸载服务
redis-server.exe --service-uninstall
```

**Redis 密码配置**：
- 后端 `application.yml` 中 Redis 密码为 `123456`
- 若本地 Redis 未设置密码，请将 `application.yml` 中 `spring.data.redis.password` 置空，或在 `redis.windows.conf` 中设置 `requirepass 123456`

### 17.8 Maven 统一 settings.xml（推荐）

团队建议统一使用以下 `settings.xml` 配置，确保依赖下载速度一致：

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <localRepository>D:/maven-repo</localRepository>

    <mirrors>
        <!-- 阿里云镜像 -->
        <mirror>
            <id>aliyun</id>
            <name>Aliyun Maven</name>
            <url>https://maven.aliyun.com/repository/public</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>

    <profiles>
        <profile>
            <id>jdk-17</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <maven.compiler.source>17</maven.compiler.source>
                <maven.compiler.target>17</maven.compiler.target>
                <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            </properties>
        </profile>
    </profiles>
</settings>
```

- 本地仓库路径统一为 `D:/maven-repo`（避免 C 盘空间不足）
- 使用阿里云镜像加速依赖下载
- 默认激活 JDK 17 编译配置

### 17.9 Git 统一配置

```powershell
# 统一用户名和邮箱（每位成员使用自己的信息）
git config --global user.name "你的姓名"
git config --global user.email "你的邮箱@example.com"

# 统一换行符为 LF（与 Linux/Mac 协作时建议）
git config --global core.autocrlf false
git config --global core.eol lf

# 设置默认分支名为 main（可选）
git config --global init.defaultBranch main
```

### 17.10 环境配置 Checklist（新人入职）

新成员加入团队时，请按以下清单完成环境配置，并在完成后在团队共享文档中打勾确认：

| 步骤 | 检查项 | 验证命令 |
|------|--------|---------|
| 1 | 安装 JDK 17 并配置 JAVA_HOME | `java -version` |
| 2 | 安装 Maven 3.9.x 并配置 PATH | `mvn -version` |
| 3 | 安装 Node.js 22.18.0+ | `node -v` |
| 4 | 安装 Git 2.40+ 并配置用户名邮箱 | `git --version` |
| 5 | 安装 MySQL 8.0.x 并创建 school_spring 数据库 | `mysql -uroot -p -e "SHOW DATABASES;"` |
| 6 | 安装 Redis 并确认可 ping 通 | `redis-cli ping` |
| 7 | 安装 IntelliJ IDEA / VS Code 及推荐插件 | 手动确认 |
| 8 | 克隆项目代码并导入 IDE | 手动确认 |
| 9 | 后端运行启动类，访问 Swagger UI | `http://localhost:8888/swagger-ui/index.html` |
| 10 | 前端安装依赖并启动 | `npm install && npm run dev` |

### 17.11 常见问题排查

#### Q1: `mvn clean install` 报错 "无效的目标发行版: 17"

**原因**：Maven 使用的 JDK 不是 17。

**解决**：
1. 检查 `JAVA_HOME` 是否指向 JDK 17
2. 检查 IntelliJ IDEA 中 Maven 的 JDK 设置
3. 执行 `mvn -version` 确认 `Java version` 为 17

#### Q2: 前端 `npm install` 失败或依赖冲突

**原因**：Node 版本不匹配或 npm 缓存损坏。

**解决**：
1. 确认 Node 版本为 22.18.0+
2. 清除 npm 缓存：`npm cache clean --force`
3. 删除 `node_modules` 和 `package-lock.json` 后重新安装：`npm install`

#### Q3: 后端启动报错 "无法连接 Redis"

**原因**：Redis 服务未启动或密码不匹配。

**解决**：
1. 执行 `redis-cli ping`，确认返回 `PONG`
2. 检查 `application.yml` 中 Redis 密码是否与本地 Redis 配置一致
3. 若本地 Redis 无密码，将 `spring.data.redis.password` 置空

#### Q4: 后端启动报错 "Access denied for user 'root'@'localhost'"

**原因**：MySQL 账号密码与 `application.yml` 不一致。

**解决**：
1. 确认本地 MySQL root 密码
2. 创建本地覆盖配置文件 `application-local.yml`（**不提交 Git**）

```yaml
spring:
  datasource:
    username: 你的用户名
    password: 你的密码
```

3. 在 IDEA 启动配置中增加 `--spring.profiles.active=local`

#### Q5: 前端请求后端接口报 401

**原因**：登录 Token 未正确存储或已过期。

**解决**：
1. 确认登录接口返回了 Token
2. 确认浏览器 localStorage 中存储了 `Token`
3. 确认 axios 拦截器正确注入了 `Token` Header
4. 若 Token 过期，重新登录

---

*本文档为工程标准化基线，后续所有开发工作均以此为准。如有架构调整、规范更新或环境版本升级，请同步更新本文档并通知团队。*
