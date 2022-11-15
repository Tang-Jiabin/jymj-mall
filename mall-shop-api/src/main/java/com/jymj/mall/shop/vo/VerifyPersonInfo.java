package com.jymj.mall.shop.vo;


import com.jymj.mall.admin.vo.AdminInfo;
import com.jymj.mall.user.vo.UserInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 核销人员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-10-27
 */
@Data
@ApiModel("核销人员")
public class VerifyPersonInfo {

    @ApiModelProperty("用户id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户信息")
    private UserInfo userInfo;

    @ApiModelProperty("管理员信息")
    private AdminInfo adminInfo;

    @ApiModelProperty("管理员id")
    private Long adminId;

    @ApiModelProperty("店铺id")
    private List<Long> shopIdList;

    @ApiModelProperty("商品id")
    private List<Long> mdseIds;

}
