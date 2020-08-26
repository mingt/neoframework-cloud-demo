
package com.anilallewar.microservices.auth.integration.authenticator;

import com.anilallewar.microservices.auth.common.code.CodeConstants;
import com.anilallewar.microservices.auth.common.code.FilterIgnorePropertiesConfig;
import com.anilallewar.microservices.auth.common.code.SecurityConstants;
import com.anilallewar.microservices.auth.common.code.WebUtils;
import com.anilallewar.microservices.auth.integration.IntegrationAuthentication;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;

/**
 * 集成验证码认证
 *
 * <p>TODO: 未完成.
 *
 * 20191201 整合 pigx-ui 时完善。目前使用 redis 等调整</p>
 *
 * @author LIQIU
 * @since 2018-3-31
 */
@Component
public class VerificationCodeIntegrationAuthenticator extends UsernamePasswordAuthenticator {

    private static final String VERIFICATION_CODE_AUTH_TYPE = "vc";

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FilterIgnorePropertiesConfig filterIgnorePropertiesConfig;

    // @Autowired
    // private VerificationCodeClient verificationCodeClient;

    @Override
    public void prepare(IntegrationAuthentication integrationAuthentication, ServletRequest servletRequest) {
        // String vcToken = integrationAuthentication.getAuthParameter("vc_token");
        // String vcCode = integrationAuthentication.getAuthParameter("vc_code");
        //
        // //验证验证码
        //// Result<Boolean> result = verificationCodeClient.validate(vcToken, vcCode, null);
        //// if (!result.getData()) {
        // throw new OAuth2Exception("验证码错误");
        //// }

        // ahming notes: 下面一段应该移到 UsernamePasswordAuthenticator 去，统一处理所以与 UsernamePasswordAuthenticator 相关的
        // --> 仍然需要，两处同时需要，对应不同的作用
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String client = integrationAuthentication.getClient();
        if (StringUtils.isBlank(client)) {
            String[] clientInfos; // = new String[0];
            clientInfos = WebUtils.getClientId(request);
            client = clientInfos[0];
        }
        if (filterIgnorePropertiesConfig.getClients().contains(client)) {
            // return chain.filter(exchange);
            return;
        }

        // 刷新token，也不需要验证码
        String grantType = request.getParameter("grant_type");
        if (StringUtils.equals(SecurityConstants.REFRESH_TOKEN, grantType)) {
            return;
        }

        // // 本地环境也跳过 // ahming notes: TODO： 待确认
        // if (ElearningUtil.isLocalEnvironment()) {
        // return;
        // }

        String code = integrationAuthentication.getAuthParameter("code");
        String randomStr = integrationAuthentication.getAuthParameter("randomStr");
        String mobile = integrationAuthentication.getAuthParameter("mobile");

        checkCode(code, randomStr, mobile);
    }

    @Override
    public boolean support(IntegrationAuthentication integrationAuthentication) {
        return VERIFICATION_CODE_AUTH_TYPE.equals(integrationAuthentication.getAuthType());
    }

    /**
     * 检查code
     *
     * @param code 验证码
     * @param randomStr 随机串
     * @param mobile 手机号
     */
    // @SneakyThrows
    private void checkCode(String code, String randomStr, String mobile) { // ServerHttpRequest request
        // // String code = request.getQueryParams().getFirst("code");
        // String code = request.getParameter("code");

        if (StringUtils.isBlank(code)) {
            throw new OAuth2Exception("验证码不能为空"); // ValidateCodeException
        }

        // // String randomStr = request.getQueryParams().getFirst("randomStr");
        // String randomStr = request.getParameter("randomStr");

        // https://gitee.com/log4j/pig/issues/IWA0D
        // // String mobile = request.getQueryParams().getFirst("mobile");
        // String mobile = request.getParameter("mobile");
        if (StringUtils.isNotBlank(mobile)) {
            randomStr = mobile;
        }

        String key = CodeConstants.DEFAULT_CODE_KEY + randomStr;
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        if (!redisTemplate.hasKey(key)) {
            throw new OAuth2Exception("验证码不合法");
        }

        Object codeObj = redisTemplate.opsForValue().get(key);

        if (codeObj == null) {
            throw new OAuth2Exception("验证码不合法");
        }

        String saveCode = codeObj.toString();
        if (StringUtils.isBlank(saveCode)) {
            redisTemplate.delete(key);
            throw new OAuth2Exception("验证码不合法");
        }

        if (!StringUtils.equals(saveCode, code)) {
            redisTemplate.delete(key);
            throw new OAuth2Exception("验证码不合法");
        }

        // 验证成功后删除缓存信息等
        redisTemplate.delete(key);
    }
}
