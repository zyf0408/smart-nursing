package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.dto.UserDto;
import com.smart.nursing.entity.UserEntity;
import com.smart.nursing.vo.LoginUserVo;

import java.util.List;

/**
 * 系统用户 Service 接口
 */
public interface IUserService extends IService<UserEntity> {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 登录用户信息（含 Token 和菜单树）
     */
    LoginUserVo login(String username, String password);

    /**
     * 退出登录
     *
     * @param token 访问令牌
     */
    void logout(String token);

    /**
     * 分页条件查询用户
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<UserEntity> listUserByCondition(UserDto dto);

    /**
     * 新增用户
     *
     * @param dto 用户信息
     */
    void addUser(UserDto dto);

    /**
     * 修改用户
     *
     * @param dto 用户信息
     */
    void updateUser(UserDto dto);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 给用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID集合
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     */
    void resetPassword(Long userId);
}
