package com.jymj.mall.user.service.impl;

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
import com.jymj.mall.user.entity.MallMember;
import com.jymj.mall.user.repository.MallMemberRepository;
import com.jymj.mall.user.service.MemberService;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MallMember add(MemberDTO dto) {
        validMobileAndIDCard(dto);
        Long userId = UserUtils.getUserId();
        Optional<MallMember> memberOptional = memberRepository.findByUserId(userId);
        memberOptional.ifPresent(mallMember -> {
            throw new BusinessException("已存在会员信息");
        });
        MallMember mallMember = MallMember.builder()
                .userId(userId)
                .name(dto.getName())
                .mobile(dto.getMobile())
                .address(dto.getAddress())
                .idNumber(dto.getIdNumber())
                .build();
        mallMember.setDeleted(SystemConstants.DELETED_NO);

        return memberRepository.save(mallMember);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = {"mall-user:member-info:", "mall-user:member-entity:"}, key = "'brand-id:'+#dto.memberId")
    public Optional<MallMember> update(MemberDTO dto) {
        validMobileAndIDCard(dto);
        Long userId = UserUtils.getUserId();
        Optional<MallMember> memberOptional = memberRepository.findByUserId(userId);
        MallMember mallMember = memberOptional.orElseThrow(() -> new BusinessException("会员信息不存在"));

        mallMember.setName(dto.getName());
        mallMember.setMobile(dto.getMobile());
        mallMember.setAddress(dto.getAddress());
        mallMember.setIdNumber(dto.getIdNumber());
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
            return MemberInfo.builder()
                    .memberId(entity.getMemberId())
                    .name(entity.getName())
                    .mobile(entity.getMobile())
                    .address(entity.getAddress())
                    .idNumber(entity.getIdNumber())
                    .userId(entity.getUserId())
                    .createTime(entity.getCreateTime())
                    .build();
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
