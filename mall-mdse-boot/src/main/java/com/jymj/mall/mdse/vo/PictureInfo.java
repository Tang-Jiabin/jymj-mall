package com.jymj.mall.mdse.vo;

import com.jymj.mall.mdse.enums.PictureType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 图片信息
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-02
 */
@Data
@ApiModel("图片信息")
public class PictureInfo {

    @ApiModelProperty("图片id")
    private Long pictureId;

    @ApiModelProperty("图片地址")
    private String url;

    @ApiModelProperty("图片类型")
    private PictureType type;

    @ApiModelProperty("规格id")
    private Long stockId;
}
