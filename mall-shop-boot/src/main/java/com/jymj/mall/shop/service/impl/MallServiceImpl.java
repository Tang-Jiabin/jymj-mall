package com.jymj.mall.shop.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.*;
import com.jymj.mall.admin.dto.*;
import com.jymj.mall.admin.vo.AdminInfo;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.admin.vo.DistrictInfo;
import com.jymj.mall.admin.vo.RoleInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.shop.dto.AddMallDTO;
import com.jymj.mall.shop.dto.MallPageQueryDTO;
import com.jymj.mall.shop.dto.UpdateMallDTO;
import com.jymj.mall.shop.entity.MallAuth;
import com.jymj.mall.shop.entity.MallDetails;
import com.jymj.mall.shop.entity.MallTag;
import com.jymj.mall.shop.repository.MallDetailsRepository;
import com.jymj.mall.shop.service.MallAuthService;
import com.jymj.mall.shop.service.MallService;
import com.jymj.mall.shop.service.MallTagService;
import com.jymj.mall.shop.vo.MallAuthInfo;
import com.jymj.mall.shop.vo.MallInfo;
import com.jymj.mall.shop.vo.TagInfo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商场
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MallServiceImpl implements MallService {

    private final AdminFeignClient adminFeignClient;
    private final DeptFeignClient deptFeignClient;
    private final DistrictFeignClient districtFeignClient;
    private final PermissionFeignClient permissionFeignClient;
    private final MallTagService mallTagService;
    private final MallDetailsRepository mallDetailsRepository;
    private final MallAuthService authService;
    private final RoleFeignClient roleFeignClient;

    @Override
    @GlobalTransactional(name = "mall-shop-add-mall", rollbackFor = Exception.class)
    public void addMall(AddMallDTO mallDTO) {

        AddDeptDTO deptDTO = new AddDeptDTO();
        deptDTO.setName(mallDTO.getName());
        deptDTO.setParentId(1L);
        deptDTO.setSort(mallDTO.getSort());
        deptDTO.setStatus(mallDTO.getStatus());
        Result<DeptInfo> deptInfoResult = deptFeignClient.add(deptDTO);

        if (Result.isSuccess(deptInfoResult)) {
            MallDetails mallDetails = new MallDetails();

            mallDetails.setName(mallDTO.getName());
            mallDetails.setLogo(mallDTO.getLogo());
            mallDetails.setIntroduce(mallDTO.getIntroduce());
            mallDetails.setDistrictId(mallDTO.getDistrictId());

            mallDetails.setDeptId(deptInfoResult.getData().getDeptId());
            mallDetails.setManagerName(mallDTO.getNickname());
            mallDetails.setManagerMobile(mallDTO.getMobile());
            mallDetails.setDeleted(SystemConstants.DELETED_NO);
            mallDetails.setMallNo(generateNo(mallDTO.getDistrictId()));
            mallDetails.setType(mallDTO.getType());
            mallDetails = mallDetailsRepository.save(mallDetails);

            if (mallDTO.getTagId() != null && mallDTO.getTagId().size() > 0) {
                mallTagService.addMallTag(mallDetails.getMallId(), mallDTO.getTagId());
            }

            AddRole addRole = new AddRole();
            addRole.setName("高级管理员");
            addRole.setCode("ADMIN");
            addRole.setDescribe("具备店铺所有管理的权限。");
            addRole.setSort(0);
            addRole.setStatus(1);
            addRole.setDeptId(deptInfoResult.getData().getDeptId());

            Result<RoleInfo> roleInfoResult = roleFeignClient.addRole(addRole);
            if (Result.isSuccess(roleInfoResult)) {
                List<Long> permIdsList = Lists.newArrayList();
                int permMax = 75;
                for (int i = 1; i < permMax; i++) {
                    permIdsList.add((long) i);
                }
                RoleInfo roleInfo = roleInfoResult.getData();
                RoleResource roleResource = new RoleResource();
                roleResource.setMenuIds(Lists.newArrayList());
                roleResource.setPermIds(permIdsList);
                roleFeignClient.updateRoleResource(roleInfo.getRoleId(), roleResource);

                UpdateAdminDTO adminDTO = new UpdateAdminDTO();
                adminDTO.setUsername(mallDTO.getMobile());
                adminDTO.setPassword(SystemConstants.DEFAULT_USER_PASSWORD);
                adminDTO.setNickname(mallDTO.getNickname());
                adminDTO.setMobile(mallDTO.getMobile());
                adminDTO.setGender(0);
                adminDTO.setNumber(mallDetails.getMallNo() + "001");
                adminDTO.setMallId(mallDetails.getMallId());
                adminDTO.setStatus(SystemConstants.STATUS_OPEN);
                adminDTO.setDeptId(deptInfoResult.getData().getDeptId());
                adminDTO.setRoleIdList(Lists.newArrayList(roleInfo.getRoleId()));
                adminFeignClient.add(adminDTO);
            }


        }

    }


    @Override
    public Page<MallDetails> findPage(MallPageQueryDTO mallPageQuery) {

        Sort.Direction direction = PageUtils.getPageDirection(mallPageQuery);
        String properties = PageUtils.getPageProperties(mallPageQuery);

        Pageable pageable = PageRequest.of(mallPageQuery.getPage(), mallPageQuery.getSize(), direction, properties);
        return mallDetailsRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> list = Lists.newArrayList();

            if (StringUtils.hasText(mallPageQuery.getName())) {
                list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + mallPageQuery.getName() + "%"));
            }
            if (StringUtils.hasText(mallPageQuery.getManagerName())) {
                list.add(criteriaBuilder.like(root.get("managerName").as(String.class), "%" + mallPageQuery.getManagerName() + "%"));
            }
            if (StringUtils.hasText(mallPageQuery.getManagerMobile())) {
                list.add(criteriaBuilder.like(root.get("managerMobile").as(String.class), "%" + mallPageQuery.getManagerMobile() + "%"));
            }
            if (mallPageQuery.getType() != null) {
                list.add(criteriaBuilder.equal(root.get("type").as(Integer.class), mallPageQuery.getType()));
            }
            if (mallPageQuery.getDistrictId() != null) {
                Result<List<DistrictInfo>> districtListResult = districtFeignClient.children(mallPageQuery.getDistrictId());
                if (Result.isSuccess(districtListResult)) {
                    List<DistrictInfo> districtInfoList = districtListResult.getData();
                    List<Long> districtIdList = Optional
                            .ofNullable(districtInfoList)
                            .orElse(Lists.newArrayList())
                            .stream()
                            .map(DistrictInfo::getDistrictId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    CriteriaBuilder.In<Long> in = criteriaBuilder.in(root.get("districtId").as(Long.class));
                    districtIdList.forEach(in::value);
                    list.add(in);
                }
            }

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), SystemConstants.DELETED_NO));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and(list.toArray(p));
        }, pageable);
    }

    @Override
    public List<MallInfo> list2vo(List<MallDetails> content) {
        List<MallInfo> mallVOList = Lists.newArrayList();
        Optional.ofNullable(content)
                .orElse(Lists.newArrayList())
                .forEach(mall -> {
                    MallInfo mallVO = mall2vo(mall);
                    mallVOList.add(mallVO);
                });
        return mallVOList;
    }

    @Override
    public void deleteMall(String ids) {
        List<Long> mallIds = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        Optional.of(mallIds)
                .orElse(new ArrayList<>())
                .forEach(id -> {
                    //TODO 删除管理员
                    int count = 0;
                    Assert.isTrue(count <= 0, "该商场已分配管理，无法删除");

                    //TODO 删除网点

                    //TODO 删除商品
                    mallDetailsRepository.deleteById(id);
                });


        // 删除成功，刷新权限缓存
        permissionFeignClient.refresh();
    }

    @Override
    @GlobalTransactional(name = "mall-shop-update-mall", rollbackFor = Exception.class)
    public void updateMall(UpdateMallDTO updateMallDTO) {
        Optional<MallDetails> mallDetailsOptional = mallDetailsRepository.findById(updateMallDTO.getMallId());

        mallDetailsOptional.ifPresent(mallDetails -> {

            updateMallDeptInfo(updateMallDTO, mallDetails);

            updateMallTagInfo(updateMallDTO, mallDetails);

            updateMallAdminInfo(updateMallDTO, mallDetails);


            mallDetails.setMallId(updateMallDTO.getMallId());
            mallDetails.setMallNo(updateMallDTO.getMallNo());
            mallDetails.setName(updateMallDTO.getName());
            mallDetails.setLogo(updateMallDTO.getLogo());
            mallDetails.setIntroduce(updateMallDTO.getIntroduce());
            mallDetails.setType(updateMallDTO.getType());
            mallDetails.setDistrictId(updateMallDTO.getDistrictId());
            mallDetails.setManagerName(updateMallDTO.getNickname());
            mallDetails.setManagerMobile(updateMallDTO.getMobile());
            mallDetailsRepository.save(mallDetails);
        });

    }

    @Override
    public Optional<MallDetails> findById(Long mallId) {
        return mallDetailsRepository.findById(mallId);
    }


    public MallInfo mall2vo(MallDetails mall) {
        MallInfo mallVO = new MallInfo();
        mallVO.setMallId(mall.getMallId());
        mallVO.setDeptId(mall.getDeptId());
        mallVO.setMallNo(mall.getMallNo());
        mallVO.setName(mall.getName());
        mallVO.setLogo(mall.getLogo());
        mallVO.setIntroduce(mall.getIntroduce());
        mallVO.setManagerName(mall.getManagerName());
        mallVO.setManagerMobile(mall.getManagerMobile());
        mallVO.setCreateTime(mall.getCreateTime());

        if (mall.getDistrictId() != null) {
            Result<List<DistrictInfo>> districtListResult = districtFeignClient.parent(mall.getDistrictId());
            if (Result.isSuccess(districtListResult)) {
                List<DistrictInfo> districtInfoList = districtListResult.getData();
                String districtName = districtInfo2String(districtInfoList, SystemConstants.ROOT_DISTRICT_ID);
                String districtIdStr = districtInfo2idStr(districtInfoList, SystemConstants.ROOT_DISTRICT_ID);
                String[] ids = districtIdStr.split("-");
                Optional<DistrictInfo> districtInfoOptional = districtInfoList.stream().filter(districtInfo -> districtInfo.getDistrictId().equals(mall.getDistrictId())).findFirst();
                districtInfoOptional.ifPresent(districtInfo -> {
                    districtInfo.setName(districtName);
                    districtInfo.setIds(ids);
                    mallVO.setDistrictInfo(districtInfo);
                });
            }
        }
        if (mall.getType() != null) {
            mallVO.setType(mall.getType().getLabel());
        }
        if (mall.getCreateTime() != null) {
            mallVO.setCreateTime(mall.getCreateTime());
        }
        List<MallTag> mallTagList = mallTagService.findAllByMallId(mall.getMallId());
        List<TagInfo> tagInfoList = mallTagService.list2vo(mallTagList);
        mallVO.setTagList(tagInfoList);

        Optional<MallAuth> authOptional = authService.getAuthByMallId(mall.getMallId());
        if (authOptional.isPresent()) {
            MallAuthInfo authInfo = authService.auth2vo(authOptional.get());
            mallVO.setAuthInfo(authInfo);
        }

        return mallVO;
    }

    private String districtInfo2idStr(List<DistrictInfo> districtInfoList, Long pid) {
        StringBuilder sb = new StringBuilder();
        Optional.ofNullable(districtInfoList).orElse(Lists.newArrayList()).stream().filter(districtInfo -> districtInfo.getPid().equals(pid)).forEach(districtInfo -> {
            sb.append(districtInfo.getDistrictId());
            String districtId = districtInfo2idStr(districtInfoList, districtInfo.getDistrictId());
            if (StringUtils.hasText(districtId)) {
                sb.append("-");
                sb.append(districtId);
            }
        });

        return sb.toString();
    }

    @Override
    public Optional<MallDetails> findByDeptId(Long deptId) {

        return mallDetailsRepository.findByDeptId(deptId);

    }

    @Override
    public List<MallDetails> findAllByDeptIdIn(String deptIds) {
        List<Long> deptIdList = Arrays.stream(deptIds.split(",")).map(Long::parseLong).collect(Collectors.toList());
        return mallDetailsRepository.findAllByDeptIdIn(deptIdList);
    }

    private String districtInfo2String(List<DistrictInfo> districtInfoList, Long pid) {
        StringBuilder sb = new StringBuilder();
        Optional.ofNullable(districtInfoList).orElse(Lists.newArrayList()).stream().filter(districtInfo -> districtInfo.getPid().equals(pid)).forEach(districtInfo -> {
            sb.append(districtInfo.getName());
            String name = districtInfo2String(districtInfoList, districtInfo.getDistrictId());
            if (StringUtils.hasText(name)) {
                sb.append("-");
                sb.append(name);
            }
        });

        return sb.toString();
    }


    private String generateNo(Long districtId) {
//        Optional<SysDistrict> districtOptional = districtService.findById(districtId);
//        SysDistrict sysDistrict = districtOptional.orElseThrow(() -> new BusinessException("行政区不存在"));
//        return sysDistrict.getCode() + RandomUtil.randomNumbers(5);
        return RandomUtil.randomNumbers(5);
    }

    private void updateMallDeptInfo(UpdateMallDTO updateMallDTO, MallDetails mallDetails) {
        if (!mallDetails.getName().equals(updateMallDTO.getName())) {
            Result<DeptInfo> deptInfoResult = deptFeignClient.getDeptById(mallDetails.getDeptId());
            if (Result.isSuccess(deptInfoResult)) {
                DeptInfo deptInfo = deptInfoResult.getData();
                UpdateDeptDTO updateDeptDTO = new UpdateDeptDTO();
                updateDeptDTO.setDeptId(deptInfo.getDeptId());
                updateDeptDTO.setName(updateMallDTO.getName());
                updateDeptDTO.setParentId(deptInfo.getParentId());
                updateDeptDTO.setSort(deptInfo.getSort());
                updateDeptDTO.setStatus(deptInfo.getStatus());
                deptFeignClient.updateDept(updateDeptDTO);
            }
        }
    }

    private void updateMallTagInfo(UpdateMallDTO updateMallDTO, MallDetails mallDetails) {
        List<Long> newTagIdList = updateMallDTO.getTagId();
        List<Long> oldTagIdList = mallTagService.findAllTagIdByMallId(mallDetails.getMallId());
        if (!ObjectUtils.nullSafeEquals(newTagIdList, oldTagIdList)) {
            List<Long> deleteIdList = oldTagIdList.stream().filter(item -> !newTagIdList.contains(item)).collect(Collectors.toList());
            List<Long> saveIdList = newTagIdList.stream().filter(item -> !oldTagIdList.contains(item)).collect(Collectors.toList());
            if (deleteIdList.size() > 0) {
                mallTagService.deleteMallTag(mallDetails.getMallId(), deleteIdList);
            }

            if (saveIdList.size() > 0) {
                mallTagService.addMallTag(mallDetails.getMallId(), saveIdList);
            }
        }
    }

    private void updateMallAdminInfo(UpdateMallDTO updateMallDTO, MallDetails mallDetails) {

        if (!mallDetails.getManagerName().equals(updateMallDTO.getNickname()) || !mallDetails.getManagerMobile().equals(updateMallDTO.getMobile())) {
            Result<AdminInfo> oldAdmin = adminFeignClient.getAdminByMobile(mallDetails.getManagerMobile());
            if (Result.isSuccess(oldAdmin)) {
                AdminInfo adminInfo = oldAdmin.getData();
                UpdateAdminDTO updateAdminDTO = new UpdateAdminDTO();
                updateAdminDTO.setAdminId(adminInfo.getAdminId());
                if (!mallDetails.getManagerName().equals(updateMallDTO.getNickname())) {
                    updateAdminDTO.setNickname(updateMallDTO.getNickname());
                }
                if (!mallDetails.getManagerMobile().equals(updateMallDTO.getMobile())) {
                    updateAdminDTO.setMobile(updateMallDTO.getMobile());
                }
                if (adminInfo.getUsername().equals(adminInfo.getMobile())) {
                    updateAdminDTO.setUsername(updateMallDTO.getMobile());
                }
                adminFeignClient.updateAdmin(updateAdminDTO);
            }
        }
    }
}
