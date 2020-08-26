
package com.anilallewar.microservices.auth.common;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

/**
 * Created on 2018/5/24 0024.
 *
 * @author zlf
 * @email i@merryyou.cn
 * @since 1.0
 *
 * @author imported by ahming
 */
@Component("customWebResponseExceptionTranslator")
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {

        if (e instanceof OAuth2Exception) {
            OAuth2Exception oAuth2Exception = (OAuth2Exception) e;
            return ResponseEntity.status(oAuth2Exception.getHttpErrorCode())
                    .body((OAuth2Exception) (new CustomOauthException(oAuth2Exception.getMessage(),
                            oAuth2Exception.getOAuth2ErrorCode())));
        } else {
            throw e; // TODO：待确认，如果不是期望的异常，是不是继续抛出 --> 基本可行，在gatling压测验证了
        }
    }
}
