package com.jymj.mall.common.web.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SecurityConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * JWT工具类
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Slf4j
public class UserUtils {

    /**
     * 解析JWT获取用户ID
     *
     * @return
     */
    public static Long getUserId() {
        JSONObject jwtPayload = JwtUtils.getJwtPayload();

        if (ObjectUtils.isEmpty(jwtPayload)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID_OR_EXPIRED);
        }
        Long userId = jwtPayload.getLong("userId");

        if (ObjectUtils.isEmpty(userId)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID_OR_EXPIRED);
        }
        return userId;
    }

    public static String getOpenId() {
        JSONObject jwtPayload = JwtUtils.getJwtPayload();

        if (ObjectUtils.isEmpty(jwtPayload)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID_OR_EXPIRED);
        }
        String openId = jwtPayload.getStr("openId");

        if (!StringUtils.hasText(openId)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID_OR_EXPIRED);
        }
        return openId;
    }

    public static Long getAdminId() {
        if (ObjectUtils.isEmpty(JwtUtils.getJwtPayload())) {
            return 0L;
        }
        return JwtUtils.getJwtPayload().getLong("adminId");
    }

    /**
     * 解析JWT获取用户ID
     *
     * @return
     */
    public static Long getDeptId() {
        if (ObjectUtils.isEmpty(JwtUtils.getJwtPayload())) {
            throw new BusinessException(ResultCode.AUTHORIZED_ERROR);
        }
        return JwtUtils.getJwtPayload().getLong("deptId");
    }

    /**
     * 解析JWT获取获取用户名
     *
     * @return
     */
    public static String getUsername() {
        if (ObjectUtils.isEmpty(JwtUtils.getJwtPayload())) {
            throw new BusinessException(ResultCode.AUTHORIZED_ERROR);
        }
        return JwtUtils.getJwtPayload().getStr(SecurityConstants.USER_NAME_KEY);
    }


    /**
     * JWT获取用户角色列表
     *
     * @return 角色列表
     */
    public static List<String> getRoles() {
        if (ObjectUtils.isEmpty(JwtUtils.getJwtPayload())) {
            throw new BusinessException(ResultCode.AUTHORIZED_ERROR);
        }
        List<String> roles = Lists.newArrayList();
        JSONObject payload = JwtUtils.getJwtPayload();
        if (payload.containsKey(SecurityConstants.JWT_AUTHORITIES_KEY)) {
            roles = payload.getJSONArray(SecurityConstants.JWT_AUTHORITIES_KEY).toList(String.class);
        }
        return roles;
    }

    /**
     * 是否「超级管理员」
     *
     * @return
     */
    public static boolean isRoot() {
        List<String> roles = getRoles();
        return CollUtil.isNotEmpty(roles) && roles.contains("ROOT");
    }
}
