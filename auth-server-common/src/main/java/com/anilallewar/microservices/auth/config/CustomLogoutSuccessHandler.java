
package com.anilallewar.microservices.auth.config;

import com.neoframework.common.api.R;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

/**
 * 登出成功删除token
 *
 * @author igylove
 * @create 2018-08-04 10:30
 */
public class CustomLogoutSuccessHandler extends HttpStatusReturningLogoutSuccessHandler {

    ConsumerTokenServices tokenServices;

    public void setTokenServices(ConsumerTokenServices tokenServices) {
        this.tokenServices = tokenServices;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        super.onLogoutSuccess(request, response, authentication);

        /** 删除对应的token **/
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith(OAuth2AccessToken.BEARER_TYPE)) {
            String tokenValue = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, "").trim();
            tokenServices.revokeToken(tokenValue);
        }

        // SimpleResult result = new SimpleResult();
        // result.setStatus(HttpStatus.OK.value());
        // result.setMessage("logout success");
        // response.getWriter().write(JSON.toJSONString(result));
        response.getWriter().write(JsonMapper.toJsonString(R.ok()));
    }
}
