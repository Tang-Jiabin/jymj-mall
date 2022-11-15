package com.jymj.mall.shop.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.shop.dto.VerifyOrderMdse;
import com.jymj.mall.shop.dto.VerifyPersonDTO;
import com.jymj.mall.shop.dto.VerifyPersonPageQuery;
import com.jymj.mall.shop.entity.VerifyPerson;
import com.jymj.mall.shop.vo.VerifyPersonInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * 核销
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-26
 */
public interface VerifyService extends BaseService<VerifyPerson, VerifyPersonInfo, VerifyPersonDTO> {
    Page<VerifyPerson> findPage(VerifyPersonPageQuery verifyPersonPageQuery);

    Optional<VerifyPerson> findByAdminId(Long adminId);

    Optional<VerifyPerson> findByUserId(Long userId);

    List<VerifyPerson> findAllByAdminIdIn(List<Long> adminIds);

    void deleteAll(List<VerifyPerson> verifyPeopleList);

    void verify(VerifyOrderMdse verifyOrderMdse);

}
