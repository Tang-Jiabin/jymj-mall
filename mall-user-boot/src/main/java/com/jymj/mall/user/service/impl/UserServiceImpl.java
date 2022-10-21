package com.jymj.mall.user.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.user.dto.UserAuthDTO;
import com.jymj.mall.user.dto.UserDTO;
import com.jymj.mall.user.dto.UserPageQuery;
import com.jymj.mall.user.entity.MallUser;
import com.jymj.mall.user.enums.MemberEnum;
import com.jymj.mall.user.enums.SourceEnum;
import com.jymj.mall.user.repository.UserRepository;
import com.jymj.mall.user.service.UserService;
import com.jymj.mall.user.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-04
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public Optional<UserAuthDTO> loadUserByOpenid(String openid) {

        Optional<MallUser> userOptional = userRepository.findByOpenid(openid);
        if (userOptional.isPresent()) {
            MallUser user = userOptional.get();
            UserAuthDTO userAuthDTO = new UserAuthDTO();
            userAuthDTO.setUserId(user.getUserId());
            userAuthDTO.setUsername(user.getOpenid());
            userAuthDTO.setNickname(user.getNickName());
            userAuthDTO.setPassword(user.getPassword());
            userAuthDTO.setStatus(user.getStatus());
            userAuthDTO.setRoles(Lists.newArrayList("USER"));
            user.setLoginTime(new Date());
            userRepository.save(user);
            return Optional.of(userAuthDTO);
        }
        return Optional.empty();
    }

    @Override
    public MallUser add(UserDTO dto) {

        if (StringUtils.hasText(dto.getOpenid())) {
            Optional<MallUser> mallUserOptional = userRepository.findByOpenid(dto.getOpenid());
            if (mallUserOptional.isPresent()) {
                return mallUserOptional.get();
            }
        }
        MallUser user = new MallUser();
        user.setUsername(dto.getUsername());
        user.setNickName(dto.getNickName());
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setGender(dto.getGender());
        user.setMobile(dto.getMobile());
        user.setBirthday(dto.getBirthday());
        user.setAvatarUrl(dto.getAvatarUrl());
        user.setOpenid(dto.getOpenid());
        user.setCity(dto.getCity());
        user.setCountry(dto.getCountry());
        user.setLanguage(dto.getLanguage());
        user.setProvince(dto.getProvince());
        user.setStatus(SystemConstants.STATUS_OPEN);
        user.setDeleted(SystemConstants.DELETED_NO);
        user.setMemberType(MemberEnum.ORDINARY_USER);
        user.setLoginTime(new Date());
        user.setSourceType(dto.getSourceType());
        user.setPurchaseCount(0);

        return userRepository.save(user);
    }


    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MallUser> brandList = userRepository.findAllById(idList);
            userRepository.deleteAll(brandList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Optional<MallUser> update(UserDTO dto) {
        if (!ObjectUtils.isEmpty(dto) && !ObjectUtils.isEmpty(dto.getUserId())) {
            Optional<MallUser> userOptional = userRepository.findById(dto.getUserId());
            if (userOptional.isPresent()) {
                MallUser user = userOptional.get();
                boolean update = baseUpdate(dto, user);
                if (update) {
                    return Optional.of(userRepository.save(user));
                }
            }
        }

        return Optional.empty();
    }

    private static boolean baseUpdate(UserDTO dto, MallUser user) {
        boolean update = false;
        if (StringUtils.hasText(dto.getNickName())) {
            user.setNickName(dto.getNickName());
            update = true;
        }
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(dto.getPassword());
            update = true;
        }
        if (StringUtils.hasText(dto.getMobile())) {
            user.setMobile(dto.getMobile());
            update = true;
        }
        if (StringUtils.hasText(dto.getAvatarUrl())) {
            user.setAvatarUrl(dto.getAvatarUrl());
            update = true;
        }
        if (StringUtils.hasText(dto.getCity())) {
            user.setCity(dto.getCity());
            update = true;
        }
        if (StringUtils.hasText(dto.getCountry())) {
            user.setCountry(dto.getCountry());
            update = true;
        }
        if (StringUtils.hasText(dto.getLanguage())) {
            user.setLanguage(dto.getLanguage());
            update = true;
        }
        if (StringUtils.hasText(dto.getProvince())) {
            user.setProvince(dto.getProvince());
            update = true;
        }
        if (!ObjectUtils.isEmpty(dto.getGender())) {
            user.setGender(dto.getGender());
            update = true;
        }
        if (!ObjectUtils.isEmpty(dto.getBirthday())) {
            user.setBirthday(dto.getBirthday());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getMemberType())) {
            user.setMemberType(dto.getMemberType());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getSourceType())) {
            user.setSourceType(dto.getSourceType());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getLoginTime())) {
            user.setLoginTime(dto.getLoginTime());
            update = true;
        }

        if (!ObjectUtils.isEmpty(dto.getPurchaseCount())) {
            user.setPurchaseCount(dto.getPurchaseCount());
            update = true;
        }

        return update;
    }

    @Override
    public Optional<MallUser> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserInfo entity2vo(MallUser entity) {
        if (!ObjectUtils.isEmpty(entity)) {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(entity.getUserId());
            userInfo.setUsername(entity.getUsername());
            userInfo.setNickName(entity.getNickName());
            userInfo.setMobile(entity.getMobile());
            userInfo.setGender(entity.getGender());
            userInfo.setBirthday(entity.getBirthday());
            userInfo.setAvatarUrl(entity.getAvatarUrl());
            userInfo.setCity(entity.getCity());
            userInfo.setCountry(entity.getCountry());
            userInfo.setLanguage(entity.getLanguage());
            userInfo.setProvince(entity.getProvince());
            userInfo.setStatus(entity.getStatus());
            userInfo.setCreateTime(entity.getCreateTime());
            userInfo.setLoginTime(entity.getLoginTime());
            userInfo.setSourceType(entity.getSourceType());
            userInfo.setMemberType(entity.getMemberType());
            userInfo.setPurchaseCount(entity.getPurchaseCount());
            return userInfo;
        }
        return null;
    }

    @Override
    public List<UserInfo> list2vo(List<MallUser> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MallUser> findPage(UserPageQuery userPageQuery) {
        Pageable pageable = PageUtils.getPageable(userPageQuery);


        Specification<MallUser> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            String mobile = userPageQuery.getMobile();
            if (StringUtils.hasText(mobile)) {
                list.add(criteriaBuilder.like(root.get("mobile"), SystemConstants.generateSqlLike(mobile)));
            }
            String nickname = userPageQuery.getNickname();
            if (StringUtils.hasText(nickname)) {
                list.add(criteriaBuilder.like(root.get("nickname"), SystemConstants.generateSqlLike(nickname)));
            }
            MemberEnum memberType = userPageQuery.getMemberType();
            if (!ObjectUtils.isEmpty(memberType)) {
                list.add(criteriaBuilder.equal(root.get("memberType"), memberType));
            }
            SourceEnum sourceType = userPageQuery.getSourceType();
            if (!ObjectUtils.isEmpty(sourceType)) {
                list.add(criteriaBuilder.equal(root.get("sourceType"), sourceType));
            }

            Integer startPurchaseCount = userPageQuery.getStartPurchaseCount();
            Integer endPurchaseCount = userPageQuery.getEndPurchaseCount();
            if (!ObjectUtils.isEmpty(startPurchaseCount) && !ObjectUtils.isEmpty(endPurchaseCount)) {
                list.add(criteriaBuilder.between(root.get("purchaseCount").as(Integer.class), startPurchaseCount, endPurchaseCount));
            }

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };

        return userRepository.findAll(spec, pageable);
    }


}
