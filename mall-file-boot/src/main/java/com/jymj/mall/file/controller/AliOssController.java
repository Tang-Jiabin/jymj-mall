package com.jymj.mall.file.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.file.common.AliYunConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




/**
 * 阿里OSS
 *
 * @author J.Tang
 * @version 1.0
 * @email seven_tjb@163.com
 * @date 2022-09-05
 */
@Slf4j
@Api(tags = "阿里OSS")
@RestController
@RequestMapping("/api/v1/alioss")
@RequiredArgsConstructor
public class AliOssController {

    private final AliYunConfig aliYunConfig;

    @ApiOperation("STS")
    @GetMapping("/sts")
    public Result<JSONObject> sts() {

        DefaultProfile profile = DefaultProfile.getProfile(aliYunConfig.getOss().getRegionId(), aliYunConfig.getAccessKey(), aliYunConfig.getSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        AssumeRoleRequest request = new AssumeRoleRequest();
        request.setRoleArn(aliYunConfig.getRole().getArn());
        request.setRoleSessionName(aliYunConfig.getRole().getSessionName());

        //发起请求，并得到响应。
        try {
            AssumeRoleResponse response = client.getAcsResponse(request);
            JSONObject jsonObject = (JSONObject) JSON.toJSON(response);
            jsonObject.put("StatusCode", 200);
            return Result.success(jsonObject);
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException  e) {
            log.error("ErrCode:" + e.getErrCode());
            log.error("ErrMsg:" + e.getErrMsg());
            log.error("RequestId:" + e.getRequestId());
        }
        return Result.failed( "获取失败");
    }

}
