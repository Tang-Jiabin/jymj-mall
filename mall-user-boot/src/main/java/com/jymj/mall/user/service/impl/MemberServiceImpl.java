package com.jymj.mall.user.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.PhoneUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.mdse.api.MdseFeignClient;
import com.jymj.mall.mdse.dto.MdsePurchaseRecordDTO;
import com.jymj.mall.user.dto.MemberDTO;
import com.jymj.mall.user.dto.MemberPageQuery;
import com.jymj.mall.user.dto.UserDTO;
import com.jymj.mall.user.entity.MallMember;
import com.jymj.mall.user.repository.MallMemberRepository;
import com.jymj.mall.user.service.MemberService;
import com.jymj.mall.user.service.UserService;
import com.jymj.mall.user.vo.MemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 会员
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-11-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MallMemberRepository memberRepository;
    private final ThreadPoolTaskExecutor executor;
    private final MdseFeignClient mdseFeignClient;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallMember add(MemberDTO dto) {
        validMobileAndIDCard(dto);

        if (StringUtils.hasText(dto.getEmail())){
            if (!Validator.isEmail(dto.getEmail())) {
                throw new BusinessException("邮箱格式错误");
            }
        }
        Long userId = UserUtils.getUserId();
        Optional<MallMember> memberOptional = memberRepository.findByUserId(userId);
        memberOptional.ifPresent(mallMember -> {
            throw new BusinessException("已存在会员信息");
        });

        userService.update(UserDTO.builder().userId(userId).memberName(dto.getName()).memberMobile(dto.getMobile()).build());

        MallMember mallMember = MallMember.builder()
                .userId(userId)
                .name(dto.getName())
                .mobile(dto.getMobile())
                .address(dto.getAddress())
                .idNumber(dto.getIdNumber())
                .email(dto.getEmail())
                .state(SystemConstants.STATUS_CLOSE)
                .build();

        mallMember.setDeleted(SystemConstants.DELETED_NO);

        return memberRepository.save(mallMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"mall-user:member-info:", "mall-user:member-entity:"}, key = "'member-id:'+#dto.memberId")
    public Optional<MallMember> update(MemberDTO dto) {
        validMobileAndIDCard(dto);
        Long userId = UserUtils.getUserId();
        Optional<MallMember> memberOptional = memberRepository.findByUserId(userId);
        MallMember mallMember = memberOptional.orElseThrow(() -> new BusinessException("会员信息不存在"));
        if (StringUtils.hasText(dto.getName())) {
            mallMember.setName(dto.getName());
        }
        if (StringUtils.hasText(dto.getMobile())) {
            mallMember.setMobile(dto.getMobile());
        }
        if (StringUtils.hasText(dto.getAddress())) {
            mallMember.setAddress(dto.getAddress());
        }
        if (StringUtils.hasText(dto.getIdNumber())) {
            mallMember.setIdNumber(dto.getIdNumber());
        }
        if (!ObjectUtils.isEmpty(dto.getLevel())) {
            mallMember.setLevel(dto.getLevel());
        }
        if (!ObjectUtils.isEmpty(dto.getState())) {
            mallMember.setState(dto.getState());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            mallMember.setEmail(dto.getEmail());
        }
        mallMember.setDeleted(SystemConstants.DELETED_NO);

        return Optional.of(memberRepository.save(mallMember));
    }

    private static void validMobileAndIDCard(MemberDTO dto) {
        if (!PhoneUtil.isMobile(dto.getMobile())) {
            throw new BusinessException("手机号码错误");
        }
        if (!IdcardUtil.isValidCard(dto.getIdNumber())) {
            throw new BusinessException("身份证号错误");
        }
    }


    @Override
    @CacheEvict(value = {"mall-user:member-info:", "mall-user:member-entity:"}, allEntries = true)
    public void delete(String ids) {
        List<Long> memberIdList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(memberIdList)) {
            List<MallMember> memberList = memberRepository.findAllById(memberIdList);
            memberRepository.deleteAll(memberList);
        }
    }

    @Override
    @Cacheable(cacheNames = "mall-user:member-entity:", key = "'member-id:'+#id")
    public Optional<MallMember> findById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    @Cacheable(cacheNames = "mall-user:member-info:", key = "'member-id:'+#entity.memberId")
    public MemberInfo entity2vo(MallMember entity) {
        if (!ObjectUtils.isEmpty(entity)) {
            MemberInfo memberInfo = MemberInfo.builder()
                    .memberId(entity.getMemberId())
                    .name(entity.getName())
                    .mobile(entity.getMobile())
                    .address(entity.getAddress())
                    .idNumber(entity.getIdNumber())
                    .userId(entity.getUserId())
                    .createTime(entity.getCreateTime())
                    .email(entity.getEmail())
                    .build();
            if (StringUtils.hasText(entity.getIdNumber())) {
                memberInfo.setBirth(DateUtil.format(IdcardUtil.getBirthDate(entity.getIdNumber()), "yyyy-MM-dd"));
                memberInfo.setAge(IdcardUtil.getAgeByIdCard(entity.getIdNumber()));
                memberInfo.setGender(IdcardUtil.getGenderByIdCard(entity.getIdNumber()));
                memberInfo.setCityCode(IdcardUtil.getCityCodeByIdCard(entity.getIdNumber()));
            }
            return memberInfo;
        }
        return null;
    }

    @Override
    public List<MemberInfo> list2vo(List<MallMember> entityList) {
        List<CompletableFuture<MemberInfo>> futureList = Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream().filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(entity -> CompletableFuture.supplyAsync(() -> entity2vo(entity), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MallMember> findPage(MemberPageQuery memberPageQuery) {
        Pageable pageable = PageUtils.getPageable(memberPageQuery);

        Specification<MallMember> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();
            CriteriaBuilder.In<Long> userIdIn = criteriaBuilder.in(root.get("userId").as(Long.class));
            Set<Long> userIdSet = Sets.newHashSet();
            if (Objects.nonNull(memberPageQuery.getMdseId())) {
                Result<List<MdsePurchaseRecordDTO>> purchaseRecordListResult = mdseFeignClient.getPurchaseRecordByMdseId(memberPageQuery.getMdseId());
                if (Result.isSuccess(purchaseRecordListResult)) {
                    purchaseRecordListResult.getData().forEach(purchaseRecord -> userIdSet.add(purchaseRecord.getUserId()));
                }
                userIdSet.add(0L);
            }
            String mobile = memberPageQuery.getMobile();
            if (StringUtils.hasText(mobile)) {
                list.add(criteriaBuilder.like(root.get("mobile"), SystemConstants.generateSqlLike(mobile)));
            }
            String nickname = memberPageQuery.getName();
            if (StringUtils.hasText(nickname)) {
                list.add(criteriaBuilder.like(root.get("name"), SystemConstants.generateSqlLike(nickname)));
            }

            if (!CollectionUtils.isEmpty(userIdSet)) {
                userIdSet.forEach(userIdIn::value);
                list.add(userIdIn);
            }

            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };

        return memberRepository.findAll(spec, pageable);
    }

    @Override
    public Optional<MallMember> findByUserId(Long userId) {
        return memberRepository.findByUserId(userId);
    }
}
