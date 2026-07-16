package com.smart.nursing.aop;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户信息（缓存于 Redis 中的 Token 对应数据）
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 角色：ADMIN / TEACHER / NURSE
     */
    private String role;
}
