package com.jymj.mall.mdse.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.admin.api.DeptFeignClient;
import com.jymj.mall.admin.vo.DeptInfo;
import com.jymj.mall.common.constants.SystemConstants;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.web.util.UserUtils;
import com.jymj.mall.mdse.dto.LabelDTO;
import com.jymj.mall.mdse.entity.MallMdseLabelMap;
import com.jymj.mall.mdse.entity.MdseLabel;
import com.jymj.mall.mdse.repository.MdseLabelMapRepository;
import com.jymj.mall.mdse.repository.MdseLabelRepository;
import com.jymj.mall.mdse.service.LabelService;
import com.jymj.mall.mdse.vo.LabelInfo;
import com.jymj.mall.shop.api.MallFeignClient;
import com.jymj.mall.shop.vo.MallInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商品标签
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final MallFeignClient mallFeignClient;
    private final DeptFeignClient deptFeignClient;

    private final MdseLabelRepository labelRepository;
    private final MdseLabelMapRepository labelMapRepository;

    @Override
    public MdseLabel add(LabelDTO dto) {



        MdseLabel label = new MdseLabel();
        label.setName(dto.getName());
        label.setRemarks(dto.getRemarks());
        label.setMallId(dto.getMallId());
        label.setDeleted(SystemConstants.DELETED_NO);

        return labelRepository.save(label);

    }

    @Override
    public Optional<MdseLabel> update(LabelDTO dto) {
        if (!ObjectUtils.isEmpty(dto.getLabelId())) {
            Optional<MdseLabel> labelOptional = labelRepository.findById(dto.getLabelId());
            if (labelOptional.isPresent()) {
                MdseLabel mdseLabel = labelOptional.get();
                boolean update = false;

                if (StringUtils.hasText(dto.getName())) {
                    mdseLabel.setName(dto.getName());
                    update = true;
                }

                if (StringUtils.hasText(dto.getRemarks())) {
                    mdseLabel.setRemarks(dto.getRemarks());
                    update = true;
                }

                if (!ObjectUtils.isEmpty(dto.getMallId())) {
                    mdseLabel.setMallId(dto.getMallId());
                    update = true;
                }
                if (update) {
                    return Optional.of(labelRepository.save(mdseLabel));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            List<MdseLabel> labelList = labelRepository.findAllById(idList);
            labelRepository.deleteAll(labelList);
        }
    }

    @Override
    public Optional<MdseLabel> findById(Long id) {
        return labelRepository.findById(id);
    }

    @Override
    public LabelInfo entity2vo(MdseLabel entity) {

        if (!ObjectUtils.isEmpty(entity)) {
            LabelInfo labelInfo = new LabelInfo();
            labelInfo.setLabelId(entity.getLabelId());
            labelInfo.setName(entity.getName());
            labelInfo.setRemarks(entity.getRemarks());
            labelInfo.setMallId(entity.getMallId());
            return labelInfo;

        }
        return null;
    }

    @Override
    public List<LabelInfo> list2vo(List<MdseLabel> entityList) {
        return Optional.of(entityList)
                .orElse(Lists.newArrayList())
                .stream()
                .filter(entity -> !ObjectUtils.isEmpty(entity))
                .map(this::entity2vo)
                .collect(Collectors.toList());
    }



    @Override
    public List<MdseLabel> findAllById(List<Long> idList) {
        return labelRepository.findAllById(idList);
    }

    @Override
    public void addMdseLabelMap(Long mdseId, List<Long> labelList) {
        for (Long labelId : labelList) {
            MallMdseLabelMap labelMap = new MallMdseLabelMap();
            labelMap.setMdseId(mdseId);
            labelMap.setLabelId(labelId);
            labelMap.setDeleted(SystemConstants.DELETED_NO);
            labelMapRepository.save(labelMap);
        }
    }

    @Override
    public List<MdseLabel> findAllByMdseId(Long mdseId) {
        List<MallMdseLabelMap> mdseLabelMapList = labelMapRepository.findAllByMdseId(mdseId);
        return labelRepository.findAllById(
                mdseLabelMapList.stream().map(MallMdseLabelMap::getLabelId).collect(Collectors.toList())
        );
    }

    @Override
    public List<MdseLabel> findAllByAuth() {
        Long deptId = UserUtils.getDeptId();
        Result<List<DeptInfo>> deptListResult = deptFeignClient.tree(deptId);

        if (Result.isSuccess(deptListResult)) {
            List<DeptInfo> deptInfoList = deptListResult.getData();
            List<Long> deptIdList = deptInfoList.stream().map(DeptInfo::getDeptId).collect(Collectors.toList());
            Result<List<MallInfo>> mallInfoListResult = mallFeignClient.getMallByDeptIdIn(StringUtils.collectionToCommaDelimitedString(deptIdList));
            if (Result.isSuccess(mallInfoListResult)){
                List<MallInfo> mallInfoList = mallInfoListResult.getData();
                List<Long> mallIdList = mallInfoList.stream().map(MallInfo::getMallId).collect(Collectors.toList());
                return labelRepository.findAllByMallIdIn(mallIdList);
            }
        }
        return Lists.newArrayList();
    }

    @Override
    public List<MallMdseLabelMap> findMdseLabelAllByLabelId(Long labelId) {

        return labelMapRepository.findAllByLabelId(labelId);
    }

    @Override
    public List<MallMdseLabelMap> findMdseLabelAllByMdseId(Long mdseId) {

        return labelMapRepository.findAllByMdseId(mdseId);
    }

    @Override
    public void deleteMdseLabel(List<MallMdseLabelMap> deleteMdseLabelMapList) {
        labelMapRepository.deleteAll(deleteMdseLabelMapList);
    }

    @Override
    public List<MdseLabel> findAllByMallId(Long mallId) {

        if (mallId!=null){
            return labelRepository.findAllByMallId(mallId);
        }
        return labelRepository.findAll();
    }
}
