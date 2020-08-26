/*
 * Copyright (c) 2019. Project: Elearning.
 *
 * All rights reserved.
 */

package com.anilallewar.microservices.auth.common.code;

// import cn.hutool.core.codec.Base64;
// import cn.hutool.json.JSONUtil;

import com.neoframework.common.api.exception.BadRequestException;
import com.neoframework.common.api.exception.InternalErrorException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

/**
 * Miscellaneous utilities for web applications.
 *
 * @author L.cm
 */
// @Slf4j
// @UtilityClass
public class WebUtils extends org.springframework.web.util.WebUtils {

    private static final String BASIC_ = "Basic ";
    private static final String UNKNOWN = "unknown";

    /**
     * 判断是否ajax请求
     * spring ajax 返回含有 ResponseBody 或者 RestController注解
     *
     * @param handlerMethod HandlerMethod
     * @return 是否ajax请求 boolean
     */
    public static boolean isBody(HandlerMethod handlerMethod) {
        ResponseBody responseBody = ClassUtils.getAnnotation(handlerMethod, ResponseBody.class);
        return responseBody != null;
    }

    /**
     * 读取cookie
     *
     * @param name cookie name
     * @return cookie value
     */
    public static String getCookieVal(String name) {
        HttpServletRequest request = WebUtils.getRequest();
        Assert.notNull(request, "request from RequestContextHolder is null");
        return getCookieVal(request, name);
    }

    /**
     * 读取cookie
     *
     * @param request HttpServletRequest
     * @param name cookie name
     * @return cookie value
     */
    public static String getCookieVal(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 清除 某个指定的cookie
     *
     * @param response HttpServletResponse
     * @param key cookie key
     */
    public static void removeCookie(HttpServletResponse response, String key) {
        setCookie(response, key, null, 0);
    }

    /**
     * 设置cookie
     *
     * @param response HttpServletResponse
     * @param name cookie name
     * @param value cookie value
     * @param maxAgeInSeconds maxage
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    /**
     * 获取 HttpServletRequest
     *
     * @return {HttpServletRequest}
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取 HttpServletResponse
     *
     * @return {HttpServletResponse}
     */
    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    // /**
    // * 返回json
    // *
    // * @param response HttpServletResponse
    // * @param result 结果对象
    // */
    // public static void renderJson(HttpServletResponse response, Object result) {
    // renderJson(response, result, MediaType.APPLICATION_JSON_UTF8_VALUE);
    // }
    //
    // /**
    // * 返回json
    // *
    // * @param response HttpServletResponse
    // * @param result 结果对象
    // * @param contentType contentType
    // */
    // public static void renderJson(HttpServletResponse response, Object result, String contentType) {
    // response.setCharacterEncoding("UTF-8");
    // response.setContentType(contentType);
    // try (PrintWriter out = response.getWriter()) {
    // out.append(JSONUtil.toJsonStr(result));
    // } catch (IOException e) {
    // log.error(e.getMessage(), e);
    // }
    // }

    /**
     * 获取ip
     *
     * @return {String}
     */
    public static String getIP() {
        return getIP(WebUtils.getRequest());
    }

    /**
     * 获取ip
     *
     * @param request HttpServletRequest
     * @return {String}
     */
    public static String getIP(HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest is null");
        String ip = request.getHeader("X-Requested-For");
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return StringUtils.isBlank(ip) ? null : ip.split(",")[0];
    }

    /**
     * 从 ServerHttpRequest 获取CLIENT_ID
     *
     * @param request the request
     * @return string [ ]
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    // @SneakyThrows
    public static String[] getClientId(ServerHttpRequest request) throws UnsupportedEncodingException {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(BASIC_)) {
            throw new BadRequestException("请求头中client信息为空"); // CheckedException
        }
        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            // decoded = Base64.decode(base64Token); // import cn.hutool.core.codec.Base64;
            Base64.Decoder decoder = Base64.getDecoder(); // import elearning.api.common.util.Base64;
            decoded = decoder.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Failed to decode basic authentication token"); // CheckedException
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadRequestException("Invalid basic authentication token"); // CheckedException
        }
        return new String[] {token.substring(0, delim), token.substring(delim + 1)};
    }

    /**
     * 从 ServletRequest 获取CLIENT_ID
     *
     * @param request the request
     * @return string [ ]
     */
    // @SneakyThrows
    public static String[] getClientId(HttpServletRequest request) {
        // String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(BASIC_)) {
            throw new BadRequestException("请求头中client信息为空"); // CheckedException
        }
        byte[] base64Token = new byte[0];
        try {
            base64Token = header.substring(6).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new InternalErrorException("getClientId UnsupportedEncodingException");
        }
        byte[] decoded;
        try {
            // decoded = Base64.decode(base64Token); // import cn.hutool.core.codec.Base64;
            Base64.Decoder decoder = Base64.getDecoder(); // import elearning.api.common.util.Base64;
            decoded = decoder.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Failed to decode basic authentication token"); // CheckedException
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadRequestException("Invalid basic authentication token"); // CheckedException
        }
        return new String[] {token.substring(0, delim), token.substring(delim + 1)};
    }
}
