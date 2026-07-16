package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.nursing.dao.MenuMapper;
import com.smart.nursing.dao.RoleMapper;
import com.smart.nursing.dao.RoleMenuMapper;
import com.smart.nursing.entity.MenuEntity;
import com.smart.nursing.entity.RoleEntity;
import com.smart.nursing.entity.RoleMenuEntity;
import com.smart.nursing.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色 Service 实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements IRoleService {

    private final RoleMenuMapper roleMenuMapper;
    private final MenuMapper menuMapper;

    @Override
    public IPage<RoleEntity> listRoleByCondition(Page<RoleEntity> page, String roleName) {
        LambdaQueryWrapper<RoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), RoleEntity::getRoleName, roleName);
        wrapper.orderByDesc(RoleEntity::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 先删除原有菜单关联
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenuEntity>()
                .eq(RoleMenuEntity::getRoleId, roleId));
        // 再插入新的菜单关联
        if (!CollectionUtils.isEmpty(menuIds)) {
            for (Long menuId : menuIds) {
                RoleMenuEntity roleMenu = new RoleMenuEntity();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    public List<MenuEntity> getRoleMenus(Long roleId) {
        List<RoleMenuEntity> roleMenus = roleMenuMapper.selectList(new LambdaQueryWrapper<RoleMenuEntity>()
                .eq(RoleMenuEntity::getRoleId, roleId));
        if (CollectionUtils.isEmpty(roleMenus)) {
            return new ArrayList<>();
        }
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenuEntity::getMenuId)
                .collect(Collectors.toList());
        return menuMapper.selectBatchIds(menuIds);
    }
}
