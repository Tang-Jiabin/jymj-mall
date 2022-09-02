package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.mdse.dto.GroupDTO;
import com.jymj.mall.mdse.dto.GroupPageQuery;
import com.jymj.mall.mdse.entity.MdseGroup;
import com.jymj.mall.mdse.service.GroupService;
import com.jymj.mall.mdse.vo.GroupInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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

    private final GroupService groupService;

    @ApiOperation(value = "添加分组")
    @PostMapping
    private Result<GroupInfo> addGroup(@Valid @RequestBody GroupDTO groupDTO) {
        MdseGroup group = groupService.add(groupDTO);
        GroupInfo groupInfo = groupService.entity2vo(group);
        return Result.success(groupInfo);
    }

    @ApiOperation(value = "删除分组")
    @DeleteMapping("/{ids}")
    private Result deleteGroup(@Valid @PathVariable String ids) {
        groupService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改分组")
    @PutMapping
    private Result<GroupInfo> updateGroup(@RequestBody GroupDTO groupDTO) {
        Optional<MdseGroup> groupOptional = groupService.update(groupDTO);
        return groupOptional.map(entity -> Result.success(groupService.entity2vo(entity))).orElse(Result.failed("更新失败"));
    }

    @ApiOperation(value = "分组信息")
    @GetMapping("/{groupId}/info")
    public Result<GroupInfo> getGroupById(@Valid @PathVariable Long groupId) {
        Optional<MdseGroup> groupOptional = groupService.findById(groupId);
        return groupOptional.map(entity -> Result.success(groupService.entity2vo(entity))).orElse(Result.failed("分组信息不存在"));
    }

    @ApiOperation(value = "分组分页")
    @GetMapping("/pages")
    public Result pages(GroupPageQuery groupPageQuery) {
        Page<MdseGroup> page = groupService.findPage(groupPageQuery);
        List<GroupInfo> groupInfoList = groupService.list2vo(page.getContent());
        PageVO<GroupInfo> pageVo = PageUtils.toPageVO(page, groupInfoList);
        return Result.success(pageVo);
    }

    @ApiOperation(value = "分组列表")
    @GetMapping("/lists")
    public Result<List<GroupInfo>> lists() {
        List<MdseGroup> groupList = groupService.findAllByAuth();
        List<GroupInfo> groupInfoList = groupService.list2vo(groupList);
        return Result.success(groupInfoList);
    }
}
