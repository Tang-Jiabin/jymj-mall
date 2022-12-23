package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.mdse.dto.GroupDTO;
import com.jymj.mall.mdse.dto.GroupPageQuery;
import com.jymj.mall.mdse.entity.MallMdseGroupMap;
import com.jymj.mall.mdse.entity.MdseGroup;
import com.jymj.mall.mdse.repository.MdseGroupMapRepository;
import com.jymj.mall.mdse.repository.MdseGroupRepository;
import com.jymj.mall.mdse.service.GroupService;
import com.jymj.mall.mdse.vo.GroupInfo;
import com.jymj.mall.shop.api.MallFeignClient;
import com.jymj.mall.shop.vo.MallInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 分组
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final MdseGroupRepository mdseGroupRepository;
    private final MdseGroupMapRepository mdseGroupMapRepository;
    private final MallFeignClient mallFeignClient;
    private final DeptFeignClient deptFeignClient;
    private final ThreadPoolTaskExecutor executor;
    @Override
    public MdseGroup add(GroupDTO dto) {

        MdseGroup group = new MdseGroup();
        group.setNumber(dto.getNumber());
        group.setName(dto.getName());
        group.setShow(dto.getShow());
        group.setMallId(dto.getMallId());
        group.setRemarks(dto.getRemarks());
        group.setDeleted(SystemConstants.DELETED_NO);

        return mdseGroupRepository.save(group);
    }


    @Override
    @CacheEvict(value = {"mall-mdse:group-info:", "mall-mdse:group-entity:"}, key = "'group-id:'+#dto.groupId")
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
                if (!ObjectUtils.isEmpty(dto.getShow()) && !group.getShow().equals(dto.getShow())) {
                    group.setShow(dto.getShow());
                    update = true;
                }
                if (StringUtils.hasText(dto.getRemarks()) && !group.getRemarks().equals(dto.getRemarks())) {
                    group.setRemarks(dto.getRemarks());
                    update = true;
                }
                if (!ObjectUtils.isEmpty(dto.getMallId()) && !group.getMallId().equals(dto.getMallId())) {

                    group.setMallId(dto.getMallId());
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
    @CacheEvict(value = {"mall-mdse:group-info:", "mall-mdse:group-entity:"},allEntries = true)
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseGroup> groupList = mdseGroupRepository.findAllById(idList);
            mdseGroupRepository.deleteAll(groupList);
        }
    }

    @Override
    @Cacheable(cacheNames = "mall-mdse:group-entity:", key = "'group-id:'+#id")
    public Optional<MdseGroup> findById(Long id) {
        return mdseGroupRepository.findById(id);
    }

    @Override
    @Cacheable(cacheNames = "mall-mdse:group-info:", key = "'group-id:'+#entity.groupId")
    public GroupInfo entity2vo(MdseGroup entity) {

        if (entity != null) {

            return getGroupInfo(entity);
        }

        return null;
    }



    @NotNull
    private GroupInfo getGroupInfo(MdseGroup entity) {
        GroupInfo info = new GroupInfo();
        info.setGroupId(entity.getGroupId());
        info.setNumber(entity.getNumber());
        info.setName(entity.getName());
        info.setShow(entity.getShow());
        info.setRemarks(entity.getRemarks());
        info.setCreateTime(DateFormatUtils.format(entity.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        Integer count = mdseGroupMapRepository.countByGroupId(entity.getGroupId());
        info.setMdseCount(count);
        return info;
    }

    @Override
    public List<GroupInfo> list2vo(List<MdseGroup> entityList) {
        List<CompletableFuture<GroupInfo>> futureList = Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(entity -> CompletableFuture.supplyAsync(() -> entity2vo(entity), executor))
                .collect(Collectors.toList());
        return futureList.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<MdseGroup> findAllByAuth() {
        Long deptId = UserUtils.getDeptId();
        Result<List<DeptInfo>> deptListResult = deptFeignClient.tree(deptId);

        if (Result.isSuccess(deptListResult)) {
            List<DeptInfo> deptInfoList = deptListResult.getData();
            List<Long> deptIdList = deptInfoList.stream().map(DeptInfo::getDeptId).collect(Collectors.toList());
            Result<List<MallInfo>> mallInfoListResult = mallFeignClient.getMallByDeptIdIn(StringUtils.collectionToCommaDelimitedString(deptIdList));
            if (Result.isSuccess(mallInfoListResult)) {
                List<MallInfo> mallInfoList = mallInfoListResult.getData();
                List<Long> mallIdList = mallInfoList.stream().map(MallInfo::getMallId).collect(Collectors.toList());
                return mdseGroupRepository.findAllByMallIdInAndShow(mallIdList, true);
            }
        }
        return Lists.newArrayList();
    }


    @Override
    public Page<MdseGroup> findPage(GroupPageQuery groupPageQuery) {

        Pageable pageable = PageUtils.getPageable(groupPageQuery);


        Specification<MdseGroup> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();


            if (StringUtils.hasText(groupPageQuery.getNumber())) {
                list.add(criteriaBuilder.like(root.get("number").as(String.class), groupPageQuery.getNumber() + SystemConstants.SQL_LIKE));
            }

            if (StringUtils.hasText(groupPageQuery.getName())) {
                list.add(criteriaBuilder.like(root.get("name").as(String.class), groupPageQuery.getName() + SystemConstants.SQL_LIKE));
            }

            if (!ObjectUtils.isEmpty(groupPageQuery.getShow())) {
                list.add(criteriaBuilder.equal(root.get("show").as(Boolean.class), groupPageQuery.getShow()));
            }

            if (!ObjectUtils.isEmpty(groupPageQuery.getMallId())) {
                list.add(criteriaBuilder.equal(root.get("mallId").as(Long.class), groupPageQuery.getMallId()));
            }

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        };

        return mdseGroupRepository.findAll(spec, pageable);

    }

    @Override
    public List<MdseGroup> findAllById(List<Long> groupIdList) {
        return mdseGroupRepository.findAllById(groupIdList);
    }

    @Override
    public void addMdseGroupMap(Long mdseId, List<Long> groupIdList) {
        List<MallMdseGroupMap> mdseGroupMaps = Optional.of(groupIdList)
                .orElse(Lists.newArrayList())
                .stream().filter(group -> !ObjectUtils.isEmpty(group))
                .map(groupId -> {
                    MallMdseGroupMap mdseGroupMap = new MallMdseGroupMap();
                    mdseGroupMap.setMdseId(mdseId);
                    mdseGroupMap.setGroupId(groupId);
                    mdseGroupMap.setDeleted(SystemConstants.DELETED_NO);
                    return mdseGroupMap;
                }).collect(Collectors.toList());

        mdseGroupMapRepository.saveAll(mdseGroupMaps);
    }

    @Override
    public void addMdseGroupMap(List<Long> mdseIdList, Long groupId) {
        List<MallMdseGroupMap> mdseGroupMaps = Optional.of(mdseIdList)
                .orElse(Lists.newArrayList())
                .stream().filter(group -> !ObjectUtils.isEmpty(group))
                .map(mdseId -> {
                    MallMdseGroupMap mdseGroupMap = new MallMdseGroupMap();
                    mdseGroupMap.setMdseId(mdseId);
                    mdseGroupMap.setGroupId(groupId);
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

    @Override
    public void deleteMdseGroupAll(List<MallMdseGroupMap> deleteMdseGroupList) {
        if (!CollectionUtils.isEmpty(deleteMdseGroupList)) {
            mdseGroupMapRepository.deleteAll(deleteMdseGroupList);
        }
    }

    @Override
    public List<MallMdseGroupMap> findMdseGroupAllByMdseId(Long mdseId) {
        return mdseGroupMapRepository.findAllByMdseId(mdseId);
    }

    @Override
    public List<MdseGroup> findAllByMallId(Long mallId) {
        if (mallId != null) {
            return mdseGroupRepository.findAllByMallId(mallId);
        }
        return mdseGroupRepository.findAll();
    }
}
