package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
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
    private final MallFeignClient mallFeignClient;
    private final DeptFeignClient deptFeignClient;

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
            info.setCreateTime(entity.getCreateTime());
            Integer count = mdseGroupMapRepository.countByGroupId(entity.getGroupId());
            info.setMdseCount(count);
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
        Long deptId = UserUtils.getDeptId();
        Result<List<DeptInfo>> deptListResult = deptFeignClient.tree(deptId);

        if (Result.isSuccess(deptListResult)) {
            List<DeptInfo> deptInfoList = deptListResult.getData();
            List<Long> deptIdList = deptInfoList.stream().map(DeptInfo::getDeptId).collect(Collectors.toList());
            Result<List<MallInfo>> mallInfoListResult = mallFeignClient.getMallByDeptIdIn(StringUtils.collectionToCommaDelimitedString(deptIdList));
            if (Result.isSuccess(mallInfoListResult)) {
                List<MallInfo> mallInfoList = mallInfoListResult.getData();
                List<Long> mallIdList = mallInfoList.stream().map(MallInfo::getMallId).collect(Collectors.toList());
                return mdseGroupRepository.findAllByMallIdInAndShow(mallIdList,true);
            }
        }
        return Lists.newArrayList();
    }

    private List<Long> findMallIdList() {
        Long deptId = UserUtils.getDeptId();
        Result<List<DeptInfo>> deptListResult = deptFeignClient.tree(deptId);

        if (Result.isSuccess(deptListResult)) {
            List<DeptInfo> deptInfoList = deptListResult.getData();
            List<Long> deptIdList = deptInfoList.stream().map(DeptInfo::getDeptId).collect(Collectors.toList());
            Result<List<MallInfo>> mallInfoListResult = mallFeignClient.getMallByDeptIdIn(StringUtils.collectionToCommaDelimitedString(deptIdList));
            if (Result.isSuccess(mallInfoListResult)) {
                List<MallInfo> mallInfoList = mallInfoListResult.getData();
                return mallInfoList.stream().map(MallInfo::getMallId).collect(Collectors.toList());
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
                list.add(criteriaBuilder.equal(root.get("mallId").as(Boolean.class), groupPageQuery.getMallId()));
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
        if (mallId!=null){
            return mdseGroupRepository.findAllByMallId(mallId);
        }
        return mdseGroupRepository.findAll();
    }
}