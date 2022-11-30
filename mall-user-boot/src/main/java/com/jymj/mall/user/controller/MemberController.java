package com.jymj.mall.user.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.result.ResultCode;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.user.dto.MemberDTO;
import com.jymj.mall.user.dto.MemberPageQuery;
import com.jymj.mall.user.entity.MallMember;
import com.jymj.mall.user.service.MemberService;
import com.jymj.mall.user.vo.MemberInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 会员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-07
 */
@Api(tags = "会员")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;



    @ApiOperation(value = "添加会员")
    @PostMapping
    public Result<MemberInfo> addMember(@Valid @RequestBody MemberDTO memberDTO) {
        MallMember mallMember = memberService.add(memberDTO);
        MemberInfo memberInfo = memberService.entity2vo(mallMember);
        return Result.success(memberInfo);
    }


    @ApiOperation(value = "删除会员")
    @DeleteMapping("/{ids}")
    public Result<String> deleteMember(@Valid @PathVariable String ids) {
        memberService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改会员")
    @PutMapping
    public Result<MemberInfo> updateMember(@RequestBody MemberDTO memberDTO) {
        Optional<MallMember> memberOptional = memberService.update(memberDTO);
        return memberOptional.map(member->Result.success(memberService.entity2vo(member))).orElseGet(() -> Result.failed("更新失败"));
    }
    

    @ApiOperation(value = "会员信息")
    @GetMapping("/{memberId}/info")
    public Result<MemberInfo> getMemberById(@Valid @PathVariable Long memberId) {

        Optional<MallMember> memberOptional = memberService.findById(memberId);

        return memberOptional.map(mallMember -> Result.success(memberService.entity2vo(mallMember))).orElseGet(() -> Result.failed(ResultCode.USER_NOT_EXIST));
    }

    @ApiOperation(value = "会员信息")
    @GetMapping("/user/{userId}/info")
    public Result<MemberInfo> getMemberByUserId(@Valid @PathVariable Long userId) {

        Optional<MallMember> memberOptional = memberService.findByUserId(userId);

        return memberOptional.map(mallMember -> Result.success(memberService.entity2vo(mallMember))).orElseGet(() -> Result.failed(ResultCode.USER_NOT_EXIST));
    }

    @ApiOperation(value = "会员分页")
    @GetMapping("/pages")
    public Result<PageVO<MemberInfo>> pages(MemberPageQuery memberPageQuery) {
        Page<MallMember> page = memberService.findPage(memberPageQuery);
        List<MemberInfo> mallMemberList = memberService.list2vo(page.getContent());
        PageVO<MemberInfo> pageVo = PageUtils.toPageVO(page, mallMemberList);
        return Result.success(pageVo);
    }

}
