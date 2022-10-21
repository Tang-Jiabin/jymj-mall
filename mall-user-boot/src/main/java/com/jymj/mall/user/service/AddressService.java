package com.jymj.mall.user.service;

import com.jymj.mall.common.web.service.BaseService;
import com.jymj.mall.user.dto.AddressDTO;
import com.jymj.mall.user.entity.UserAddress;
import com.jymj.mall.user.vo.AddressInfo;

import java.util.List;

/**
 * 地址
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-18
 */
public interface AddressService extends BaseService<UserAddress, AddressInfo, AddressDTO> {

    List<UserAddress> findAllByUserId(Long userId);
}
