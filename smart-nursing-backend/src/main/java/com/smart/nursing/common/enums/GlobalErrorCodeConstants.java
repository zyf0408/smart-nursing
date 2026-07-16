package com.smart.nursing.common.enums;

import com.smart.nursing.common.result.ErrorCode;
import lombok.Getter;

/**
 * 全局错误码常量
 */
@Getter
public enum GlobalErrorCodeConstants {

    /* ========== 通用错误码 ========== */
    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数不正确"),
    UNAUTHORIZED(401, "账号未登录"),
    FORBIDDEN(403, "无该操作权限"),
    NOT_FOUND(404, "请求未找到"),
    INTERNAL_SERVER_ERROR(500, "系统异常"),

    /* ========== 用户相关 ========== */
    USER_NOT_EXIST(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已禁用"),
    USER_EXIST(902, "用户名已存在"),

    /* ========== 通用增删改 ========== */
    ADD_ERROR(1004, "添加失败"),
    UPDATE_ERROR(1005, "修改失败"),
    DELETE_ERROR(1006, "删除失败"),

    /* ========== 业务相关 ========== */
    CATEGORY_HAS_CHILDREN(903, "存在子分类，不允许删除"),
    CATEGORY_HAS_CONTENT(904, "分类下存在内容，不允许删除"),
    TAG_IN_USE(905, "标签被使用中，不允许删除"),

    EXAM_NOT_AVAILABLE(906, "考试不可用"),
    EXAM_ALREADY_SUBMITTED(907, "考试已提交"),
    QUESTION_TYPE_ERROR(908, "题目类型错误"),

    /* ========== 其它 ========== */
    DEMO_DENY(901, "演示模式，禁止操作"),
    UNKNOWN(999, "未知错误");

    private final Integer code;
    private final String msg;

    GlobalErrorCodeConstants(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 转换为错误码对象
     */
    public ErrorCode toErrorCode() {
        return new ErrorCode(this.code, this.msg);
    }
}
