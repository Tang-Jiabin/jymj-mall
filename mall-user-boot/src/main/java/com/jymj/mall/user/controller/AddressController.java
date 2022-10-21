package com.jymj.mall.user.controller;

import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.user.dto.AddressDTO;
import com.jymj.mall.user.entity.UserAddress;
import com.jymj.mall.user.service.AddressService;
import com.jymj.mall.user.vo.AddressInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 地址
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-18
 */
@Api(tags = "地址")
@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
public class AddressController {
    
    private final AddressService addressService;
    
    @ApiOperation(value = "添加地址")
    @PostMapping
    public Result<AddressInfo> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        UserAddress address = addressService.add(addressDTO);
        AddressInfo addressInfo = addressService.entity2vo(address);
        return Result.success(addressInfo);
    }

    @ApiOperation(value = "删除地址")
    @DeleteMapping("/{ids}")
    public Result<Objects> deleteAddress(@ApiParam("删除，多个id用英文逗号分割") @PathVariable String ids) {
        addressService.delete(ids);
        return Result.success();
    }

    @ApiOperation(value = "修改地址")
    @PutMapping
    public Result<AddressInfo> updateAddress(@RequestBody AddressDTO addressDTO) {
        Optional<UserAddress> addressOptional = addressService.update(addressDTO);
        return addressOptional.map(address -> Result.success(addressService.entity2vo(address))).orElse(Result.failed());
    }

    @ApiOperation(value = "地址信息")
    @GetMapping("/{addressId}/info")
    public Result<AddressInfo> getAddressById(@PathVariable Long addressId) {
        Optional<UserAddress> addressOptional = addressService.findById(addressId);
        return addressOptional.map(address -> Result.success(addressService.entity2vo(address))).orElse(Result.failed());
    }

    @ApiOperation(value = "默认地址")
    @GetMapping("/default")
    public Result<AddressInfo> getDefaultAddress() {
        List<UserAddress> addressList = addressService.findAllByUserId(UserUtils.getUserId());
        Optional<UserAddress> addressOptional = addressList.stream().filter(address -> address.getStatus().equals(SystemConstants.STATUS_OPEN)).findFirst();
        return addressOptional.map(address -> Result.success(addressService.entity2vo(address))).orElse(Result.failed());
    }

    @ApiOperation(value = "地址列表")
    @GetMapping("/list")
    public Result<List<AddressInfo>> getAddressList() {
        List<UserAddress> addressList = addressService.findAllByUserId(UserUtils.getUserId());
        return Result.success(addressService.list2vo(addressList));
    }
}
