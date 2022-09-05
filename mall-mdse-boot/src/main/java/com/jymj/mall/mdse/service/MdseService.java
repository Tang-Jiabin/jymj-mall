package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.entity.MallMdse;
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

    MdseInfo voAddPictureList(MdseInfo mdseInfo);

    MdseInfo voAddGroupList(MdseInfo mdseInfo);

    MdseInfo voAddBrand(MdseInfo mdseInfo, Long brandId);

    MdseInfo voAddMfg(MdseInfo mdseInfo, Long mfgId);

    MdseInfo voAddStockList(MdseInfo mdseInfo);

    MdseInfo voAddType(MdseInfo mdseInfo, Long typeId);

    MdseInfo voAddLabelList(MdseInfo mdseInfo);

    MdseInfo entity2vo(MallMdse mallMdse,boolean group,boolean  stock,boolean  label,boolean  picture,boolean  mfg,boolean  type,boolean  brand);

    List<MdseInfo> list2vo(List<MallMdse> entityList, boolean group, boolean stock, boolean label, boolean picture, boolean mfg, boolean type, boolean brand);
}
