package com.smart.nursing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.nursing.aop.LoginUser;
import com.smart.nursing.common.enums.GlobalErrorCodeConstants;
import com.smart.nursing.common.exception.BusinessException;
import com.smart.nursing.common.utils.SecurityUtils;
import com.smart.nursing.dao.RoleMapper;
import com.smart.nursing.dao.UserMapper;
import com.smart.nursing.dao.UserRoleMapper;
import com.smart.nursing.dto.UserDto;
import com.smart.nursing.entity.RoleEntity;
import com.smart.nursing.entity.UserEntity;
import com.smart.nursing.entity.UserRoleEntity;
import com.smart.nursing.service.IUserService;
import com.smart.nursing.vo.LoginUserVo;
import com.smart.nursing.vo.MenuTreeVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 系统用户 Service 实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    private static final String TOKEN_PREFIX = "token:";
    private static final long TOKEN_EXPIRE_HOURS = 10;

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final MenuServiceImpl menuServiceImpl;

    @Override
    public LoginUserVo login(String username, String password) {
        // 1. 根据用户名查询用户
        UserEntity user = this.getOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username));
        if (user == null) {
            throw new BusinessException(GlobalErrorCodeConstants.USER_NOT_EXIST);
        }
        // 2. 校验用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(GlobalErrorCodeConstants.USER_DISABLED);
        }
        // 3. BCrypt 校验密码
        if (!SecurityUtils.matches(password, user.getPassword())) {
            throw new BusinessException(GlobalErrorCodeConstants.PASSWORD_ERROR);
        }
        // 4. 查询用户角色
        UserRoleEntity userRole = userRoleMapper.selectOne(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, user.getUserId()));
        RoleEntity role = null;
        if (userRole != null) {
            role = roleMapper.selectById(userRole.getRoleId());
        }
        // 5. 生成 UUID Token
        String token = UUID.randomUUID().toString().replace("-", "");
        // 6. 组装 LoginUser 存入 Redis（JSON，TTL 10h）
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getRealName());
        loginUser.setRole(role != null ? role.getRoleCode() : null);
        try {
            String loginUserJson = objectMapper.writeValueAsString(loginUser);
            stringRedisTemplate.opsForValue().set(TOKEN_PREFIX + token, loginUserJson, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("序列化登录用户信息失败", e);
            throw new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        }
        // 7. 组装 LoginUserVo（含菜单树）
        LoginUserVo vo = new LoginUserVo();
        vo.setToken(token);
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        if (role != null) {
            vo.setRoleId(role.getRoleId());
            vo.setRoleCode(role.getRoleCode());
            vo.setRoleName(role.getRoleName());
        }
        List<MenuTreeVo> menuTree = menuServiceImpl.getMenuTreeByUserId(user.getUserId());
        vo.setMenuList(menuTree);
        return vo;
    }

    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            stringRedisTemplate.delete(TOKEN_PREFIX + token);
        }
    }

    @Override
    public IPage<UserEntity> listUserByCondition(UserDto dto) {
        Page<UserEntity> page = new Page<>(dto.getPageNo(), dto.getPageSize());
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getUsername()), UserEntity::getUsername, dto.getUsername());
        wrapper.like(StringUtils.hasText(dto.getRealName()), UserEntity::getRealName, dto.getRealName());
        wrapper.like(StringUtils.hasText(dto.getPhone()), UserEntity::getPhone, dto.getPhone());
        wrapper.eq(dto.getUserType() != null, UserEntity::getUserType, dto.getUserType());
        wrapper.eq(dto.getStatus() != null, UserEntity::getStatus, dto.getStatus());
        wrapper.orderByDesc(UserEntity::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(UserDto dto) {
        // 1. 检查用户名唯一
        long count = this.count(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(GlobalErrorCodeConstants.USER_EXIST);
        }
        // 2. BCrypt 加密密码（若未指定密码则默认使用用户名）
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        String rawPassword = (dto.getPassword() != null && !dto.getPassword().isEmpty())
                ? dto.getPassword() : dto.getUsername();
        user.setPassword(SecurityUtils.encode(rawPassword));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        user.setUserType(dto.getUserType());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        // 3. 插入用户
        this.save(user);
        // 4. 关联角色
        if (dto.getRoleId() != null) {
            UserRoleEntity userRole = new UserRoleEntity();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(dto.getRoleId());
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserDto dto) {
        UserEntity user = this.getById(dto.getUserId());
        if (user == null) {
            throw new BusinessException(GlobalErrorCodeConstants.USER_NOT_EXIST);
        }
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        user.setUserType(dto.getUserType());
        user.setStatus(dto.getStatus());
        this.updateById(user);
        // 更新角色关联
        if (dto.getRoleId() != null) {
            userRoleMapper.delete(new LambdaQueryWrapper<UserRoleEntity>()
                    .eq(UserRoleEntity::getUserId, dto.getUserId()));
            UserRoleEntity userRole = new UserRoleEntity();
            userRole.setUserId(dto.getUserId());
            userRole.setRoleId(dto.getRoleId());
            userRoleMapper.insert(userRole);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        this.removeById(userId);
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 先删除原有角色关联
        userRoleMapper.delete(new LambdaQueryWrapper<UserRoleEntity>()
                .eq(UserRoleEntity::getUserId, userId));
        // 再插入新的角色关联
        if (!CollectionUtils.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                UserRoleEntity userRole = new UserRoleEntity();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }
}
