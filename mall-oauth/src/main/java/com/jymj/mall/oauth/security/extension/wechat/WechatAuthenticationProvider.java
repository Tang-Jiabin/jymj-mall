package com.jymj.mall.oauth.security.extension.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import cn.hutool.core.bean.BeanUtil;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.result.ResultCode;
import com.jymj.mall.oauth.security.core.userdetails.user.SysUserDetails;
import com.jymj.mall.oauth.security.core.userdetails.user.SysUserDetailsServiceImpl;
import com.jymj.mall.user.api.UserFeignClient;
import com.jymj.mall.user.dto.UserAuthDTO;
import com.jymj.mall.user.dto.UserDTO;
import com.jymj.mall.user.enums.SourceEnum;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

@Slf4j
@Data
public class WechatAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private WxMaService wxMaService;
    private UserFeignClient userFeignClient;

    /**
     * 微信认证
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WechatAuthenticationToken authenticationToken = (WechatAuthenticationToken) authentication;
        String code = (String) authenticationToken.getPrincipal();
        WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
        String openid = sessionInfo.getOpenid();
        Result<UserAuthDTO> memberAuthResult = userFeignClient.loadUserByOpenId(openid);
        log.info("微信用户认证：openid:{} : {}",openid,memberAuthResult);
        // 微信用户不存在，注册成为新会员
        if (memberAuthResult != null && ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {

            String sessionKey = sessionInfo.getSessionKey();
            String encryptedData = authenticationToken.getEncryptedData();
            String iv = authenticationToken.getIv();
            log.info("获取微信用户信息：sessionKey:{} encryptedData:{} iv:{}",sessionKey,encryptedData,iv);
            // 解密 encryptedData 获取用户信息
            WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
            log.info("获取微信用户信息：{}",userInfo);
            WxMaConfigHolder.remove();
            UserDTO userDTO = new UserDTO();
            BeanUtil.copyProperties(userInfo, userDTO);
            userDTO.setSourceType(SourceEnum.WECHAT);
            userDTO.setOpenid(openid);
            log.info("新增微信用户：{}",userDTO);
            userFeignClient.addUser(userDTO);
        }
        SysUserDetails userDetails = (SysUserDetails) ((SysUserDetailsServiceImpl) userDetailsService).loadUserByOpenId(openid);
        userDetails.setSessionKey(sessionInfo.getSessionKey());
        userDetails.setOpenId(openid);

        log.info("微信用户认证信息: {}",userDetails);

        WechatAuthenticationToken result = new WechatAuthenticationToken(userDetails, userDetails.getAuthorities());
        result.setDetails(authentication.getDetails());
        log.info("微信用户凭证信息: {}",result);
        return result;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return WechatAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
