package com.jymj.mall.admin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 管理员信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-19
 */
@Data
public class AdminInfo {

    @ApiModelProperty("管理员id")
    private Long adminId;

    @ApiModelProperty("编号")
    private String number;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("性别 1-男 2-女")
    private Integer gender;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("角色集合")
    private List<RoleInfo> roleInfoList;

    @ApiModelProperty("菜单集合")
    private List<MenuInfo> menuInfoList;

    @ApiModelProperty("部门信息")
    private DeptInfo deptInfo;

    @ApiModelProperty("商城ID")
    private Long mallId;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("操作时间")
    private Date operationTime;

    @ApiModelProperty("核销人员")
    private Integer verifyPerson;

    @ApiModelProperty("初始化密码")
    private Integer initPassword;
}
