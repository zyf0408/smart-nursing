# 互联网+智慧护理培训系统 — 企业级实施方案（完整版）

> 基于 `requirement.md` 工程标准，结合指定技术栈与工具链，从零搭建含 PC 端 + 移动端的完整业务系统。本方案经审查通过后方可落地代码。

---

## 目录

- [一、系统定位与功能清单](#一系统定位与功能清单)
- [二、技术栈与工具链](#二技术栈与工具链)
- [三、系统架构设计](#三系统架构设计)
- [四、数据库设计](#四数据库设计)
- [五、后端架构设计](#五后端架构设计)
- [六、PC 前端架构设计](#六pc-前端架构设计)
- [七、移动端架构设计](#七移动端架构设计)
- [八、安全与规范设计](#八安全与规范设计)
- [九、部署架构设计](#九部署架构设计)
- [十、开发流程与工具链集成](#十开发流程与工具链集成)
- [十一、实施计划与分期](#十一实施计划与分期)
- [十二、工程目录总览](#十二工程目录总览)
- [十三、工程标准全文（基于 requirement.md）](#十三工程标准全文基于-requirementmd)
  - 13.1 后端工程架构标准（Spring Boot）
  - 13.2 前端工程架构标准（Vue3）
  - 13.3 MyBatis / MyBatis-Plus 数据层标准
  - 13.4 数据库设计标准
  - 13.5 API 接口设计标准
  - 13.6 认证与权限机制
  - 13.7 统一返回结果与异常处理
  - 13.8 配置文件标准
  - 13.9 命名规范总表
  - 13.10 编码约定
  - 13.11 前后端联调约定
  - 13.12 日志规范
  - 13.13 依赖版本锁定清单
  - 13.14 工程目录速查模板
  - 13.15 SpringAI 集成架构标准
  - 13.16 团队统一开发环境配置
  - 13.17 Swagger/OpenAPI 文档访问
- [十四、验收标准](#十四验收标准)
- [十五、关键风险与应对](#十五关键风险与应对)

---

## 一、系统定位与功能清单

### 1.1 业务背景

加快发展老年护理服务工作要求，开展老年护理专业护士培训。通过培训，切实提高老年护理专业护士的基本理论、基本知识和基本技能。具备良好的职业道德素养、沟通交流能力、应急处理能力等。掌握常见老年综合征、老年疾病、心理问题等护理要点，增强人文关怀和责任意识，能够规范、专业地为老年患者提供机构和居家护理服务。

### 1.2 系统名称

**互联网+智慧护理培训系统**（Smart Nursing Training System，简称 SNTS）

### 1.3 功能清单（18 个功能模块）

| 序号 | 功能模块 | 所属端 | 说明 |
|------|---------|--------|------|
| 1 | 用户登录认证管理 | PC + 移动端 | 账号密码登录、Token 认证、退出登录 |
| 2 | 用户管理 | PC 管理端 | 护士学员/培训师/管理员的增删改查、分页、状态控制 |
| 3 | 角色权限管理 | PC 管理端 | 角色 CRUD、角色-菜单关联、数据权限分配 |
| 4 | 菜单管理 | PC 管理端 | 菜单树 CRUD、图标管理、排序、按角色控制可见性 |
| 5 | 护理培训类别管理 | PC 管理端 | 树形分类（老年综合征、老年疾病护理、心理护理、应急处理等） |
| 6 | 护理培训标签管理 | PC 管理端 | 标签 CRUD、标签与文章/视频/PPT 多对多关联 |
| 7 | 护理培训文章管理 | PC 管理端 | 富文本编辑、封面图、摘要、发布/下架/草稿、浏览量统计 |
| 8 | 护理培训视频管理 | PC 管理端 | 视频上传/URL 管理、封面图、时长、发布/下架 |
| 9 | 护理培训PPT管理 | PC 管理端 | PPT 文件管理、在线预览、封面图、发布/下架 |
| 10 | 培训考试管理 | PC 管理端 | 试题 CRUD（单选/多选/判断）、组卷、考试发布 |
| 11 | 成绩管理 | PC 管理端 | 成绩查询、统计、导出、及格率分析 |
| 12 | 学习记录管理 | PC + 移动端 | 学习进度记录、学习时长统计、完成状态跟踪 |
| 13 | 学习进度跟踪 | PC + 移动端 | 个人学习仪表盘、进度条、完成率、学习趋势图 |
| 14 | 首页数据统计 | PC 管理端 | 文章/视频/PPT/学员数量、类别分布图、学习趋势 Echarts |
| 15 | 系统日志管理 | PC 管理端 | 操作日志记录、登录日志、异常日志查询 |
| 16 | 移动端学习中心 | 移动端 H5 | 文章阅读、视频播放、PPT 查看、分类浏览、标签筛选 |
| 17 | 移动端考试中心 | 移动端 H5 | 在线答题、交卷、查看成绩、错题回顾 |
| 18 | 个人中心 | PC + 移动端 | 个人信息修改、密码修改、头像上传、我的学习记录 |

### 1.4 三端职责划分

```
┌─────────────────────────────────────────────────────────┐
│                  PC 管理端（Vue3 + Element Plus）        │
│  功能 1-15、18：管理后台，培训师/管理员使用                │
│  内容管理 + 考试管理 + 成绩管理 + 统计分析 + 系统管理      │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│            移动端 H5（uni-app → 仅编译 H5 响应式）        │
│  功能 1、12-13、16-18：护士学员使用                       │
│  学习中心 + 考试中心 + 个人中心 + 学习跟踪                 │
│  手机浏览器 / 微信内置浏览器直接访问                       │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                  后端 API 服务（Spring Boot）            │
│  统一 API 服务，同时为 PC 端和移动端提供接口               │
└─────────────────────────────────────────────────────────┘
```

---

## 二、技术栈与工具链

### 2.1 开发语言与框架

| 类别 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **开发语言** | Java | 17 | 后端（本地 JDK 17.0.16，Spring Boot 3.5.13 兼容） |
| **开发语言** | JavaScript | ES2023+ | 前端 + 移动端 |
| **后端框架** | Spring Boot | 3.5.13 | 父 POM 统一管理 |
| **后端框架** | Spring MVC | Boot 管理 | Web 层 |
| **ORM 框架** | MyBatis-Plus | 3.5.12 | spring-boot3-starter |
| **ORM 框架** | MyBatis | 3.5.x | MyBatis-Plus 内含 |
| **缓存中间件** | Redis | 5.x+ | Token 存储 + 数据缓存 |
| **Web 服务器** | Nginx | 1.24+ | 前端部署 + 反向代理 + 负载均衡 |
| **前端框架** | Vue3 | ^3.5.38 | Composition API + `<script setup>` |
| **前端 UI** | Element Plus | ^2.14.3 | PC 管理端 UI 组件库 |
| **前端路由** | Vue Router | ^4.6.3 | History 模式 |
| **HTTP 库** | Axios | ^1.18.1 | HTTP 请求 |
| **图表库** | Echarts | ^6.0.0 | 数据可视化 |
| **状态管理** | Pinia | ^2.2.6 | Vue3 状态管理 |
| **构建工具** | Vite | ^7.0.0 | 前端构建（Vite 7.0 于 2025-06-24 发布，最新主版本） |
| **移动端框架** | uni-app | ^3.0 | HBuilderX 内置，仅编译 H5 响应式页面 |
| **移动端 UI** | uni-ui | ^2.0 | uni-app 组件库，移动端 H5 适配 |

### 2.2 数据库与运维

| 类别 | 技术 | 版本 | 说明 |
|------|------|------|------|
| **数据库** | MySQL | 8.0 | 数据存储 |
| **版本控制** | Git | 2.40+ | 代码版本管理 |
| **部署环境** | Windows | Server 2016+ | 生产部署 |

### 2.3 集成开发环境

| 工具 | 版本 | 用途 |
|------|------|------|
| IntelliJ IDEA | 2025.2.4 | 后端 Java 开发 |
| HBuilderX | 4.76 | 移动端 uni-app 开发 |
| VS Code | 1.128.1 | PC 前端 Vue3 开发 |

### 2.4 项目管理与设计工具

| 工具 | 版本 | 用途 | 产出物 |
|------|------|------|--------|
| Microsoft Project | 2013 | 项目计划管理 | 甘特图、里程碑、任务分配 |
| Visio | 2013 | 系统建模绘图 | 架构图、流程图、ER 图、部署图 |
| PowerDesigner | 16.5 | 数据库设计 | 物理数据模型（PDM）、概念模型（CDM） |
| SQLyog / Navicat | 最新版 | 数据库管理 | 建表、数据导入导出、SQL 调试 |
| JMeter | 5.6+ | 性能测试 | 并发测试、压力测试报告 |
| 禅道/Jira | 最新版 | 测试管理 | 测试用例管理、缺陷跟踪 |

### 2.5 工具链在开发流程中的应用

```
MS Project → 制定项目计划（甘特图、里程碑）
     ↓
PowerDesigner → 数据库建模（PDM → 导出建表 SQL）
     ↓
Visio → 绘制架构图/流程图/ER图/部署图
     ↓
IDEA + VS Code + HBuilderX → 三端并行开发
     ↓
Git → 代码版本管理（分支策略：main/develop/feature）
     ↓
SQLyog/Navicat → 数据库管理与数据初始化
     ↓
禅道/Jira → 测试用例管理 + 缺陷跟踪
     ↓
JMeter → 性能压测（关键接口并发测试）
     ↓
Nginx + Redis + MySQL → Windows 生产部署
```

---

## 三、系统架构设计

### 3.1 整体架构

```
                        ┌──────────────────────┐
                        │     用户终端层        │
                        └──────┬───────┬───────┘
                               │       │
              ┌────────────────┘       └────────────────┐
              │                                         │
    ┌─────────▼─────────┐                  ┌────────────▼────────────┐
    │   PC 管理端       │                   │    移动端               │
    │   Vue3+ElementPlus│                  │  uni-app(仅H5响应式)     │
    │   端口: 5173(开发) │                 │    HBuilderX 构建        │
    │   Nginx: 80(生产) │                  │                         │
    └─────────┬─────────┘                  └────────────┬────────────┘
              │                                         │
              │            HTTP / RESTful API           │
              │         Token Header 认证               │
              └──────────────┬──────────────────────────┘
                             │
                ┌────────────▼────────────┐
                │     Nginx 反向代理       │
                │  端口: 80 → 8888        │
                │  静态资源 + API 转发     │
                └────────────┬────────────┘
                             │
                ┌────────────▼────────────┐
                │   Spring Boot 后端服务   │
                │   端口: 8888             │
                │  ┌────────────────────┐ │
                │  │  AOP (Token 切面)  │ │
                │  ├────────────────────┤ │
                │  │  Controller 层     │ │
                │  ├────────────────────┤ │
                │  │  Service 层        │ │
                │  ├────────────────────┤ │
                │  │  DAO (MyBatis-Plus)│ │
                │  └────────────────────┘ │
                └──────┬──────────┬───────┘
                       │          │
              ┌────────▼──┐  ┌───▼──────────┐
              │  MySQL 8.0 │  │   Redis 5.x  │
              │  数据存储   │  │  Token+缓存  │
              └────────────┘  └──────────────┘
```

### 3.2 后端分层架构

```
请求 → AOP切面(Token校验) → Controller → Service → Mapper → MySQL
                                                        ↓
响应 ← Controller ← Service(封装CommonResult) ← Mapper ← MySQL
                                         ↓
                                    Redis(缓存/Token)
```

### 3.3 API 分端设计

后端为 PC 端和移动端提供**统一的 API 服务**，通过路径前缀区分：

| 前缀 | 服务对象 | 示例 |
|------|---------|------|
| `/admin/*` | PC 管理端 | `/admin/user/list`、`/admin/article/add` |
| `/mobile/*` | 移动端 | `/mobile/learning/center`、`/mobile/exam/start` |
| `/login`、`/logout` | 公共 | 两端共用 |
| `/common/*` | 公共 | `/common/upload`、`/common/category/tree` |

---

## 四、数据库设计

### 4.1 数据库概览

- 数据库名：`smart_nursing`
- 字符集：`utf8mb4`，排序规则 `utf8mb4_unicode_ci`
- 建模工具：PowerDesigner 16.5（PDM 物理数据模型）
- 管理工具：SQLyog / Navicat

### 4.2 表清单（21 张表）

> **[审查修订]** 原 20 张表，新增 `nursing_favorite`（收藏表），`nursing_exam` 增加 `max_attempts` 字段（可配置补考次数）。

| 序号 | 表名 | 说明 | 所属模块 |
|------|------|------|---------|
| 1 | `sys_user` | 系统用户 | 系统管理 |
| 2 | `sys_role` | 角色表 | 系统管理 |
| 3 | `sys_menu` | 菜单权限 | 系统管理 |
| 4 | `sys_role_menu` | 角色-菜单关联 | 系统管理 |
| 5 | `sys_user_role` | 用户-角色关联 | 系统管理 |
| 6 | `sys_log` | 系统操作日志 | 系统管理 |
| 7 | `nursing_category` | 培训类别（树形） | 内容管理 |
| 8 | `nursing_tag` | 培训标签 | 内容管理 |
| 9 | `nursing_article` | 培训文章 | 内容管理 |
| 10 | `nursing_video` | 培训视频 | 内容管理 |
| 11 | `nursing_ppt` | 培训PPT | 内容管理 |
| 12 | `nursing_article_tag` | 文章-标签关联 | 内容管理 |
| 13 | `nursing_video_tag` | 视频-标签关联 | 内容管理 |
| 14 | `nursing_ppt_tag` | PPT-标签关联 | 内容管理 |
| 15 | `nursing_exam` | 考试表 | 考试管理 |
| 16 | `nursing_question` | 试题表 | 考试管理 |
| 17 | `nursing_exam_question` | 考试-试题关联 | 考试管理 |
| 18 | `nursing_exam_record` | 考试记录（学员成绩） | 考试管理 |
| 19 | `nursing_learning_record` | 学习记录 | 学习管理 |
| 20 | `nursing_study_plan` | 学习计划 | 学习管理 |
| 21 | `nursing_favorite` | 收藏表（文章/视频/PPT收藏） | 学习管理 |

### 4.3 核心表结构设计

#### 4.3.1 系统管理表

**sys_user** — 系统用户

| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | BIGINT AUTO_INCREMENT | 主键 |
| username | VARCHAR(50) | 登录账号（联合唯一索引 uk_username_del(username, is_delete)） |
| password | VARCHAR(100) | 密码（BCrypt 加密） |
| real_name | VARCHAR(50) | 真实姓名 |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| avatar | VARCHAR(255) | 头像URL |
| user_type | INT | 用户类型（1-管理员 2-培训师 3-护士学员） |
| status | INT DEFAULT 1 | 状态（0-禁用 1-启用） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| is_delete | INT DEFAULT 0 | 逻辑删除 |

**sys_role** — 角色表

| 字段 | 类型 | 说明 |
|------|------|------|
| role_id | BIGINT AUTO_INCREMENT | 主键 |
| role_name | VARCHAR(50) | 角色名称 |
| role_code | VARCHAR(50) | 角色编码（联合唯一索引 uk_rolecode_del(role_code, is_delete)） |
| description | VARCHAR(200) | 描述 |
| status | INT DEFAULT 1 | 状态 |
| create_time | DATETIME | 创建时间 |
| is_delete | INT DEFAULT 0 | 逻辑删除 |

**sys_menu** — 菜单权限

| 字段 | 类型 | 说明 |
|------|------|------|
| menu_id | BIGINT AUTO_INCREMENT | 主键 |
| title | VARCHAR(50) | 菜单标题 |
| path | VARCHAR(100) | 路由路径 |
| icon | VARCHAR(50) | 图标名 |
| parent_id | BIGINT DEFAULT 0 | 父菜单ID（0为顶级） |
| sort_order | INT | 排序 |
| visible | INT DEFAULT 1 | 是否可见 |
| create_time | DATETIME | 创建时间 |

**sys_role_menu** — 角色-菜单关联

| 字段 | 类型 | 说明 |
|------|------|------|
| role_id | BIGINT | 角色ID |
| menu_id | BIGINT | 菜单ID |

**sys_user_role** — 用户-角色关联

| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | BIGINT | 用户ID |
| role_id | BIGINT | 角色ID |

**sys_log** — 系统操作日志

| 字段 | 类型 | 说明 |
|------|------|------|
| log_id | BIGINT AUTO_INCREMENT | 主键 |
| user_id | BIGINT | 操作用户ID |
| username | VARCHAR(50) | 操作用户名 |
| operation | VARCHAR(200) | 操作描述 |
| method | VARCHAR(200) | 请求方法 |
| params | TEXT | 请求参数 |
| ip | VARCHAR(50) | IP地址 |
| log_type | INT | 日志类型（1-操作日志 2-登录日志 3-异常日志） |
| create_time | DATETIME | 操作时间 |

#### 4.3.2 内容管理表

**nursing_category** — 培训类别（树形）

| 字段 | 类型 | 说明 |
|------|------|------|
| category_id | BIGINT AUTO_INCREMENT | 主键 |
| category_name | VARCHAR(100) | 类别名称 |
| parent_id | BIGINT DEFAULT 0 | 父类别ID（0为顶级） |
| sort_order | INT DEFAULT 0 | 排序 |
| description | VARCHAR(500) | 描述 |
| create_time | DATETIME | 创建时间 |
| is_delete | INT DEFAULT 0 | 逻辑删除 |

**nursing_tag** — 培训标签

| 字段 | 类型 | 说明 |
|------|------|------|
| tag_id | BIGINT AUTO_INCREMENT | 主键 |
| tag_name | VARCHAR(50) | 标签名称 |
| create_time | DATETIME | 创建时间 |
| is_delete | INT DEFAULT 0 | 逻辑删除 |

**nursing_article** — 培训文章

| 字段 | 类型 | 说明 |
|------|------|------|
| article_id | BIGINT AUTO_INCREMENT | 主键 |
| title | VARCHAR(200) | 标题 |
| category_id | BIGINT | 所属类别 |
| summary | VARCHAR(500) | 摘要 |
| content | LONGTEXT | 正文（富文本HTML） |
| cover_image | VARCHAR(255) | 封面图URL |
| author_id | BIGINT | 作者ID（关联 sys_user.user_id） |
| view_count | INT DEFAULT 0 | 浏览量 |
| sort_order | INT DEFAULT 0 | 排序 |
| status | INT DEFAULT 0 | 状态（0-草稿 1-已发布 2-已下架） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| is_delete | INT DEFAULT 0 | 逻辑删除 |

**nursing_video** — 培训视频

| 字段 | 类型 | 说明 |
|------|------|------|
| video_id | BIGINT AUTO_INCREMENT | 主键 |
| title | VARCHAR(200) | 标题 |
| category_id | BIGINT | 所属类别 |
| description | VARCHAR(500) | 描述 |
| video_url | VARCHAR(500) | 视频地址 |
| cover_image | VARCHAR(255) | 封面图URL |
| duration | INT | 时长（秒） |
| author_id | BIGINT | 作者ID（关联 sys_user.user_id） |
| view_count | INT DEFAULT 0 | 浏览量 |
| sort_order | INT DEFAULT 0 | 排序 |
| status | INT DEFAULT 0 | 状态（0-草稿 1-已发布 2-已下架） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| is_delete | INT DEFAULT 0 | 逻辑删除 |

**nursing_ppt** — 培训PPT

| 字段 | 类型 | 说明 |
|------|------|------|
| ppt_id | BIGINT AUTO_INCREMENT | 主键 |
| title | VARCHAR(200) | 标题 |
| category_id | BIGINT | 所属类别 |
| description | VARCHAR(500) | 描述 |
| file_url | VARCHAR(500) | PPT文件地址 |
| cover_image | VARCHAR(255) | 封面图URL |
| author_id | BIGINT | 作者ID（关联 sys_user.user_id） |
| view_count | INT DEFAULT 0 | 浏览量 |
| sort_order | INT DEFAULT 0 | 排序 |
| status | INT DEFAULT 0 | 状态（0-草稿 1-已发布 2-已下架） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| is_delete | INT DEFAULT 0 | 逻辑删除 |

**nursing_article_tag / nursing_video_tag / nursing_ppt_tag** — 关联表

| 字段 | 类型 | 说明 |
|------|------|------|
| {content}_id | BIGINT | 内容ID |
| tag_id | BIGINT | 标签ID |

#### 4.3.3 考试管理表

**nursing_exam** — 考试表

| 字段 | 类型 | 说明 |
|------|------|------|
| exam_id | BIGINT AUTO_INCREMENT | 主键 |
| exam_name | VARCHAR(100) | 考试名称 |
| description | VARCHAR(500) | 考试说明 |
| total_score | INT | 总分 |
| pass_score | INT | 及格分 |
| duration | INT | 考试时长（分钟） |
| max_attempts | INT DEFAULT 1 | 最大考试次数（1=不可补考，可配置） |
| status | INT DEFAULT 0 | 状态（0-未发布 1-已发布 2-已结束） |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| create_time | DATETIME | 创建时间 |
| is_delete | INT DEFAULT 0 | 逻辑删除 |

**nursing_question** — 试题表

| 字段 | 类型 | 说明 |
|------|------|------|
| question_id | BIGINT AUTO_INCREMENT | 主键 |
| category_id | BIGINT | 所属类别 |
| question_type | INT | 题型（1-单选 2-多选 3-判断） |
| content | TEXT | 题干 |
| option_a | VARCHAR(500) | 选项A |
| option_b | VARCHAR(500) | 选项B |
| option_c | VARCHAR(500) | 选项C |
| option_d | VARCHAR(500) | 选项D |
| answer | VARCHAR(10) | 正确答案（如 "A" / "AB" / "true"） |
| analysis | TEXT | 答案解析 |
| score | INT DEFAULT 5 | 分值 |
| create_time | DATETIME | 创建时间 |
| is_delete | INT DEFAULT 0 | 逻辑删除 |

**nursing_exam_question** — 考试-试题关联

| 字段 | 类型 | 说明 |
|------|------|------|
| exam_id | BIGINT | 考试ID |
| question_id | BIGINT | 试题ID |
| sort_order | INT | 排序 |

**nursing_exam_record** — 考试记录

| 字段 | 类型 | 说明 |
|------|------|------|
| record_id | BIGINT AUTO_INCREMENT | 主键 |
| exam_id | BIGINT | 考试ID |
| user_id | BIGINT | 学员ID |
| attempt_count | INT DEFAULT 1 | 第几次考试（联合唯一索引 uk_exam_user_attempt(exam_id, user_id, attempt_count)） |
| status | INT DEFAULT 0 | 考试状态（0-未考 1-考试中 2-已交卷） |
| answers | JSON | 答题JSON（`[{questionId, answer}, ...]`） |
| score | INT | 得分 |
| is_pass | INT DEFAULT 0 | 是否及格（0-未通过 1-通过） |
| start_time | DATETIME | 开始答题时间 |
| submit_time | DATETIME | 交卷时间 |
| create_time | DATETIME | 创建时间 |

#### 4.3.4 学习管理表

**nursing_learning_record** — 学习记录

| 字段 | 类型 | 说明 |
|------|------|------|
| record_id | BIGINT AUTO_INCREMENT | 主键 |
| user_id | BIGINT | 用户ID |
| content_type | INT | 内容类型（1-文章 2-视频 3-PPT） |
| content_id | BIGINT | 内容ID |
| progress | INT DEFAULT 0 | 学习进度（0-100） |
| study_duration | INT DEFAULT 0 | 学习时长（秒） |
| last_study_time | DATETIME | 最后学习时间 |
| is_completed | INT DEFAULT 0 | 是否完成（0-未完成 1-已完成） |
| version | INT DEFAULT 0 | 乐观锁版本号（防止并发更新覆盖） |

> **[审查修复]** 联合唯一索引：`uk_user_content(user_id, content_type, content_id)`，确保同一用户对同一内容只有一条学习记录。更新时使用 `UPDATE ... SET version = version + 1 WHERE version = ?` 乐观锁。

**nursing_study_plan** — 学习计划

| 字段 | 类型 | 说明 |
|------|------|------|
| plan_id | BIGINT AUTO_INCREMENT | 主键 |
| user_id | BIGINT | 用户ID |
| plan_name | VARCHAR(100) | 计划名称 |
| target_content_type | INT | 目标内容类型 |
| target_content_id | BIGINT | 目标内容ID |
| target_progress | INT DEFAULT 100 | 目标进度 |
| deadline | DATETIME | 截止日期 |
| status | INT DEFAULT 0 | 状态（0-进行中 1-已完成 2-已逾期） |
| create_time | DATETIME | 创建时间 |

**nursing_favorite** — 收藏表（文章/视频/PPT收藏）

| 字段 | 类型 | 说明 |
|------|------|------|
| favorite_id | BIGINT AUTO_INCREMENT | 主键 |
| user_id | BIGINT | 用户ID |
| content_type | INT | 内容类型（1-文章 2-视频 3-PPT） |
| content_id | BIGINT | 内容ID |
| create_time | DATETIME | 收藏时间 |

> **[审查修复]** 联合唯一索引：`uk_favorite(user_id, content_type, content_id)`，防止重复收藏。

### 4.4 ER 关系图

```
sys_user ──< sys_user_role >── sys_role ──< sys_role_menu >── sys_menu
    │
    ├──< sys_log
    ├──< nursing_learning_record >── nursing_article / nursing_video / nursing_ppt
    ├──< nursing_exam_record >── nursing_exam ──< nursing_exam_question >── nursing_question
    └──< nursing_study_plan

nursing_category ──< nursing_article
                 ──< nursing_video
                 ──< nursing_ppt
                 ──< nursing_question

nursing_tag ──< nursing_article_tag >── nursing_article
            ──< nursing_video_tag  >── nursing_video
            ──< nursing_ppt_tag    >── nursing_ppt
```

---

## 五、后端架构设计

### 5.1 包结构

根包：`com.smart.nursing`

```
com.smart.nursing
├── SmartNursingApplication.java                      # 启动类
│
├── controller/
│   ├── common/
│   │   ├── LoginController.java                      # 登录/退出（公共）
│   │   ├── UploadController.java                     # 文件上传（公共）
│   │   └── CategoryController.java                   # 类别树（公共）
│   ├── admin/                                        # PC 管理端接口
│   │   ├── UserController.java                       # 用户管理
│   │   ├── RoleController.java                       # 角色管理
│   │   ├── MenuController.java                       # 菜单管理
│   │   ├── LogController.java                        # 日志管理
│   │   ├── TagController.java                        # 标签管理
│   │   ├── ArticleController.java                    # 文章管理
│   │   ├── VideoController.java                      # 视频管理
│   │   ├── PptController.java                        # PPT管理
│   │   ├── ExamController.java                       # 考试管理
│   │   ├── QuestionController.java                   # 试题管理
│   │   ├── ExamRecordController.java                 # 成绩管理
│   │   ├── LearningController.java                   # 学习记录管理
│   │   ├── FavoriteController.java                   # 收藏管理
│   │   ├── DataCountController.java                  # 首页统计
│   │   └── AIController.java                         # AI 功能（问答/生成/推荐/文生图）
│   └── mobile/                                       # 移动端接口
│       ├── MobileLearnController.java                # 移动端学习中心（含收藏）
│       ├── MobileExamController.java                 # 移动端考试中心
│       └── MobileUserController.java                 # 移动端个人中心
│
├── service/
│   ├── IUserService.java
│   ├── IRoleService.java
│   ├── IMenuService.java
│   ├── ILogService.java
│   ├── ICategoryService.java
│   ├── ITagService.java
│   ├── IArticleService.java
│   ├── IVideoService.java
│   ├── IPptService.java
│   ├── IExamService.java
│   ├── IQuestionService.java
│   ├── IExamRecordService.java
│   ├── ILearningService.java
│   ├── IStudyPlanService.java
│   ├── IFavoriteService.java
│   ├── IDataCountService.java
│   ├── LanguageModelService.java                    # AI 同步文本生成
│   ├── ChatModelService.java                        # AI 流式对话
│   ├── ImageGenerationService.java                  # AI 文生图
│   ├── RecommendService.java                        # AI 个性化推荐
│   └── impl/
│       ├── UserServiceImpl.java
│       ├── RoleServiceImpl.java
│       ├── MenuServiceImpl.java
│       ├── LogServiceImpl.java
│       ├── CategoryServiceImpl.java
│       ├── TagServiceImpl.java
│       ├── ArticleServiceImpl.java
│       ├── VideoServiceImpl.java
│       ├── PptServiceImpl.java
│       ├── ExamServiceImpl.java
│       ├── QuestionServiceImpl.java
│       ├── ExamRecordServiceImpl.java
│       ├── LearningServiceImpl.java
│       ├── StudyPlanServiceImpl.java
│       ├── FavoriteServiceImpl.java
│       └── DataCountServiceImpl.java
│
├── dao/
│   ├── UserMapper.java
│   ├── RoleMapper.java
│   ├── MenuMapper.java
│   ├── RoleMenuMapper.java
│   ├── UserRoleMapper.java
│   ├── LogMapper.java
│   ├── CategoryMapper.java
│   ├── TagMapper.java
│   ├── ArticleMapper.java
│   ├── ArticleTagMapper.java
│   ├── VideoMapper.java
│   ├── VideoTagMapper.java
│   ├── PptMapper.java
│   ├── PptTagMapper.java
│   ├── ExamMapper.java
│   ├── QuestionMapper.java
│   ├── ExamQuestionMapper.java
│   ├── ExamRecordMapper.java
│   ├── LearningRecordMapper.java
│   ├── StudyPlanMapper.java
│   └── FavoriteMapper.java
│
├── entity/                                           # 21 个实体类（对应 21 张表）
│   ├── UserEntity.java
│   ├── RoleEntity.java
│   ├── MenuEntity.java
│   ├── RoleMenuEntity.java
│   ├── UserRoleEntity.java
│   ├── LogEntity.java
│   ├── CategoryEntity.java
│   ├── TagEntity.java
│   ├── ArticleEntity.java
│   ├── ArticleTagEntity.java
│   ├── VideoEntity.java
│   ├── VideoTagEntity.java
│   ├── PptEntity.java
│   ├── PptTagEntity.java
│   ├── ExamEntity.java
│   ├── QuestionEntity.java
│   ├── ExamQuestionEntity.java
│   ├── ExamRecordEntity.java
│   ├── LearningRecordEntity.java
│   ├── StudyPlanEntity.java
│   └── FavoriteEntity.java
│
├── dto/
│   ├── UserDto.java                                  # 用户查询（含分页+角色筛选）
│   ├── ArticleDto.java                               # 文章查询（含分页+类别+标签）
│   ├── VideoDto.java
│   ├── PptDto.java
│   ├── ExamDto.java                                  # 考试查询（含分页）
│   ├── QuestionDto.java                              # 试题查询（含分页+类别+题型）
│   ├── ExamRecordDto.java                            # 成绩查询（含分页+考试+学员）
│   ├── LearningRecordDto.java                        # 学习记录查询
│   └── StudyPlanDto.java
│
├── vo/
│   ├── LoginUserVo.java                              # 登录返回（用户+角色+菜单+token）
│   ├── ArticleVo.java                                # 文章详情（含类别名+标签列表）
│   ├── VideoVo.java
│   ├── PptVo.java
│   ├── CategoryTreeVo.java                           # 树形类别
│   ├── MenuTreeVo.java                               # 树形菜单
│   ├── DashboardVo.java                              # 统计数据
│   ├── ExamDetailVo.java                             # 考试详情（含试题列表）
│   ├── ExamResultVo.java                             # 考试结果（含得分+答案解析）
│   └── LearningProgressVo.java                       # 学习进度统计
│
├── aop/
│   ├── AroundCut.java                                # Token 校验切面
│   ├── NoToken.java                                  # 免 Token 注解
│   └── LogAspect.java                                # ★ 操作日志切面（AOP 自动记录日志）
│
└── common/
    ├── config/
    │   ├── SpringDocConfig.java                      # Swagger/OpenAPI 配置
    │   ├── WebConfig.java                            # CORS 跨域配置
    │   ├── MybatisPlusConfig.java                    # MyBatis-Plus 配置 + MapperScan
    │   └── RedisConfig.java                          # Redis 序列化配置
    ├── exception/
    │   ├── GlobalExceptionHandler.java               # 全局异常处理器（细分类型）
    │   └── BusinessException.java                     # 业务异常
    ├── result/
    │   ├── CommonResult.java                         # 统一响应封装
    │   ├── ErrorCode.java                            # 错误码对象
    │   └── PageParam.java                            # 分页参数基类
    ├── enums/
    │   └── GlobalErrorCodeConstants.java             # 错误码常量
    └── utils/
        ├── SecurityUtils.java                        # 密码加密工具（BCryptPasswordEncoder）
        └── DateUtils.java                            # 日期工具
```

### 5.2 API 接口清单（共 60+ 个接口）

#### 5.2.1 公共接口

| 方法 | 路径 | 入参 | 返回 | Token |
|------|------|------|------|-------|
| POST | `/login` | `{username, password}` | `CommonResult<LoginUserVo>` | 否 |
| GET | `/logout` | — | `CommonResult<Void>` | 是 |
| POST | `/common/upload` | `MultipartFile` | `CommonResult<String>` (url) | 是 |
| GET | `/common/category/tree` | — | `CommonResult<List<CategoryTreeVo>>` | 是 |

> **[审查补全]** 以下接口在原清单中遗漏，现补全。

#### 5.2.1a 补全接口

| 方法 | 路径 | 说明 | 补全原因 |
|------|------|------|---------|
| GET | `/admin/category/tree` | 类别树查询（PC 端管理展示用） | 原只有 add/update/delete，缺树查询 |
| POST | `/admin/tag/list` | 标签分页查询 | 原只有 `tag/all`，标签多时需分页 |
| POST | `/mobile/learn/favorite/add` | 添加收藏 | 新增收藏表后需对应接口 |
| POST | `/mobile/learn/favorite/remove` | 取消收藏 | 同上 |
| GET | `/mobile/learn/favorite/list` | 我的收藏列表 | 同上 |
| GET | `/mobile/learn/search` | 移动端内容搜索（关键词+类型筛选） | 移动端设计有搜索栏但无接口 |
| POST | `/ai/question/generate` | AI 生成试题并保存到题库 | AI 生成试题后需要保存链路 |
| GET | `/ai/recommend/{userId}` | AI 个性化学习推荐 | 基于学习记录动态推荐内容 |

#### 5.2.2 PC 管理端 — 用户/角色/菜单/日志

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/admin/user/list` | 用户分页查询 |
| POST | `/admin/user/add` | 新增用户 |
| POST | `/admin/user/update` | 修改用户 |
| POST | `/admin/user/delete` | 删除用户 |
| GET | `/admin/user/getById/{userId}` | 用户详情 |
| POST | `/admin/user/resetPassword` | 重置密码 |
| POST | `/admin/role/list` | 角色分页查询 |
| POST | `/admin/role/add` | 新增角色 |
| POST | `/admin/role/update` | 修改角色 |
| POST | `/admin/role/delete` | 删除角色 |
| POST | `/admin/role/assignMenus` | 角色分配菜单 |
| GET | `/admin/menu/tree` | 菜单树查询 |
| POST | `/admin/menu/add` | 新增菜单 |
| POST | `/admin/menu/update` | 修改菜单 |
| POST | `/admin/menu/delete` | 删除菜单 |
| POST | `/admin/log/list` | 日志分页查询 |

#### 5.2.3 PC 管理端 — 内容管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/admin/category/add` | 新增类别 |
| POST | `/admin/category/update` | 修改类别 |
| POST | `/admin/category/delete/{categoryId}` | 删除类别 |
| GET | `/admin/tag/all` | 标签全量列表 |
| POST | `/admin/tag/add` | 新增标签 |
| POST | `/admin/tag/update` | 修改标签 |
| POST | `/admin/tag/delete/{tagId}` | 删除标签 |
| POST | `/admin/article/list` | 文章分页查询 |
| GET | `/admin/article/getById/{articleId}` | 文章详情 |
| POST | `/admin/article/add` | 新增文章（含标签关联） |
| POST | `/admin/article/update` | 修改文章 |
| POST | `/admin/article/delete/{articleId}` | 删除文章 |
| POST | `/admin/article/publish/{articleId}` | 发布/下架 |
| POST | `/admin/video/list` | 视频分页查询 |
| GET | `/admin/video/getById/{videoId}` | 视频详情 |
| POST | `/admin/video/add` | 新增视频 |
| POST | `/admin/video/update` | 修改视频 |
| POST | `/admin/video/delete/{videoId}` | 删除视频 |
| POST | `/admin/ppt/list` | PPT分页查询 |
| GET | `/admin/ppt/getById/{pptId}` | PPT详情 |
| POST | `/admin/ppt/add` | 新增PPT |
| POST | `/admin/ppt/update` | 修改PPT |
| POST | `/admin/ppt/delete/{pptId}` | 删除PPT |

#### 5.2.4 PC 管理端 — 考试管理

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/admin/exam/list` | 考试分页查询 |
| GET | `/admin/exam/getById/{examId}` | 考试详情（含试题） |
| POST | `/admin/exam/add` | 新增考试 |
| POST | `/admin/exam/update` | 修改考试 |
| POST | `/admin/exam/delete/{examId}` | 删除考试 |
| POST | `/admin/exam/publish/{examId}` | 发布/结束考试 |
| POST | `/admin/exam/assignQuestions` | 考试关联试题 |
| POST | `/admin/question/list` | 试题分页查询 |
| POST | `/admin/question/add` | 新增试题 |
| POST | `/admin/question/update` | 修改试题 |
| POST | `/admin/question/delete/{questionId}` | 删除试题 |
| POST | `/admin/examRecord/list` | 成绩分页查询 |
| GET | `/admin/examRecord/getById/{recordId}` | 成绩详情（含答题详情） |
| POST | `/admin/examRecord/export` | 成绩导出 |

#### 5.2.5 PC 管理端 — 学习管理 + 统计

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/admin/learning/list` | 学习记录分页查询 |
| GET | `/admin/learning/userProgress/{userId}` | 指定学员学习进度 |
| GET | `/data/getMainPageCountData` | 首页4项统计 |
| GET | `/data/getCategoryContentCount` | 各类别内容数量（图表） |
| GET | `/data/getLearningTrend` | 学习趋势（图表） |
| GET | `/data/getExamPassRate` | 考试及格率（图表） |

#### 5.2.6 移动端接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/mobile/learn/articles` | 文章列表（分页+分类+标签筛选） |
| GET | `/mobile/learn/article/{articleId}` | 文章阅读详情 |
| GET | `/mobile/learn/videos` | 视频列表 |
| GET | `/mobile/learn/video/{videoId}` | 视频播放详情 |
| GET | `/mobile/learn/ppts` | PPT列表 |
| GET | `/mobile/learn/ppt/{pptId}` | PPT查看详情 |
| POST | `/mobile/learn/record` | 上报学习记录 |
| GET | `/mobile/learn/myProgress` | 我的学习进度 |
| GET | `/mobile/learn/myRecords` | 我的学习记录 |
| GET | `/mobile/exam/list` | 可用考试列表 |
| GET | `/mobile/exam/start/{examId}` | 开始考试（获取试题） |
| POST | `/mobile/exam/submit` | 交卷 |
| GET | `/mobile/exam/result/{recordId}` | 查看成绩+答案解析 |
| GET | `/mobile/exam/myRecords` | 我的考试记录 |
| GET | `/mobile/user/profile` | 个人信息 |
| POST | `/mobile/user/updateProfile` | 修改个人信息 |
| POST | `/mobile/user/changePassword` | 修改密码 |
| POST | `/mobile/user/uploadAvatar` | 上传头像 |

### 5.3 关键实现要点

#### 5.3.0 文件存储方案

> **[审查新增]** 原文档缺少文件存储方案，现补充。

| 维度 | 约定 |
|------|------|
| 存储方式 | 本地磁盘存储 |
| 存储根目录 | 后端工程下 `upload/` 目录（生产环境为 `D:/deploy/upload/`） |
| URL 访问规则 | `/upload/{yyyy}/{MM}/{uuid扩展名}`，如 `/upload/2026/07/abc123.jpg` |
| 目录结构 | 按年月分目录：`upload/2026/07/xxx.jpg` |
| 文件命名 | UUID 重命名，保留原扩展名 |
| 允许类型 | 图片：jpg/jpeg/png/gif；视频：mp4；文档：ppt/pptx/pdf |
| 大小限制 | 图片 5MB，视频 100MB，PPT 50MB（Nginx + Spring 双重限制） |
| 静态资源映射 | WebConfig 中 `/upload/**` → `file:upload/` |
| 清理策略 | 不自动清理（管理员手动管理） |

#### 5.3.1 登录认证流程

```
登录请求 → UserService 校验账号密码（BCrypt） → 生成 UUID Token
                                           ↓
                              Redis 存储：Key = "Token::{uuid}"
                              Value = 用户JSON + 角色权限，TTL = 600 分钟
                                           ↓
                              查询用户角色 → 查询角色菜单 → 组装 LoginUserVo
                                           ↓
                              返回 {user, roles, menus, token}
```

#### 5.3.2 AOP 操作日志切面

> **[红线修订]** 日志切面改用 `@Around`（非 `@AfterReturning`），覆盖异常场景——方法抛异常时也记录日志。

```java
@Aspect
@Component
public class LogAspect {
    @Autowired
    private LogService logService;

    // 自定义注解
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RecordLog {
        String value() default "";  // 操作描述
    }

    @Around("@annotation(logAnnotation)")
    public Object recordLog(ProceedingJoinPoint pjp, RecordLog logAnnotation) throws Throwable {
        LogEntity log = new LogEntity();
        log.setOperation(logAnnotation.value());
        log.setMethod(pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName());
        // [审查优化] 大文件炸弹防护：过滤 MultipartFile/HttpServletRequest 等特殊对象
        log.setParams(safeParamsToString(pjp.getArgs()));
        // 从 RequestContextHolder 获取 IP
        // 从 Token 获取当前用户

        Object result;
        try {
            result = pjp.proceed();
            log.setLogType(1); // 正常操作
            return result;
        } catch (Throwable e) {
            log.setLogType(3); // 异常日志
            log.setParams(log.getParams() + " | Exception: " + e.getMessage());
            throw e; // 重新抛出，交由全局异常处理器
        } finally {
            logService.save(log); // 无论成功失败都记录
        }
    }

    // [审查优化] 安全序列化参数：跳过 MultipartFile/HttpServletRequest/BindingResult 等特殊对象
    private String safeParamsToString(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (Object arg : args) {
            if (arg == null) {
                sb.append("null");
            } else if (arg instanceof MultipartFile
                    || arg instanceof HttpServletRequest
                    || arg instanceof HttpServletResponse
                    || arg instanceof BindingResult
                    || arg instanceof MultipartHttpServletRequest) {
                sb.append("[").append(arg.getClass().getSimpleName()).append("]");
            } else {
                sb.append(arg.toString());
            }
            sb.append(", ");
        }
        // 去掉末尾多余的分隔符
        if (sb.length() > 1) sb.setLength(sb.length() - 2);
        sb.append("]");
        // [审查优化] 截断超长参数，防止日志爆炸
        String result = sb.toString();
        return result.length() > 2000 ? result.substring(0, 2000) + "...[truncated]" : result;
    }
}
```

Controller 使用：
```java
@RecordLog("新增培训文章")
public CommonResult addArticle(@RequestBody ArticleDto dto) { ... }
```

#### 5.3.3 菜单权限控制

> **[审查修订]** 后端 AOP 切面除校验 Token 外，**增加角色校验**。

- 登录时根据用户角色查询关联菜单，返回前端动态渲染
- 后端 AOP 切面校验 Token 有效性 **+ 角色权限**：`/admin/*` 只允许管理员/培训师，`/mobile/*` 只允许护士学员
- 菜单级可见性由前端控制（哪些菜单项显示），接口级权限由后端控制（哪些接口可调）
- 移动端菜单固定（不动态渲染），仅校验 Token + 角色

#### 5.3.4 考试系统业务逻辑

> **[审查新增]** 原文档缺少考试系统关键业务逻辑定义，现补充。

**考试开始条件校验**：
1. 考试状态为"已发布"（status=1）
2. 当前时间在 start_time ~ end_time 范围内
3. 学员该考试已答题次数 < max_attempts
4. 以上条件全部满足才允许开始考试

**判分规则**：
| 题型 | 答案格式 | 判分规则 |
|------|---------|---------|
| 单选题 | 单字母如 "A" | 完全匹配得满分，否则 0 分 |
| 多选题 | 多字母如 "ABD" | 完全匹配得满分，**少选/多选/错选均 0 分**（不做部分给分） |
| 判断题 | "true" 或 "false" | 完全匹配得满分，否则 0 分 |

**补考逻辑**：
- 每次考试生成一条 `nursing_exam_record` 记录
- 补考不覆盖之前成绩，所有记录都保留
- 成绩查询时取**最高分**作为最终成绩
- 达到 max_attempts 后提示"已达最大考试次数"

**考试中途断网处理**：
- 已提交的答案保存在前端 localStorage（每题作答后自动保存）
- 重新进入考试时恢复已答题目
- 后端不保存中途状态（交卷时一次性提交所有答案）
- 考试时长以第一次进入考试的时间为起点计算

**防作弊措施（基础版）**：
- 试题顺序打乱（每个学员看到的题目顺序不同）
- 选项顺序不打乱（保证答案有效性）
- 禁止复制粘贴（前端 CSS + JS 控制）

**[红线] 防重复交卷**：
- 考试交卷接口 `/mobile/exam/submit` 必须加 **Redis 分布式锁**，锁 Key = `ExamSubmit::{examId}::{userId}`，TTL = 60 秒
- 同时在 `nursing_exam_record` 表上建立 **联合唯一索引** `uk_exam_user_attempt(exam_id, user_id, attempt_count)`，数据库层面兜底
- 交卷流程：获取 Redis 锁 → 查询已考次数 → 判断是否超过 max_attempts → 写入记录（唯一索引兜底）→ 释放锁
- 如获取锁失败，返回 `CommonResult.error(907, "正在提交中，请勿重复操作")`

---

## 六、PC 前端架构设计

### 6.1 目录结构

```
smart-nursing-admin/                         # PC 管理端
├── .env.development                         # 开发环境变量
├── .env.production                          # 生产环境变量
├── .eslintrc.cjs                            # ESLint 配置
├── .prettierrc                              # Prettier 配置
├── .vscode/
│   ├── extensions.json
│   └── settings.json
├── public/
│   └── favicon.ico
├── src/
│   ├── api/                                 # API 接口层（按模块拆分）
│   │   ├── auth.js                          # 认证接口
│   │   ├── user.js                          # 用户管理
│   │   ├── role.js                          # 角色管理
│   │   ├── menu.js                          # 菜单管理
│   │   ├── log.js                           # 日志管理
│   │   ├── category.js                      # 类别管理
│   │   ├── tag.js                           # 标签管理
│   │   ├── article.js                       # 文章管理
│   │   ├── video.js                         # 视频管理
│   │   ├── ppt.js                           # PPT管理
│   │   ├── exam.js                          # 考试管理
│   │   ├── question.js                      # 试题管理
│   │   ├── examRecord.js                    # 成绩管理
│   │   ├── learning.js                      # 学习记录
│   │   ├── dashboard.js                     # 统计数据
│   │   └── upload.js                        # 文件上传
│   ├── assets/
│   │   ├── base.css
│   │   └── main.css
│   ├── components/                          # 公共组件
│   │   ├── ImageUpload.vue                  # 图片上传组件
│   │   ├── FileUpload.vue                   # 文件上传组件
│   │   ├── RichTextEditor.vue               # 富文本编辑器
│   │   ├── TagSelect.vue                    # 标签选择器
│   │   └── CategorySelect.vue               # 类别选择器（树形）
│   ├── router/
│   │   └── index.js
│   ├── stores/                              # Pinia 状态管理
│   │   ├── user.js                          # 用户状态
│   │   └── app.js                           # 应用状态
│   ├── utils/
│   │   ├── request.js                       # axios 封装
│   │   └── format.js                        # 格式化工具
│   ├── views/
│   │   ├── LoginView.vue                    # 登录页
│   │   ├── HomeView.vue                     # 主页布局（菜单+路由出口）
│   │   ├── DashboardView.vue                # 首页统计（Echarts）
│   │   ├── user/
│   │   │   └── UserView.vue                 # 用户管理
│   │   ├── role/
│   │   │   ├── RoleView.vue                 # 角色管理
│   │   │   └── AssignMenu.vue               # 分配菜单弹窗
│   │   ├── menu/
│   │   │   └── MenuView.vue                 # 菜单管理
│   │   ├── log/
│   │   │   └── LogView.vue                  # 日志管理
│   │   ├── category/
│   │   │   └── CategoryView.vue             # 类别管理（树形）
│   │   ├── tag/
│   │   │   └── TagView.vue                  # 标签管理
│   │   ├── article/
│   │   │   ├── ArticleList.vue              # 文章列表
│   │   │   └── ArticleEdit.vue              # 文章编辑（富文本）
│   │   ├── video/
│   │   │   ├── VideoList.vue                # 视频列表
│   │   │   └── VideoEdit.vue                # 视频编辑
│   │   ├── ppt/
│   │   │   ├── PptList.vue                  # PPT列表
│   │   │   └── PptEdit.vue                  # PPT编辑
│   │   ├── exam/
│   │   │   ├── ExamList.vue                 # 考试列表
│   │   │   ├── ExamEdit.vue                 # 考试编辑+组卷
│   │   │   └── QuestionBank.vue             # 试题库管理
│   │   ├── examRecord/
│   │   │   ├── RecordList.vue               # 成绩列表
│   │   │   └── RecordDetail.vue             # 成绩详情
│   │   ├── learning/
│   │   │   └── LearningRecord.vue           # 学习记录
│   │   └── profile/
│   │       └── ProfileView.vue              # 个人中心
│   ├── App.vue
│   └── main.js
├── index.html
├── jsconfig.json
├── package.json
└── vite.config.js                           # 配置 Proxy 代理
```

### 6.2 路由配置

> **[红线修订]** PC 端路由必须改为**基于菜单的动态路由**：登录后根据用户角色获取菜单列表，通过 `router.addRoute()` 动态注册路由，废弃静态路由表。

```javascript
// router/index.js — 只定义基础路由，业务路由动态注入
const routes = [
  { path: '/', name: 'login', component: LoginView, meta: { name: '登录' } },
  {
    path: '/home', name: 'home', component: HomeView, meta: { name: '主页' },
    children: [
      { path: '/dashboard', name: 'dashboard', component: DashboardView, meta: { name: '首页统计' } },
      // 其他业务路由登录后通过 router.addRoute() 动态注入
    ]
  }
]

// 登录成功后动态注册路由
async function initDynamicRoutes(menuList) {
  menuList.forEach(menu => {
    if (menu.path && menu.component) {
      router.addRoute('home', {
        path: menu.path,
        name: menu.name,
        component: () => import(`@/views/${menu.component}.vue`),
        meta: { name: menu.title }
      })
    }
  })
}
```

### 6.3 Vite 配置

```javascript
import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8888',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

### 6.4 环境变量

```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8888
VITE_APP_TITLE=智慧护理培训系统（管理端）

# .env.production
VITE_API_BASE_URL=/api
VITE_APP_TITLE=智慧护理培训系统
```

---

## 七、移动端架构设计

### 7.1 技术方案

使用 **uni-app** 框架（HBuilderX 开发），一套代码同时编译为：
- H5 响应式页面（手机浏览器 / 微信内置浏览器直接访问）

### 7.2 目录结构

```
smart-nursing-mobile/                        # 移动端 uni-app 工程
├── pages.json                               # 页面路由配置（uni-app 规范）
├── manifest.json                            # 应用配置（H5 发布信息）
├── App.vue                                  # 根组件
├── main.js                                  # 入口
├── uni.scss                                 # 全局样式变量
├── static/                                  # 静态资源
│   ├── images/
│   │   ├── logo.png
│   │   └── tabbar/                          # 底部导航栏图标
│   │       ├── learn.png
│   │       ├── learn-active.png
│   │       ├── exam.png
│   │       ├── exam-active.png
│   │       ├── profile.png
│   │       └── profile-active.png
├── api/
│   ├── request.js                           # uni.request 封装（Token 拦截）
│   ├── auth.js                              # 登录/退出
│   ├── learn.js                             # 学习中心接口
│   ├── exam.js                              # 考试中心接口
│   └── user.js                              # 个人中心接口
├── store/
│   └── user.js                              # 用户状态（Pinia for uni-app）
├── utils/
│   ├── auth.js                              # Token 存储工具
│   └── format.js                            # 格式化工具
├── components/
│   ├── ArticleCard.vue                      # 文章卡片
│   ├── VideoCard.vue                        # 视频卡片
│   ├── PptCard.vue                          # PPT卡片
│   ├── CategoryTabs.vue                     # 分类标签栏
│   ├── ProgressBar.vue                      # 进度条组件
│   └── EmptyState.vue                       # 空状态组件
└── pages/
    ├── login/
    │   └── login.vue                        # 登录页
    ├── learn/                               # 学习中心 Tab
    │   ├── index.vue                        # 学习首页（分类+内容列表）
    │   ├── article-detail.vue               # 文章阅读
    │   ├── video-detail.vue                 # 视频播放
    │   └── ppt-detail.vue                   # PPT查看
    ├── exam/                                # 考试中心 Tab
    │   ├── index.vue                        # 考试列表
    │   ├── exam-paper.vue                   # 答题页面
    │   └── exam-result.vue                  # 成绩与解析
    ├── profile/                             # 个人中心 Tab
    │   ├── index.vue                        # 个人中心首页
    │   ├── my-progress.vue                  # 我的学习进度
    │   ├── my-records.vue                   # 我的学习记录
    │   ├── my-exams.vue                     # 我的考试记录
    │   ├── edit-profile.vue                 # 修改个人信息
    │   └── change-password.vue              # 修改密码
    └── study-plan/                          # 学习计划
        └── index.vue                        # 学习计划列表
```

### 7.3 pages.json 配置（页面路由 + 底部 Tab）

```json
{
  "pages": [
    { "path": "pages/login/login", "style": { "navigationStyle": "custom" } },
    { "path": "pages/learn/index", "style": { "navigationBarTitleText": "学习中心" } },
    { "path": "pages/learn/article-detail", "style": { "navigationBarTitleText": "文章阅读" } },
    { "path": "pages/learn/video-detail", "style": { "navigationBarTitleText": "视频播放" } },
    { "path": "pages/learn/ppt-detail", "style": { "navigationBarTitleText": "PPT查看" } },
    { "path": "pages/exam/index", "style": { "navigationBarTitleText": "考试中心" } },
    { "path": "pages/exam/exam-paper", "style": { "navigationBarTitleText": "答题", "navigationStyle": "custom" } },
    { "path": "pages/exam/exam-result", "style": { "navigationBarTitleText": "考试结果" } },
    { "path": "pages/profile/index", "style": { "navigationBarTitleText": "我的" } },
    { "path": "pages/profile/my-progress", "style": { "navigationBarTitleText": "学习进度" } },
    { "path": "pages/profile/my-records", "style": { "navigationBarTitleText": "学习记录" } },
    { "path": "pages/profile/my-exams", "style": { "navigationBarTitleText": "考试记录" } },
    { "path": "pages/profile/edit-profile", "style": { "navigationBarTitleText": "修改信息" } },
    { "path": "pages/profile/change-password", "style": { "navigationBarTitleText": "修改密码" } },
    { "path": "pages/study-plan/index", "style": { "navigationBarTitleText": "学习计划" } }
  ],
  "tabBar": {
    "color": "#999999",
    "selectedColor": "#409eff",
    "borderStyle": "black",
    "backgroundColor": "#ffffff",
    "list": [
      { "pagePath": "pages/learn/index", "text": "学习", "iconPath": "static/images/tabbar/learn.png", "selectedIconPath": "static/images/tabbar/learn-active.png" },
      { "pagePath": "pages/exam/index", "text": "考试", "iconPath": "static/images/tabbar/exam.png", "selectedIconPath": "static/images/tabbar/exam-active.png" },
      { "pagePath": "pages/profile/index", "text": "我的", "iconPath": "static/images/tabbar/profile.png", "selectedIconPath": "static/images/tabbar/profile-active.png" }
    ]
  },
  "globalStyle": {
    "navigationBarBackgroundColor": "#409eff",
    "navigationBarTextStyle": "white",
    "navigationBarTitleText": "智慧护理培训",
    "backgroundColor": "#f8f8f8"
  }
}
```

### 7.3.1 H5 部署路径配置

> **[审查优化]** 移动端 H5 部署在 Nginx 的 `/m/` 路径下，必须在 `manifest.json` 中配置 H5 路由基础路径，否则静态资源 404 白屏。

**manifest.json H5 配置**：
```json
{
  "h5": {
    "router": {
      "mode": "history",
      "base": "/m/"
    },
    "publicPath": "/m/",
    "template": "index.html"
  }
}
```

> **说明**：`base` 和 `publicPath` 都设为 `/m/`，确保 HBuilderX 编译后所有 JS/CSS/图片引用路径都带 `/m/` 前缀，与 Nginx `location /m/` 对应。

### 7.4 移动端请求封装（api/request.js）

> **[审查修复]** BASE_URL 改为条件编译区分开发/生产环境；401 拦截器增加全局防抖锁防止并发重复跳转。

```javascript
// [审查修复] 条件编译区分环境，废弃硬编码
// #ifdef H5
const BASE_URL = import.meta.env.VITE_API_BASE_URL || window.location.origin + '/api'
// #endif
// #ifndef H5
const BASE_URL = 'http://localhost:8888'  // App/小程序环境（打包时替换为生产地址）
// #endif

// [审查修复] 401 全局防抖锁，防止并发请求重复跳转
let isRedirecting = false

export const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('Token')
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Token': token || ''
      },
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data)
        } else if (res.data.code === 401) {
          // [审查修复] 防抖锁：并发请求只跳转一次
          if (!isRedirecting) {
            isRedirecting = true
            uni.showToast({ title: '登录已失效，请重新登录', icon: 'none' })
            uni.removeStorageSync('Token')
            uni.reLaunch({
              url: '/pages/login/login',
              complete: () => { isRedirecting = false }
            })
          }
          reject(res.data)
        } else {
          uni.showToast({ title: res.data.msg || '请求失败', icon: 'none' })
          reject(res.data)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络异常', icon: 'none' })
        reject(err)
      }
    })
  })
}
```

### 7.5 移动端页面设计要点

**学习中心**（pages/learn/index.vue）：
- 顶部分类 Tab 横向滚动（CategoryTabs 组件）
- 内容列表支持下拉刷新 + 上拉加载更多
- 文章/视频/PPT 三种内容类型混合展示，卡片样式区分
- 搜索栏支持关键词搜索

**文章阅读**（pages/learn/article-detail.vue）：
- **[审查修复]** 使用 `mp-html` 插件渲染富文本（替代 `rich-text`，后者不支持复杂 CSS/视频/懒加载）
- 底部显示学习进度条
- 支持收藏功能

**视频播放**（pages/learn/video-detail.vue）：
- video 组件播放视频
- **[审查修复]** 防刷逻辑：前端每 15 秒上报一次播放进度，后端校验 `currentTime` 必须递增（拒绝回退或跳跃），`study_duration` 增量不超过 15 秒
- 上报接口：`POST /mobile/learn/progress`，参数 `{contentId, currentTime, studyDuration}`
- 后端使用乐观锁更新 `nursing_learning_record`，防止并发覆盖
- 支持全屏播放

**答题页面**（pages/exam/exam-paper.vue）：
- 单选题/多选题/判断题三种题型
- 顶部倒计时显示
- 底部答题卡导航
- 支持标记题目（待复查）
- 交卷前确认弹窗

---

## 八、安全与规范设计

### 8.1 认证与权限

> **[审查修订]** Token 机制由固定过期改为**滑动续期**；接口权限由仅校验登录升级为**增加角色校验**。

| 维度 | 约定 |
|------|------|
| Token 格式 | UUID 字符串 |
| Token 存储 | Redis，Key = `Token::{uuid}`，TTL = 600 分钟 |
| Token 续期 | **滑动续期**——每次请求自动刷新 TTL 为 600 分钟 |
| Token 传递 | HTTP Header `Token` 字段 |
| 白名单 | `@NoToken` 注解标注的方法跳过校验（如 `/login`） |
| 接口权限 | **角色校验**：`/admin/*` 只允许管理员/培训师访问，`/mobile/*` 只允许护士学员访问；`/login`、`/common/*`、`/data/*` 登录即可 |
| PC 端权限 | 登录返回菜单列表，前端动态渲染菜单控制可见性 |
| 移动端权限 | 登录后获取 Token，所有接口需携带 Token |
| 密码加密 | BCryptPasswordEncoder（Spring Security Crypto，**强制废弃 MD5**） |

### 8.2 统一返回结果

```java
CommonResult<T> {
    Integer code;      // 错误码（200=成功）
    T data;            // 返回数据
    Long total;        // 分页总数
    String msg;        // 提示信息
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
| 500 | INTERNAL_SERVER_ERROR | 服务器内部错误 |
| 501 | NOT_IMPLEMENTED | 功能未实现 |
| 550 | UPDATE_ERROR | 更新失败 |
| 551 | ADD_ERROR | 新增失败 |
| 552 | DELETE_ERROR | 删除失败 |
| 902 | USER_EXIST | 用户名已存在 |
| 903 | CATEGORY_HAS_CHILDREN | 类别下有子类别，不可删除 |
| 904 | CATEGORY_HAS_CONTENT | 类别下有内容，不可删除 |
| 905 | TAG_IN_USE | 标签已被使用，不可删除 |
| 906 | EXAM_NOT_AVAILABLE | 考试未开始或已结束 |
| 907 | EXAM_ALREADY_SUBMITTED | 已交卷，不可重复提交 |
| 908 | QUESTION_TYPE_ERROR | 试题类型错误 |

### 8.4 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public CommonResult<?> businessExceptionHandler(BusinessException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    // [红线] 补齐参数校验异常捕获：@RequestBody + @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> validationExceptionHandler(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", msg);
        return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), msg);
    }

    // [审查优化] 补齐参数校验异常：@RequestParam + @Validated
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", msg);
        return CommonResult.error(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), msg);
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<?> exceptionHandler(Exception e) {
        // [红线] 禁止 e.printStackTrace()，改用 log.error() 带异常堆栈
        log.error("系统异常: ", e);
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
    }
}
```

---

## 九、部署架构设计

### 9.1 Windows 生产环境部署

```
Windows Server 2016+
├── Docker Desktop / Docker Engine
│   ├── Nginx 容器（端口 80）— [红线] 容器化部署
│   │   ├── 静态资源：PC 前端 dist/（挂载卷）
│   │   ├── 反向代理：/api/ → http://host.docker.internal:8888
│   │   ├── 移动端 H5：/m/ → 移动端 dist/build/h5/（挂载卷）
│   │   └── 上传文件：/upload/ → http://host.docker.internal:8888
│   └── Redis 容器（端口 6379）— [红线] 容器化部署
│       └── 数据持久化：挂载卷 redis-data
├── Spring Boot 应用（端口 8888）— 宿主机直接运行
│   ├── JDK 17 运行环境
│   └── jar 包方式启动（winsw 注册为 Windows 服务）
├── MySQL 8.0（端口 3306）— 宿主机直接运行
└── 移动端发布
    └── 移动端 H5：HBuilderX 编译发布 H5 到 Nginx 挂载卷
```

> **[红线修订]** Nginx 和 Redis 强制通过 Docker 容器化部署，Spring Boot 和 MySQL 在宿主机直接运行（避免 Docker 网络带来的数据库连接复杂度）。提供 `docker-compose.yml` 一键启动 Nginx + Redis。

### 9.2 Nginx 配置

```nginx
server {
    listen 80;
    server_name nursing.example.com;

    # PC 管理端前端 — [审查修复] 改为容器内路径
    location / {
        root /usr/share/nginx/html/admin;
        try_files $uri $uri/ /index.html;
    }

    # 移动端 H5 — [审查修复] 改为容器内路径
    location /m/ {
        alias /usr/share/nginx/html/mobile/;
        try_files $uri $uri/ /m/index.html;
    }

    # 后端 API 反向代理（前端 axios baseURL = /api，Nginx 去掉 /api 前缀转发到后端）
    # [红线] Docker 容器内不能用 localhost，用 host.docker.internal 访问宿主机
    location /api/ {
        proxy_pass http://host.docker.internal:8888/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_read_timeout 300s;
    }

    # [审查补全] 上传文件代理到后端（后端 WebConfig 映射 /upload/** → file:upload/）
    location /upload/ {
        proxy_pass http://host.docker.internal:8888;
        proxy_set_header Host $host;
    }

    # 文件上传大小限制
    client_max_body_size 100m;

    # Gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;
    gzip_min_length 1000;
}
```

> **[审查补全]** 前端 API 调用约定：PC 端和移动端的 axios/request.js 中 `baseURL` 统一设为 `/api`，所有 API 请求走 `/api/xxx`，Nginx 代理时去掉 `/api` 前缀后转发到后端 8888 端口。上传文件访问走 `/upload/xxx`，由 Nginx 代理到后端静态资源映射。

### 9.3 Spring Boot 启动脚本（Windows）

```bat
@echo off
cd D:\deploy\smart-nursing-backend
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%

REM [红线] API Key 严禁明文写死在脚本或 yml 中，必须通过系统环境变量注入
REM 在 Windows 系统环境变量中设置 DASHSCOPE_API_KEY（不在此脚本中写明）

REM [审查修复] JVM 内存调大：SpringAI 流式问答需要更多内存
java -Xms512m -Xmx1024m -jar smart-nursing-backend.jar --spring.profiles.active=prod
```

注册为 Windows 服务：使用 `winsw` 工具将 jar 包注册为系统服务，开机自启。

### 9.4 Docker 容器化部署（Nginx + Redis）

> **[红线修订]** Nginx 和 Redis 强制通过 Docker 容器化部署。提供 `docker-compose.yml` 一键启动。

**docker-compose.yml**：
```yaml
version: '3.8'
services:
  nginx:
    image: nginx:1.27
    container_name: smart-nursing-nginx
    ports:
      - "80:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/conf.d/default.conf
      - D:/deploy/smart-nursing-admin/dist:/usr/share/nginx/html/admin
      - D:/deploy/smart-nursing-mobile/dist/build/h5:/usr/share/nginx/html/mobile
    depends_on:
      - redis
    restart: always

  redis:
    image: redis:7-alpine
    container_name: smart-nursing-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes
    restart: always

volumes:
  redis-data:
```

**启动命令**：
```powershell
# 启动 Nginx + Redis 容器
docker compose up -d

# 查看容器状态
docker compose ps

# 查看日志
docker compose logs -f nginx
docker compose logs -f redis

# 停止
docker compose down
```

> **注意**：Spring Boot 应用和 MySQL 在宿主机直接运行，不在 Docker 中。Nginx 容器通过 `host.docker.internal` 访问宿主机的 8888 端口（后端 API）。Redis 容器端口 6379 映射到宿主机，后端通过 `localhost:6379` 连接 Redis。

---

## 十、开发流程与工具链集成

### 10.1 项目计划阶段

**工具：Microsoft Project 2013**

| 阶段 | 工期 | 产出 |
|------|------|------|
| 需求分析与设计 | 1 周 | 需求文档 + 架构图 + ER 图 |
| Phase 1：基础骨架 | 1 周 | 后端框架 + 前端框架 + 数据库 |
| Phase 2：系统管理 | 1 周 | 认证 + 用户 + 角色 + 菜单 + 日志 |
| Phase 3：内容管理 | 2 周 | 类别 + 标签 + 文章 + 视频 + PPT |
| Phase 4：考试系统 | 1 周 | 试题 + 考试 + 成绩 |
| Phase 5：学习系统 | 1 周 | 学习记录 + 进度跟踪 + 统计 |
| Phase 6：移动端 | 2 周 | uni-app 学习 + 考试 + 个人中心 |
| Phase 7：测试部署 | 1 周 | 测试 + 性能压测 + 部署 |

产出 MS Project 甘特图，分配任务到人。

### 10.2 设计阶段

**工具：Visio 2013 + PowerDesigner 16.5**

| 设计物 | 工具 | 内容 |
|--------|------|------|
| 系统架构图 | Visio | 三端架构图、部署架构图 |
| 业务流程图 | Visio | 登录流程、考试流程、学习流程 |
| ER 图 | Visio / PowerDesigner | 实体关系图 |
| 数据库 PDM | PowerDesigner | 物理数据模型 → 导出建表 SQL |
| 接口文档 | Swagger UI（自动生成） | SpringDoc OpenAPI 3 |

### 10.3 开发阶段

**Git 分支策略**：

```
main          ← 生产分支，只从 develop 合并
  └── develop ← 开发主分支
       ├── feature/user-management     ← 用户管理功能
       ├── feature/article-management  ← 文章管理功能
       ├── feature/exam-system         ← 考试系统
       └── feature/mobile-app          ← 移动端
```

**三端并行开发**：

| 开发者 | IDE | 任务 |
|--------|-----|------|
| 后端开发 | IntelliJ IDEA 2025.2.4 | Spring Boot 后端 API |
| PC 前端开发 | VS Code | Vue3 管理端 |
| 移动端开发 | HBuilderX | uni-app 移动端 |

### 10.4 测试阶段

**工具：JMeter + 禅道/Jira**

| 测试类型 | 工具 | 内容 |
|---------|------|------|
| 功能测试 | 禅道/Jira | 测试用例编写、执行、缺陷跟踪 |
| 接口测试 | Postman / Swagger UI | API 接口逐个验证 |
| 性能测试 | JMeter | 关键接口并发压测（登录、考试提交、学习记录） |
| 兼容性测试 | 浏览器 | PC 端浏览器兼容 + 移动端 H5 手机浏览器/微信浏览器 |

**JMeter 压测场景**：
- 登录接口：100 并发用户，持续 10 分钟
- 考试提交接口：50 并发用户同时交卷
- 学习记录上报：200 并发用户持续学习

### 10.5 部署阶段

**工具：SQLyog/Navicat + Nginx + Windows**

| 步骤 | 操作 |
|------|------|
| 1. 数据库部署 | SQLyog/Navicat 执行 `init.sql` 建表 + 初始数据导入 |
| 2. Redis 部署 | Windows 服务方式启动 Redis |
| 3. 后端部署 | `java -jar` 启动 + winsw 注册服务 |
| 4. PC 前端部署 | `npm run build` → dist 部署到 Nginx |
| 5. 移动端部署 | HBuilderX 编译 H5 → 部署到 Nginx `/m/` 路径 |
| 6. Nginx 配置 | 配置反向代理 + 静态资源 + Gzip |

---

## 十一、实施计划与分期

> **[审查修订]** 开发方式由"全端并行"调整为**按端横向推进**（后端全部完成 → PC 管理端 → 移动端），新增 AI 功能为独立阶段。

### 后端阶段

#### 后端 Phase 1：基础骨架 + 数据库

| 任务 | 产出 |
|------|------|
| 数据库建模 | `sql/init.sql`（21 张表 + 种子数据） |
| 后端 Maven 工程 | pom.xml + 启动类 + application.yml + logback-spring.xml |
| 后端公共模块 | CommonResult / ErrorCode / GlobalExceptionHandler / AroundCut（含角色校验+滑动续期）/ NoToken / LogAspect / WebConfig / MybatisPlusConfig / SpringDocConfig / RedisConfig / SecurityUtils |

#### 后端 Phase 2：系统管理 + 认证

| 任务 | 产出 |
|------|------|
| 登录/退出 | LoginController + UserServiceImpl + LoginUserVo + Token Redis 存储 |
| 用户管理 CRUD | UserController + UserDto + UserService + UserMapper |
| 角色管理 | RoleController + RoleServiceImpl + 角色-菜单分配 |
| 菜单管理 | MenuController + MenuServiceImpl + 菜单树查询 |
| 日志管理 | LogController + LogAspect AOP + LogServiceImpl |

#### 后端 Phase 3：培训内容管理

| 任务 | 产出 |
|------|------|
| 类别管理（树形） | CategoryController + CategoryTreeVo |
| 标签管理 | TagController + 标签关联逻辑 |
| 文章管理 | ArticleController + ArticleVo + 标签关联 |
| 视频管理 | VideoController + VideoVo |
| PPT管理 | PptController + PptVo |
| 文件上传 | UploadController |

#### 后端 Phase 4：考试系统

| 任务 | 产出 |
|------|------|
| 试题管理 | QuestionController + QuestionDto |
| 考试管理 | ExamController + ExamDetailVo（含 max_attempts 补考配置） |
| 成绩管理 | ExamRecordController + ExamResultVo |
| 考试逻辑 | 自动判分（单选/多选/判断）、及格判定、补考次数校验 |

#### 后端 Phase 5：学习系统 + 统计

| 任务 | 产出 |
|------|------|
| 学习记录 | LearningController + LearningServiceImpl |
| 学习计划 | StudyPlanController + StudyPlanServiceImpl |
| 收藏功能 | FavoriteController + FavoriteEntity |
| 进度跟踪 | LearningProgressVo + 进度计算逻辑 |
| 首页统计 | DataCountController + 4 项统计 + 类别分布 + 学习趋势 + 考试及格率 |

#### 后端 Phase 6：AI 功能模块

| 任务 | 产出 |
|------|------|
| AI 护理知识问答助手 | AIController + ChatModelService（DashScope 流式 SSE） |
| AI 自动生成试题 | AIController + QuestionGenerationService |
| AI 个性化学习推荐 | RecommendService（基于学习记录动态计算） |
| AI 文生图 | ImageGenerationService（DashScope 原生 SDK） |

### PC 管理端阶段

#### PC Phase 1：基础骨架 + 登录 + 主页

| 任务 | 产出 |
|------|------|
| Vite 工程 | package.json + vite.config.js + .env + main.js + App.vue |
| 公共模块 | request.js / Pinia stores / router 框架 / base.css |
| 登录页 | LoginView.vue |
| 主页布局 | HomeView.vue（菜单动态渲染 + 路由出口） |

#### PC Phase 2：系统管理页面

| 任务 | 产出 |
|------|------|
| 用户/角色/菜单/日志 | UserView / RoleView / MenuView / LogView |

#### PC Phase 3：内容管理页面

| 任务 | 产出 |
|------|------|
| 类别/标签 | CategoryView / TagView + TagSelect / CategorySelect 组件 |
| 文章 | ArticleList + ArticleEdit + RichTextEditor |
| 视频/PPT | VideoList/Edit + PptList/Edit + FileUpload 组件 |

#### PC Phase 4：考试管理页面

| 任务 | 产出 |
|------|------|
| 考试/试题/成绩 | ExamList + ExamEdit + QuestionBank + RecordList/Detail |

#### PC Phase 5：学习 + 统计 + AI 页面

| 任务 | 产出 |
|------|------|
| 统计仪表盘 | DashboardView + Echarts 图表 |
| 学习记录 | LearningRecord |
| AI 功能页 | AiView（问答对话 + 生成试题 + 文生图） |

### 移动端阶段

#### 移动端 Phase 1：基础骨架 + 登录

| 任务 | 产出 |
|------|------|
| uni-app 工程 | HBuilderX 创建项目 + pages.json + manifest.json + request.js |
| 登录页 | pages/login/login.vue |

#### 移动端 Phase 2：学习中心

| 任务 | 产出 |
|------|------|
| 学习首页 | pages/learn/index.vue（分类 Tab + 内容列表） |
| 内容详情 | article-detail / video-detail / ppt-detail |
| 收藏功能 | 收藏/取消收藏 |
| H5 适配 | rpx/rem 响应式 + 媒体查询 |

#### 移动端 Phase 3：考试中心

| 任务 | 产出 |
|------|------|
| 考试列表 | pages/exam/index.vue |
| 答题页面 | exam-paper.vue（倒计时 + 答题卡 + 交卷） |
| 考试结果 | exam-result.vue（成绩 + 解析） |

#### 移动端 Phase 4：个人中心 + AI

| 任务 | 产出 |
|------|------|
| 个人中心 | profile/index + edit-profile + change-password |
| 学习进度 | my-progress + my-records + my-exams |
| AI 问答 | AI 护理知识问答助手（移动端） |

### 测试与部署阶段

| 任务 | 产出 |
|------|------|
| 功能测试 | 测试用例 + 缺陷修复 |
| 性能测试 | **实际压测**：登录 100 并发、考试提交 50 并发 |
| Nginx 部署 | Windows 生产环境部署 + Nginx 配置 |
| 文档整理 | README + Swagger 文档 + 部署手册 |

---

## 十二、工程目录总览

```
d:\practice\智慧护理培训系统\
├── smart-nursing-backend/              # 后端工程（IDEA 开发）
│   ├── pom.xml
│   ├── src/
│   │   ├── main/java/com/smart/nursing/
│   │   └── main/resources/
│   │       ├── application.yml
│   │       ├── application-local.yml.example
│   │       ├── logback-spring.xml
│   │       └── mapper/                 # MyBatis XML（复杂查询）
│   └── src/test/
├── smart-nursing-admin/                # PC 管理端（VS Code 开发）
│   ├── package.json
│   ├── vite.config.js
│   ├── .env.development
│   ├── .env.production
│   └── src/
├── smart-nursing-mobile/               # 移动端（HBuilderX 开发）
│   ├── pages.json
│   ├── manifest.json
│   ├── App.vue
│   ├── main.js
│   └── pages/
├── sql/
│   └── init.sql                        # 数据库初始化脚本（PowerDesigner 导出）
├── docs/                               # 项目文档
│   ├── 需求文档.md
│   ├── 架构设计文档.md
│   ├── 数据库设计文档.md
│   ├── 接口文档.md                     # Swagger 导出
│   └── 部署手册.md
├── design/                             # 设计产出物
│   ├── 系统架构图.vsdx                 # Visio
│   ├── 业务流程图.vsdx                 # Visio
│   ├── ER图.vsdx                       # Visio
│   ├── 数据库PDM.pdm                   # PowerDesigner
│   └── 项目计划.mpp                    # MS Project
├── nginx/
│   └── nginx.conf                      # Nginx 配置文件
├── deploy/
│   ├── startup.bat                     # Windows 启动脚本
│   └── smart-nursing-service.xml       # winsw 服务配置
├── .gitignore
└── README.md
```

---

## 十三、工程标准全文（基于 requirement.md）

> 以下为本系统遵循的完整工程标准，涵盖后端架构、前端架构、数据层、数据库设计、API 设计、认证机制、异常处理、配置文件、命名规范、编码约定、联调约定、日志规范、依赖锁定、目录模板、SpringAI 集成及团队环境配置共 16 个子节。所有代码开发严格按此标准执行。

### 13.1 后端工程架构标准（Spring Boot）

#### 13.1.1 包结构

根包：`com.smart.nursing`

```
com.smart.nursing
├── SmartNursingApplication.java          # 启动类（@MapperScan 标注于此）
├── controller/                            # 控制层
│   ├── common/                            # 公共接口（登录/上传/类别树）
│   ├── admin/                             # PC 管理端接口
│   └── mobile/                            # 移动端接口
├── service/                               # 业务逻辑层
│   ├── IXxxService.java                   # 接口（继承 IService<Entity>）
│   └── impl/
│       └── XxxServiceImpl.java            # 实现（继承 ServiceImpl<Mapper, Entity>）
├── dao/                                   # 数据访问层（包名为 dao）
│   └── XxxMapper.java                     # Mapper 接口（继承 BaseMapper<Entity>）
├── entity/                                # 实体层（数据库映射）
│   └── XxxEntity.java
├── dto/                                   # 数据传输对象
│   └── XxxDto.java                        # 继承 Entity，追加分页参数等
├── vo/                                    # 视图对象
│   └── XxxVo.java                         # 封装返回数据
├── aop/                                   # AOP 切面
│   ├── AroundCut.java                     # Token 校验环绕切面
│   ├── NoToken.java                       # 免 Token 注解
│   └── LogAspect.java                     # 操作日志切面
└── common/                                # 公共模块
    ├── config/
    │   ├── SpringDocConfig.java           # Swagger/OpenAPI 配置
    │   ├── WebConfig.java                 # 跨域 CORS 配置
    │   ├── MybatisPlusConfig.java         # MyBatis-Plus 配置 + @MapperScan
    │   └── RedisConfig.java               # Redis 序列化配置
    ├── exception/
    │   ├── GlobalExceptionHandler.java    # 全局异常处理器
    │   ├── BusinessException.java          # 业务异常
    │   └── AiException.java               # AI 异常
    ├── result/
    │   ├── CommonResult.java              # 统一响应封装
    │   ├── ErrorCode.java                 # 错误码对象
    │   └── PageParam.java                 # 分页参数基类
    ├── enums/
    │   └── GlobalErrorCodeConstants.java   # 全局错误码常量
    └── utils/
        └── XxxUtils.java                  # 工具类
```

#### 13.1.2 分层职责与调用链

```
请求 → AOP切面(Token校验+角色校验) → Controller → Service → Mapper → MySQL
                                                                     ↓
响应 ← Controller(封装CommonResult) ← Service(返回业务数据/抛异常) ← Mapper ← MySQL
                                                                    ↓
                                                               Redis(缓存/Token)
```

| 层级 | 职责 | 关键约定 |
|------|------|----------|
| **Controller** | 接收 HTTP 请求，参数校验，调用 Service，**封装 CommonResult 返回** | `@RestController` + `@RequestMapping` + **`@GetMapping`/`@PostMapping`** + `@Tag` + `@Operation` |
| **Service** | 核心业务逻辑，事务控制，数据组装。**[红线] 不返回 CommonResult**，只返回业务数据或抛 `BusinessException` | 接口 `IXxxService` 继承 `IService<Entity>`；实现继承 `ServiceImpl<Mapper, Entity>` |
| **DAO** | 数据库操作 | 包名统一用 `dao`（非 `mapper`），继承 `BaseMapper<Entity>`，靠 `@MapperScan` 扫描 |
| **Entity** | 数据库表映射 | `@Data` + `@ToString` + `@TableName` + `@TableId(type=IdType.ASSIGN_ID)` |
| **DTO** | 接收前端参数，可继承 Entity 追加分页等字段 | `XxxDto extends XxxEntity`，追加 `pageNo`/`pageSize` |
| **VO** | 封装返回前端的视图数据 | 如 `LoginUserVo`（用户 + 菜单 + token） |

#### 13.1.3 Controller 层规范

```java
@RestController
@RequestMapping("/admin/article")
@Tag(name = "文章管理", description = "护理培训文章管理相关接口")
public class ArticleController {

    @Autowired
    private IArticleService articleService;

    // [红线] 明确使用 @PostMapping，Service 只返回业务数据，Controller 封装 CommonResult
    @PostMapping("/add")
    @Operation(summary = "新增文章", description = "根据传入的文章信息新增一条记录")
    public CommonResult addArticle(@RequestBody @Valid ArticleDto dto) {
        articleService.addArticle(dto); // Service 不返回 CommonResult，失败抛 BusinessException
        return CommonResult.success();
    }

    @PostMapping("/list")
    @Operation(summary = "条件分页查询", description = "根据条件分页查询文章列表")
    public CommonResult listArticleByCondition(@RequestBody ArticleDto dto) {
        IPage<ArticleEntity> page = articleService.listArticleByCondition(dto);
        return CommonResult.successPageData(page);
    }

    @GetMapping("/getById/{articleId}")
    @Operation(summary = "按ID查询文章详情")
    public CommonResult getArticleById(@PathVariable Long articleId) {
        ArticleEntity article = articleService.getArticleById(articleId);
        return CommonResult.success(article);
    }
}
```

**规范要点**：
- 类注解：`@RestController` + `@RequestMapping("/模块名")` + `@Tag`
- 方法注解：**[红线] 明确使用 `@GetMapping` / `@PostMapping`**（废弃不区分的 `@RequestMapping`）+ `@Operation(summary=, description=)`
- 依赖注入：使用 `@Autowired` 注入 Service 接口
- 返回值：统一返回 `CommonResult<T>`
- 请求映射：**[红线] 明确使用 `@GetMapping`（查询）或 `@PostMapping`（增删改）**，路径风格为 `/模块/动作`
- 参数接收：JSON Body 用 `@RequestBody`，路径参数用 `@PathVariable`，**[红线] 参数校验用 `@Valid`**

#### 13.1.4 Service 层规范

> **[红线修订]** Service 层**不返回 CommonResult**，只返回业务数据或抛 `BusinessException`。Controller 负责封装 `CommonResult`。

```java
// 接口
public interface IArticleService extends IService<ArticleEntity> {
    void addArticle(ArticleDto dto);                    // 无返回值，失败抛异常
    IPage<ArticleEntity> listArticleByCondition(ArticleDto dto);  // 返回业务数据
    ArticleEntity getArticleById(Long articleId);       // 返回实体
}

// 实现
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ArticleEntity>
        implements IArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public void addArticle(ArticleDto dto) {
        // 业务校验 — [红线] 失败抛异常，不返回 CommonResult
        LambdaQueryWrapper<ArticleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleEntity::getTitle, dto.getTitle());
        if (articleMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(GlobalErrorCodeConstants.ARTICLE_EXIST);
        }
        // 插入 — [红线] 失败抛异常
        int result = articleMapper.insert(dto);
        if (result <= 0) {
            throw new BusinessException(GlobalErrorCodeConstants.ADD_ERROR);
        }
    }

    @Override
    public IPage<ArticleEntity> listArticleByCondition(ArticleDto dto) {
        LambdaQueryWrapper<ArticleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(dto.getTitle()), ArticleEntity::getTitle, dto.getTitle())
               .eq(dto.getCategoryId() != null, ArticleEntity::getCategoryId, dto.getCategoryId())
               .eq(ArticleEntity::getIsDelete, 0)
               .orderByDesc(ArticleEntity::getCreateTime);
        Page<ArticleEntity> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        return articleMapper.selectPage(page, wrapper);
    }
}
```

**规范要点**：
- 接口必须以 `I` 开头，继承 MyBatis-Plus 的 `IService<Entity>`
- 实现类继承 `ServiceImpl<XxxMapper, Entity>`，获得内置 CRUD
- **[红线] Service 层不返回 CommonResult**，只返回业务数据或抛 `BusinessException`
- **[红线] 废弃 FastJSON**，改用 Jackson 的 `ObjectMapper`/`JsonNode` 组装复杂数据
- 业务逻辑中充分利用 MyBatis-Plus 的 `LambdaQueryWrapper` 进行条件构造
- 分页查询使用 MyBatis-Plus 的 `Page<T>` 对象

#### 13.1.5 Entity 层规范

```java
@Data
@ToString
@TableName("nursing_article")
public class ArticleEntity implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)  // [红线] 雪花算法，废弃 AUTO
    private Long articleId;

    private String title;              // 驼峰自动映射 title
    private Long categoryId;           // 驼峰自动映射 category_id

    @TableField(value = "cover_image") // 显式映射（可选，驼峰可自动映射时省略）
    private String coverImage;

    private String content;
    private Integer viewCount;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    @TableLogic
    private Integer isDelete;
}
```

**规范要点**：
- 注解组合：`@Data` + `@ToString`（Lombok）+ `@TableName("表名")`
- 主键：`@TableId(type = IdType.ASSIGN_ID)` 雪花算法分配（**[红线] 废弃 AUTO 自增，防暴露业务量**）
- 字段映射：依赖驼峰自动映射（`map-underscore-to-camel-case: true`），特殊字段用 `@TableField(value="列名")`
- 逻辑删除字段：`@TableLogic`
- 所有实体 `implements Serializable`
- 主键/外键类型用 `Long`（包装类型），年龄用 `Integer`，日期用 `Date` 或 `String`

#### 13.1.6 DAO 层规范

```java
public interface ArticleMapper extends BaseMapper<ArticleEntity> {
    // 单表 CRUD 由 BaseMapper 提供，无需写任何方法
    // 仅复杂多表关联查询才自定义方法 + XML
}
```

**规范要点**：
- 包名统一为 `dao`（通过 `@MapperScan("com.smart.nursing.dao")` 扫描）
- 继承 `BaseMapper<Entity>`，获得全部单表 CRUD
- 仅有多表关联查询时才自定义方法，配合 `resources/mapper/XxxMapper.xml`
- 无需在接口上加 `@Mapper` 注解

#### 13.1.7 DTO / VO 规范

```java
// DTO：继承 Entity，追加查询参数
@Data
@ToString(callSuper = true)
public class ArticleDto extends ArticleEntity {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private List<Long> tagIds;  // 追加标签筛选
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

### 13.2 前端工程架构标准（Vue3）

#### 13.2.1 目录结构

```
smart-nursing-admin/
├── .env.development                         # 开发环境变量
├── .env.production                          # 生产环境变量
├── .eslintrc.cjs                            # ESLint 配置
├── .prettierrc                              # Prettier 配置
├── .vscode/
│   ├── extensions.json
│   └── settings.json
├── public/
│   └── favicon.ico
├── src/
│   ├── api/                        # API 接口层（按模块拆分）
│   │   ├── auth.js                 # 认证接口
│   │   ├── user.js                 # 用户管理
│   │   ├── article.js              # 文章管理
│   │   └── ...
│   ├── assets/                     # 静态资源
│   │   ├── base.css                # 基础样式（CSS 变量、reset）
│   │   └── main.css                # 主样式
│   ├── components/                 # 公共可复用组件
│   ├── router/                     # 路由配置
│   │   └── index.js               # createRouter + createWebHistory
│   ├── stores/                     # Pinia 状态管理
│   │   ├── user.js                 # 用户状态
│   │   └── app.js                  # 应用状态
│   ├── utils/                      # 工具类
│   │   ├── request.js              # axios 封装（实例 + 拦截器）
│   │   └── format.js               # 格式化工具
│   ├── views/                      # 页面级视图组件
│   ├── App.vue                     # 根组件（仅含 <RouterView>）
│   └── main.js                     # 入口（挂载 Vue、注册插件）
├── index.html
├── jsconfig.json                   # 路径别名 @ -> ./src
├── package.json
├── vite.config.js
└── package-lock.json
```

#### 13.2.2 各目录命名规范

| 目录 | 职责 | 命名规范 |
|------|------|----------|
| `api/` | 封装所有后端 HTTP 请求方法 | 文件名按业务域，方法名驼峰动词开头 |
| `utils/` | 通用工具类，与业务无关的纯函数 | 语义化命名 |
| `router/` | 路由配置，入口文件为 `index.js` | 统一 `index.js` |
| `views/` | 页面级路由组件 | 大驼峰 + `View` 后缀 |
| `components/` | 可复用公共组件 | 大驼峰命名 |
| `assets/` | 全局样式、图片等静态资源 | 小写连字符 |
| `stores/` | Pinia 状态管理 | 语义化命名 |

#### 13.2.3 应用入口（main.js）

```javascript
import './assets/base.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(router)
app.use(createPinia())
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
- Pinia 状态管理（替代 localStorage）
- `base.css` 在入口引入
- `App.vue` 仅含 `<RouterView>`，无布局逻辑

#### 13.2.4 API 请求封装（utils/request.js）

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import Router from '@/router'

const service = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8888',
    timeout: 1000000,
    crossDomain: true
})

// 请求拦截器：自动注入 Token
service.interceptors.request.use(
    config => {
        const userStore = useUserStore()
        const token = userStore.token
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
            // [红线] 废弃 return -1，改用 Promise.reject
            return Promise.reject(new Error(res.msg || '登录已失效'))
        } else {
            ElMessage.error(res.msg || '请求失败')
            // [红线] 废弃 return -1，改用 Promise.reject
            return Promise.reject(new Error(res.msg || '请求失败'))
        }
    },
    error => {
        ElMessage.error('网络请求异常')
        // [红线] 废弃 return -1，改用 Promise.reject
        return Promise.reject(error)
    }
)

export default service
```

**规范要点**：
- Token 注入到请求头 `Token` 字段（自定义 Header）
- 使用 `.env` 环境变量配置 baseURL（非硬编码）
- 使用 Pinia store 获取 Token（非 localStorage）
- 响应拦截器处理业务状态码：`200` 返回数据，`401` 跳转登录，其他 `Promise.reject`
- **[红线] 废弃 `return -1`**，改用 `Promise.reject`，调用方用 `try/catch` 或 `.catch()` 处理错误

#### 13.2.5 API 方法定义（api/模块名.js）

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
export const listArticleByConditionPage = (data) => {
    return request({ url: `/admin/article/list`, method: 'post', data })
}

// GET + 路径参数
export const getArticleById = (id) => {
    return request({ url: `/admin/article/getById/${id}`, method: 'get' })
}
```

**方法命名约定**：
- 动词开头：`login` / `logout` / `add` / `update` / `del` / `get` / `list`
- 业务实体跟随：`loginUser` / `addArticle` / `listArticleByConditionPage`

#### 13.2.6 路由配置（router/index.js）

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'
import DashboardView from '@/views/DashboardView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'login',
            component: LoginView,
            meta: { name: '登录页面' }
        },
        {
            path: '/home',
            name: 'home',
            component: HomeView,
            meta: { name: '主页' },
            children: [
                { path: '/dashboard', name: 'dashboard', component: DashboardView, meta: { name: '首页统计' } },
                { path: '/user', name: 'user', component: UserView, meta: { name: '用户管理' } },
                // ... 更多子路由
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
- **[红线] 业务路由基于菜单动态注册**：登录后调用后端获取菜单列表，通过 `router.addRoute()` 注入，不写死在路由表中

#### 13.2.7 状态管理（Pinia stores/user.js）

```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
    const token = ref('')
    const userInfo = ref(null)
    const menus = computed(() => userInfo.value?.menus || [])

    function setToken(newToken) {
        token.value = newToken
    }

    function setUserInfo(info) {
        userInfo.value = info
    }

    function clear() {
        token.value = ''
        userInfo.value = null
    }

    return { token, userInfo, menus, setToken, setUserInfo, clear }
})
```

#### 13.2.8 组件开发规范

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

#### 13.2.9 页面组件标准模式（CRUD 范例）

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

#### 13.2.10 菜单渲染规范

```vue
<el-menu :default-active="activeIndex" mode="horizontal" router>
    <el-menu-item v-for="(item, index) in userStore.menus"
                  :key="index" :index="item.path">
        <el-icon><component :is="item.icon"></component></el-icon>
        {{ item.title }}
    </el-menu-item>
</el-menu>
```

- `el-menu` 开启 `router` 属性，`index` 即路由 path，点击自动跳转
- 菜单图标通过 `<component :is="item.icon">` 动态渲染
- 菜单数据结构：`{ path, title, icon }`

#### 13.2.11 Echarts 图表规范

```javascript
import * as echarts from 'echarts'

const initEcharts = () => {
    // [审查修复] 废弃 res && res != -1，改用 .then().catch()
    getCategoryContentCount().then(res => {
        option.value.series[0].data = res.data
        echartsForEach.value.forEach(item => {
            const eNode = document.getElementById(`nursingEcharts-${item}`)
            const myChart = echarts.init(eNode)
            myChart.setOption(option.value)
        })
    }).catch(err => {
        console.error('图表数据加载失败', err)
    })
}

onMounted(() => { getDataCount(); initEcharts() })
```

#### 13.2.12 Vite 配置

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
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8888',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

- 路径别名 `@` → `./src`（与 `jsconfig.json` 中 `"@/*": ["./src/*"]` 对应）
- 开发环境通过 Vite Proxy 代理解决跨域

### 13.3 MyBatis / MyBatis-Plus 数据层标准

#### 13.3.1 MyBatis-Plus 配置（application.yml）

> **[审查修复 - 必删]** 原生 `mybatis-config.xml`（含 `<environments>`/`<dataSource>`）已删除，会与 Spring Boot 数据源冲突导致启动报错。本项目使用 Spring Boot + MyBatis-Plus，数据源由 `application.yml` 的 `spring.datasource` 统一管理，**禁止使用原生 MyBatis 配置文件**。

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true    # 驼峰映射
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 开发环境打印 SQL
  global-config:
    db-config:
      id-type: ASSIGN_ID  # [红线] 雪花算法
      logic-delete-field: is_delete
      logic-delete-value: 1
      logic-not-delete-value: 0
  mapper-locations: classpath:mapper/*.xml  # Mapper XML 位置
```

#### 13.3.2 Mapper 接口规范

```java
// MyBatis-Plus：XxxMapper 后缀，继承 BaseMapper
public interface ArticleMapper extends BaseMapper<ArticleEntity> {
    // 单表 CRUD 由 BaseMapper 提供
    // 仅复杂查询才自定义方法

    // 多参数必须用 @Param 绑定
    int updateArticleTitleAndStatus(
        @Param("article") ArticleEntity article,
        @Param("title") String newTitle,
        @Param("articleId") Long articleId
    );
}
```

#### 13.3.3 Mapper XML 规范

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- namespace = Mapper 接口全限定名 -->
<mapper namespace="com.smart.nursing.dao.ArticleMapper">

    <!-- resultMap：多表关联映射 -->
    <resultMap id="articleVo" type="com.smart.nursing.vo.ArticleVo">
        <association property="article" javaType="com.smart.nursing.entity.ArticleEntity">
            <id property="articleId" column="article_id"/>
            <result property="title" column="title"/>
        </association>
        <association property="category" javaType="com.smart.nursing.entity.CategoryEntity">
            <id property="categoryId" column="category_id"/>
            <result property="categoryName" column="category_name"/>
        </association>
    </resultMap>

    <!-- 简单查询：resultType 可用类型别名 -->
    <select id="getArticleInfoById" resultType="article">
        select * from nursing_article where article_id=#{articleId}
    </select>

    <!-- LIKE 查询：用 concat 防注入 -->
    <select id="loadArticleByTitle" resultType="article">
        select * from nursing_article where title like concat('%', #{title}, '%')
    </select>
</mapper>
```

**规范要点**：
- `namespace` 必须等于 Mapper 接口全限定名
- `id` 与接口方法名一一对应
- 参数引用统一用 `#{}`（预编译防注入），**禁止** `${}`
- LIKE 查询用 `concat('%', #{xxx}, '%')`
- `resultType` 可用类型别名或全限定名

#### 13.3.4 动态 SQL 规范

**条件查询（`<where>` + `<if>`）**：

```xml
<select id="getArticleByCondition" resultMap="articleVo">
    select * from nursing_article
    <where>
        <if test="title != null and title != ''">
            and title like concat('%', #{title}, '%')
        </if>
        <if test="categoryId != null and categoryId != 0">
            and category_id = #{categoryId}
        </if>
    </where>
</select>
```

**条件更新（`<set>` + `<if>`）**：

```xml
<update id="updateArticleSelective">
    update nursing_article
    <set>
        <if test="title != null and title != ''">
            title = #{title},
        </if>
        <if test="status != null">
            status = #{status},
        </if>
    </set>
    where article_id = #{articleId}
</update>
```

**批量操作（`<foreach>`）**：

```xml
<!-- 数组：collection="array" -->
<select id="getByIdsArray" resultType="article">
    select * from nursing_article where article_id in
    <foreach item="item" collection="array" open="(" separator="," close=")">
        #{item}
    </foreach>
</select>

<!-- List：collection="list" -->
<select id="getByIdsList" resultType="article">
    select * from nursing_article where article_id in
    <foreach item="item" collection="list" open="(" separator="," close=")">
        #{item}
    </foreach>
</select>
```

**判空规则**：
- 字符串：`!= null and != ''`
- 数值：`!= null and != 0`

#### 13.3.5 多表关联映射规范

**一对一（`<association>`）**：

```xml
<resultMap id="articleCategoryVo" type="com.smart.nursing.vo.ArticleVo">
    <association property="article" javaType="com.smart.nursing.entity.ArticleEntity">
        <id property="articleId" column="article_id"/>
        <result property="title" column="title"/>
    </association>
    <association property="category" javaType="com.smart.nursing.entity.CategoryEntity">
        <id property="categoryId" column="category_id"/>
        <result property="categoryName" column="category_name"/>
    </association>
</resultMap>
```

**一对多（`<association>` + `<collection>`）**：

```xml
<resultMap id="categoryArticleVo" type="com.smart.nursing.vo.CategoryTreeVo">
    <association property="category" javaType="com.smart.nursing.entity.CategoryEntity">
        <id property="categoryId" column="category_id"/>
        <result property="categoryName" column="category_name"/>
    </association>
    <collection property="articles" ofType="com.smart.nursing.entity.ArticleEntity">
        <id property="articleId" column="article_id"/>
        <result property="title" column="title"/>
    </collection>
</resultMap>
```

- `<id>` 标注主键列（影响嵌套映射去重）
- `<result>` 标注普通列
- `javaType` 用于 association，`ofType` 用于 collection 元素类型

#### 13.3.6 MyBatis-Plus 条件构造器规范

```java
// LambdaQueryWrapper（推荐，类型安全）
LambdaQueryWrapper<ArticleEntity> wrapper = new LambdaQueryWrapper<>();
wrapper.like(StringUtils.isNotBlank(title), ArticleEntity::getTitle, title)
       .eq(categoryId != null, ArticleEntity::getCategoryId, categoryId)
       .orderByAsc(ArticleEntity::getSortOrder);
articleMapper.selectList(wrapper);

// 分页查询
Page<ArticleEntity> page = new Page<>(pageNo, pageSize);
articleMapper.selectPage(page, wrapper);
page.getRecords();  // 当前页数据
page.getTotal();    // 总条数
```

**规范要点**：
- 条件构造方法第一个参数为 `boolean`（条件是否生效），实现动态条件
- 优先用 Lambda 版本（`ArticleEntity::getTitle`）避免硬编码列名
- 判空用 `StringUtils.isNotBlank`（来自 MyBatis-Plus 工具包）

#### 13.3.7 MyBatis-Plus 分页插件配置

```java
@Configuration
@MapperScan("com.smart.nursing.dao")
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

#### 13.3.8 数据库连接配置

**Spring Boot / MyBatis-Plus（application.yml）**：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver    # MySQL 8.x 驱动
    url: jdbc:mysql://localhost:3306/smart_nursing?serverTimeZone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
    username: root
    password: root
```

### 13.4 数据库设计标准

| 维度 | 约定 |
|------|------|
| **数据库** | MySQL 8.x，库名 `smart_nursing` |
| **字符集** | `utf8mb4`，排序规则 `utf8mb4_unicode_ci` |
| **主键** | 雪花算法（`IdType.ASSIGN_ID`），类型 `BIGINT`（**[红线] 废弃自增**） |
| **表命名** | 下划线命名法（snake_case），如 `nursing_article`、`sys_user` |
| **字段命名** | 下划线命名法，如 `article_name`、`create_time` |
| **Java映射** | 驼峰命名法（camelCase），靠 `map-underscore-to-camel-case: true` 自动映射 |
| **逻辑删除** | 字段 `is_delete`，配合 `@TableLogic` 注解。**[红线]** 逻辑删除字段必须与业务唯一键组成**联合唯一索引**（如 `uk_username_del(username, is_delete)`），防止删除后同名列无法重新插入 |
| **菜单权限** | RBAC 模型：`sys_user` → `sys_user_role` → `sys_role` → `sys_role_menu` → `sys_menu` |
| **SQL 日志** | 开发环境控制台输出（`StdOutImpl`），生产环境关闭 |

### 13.5 API 接口设计标准

#### 13.5.1 URL 路径约定

```
/模块名/动作名

示例：
/login                              # 登录
/logout                             # 退出
/admin/article/add                  # 新增文章（PC管理端）
/admin/article/list                 # 条件分页查询
/admin/article/getById/{id}         # 按ID查询
/mobile/learn/articles              # 移动端文章列表
/data/getMainPageCountData          # 首页统计
/data/getCategoryContentCount       # 图表数据
```

#### 13.5.2 请求方式约定

| 场景 | Method | 参数传递 |
|------|--------|----------|
| 登录/提交表单 | POST | `@RequestBody` JSON |
| 条件分页查询 | POST | `@RequestBody` JSON（含 pageNo/pageSize） |
| 简单查询/下拉列表 | GET | 无参或路径参数 |
| 按ID查询 | GET | `@PathVariable` 路径参数 |
| 新增/修改/删除 | POST | `@RequestBody` JSON |

> **注意**：**[红线] Controller 层必须明确使用 `@GetMapping`（查询）或 `@PostMapping`（增删改）**，废弃不区分 HTTP 方法的 `@RequestMapping`。

#### 13.5.3 分页参数约定

**请求参数**：

```json
{
    "title": "老年",
    "categoryId": 1,
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

### 13.6 认证与权限机制

#### 13.6.1 Token 认证流程

```
登录请求 → UserService 校验账号密码 → 生成 UUID Token
                                      ↓
                        Redis 存储：Key = "Token::{uuid}"
                        Value = 用户 JSON，有效期 600 分钟
                                      ↓
                        返回 LoginUserVo（含 user + roles + menus + token）
                                      ↓
前端存储 Token → 后续请求 Header 携带 "Token: {uuid}"
                                      ↓
后端 AOP 切面 → 检查 @NoToken 注解 → 是则放行
             → 否则从 Header 取 Token → Redis 验证 → 有效则滑动续期 + 放行
             → 无效则抛出 BusinessException(401) → 全局异常处理器捕获返回
```

#### 13.6.2 AOP 切面实现

> **[红线修订]** Token 切面**不强转 CommonResult**，校验失败**抛 BusinessException**交由全局异常处理器统一返回。增加滑动续期和角色校验。

```java
@Component
@Aspect
public class AroundCut {
    @Autowired
    StringRedisTemplate redisTemplate;

    // 切入点：controller 包下所有方法
    public static final String POINT_CUT =
        "execution(* com.smart.nursing.controller..*.*(..))";

    @Around(AroundCut.POINT_CUT)
    public Object checkToken(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        // 1. 检查 @NoToken 注解，有则放行
        if (method.isAnnotationPresent(NoToken.class)) {
            return pjp.proceed();
        }

        // 2. 从请求头获取 Token
        HttpServletRequest request = ((ServletRequestAttributes)
            RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Token");

        // 3. [红线] Token 为空 → 抛异常，交由全局异常处理器（不返回 CommonResult）
        if (StringUtils.isBlank(token)) {
            throw new BusinessException(GlobalErrorCodeConstants.UNAUTHORIZED);
        }

        // 4. Redis 验证 Token
        String userInfoString = redisTemplate.opsForValue().get("Token::" + token);
        if (StringUtils.isBlank(userInfoString)) {
            throw new BusinessException(GlobalErrorCodeConstants.UNAUTHORIZED);
        }

        // 5. [红线] 滑动续期：每次请求刷新 Token 过期时间
        redisTemplate.expire("Token::" + token, 10, TimeUnit.HOURS);

        // 6. [红线] 角色校验：根据请求路径前缀校验用户角色
        String requestUri = request.getRequestURI();
        // [红线] 使用 Jackson 替代 FastJSON
        ObjectMapper mapper = new ObjectMapper();
        String roleCode = mapper.readTree(userInfoString).path("roleCode").asText();
        if (requestUri.startsWith("/admin/") && !"ADMIN".equals(roleCode) && !"TEACHER".equals(roleCode)) {
            throw new BusinessException(GlobalErrorCodeConstants.FORBIDDEN);
        }
        if (requestUri.startsWith("/mobile/") && !"NURSE".equals(roleCode)) {
            throw new BusinessException(GlobalErrorCodeConstants.FORBIDDEN);
        }

        // 7. 放行（不强转 CommonResult，直接返回 Object）
        return pjp.proceed();
    }
}
```

#### 13.6.3 免 Token 注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoToken {
}
```

- 用于标记不需要 Token 校验的接口（如 `/login`）
- 标注在 Controller 方法上

#### 13.6.4 Token 存储

| 维度 | 约定 |
|------|------|
| **Key 格式** | `Token::{uuid}` |
| **Value** | 用户信息 JSON 字符串 |
| **有效期** | 600 分钟（滑动续期，每次请求自动刷新） |
| **存储介质** | Redis（database: 0） |
| **传递方式** | HTTP Header `Token` 字段 |
| **退出登录** | 从 Redis 删除对应 Key |

### 13.7 统一返回结果与异常处理

#### 13.7.1 CommonResult 统一响应封装

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

#### 13.7.2 响应 JSON 格式

```json
{
    "code": 200,
    "data": { ... },
    "total": null,
    "msg": ""
}
```

#### 13.7.3 错误码体系

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
| 550 | UPDATE_ERROR | 更新失败 |
| 551 | ADD_ERROR | 新增失败 |
| 552 | DELETE_ERROR | 删除失败 |
| 900 | REPEATED_REQUESTS | 重复请求 |
| 901 | DEMO_DENY | 演示模式禁止写操作 |
| 902 | USER_EXIST | 用户名已存在 |
| 903 | CATEGORY_HAS_CHILDREN | 类别下有子类别，不可删除 |
| 904 | CATEGORY_HAS_CONTENT | 类别下有内容，不可删除 |
| 905 | TAG_IN_USE | 标签已被使用，不可删除 |
| 906 | EXAM_NOT_AVAILABLE | 考试未开始或已结束 |
| 907 | EXAM_ALREADY_SUBMITTED | 已交卷，不可重复提交 |
| 908 | QUESTION_TYPE_ERROR | 试题类型错误 |
| 999 | UNKNOWN | 未知错误 |

#### 13.7.4 异常类型细分

```java
// 业务异常（可预期错误，如用户名重复、类别有子项等）
public class BusinessException extends RuntimeException {
    private Integer code;
    private String message;
    // 构造方法...
}

// AI 异常（AI 调用相关错误，如模型超时、API Key 无效等）
public class AiException extends RuntimeException {
    private Integer code;
    private String message;
    // 构造方法...
}
```

#### 13.7.5 全局异常处理器

> **[红线修订]** 禁止 `e.printStackTrace()` 和 `System.err.println()`，改用 `log.error()`/`log.warn()`。补齐参数校验异常捕获。

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public CommonResult<?> businessExceptionHandler(BusinessException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(AiException.class)
    public CommonResult<?> aiExceptionHandler(AiException e) {
        log.warn("AI异常: {}", e.getMessage());
        return CommonResult.error(e.getCode(), e.getMessage());
    }

    // [红线] 补齐参数校验异常：@RequestBody + @Valid 校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResult<?> validationExceptionHandler(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", msg);
        return CommonResult.error(400, msg);
    }

    // [审查优化] 补齐参数校验异常：@GetMapping + @RequestParam + @Validated 校验失败
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonResult<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", msg);
        return CommonResult.error(400, msg);
    }

    // [审查优化] 补齐请求参数缺失异常
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public CommonResult<?> missingParamExceptionHandler(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数: {}", e.getParameterName());
        return CommonResult.error(400, "缺少必要参数: " + e.getParameterName());
    }

    @ExceptionHandler(Exception.class)
    public CommonResult<?> exceptionHandler(Exception e) {
        // [红线] 禁止 printStackTrace，改用 log.error 带堆栈
        log.error("系统异常: ", e);
        return CommonResult.error(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
    }
}
```

- 使用 `@RestControllerAdvice` 全局捕获
- 异常类型细分：`BusinessException`（业务异常）和 `AiException`（AI 异常）分别处理
- 捕获所有 `Exception`，统一返回 500 错误码
- 控制台打印异常堆栈

#### 13.7.6 分页参数基类

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

### 13.8 配置文件标准

#### 13.8.1 后端 application.yml

```yaml
server:
  port: 8888

logging:
  level:
    com.smart.nursing: DEBUG
  config: classpath:logback-spring.xml

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/smart_nursing?serverTimeZone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true
    username: root
    password: 123456
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
      timeout: 1800000
      lettuce:
        pool:
          max-active: 20
          max-wait: -1
          max-idle: 5
          min-idle: 1
  ai:
    # [审查修订] 阿里云百炼工作空间 MaaS 端点，使用 OpenAI 兼容模式
    openai:
      api-key: ${DASHSCOPE_API_KEY}
      base-url: ${SPRING_AI_OPENAI_BASE_URL:https://ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com/compatible-mode/v1}
      chat:
        options:
          model: qwen-plus
          temperature: 0.7

springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true

mybatis-plus:
  mapper-locations: classpath:/mapper/*.xml
  type-aliases-package: com.smart.nursing.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: ASSIGN_ID  # [红线] 雪花算法，废弃 AUTO 自增
```

#### 13.8.2 后端 logback-spring.xml

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

#### 13.8.3 前端 package.json

```json
{
  "name": "smart-nursing-admin",
  "version": "0.0.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext .vue,.js --fix",
    "format": "prettier --write src/"
  },
  "dependencies": {
    "vue": "^3.5.38",
    "vue-router": "^4.6.3",
    "element-plus": "^2.14.3",
    "@element-plus/icons-vue": "^2.3.1",
    "axios": "^1.18.1",
    "echarts": "^6.0.0",
    "pinia": "^2.2.6",
    "markdown-it": "^14.0.0",
    "highlight.js": "^11.10.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^6.0.7",
    "vite": "^7.0.0",
    "vite-plugin-vue-devtools": "^7.0.0",
    "eslint": "^9.0.0",
    "eslint-plugin-vue": "^9.30.0",
    "prettier": "^3.4.0"
  },
  "engines": {
    "node": "^22.18.0 || >=24.12.0"
  }
}
```

#### 13.8.4 前端 jsconfig.json

```json
{
  "compilerOptions": {
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

#### 13.8.5 前端 .env 环境变量

```bash
# .env.development
VITE_API_BASE_URL=http://localhost:8888
VITE_APP_TITLE=智慧护理培训系统（管理端）

# .env.production
VITE_API_BASE_URL=/api
VITE_APP_TITLE=智慧护理培训系统
```

#### 13.8.6 前端 .eslintrc.cjs

```javascript
module.exports = {
  root: true,
  env: { browser: true, es2023: true },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
    'prettier'
  ],
  parserOptions: { ecmaVersion: 2023, sourceType: 'module' },
  rules: {
    'vue/multi-word-component-names': 'off',
    'no-unused-vars': 'warn'
  }
}
```

#### 13.8.7 前端 .prettierrc

```json
{
  "semi": false,
  "singleQuote": true,
  "tabWidth": 2,
  "trailingComma": "none",
  "printWidth": 100
}
```

### 13.9 命名规范总表

#### 13.9.1 后端命名规范

| 类型 | 命名模式 | 示例 |
|------|----------|------|
| Controller | `XxxController` | `ArticleController` |
| Service 接口 | `IXxxService` | `IArticleService` |
| Service 实现 | `XxxServiceImpl` | `ArticleServiceImpl` |
| Mapper | `XxxMapper` | `ArticleMapper` |
| Entity | `XxxEntity` | `ArticleEntity` |
| DTO | `XxxDto` | `ArticleDto` |
| VO | `XxxVo` | `LoginUserVo` |
| 配置类 | `XxxConfig` | `WebConfig` |
| 异常处理 | `GlobalExceptionHandler` | — |
| 切面 | `XxxCut` / `XxxAspect` | `AroundCut` / `LogAspect` |
| 注解 | 语义化命名 | `NoToken` / `RecordLog` |
| 工具类 | `XxxUtils` | `SecurityUtils` |
| 错误码常量 | `GlobalErrorCodeConstants` | — |

#### 13.9.2 前端命名规范

| 类型 | 命名模式 | 示例 |
|------|----------|------|
| 页面组件 | 大驼峰 + `View` 后缀 | `ArticleView.vue` |
| 公共组件 | 大驼峰 | `ImageUpload.vue` |
| API 方法 | 驼峰动词开头 | `listArticleByConditionPage` |
| 工具文件 | 语义化小写 | `request.js` |
| 路由 name | 小写 | `article` |
| 路由 path | 小写斜杠 | `/article/list` |
| CSS 文件 | 小写连字符 | `base.css` |
| Pinia store | 语义化 | `useUserStore` |

#### 13.9.3 数据库命名规范

| 类型 | 命名模式 | 示例 |
|------|----------|------|
| 表名 | 下划线小写 | `nursing_article`、`sys_user` |
| 字段名 | 下划线小写 | `article_title`、`create_time` |
| 主键 | `{表名简写}_id` | `article_id`、`user_id` |
| 外键 | `{关联表名简写}_id` | `category_id`、`role_id` |
| 关联表 | `{表A}_{表B}` | `nursing_article_tag`、`sys_role_menu` |

### 13.10 编码约定

#### 13.10.1 通用约定

- **Java 版本**：17，全项目统一
- **编码**：UTF-8
- **序列化**：所有实体/VO/DTO `implements Serializable`
- **参数绑定**：一律 `#{}`（预编译防注入），**禁止** `${}`
- **主键类型**：用 `Long`（包装类型），避免 0 值歧义
- **返回值**：增删改方法返回 `int`（受影响行数），业务层判断 `> 0` 判定成功

#### 13.10.2 后端编码约定

- **注解组合**：Entity 用 `@Data` + `@ToString` + `@TableName` + `@TableId`
- **Controller**：`@RestController` + `@RequestMapping` + `@Tag` + `@Operation`
- **Service**：`@Service`，实现类继承 `ServiceImpl<Mapper, Entity>`
- **Mapper**：继承 `BaseMapper<Entity>`，无需 `@Mapper` 注解
- **配置类**：`@Configuration`
- **AOP**：`@Component` + `@Aspect`
- **Swagger**：使用 SpringDoc OpenAPI 3（非旧版 `@Api`/`@ApiOperation`）
- **JSON 处理**：**[红线] 废弃 FastJSON 1.2.73**，统一使用 Jackson 的 `ObjectMapper`/`JsonNode` 组装复杂数据
- **异常细分**：业务异常用 `BusinessException`，AI 异常用 `AiException`

#### 13.10.3 前端编码约定

- **语法风格**：Vue3 `<script setup>` Composition API，不使用 Options API
- **路径引用**：统一使用 `@/` 别名
- **API 调用**：通过 `@/api/模块名` 统一导出
- **错误处理**：**[审查修复]** 使用 `try/catch` 或 `.catch()` 捕获错误（废弃 `res && res != -1` 判断）
- **状态共享**：Pinia store 管理 Token + userInfo（替代 localStorage）
- **环境变量**：使用 `.env.development` / `.env.production`（非硬编码 URL）
- **表单校验**：Element Plus `rules` + `ruleFormRef.validate()`，trigger 为 `blur`
- **操作反馈**：成功用 `ElNotification`，确认用 `ElMessageBox.confirm`，即时提示用 `ElMessage`
- **组件样式**：使用 `<style scoped>` 隔离
- **生命周期**：数据初始化统一在 `onMounted` 中调用
- **新增/编辑复用**：同一 `el-dialog`，通过主键 ID 是否存在区分操作类型
- **代码规范**：ESLint + Prettier 自动格式化

#### 13.10.4 跨域处理约定

- 后端通过 `WebConfig` 配置全局 `CorsFilter`
- 允许所有源（`*`）、所有请求头、所有方法
- 允许携带凭证（`setAllowCredentials(true)`）
- 过滤器优先级：99999（最低优先级）
- 开发环境也可通过 Vite Proxy 代理解决跨域

### 13.11 前后端联调约定

#### 13.11.1 端口约定

| 服务 | 端口 |
|------|------|
| 后端 Spring Boot | 8888 |
| 前端 Vite Dev Server | 5173 |
| MySQL | 3306 |
| Redis | 6379 |
| Nginx（生产） | 80 |

#### 13.11.2 认证联调

| 步骤 | 前端 | 后端 |
|------|------|------|
| 登录 | POST `/login`，Body 含账号密码 | 校验通过后生成 UUID Token，存入 Redis，返回 `LoginUserVo` |
| 存储 | Pinia store 保存 Token + userInfo | — |
| 请求 | 拦截器自动注入 `Header: Token` | AOP 切面从 Header 取 Token，Redis 验证 |
| 失效 | 响应 401 → 跳转登录页 | Token 不存在或过期返回 401 |
| 退出 | GET `/logout` | 从 Redis 删除 Token |

#### 13.11.3 分页联调

| 维度 | 前端参数 | 后端处理 |
|------|----------|----------|
| 请求 | `pageNo`（从1开始）+ `pageSize` | DTO 继承 Entity 追加分页字段 |
| 响应 | 读取 `data.records`（列表）+ `total`（总数） | `CommonResult.successPageData(IPage)` 或手动组装 |

### 13.12 日志规范

#### 13.12.1 后端日志

| 环境 | 日志级别 | 输出方式 |
|------|----------|----------|
| 开发 | DEBUG | 控制台（含 SQL） + 文件 |
| 生产 | INFO | 文件（按天滚动） |

- 配置文件：`logback-spring.xml`
- 日志格式：`[%p]%d - %msg%n`
- 文件路径：`logs/%d.log`（按天滚动）
- SQL 日志：开发环境用 `StdOutImpl` 输出到控制台
- 操作日志：通过 AOP `LogAspect` 自动记录到 `sys_log` 表

#### 13.12.2 前端日志

- 开发环境：控制台输出（Vite 默认）
- 生产环境：构建时自动移除 console（如需）

### 13.13 依赖版本锁定清单

#### 13.13.1 后端 pom.xml 关键依赖

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

    <!-- [红线] BCrypt 密码加密（废弃 MD5） -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-crypto</artifactId>
    </dependency>

    <!-- [红线] 参数校验 -->
    <dependency><artifactId>spring-boot-starter-validation</artifactId></dependency>

    <!-- Swagger / OpenAPI -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.8.4</version>
    </dependency>

    <!-- [红线] 废弃 FastJSON 1.2.73，统一使用 Spring Boot 内置 Jackson -->

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
    <!-- [审查修订] 移除 DashScope Alibaba 依赖，统一使用 OpenAI 兼容模式对接百炼 MaaS 端点 -->
    <!-- @ConditionalOnProperty 隔离：API Key 未配置时 AI Bean 不加载，不影响主业务 -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-openai</artifactId>
        <version>1.1.2</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
</dependencies>
```

**构建插件**：
- `spring-boot-maven-plugin`：排除 Lombok 打包
- `maven-compiler-plugin`：配置 Lombok 注解处理器（compile + testCompile 两个 execution）

#### 13.13.2 前端 package.json 关键依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| `vue` | ^3.5.38 | 核心框架 |
| `vue-router` | ^4.6.3 | 路由 |
| `element-plus` | ^2.14.3 | UI 组件库 |
| `@element-plus/icons-vue` | ^2.3.1 | 图标库 |
| `axios` | ^1.18.1 | HTTP 请求 |
| `echarts` | ^6.0.0 | 数据可视化 |
| `pinia` | ^2.2.6 | 状态管理 |
| `markdown-it` | ^14.0.0 | Markdown 渲染 |
| `highlight.js` | ^11.10.0 | 代码高亮 |
| `vite` | ^7.0.0 | 构建工具 |
| `@vitejs/plugin-vue` | ^6.0.7 | Vue 插件 |
| `vite-plugin-vue-devtools` | ^7.0.0 | DevTools |
| `eslint` | ^9.0.0 | 代码检查 |
| `prettier` | ^3.4.0 | 代码格式化 |

> **SpringAI 前端说明**：AI 流式对话使用浏览器原生 Fetch + ReadableStream，无额外 npm 依赖。

### 13.14 工程目录速查模板

#### 13.14.1 后端新建工程模板

```
springboot-smart-nursing/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/smart/nursing/
│   │   │   ├── SmartNursingApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── common/
│   │   │   │   ├── admin/
│   │   │   │   └── mobile/
│   │   │   ├── service/
│   │   │   │   └── impl/
│   │   │   ├── dao/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── vo/
│   │   │   ├── aop/
│   │   │   │   ├── AroundCut.java
│   │   │   │   ├── NoToken.java
│   │   │   │   └── LogAspect.java
│   │   │   └── common/
│   │   │       ├── config/
│   │   │       ├── exception/
│   │   │       ├── result/
│   │   │       ├── enums/
│   │   │       └── utils/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml.example
│   │       ├── logback-spring.xml
│   │       └── mapper/
│   └── test/
└── .gitignore
```

#### 13.14.2 前端新建工程模板

```
smart-nursing-admin/
├── .env.development
├── .env.production
├── .eslintrc.cjs
├── .prettierrc
├── .vscode/
│   ├── extensions.json
│   └── settings.json
├── public/
│   └── favicon.ico
├── src/
│   ├── api/
│   │   ├── auth.js
│   │   ├── user.js
│   │   └── ...
│   ├── assets/
│   │   ├── base.css
│   │   └── main.css
│   ├── components/
│   ├── router/
│   │   └── index.js
│   ├── stores/
│   │   ├── user.js
│   │   └── app.js
│   ├── utils/
│   │   ├── request.js
│   │   ├── streamChatAPI.js
│   │   └── format.js
│   ├── views/
│   ├── App.vue
│   └── main.js
├── index.html
├── jsconfig.json
├── package.json
├── vite.config.js
└── .gitignore
```

#### 13.14.3 新增功能模块 Checklist

新增一个业务模块（如"课程管理"）时，按以下顺序创建文件：

**后端**：
1. `entity/CourseEntity.java` — 实体类（`@Data` + `@TableName`）
2. `dao/CourseMapper.java` — Mapper 接口（继承 `BaseMapper`）
3. `service/ICourseService.java` — Service 接口（继承 `IService`）
4. `service/impl/CourseServiceImpl.java` — Service 实现（继承 `ServiceImpl`）
5. `controller/admin/CourseController.java` — Controller（`@RestController` + `@Tag`）
6. `dto/CourseDto.java` — DTO（如需分页查询，继承 Entity 追加 pageNo/pageSize）
7. `resources/mapper/CourseMapper.xml` — Mapper XML（仅复杂查询需要）
8. `common/enums/GlobalErrorCodeConstants.java` — 追加业务错误码

**前端**：
1. `api/course.js` — 课程相关接口方法
2. `views/course/CourseView.vue` — 课程管理页面（参考 CRUD 模板）
3. `router/index.js` — 追加路由配置（`children` 中添加）
4. 后端菜单表追加课程管理菜单记录（含 path/title/icon）

### 13.15 SpringAI 集成架构标准

> 本节基于 `springboot-swjtu+SpringAI`（后端）与 `swjtu-vue+SpringAI集成`（前端）两个项目提炼，作为本系统 AI 功能开发的标准。

> **[审查修订]** AI 模块采用 `@ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")` 条件加载策略。当 API Key 未配置或为占位值时，AI 相关 Bean 不会加载，AIController 返回"AI 功能未启用"提示，确保 AI 依赖问题不影响主业务启动。

> **[审查修订 - 重要]** 用户使用的是**阿里云百炼工作空间专属 MaaS 端点**（非标准 DashScope 端点），需通过 **OpenAI 兼容模式**对接，不再使用 `spring-ai-alibaba-starter-dashscope`，改为 `spring-ai-starter-model-openai` 指向百炼兼容端点。文生图功能改用百炼 OpenAI 兼容模式中的图像生成接口或 HTTP 直调。

#### 13.15.1 SpringAI 集成总览

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
│  ├─ LanguageModelService  → OpenAiChatModel（同步）        │
│  ├─ ChatModelService      → OpenAiChatModel（流式Flux）    │
│  │    └─ Redis 存储上下文（AIChat::{sessionId}）            │
│  └─ ImageGenerationService → HTTP 直调百炼文生图接口        │
└────────────────────────┬─────────────────────────────────┘
                         │ OpenAI 兼容协议
              ┌──────────┴──────────────────────────┐
              │  阿里云百炼 MaaS 工作空间端点          │
              │  https://{workspace}.maas.aliyuncs   │
              │  .com/compatible-mode/v1              │
              │  模型: qwen-plus / qwen-turbo         │
              └──────────────────────────────────────┘
```

**与业务系统的关系**：
- AI 模块与业务模块（User/Article/Exam 等）**完全独立**，互不影响
- AI Service 不继承 `IService`/`ServiceImpl`，直接 `@Service` 注解标注
- AI 请求 DTO 为 Controller 内部静态类，未放入 `dto` 包
- AOP Token 切面、全局异常处理、CommonResult 统一返回等公共机制**全部复用**

#### 13.15.2 SpringAI 后端依赖（pom.xml 新增）

> **[审查修订]** 移除 `spring-ai-alibaba-starter-dashscope` 和 `dashscope-sdk-java`，统一使用 `spring-ai-starter-model-openai` 对接百炼 OpenAI 兼容端点。

```xml
<!-- Spring AI OpenAI 兼容模型支持（指向阿里云百炼 MaaS 端点） -->
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
```

**版本对齐规则**：Spring AI 主版本 `1.1.2`，通过 OpenAI 兼容协议对接百炼端点，无需 Alibaba 版本对齐。

#### 13.15.3 后端 AI 架构分层

```
com.smart.nursing
├── controller/
│   └── AIController.java                 # AI 接口入口（@RestController + @Tag）
├── service/                              # AI Service（不继承 IService 体系）
│   ├── LanguageModelService.java         # 同步文本生成（OpenAiChatModel）
│   ├── ChatModelService.java             # 流式对话 + 上下文管理（OpenAiChatModel.stream）
│   ├── ImageGenerationService.java       # 文生图（HTTP 直调百炼接口）
│   └── RecommendService.java             # AI 个性化学习推荐
└── ...（原有业务代码不变）
```

**AI Service 与业务 Service 的区别**：

| 维度 | 业务 Service | AI Service |
|------|-------------|-----------|
| 继承 | `IService<Entity>` + `ServiceImpl<Mapper, Entity>` | 无继承，直接 `@Service` |
| 注入 | `@Autowired XxxMapper` | `@Autowired OpenAiChatModel`（Spring AI 自动装配） |
| 返回 | `CommonResult<T>` | 同步返回 `CommonResult<T>`，流式返回 `Flux<String>` |
| DTO 位置 | `dto/XxxDto.java` 独立文件 | Controller 内部静态类 |
| 数据库 | 通过 Mapper 操作 MySQL | 不操作数据库（上下文存 Redis） |

#### 13.15.4 AIController 接口规范

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
    static class ChatRequest { private String sessionId; private String message; }

    @Data
    static class ChatWithSystemRequest { private String sessionId; private String systemPrompt; private String message; }

    @Data
    static class ImageRequest { private String prompt; private String size; private Integer number; }
}
```

#### 13.15.5 AI 接口清单

| 方法 | 路径 | 入参 | 返回类型 | 说明 |
|------|------|------|---------|------|
| POST | `/ai/text/generate` | `{prompt}` | `CommonResult<String>` | 简单文本生成 |
| POST | `/ai/text/generate-template` | `{topic, style}` | `CommonResult<String>` | PromptTemplate 模板生成 |
| POST | `/ai/text/generate-chat` | `{prompt}` | `CommonResult<String>` | ChatClient 生成 |
| POST | `/ai/chat/stream` | `{sessionId, message}` | `Flux<String>` (SSE) | 流式单轮对话 |
| POST | `/ai/chat/stream-with-system` | `{sessionId, systemPrompt, message}` | `Flux<String>` (SSE) | 带系统提示词流式对话 |
| GET | `/ai/chat/history/{sessionId}` | 路径参数 | `CommonResult<List<{role,content}>>` | 查询对话历史 |
| DELETE | `/ai/chat/history/{sessionId}` | 路径参数 | `CommonResult<Void>` | 清除对话历史 |
| POST | `/ai/image/generate` | `{prompt, size, number}` | `CommonResult<String>` | 单图生成 |
| POST | `/ai/image/generate-multiple` | `{prompt, size, number}` | `CommonResult<List<String>>` | 多图生成 |

#### 13.15.6 Service 层实现规范

**LanguageModelService（同步文本生成）**：注入 `OpenAiChatModel`，提供三种调用方式：

```java
@Service
public class LanguageModelService {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    // 方式一：最简调用
    public String generateText(String prompt) {
        return openAiChatModel.call(prompt);
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
        return openAiChatModel.call(prompt).getResult().getOutput().getText();
    }

    // 方式三：ChatClient 链式调用
    public String generateChatClient(String prompt) {
        ChatClient chatClient = ChatClient.create(openAiChatModel);
        return chatClient.prompt().user(prompt).call().content();
    }
}
```

**ChatModelService（流式对话 + 上下文管理）**：

```java
@Service
public class ChatModelService {

    @Autowired
    private OpenAiChatModel openAiChatModel;
    @Autowired
    private StringRedisTemplate redisTemplate;

    // Redis 存储会话历史，Key 格式：AIChat::{sessionId}，TTL 24小时
    private static final String AI_CHAT_KEY_PREFIX = "AIChat::";
    private static final long AI_CHAT_TTL_HOURS = 24;

    // 流式对话（统一在 doOnComplete 中保存 AI 回复）
    public Flux<String> streamChat(String sessionId, String message) {
        List<Message> history = getHistoryFromRedis(sessionId);
        history.add(new UserMessage(message));
        Prompt prompt = new Prompt(history);
        StringBuilder fullResponse = new StringBuilder();
        return openAiChatModel.stream(prompt)
            .map(response -> {
                String text = response.getResult().getOutput().getText();
                fullResponse.append(text);
                return text;
            })
            .doOnError(e -> {
                // 失败时回滚最后一条用户消息
                List<Message> msgs = getHistoryFromRedis(sessionId);
                if (msgs != null && !msgs.isEmpty()) msgs.remove(msgs.size() - 1);
                saveHistoryToRedis(sessionId, msgs);
            })
            .doOnComplete(() -> {
                // 统一保存完整 AI 回复到历史
                history.add(new AssistantMessage(fullResponse.toString()));
                saveHistoryToRedis(sessionId, history);
            });
    }

    // 带系统提示词的流式对话（使用 SystemMessage）
    public Flux<String> streamChatWithSystem(String sessionId, String systemPrompt, String message) {
        List<Message> history = getHistoryFromRedis(sessionId);
        if (history.isEmpty()) {
            history.add(new SystemMessage(systemPrompt));  // 使用 SystemMessage（非 AssistantMessage）
        }
        history.add(new UserMessage(message));
        Prompt prompt = new Prompt(history);
        StringBuilder fullResponse = new StringBuilder();
        return openAiChatModel.stream(prompt)  // [审查修复] dashScopeChatModel → openAiChatModel
            .map(response -> {
                String text = response.getResult().getOutput().getText();
                fullResponse.append(text);
                return text;
            })
            .doOnComplete(() -> {
                history.add(new AssistantMessage(fullResponse.toString()));
                saveHistoryToRedis(sessionId, history);
            });
    }

    // 从 Redis 获取历史
    private List<Message> getHistoryFromRedis(String sessionId) {
        String json = redisTemplate.opsForValue().get(AI_CHAT_KEY_PREFIX + sessionId);
        if (json == null) return new ArrayList<>();
        // [红线] 使用 Jackson 替代 FastJSON 反序列化
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<List<Message>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // 保存历史到 Redis（TTL 24h）
    private void saveHistoryToRedis(String sessionId, List<Message> history) {
        try {
            // [红线] 使用 Jackson 替代 FastJSON 序列化
            ObjectMapper mapper = new ObjectMapper();
            // [红线] 修复 Message 序列化 Bug：注册 JavaTimeModule 处理日期字段
            mapper.registerModule(new JavaTimeModule());
            // [红线] 滑动窗口截断：超过 20 条消息只保留最近 10 条（用户+AI配对）
            if (history.size() > 20) {
                history = history.subList(history.size() - 10, history.size());
            }
            redisTemplate.opsForValue().set(
                AI_CHAT_KEY_PREFIX + sessionId,
                mapper.writeValueAsString(history),
                AI_CHAT_TTL_HOURS, TimeUnit.HOURS
            );
        } catch (Exception e) {
            // 序列化失败不影响主流程
        }
    }

    // 清除历史
    public void clearHistory(String sessionId) {
        redisTemplate.delete(AI_CHAT_KEY_PREFIX + sessionId);
    }

    // 查询历史
    public List<Map<String, String>> getHistory(String sessionId) {
        List<Message> history = getHistoryFromRedis(sessionId);
        if (history.isEmpty()) return Collections.emptyList();
        return history.stream().map(msg -> {
            Map<String, String> map = new HashMap<>();
            map.put("role", msg instanceof UserMessage ? "user" : 
                          msg instanceof SystemMessage ? "system" : "assistant");
            map.put("content", msg.getText());
            return map;
        }).collect(Collectors.toList());
    }
}
```

**ImageGenerationService（文生图）**：

```java
@Service
public class ImageGenerationService {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    // 文生图（通过百炼 OpenAI 兼容模式 HTTP 直调）
    // 百炼兼容端点支持图像生成模型调用
    public String generateImage(String prompt, String size, Integer number) {
        // 使用 RestClient 或 WebClient 直接调用百炼兼容端点
        // POST {baseUrl}/images/generations
        // Body: { "model": "wanx-v1", "prompt": "...", "n": 1, "size": "1024*1024" }
        // 返回图片 URL 或 base64
        // 注意：百炼工作空间端点的文生图模型需确认是否在兼容模式中可用
        // 如不可用，则通过 DashScope 原生 API 端点调用：
        //   POST https://ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis
    }
}
```

**可选模型清单**：`qwen-image`、`qwen-image-plus`、`wanx-v1`、`wan2.2-t2i-plus`、`wan2.2-t2i-flash`

#### 13.15.7 前端 AI 架构

**新增文件**：

| 文件 | 说明 |
|------|------|
| `src/utils/streamChatAPI.js` | SSE 流式请求工具（Fetch + ReadableStream + AbortController） |
| `src/views/AiView.vue` | AI 整合页面（文本生成 + 流式对话 + 图文生成） |

**流式请求工具（streamChatAPI.js）**：使用 Fetch API + ReadableStream（非 EventSource），因为 AI 对话需要 POST + JSON Body：

```javascript
const URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8888'

export const streamChatAPI = {
  async streamChat(url, body, onMessage, onError, onComplete) {
    const controller = new AbortController();
    const userStore = useUserStore();

    try {
      const response = await fetch(`${URL}${url}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Token': userStore.token || ''   // 注入 Token
        },
        body: JSON.stringify(body),
        signal: controller.signal
      });

      if (!response.ok) {
        // [审查优化] 流式接口 401 特殊处理：Axios 拦截器管不到 fetch，需手动跳转
        if (response.status === 401) {
          userStore.clear();  // 清除 Token + userInfo
          ElMessage.error('登录已失效，请重新登录')
          router.push('/')
          return controller
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      while (true) {
        const { done, value } = await reader.read();
        if (done) { if (onComplete) onComplete(); break; }

        buffer += decoder.decode(value, { stream: true });

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
                onMessage(data);
              }
            }
          }
        }
      }
    } catch (error) {
      if (error.name !== 'AbortError' && onError) onError(error);
    }
    return controller;
  }
};
```

**设计要点**：
- 三个回调：`onMessage(data)` 增量文本、`onError(error)` 错误、`onComplete()` 完成
- 返回 `AbortController` 供调用方取消请求（停止生成）
- SSE 解析：按 `\n` 分割行，匹配 `data:` 前缀，过滤 `[DONE]`
- **Token 注入**：Fetch 请求中携带 `Token` Header（与 axios 拦截器一致）

**两套请求通道**：

| 通道 | 文件 | 用途 | 特点 |
|------|------|------|------|
| **axios** | `utils/request.js` | 文本生成、历史管理、图片生成 | 走拦截器，自动注入 Token，处理业务状态码 |
| **Fetch** | `utils/streamChatAPI.js` | 流式对话（SSE） | 手动注入 Token，手动解析 SSE，支持取消 |

**AI 对话页面 Markdown 渲染**：使用 `markdown-it` + `highlight.js`（非手写正则替换）：

```javascript
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

const md = new MarkdownIt({
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch {}
    }
    return ''
  }
})

const formatMessage = (content) => {
  return content ? md.render(content) : ''
}
```

#### 13.15.8 SpringAI 注意事项

1. **API Key 安全**：`application.yml` 中禁止明文存放，必须使用环境变量 `${DASHSCOPE_API_KEY}` 注入
2. **上下文存储**：使用 Redis 存储（Key 格式 `AIChat::{sessionId}`，TTL 24 小时），非内存 `ConcurrentHashMap`
3. **流式接口 Token**：`streamChatAPI.js` 的 Fetch 请求必须携带 Token Header。**[审查优化]** 401 需手动处理（`response.status === 401` 时清 Token + 跳转登录），因为 Fetch 不走 Axios 拦截器
4. **WebFlux 与 WebMVC 共存**：`spring-boot-starter-webflux` 与 `spring-boot-starter-web` 并存时，Spring Boot 优先使用 Servlet 容器（WebMVC），WebFlux 仅提供 `Flux` 响应式类型支持
5. **系统提示词实现**：使用 `SystemMessage`（非 `AssistantMessage`）
6. **流式对话历史保存**：所有流式方法统一在 `doOnComplete` 中保存 AI 回复
7. **模型对接方式**：通过 OpenAI 兼容模式对接百炼 MaaS 端点，无需 Alibaba SDK 版本对齐
8. **图片生成模型选择**：百炼兼容端点的文生图能力需实际测试确认，如不可用则走 DashScope 原生 API
9. **[审查修复] 流式请求内存泄漏**：Vue3 组件中使用流式接口（Fetch SSE）时，**必须在 `onUnmounted` 中调用 `controller.abort()` 主动断开**，否则组件销毁后流仍在读取导致内存泄漏

```javascript
// 流式请求内存泄漏防护示例
let abortController = null

const streamChat = async (message) => {
    abortController = new AbortController()
    try {
        const response = await fetch('/api/ai/stream/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Token': token },
            body: JSON.stringify({ sessionId, message }),
            signal: abortController.signal  // 绑定 AbortSignal
        })
        // ... 读取 SSE 流
    } catch (err) {
        if (err.name === 'AbortError') return  // 主动取消，不报错
        console.error(err)
    }
}

// [审查修复] 组件销毁时主动断开流
onUnmounted(() => {
    if (abortController) abortController.abort()
})
```

#### 13.15.8a 阿里云百炼 MaaS 端点配置说明

> **[审查新增]** 用户使用阿里云百炼工作空间专属 MaaS 端点，配置信息如下：

| 配置项 | 值 |
|--------|-----|
| API Key | 通过环境变量 `DASHSCOPE_API_KEY` 注入（禁止明文存入代码仓库） |
| API Host | `ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com` |
| OpenAI 兼容地址 | `https://ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com/compatible-mode/v1` |
| DashScope 原生地址 | `https://ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com/api/v1` |
| 对话模型 | `qwen-plus`（默认）/ `qwen-turbo`（快速） |
| 文生图模型 | 待实际测试确认（百炼兼容模式可能支持 `wanx-v1`） |

**application.yml 配置**：
```yaml
spring:
  ai:
    openai:
      api-key: ${DASHSCOPE_API_KEY}
      base-url: ${SPRING_AI_OPENAI_BASE_URL:https://ws-m4sk49dmsyx5y6le.cn-beijing.maas.aliyuncs.com/compatible-mode/v1}
      chat:
        options:
          model: qwen-plus
          temperature: 0.7
```

**开发环境设置 API Key**（PowerShell）：
```powershell
$env:DASHSCOPE_API_KEY = "你的API Key"
```

**生产环境设置 API Key**（启动脚本）：
```bat
set DASHSCOPE_API_KEY=你的API Key
```

#### 13.15.9 AI 模块开发 Checklist

新增 AI 功能时，按以下顺序创建文件：

**后端**：
1. `service/XxxAiService.java` — AI Service（直接 `@Service`，注入 `OpenAiChatModel`）
2. `controller/AIController.java` — 追加接口方法（内部静态类定义请求 DTO）
3. `application.yml` — 确认 `spring.ai.openai.api-key` 和 `spring.ai.openai.base-url` 已配置
4. `pom.xml` — 确认 SpringAI 依赖已引入
5. 若接口无需 Token 校验，在 Controller 方法上标注 `@NoToken`

**前端**：
1. `api/ai.js` — 追加 AI 接口方法（同步走 axios，流式走 `streamChatAPI.js`）
2. `views/AiView.vue` — 在对应区块追加 UI 和逻辑
3. `router/index.js` — 确认 `/myai` 路由已配置
4. 流式接口调用 `streamChatAPI.streamChat()`，传入 `onMessage`/`onError`/`onComplete` 回调

#### 13.15.10 SpringAI 能力边界

| 能力 | 状态 | 说明 |
|------|------|------|
| 同步文本生成 | 已实现 | `OpenAiChatModel.call()` |
| PromptTemplate | 已实现 | Java 文本块内联模板，`{占位符}` 传参 |
| ChatClient | 已实现 | `ChatClient.create(model).prompt().user().call().content()` |
| 流式对话（SSE） | 已实现 | `Flux<String>` + WebFlux + `TEXT_EVENT_STREAM_VALUE` |
| 多轮上下文 | 已实现（Redis） | `AIChat::{sessionId}`，TTL 24h |
| 系统提示词 | 已实现 | 使用 `SystemMessage` |
| 文生图 | 已实现 | DashScope 原生 SDK，模型 `qwen-image-plus` / `wanx-v1` |
| 对话历史管理 | 已实现 | 查询/清除接口，`sessionId` 隔离 |
| RAG（检索增强生成） | **未实现** | 无 VectorStore / EmbeddingModel |
| Function Calling / Tool | **未实现** | 无 `@Tool` 注册（Agent 框架依赖已引入但未使用） |

### 13.16 团队统一开发环境配置

#### 13.16.1 团队统一环境版本清单

所有团队成员必须安装以下版本，**不建议使用更高或更低的主版本**。

| 环境项 | 统一版本 | 下载/说明 |
|--------|---------|----------|
| **JDK** | **17 LTS**（推荐 Microsoft Build of OpenJDK 17 或 Eclipse Temurin 17） | 避免使用 JDK 8/11/21，防止 Spring Boot 3 编译异常 |
| **Maven** | **3.9.x** | 使用独立 Maven，不建议使用 IDE 内置 Maven |
| **Node.js** | **22.18.0 LTS** 或 **24.12.0+** | 前端 package.json 的 engines 要求 |
| **npm** | 随 Node.js 自带 | 建议使用 `npm 10.x` |
| **Git** | **2.40+** | 统一换行符处理策略 |
| **MySQL** | **8.0.x** | 统一使用 `smart_nursing` 数据库 |
| **Redis** | **5.x+**（Windows 可用 3.2.100 / 5.0.14） | 后端 Token 认证与缓存依赖 |
| **IDE（后端）** | **IntelliJ IDEA 2025.2.4** | 推荐 Ultimate，Community 也可 |
| **IDE（前端）** | **VS Code 1.128.1** | 已安装，需安装推荐插件 |
| **IDE（移动端）** | **HBuilderX 4.76** | uni-app 开发，已安装 |
| **数据库客户端** | **SQLyog / Navicat Premium** | 推荐 Navicat Premium Lite 或 SQLyog |

#### 13.16.2 环境变量配置（Windows 团队统一）

**JAVA_HOME 与 JDK 路径**：

```
JAVA_HOME = C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot
PATH 追加：%JAVA_HOME%\bin
```

> `JAVA_HOME` 必须指向 JDK 17，不能指向 JRE 或 JDK 21。本机存在多版本 JDK 时务必统一指定。

**Maven 路径**：

```
MAVEN_HOME = D:\apache-maven-3.9.15-bin\apache-maven-3.9.15
PATH 追加：%MAVEN_HOME%\bin
```

**Node.js 路径**：安装程序自动配置，验证命令 `node -v` / `npm -v`。

#### 13.16.3 验证命令清单

团队成员配置完环境后，依次执行以下命令验证：

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

#### 13.16.4 IDE 统一配置

**IntelliJ IDEA（后端）必装插件**：
- Lombok（通常内置）
- MyBatisX（Mapper 接口与 XML 跳转）
- Maven Helper（依赖冲突分析）
- Spring Boot（内置）
- .env files support（可选）

**统一设置**：
- `Build Tools → Maven`：Maven home path 指向统一 Maven 3.9.x
- `Compiler → Java Compiler`：Target bytecode version: **17**
- `Editor → File Encodings`：Global/Project/Properties encoding 均为 **UTF-8**
- `Editor → Code Style → Java`：统一 **4 空格缩进**
- `Tools → Lombok`：确保启用 Annotation Processors

**VS Code（前端）推荐扩展**：

```json
{
  "recommendations": [
    "Vue.volar",
    "Vue.vscode-typescript-vue-plugin",
    "dbaeumer.vscode-eslint",
    "esbenp.prettier-vscode",
    "christian-kohler.path-intellisense"
  ]
}
```

**VS Code 统一设置**（`.vscode/settings.json`）：

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

#### 13.16.5 数据库初始化脚本

```sql
-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS smart_nursing
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 2. 创建用户（可选，也可直接使用 root）
CREATE USER IF NOT EXISTS 'nursing'@'localhost' IDENTIFIED BY 'nursing123';
GRANT ALL PRIVILEGES ON smart_nursing.* TO 'nursing'@'localhost';
FLUSH PRIVILEGES;

-- 3. 切换数据库
USE smart_nursing;

-- 4. 执行项目提供的建表/初始化 SQL 脚本
-- source D:/practice/智慧护理培训系统/sql/init.sql;
```

#### 13.16.6 Redis 启动方式（Windows）

**方式一：直接启动（开发调试）**：

```powershell
cd D:\redis-3.2.100
redis-server.exe redis.windows.conf
```

**方式二：注册为 Windows 服务（推荐）**：

```powershell
# 以管理员身份运行 PowerShell
cd D:\redis-3.2.100
redis-server.exe --service-install redis.windows.conf --loglevel verbose
redis-server.exe --service-start
redis-server.exe --service-stop
redis-server.exe --service-uninstall
```

#### 13.16.7 Maven 统一 settings.xml

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <localRepository>D:/maven-repo</localRepository>

    <mirrors>
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

- 本地仓库路径统一为 `D:/maven-repo`
- 使用阿里云镜像加速依赖下载
- 默认激活 JDK 17 编译配置

#### 13.16.8 Git 统一配置

```powershell
# 统一用户名和邮箱
git config --global user.name "你的姓名"
git config --global user.email "你的邮箱@example.com"

# 统一换行符为 LF
git config --global core.autocrlf false
git config --global core.eol lf

# 设置默认分支名为 main
git config --global init.defaultBranch main
```

#### 13.16.9 环境配置 Checklist（新人入职）

| 步骤 | 检查项 | 验证命令 |
|------|--------|---------|
| 1 | 安装 JDK 17 并配置 JAVA_HOME | `java -version` |
| 2 | 安装 Maven 3.9.x 并配置 PATH | `mvn -version` |
| 3 | 安装 Node.js 22.18.0+ | `node -v` |
| 4 | 安装 Git 2.40+ 并配置用户名邮箱 | `git --version` |
| 5 | 安装 MySQL 8.0.x 并创建 smart_nursing 数据库 | `mysql -uroot -p -e "SHOW DATABASES;"` |
| 6 | 安装 Redis 并确认可 ping 通 | `redis-cli ping` |
| 7 | 安装 IDEA / VS Code / HBuilderX 及推荐插件 | 手动确认 |
| 8 | 克隆项目代码并导入 IDE | 手动确认 |
| 9 | 后端运行启动类，访问 Swagger UI | `http://localhost:8888/swagger-ui/index.html` |
| 10 | 前端安装依赖并启动 | `npm install && npm run dev` |

#### 13.16.10 常见问题排查

**Q1: `mvn clean install` 报错 "无效的目标发行版: 17"**
- 原因：Maven 使用的 JDK 不是 17
- 解决：检查 `JAVA_HOME` 是否指向 JDK 17，执行 `mvn -version` 确认

**Q2: 前端 `npm install` 失败或依赖冲突**
- 原因：Node 版本不匹配或 npm 缓存损坏
- 解决：确认 Node 22.18.0+，清除缓存 `npm cache clean --force`，删除 `node_modules` 重新安装

**Q3: 后端启动报错 "无法连接 Redis"**
- 原因：Redis 服务未启动或密码不匹配
- 解决：执行 `redis-cli ping` 确认返回 `PONG`，检查密码配置

**Q4: 后端启动报错 "Access denied for user 'root'@'localhost'"**
- 原因：MySQL 账号密码与 `application.yml` 不一致
- 解决：创建本地 `application-local.yml`（不提交 Git）覆盖配置

**Q5: 前端请求后端接口报 401**
- 原因：登录 Token 未正确存储或已过期
- 解决：确认 Pinia store 存储了 Token，确认 axios 拦截器注入了 `Token` Header

### 13.17 Swagger/OpenAPI 文档访问

| 项目 | URL |
|------|-----|
| API 文档 JSON | `http://localhost:8888/v3/api-docs` |
| Swagger UI | `http://localhost:8888/swagger-ui/index.html` |

> 使用 SpringDoc OpenAPI 3（非旧版 Swagger2），注解用 `@Tag` / `@Operation`（非 `@Api` / `@ApiOperation`）。

---

## 十四、验收标准

> **[审查修订]** 数据库 20→21 张表；新增 AI 功能和收藏功能验收项；性能测试明确为实际压测。

| 验收项 | 标准 | 验证方式 |
|--------|------|---------|
| 数据库 | 21 张表全部创建成功，初始数据正确 | SQLyog/Navicat 查看 |
| 后端启动 | `mvn spring-boot:run` 成功，Swagger UI 可访问 | 浏览器访问 swagger-ui |
| PC 前端启动 | `npm run dev` 成功，页面正常加载 | 浏览器访问 localhost:5173 |
| 移动端运行 | HBuilderX 运行到浏览器 H5 正常 | HBuilderX 预览 |
| 登录认证 | PC + 移动端均可登录，Token 存入 Redis，滑动续期生效 | Redis-cli 查看 Token |
| 接口权限 | `/admin/*` 拒绝护士学员访问，`/mobile/*` 拒绝管理员访问 | 接口测试 |
| 用户/角色/菜单 | RBAC 权限体系完整，菜单动态渲染 | 功能测试 |
| 内容管理 | 类别/标签/文章/视频/PPT 的 CRUD 全部正常 | 功能测试 |
| 考试系统 | 试题管理 + 组卷 + 答题 + 自动判分 + 补考次数校验 + 成绩查询 | 功能测试 |
| 学习系统 | 学习记录保存 + 进度跟踪 + 收藏功能 + 统计图表 | 功能测试 |
| 日志管理 | AOP 自动记录操作日志，日志查询正常 | 功能测试 |
| 首页统计 | Echarts 图表正确渲染 4 项统计 + 3 个图表 | 功能测试 |
| AI 功能 | 护理问答 + 生成试题 + 个性化推荐 + 文生图 均可用 | 功能测试 |
| 移动端学习 | 文章阅读 + 视频播放 + PPT 查看 + 进度上报 + 收藏 | 真机/模拟器测试 |
| 移动端考试 | 答题 + 倒计时 + 交卷 + 查看成绩 | 真机/模拟器测试 |
| Nginx 部署 | Windows + Nginx 反向代理 + 静态资源正常 | 浏览器访问 80 端口 |
| 性能测试 | **实际压测**通过（登录 100 并发，考试提交 50 并发） | JMeter 报告 |
| 接口文档 | Swagger UI 所有接口可在线测试 | 浏览器访问 |

---

## 十五、关键风险与应对

> **[审查修订]** 新增分批交付优先级、项目回退计划、AI 隔离策略等 5 项风险管控措施。

### 15.1 技术风险

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 移动端 H5 适配 | 不同手机屏幕尺寸/浏览器兼容性 | rpx/rem 响应式 + 主流浏览器测试 |
| 文件上传大小限制 | PPT/视频文件过大 | Nginx `client_max_body_size 100m` + 后端分片上传（如需） |
| 考试并发提交 | 50+ 人同时交卷导致性能问题 | JMeter 压测验证 + Redis 缓存优化 |
| Redis 单点故障 | Token 丢失导致用户掉线 | Windows 服务自启 + 持久化配置 |
| SpringAI 依赖稳定性 | AI 依赖较新，可能阻塞后端启动 | **@ConditionalOnProperty 条件加载**，API Key 未配置时 AI Bean 不加载 |
| Vite 版本真实性 | `^7.0.0` 已确认为最新主版本（2025-06-24 发布） | 已修正，原 `^8.0.16` 不存在 |
| **Long 精度丢失（已踩坑）** | 雪花 ID 19 位超过 JS `Number.MAX_SAFE_INTEGER`（16 位），前端精度丢失导致删除/更新失败 | `JacksonConfig` 全局将 Long/BigInteger 序列化为 String；用 `modulesToInstall` 不用 `modules` 避免覆盖 `JavaTimeModule` |
| **Spring AI base-url 重复 /v1（已踩坑）** | base-url 带 `/v1` 后，Spring AI 自动追加 `/v1/chat/completions` 变成 `/v1/v1/...` → 404 | base-url 配置为 `xxx/compatible-mode`，不带 `/v1` |
| **uni-app H5 灰屏（已踩坑）** | `uni.showModal` 关闭动画未完成就调 `uni.showLoading`，遮罩叠加两层，`hideLoading` 只关一层 → 灰屏 | modal 确认回调中加 300ms 延迟，用 `finally` 确保 loading 清理；调用方传 `{ hideLoading: true }` 给 request.js |
| **端口占用导致启动失败（已踩坑）** | 命令行与 IJ 共用 8888 端口，切换启动方式时旧进程未释放 | 切换前 `Get-NetTCPConnection -LocalPort 8888 \| Stop-Process` 杀掉旧进程 |
| **考试次数统计错误（已踩坑）** | `startExam` 把进行中记录计入次数，达到 max_attempts 被拒绝无法重考 | 只统计已交卷记录（status=2），创建新记录前先删除旧的进行中记录，用 `max(attempt_count)+1` 避免唯一键冲突 |
| **移动端 NURSE 角色缺失（已踩坑）** | 用户未在 `sys_user_role` 表分配角色，移动端接口被 `AroundCut` 拦截返回 403 | 新增护士用户时必须分配 NURSE 角色（role_id=3） |

### 15.2 分批交付优先级（P0/P1/P2）

> **[审查新增]** 鉴于 18 模块 + 4 项 AI + 3 端的工作量巨大，按优先级分三批交付，每批可独立验收。

| 批次 | 优先级 | 模块 | 完成标志 |
|------|--------|------|---------|
| **第一批** | **P0 必须** | 认证登录 + 用户/角色/菜单管理 + 类别/标签/文章/视频/PPT 管理 + 学习记录 + 首页统计 + PC 端全部对应页面 | PC 管理端可完整管理培训内容，护士可登录查看 |
| **第二批** | **P1 应该** | 考试系统（试题+组卷+答题+判分+成绩）+ 学习进度跟踪 + 日志管理 + 移动端学习中心 | 学员可在移动端学习和考试 |
| **第三批** | **P2 可以延后** | AI 功能（问答+生成试题+推荐+文生图）+ 移动端考试中心 + 移动端个人中心 + 性能压测 + Nginx 部署 | 系统具备 AI 能力，完整部署上线 |

### 15.3 项目回退计划

| 回退场景 | 回退方案 | 影响范围 |
|---------|---------|---------|
| AI 功能受阻（API Key/依赖问题） | 降级为纯培训系统，AI 接口返回"功能开发中"提示 | 不影响 P0/P1 功能 |
| 移动端 H5 适配有问题 | 只交付 PC 管理端，移动端延后 | 学员通过 PC 端学习 |
| 考试系统时间不够 | 只做试题管理 + 在线答题，不做组卷和成绩导出 | 核心考试可用 |
| 9 周完不成全部 | 优先保 P0 → P1 → P2，每批独立可验收 | 保证核心功能交付 |
| Redis 故障无法恢复 | Token 降级为数据库存储（临时方案） | 性能下降但功能可用 |

### 15.4 阶段里程碑验收节点

> **[审查新增]** 每个 Phase 完成后需通过里程碑验收，未通过不进入下一 Phase。

| 里程碑 | 验收标准 | 通过条件 |
|--------|---------|---------|
| M1: 后端骨架就绪 | Maven 编译通过 + Swagger 可访问 + 数据库 21 张表建完 | 全部通过 |
| M2: 后端系统管理 | 登录/退出 + 用户/角色/菜单 CRUD + 日志记录 全部接口可调 | 全部通过 |
| M3: 后端内容管理 | 类别/标签/文章/视频/PPT CRUD + 文件上传 全部接口可调 | 全部通过 |
| M4: 后端考试+学习 | 考试全流程 + 学习记录 + 统计接口可调 | 全部通过 |
| M5: 后端 AI | AI 4 项功能接口可调（API Key 已获取，百炼 MaaS 端点已配置） | 全部通过 |
| M6: PC 端完成 | 30+ 页面全部可操作，前后端联调通过 | 全部通过 |
| M7: 移动端完成 | 15+ 页面在手机浏览器可正常使用 | 全部通过 |
| M8: 部署上线 | Nginx 部署 + 压测通过 + 文档齐全 | 全部通过 |

---

*本方案为完整可执行的企业级实施计划，涵盖 18 个功能模块 + 4 项 AI 功能、2 端（PC 管理端 + 移动端 H5）、21 张数据库表、60+ 个 API 接口。经审查修订后按"后端→PC端→移动端"横向推进。审查通过后将严格按照方案落地代码。如有调整意见，请直接标注。*
