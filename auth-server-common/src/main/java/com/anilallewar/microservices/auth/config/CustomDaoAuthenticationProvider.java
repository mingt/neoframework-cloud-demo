
package com.anilallewar.microservices.auth.config;

import com.anilallewar.microservices.auth.integration.authenticator.QrCodeIntegrationAuthenticator;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by think on 2018-02-23.
 */
public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {

    Logger logger = LoggerFactory.getLogger(CustomDaoAuthenticationProvider.class);

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        try {

            // 如果这里是二维码登录，前端送回来的 password 只是二维码的uuid，所以要特别处理。TODO: 问题：这里还要不要检验uuid？
            // 而且，这种请求只能从后端发起，这点也可以作为安全保证的考虑点
            Object details = authentication.getDetails();
            if (details instanceof LinkedHashMap) {
                Object qr = ((LinkedHashMap) details).get("auth_type");
                if (QrCodeIntegrationAuthenticator.QR_CODE_AUTH_TYPE.equals(qr)) {
                    // Object username = ((LinkedHashMap) details).get("username"); // 同外面 principal
                    Object grantType = ((LinkedHashMap) details).get("grant_type");
                    Object sCode = ((LinkedHashMap) details).get(QrCodeIntegrationAuthenticator.QR_CODE_SECURITY_CODE);
                    Object password = authentication.getCredentials(); // authentication.getCredentials().toString()
                    // TODO: 安全性判断，添加更保证的判断，例如查询 uuid 信息
                    if ("password".equals(grantType) && sCode != null && sCode instanceof String
                            && ((String) sCode).length() == 32 && password != null && password instanceof String
                            && ((String) password).length() == 32) {
                        // 表示是扫码登录的后端调用申请 token 的请求，这里直接返回表示不再判断密码，继续后面的流程，直到返回 token
                        return;
                    }
                }
            }

            super.additionalAuthenticationChecks(userDetails, authentication);
        } catch (AuthenticationException e) {

            // 其他特殊处理
            // //if (e instanceof BadCredentialsException) {
            //
            // if (userDetails instanceof CustomUserDetails) {
            //
            // User user = ((CustomUserDetails) userDetails).getUser();
            // boolean isSuccess = false;
            // if (authentication.getCredentials() != null) {
            // isSuccess = PasswordHelper.checkPwd(authentication.getCredentials().toString(), user);
            // }
            // if(!isSuccess){
            // logger.debug("Authentication failed: password does not match stored value");
            //
            // throw new BadCredentialsException(
            // messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials",
            // "Bad credentials"));
            // }
            //
            // // DO more actions
            //
            // return;
            // }

            // 重要：其他类型帐号，不处理，但要正确返回原异常为信息
            throw new BadCredentialsException(messages.getMessage(
            "AbstractUserDetailsAuthenticationProvider.badCredentials",
            "Bad credentials"));

            // //}
        }
    }

}
