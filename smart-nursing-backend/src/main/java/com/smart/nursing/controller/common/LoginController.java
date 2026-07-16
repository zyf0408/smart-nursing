package com.smart.nursing.controller.common;

import com.smart.nursing.aop.NoToken;
import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.service.IUserService;
import com.smart.nursing.vo.LoginUserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录/退出 Controller
 */
@Tag(name = "登录管理", description = "用户登录与退出")
@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class LoginController {

    private final IUserService userService;

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
}
