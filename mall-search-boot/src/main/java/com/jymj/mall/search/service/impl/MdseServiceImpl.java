package com.jymj.mall.search.service.impl;

import com.google.common.collect.Lists;
import com.jymj.mall.common.web.util.PageUtils;
import com.jymj.mall.mdse.dto.MdsePageQuery;
import com.jymj.mall.mdse.vo.MdseInfo;
import com.jymj.mall.search.repository.MdseInfoRepository;
import com.jymj.mall.search.service.MdseService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 商品
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-19
 */
@Service
@RequiredArgsConstructor
public class MdseServiceImpl implements MdseService {

    private final MdseInfoRepository mdseInfoRepository;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public MdseInfo add(MdseInfo mdseInfo) {
        return mdseInfoRepository.save(mdseInfo);
    }

    @Override
    public void delete(String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(idList)) {
            Iterable<MdseInfo> mdseInfoIterable = mdseInfoRepository.findAllById(idList);
            mdseInfoRepository.deleteAll(mdseInfoIterable);
        }
    }

    @Override
    public MdseInfo update(MdseInfo mdseInfo) {

        return mdseInfoRepository.save(mdseInfo);
    }

    @Override
    public Optional<MdseInfo> findById(Long mdseId) {
        return mdseInfoRepository.findById(mdseId);
    }

    @Override
    public SearchPage<MdseInfo> findPage(MdsePageQuery mdsePageQuery) {

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        BoolQueryBuilder boolQueryBuilder = buildBasicQuery(mdsePageQuery);
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

        if (mdsePageQuery.getProperties().equals("location") && Objects.nonNull(mdsePageQuery.getLat()) && Objects.nonNull(mdsePageQuery.getLon())) {
            GeoDistanceSortBuilder sortBuilder = SortBuilders.geoDistanceSort("location", mdsePageQuery.getLat(), mdsePageQuery.getLon());
            sortBuilder.unit(DistanceUnit.METERS);
            sortBuilder.order(Objects.nonNull(mdsePageQuery.getDirection()) ? mdsePageQuery.getDirection().equals(1) ? SortOrder.ASC : SortOrder.DESC : SortOrder.ASC);
            nativeSearchQueryBuilder.withSort(sortBuilder);
            mdsePageQuery.setProperties(null);
        }

        Pageable pageable = PageUtils.getPageable(mdsePageQuery);
        nativeSearchQueryBuilder.withPageable(pageable);

        //商品ids查询
        if (mdsePageQuery.getMdseIds() != null) {
            List<Long> mdseIdList = Arrays.stream(mdsePageQuery.getMdseIds().split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
            boolQueryBuilder.filter(QueryBuilders.termsQuery("mdseId", mdseIdList));
        }

        //商品分类ids查询
        if (mdsePageQuery.getTypeIds() != null) {
            List<Long> mdseTypeIdList = Arrays.stream(mdsePageQuery.getTypeIds().split(",")).filter(id -> !ObjectUtils.isEmpty(id)).map(Long::parseLong).collect(Collectors.toList());
            boolQueryBuilder.filter(QueryBuilders.termsQuery("typeInfo.typeId", mdseTypeIdList));
        }


        SearchHits<MdseInfo> searchHitsResult = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), MdseInfo.class);

        return SearchHitSupport.searchPageFor(searchHitsResult, pageable);
    }

    @Override
    public List<MdseInfo> updateAll(List<MdseInfo> mdseInfoList) {
        Iterable<MdseInfo> mdseInfoIterable = mdseInfoRepository.saveAll(mdseInfoList);
        return Lists.newArrayList(mdseInfoIterable);
    }

    private BoolQueryBuilder buildBasicQuery(MdsePageQuery mdsePageQuery) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (!ObjectUtils.isEmpty(mdsePageQuery.getClassify())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("classify", mdsePageQuery.getClassify()));
        }
        if (StringUtils.hasText(mdsePageQuery.getName())) {
            boolQueryBuilder.filter(QueryBuilders.matchQuery("name", mdsePageQuery.getName()));
        }
        if (!ObjectUtils.isEmpty(mdsePageQuery.getTypeId())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("typeInfo.typeId", mdsePageQuery.getTypeId()));
        }
        if (!ObjectUtils.isEmpty(mdsePageQuery.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.matchQuery("brand.brandId", mdsePageQuery.getBrandId()));
        }
        if (!ObjectUtils.isEmpty(mdsePageQuery.getGroupId())) {
            boolQueryBuilder.filter(QueryBuilders.matchQuery("groupList.groupId", mdsePageQuery.getGroupId()));
        }
        if (!StringUtils.hasText(mdsePageQuery.getProperties())) {
            mdsePageQuery.setProperties("salesVolume");
        }
        boolQueryBuilder.filter(QueryBuilders.matchQuery("status", 1));
        return boolQueryBuilder;
    }
}
