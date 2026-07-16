package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.MenuMapper;
import com.smart.nursing.dao.RoleMenuMapper;
import com.smart.nursing.dao.UserRoleMapper;
import com.smart.nursing.entity.MenuEntity;
import com.smart.nursing.entity.RoleMenuEntity;
import com.smart.nursing.entity.UserRoleEntity;
import com.smart.nursing.service.IMenuService;
import com.smart.nursing.vo.MenuTreeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统菜单 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuEntity> implements IMenuService {

    private final UserRoleMapper userRoleMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public List<MenuTreeVo> getMenuTree() {
        List<MenuEntity> allMenus = this.list(new LambdaQueryWrapper<MenuEntity>()
                .orderByAsc(MenuEntity::getSortOrder));
        return buildMenuTree(allMenus, 0L);
    }

    @Override
    public List<MenuTreeVo> getMenuTreeByUserId(Long userId) {
        // 1. userId → user_role → 获取 roleId
        UserRoleEntity userRole = userRoleMapper.selectOne(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, userId));
        if (userRole == null) {
            return new ArrayList<>();
        }
        // 2. roleId → role_menu → 获取 menuId 列表
        List<RoleMenuEntity> roleMenus = roleMenuMapper.selectList(new LambdaQueryWrapper<RoleMenuEntity>()
                .eq(RoleMenuEntity::getRoleId, userRole.getRoleId()));
        if (CollectionUtils.isEmpty(roleMenus)) {
            return new ArrayList<>();
        }
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenuEntity::getMenuId)
                .collect(Collectors.toList());
        // 3. 查询菜单
        List<MenuEntity> menus = this.listByIds(menuIds);
        menus.sort((a, b) -> {
            int sa = a.getSortOrder() != null ? a.getSortOrder() : 0;
            int sb = b.getSortOrder() != null ? b.getSortOrder() : 0;
            return sa - sb;
        });
        // 4. 构建菜单树
        return buildMenuTree(menus, 0L);
    }

    @Override
    public List<MenuEntity> getAllMenus() {
        return this.list(new LambdaQueryWrapper<MenuEntity>()
                .orderByAsc(MenuEntity::getSortOrder));
    }

    /**
     * 递归构建菜单树
     *
     * @param menus    菜单列表
     * @param parentId 父级ID
     * @return 菜单树
     */
    private List<MenuTreeVo> buildMenuTree(List<MenuEntity> menus, Long parentId) {
        List<MenuTreeVo> tree = new ArrayList<>();
        for (MenuEntity menu : menus) {
            Long pid = menu.getParentId() == null ? 0L : menu.getParentId();
            if (pid.equals(parentId)) {
                MenuTreeVo vo = new MenuTreeVo();
                BeanUtils.copyProperties(menu, vo);
                vo.setChildren(buildMenuTree(menus, menu.getMenuId()));
                tree.add(vo);
            }
        }
        return tree;
    }
}
