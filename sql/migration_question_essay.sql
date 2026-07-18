-- 迁移脚本：试题表新增解答题题型支持（用于已初始化的 smart_nursing 数据库）
-- 执行方式：mysql -uroot -p123456 smart_nursing < migration_question_essay.sql

ALTER TABLE nursing_question
    MODIFY COLUMN answer TEXT DEFAULT NULL COMMENT '正确答案（单选/多选/判断为字母或对错，解答题存参考答案）',
    ADD COLUMN reference_answer TEXT DEFAULT NULL COMMENT '参考答案/评分要点（解答题供AI评分使用）' AFTER answer,
    MODIFY COLUMN question_type INT NOT NULL COMMENT '题型（1-单选 2-多选 3-判断 4-解答题）';
