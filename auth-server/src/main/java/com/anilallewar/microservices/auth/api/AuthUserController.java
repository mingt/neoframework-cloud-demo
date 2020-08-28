
package com.anilallewar.microservices.auth.api;

import com.anilallewar.microservices.auth.config.ResourceServerConfiguration;
import com.anilallewar.microservices.auth.integration.IntegrationAuthentication;
import com.anilallewar.microservices.auth.integration.authenticator.QrCodeIntegrationAuthenticator;
import com.anilallewar.microservices.auth.service.OauthService;
import com.anilallewar.microservices.auth.service.QrCodeService;
import com.anilallewar.microservices.auth.service.impl.UserServiceImpl;
import com.neoframework.common.api.ApiErrorConstant;
import com.neoframework.common.api.R;
import com.neoframework.common.api.exception.BadRequestException;
import com.neoframework.common.api.exception.InternalErrorException;
import com.neoframework.common.auth.model.CustomUserDetails;
import com.neoframework.common.auth.model.QrCode;
import com.neoframework.common.auth.model.QrCodeInfo;
import com.neoframework.common.auth.model.TokenVo;
import com.thinkgem.jeesite.common.utils.IdGen;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.User;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 认证模块 api 接口及扫码登录接口等.
 *
 * <p>REST endpoint to be used by other micro-services using SSO to validate the
 * authentication of the logged in user.</p>
 *
 * <p>Since the "me" endpoint needs to be protected to be accessed only after the
 * OAuth2 authentication is successful; the OAuth2 server also becomes a
 * resource server.</p>
 *
 * @author anilallewar
 */
@RestController
public class AuthUserController {

    private static final Logger logger = LoggerFactory.getLogger(AuthUserController.class);

    @Autowired
    private TokenStore tokenStore;

    /** 二维码信息过期时间 */
    public static final Integer QR_CODE_EXPIRED_MINUTES = 2;

    @Autowired
    private OauthService oauthService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private QrCodeService qrCodeService;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Return the principal identifying the logged in user
     *
     * @param user the user
     * @return current logged in user
     */
    @RequestMapping("/me")
    // @ResponseBody
    public Principal getCurrentLoggedInUser(Principal user) {
        logger.info("user-auth: me in");
        // ahming marks: 在spring security验证过程中印象有一处为了安全,会把密码黑置空,但现在发现没做到.因此这里加上

        if (user instanceof OAuth2Authentication) {
            user = ((OAuth2Authentication) user).getUserAuthentication();
        }

        if (user instanceof UsernamePasswordAuthenticationToken) {
            logger.info("user-auth: me eraseCredentials..");
            Object userDetails = ((UsernamePasswordAuthenticationToken) user).getPrincipal();
            if (userDetails instanceof CustomUserDetails) {
                logger.info("user-auth: me erase userDetails");
                CustomUserDetails cud = (CustomUserDetails) userDetails;
                User oldUser = cud.getUser();
                User newUser = userService.getUser(oldUser.getId());
                cud.setUser(newUser);
                boolean auth = cud.initialAuthorities();
                if (!auth) {
                    throw new InternalErrorException(ApiErrorConstant.UserError.ROLE_CONFLITS_ERROR,
                            ApiErrorConstant.UserError.ROLE_CONFLITS_ERROR_MSG);
                }
                cud.clearPassword();
            }

        }

        return user;
    }

    /**
     * 转换 Principal 为具体的 CustomUserDetails .
     *
     * @param user Principal
     * @return 如果是本系统的 CustomUserDetails 类型，则返回，否则为 null
     */
    private CustomUserDetails getCurrentCustomUserDetails(Principal user) {
        if (user instanceof OAuth2Authentication) {
            user = ((OAuth2Authentication) user).getUserAuthentication();
        }

        if (user instanceof UsernamePasswordAuthenticationToken) {
            Object userDetails = ((UsernamePasswordAuthenticationToken) user).getPrincipal();
            if (userDetails instanceof CustomUserDetails) {
                return (CustomUserDetails) userDetails;
            }
        }

        return null;
    }

    /**
     * 退出token
     *
     * @param authHeader Authorization
     * @return the r
     */
    @DeleteMapping("/token/logout")
    public R logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        if (StringUtils.isBlank(authHeader)) {
            return R.ok(Boolean.FALSE, "退出失败，token 为空");
        }

