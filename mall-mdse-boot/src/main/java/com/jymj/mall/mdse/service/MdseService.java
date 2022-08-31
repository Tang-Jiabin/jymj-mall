package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.MdseDTO;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.entity.MallMdse;
import com.jymj.mall.mdse.vo.MdseInfo;
import org.springframework.data.domain.Page;

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
}
