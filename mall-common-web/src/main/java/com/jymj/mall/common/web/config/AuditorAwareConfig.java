package com.jymj.mall.common.web.config;

import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.ResultCode;
import com.jymj.mall.common.web.util.UserUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Optional;

/**
 * JPA自动获取id
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-12
 */
@Component
public class AuditorAwareConfig implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        if (UserUtils.getAdminId() == null){
            throw new BusinessException(ResultCode.USER_LOGIN_ERROR);
        }
        return Optional.of(UserUtils.getAdminId());
    }
}
