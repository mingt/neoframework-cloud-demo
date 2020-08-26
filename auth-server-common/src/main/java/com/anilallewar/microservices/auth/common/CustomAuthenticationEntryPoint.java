
package com.anilallewar.microservices.auth.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoframework.common.api.ApiErrorConstant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Created on 2018/5/24 0024.
 *
 * @author zlf
 * @email i@merryyou.cn
 * @since 1.0
 *
 *        ahming notes: TODO：目前未用
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws ServletException {

        Map map = new HashMap();

        map.put("status", 400);
        map.put("error", "400"); // TODO: 待使用更好的 error
        map.put("error_description", authException.getMessage());
        map.put("path", request.getServletPath());
        map.put("timestamp", new Date().getTime());
        map.put("bizStatus", ApiErrorConstant.CommonError.OAUTH_ERROR);
        map.put("message", ApiErrorConstant.CommonError.OAUTH_ERROR_MSG);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), map);
        } catch (Exception e) {
            throw new ServletException();
        }
    }
}
