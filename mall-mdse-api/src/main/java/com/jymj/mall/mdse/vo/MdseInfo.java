package com.jymj.mall.mdse.vo;


import com.jymj.mall.mdse.enums.InventoryReductionMethod;
import com.jymj.mall.shop.vo.ShopInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品vo
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-08-31
 */
@Data
@ApiModel("商品信息")
@Document(indexName = "mdse_info")
public class MdseInfo {

    @Id
    @ApiModelProperty("商品id")
    private Long mdseId;

    @ApiModelProperty("商品图片")
    private List<PictureInfo> pictureList;

    @ApiModelProperty("商品视频")
    private List<PictureInfo> videoList;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品编号")
    private String number;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("运费")
    private BigDecimal postage;

    @ApiModelProperty("销售数量")
    private Integer salesVolume;

    @ApiModelProperty("起售数量")
    private Integer startingQuantity;

    @ApiModelProperty("剩余数量 t-显示 f-不显示")
    private boolean showRemainingQuantity;

    @ApiModelProperty("退款 t-支持退款 f-不支持退款")
    private boolean refund;

    @ApiModelProperty("库存减少方式")
    private InventoryReductionMethod inventoryReductionMethod;

    @ApiModelProperty("购买按钮名称")
    private String buttonName;

    @ApiModelProperty("商品详情")
    private String details;

    @ApiModelProperty("分组集合")
    private List<GroupInfo> groupList;

    @ApiModelProperty("品牌")
    private BrandInfo brand;

    @ApiModelProperty("厂家")
    private MfgInfo mfg;

    @ApiModelProperty("类型")
    private TypeInfo typeInfo;

    @ApiModelProperty("店铺")
    private ShopInfo shopInfo;

    @ApiModelProperty("库存规格集合")
    private List<StockInfo> stockList;

    @ApiModelProperty("标签")
    private List<LabelInfo> labelInfoList;

    @ApiModelProperty("商品状态 1-上架 2-下架")
    private Integer status;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("库存规格")
    private List<SpecMap> specMap;

    @ApiModelProperty("商城id")
    private Long mallId;

    @ApiModelProperty("生效规则")
    private EffectiveRulesInfo effectiveRules;

    @ApiModelProperty("商品列表")
    private List<MdseInfo> mdseInfoList;

    @ApiModelProperty("商品分类  1-商品 2-卡")
    private Integer classify;

    @ApiModelProperty("商品坐标")
    private GeoPoint location;

    @ApiModelProperty("配送方式 1-快递 2-自提 3-快递+自提")
    private Integer deliveryMethod;
}
