package com.jymj.mall.shop.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.AdminFeignClient;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.admin.api.DistrictFeignClient;
import com.jymj.mall.admin.dto.AddAdminDTO;
import com.jymj.mall.admin.dto.AddDeptDTO;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.admin.vo.DistrictInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.shop.dto.AddMallDTO;
import com.jymj.mall.shop.dto.MallPageQueryDTO;
import com.jymj.mall.shop.entity.MallDetails;
import com.jymj.mall.shop.repository.MallDetailsRepository;
import com.jymj.mall.shop.service.MallService;
import com.jymj.mall.shop.vo.MallVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private final MallDetailsRepository mallDetailsRepository;

    private final DeptFeignClient deptFeignClient;
    private final DistrictFeignClient districtFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddMallDTO mallDTO) {

        AddDeptDTO deptDTO = new AddDeptDTO();
        deptDTO.setName(mallDTO.getName());
        deptDTO.setParentId(1L);
        deptDTO.setSort(mallDTO.getSort());
        deptDTO.setStatus(mallDTO.getStatus());
        Result<DeptInfo> deptInfoResult = deptFeignClient.add(deptDTO);

        if (Result.isSuccess(deptInfoResult)) {
            MallDetails mallDetails = new MallDetails();
            BeanUtils.copyProperties(mallDTO, mallDetails);
            mallDetails.setDeptId(deptInfoResult.getData().getDeptId());
            mallDetails.setManagerName(mallDTO.getNickname());
            mallDetails.setManagerMobile(mallDTO.getMobile());
            mallDetails.setDeleted(0);
            mallDetails.setMallNo(generateNo(mallDTO.getDistrictId()));
            mallDetails = mallDetailsRepository.save(mallDetails);

            AddAdminDTO adminDTO = new AddAdminDTO();
            adminDTO.setUsername(mallDTO.getMobile());
            adminDTO.setPassword(SystemConstants.DEFAULT_USER_PASSWORD);
            adminDTO.setNickname(mallDTO.getNickname());
            adminDTO.setMobile(mallDTO.getMobile());
            adminDTO.setGender(0);
            adminDTO.setStatus(SystemConstants.STATUS_OPEN);
            adminDTO.setDeptId(deptInfoResult.getData().getDeptId());
            adminDTO.setRoleIdList(Lists.newArrayList(3L));
            adminFeignClient.add(adminDTO);
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

            list.add(criteriaBuilder.equal(root.get("deleted").as(Integer.class), 0));
            Predicate[] p = new Predicate[list.size()];
            return criteriaBuilder.and((Predicate[]) list.toArray(p));
        }, pageable);
    }

    @Override
    public List<MallVO> list2vo(List<MallDetails> content) {
        List<MallVO> mallVOList = Lists.newArrayList();
        Optional.ofNullable(content)
                .orElse(Lists.newArrayList())
                .forEach(mall -> {
                    MallVO mallVO = mall2vo(mall);
                    mallVOList.add(mallVO);
                });
        return mallVOList;
    }

    private MallVO mall2vo(MallDetails mall) {
        MallVO mallVO = new MallVO();
        BeanUtils.copyProperties(mall, mallVO);
        if (mall.getDistrictId() != null) {
            Result<List<DistrictInfo>> districtListResult = districtFeignClient.parent(mall.getDistrictId());
            if (Result.isSuccess(districtListResult)) {
                List<DistrictInfo> districtInfoList = districtListResult.getData();
                String districtInfo = districtInfo2String(districtInfoList, SystemConstants.ROOT_DISTRICT_ID);
                mallVO.setDistrict(districtInfo);
            }
        }
        if (mall.getType() != null) {
            mallVO.setType(mall.getType().getLabel());
        }
        if (mall.getCreateTime() != null) {
            mallVO.setCreateTime(mall.getCreateTime());
        }

        return mallVO;
    }

    private String districtInfo2String(List<DistrictInfo> districtInfoList, Long pid) {
        StringBuilder sb = new StringBuilder();
        Optional.ofNullable(districtInfoList).orElse(Lists.newArrayList()).stream().filter(districtInfo -> districtInfo.getPid().equals(pid)).forEach(districtInfo -> {
            sb.append(districtInfo.getName());
            String name = districtInfo2String(districtInfoList, districtInfo.getDistrictId());
            if (StringUtils.hasText(name)){
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


}
