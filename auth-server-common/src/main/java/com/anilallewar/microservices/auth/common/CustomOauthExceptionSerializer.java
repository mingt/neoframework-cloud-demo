
package com.anilallewar.microservices.auth.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.neoframework.common.api.ApiErrorConstant;
import com.neoframework.common.api.ApiErrorConstant.CommonError;
import com.neoframework.common.api.ApiErrorConstant.UserError;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Created on 2018/5/24 0024.
 *
 * @author zlf
 * @email i@merryyou.cn
 * @since 1.0
 *
 * @author extended by ahming
 */
public class CustomOauthExceptionSerializer extends StdSerializer<CustomOauthException> {

    public CustomOauthExceptionSerializer() {
        super(CustomOauthException.class);
    }

    @Override
    public void serialize(CustomOauthException value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 先保存，后面按情况采用
        String message = value.getMessage();

        gen.writeStartObject();
        gen.writeNumberField("status", value.getHttpErrorCode());
        gen.writeStringField("error", value.getError());
        gen.writeStringField("error_description", message);
        gen.writeStringField("path", request.getServletPath());
        gen.writeNumberField("timestamp", new Date().getTime());
        if (value.getAdditionalInformation() != null) {
            for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                gen.writeStringField(key, add);
            }
        }

        if (OAuth2Exception.INVALID_GRANT.equals(value.getError())) {

            if (StringUtils.contains(message, "refresh token")) {
                gen.writeNumberField("bizStatus", CommonError.REFRESH_TOKEN_INVALID);
                gen.writeStringField("message", CommonError.REFRESH_TOKEN_INVALID_MSG);
            } else {
                // gen.writeStringField("error_description", message); // 不
                gen.writeNumberField("bizStatus", UserError.VALIDATE_ERROR);
                gen.writeStringField("message", UserError.VALIDATE_ERROR_MSG);
            }
        } else {
            // 其他，bizStatus统一为 oauth 错误
            gen.writeNumberField("bizStatus", ApiErrorConstant.CommonError.OAUTH_ERROR);
            gen.writeStringField("message", ApiErrorConstant.CommonError.OAUTH_ERROR_MSG);
        }

        gen.writeEndObject();
    }
}
