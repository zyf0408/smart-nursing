package com.smart.nursing.controller.common;

import com.smart.nursing.aop.LoginUser;
import com.smart.nursing.aop.NoToken;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.service.IMenuService;
import com.smart.nursing.service.IUserService;
import com.smart.nursing.vo.LoginUserVo;
import com.smart.nursing.vo.MenuTreeVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录/退出 Controller
 */
@Tag(name = "登录管理", description = "用户登录与退出")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final IUserService userService;
    private final IMenuService menuService;

    @NoToken
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public CommonResult<LoginUserVo> login(@RequestParam String username,
                                           @RequestParam String password) {
        LoginUserVo loginUserVo = userService.login(username, password);
        return CommonResult.success(loginUserVo);
    }

    @Operation(summary = "退出登录")
    @GetMapping("/logout")
    public CommonResult<Void> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        userService.logout(token);
        return CommonResult.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/auth/info")
    public CommonResult<Map<String, Object>> info(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        Map<String, Object> info = new HashMap<>();
        info.put("userId", loginUser.getUserId());
        info.put("username", loginUser.getUsername());
        info.put("nickname", loginUser.getNickname());
        info.put("roleCode", loginUser.getRole());
        return CommonResult.success(info);
    }

    @Operation(summary = "获取当前用户菜单列表")
    @GetMapping("/auth/menus")
    public CommonResult<List<MenuTreeVo>> menus(HttpServletRequest request) {
        LoginUser loginUser = (LoginUser) request.getAttribute("loginUser");
        List<MenuTreeVo> menuTree = menuService.getMenuTreeByUserId(loginUser.getUserId());
        return CommonResult.success(menuTree);
    }
}
