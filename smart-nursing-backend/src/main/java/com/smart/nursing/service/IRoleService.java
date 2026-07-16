package com.smart.nursing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.entity.MenuEntity;
import com.smart.nursing.entity.RoleEntity;

import java.util.List;

/**
 * 系统角色 Service 接口
 */
public interface IRoleService extends IService<RoleEntity> {

    /**
     * 分页条件查询角色
     *
     * @param page     分页参数
     * @param roleName 角色名称（模糊查询）
     * @return 分页结果
     */
    IPage<RoleEntity> listRoleByCondition(Page<RoleEntity> page, String roleName);

    /**
     * 给角色分配菜单
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID集合
     */
    void assignMenus(Long roleId, List<Long> menuIds);

    /**
     * 查询角色拥有的菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<MenuEntity> getRoleMenus(Long roleId);
}
