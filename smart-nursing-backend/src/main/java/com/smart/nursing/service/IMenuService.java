package com.smart.nursing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smart.nursing.entity.MenuEntity;
import com.smart.nursing.vo.MenuTreeVo;

import java.util.List;

/**
 * 系统菜单 Service 接口
 */
public interface IMenuService extends IService<MenuEntity> {

    /**
     * 获取完整菜单树
     *
     * @return 菜单树
     */
    List<MenuTreeVo> getMenuTree();

    /**
     * 根据用户ID获取菜单树
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<MenuTreeVo> getMenuTreeByUserId(Long userId);

    /**
     * 获取全部菜单（平铺列表）
     *
     * @return 菜单列表
     */
    List<MenuEntity> getAllMenus();
}
