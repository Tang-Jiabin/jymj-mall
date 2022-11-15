package com.jymj.mall.mdse.controller;

import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.vo.PageVO;
import com.jymj.mall.mdse.dto.*;
import com.jymj.mall.mdse.entity.MallMdse;
import com.jymj.mall.mdse.entity.MdsePurchaseRecord;
import com.jymj.mall.mdse.service.MdseService;
import com.jymj.mall.mdse.vo.MdseInfo;
import com.jymj.mall.mdse.vo.MdsePurchaseRecordInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Api(tags = "商品")
@Slf4j
@RestController
@RequestMapping("/api/v1/mdse")
@RequiredArgsConstructor
public class MdseController {

    private final MdseService mdseService;


    @ApiOperation(value = "添加商品")
    @PostMapping
    public Result<MdseInfo> addMdse(@Valid @RequestBody MdseDTO mdseDTO) {
        MallMdse mallMdse = mdseService.add(mdseDTO);
        MdseInfo mdseInfo = mdseService.entity2vo(mallMdse);
        mdseService.syncToElasticAddMdseInfo(mdseInfo);
        return Result.success(mdseInfo);
    }

    @ApiOperation(value = "删除商品")
    @DeleteMapping("/{ids}")
    public Result<Object> deleteMdse(@Valid @PathVariable String ids) {
        mdseService.delete(ids);
        mdseService.syncToElasticDeleteMdseInfo(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改商品")
    @PutMapping
    public Result<MdseInfo> updateMdse(@RequestBody MdseDTO mdseDTO) {
        Optional<MallMdse> mdseOptional = mdseService.update(mdseDTO);
        if (mdseOptional.isPresent()) {
            MdseInfo mdseInfo = mdseService.entity2vo(mdseOptional.get());
            mdseService.syncToElasticUpdateMdseInfo(mdseInfo);
            return Result.success(mdseInfo);
        }

        return Result.failed("修改失败");
    }

    @ApiOperation(value = "修改商品销量")
    @PutMapping("/salesVolume")
    public Result<MdseInfo> updateMdseSalesVolume(@RequestBody MdseDTO mdseDTO) {
        MdseDTO salesDTO = new MdseDTO();
        salesDTO.setMdseId(mdseDTO.getMdseId());
        salesDTO.setSalesVolume(mdseDTO.getSalesVolume());

        Optional<MallMdse> mdseOptional = mdseService.update(salesDTO);
        if (mdseOptional.isPresent()) {
            MdseInfo mdseInfo = mdseService.entity2vo(mdseOptional.get());
            mdseService.syncToElasticUpdateMdseInfo(mdseInfo);
            return Result.success(mdseInfo);
        }

        return Result.failed("修改失败");
    }


    @ApiOperation(value = "修改商品状态")
    @PutMapping("/status")
    public Result<MdseInfo> updateMdseStatus(@RequestBody MdseStatusDTO mdseDTO) {
        mdseService.updateStatus(mdseDTO);
        mdseService.syncToElasticUpdateMdseInfoList(mdseDTO.getMdseIds());
        return Result.success();
    }

    @ApiOperation(value = "商品信息")
    @GetMapping("/{mdseId}/info")
    public Result<MdseInfo> getMdseById(@Valid @PathVariable Long mdseId) {
        Optional<MallMdse> mdseOptional = mdseService.findById(mdseId);
        if (mdseOptional.isPresent()) {
            MallMdse mallMdse = mdseOptional.get();
            MdseInfo mdseInfo = mdseService.entity2vo(mallMdse, MdseInfoShow.builder().build());
            return Result.success(mdseInfo);
        }
        return Result.failed("商品不存在");
    }

    @ApiOperation(value = "商品信息")
    @GetMapping("/{mdseId}/optional")
    public Result<MdseInfo> getMdseOptionalById(@Valid @PathVariable Long mdseId, MdseInfoShow show) {
        Optional<MallMdse> mdseOptional = mdseService.findById(mdseId);
        if (mdseOptional.isPresent()) {
            MallMdse mallMdse = mdseOptional.get();
            MdseInfo mdseInfo = mdseService.entity2vo(mallMdse, show);
            return Result.success(mdseInfo);
        }
        return Result.failed("商品不存在");
    }

    @ApiOperation(value = "商品id查询")
    @GetMapping("/all/optional/{ids}")
    public Result<List<MdseInfo>> getAllMdseOptionalByIds(@Valid @PathVariable String ids, @SpringQueryMap MdseInfoShow show) {
        List<Long> lids = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<MallMdse> mallMdseList = mdseService.findAllById(lids);
        List<MdseInfo> mdseInfoList = mdseService.list2vo(mallMdseList, show);
        return Result.success(mdseInfoList);
    }

    @ApiOperation(value = "根据店铺id查询商品")
    @GetMapping("/all/shop/{ids}")
    public Result<List<MdseInfo>> getAllMdseOptionalByShopIds(@Valid @PathVariable String ids, @SpringQueryMap MdseInfoShow show) {
        List<Long> lids = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<MallMdse> mallMdseList = mdseService.findAllByShopIds(lids);
        List<MdseInfo> mdseInfoList = mdseService.list2vo(mallMdseList, show);
        return Result.success(mdseInfoList);
    }

    @ApiOperation(value = "商品分页")
    @GetMapping("/pages")
    public Result<PageVO<MdseInfo>> pages(MdsePageQuery mdsePageQuery) {
        Page<MallMdse> page = mdseService.findPage(mdsePageQuery);
        List<MdseInfo> mdseInfoList = mdseService.list2vo(page.getContent(), MdseInfoShow.builder().picture(1).stock(1).build());
        PageVO<MdseInfo> pageVo = PageUtils.toPageVO(page, mdseInfoList);

        return Result.success(pageVo);
    }


    @ApiOperation(value = "刷新商品到elastic")
    @PutMapping("/refreshToElastic")
    public Result<String> refreshToElastic() {
        List<MallMdse> mdseList = mdseService.findAll();
        List<MdseInfo> mdseInfoList = Lists.newArrayList();
        mdseList.forEach(mdse -> mdseInfoList.add(mdseService.entity2vo(mdse)));
        mdseInfoList.forEach(mdseService::syncToElasticAddMdseInfo);
        return Result.success();
    }

    @ApiIgnore
    @ApiOperation(value = "添加购买记录")
    @PostMapping("/mdsePurchaseRecord")
    public Result<MdseInfo> addMdsePurchaseRecord(@RequestBody MdsePurchaseRecordDTO recordDTO) {
        mdseService.addMdsePurchaseRecord(recordDTO);

        return Result.success();
    }

    @ApiOperation(value = "购买人员分页")
    @GetMapping("/buyer/pages")
    public Result<PageVO<MdsePurchaseRecordInfo>> buyerPages(BuyerPageQuery buyerPageQuery) {
        Page<MdsePurchaseRecord> page = mdseService.findBuyerPage(buyerPageQuery);
        List<MdsePurchaseRecordInfo> mdseInfoList = mdseService.purchaseRecordList2vo(page.getContent());
        PageVO<MdsePurchaseRecordInfo> pageVo = PageUtils.toPageVO(page, mdseInfoList);

        return Result.success(pageVo);
    }

    @ApiOperation(value = "按商品搜索购买记录")
    @GetMapping("/purchaseRecord/mdseId/{mdseId}")
    public Result<List<MdsePurchaseRecordDTO>> getPurchaseRecordByMdseId(@PathVariable Long mdseId) {
        List<MdsePurchaseRecord> purchaseRecordList = mdseService.getAllPurchaseRecordByMdseId(mdseId);
        List<MdsePurchaseRecordDTO> purchaseRecordDTOList = Lists.newArrayList();
        purchaseRecordList.forEach(purchaseRecord -> purchaseRecordDTOList.add(MdsePurchaseRecordDTO.builder()
                .orderId(purchaseRecord.getOrderId())
                .type(purchaseRecord.getType())
                .mdseId(purchaseRecord.getMdseId())
                .userId(purchaseRecord.getUserId())
                .build()));
        return Result.success(purchaseRecordDTOList);
    }

    @ApiOperation(value = "按类型搜索购买记录")
    @GetMapping("/purchaseRecord/type/{type}")
    public Result<List<MdsePurchaseRecordDTO>> getPurchaseRecordByType(@PathVariable Integer type) {
        List<MdsePurchaseRecord> purchaseRecordList = mdseService.getAllPurchaseRecordByType(type);
        List<MdsePurchaseRecordDTO> purchaseRecordDTOList = Lists.newArrayList();
        purchaseRecordList.forEach(purchaseRecord -> purchaseRecordDTOList.add(MdsePurchaseRecordDTO.builder()
                .orderId(purchaseRecord.getOrderId())
                .type(purchaseRecord.getType())
                .mdseId(purchaseRecord.getMdseId())
                .userId(purchaseRecord.getUserId())
                .build()));
        return Result.success(purchaseRecordDTOList);
    }
}
