package com.jymj.mall.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * 权限dto
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Data
public class PermissionDTO {

    private String name;

    private Long menuId;

    private String urlPerm;

    private String btnPerm;

    private List<String> roles;

}
