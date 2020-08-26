/*
 * Copyright (c) 2019. Project: Elearning.
 *
 * All rights reserved.
 */

package com.anilallewar.microservices.auth.api;

import com.anilallewar.microservices.auth.common.code.CodeConstants;
import com.anilallewar.microservices.auth.common.code.SecurityConstants;
import com.google.code.kaptcha.Producer;
import com.neoframework.common.api.ApiErrorConstant;
import com.neoframework.common.api.exception.ParameterErrorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码相关.
 *
 * @author ahming
 */
@RestController
public class CodeController {

    private static final Logger logger = LoggerFactory.getLogger(CodeController.class);

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private Producer producer;

    /**
     * 输出验证码.
     *
     * @param request the request
     * @param response the response
     * @throws IOException the io exception
     */
    @RequestMapping("/code")
    public void code(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 保存验证码信息
        String randomStr = request.getParameter("randomStr"); // serverRequest.queryParam("randomStr").get();
        if (StringUtils.isBlank(randomStr)) {
            throw new ParameterErrorException(ApiErrorConstant.CommonError.PARAMETER_INVALID,
                    ApiErrorConstant.CommonError.PARAMETER_INVALID_MSG);
        }

        // 生成验证码
        String text = producer.createText();

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.opsForValue().set(CodeConstants.DEFAULT_CODE_KEY + randomStr, text, SecurityConstants.CODE_TIME,
            TimeUnit.SECONDS);

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        BufferedImage image = producer.createImage(text);
        ImageIO.write(image, "jpeg", response.getOutputStream());
    }

}
