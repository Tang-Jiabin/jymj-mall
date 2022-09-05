package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.mdse.dto.GroupDTO;
import com.jymj.mall.mdse.dto.GroupPageQuery;
import com.jymj.mall.mdse.entity.MallMdseGroupMap;
import com.jymj.mall.mdse.entity.MdseGroup;
import com.jymj.mall.mdse.repository.MdseGroupRepository;
import com.jymj.mall.mdse.repository.MdseGroupMapRepository;
import com.jymj.mall.mdse.service.GroupService;
import com.jymj.mall.mdse.vo.GroupInfo;
import com.jymj.mall.shop.api.ShopFeignClient;
import com.jymj.mall.shop.vo.ShopInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 分组
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final MdseGroupRepository mdseGroupRepository;
    private final MdseGroupMapRepository mdseGroupMapRepository;
    private final ShopFeignClient shopFeignClient;

    @Override
    public MdseGroup add(GroupDTO dto) {


        verifyShopId(dto.getShopId());

        MdseGroup group = new MdseGroup();
        group.setNumber(dto.getNumber());
        group.setName(dto.getName());
        group.setShow(dto.getShow());
        group.setShopId(dto.getShopId());
        group.setRemarks(dto.getRemarks());
        group.setDeleted(SystemConstants.DELETED_NO);

        return mdseGroupRepository.save(group);
    }

    private void verifyShopId(Long shopId) {
        Result<List<ShopInfo>> shopListResult = shopFeignClient.lists();
        if (!Result.isSuccess(shopListResult)) {
            throw new BusinessException("店铺信息获取失败");
        }
        List<Long> shopIdList = shopListResult.getData().stream().map(ShopInfo::getShopId).collect(Collectors.toList());
        if (!shopIdList.contains(shopId)) {
            throw new BusinessException("没有店铺【 " + shopId + " 】的操作权限");
        }
    }

    private void verifyGroupName(String name) {
        Optional<MdseGroup> groupOptional = mdseGroupRepository.findByName(name);

        if (groupOptional.isPresent()) {
            throw new BusinessException("分组 【" + name + "】 已存在");
        }
    }

    @Override
    public Optional<MdseGroup> update(GroupDTO dto) {

        if (!ObjectUtils.isEmpty(dto.getGroupId())) {
            Optional<MdseGroup> groupOptional = mdseGroupRepository.findById(dto.getGroupId());
            if (groupOptional.isPresent()) {
                MdseGroup group = groupOptional.get();
                boolean update = false;

                if (StringUtils.hasText(dto.getNumber()) && !group.getNumber().equals(dto.getNumber())) {
                    group.setNumber(dto.getNumber());
                    update = true;
                }
                if (StringUtils.hasText(dto.getName()) && !group.getName().equals(dto.getName())) {
                    group.setName(dto.getName());
                    update = true;
                }
                if (!ObjectUtils.isEmpty(dto.getShow()) && group.getShow() != dto.getShow()) {
                    group.setShow(dto.getShow());
                    update = true;
                }
                if (StringUtils.hasText(dto.getRemarks()) && !group.getRemarks().equals(dto.getRemarks())) {
                    group.setRemarks(dto.getRemarks());
                    update = true;
                }
                if (!ObjectUtils.isEmpty(dto.getShopId()) && !group.getShopId().equals(dto.getShopId())) {
                    verifyShopId(dto.getShopId());
                    group.setShopId(dto.getShopId());
                    update = true;
                }
                if (update) {
                    return Optional.of(mdseGroupRepository.save(group));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseGroup> groupList = mdseGroupRepository.findAllById(idList);
            mdseGroupRepository.deleteAll(groupList);
        }
    }

    @Override
    public Optional<MdseGroup> findById(Long id) {
        return mdseGroupRepository.findById(id);
    }

    @Override
    public GroupInfo entity2vo(MdseGroup entity) {
        if (entity != null) {
            GroupInfo info = new GroupInfo();
            info.setGroupId(entity.getGroupId());
            info.setNumber(entity.getNumber());
            info.setName(entity.getName());
            info.setShow(entity.getShow());
            info.setRemarks(entity.getRemarks());
            return info;
        }

        return null;
    }

    @Override
    public List<GroupInfo> list2vo(List<MdseGroup> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }

    @Override
    public List<MdseGroup> findAllByAuth() {
        Result<List<ShopInfo>> shopListResult = shopFeignClient.lists();
        if (Result.isSuccess(shopListResult)) {
            List<ShopInfo> shopInfoList = shopListResult.getData();
            List<Long> shopIdList = shopInfoList.stream().map(ShopInfo::getShopId).collect(Collectors.toList());
            return mdseGroupRepository.findAllByShopIdIn(shopIdList);
        }
        return Lists.newArrayList();
    }

    @Override
    public Page<MdseGroup> findPage(GroupPageQuery groupPageQuery) {

        Pageable pageable = PageUtils.getPageable(groupPageQuery);

        List<ShopInfo> shopInfoList = Lists.newArrayList();
        Result<List<ShopInfo>> shopListResult = shopFeignClient.lists();
        if (Result.isSuccess(shopListResult)) {
            shopInfoList = shopListResult.getData();
        }
        List<Long> shopIdList = shopInfoList.stream().map(ShopInfo::getShopId).collect(Collectors.toList());

        Specification<MdseGroup> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            if (!CollectionUtils.isEmpty(shopIdList)) {
                CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("shopId").as(Long.class));
                shopIdList.forEach(in::value);
                list.add(in);
            }

            if (StringUtils.hasText(groupPageQuery.getNumber())) {
                list.add(criteriaBuilder.like(root.get("number").as(String.class), groupPageQuery.getNumber() + SystemConstants.SQL_LIKE));
            }

            if (StringUtils.hasText(groupPageQuery.getName())) {
                list.add(criteriaBuilder.like(root.get("name").as(String.class), groupPageQuery.getName() + SystemConstants.SQL_LIKE));
            }

            if (!ObjectUtils.isEmpty(groupPageQuery.getShow())) {
                list.add(criteriaBuilder.equal(root.get("show").as(Boolean.class), groupPageQuery.getShow()));
            }

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and((Predicate[]) list.toArray(p));
        };

        return mdseGroupRepository.findAll(spec, pageable);

    }

    @Override
    public List<MdseGroup> findAllById(List<Long> groupIdList) {
        return mdseGroupRepository.findAllById(groupIdList);
    }

    @Override
    public void addMdseGroupMap(Long mdseId, List<MdseGroup> groupList) {
        List<MallMdseGroupMap> mdseGroupMaps = Optional.of(groupList)
                .orElse(Lists.newArrayList())
                .stream().filter(group -> !ObjectUtils.isEmpty(group))
                .map(group -> {
                    MallMdseGroupMap mdseGroupMap = new MallMdseGroupMap();
                    mdseGroupMap.setMdseId(mdseId);
                    mdseGroupMap.setGroupId(group.getGroupId());
                    mdseGroupMap.setDeleted(SystemConstants.DELETED_NO);
                    return mdseGroupMap;
                }).collect(Collectors.toList());

        mdseGroupMapRepository.saveAll(mdseGroupMaps);
    }

    @Override
    public List<MdseGroup> findAllByMdseId(Long mdseId) {
        List<MallMdseGroupMap> mdseGroupMapList = mdseGroupMapRepository.findAllByMdseId(mdseId);
        return mdseGroupRepository.findAllById(
                mdseGroupMapList
                        .stream()
                        .map(MallMdseGroupMap::getGroupId)
                        .collect(Collectors.toList()));
    }

    @Override
    public List<MallMdseGroupMap> findAllMdseGroupById(Long groupId) {

        return mdseGroupMapRepository.findAllByGroupId(groupId);
    }
}
