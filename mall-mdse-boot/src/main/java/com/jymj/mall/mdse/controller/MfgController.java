package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.mdse.dto.MfgDTO;
import com.jymj.mall.mdse.entity.MdseMfg;
import com.jymj.mall.mdse.service.MfgService;
import com.jymj.mall.mdse.vo.MfgInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 厂家
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Api(tags = "厂家")
@RestController
@RequestMapping("/api/v1/mfg")
@RequiredArgsConstructor
public class MfgController {

    private final MfgService mfgService;

    @ApiOperation(value = "添加厂家")
    @PostMapping
    public Result<MfgInfo> addMfg(@Valid @RequestBody MfgDTO mfgDTO) {
        MdseMfg mdseMfg = mfgService.add(mfgDTO);
        MfgInfo mfgInfo = mfgService.entity2vo(mdseMfg);
        return Result.success(mfgInfo);
    }

    @ApiOperation(value = "删除厂家")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteMfg(@Valid @PathVariable String ids) {
        mfgService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改厂家")
    @PutMapping
    public  Result<MfgInfo> updateMfg(@RequestBody MfgDTO mfgDTO) {
        Optional<MdseMfg> mfgOptional = mfgService.update(mfgDTO);
        return mfgOptional.map(entity -> Result.success(mfgService.entity2vo(entity))).orElse(Result.failed("更新失败"));
    }

    @ApiOperation(value = "厂家信息")
    @GetMapping("/{mfgId}/info")
    public Result<MfgInfo> getMfgById(@Valid @PathVariable Long mfgId) {
        Optional<MdseMfg> mfgOptional = mfgService.findById(mfgId);
        return mfgOptional.map(entity -> Result.success(mfgService.entity2vo(entity))).orElse(Result.failed("厂家不存在"));
    }

    @ApiOperation(value = "厂家列表")
    @GetMapping("/lists")
    public Result< List<MfgInfo> > lists(Long mallId) {
        List<MdseMfg> mfgList =  mfgService.findAllByMallId(mallId);
        List<MfgInfo> mfgInfoList = mfgService.list2vo(mfgList);
        return Result.success(mfgInfoList);
    }
}
