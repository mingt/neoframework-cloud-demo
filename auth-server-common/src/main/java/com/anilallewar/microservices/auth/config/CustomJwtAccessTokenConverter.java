
package com.anilallewar.microservices.auth.config;

import com.anilallewar.microservices.auth.common.CacheConstants;
import com.anilallewar.microservices.auth.common.code.SecurityConstants;
import com.anilallewar.microservices.auth.service.impl.UserServiceImpl;
import com.neoframework.common.auth.model.CustomUserDetails;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.User;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * The type Custom jwt access token converter.
 *
 * @author igylove
 * @create 2018 -08-02 18:43
 */
public class CustomJwtAccessTokenConverter extends JwtAccessTokenConverter {

    @Autowired
    private UserServiceImpl userService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        Map<String, Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
        Map<String, String> requestParameters = authentication.getOAuth2Request().getRequestParameters();
        // info.putAll(requestParameters);//所有信息都保存到token里
        if (null != requestParameters) { // 只保存机器码到token里
            String machineId = requestParameters.get(CacheConstants.MACHINE_ID);
            info.put(CacheConstants.MACHINE_ID, machineId);
        }

        // 加入 pigx 需要的字段
        Object userDetails = authentication.getUserAuthentication().getPrincipal();
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails cud = (CustomUserDetails) userDetails;
            User oldUser = cud.getUser();
            if (oldUser.getUid() == null || StringUtils.isBlank(oldUser.getLoginName()) || oldUser.getCompany() == null
                    || oldUser.getOffice().getOid() == null) {
                oldUser = userService.getUser(oldUser.getId());
            }
            if (oldUser != null) {
                info.put(SecurityConstants.DETAILS_USER_ID, oldUser.getUid());
                info.put(SecurityConstants.DETAILS_USERNAME, oldUser.getLoginName());

                // TODO: 添加部门 ID
                // info.put(SecurityConstants.DETAILS_DEPT_ID, oldUser.getDeptId());
                // // if (oldUser.getOffice() != null) {
                // // info.put(SecurityConstants.DETAILS_DEPT_ID, oldUser.getOffice().getOid());
                // // }
            }
        }
        info.put(SecurityConstants.DETAILS_TENANT_ID, 1);
        info.put(SecurityConstants.ACTIVE, Boolean.TRUE);

        DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
        customAccessToken.setAdditionalInformation(info);
        return super.enhance(customAccessToken, authentication);
    }
}
