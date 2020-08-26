
package com.anilallewar.microservices.auth.config;

import com.anilallewar.microservices.auth.common.CacheConstants;
import com.anilallewar.microservices.auth.common.DuplicateLoginException;
import com.anilallewar.microservices.auth.service.CustomJdbcTokenStore;
import com.thinkgem.jeesite.common.utils.StringUtils;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 自定义tokenService，效验是否是同端多次登录
 *
 * @author igylove
 * @create 2018-08-03 9:10
 */
public class CustomTokenService extends DefaultTokenServices {

    private Logger logger = LoggerFactory.getLogger(CustomTokenService.class);

    private TokenStore customTokenStore;

    public TokenStore getCustomTokenStore() {
        return customTokenStore;
    }

    public void setCustomTokenStore(TokenStore customTokenStore) {
        this.customTokenStore = customTokenStore;
    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OAuth2AccessToken accessToken = super.createAccessToken(authentication);
        Map<String, String> requestParameters = authentication.getOAuth2Request().getRequestParameters();
        String machineId = requestParameters.get(CacheConstants.MACHINE_ID);
        Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
        if (null != additionalInformation) {
            boolean needRegenToken = false;
            Object storeMachineIdObject = additionalInformation.get(CacheConstants.MACHINE_ID);
            if (null != storeMachineIdObject) {
                String storeMachineId = String.valueOf(storeMachineIdObject);
                if (StringUtils.isNotBlank(storeMachineId) && !storeMachineId.equals(machineId)) {
                    needRegenToken = true;
                }
            } else if (StringUtils.isNotBlank(machineId)) {
                // 旧的 token 没有 machineId ，但现在请求 token 带有，这时也刷新
                needRegenToken = true;
            }
            if (needRegenToken) {
                /** 同客户端则其他设备登录则把所有的token删除，新增带新设备id的token **/
                customTokenStore.removeAccessToken(accessToken);
                accessToken = super.createAccessToken(authentication);
            }
        }
        return accessToken;
    }

    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest)
            throws AuthenticationException {
        if (null != customTokenStore) {
            clearInvalidRefreshToken();
            OAuth2RefreshToken refreshToken = customTokenStore.readRefreshToken(refreshTokenValue);
            if (refreshToken == null) {
                throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
            }

            OAuth2Authentication authentication = customTokenStore.readAuthenticationForRefreshToken(refreshToken);
            if (null == authentication) {
                throw new AuthenticationServiceException("load old authentication is null");
            }

            /** 使用旧的auth 信息获取当前的登录信息 **/
            OAuth2AccessToken accessToken = this.getAccessToken(authentication);
            if (null == accessToken) {
                logger.warn("use old authentication get new access token is null,maybe expired!!!");
            } else {
                /** 比对当前登录的设备码与旧的设备码 **/
                Map<String, Object> storeRequestParam = accessToken.getAdditionalInformation();
                Map<String, String> requestParameters = tokenRequest.getRequestParameters();
                if (null != requestParameters && null != storeRequestParam) {
                    Object storeRequestMachineObj = storeRequestParam.get(CacheConstants.MACHINE_ID);
                    if (null != storeRequestMachineObj) {
                        String storeRequestMachineId = String.valueOf(storeRequestMachineObj);
                        String requestMachineId = requestParameters.get(CacheConstants.MACHINE_ID);
                        if (StringUtils.isNotBlank(storeRequestMachineId)
                                && !storeRequestMachineId.equals(requestMachineId)) {
                            throw new DuplicateLoginException("duplicate login,cannot refresh token");
                        }
                    }
                }
            }
        }
        return super.refreshAccessToken(refreshTokenValue, tokenRequest);
    }

    /**
     * 清除oauth_refresh_token表的垃圾数据
     */
    public void clearInvalidRefreshToken() {
        if (customTokenStore instanceof CustomJdbcTokenStore) {
            List<OAuth2RefreshToken> invalidRefreshTokens =
                    ((CustomJdbcTokenStore) customTokenStore).getInvalidRefreshTokens();
            // if(!Collections3.isEmpty(invalidRefreshTokens)){
            if (invalidRefreshTokens != null && invalidRefreshTokens.size() > 0) {
                for (OAuth2RefreshToken refreshToken : invalidRefreshTokens) {
                    if (isExpired(refreshToken)) {
                        customTokenStore.removeRefreshToken(refreshToken);
                    }
                }
            }
        }
    }
}
