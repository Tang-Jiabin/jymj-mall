package com.jymj.mall.admin.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 修改管理员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAdminDTO {

    @ApiModelProperty("管理员Id")
    private Long adminId;

    @ApiModelProperty("编号")
    private String number;

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("旧密码")
    private String oldPassword;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("昵称")
    private String nickname;

    @Pattern(regexp = "^1(3\\d|4[5-9]|5[0-35-9]|6[2567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$", message = "手机号格式不正确")
    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("性别 1-男 2-女")
    private Integer gender;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("邮箱")
    private String email;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty("状态")
    private Integer status;

    @NotNull(message = "部门ID不能为空")
    @ApiModelProperty("部门ID")
    private Long deptId;

    @NotNull(message = "商城ID不能为空")
    @ApiModelProperty("商城ID")
    private Long mallId;

    @NotEmpty(message = "用户角色不能为空")
    @ApiModelProperty("角色Id集合")
    private List<Long> roleIdList;

    @ApiModelProperty("核销人员")
    private Integer verifyPerson;


}
