package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.*;
import com.jymj.mall.mdse.entity.MallMdse;
import com.jymj.mall.mdse.entity.MdseCardRules;
import com.jymj.mall.mdse.entity.MdsePurchaseRecord;
import com.jymj.mall.mdse.vo.EffectiveRulesInfo;
import com.jymj.mall.mdse.vo.GroupInfo;
import com.jymj.mall.mdse.vo.MdseInfo;
import com.jymj.mall.mdse.vo.MdsePurchaseRecordInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * 商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
public interface MdseService extends BaseService<MallMdse, MdseInfo, MdseDTO> {
    Page<MallMdse> findPage(MdsePageQuery mdsePageQuery);

    List<GroupInfo> findGroupListByMdseId(Long mdseId);

    MdseInfo entity2vo(MallMdse mallMdse, MdseInfoShow show);

    List<MdseInfo> list2vo(List<MallMdse> entityList, MdseInfoShow show);

    List<MallMdse> findAllById(List<Long> idList);

    void syncToElasticAddMdseInfo(MdseInfo mdseInfo);

    void syncToElasticUpdateMdseInfo(MdseInfo mdseInfo);

    void syncToElasticDeleteMdseInfo(String ids);

    void updateStatus(MdseStatusDTO mdseDTO);

    void syncToElasticUpdateMdseInfoList(List<Long> mdseIds);


    List<MallMdse> findAll();

    List<MallMdse> findAllByShopIds(List<Long> lids);

    Page<MdsePurchaseRecord> findBuyerPage(BuyerPageQuery buyerPageQuery);

    List<MdsePurchaseRecordInfo> purchaseRecordList2vo(List<MdsePurchaseRecord> content);

    void addMdsePurchaseRecord(MdsePurchaseRecordDTO recordDTO);

    List<MdsePurchaseRecord> getAllPurchaseRecordByMdseId(Long mdseId);

    List<MdsePurchaseRecord> getAllPurchaseRecordByType(Integer type);

    Optional<MdseCardRules> findCardRulesByMdseId(Long mdseId);

    EffectiveRulesInfo rule2vo(MdseCardRules cardRules);
}
