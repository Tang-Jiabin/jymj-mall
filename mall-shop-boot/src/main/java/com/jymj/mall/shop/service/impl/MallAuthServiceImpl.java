package com.jymj.mall.shop.service.impl;

import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.shop.dto.AddMallAuth;
import com.jymj.mall.shop.dto.UpdateMallAuth;
import com.jymj.mall.shop.entity.MallAuth;
import com.jymj.mall.shop.entity.MallDetails;
import com.jymj.mall.shop.repository.MallAuthRepository;
import com.jymj.mall.shop.service.MallAuthService;
import com.jymj.mall.shop.service.MallService;
import com.jymj.mall.shop.vo.MallAuthInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 商场授权
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-29
 */
@Service
@RequiredArgsConstructor
public class MallAuthServiceImpl implements MallAuthService {

    private final MallAuthRepository authRepository;
    private final MallService mallService;

    @Override
    public MallAuth addAuth(AddMallAuth addMallAuth) {

        Optional<MallDetails> mallDetailsOptional = mallService.findById(addMallAuth.getMallId());

        if (!mallDetailsOptional.isPresent()) {
            throw new BusinessException("商场不存在");
        }

        Optional<MallAuth> mallAuthOptional = authRepository.findByMallId(addMallAuth.getMallId());

        if (mallAuthOptional.isPresent()) {
            throw new BusinessException("该商场已认证");
        }

        MallAuth mallAuth = add2auth(addMallAuth);
        mallAuth.setDeleted(SystemConstants.DELETED_NO);
        return authRepository.save(mallAuth);
    }

    private MallAuth add2auth(AddMallAuth addMallAuth) {
        return MallAuth.builder()
                .mallId(addMallAuth.getMallId())
                .companyName(addMallAuth.getCompanyName())
                .companyAddress(addMallAuth.getCompanyAddress())
                .legalPerson(addMallAuth.getLegalPerson())
                .identity(addMallAuth.getIdentity())
                .mobile(addMallAuth.getMobile())
                .unifiedSocialCreditCode(addMallAuth.getUnifiedSocialCreditCode())
                .license(addMallAuth.getLicense())
                .idFront(addMallAuth.getIdFront())
                .idBack(addMallAuth.getIdBack())
                .build();
    }

    @Override
    public void deleteAuth(Long id) {
        authRepository.deleteById(id);
    }

    @Override
    public MallAuth updateAuth(UpdateMallAuth updateMallAuth) {
        Optional<MallAuth> mallAuthOptional = authRepository.findById(updateMallAuth.getAuthId());
        MallAuth mallAuth = mallAuthOptional.orElseThrow(() -> new BusinessException("授权不存在"));

        if (StringUtils.hasText(updateMallAuth.getCompanyName())) {
            mallAuth.setCompanyName(updateMallAuth.getCompanyName());
        }

        if (StringUtils.hasText(updateMallAuth.getCompanyAddress())) {
            mallAuth.setCompanyAddress(updateMallAuth.getCompanyAddress());
        }

        if (StringUtils.hasText(updateMallAuth.getLegalPerson())) {
            mallAuth.setLegalPerson(updateMallAuth.getLegalPerson());
        }

        if (StringUtils.hasText(updateMallAuth.getIdentity())) {
            mallAuth.setIdentity(updateMallAuth.getIdentity());
        }

        if (StringUtils.hasText(updateMallAuth.getUnifiedSocialCreditCode())) {
            mallAuth.setUnifiedSocialCreditCode(updateMallAuth.getUnifiedSocialCreditCode());
        }

        if (StringUtils.hasText(updateMallAuth.getMobile())) {
            mallAuth.setMobile(updateMallAuth.getMobile());
        }

        if (StringUtils.hasText(updateMallAuth.getLicense())) {
            mallAuth.setLicense(updateMallAuth.getLicense());
        }

        if (StringUtils.hasText(updateMallAuth.getIdFront())) {
            mallAuth.setIdFront(updateMallAuth.getIdFront());
        }

        if (StringUtils.hasText(updateMallAuth.getIdBack())) {
            mallAuth.setIdBack(updateMallAuth.getIdBack());
        }

        return authRepository.save(mallAuth);
    }

    @Override
    public Optional<MallAuth> getAuthByMallId(Long mallId) {
        return authRepository.findByMallId(mallId);
    }

    @Override
    public MallAuthInfo auth2vo(MallAuth mallAuth) {
        return MallAuthInfo.builder()
                .authId(mallAuth.getAuthId())
                .companyName(mallAuth.getCompanyName())
                .companyAddress(mallAuth.getCompanyAddress())
                .legalPerson(mallAuth.getLegalPerson())
                .identity(mallAuth.getIdentity())
                .mobile(mallAuth.getMobile())
                .license(mallAuth.getLicense())
                .unifiedSocialCreditCode(mallAuth.getUnifiedSocialCreditCode())
                .idFront(mallAuth.getIdFront())
                .idBack(mallAuth.getIdBack())
                .build();
    }
}
