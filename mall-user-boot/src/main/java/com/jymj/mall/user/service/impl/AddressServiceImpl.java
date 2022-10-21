package com.jymj.mall.user.service.impl;

import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.user.dto.AddressDTO;
import com.jymj.mall.user.entity.UserAddress;
import com.jymj.mall.user.repository.UserAddressRepository;
import com.jymj.mall.user.service.AddressService;
import com.jymj.mall.user.vo.AddressInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 地址信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserAddressRepository addressRepository;

    @Override
    public UserAddress add(AddressDTO dto) {
        UserAddress userAddress = UserAddress.builder()
                .name(dto.getName())
                .mobile(dto.getMobile())
                .region(dto.getRegion())
                .detailedAddress(dto.getDetailedAddress())
                .status(dto.getStatus())
                .userId(UserUtils.getUserId())
                .build();
        userAddress.setDeleted(SystemConstants.DELETED_NO);
        if (StringUtils.hasText(dto.getLabel())) {
            userAddress.setLabel(dto.getLabel());
        }

        updateAddressStatusClose(dto);

        return addressRepository.save(userAddress);
    }

    private void updateAddressStatusClose(AddressDTO dto) {
        if (dto.getStatus().equals(SystemConstants.STATUS_OPEN)) {
            List<UserAddress> addressList = addressRepository.findAllByUserId(UserUtils.getUserId());
            addressList.stream().filter(address -> address.getStatus().equals(SystemConstants.STATUS_OPEN)).map(address -> {
                address.setStatus(SystemConstants.STATUS_CLOSE);
                return addressRepository.save(address);
            }).collect(Collectors.toList());
        }
    }

    @Override
    public Optional<UserAddress> update(AddressDTO dto) {
        if (!ObjectUtils.isEmpty(dto.getAddressId())) {
            Optional<UserAddress> addressOptional = findById(dto.getAddressId());
            UserAddress userAddress = addressOptional.orElseThrow(() -> new BusinessException("地址不存在"));
            if (StringUtils.hasText(dto.getName())) {
                userAddress.setName(dto.getName());
            }
            if (StringUtils.hasText(dto.getMobile())) {
                userAddress.setMobile(dto.getMobile());
            }
            if (StringUtils.hasText(dto.getRegion())) {
                userAddress.setRegion(dto.getRegion());
            }
            if (StringUtils.hasText(dto.getDetailedAddress())) {
                userAddress.setDetailedAddress(dto.getDetailedAddress());
            }
            if (StringUtils.hasText(dto.getLabel())) {
                userAddress.setLabel(dto.getLabel());
            }
            if (!ObjectUtils.isEmpty(dto.getStatus())) {
                updateAddressStatusClose(dto);
                userAddress.setStatus(dto.getStatus());
            }
            return Optional.of(addressRepository.save(userAddress));
        }
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> addressIds = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(addressIds)) {
            List<UserAddress> addressList = addressRepository.findAllById(addressIds);
            addressList = addressList.stream().filter(userAddress -> userAddress.getUserId().equals(UserUtils.getUserId())).collect(Collectors.toList());
            addressRepository.deleteAll(addressList);
        }
    }

    @Override
    public Optional<UserAddress> findById(Long id) {
        return addressRepository.findById(id);
    }

    @Override
    public AddressInfo entity2vo(UserAddress entity) {
        if (!ObjectUtils.isEmpty(entity)) {
            return AddressInfo.builder()
                    .addressId(entity.getAddressId())
                    .name(entity.getName())
                    .mobile(entity.getMobile())
                    .region(entity.getRegion())
                    .detailedAddress(entity.getDetailedAddress())
                    .label(entity.getLabel())
                    .status(entity.getStatus())
                    .build();
        }
        return null;
    }

    @Override
    public List<AddressInfo> list2vo(List<UserAddress> entityList) {

        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream().filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo).collect(Collectors.toList());
    }

    @Override
    public List<UserAddress> findAllByUserId(Long userId) {
        return addressRepository.findAllByUserId(userId);
    }
}
