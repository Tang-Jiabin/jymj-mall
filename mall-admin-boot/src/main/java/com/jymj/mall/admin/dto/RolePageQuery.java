package com.jymj.mall.admin.dto;

import com.jymj.mall.common.web.dto.BasePageQueryDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("角色分页")
public class RolePageQuery  extends BasePageQueryDTO {
}
