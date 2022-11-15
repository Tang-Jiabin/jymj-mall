package com.jymj.mall.user.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.user.dto.MemberDTO;
import com.jymj.mall.user.dto.MemberPageQuery;
import com.jymj.mall.user.entity.MallMember;
import com.jymj.mall.user.vo.MemberInfo;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * 会员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-07
 */
public interface MemberService extends BaseService<MallMember, MemberInfo, MemberDTO> {
    Page<MallMember> findPage(MemberPageQuery memberPageQuery);

    Optional<MallMember> findByUserId(Long userId);
}
