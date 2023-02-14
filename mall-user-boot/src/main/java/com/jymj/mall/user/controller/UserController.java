package com.jymj.mall.user.controller;


import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import com.jymj.mall.common.enums.EnumTypeInfo;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.result.ResultCode;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.user.dto.UserAuthDTO;
import com.jymj.mall.user.dto.UserDTO;
import com.jymj.mall.user.dto.UserPageQuery;
import com.jymj.mall.user.dto.WxUpdateMobile;
import com.jymj.mall.user.entity.MallUser;
import com.jymj.mall.user.enums.MemberEnum;
import com.jymj.mall.user.enums.SourceEnum;
import com.jymj.mall.user.service.UserService;
import com.jymj.mall.user.vo.UserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Api(tags = "用户")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final WxMaService wxMaService;


    @ApiIgnore
    @GetMapping("/openId/{openId}")
    public Result<UserAuthDTO> loadUserByOpenid(@PathVariable String openId) {
        Optional<UserAuthDTO> userAuthOptional = userService.loadUserByOpenid(openId);
        return userAuthOptional.map(Result::success).orElse(Result.failed(ResultCode.USER_NOT_EXIST));
    }

    @ApiIgnore
    @GetMapping("/username/{username}")
    public Result<UserAuthDTO> loadUserByUsername( @PathVariable String username){
        Optional<UserAuthDTO> userAuthOptional = userService.loadUserByUsername(username);
        return userAuthOptional.map(Result::success).orElse(Result.failed(ResultCode.USER_NOT_EXIST));
    }

    @ApiIgnore
    @GetMapping("/mobile/{mobile}")
    public Result<UserAuthDTO> loadUserByMobile( @PathVariable String mobile){
        Optional<UserAuthDTO> userAuthOptional = userService.loadUserByMobile(mobile);
        return userAuthOptional.map(Result::success).orElse(Result.failed(ResultCode.USER_NOT_EXIST));
    }


    @ApiOperation(value = "添加用户")
    @PostMapping
    public Result<UserInfo> addUser(@Valid @RequestBody UserDTO userDTO) {
        MallUser mallUser = userService.add(userDTO);
        UserInfo userInfo = userService.entity2vo(mallUser);
        return Result.success(userInfo);
    }


    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{ids}")
    public Result<String> deleteUser(@Valid @PathVariable String ids) {
        userService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改用户")
    @PutMapping
    public Result<UserInfo> updateUser(@RequestBody UserDTO userDTO) {
        Optional<MallUser> userOptional = userService.update(userDTO);
        return userOptional.map(user->Result.success(userService.entity2vo(user))).orElseGet(() -> Result.failed("更新失败"));
    }


    @ApiOperation(value = "修改用户手机号")
    @PutMapping("/{userId}/phone")
    public Result<UserInfo> phone(@PathVariable Long userId, @Valid @RequestBody WxUpdateMobile wxUpdateMobile) {

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(wxUpdateMobile.getSessionKey(), wxUpdateMobile.getEncryptedData(), wxUpdateMobile.getIv());
        //清理ThreadLocal
        WxMaConfigHolder.remove();

        Optional<MallUser> userOptional = userService.update(UserDTO.builder().userId(userId).mobile(phoneNoInfo.getPhoneNumber()).build());

        return userOptional.map(entity -> Result.success(userService.entity2vo(entity))).orElse(Result.failed("更新失败"));
    }

    @ApiOperation(value = "用户信息")
    @GetMapping("/{userId}/info")
    @Cacheable(value="mall-user:user-info:",key="'user-id:'+#userId")
    public Result<UserInfo> getUserById(@Valid @PathVariable Long userId) {

        Optional<MallUser> userOptional = userService.findById(userId);

        return userOptional.map(mallUser -> Result.success(userService.entity2vo(mallUser))).orElseGet(() -> Result.failed(ResultCode.USER_NOT_EXIST));
    }

    @ApiOperation(value = "用户分页")
    @GetMapping("/pages")
    public Result<PageVO<UserInfo>> pages(UserPageQuery userPageQuery) {
        Page<MallUser> page = userService.findPage(userPageQuery);
        List<UserInfo> mallUserList = userService.list2vo(page.getContent());
        PageVO<UserInfo> pageVo = PageUtils.toPageVO(page, mallUserList);
        return Result.success(pageVo);
    }

    @ApiOperation(value = "用户来源列表")
    @GetMapping("/source/lists")
    public Result<List<EnumTypeInfo>> sourceLists() {
        return Result.success(SourceEnum.toList());
    }

    @ApiOperation(value = "用户身份列表")
    @GetMapping("/member/lists")
    public Result<List<EnumTypeInfo>> memberLists() {
        return Result.success(MemberEnum.toList());
    }
}
