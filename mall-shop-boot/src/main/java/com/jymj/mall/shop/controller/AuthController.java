package com.jymj.mall.shop.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.shop.dto.AddMallAuth;
import com.jymj.mall.shop.dto.UpdateMallAuth;
import com.jymj.mall.shop.entity.MallAuth;
import com.jymj.mall.shop.service.MallAuthService;
import com.jymj.mall.shop.vo.MallAuthInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * 商场认证
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-29
 */
@Api(tags = "商场认证")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MallAuthService mallAuthService;

    @ApiOperation(value = "添加认证")
    @PostMapping
    private Result<MallAuthInfo> addAuth(@Valid @RequestBody AddMallAuth mallAuth) {
        MallAuth auth = mallAuthService.addAuth(mallAuth);
        MallAuthInfo authInfo = mallAuthService.auth2vo(auth);
        return Result.success(authInfo);
    }

    @ApiOperation(value = "删除认证")
    @DeleteMapping("/{id}")
    private Result deleteAuth( @Valid @PathVariable Long id) {
        mallAuthService.deleteAuth(id);
        return Result.success();
    }

    @ApiOperation(value = "修改认证")
    @PutMapping
    private Result<MallAuthInfo> updateAuth(@Valid @RequestBody UpdateMallAuth updateMallAuth) {
        MallAuth mallAuth = mallAuthService.updateAuth(updateMallAuth);
        MallAuthInfo authInfo = mallAuthService.auth2vo(mallAuth);
        return Result.success(authInfo);
    }

    @ApiOperation(value = "认证信息")
    @GetMapping("/{mallId}/info")
    public Result<MallAuthInfo> getAuthByMallId(@PathVariable Long mallId) {
        Optional<MallAuth> mallAuthOptional = mallAuthService.getAuthByMallId(mallId);
        if (!mallAuthOptional.isPresent()) {
            return Result.failed("认证信息不存在");
        }
        MallAuthInfo authInfo = mallAuthService.auth2vo(mallAuthOptional.get());
        return Result.success(authInfo);
    }
}