        String tokenValue = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, StringUtils.EMPTY).trim();
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
        if (accessToken == null || StringUtils.isBlank(accessToken.getValue())) {
            return R.ok(Boolean.TRUE, "退出失败，token 无效");
        }

        // OAuth2Authentication auth2Authentication = tokenStore.readAuthentication(accessToken);
        // // 清空用户信息
        // cacheManager.getCache(CacheConstants.USER_DETAILS)
        // .evict(auth2Authentication.getName());

        // 清空access token
        tokenStore.removeAccessToken(accessToken);

        // 清空 refresh token
        OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
        tokenStore.removeRefreshToken(refreshToken);
        return R.ok(Boolean.TRUE);
    }

    // /**
    // * 测试获取全部 client. !!! 危险接口，仅为了演示所用，勿打开
    // *
    // * @return
    // */
    // @RequestMapping("/client/all")
    // // @ResponseBody
    // public List<OauthClientDetailsDto> getAllClients() {
    // List<OauthClientDetailsDto> result = oauthService.loadAllOauthClientDetailsDtos();
    // return null != result ? result : new ArrayList<OauthClientDetailsDto>(0);
    // }

    /**
     * 测试 QrCode .
     *
     * @param user the user
     * @param id the id
     * @return qr code
     */
    @RequestMapping("qrcodeTest")
    public QrCode getQrCode(Principal user, String id) {
        if (StringUtils.isBlank(id)) {
            id = "1";
        }
        QrCode result = qrCodeService.get(id);

        return result;
    }

    /**
     * 获取或刷新二维码信息接口. 不需要登录即可访问, see also {@link ResourceServerConfiguration}
     *
     * <p>TODO: 这个接口应该要明确控制请求频率, 基于过期刷新时间控制</p>
     *
     * @param request the request
     * @return 前端用户生成二维码的 URL 地址，有定时检查是否成功扫描的 URL 地址
     */
    @RequestMapping("qrCodeInfo")
    public QrCodeInfo qrCodeInfo(HttpServletRequest request) {
        QrCode qrCode = new QrCode();
        qrCode.setId(IdGen.uuid());
        qrCode.setCode(IdGen.uuid());
        Date now = new Date();
        Date expireDate = DateUtils.addMinutes(now, AuthUserController.QR_CODE_EXPIRED_MINUTES);
        qrCode.setCreateDate(now);
        qrCode.setExpiredDate(expireDate);
        if (qrCodeService.insert(qrCode) > 0) {

            // TODO: 应该考虑使用速度更快的 cache 缓存，之后在后面获取

            QrCodeInfo result = new QrCodeInfo();
            result.setExpiredMinute(AuthUserController.QR_CODE_EXPIRED_MINUTES);

            String baseUrl = getBaseUrl(request);
            result.setScanUrl(baseUrl + "/scan?uuid=" + qrCode.getId() + "&t=" + now.getTime());
            result.setCheckUrl(baseUrl + "/scanCheck?uuid=" + qrCode.getId());

            return result;
        } else {
            logger.error("二维码信息插入错误");
            throw new InternalErrorException(ApiErrorConstant.UserError.QR_CODE_INFO_ERROR,
                    ApiErrorConstant.UserError.QR_CODE_INFO_ERROR_MSG);
        }
    }

    /**
     * 获取当前请求的其他路径，带 context .
     *
     * @param request HttpServletRequest
     * @return 如果为默认80端口，则不显示端口；否则带上端口
     */
    private String getBaseUrl(HttpServletRequest request) {
        Integer port = request.getServerPort();

        // TODO: 以下暂时直接指定 https ，后面完善按 nginx 反向代理, springboot tomcat 相关配置再处理
        // String baseUrl = request.getScheme() + "://" + request.getServerName();
        String baseUrl = "https://" + request.getServerName();

        if (port == 80) {
            baseUrl += request.getServletContext().getContextPath();
        } else {
            baseUrl += ":" + port + request.getServletContext().getContextPath();
        }
        return baseUrl;
    }

    /**
     * scanUrl 对应的接口. 移动端登录用户访问.
     *
     * @param user 当前用户
     * @param uuid 二维码 uuid
     * @return the r
     */
    @RequestMapping("/scan")
    public R scan(Principal user, @RequestParam String uuid) {
        if (StringUtils.length(uuid) != 32) {
            throw new BadRequestException(ApiErrorConstant.UserError.QR_CODE_INFO_INVALID,
                    ApiErrorConstant.UserError.QR_CODE_INFO_INVALID_MSG);
        }

        Date now = new Date();
        QrCode qrCode = qrCodeService.get(uuid);
        if (qrCode == null) {
            throw new BadRequestException(ApiErrorConstant.UserError.QR_CODE_INFO_INVALID,
                    ApiErrorConstant.UserError.QR_CODE_INFO_INVALID_MSG);
        }
        if (qrCode.getScanned()) {
            // return new R(R.STATUS_OK, R.MSG_OK); // 避免重进入
            return R.ok(); // 避免重进入
        }
        if (now.after(qrCode.getExpiredDate())) {
            throw new BadRequestException(ApiErrorConstant.UserError.QR_CODE_INFO_EXPIRED,
                    ApiErrorConstant.UserError.QR_CODE_INFO_EXPIRED_MSG);
        }

        CustomUserDetails userDetails = getCurrentCustomUserDetails(user);
        if (null == userDetails) {
            // 提示前端重试 TODO: 优化提示
            throw new InternalErrorException(ApiErrorConstant.CommonError.INTERNAL_ERROR,
                    ApiErrorConstant.CommonError.INTERNAL_ERROR_MSG);
        }

        // 20180721 移动端增加登录确认按钮，所以下面更新到表要放到单独接口

        return R.ok(); // return new R(R.STATUS_OK, R.MSG_OK);
    }

    /**
     * 移动端扫描后，确认按钮的接口. 这一步基本是上一个 {@link #scan(Principal, String)} 的再确认.
     *
     * @param user 当前用户
     * @param uuid 二维码 uuid
     * @return the r
     */
    @RequestMapping("/scanConfirm")
    public R scanConfirm(Principal user, @RequestParam String uuid) {
        if (StringUtils.length(uuid) != 32) {
            throw new BadRequestException(ApiErrorConstant.UserError.QR_CODE_INFO_INVALID,
                    ApiErrorConstant.UserError.QR_CODE_INFO_INVALID_MSG);
        }

        Date now = new Date();
        QrCode qrCode = qrCodeService.get(uuid);
        if (qrCode == null) {
            throw new BadRequestException(ApiErrorConstant.UserError.QR_CODE_INFO_INVALID,
                    ApiErrorConstant.UserError.QR_CODE_INFO_INVALID_MSG);
        }
        if (qrCode.getScanned()) {
            return R.ok(); // return new R(R.STATUS_OK, R.MSG_OK); // 避免重进入
        }

        // 这一步仍然需要判断是否过期!!!
        if (now.after(qrCode.getExpiredDate())) {
            throw new BadRequestException(ApiErrorConstant.UserError.QR_CODE_INFO_EXPIRED,
                    ApiErrorConstant.UserError.QR_CODE_INFO_EXPIRED_MSG);
        }

        CustomUserDetails userDetails = getCurrentCustomUserDetails(user);
        if (null == userDetails) {
            // 提示前端重试 TODO: 优化提示
            throw new InternalErrorException(ApiErrorConstant.CommonError.INTERNAL_ERROR,
                    ApiErrorConstant.CommonError.INTERNAL_ERROR_MSG);
        }

        // 20180721 移动端增加登录确认按钮，所以下面更新到表要放到单独接口
        // TODO： 待确认：userDetails.getUser().getLoginName() or userDetails.getUsername?
        qrCode.setUsername(userDetails.getUser().getLoginName());
        qrCode.setScanned(true);
        if (qrCodeService.update(qrCode) < 0) { // 去掉 = 0
            logger.error("更新二维码信息插入错误");

            // 提示前端重试 TODO: 优化为更友好的提示
            throw new InternalErrorException(ApiErrorConstant.CommonError.INTERNAL_ERROR,
                    ApiErrorConstant.CommonError.INTERNAL_ERROR_MSG);
        }

        return R.ok(); // return new R(R.STATUS_OK, R.MSG_OK);
    }

    /**
     * checkUrl 对应的接口. 不需要登录即可访问, see also {@link ResourceServerConfiguration}
     *
     * @param uuid 二维码 uuid
     * @param request the request
     * @return 如果已扫描 ，返回 username 和 token，状态码 0，如果未扫描，返回状态码 1，如果之前 token 已获取过，返回状态 2
     */
    @RequestMapping("/scanCheck")
    @SuppressWarnings("unchecked")
    public R<Map<String, String>> scanCheck(@RequestParam String uuid, HttpServletRequest request) {

        // TODO: 下面获取 QrCode 应该使用速度更快的 cache 缓存

        Date now = new Date();
        QrCode qrCode = qrCodeService.get(uuid);
        if (qrCode == null) {
            // 提示前端 刷新二维码 再重试 TODO: 优化提示
            throw new BadRequestException(ApiErrorConstant.UserError.QR_CODE_INFO_INVALID,
                    ApiErrorConstant.UserError.QR_CODE_INFO_INVALID_MSG);
        }

        // 下面判断添加 username 判断，是为了在过期的时间边界上表现友好，即使 expired 已超出，但一旦已扫描，就数据后面的判断
        if (StringUtils.isBlank(qrCode.getUsername()) && now.after(qrCode.getExpiredDate())) {
            // throw new InternalErrorException(ApiErrorConstant.UserError.QR_CODE_INFO_EXPIRED,
            // ApiErrorConstant.UserError.QR_CODE_INFO_EXPIRED_MSG);

            return R.failed("已过期");
        }

        if (qrCode.getScanned()) {

            // TODO: 这里暂时更新到数据库的 code 字段作为已经成功扫描完毕且前端获取到 token ，尽量避免重入 -- ?为什么不可以重入?
            // 因为前端检测一般是2、3秒左右，请求较多 --> 但一般来说，前端一旦请求到 token 就会中止后续请求，已会避免重入
            // ----> 这些考虑只是未使用 Ehcache 或 Redis 前的临时考虑，到时 Ehcache 和 Redis 的设置过期即是原子操作，速度很快，
            // 就不需要这些考虑了

            Map<String, String> data = new HashMap<>();
            data.put("username", qrCode.getUsername());
            if (StringUtils.isBlank(qrCode.getToken())) {

                // TODO: 生成前端所用的 token , 目前可扫描登录的都为 WEB 端. 使用对应的 client id
                // 手工调用 oauth/token 方法，增加多一个识别参数

                // String baseUrl = getBaseUrl(request);

                MultiValueMap<String, String> headers = new LinkedMultiValueMap();

                // TODO: 应该单独一个 client 例如 elearningqrcode 。目前 client 管理采用直接数据表操作
                String auth = "elearingweb" + ":" + "mz6DF!^!bhi7P&^PbDUux@PVWg#jjGtM";
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
                String authHeader = "Basic " + new String(encodedAuth);
                headers.add("Authorization", authHeader);

                MultiValueMap param = new LinkedMultiValueMap();
                param.add("grant_type", "password");
                param.add("username", qrCode.getUsername());
                param.add("password", qrCode.getId());
                // 下面务必送出 auth_type 参数，在 IntegrationAuthentication 过程中应用
                param.add(IntegrationAuthentication.AUTH_TYPE_PARM_NAME,
                    QrCodeIntegrationAuthenticator.QR_CODE_AUTH_TYPE);
                param.add(QrCodeIntegrationAuthenticator.QR_CODE_SECURITY_CODE, qrCode.getCode());

                HttpEntity entity = new HttpEntity(param, headers);
                try {
                    ResponseEntity<TokenVo> peopleResponseEntity = restTemplate
                            .postForEntity("http://localhost:8899/userauth/oauth/token", entity, TokenVo.class);
                    TokenVo body = peopleResponseEntity.getBody();
                    if (null == body || StringUtils.isBlank(body.getAccessToken())) {
                        throw new Exception("二维码登录获取auth token出错了");
                    }
                    data.put("token", body.getAccessToken());
                    data.put("refresh_token", body.getRefreshToken());
                    data.put("expires_in", String.valueOf(body.getExpiresIn()));

                    qrCode.setToken(body.getAccessToken());
                    qrCodeService.updateToken(qrCode);

                } catch (Exception e) { // RestClientException
                    logger.error("二维码登录请求token错误", e);

                    // 提示前端重试 TODO: 优化提示
                    throw new InternalErrorException(ApiErrorConstant.UserError.QR_CODE_INFO_INVALID,
                            ApiErrorConstant.UserError.QR_CODE_INFO_INVALID_MSG);
                }

            } else {
                data.put("token", qrCode.getToken());
            }
            return R.ok(); // return new R(R.STATUS_OK, R.MSG_OK, data);
        } else {
            return R.failed("未扫描"); // return new R(1, "未扫描", null);
        }
    }

    /**
     * 测试 QrCode .
     *
     * @param user 用户信息
     * @param id 测试 ID
     * @return 测试信息 qr code 3
     */
    @RequestMapping("qrcodeTest3")
    public QrCode getQrCode3(Principal user, String id) {
        if (StringUtils.isBlank(id)) {
            id = "3";
        }
        QrCode result = qrCodeService.get(id);

        return result;
    }
}
