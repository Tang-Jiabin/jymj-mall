package com.jymj.mall.group.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.GroupDTO;
import com.jymj.mall.mdse.vo.GroupInfo;
import com.jymj.mall.mdse.vo.MfgInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 分组
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Api(tags = "分组")
@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
public class GroupController {

    @ApiOperation(value = "添加分组")
    @PostMapping
    private Result<GroupInfo> addGroup(@Valid @RequestBody GroupDTO groupDTO) {
      
        return Result.success();
    }

    @ApiOperation(value = "删除分组")
    @DeleteMapping("/{ids}")
    private Result deleteGroup(@Valid @PathVariable String ids) {
        
        return Result.success();
    }

    @ApiOperation(value = "修改分组")
    @PutMapping
    private Result<GroupInfo> updateGroup(@RequestBody GroupDTO groupDTO) {
       
        return Result.success();
    }

    @ApiOperation(value = "分组信息")
    @GetMapping("/{groupId}/info")
    public Result<GroupInfo> getGroupById(@Valid @PathVariable Long groupId) {
       
        return Result.success();
    }

    @ApiOperation(value = "分组分页")
    @GetMapping("/pages")
    public Result pages() {
     
        return Result.success();
    }

    @ApiOperation(value = "分组列表")
    @GetMapping("/lists")
    public Result<MfgInfo> lists() {

        return Result.success();
    }
}
