package com.jymj.mall.mdse.vo;

import com.jymj.mall.user.vo.UserInfo;
import lombok.Data;

/**
 * 购买人员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-09
 */
@Data
public class MdsePurchaseRecordInfo {

    private MdseInfo mdseInfo;
    private UserInfo userInfo;
}
