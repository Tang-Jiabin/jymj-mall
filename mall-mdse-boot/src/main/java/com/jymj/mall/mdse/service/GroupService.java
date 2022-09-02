package com.jymj.mall.mdse.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.mdse.dto.BrandDTO;
import com.jymj.mall.mdse.dto.GroupDTO;
import com.jymj.mall.mdse.dto.GroupPageQuery;
import com.jymj.mall.mdse.entity.MdseBrand;
import com.jymj.mall.mdse.entity.MdseGroup;
import com.jymj.mall.mdse.vo.BrandInfo;
import com.jymj.mall.mdse.vo.GroupInfo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分组
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
public interface GroupService extends BaseService<MdseGroup, GroupInfo, GroupDTO> {
    List<MdseGroup> findAll();

    Page<MdseGroup> findPage(GroupPageQuery groupPageQuery);

    List<MdseGroup> findAllById(List<Long> groupIdList);
}
