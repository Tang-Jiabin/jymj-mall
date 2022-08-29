package com.jymj.mall.oauth.security.extension.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.bean.BeanUtil;
import com.jymj.mall.common.result.Result;
import com.jymj.mall.common.result.ResultCode;
import com.jymj.mall.oauth.security.core.userdetails.user.SysUserDetailsServiceImpl;
import com.jymj.mall.user.api.UserFeignClient;
import com.jymj.mall.user.dto.UserAuthDTO;
import com.jymj.mall.user.dto.UserDTO;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.HashSet;


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

        WxMaJscode2SessionResult sessionInfo =  wxMaService.getUserService().getSessionInfo(code);
        String openid = sessionInfo.getOpenid();
        Result<UserAuthDTO> memberAuthResult = userFeignClient.loadUserByOpenId(openid);
        // 微信用户不存在，注册成为新会员
        if (memberAuthResult != null && ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {

            String sessionKey = sessionInfo.getSessionKey();
            String encryptedData = authenticationToken.getEncryptedData();
            String iv = authenticationToken.getIv();
            // 解密 encryptedData 获取用户信息
            WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

            UserDTO userDTO = new UserDTO();
            BeanUtil.copyProperties(userInfo, userDTO);
            userDTO.setOpenid(openid);
            userFeignClient.addUser(userDTO);
        }
        UserDetails userDetails = ((SysUserDetailsServiceImpl) userDetailsService).loadUserByOpenId(openid);
        WechatAuthenticationToken result = new WechatAuthenticationToken(userDetails, new HashSet<>());
        result.setDetails(authentication.getDetails());
        return result;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return WechatAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
