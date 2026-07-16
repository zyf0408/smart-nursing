package com.smart.nursing.controller.admin;

import com.smart.nursing.common.result.CommonResult;
import com.smart.nursing.entity.FavoriteEntity;
import com.smart.nursing.service.IFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏管理 Controller（管理端）
 */
@Tag(name = "收藏管理", description = "收藏记录查询")
@RestController
@RequestMapping("/admin/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final IFavoriteService favoriteService;

    @Operation(summary = "查询用户收藏列表")
    @PostMapping("/list")
    public CommonResult<List<FavoriteEntity>> list(@RequestParam Long userId) {
        return CommonResult.success(favoriteService.getFavoriteList(userId));
    }
}
