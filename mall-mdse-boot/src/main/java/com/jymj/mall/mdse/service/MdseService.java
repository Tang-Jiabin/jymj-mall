package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdseInfoShow;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.dto.MdseStatusDTO;
import com.jymj.mall.mdse.entity.MallMdse;
import com.jymj.mall.mdse.vo.GroupInfo;
import com.jymj.mall.mdse.vo.MdseInfo;
import org.springframework.data.domain.Page;

import java.util.List;

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

    void deleteCache(Long... array);

    List<MallMdse> findAll();

}
