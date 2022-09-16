package com.jymj.mall.common.constants;

import org.springframework.util.StringUtils;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
public interface SystemConstants {

    /**
     * 根部门ID
     */
    Long ROOT_DEPT_ID = 0L;

    /**
     * 根菜单ID
     */
    Long ROOT_MENU_ID = 0L;

    /**
     * 根行政区ID
     */
    Long ROOT_DISTRICT_ID = 0L;

    /**
     * 未删除状态
     */
    Integer DELETED_NO = 0;

    /**
     * 删除状态
     */
    Integer DELETED = 1;

    /**
     * 系统默认密码
     */
    String DEFAULT_USER_PASSWORD = "123456";

    /**
     * 超级管理员角色编码
     */
    String ROOT_ROLE_CODE = "ROOT";

    Integer STATUS_OPEN = 1;

    Integer STATUS_CLOSE = 2;

    String SQL_LIKE = "%";

    /**
     * 生成sqlLike语句
     *
     * @param param 参数
     * @return like
     */
    static String generateSqlLike(String param) {
        if (StringUtils.hasText(param)) {
            return SQL_LIKE + param + SQL_LIKE;
        }
        return "NULL";
    }

}
