-- ============================================================
-- 智慧护理培训系统 - 数据库初始化脚本
-- 数据库: smart_nursing
-- 包含: 21张表 + 种子数据
-- ============================================================

CREATE DATABASE IF NOT EXISTS smart_nursing DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE smart_nursing;

-- ==================== 系统管理表 ====================

-- 1. 系统用户表
CREATE TABLE sys_user (
    user_id      BIGINT       NOT NULL COMMENT '主键',
    username     VARCHAR(50)  NOT NULL COMMENT '登录账号',
    password     VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name    VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    phone        VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email        VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar       VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    user_type    INT          DEFAULT 3 COMMENT '用户类型（1-管理员 2-培训师 3-护士学员）',
    status       INT          DEFAULT 1 COMMENT '状态（0-禁用 1-启用）',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete    INT          DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username_del (username, is_delete)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户';

-- 2. 角色表
CREATE TABLE sys_role (
    role_id      BIGINT       NOT NULL COMMENT '主键',
    role_name    VARCHAR(50)  NOT NULL COMMENT '角色名称',
    role_code    VARCHAR(50)  NOT NULL COMMENT '角色编码',
    description  VARCHAR(200) DEFAULT NULL COMMENT '描述',
    status       INT          DEFAULT 1 COMMENT '状态',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    is_delete    INT          DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_rolecode_del (role_code, is_delete)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色';

-- 3. 菜单权限表
CREATE TABLE sys_menu (
    menu_id      BIGINT       NOT NULL COMMENT '主键',
    title        VARCHAR(50)  NOT NULL COMMENT '菜单标题',
    path         VARCHAR(100) DEFAULT NULL COMMENT '路由路径',
    name         VARCHAR(50)  DEFAULT NULL COMMENT '路由名称',
    component    VARCHAR(100) DEFAULT NULL COMMENT '前端组件路径',
    icon         VARCHAR(50)  DEFAULT NULL COMMENT '图标名',
    parent_id    BIGINT       DEFAULT 0 COMMENT '父菜单ID（0为顶级）',
    sort_order   INT          DEFAULT 0 COMMENT '排序',
    visible      INT          DEFAULT 1 COMMENT '是否可见',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限';

-- 4. 角色-菜单关联表
CREATE TABLE sys_role_menu (
    role_id      BIGINT       NOT NULL COMMENT '角色ID',
    menu_id      BIGINT       NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单关联';

-- 5. 用户-角色关联表
CREATE TABLE sys_user_role (
    user_id      BIGINT       NOT NULL COMMENT '用户ID',
    role_id      BIGINT       NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联';

-- 6. 系统操作日志表
CREATE TABLE sys_log (
    log_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id      BIGINT       DEFAULT NULL COMMENT '操作用户ID',
    username     VARCHAR(50)  DEFAULT NULL COMMENT '操作用户名',
    operation    VARCHAR(200) DEFAULT NULL COMMENT '操作描述',
    method       VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    params       TEXT         DEFAULT NULL COMMENT '请求参数',
    ip           VARCHAR(50)  DEFAULT NULL COMMENT 'IP地址',
    log_type     INT          DEFAULT 1 COMMENT '日志类型（1-操作日志 2-登录日志 3-异常日志）',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (log_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志';

-- ==================== 内容管理表 ====================

-- 7. 培训类别表（树形）
CREATE TABLE nursing_category (
    category_id   BIGINT       NOT NULL COMMENT '主键',
    category_name VARCHAR(100) NOT NULL COMMENT '类别名称',
    parent_id     BIGINT       DEFAULT 0 COMMENT '父类别ID（0为顶级）',
    sort_order    INT          DEFAULT 0 COMMENT '排序',
    description   VARCHAR(500) DEFAULT NULL COMMENT '描述',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    is_delete     INT          DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训类别';

-- 8. 培训标签表
CREATE TABLE nursing_tag (
    tag_id       BIGINT       NOT NULL COMMENT '主键',
    tag_name     VARCHAR(50)  NOT NULL COMMENT '标签名称',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    is_delete    INT          DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训标签';

-- 9. 培训文章表
CREATE TABLE nursing_article (
    article_id   BIGINT       NOT NULL COMMENT '主键',
    title        VARCHAR(200) NOT NULL COMMENT '标题',
    category_id  BIGINT       DEFAULT NULL COMMENT '所属类别',
    summary      VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    content      LONGTEXT     DEFAULT NULL COMMENT '正文（富文本HTML）',
    cover_image  VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    author_id    BIGINT       DEFAULT NULL COMMENT '作者ID（关联sys_user）',
    view_count   INT          DEFAULT 0 COMMENT '浏览量',
    sort_order   INT          DEFAULT 0 COMMENT '排序',
    status       INT          DEFAULT 0 COMMENT '状态（0-草稿 1-已发布 2-已下架）',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_delete    INT          DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (article_id),
    KEY idx_category (category_id),
    KEY idx_author (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训文章';

-- 10. 培训视频表
CREATE TABLE nursing_video (
    video_id     BIGINT       NOT NULL COMMENT '主键',
    title        VARCHAR(200) NOT NULL COMMENT '标题',
    category_id  BIGINT       DEFAULT NULL COMMENT '所属类别',
    description  VARCHAR(500) DEFAULT NULL COMMENT '描述',
    video_url    VARCHAR(500) DEFAULT NULL COMMENT '视频地址',
    cover_image  VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    duration     INT          DEFAULT 0 COMMENT '时长（秒）',
    author_id    BIGINT       DEFAULT NULL COMMENT '作者ID（关联sys_user）',
    view_count   INT          DEFAULT 0 COMMENT '浏览量',
    sort_order   INT          DEFAULT 0 COMMENT '排序',
    status       INT          DEFAULT 0 COMMENT '状态（0-草稿 1-已发布 2-已下架）',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_delete    INT          DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (video_id),
    KEY idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训视频';

-- 11. 培训PPT表
CREATE TABLE nursing_ppt (
    ppt_id       BIGINT       NOT NULL COMMENT '主键',
    title        VARCHAR(200) NOT NULL COMMENT '标题',
    category_id  BIGINT       DEFAULT NULL COMMENT '所属类别',
    description  VARCHAR(500) DEFAULT NULL COMMENT '描述',
    file_url     VARCHAR(500) DEFAULT NULL COMMENT 'PPT文件地址',
    cover_image  VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    author_id    BIGINT       DEFAULT NULL COMMENT '作者ID（关联sys_user）',
    view_count   INT          DEFAULT 0 COMMENT '浏览量',
    sort_order   INT          DEFAULT 0 COMMENT '排序',
    status       INT          DEFAULT 0 COMMENT '状态（0-草稿 1-已发布 2-已下架）',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_delete    INT          DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (ppt_id),
    KEY idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训PPT';

-- 12. 文章-标签关联表
CREATE TABLE nursing_article_tag (
    article_id   BIGINT       NOT NULL COMMENT '文章ID',
    tag_id       BIGINT       NOT NULL COMMENT '标签ID',
    PRIMARY KEY (article_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章-标签关联';

-- 13. 视频-标签关联表
CREATE TABLE nursing_video_tag (
    video_id     BIGINT       NOT NULL COMMENT '视频ID',
    tag_id       BIGINT       NOT NULL COMMENT '标签ID',
    PRIMARY KEY (video_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频-标签关联';

-- 14. PPT-标签关联表
CREATE TABLE nursing_ppt_tag (
    ppt_id       BIGINT       NOT NULL COMMENT 'PPT ID',
    tag_id       BIGINT       NOT NULL COMMENT '标签ID',
    PRIMARY KEY (ppt_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='PPT-标签关联';

-- ==================== 考试管理表 ====================

-- 15. 考试表
CREATE TABLE nursing_exam (
    exam_id      BIGINT       NOT NULL COMMENT '主键',
    exam_name    VARCHAR(100) NOT NULL COMMENT '考试名称',
    description  VARCHAR(500) DEFAULT NULL COMMENT '考试说明',
    total_score  INT          DEFAULT 100 COMMENT '总分',
    pass_score   INT          DEFAULT 60 COMMENT '及格分',
    duration     INT          NOT NULL COMMENT '考试时长（分钟）',
    max_attempts INT          DEFAULT 1 COMMENT '最大考试次数',
    status       INT          DEFAULT 0 COMMENT '状态（0-未发布 1-已发布 2-已结束）',
    start_time   DATETIME     DEFAULT NULL COMMENT '开始时间',
    end_time     DATETIME     DEFAULT NULL COMMENT '结束时间',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    is_delete    INT          DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试';

-- 16. 试题表
CREATE TABLE nursing_question (
    question_id  BIGINT       NOT NULL COMMENT '主键',
    category_id  BIGINT       DEFAULT NULL COMMENT '所属类别',
    question_type INT         NOT NULL COMMENT '题型（1-单选 2-多选 3-判断 4-解答题）',
    content      TEXT         NOT NULL COMMENT '题干',
    option_a     VARCHAR(500) DEFAULT NULL COMMENT '选项A',
    option_b     VARCHAR(500) DEFAULT NULL COMMENT '选项B',
    option_c     VARCHAR(500) DEFAULT NULL COMMENT '选项C',
    option_d     VARCHAR(500) DEFAULT NULL COMMENT '选项D',
    answer       TEXT         DEFAULT NULL COMMENT '正确答案（单选/多选/判断为字母或对错，解答题存参考答案）',
    reference_answer TEXT     DEFAULT NULL COMMENT '参考答案/评分要点（解答题供AI评分使用）',
    analysis     TEXT         DEFAULT NULL COMMENT '答案解析',
    score        INT          DEFAULT 5 COMMENT '分值',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    is_delete    INT          DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (question_id),
    KEY idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试题';

-- 17. 考试-试题关联表
CREATE TABLE nursing_exam_question (
    exam_id      BIGINT       NOT NULL COMMENT '考试ID',
    question_id  BIGINT       NOT NULL COMMENT '试题ID',
    sort_order   INT          DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (exam_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试-试题关联';

-- 18. 考试记录表
CREATE TABLE nursing_exam_record (
    record_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    exam_id       BIGINT       NOT NULL COMMENT '考试ID',
    user_id       BIGINT       NOT NULL COMMENT '学员ID',
    attempt_count INT          DEFAULT 1 COMMENT '第几次考试',
    status        INT          DEFAULT 0 COMMENT '考试状态（0-未考 1-考试中 2-已交卷）',
    answers       JSON         DEFAULT NULL COMMENT '答题JSON',
    score         INT          DEFAULT NULL COMMENT '得分',
    is_pass       INT          DEFAULT 0 COMMENT '是否及格',
    start_time    DATETIME     DEFAULT NULL COMMENT '开始答题时间',
    submit_time   DATETIME     DEFAULT NULL COMMENT '交卷时间',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (record_id),
    UNIQUE KEY uk_exam_user_attempt (exam_id, user_id, attempt_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录';

-- ==================== 学习管理表 ====================

-- 19. 学习记录表
CREATE TABLE nursing_learning_record (
    record_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id        BIGINT       NOT NULL COMMENT '用户ID',
    content_type   INT          NOT NULL COMMENT '内容类型（1-文章 2-视频 3-PPT）',
    content_id     BIGINT       NOT NULL COMMENT '内容ID',
    progress       INT          DEFAULT 0 COMMENT '学习进度（0-100）',
    study_duration INT          DEFAULT 0 COMMENT '学习时长（秒）',
    last_study_time DATETIME    DEFAULT NULL COMMENT '最后学习时间',
    is_completed   INT          DEFAULT 0 COMMENT '是否完成',
    version        INT          DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (record_id),
    UNIQUE KEY uk_user_content (user_id, content_type, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习记录';

-- 20. 学习计划表
CREATE TABLE nursing_study_plan (
    plan_id            BIGINT       NOT NULL COMMENT '主键',
    user_id            BIGINT       NOT NULL COMMENT '用户ID',
    plan_name          VARCHAR(100) NOT NULL COMMENT '计划名称',
    target_content_type INT         DEFAULT NULL COMMENT '目标内容类型',
    target_content_id  BIGINT       DEFAULT NULL COMMENT '目标内容ID',
    target_progress    INT          DEFAULT 100 COMMENT '目标进度',
    deadline           DATETIME     DEFAULT NULL COMMENT '截止日期',
    status             INT          DEFAULT 0 COMMENT '状态（0-进行中 1-已完成 2-已逾期）',
    create_time        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (plan_id),
    KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习计划';

-- 21. 收藏表
CREATE TABLE nursing_favorite (
    favorite_id  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id      BIGINT       NOT NULL COMMENT '用户ID',
    content_type INT          NOT NULL COMMENT '内容类型（1-文章 2-视频 3-PPT）',
    content_id   BIGINT       NOT NULL COMMENT '内容ID',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (favorite_id),
    UNIQUE KEY uk_favorite (user_id, content_type, content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏';

-- ============================================================
-- 种子数据
-- ============================================================

-- 角色数据
INSERT INTO sys_role (role_id, role_name, role_code, description) VALUES
(1, '管理员', 'ADMIN', '系统管理员，拥有全部权限'),
(2, '培训师', 'TEACHER', '培训师，可管理培训内容和考试'),
(3, '护士学员', 'NURSE', '护士学员，可学习内容和参加考试');

-- 菜单数据
INSERT INTO sys_menu (menu_id, title, path, name, component, icon, parent_id, sort_order, visible) VALUES
(1,  '首页统计',   '/dashboard',      'dashboard',     'DashboardView',          'DataLine',     0, 1,  1),
(10, '系统管理',   NULL,              NULL,            NULL,                     'Setting',      0, 2,  1),
(11, '用户管理',   '/user',           'user',          'UserView',               'User',         10, 1, 1),
(12, '角色管理',   '/role',           'role',          'RoleView',               'UserFilled',   10, 2, 1),
(13, '菜单管理',   '/menu',           'menu',          'MenuView',               'Menu',         10, 3, 1),
(14, '日志管理',   '/log',            'log',           'LogView',                'Document',     10, 4, 1),
(20, '内容管理',   NULL,              NULL,            NULL,                     'Reading',      0, 3,  1),
(21, '类别管理',   '/category',       'category',      'CategoryView',           'Folder',       20, 1, 1),
(22, '标签管理',   '/tag',            'tag',           'TagView',                'PriceTag',     20, 2, 1),
(23, '文章管理',   '/article/list',   'articleList',   'ArticleList',            'Document',     20, 3, 1),
(24, '文章编辑',   '/article/edit',   'articleEdit',   'ArticleEdit',            'EditPen',      20, 4, 0),
(25, '视频管理',   '/video/list',     'videoList',     'VideoList',              'VideoCamera',  20, 5, 1),
(26, '视频编辑',   '/video/edit',     'videoEdit',     'VideoEdit',              'EditPen',      20, 6, 0),
(27, 'PPT管理',    '/ppt/list',       'pptList',       'PptList',                'Files',        20, 7, 1),
(28, 'PPT编辑',    '/ppt/edit',       'pptEdit',       'PptEdit',                'EditPen',      20, 8, 0),
(30, '考试管理',   NULL,              NULL,            NULL,                     'Edit',         0, 4,  1),
(31, '考试列表',   '/exam/list',      'examList',      'ExamList',               'List',         30, 1, 1),
(32, '考试编辑',   '/exam/edit',      'examEdit',      'ExamEdit',               'EditPen',      30, 2, 0),
(33, '试题库',     '/exam/question',  'questionBank',  'QuestionBank',           'QuestionFilled',30,3,1),
(34, '成绩管理',   '/examRecord/list','recordList',    'RecordList',             'Tickets',      30, 4, 1),
(35, '成绩详情',   '/examRecord/detail','recordDetail','RecordDetail',           'View',         30, 5, 0),
(40, '学习管理',   NULL,              NULL,            NULL,                     'Study',        0, 5,  1),
(41, '学习记录',   '/learning',       'learning',      'LearningRecord',         'Notebook',     40, 1, 1),
(50, 'AI 助手',    '/myai',           'myai',          'AiView',                 'ChatDotRound', 0, 6,  1);

-- 角色-菜单关联（管理员拥有全部菜单）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1,1),(1,10),(1,11),(1,12),(1,13),(1,14),
(1,20),(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),
(1,30),(1,31),(1,32),(1,33),(1,34),(1,35),
(1,40),(1,41),(1,50);

-- 培训师角色菜单（无系统管理，有内容+考试+学习管理）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2,1),(2,20),(2,21),(2,22),(2,23),(2,24),(2,25),(2,26),(2,27),(2,28),
(2,30),(2,31),(2,32),(2,33),(2,34),(2,35),(2,40),(2,41),(2,50);

-- 用户数据（密码均为 123456 的 BCrypt 加密）
INSERT INTO sys_user (user_id, username, password, real_name, phone, email, user_type, status) VALUES
(1, 'admin',   '$2a$10$N.ZOn9G6/YLFixAOPMg/h.z7pCu6v2XyFDtC4q.jeeGm/TEZyj3C6', '系统管理员', '13800138000', 'admin@nursing.com', 1, 1),
(2, 'teacher', '$2a$10$N.ZOn9G6/YLFixAOPMg/h.z7pCu6v2XyFDtC4q.jeeGm/TEZyj3C6', '张老师',     '13800138001', 'teacher@nursing.com', 2, 1),
(3, 'nurse01', '$2a$10$N.ZOn9G6/YLFixAOPMg/h.z7pCu6v2XyFDtC4q.jeeGm/TEZyj3C6', '李护士',     '13800138002', 'nurse01@nursing.com', 3, 1),
(4, 'nurse02', '$2a$10$N.ZOn9G6/YLFixAOPMg/h.z7pCu6v2XyFDtC4q.jeeGm/TEZyj3C6', '王护士',     '13800138003', 'nurse02@nursing.com', 3, 1);

-- 用户-角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 3);

-- 培训类别
INSERT INTO nursing_category (category_id, category_name, parent_id, sort_order, description) VALUES
(1, '基础护理',     0, 1, '老年护理基础知识和技能'),
(2, '专科护理',     0, 2, '老年专科护理知识'),
(3, '安全管理',     0, 3, '老年护理安全管理'),
(4, '心理护理',     0, 4, '老年心理护理'),
(5, '生活护理',     1, 1, '日常生活护理技能'),
(6, '用药护理',     1, 2, '老年用药护理'),
(7, '常见疾病护理', 2, 1, '老年常见疾病护理要点'),
(8, '急救护理',     3, 1, '老年急救护理技能');

-- 培训标签
INSERT INTO nursing_tag (tag_id, tag_name) VALUES
(1, '入门'), (2, '进阶'), (3, '高级'), (4, '必学'), (5, '实操'), (6, '理论');

-- 培训文章
INSERT INTO nursing_article (article_id, title, category_id, summary, content, cover_image, author_id, view_count, sort_order, status) VALUES
(1, '老年护理基础知识概述', 1, '老年护理的基本概念、原则和注意事项', '<h2>老年护理概述</h2><p>老年护理是指针对老年人群的生理、心理和社会需求，提供全面的护理服务...</p><p>老年护理的核心原则包括：尊重、安全、个性化、连续性。</p>', NULL, 2, 156, 1, 1),
(2, '老年人跌倒预防与处理', 3, '跌倒是老年人常见的安全问题，需重点预防', '<h2>跌倒预防</h2><p>老年人跌倒的发生率较高，后果严重...</p><ul><li>环境改造</li><li>药物管理</li><li>平衡训练</li></ul>', NULL, 2, 89, 2, 1),
(3, '老年痴呆症护理要点', 7, '阿尔茨海默病等老年痴呆的护理方法', '<h2>老年痴呆护理</h2><p>老年痴呆症是老年人常见的神经退行性疾病...</p>', NULL, 2, 67, 3, 1),
(4, '老年人口服药物管理', 6, '老年人用药安全管理的注意事项', '<h2>用药管理</h2><p>老年人药物代谢能力下降，需特别注意...</p>', NULL, 2, 45, 4, 1),
(5, '老年人心理护理策略', 4, '老年心理护理的沟通技巧和心理支持', '<h2>心理护理</h2><p>老年人常见的心理问题包括孤独、焦虑、抑郁等...</p>', NULL, 2, 34, 5, 1);

-- 培训视频
INSERT INTO nursing_video (video_id, title, category_id, description, video_url, cover_image, duration, author_id, view_count, sort_order, status) VALUES
(1, '老年人翻身拍背操作', 5, '长期卧床老年人翻身拍背的标准操作流程', '/upload/videos/turning.mp4', NULL, 600, 2, 234, 1, 1),
(2, '老年人轮椅转移技术', 5, '轮椅转移操作演示', '/upload/videos/wheelchair.mp4', NULL, 480, 2, 178, 2, 1),
(3, '老年急救心肺复苏', 8, '针对老年人的心肺复苏操作', '/upload/videos/cpr.mp4', NULL, 900, 2, 312, 3, 1);

-- 培训PPT
INSERT INTO nursing_ppt (ppt_id, title, category_id, description, file_url, cover_image, author_id, view_count, sort_order, status) VALUES
(1, '老年护理学概论', 1, '老年护理学的基本理论框架', '/upload/ppts/intro.pptx', NULL, 2, 145, 1, 1),
(2, '老年评估技术', 1, '老年综合评估方法和工具', '/upload/ppts/assessment.pptx', NULL, 2, 98, 2, 1);

-- 文章-标签关联
INSERT INTO nursing_article_tag (article_id, tag_id) VALUES
(1, 1), (1, 4), (2, 2), (2, 5), (3, 3), (3, 4), (4, 2), (5, 4);

-- 试题数据
INSERT INTO nursing_question (question_id, category_id, question_type, content, option_a, option_b, option_c, option_d, answer, analysis, score) VALUES
(1, 1, 1, '老年护理的基本原则不包括以下哪项？', '尊重老年人', '保证安全', '统一化护理', '个性化护理', 'C', '老年护理应注重个性化而非统一化', 5),
(2, 1, 1, '老年人跌倒最常见的风险因素是？', '视力下降', '药物副作用', '环境因素', '以上都是', 'D', '以上均为跌倒风险因素', 5),
(3, 3, 1, '老年人最常见的心血管疾病是？', '高血压', '冠心病', '心力衰竭', '心律失常', 'A', '高血压是老年人最常见的心血管疾病', 5),
(4, 1, 2, '老年护理中预防压疮的措施包括？', '定时翻身', '保持皮肤清洁', '使用气垫床', '营养支持', 'ABCD', '以上均为预防压疮的重要措施', 10),
(5, 7, 3, '老年痴呆症是可逆的神经退行性疾病。', NULL, NULL, NULL, NULL, 'false', '老年痴呆症是不可逆的', 5),
(6, 3, 1, '心肺复苏时胸外按压的频率应为？', '60-80次/分', '100-120次/分', '80-100次/分', '120次以上/分', 'B', 'AHA指南建议100-120次/分', 5),
(7, 6, 1, '老年人用药安全的"五种药物"原则是指？', '列出所有药物', '了解每种药物', '定期整理药箱', '以上都是', 'D', '药物管理需全面掌握', 5),
(8, 4, 3, '老年人孤独感是正常的心理现象，无需干预。', NULL, NULL, NULL, NULL, 'false', '孤独感需及时干预和疏导', 5),
(9, 2, 2, '老年护理评估的内容包括？', '躯体功能评估', '认知功能评估', '心理状态评估', '社会支持评估', 'ABCD', '综合评估需涵盖以上所有方面', 10),
(10, 1, 1, '老年人每日饮水量建议为？', '500-1000ml', '1000-1500ml', '1500-2000ml', '2000ml以上', 'C', '建议每日饮水1500-2000ml', 5);

-- 考试数据
INSERT INTO nursing_exam (exam_id, exam_name, description, total_score, pass_score, duration, max_attempts, status, start_time, end_time) VALUES
(1, '老年护理基础知识考试', '涵盖基础护理、安全管理和专科护理知识', 60, 36, 60, 2, 1, '2025-01-01 00:00:00', '2026-12-31 23:59:59'),
(2, '老年急救技能考核', '心肺复苏、跌倒处理等急救技能', 30, 18, 30, 1, 1, '2025-01-01 00:00:00', '2026-12-31 23:59:59');

-- 考试-试题关联
INSERT INTO nursing_exam_question (exam_id, question_id, sort_order) VALUES
(1, 1, 1), (1, 2, 2), (1, 3, 3), (1, 4, 4), (1, 5, 5), (1, 6, 6), (1, 7, 7), (1, 8, 8), (1, 9, 9), (1, 10, 10),
(2, 6, 1), (2, 2, 2), (2, 4, 3), (2, 9, 4), (2, 8, 5), (2, 5, 6);

-- 学习记录
INSERT INTO nursing_learning_record (record_id, user_id, content_type, content_id, progress, study_duration, last_study_time, is_completed, version) VALUES
(1, 3, 1, 1, 100, 1800, '2025-06-01 10:00:00', 1, 0),
(2, 3, 2, 1, 85, 540, '2025-06-02 14:00:00', 0, 0),
(3, 3, 1, 2, 60, 900, '2025-06-03 09:00:00', 0, 0),
(4, 4, 1, 1, 100, 1800, '2025-06-01 11:00:00', 1, 0),
(5, 4, 2, 3, 50, 450, '2025-06-02 15:00:00', 0, 0);

-- 收藏数据
INSERT INTO nursing_favorite (favorite_id, user_id, content_type, content_id) VALUES
(1, 3, 1, 1), (2, 3, 2, 1), (3, 4, 1, 3);

-- 学习计划
INSERT INTO nursing_study_plan (plan_id, user_id, plan_name, target_content_type, target_content_id, target_progress, deadline, status) VALUES
(1, 3, '6月基础护理学习计划', 1, 1, 100, '2025-06-30 23:59:59', 1),
(2, 3, '急救技能学习计划', 2, 3, 100, '2025-07-15 23:59:59', 0),
(3, 4, '护理操作技能提升', 2, 1, 100, '2025-07-01 23:59:59', 0);
