package com.jymj.mall.shop.controller;


import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.shop.dto.VerifyOrderMdse;
import com.jymj.mall.shop.dto.VerifyPersonDTO;
import com.jymj.mall.shop.dto.VerifyPersonPageQuery;
import com.jymj.mall.shop.entity.VerifyPerson;
import com.jymj.mall.shop.service.VerifyService;
import com.jymj.mall.shop.vo.VerifyPersonInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 核销订单
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-26
 */
@Api(tags = "核销订单")
@RestController
@RequestMapping("/api/v1/verification")
@RequiredArgsConstructor
public class VerifyController {

    private final VerifyService verifyService;


    @ApiOperation(value = "添加核销员")
    @PostMapping
    public Result<VerifyPersonInfo> addVerifyPerson(@Valid @RequestBody VerifyPersonDTO verifyPersonDTO) {
        VerifyPerson verifyPerson = verifyService.add(verifyPersonDTO);
        VerifyPersonInfo verifyPersonInfo = verifyService.entity2vo(verifyPerson);

        return Result.success(verifyPersonInfo);
    }

    @ApiOperation(value = "删除核销员")
    @DeleteMapping("/id/{ids}")
    public Result<String> deleteVerifyPerson(@Valid @PathVariable String ids) {
        verifyService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "删除核销员")
    @DeleteMapping("/adminId/{ids}")
    public Result<String> deleteVerifyPersonByAdminIds(@Valid @PathVariable String ids) {
        List<Long> adminIds = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<VerifyPerson> verifyPeopleList = verifyService.findAllByAdminIdIn(adminIds);
        verifyService.deleteAll(verifyPeopleList);
        return Result.success();
    }

    @ApiOperation(value = "修改核销员")
    @PutMapping
    public Result<VerifyPersonInfo> updateVerifyPerson(@RequestBody VerifyPersonDTO verifyPersonDTO) {
        Optional<VerifyPerson> verifyPersonOptional = verifyService.update(verifyPersonDTO);
        if (verifyPersonOptional.isPresent()) {
            VerifyPersonInfo verifyPersonInfo = verifyService.entity2vo(verifyPersonOptional.get());

            return Result.success(verifyPersonInfo);
        }
        return Result.failed("更新失败");
    }

    @ApiOperation(value = "核销员信息 verifyPersonId")
    @GetMapping("/id/{verifyPersonId}/info")
    public Result<VerifyPersonInfo> getVerifyPersonById(@Valid @PathVariable Long verifyPersonId) {
        Optional<VerifyPerson> cardOptional = verifyService.findById(verifyPersonId);
        return cardOptional.map(entity -> Result.success(verifyService.entity2vo(entity))).orElse(Result.failed("核销员信息不存在"));
    }

    @ApiOperation(value = "核销员信息 adminId")
    @GetMapping("/adminId/{adminId}/info")
    public Result<VerifyPersonInfo> getVerifyPersonByAdminId(@Valid @PathVariable Long adminId) {
        Optional<VerifyPerson> cardOptional = verifyService.findByAdminId(adminId);
        return cardOptional.map(entity -> Result.success(verifyService.entity2vo(entity))).orElse(Result.failed("核销员信息不存在"));
    }

    @ApiOperation(value = "核销员信息 userId")
    @GetMapping("/userId/{userId}/info")
    public Result<VerifyPersonInfo> getVerifyPersonByUserId(@Valid @PathVariable Long userId) {
        Optional<VerifyPerson> cardOptional = verifyService.findByUserId(userId);
        return cardOptional.map(entity -> Result.success(verifyService.entity2vo(entity))).orElse(Result.failed("核销员信息不存在"));
    }

    @ApiOperation(value = "核销员分页")
    @GetMapping("/pages")
    public Result<PageVO<VerifyPersonInfo>> pages(VerifyPersonPageQuery verifyPersonPageQuery) {
        Page<VerifyPerson> page = verifyService.findPage(verifyPersonPageQuery);
        List<VerifyPersonInfo> cardInfoList = verifyService.list2vo(page.getContent());
        PageVO<VerifyPersonInfo> pageVo = PageUtils.toPageVO(page, cardInfoList);
        return Result.success(pageVo);
    }

    @ApiOperation(value = "核销商品")
    @PostMapping("/order/mdse")
    public Result<VerifyPersonInfo> verifyOrderMdse(@Valid @RequestBody VerifyOrderMdse verifyOrderMdse) {
        verifyService.verify(verifyOrderMdse);
        return Result.success();
    }

}
