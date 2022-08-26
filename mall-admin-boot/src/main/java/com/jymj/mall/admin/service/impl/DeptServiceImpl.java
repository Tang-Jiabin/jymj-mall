package com.jymj.mall.admin.service.impl;

import com.jymj.mall.admin.dto.AddDeptDTO;
import com.jymj.mall.admin.dto.UpdateDeptDTO;
import com.jymj.mall.admin.entity.SysDept;
import com.jymj.mall.admin.repository.SysDeptRepository;
import com.jymj.mall.admin.service.DeptService;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 部门
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-15
 */
@Service
@AllArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final SysDeptRepository deptRepository;

    @Override
    public SysDept add(AddDeptDTO deptDTO) {
        SysDept sysDept = new SysDept();
        sysDept.setName(deptDTO.getName());
        sysDept.setParentId(deptDTO.getParentId());
        sysDept.setSort(deptDTO.getSort());
        sysDept.setStatus(deptDTO.getStatus());
        sysDept.setDeleted(SystemConstants.DELETED_NO);
        sysDept.setTreePath(generateDeptTreePath(sysDept));

        sysDept = deptRepository.save(sysDept);
        return sysDept;

    }

    @Override
    public Optional<SysDept> findById(Long deptId) {

        return deptRepository.findByDeptIdAndDeleted(deptId, SystemConstants.DELETED_NO);
    }

    @Override
    public DeptInfo dept2vo(SysDept dept) {
        DeptInfo deptInfo = new DeptInfo();
        deptInfo.setDeptId(dept.getDeptId());
        deptInfo.setName(dept.getName());
        deptInfo.setParentId(dept.getParentId());
        deptInfo.setSort(dept.getSort());
        deptInfo.setStatus(dept.getStatus());
        return deptInfo;
    }

    @Override
    public void updateDept(UpdateDeptDTO updateDeptDTO) {
        Optional<SysDept> deptOptional = deptRepository.findById(updateDeptDTO.getDeptId());
        SysDept sysDept = deptOptional.orElseThrow(() -> new BusinessException("部门不存在"));
        sysDept.setName(StringUtils.hasText(updateDeptDTO.getName()) ? updateDeptDTO.getName() : sysDept.getName());
        sysDept.setParentId(updateDeptDTO.getParentId() != null ? updateDeptDTO.getParentId() : sysDept.getParentId());
        sysDept.setSort(updateDeptDTO.getSort() != null ? updateDeptDTO.getSort() : sysDept.getSort());
        sysDept.setStatus(updateDeptDTO.getStatus() != null ? updateDeptDTO.getStatus() : sysDept.getStatus());
        deptRepository.save(sysDept);

    }

    @Override
    public void deleteDept(String ids) {
        List<Long> deptIds = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        Optional.of(deptIds)
                .orElse(new ArrayList<>())
                .forEach(deptRepository::deleteById);
    }


    private String generateDeptTreePath(SysDept dept) {
        Long parentId = dept.getParentId();
        String treePath;
        if (parentId.equals(SystemConstants.ROOT_DEPT_ID)) {
            treePath = String.valueOf(SystemConstants.ROOT_DEPT_ID);
        } else {
            Optional<SysDept> parentDept = deptRepository.findById(parentId);
            treePath = parentDept.map(item -> item.getTreePath() + "," + item.getDeptId()).orElse(Strings.EMPTY);
        }
        return treePath;
    }
}
