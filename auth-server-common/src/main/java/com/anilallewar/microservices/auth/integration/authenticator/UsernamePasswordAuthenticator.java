
package com.anilallewar.microservices.auth.integration.authenticator;

import com.anilallewar.microservices.auth.common.code.FilterIgnorePropertiesConfig;
import com.anilallewar.microservices.auth.common.code.SecurityConstants;
import com.anilallewar.microservices.auth.common.code.WebUtils;
import com.anilallewar.microservices.auth.config.OAuthWebFormConfiguration;
import com.anilallewar.microservices.auth.dao.UserDao;
import com.anilallewar.microservices.auth.integration.IntegrationAuthentication;
import com.anilallewar.microservices.auth.service.impl.UserServiceImpl;
import com.neoframework.common.utils.RegexUtils;
import com.thinkgem.jeesite.modules.sys.entity.User;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;

/**
 * 默认登录处理
 *
 * @author LIQIU
 * @since 2018-3-31
 */
@Component
@Primary
public class UsernamePasswordAuthenticator extends AbstractPreparableIntegrationAuthenticator {

    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(UsernamePasswordAuthenticator.class);

    // @Autowired
    // private SysUserClient sysUserClient;
    @Autowired
    private UserDao userDao;

    @Autowired
    private FilterIgnorePropertiesConfig filterIgnorePropertiesConfig;

    // @Autowired
    // private TcConfigService configService;
    //
    // @Autowired
    // private RedisLimitUtils redisLimitUtils;

    @Override
    public User authenticate(IntegrationAuthentication integrationAuthentication) {
        // User sysUserAuthentication = sysUserClient.findUserByUsername(integrationAuthentication.getUsername());
        User user = loadUserByUsername(integrationAuthentication.getUsername(), integrationAuthentication);
        return user;
    }

    /**
     * ahming 20191202 处理验证码相关.
     *
     * <p>情况： 1. 之前都不需要验证码，要兼容 2. 添加 FilterIgnorePropertiesConfig 把之前的都加上到里面
     * 3. 后面新的client者要求验证码</p>
     *
     * @param integrationAuthentication 集成认证器信息
     * @param servletRequest 请求信息
     */
    @Override
    public void prepare(IntegrationAuthentication integrationAuthentication, ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // 判断client是否在验证码白名单里面，如果是，也直接返回，允许继续
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

        throw new OAuth2Exception("缺少验证码"); // 缺省默认都需要验证码
    }

    /**
     * TODO: 为了避免可能出现不存在的 auth_type , 这里可考虑添加白名单判断
     *
     * @param integrationAuthentication 集成认证器信息
     * @return
     */
    @Override
    public boolean support(IntegrationAuthentication integrationAuthentication) {
        return StringUtils.isEmpty(integrationAuthentication.getAuthType());
    }

    /**
     * 从数据库查询用户.
     *
     * <p>源自于 {@link UserServiceImpl#loadUserByUsername(java.lang.String)}
     *
     * 用户名，博学号，邮箱，手机登录策略：
     * 先判断格式，邮箱（精确）/手机/博学号（），
     * （1）邮箱格式，直接使用邮箱
     * （2）手机格式，先按手机，再按用户名
     * （3）博学号，先按博学号，再按用户名
     * （4）最后按用户名，若还未匹配，则帐号无效
     *
     * 同时，为了准确性，在注册时也应该按情况处理一下。
     * user.api.UserController#register()
     *
     * See also: 密码判断见下面配置
     * {@link OAuthWebFormConfiguration.LoginConfig}</p>
     *
     * @param username 用户名
     * @return 用户信息
     * @throws UsernameNotFoundException 查询不到用户异常
     */
    private User loadUserByUsername(String username, IntegrationAuthentication integrationAuthentication)
            throws UsernameNotFoundException {
        // User user = userRepository.findByUsername(username);

        User queryInfo = new User();
        User user = null;

        if (RegexUtils.isMobile(username)) {
            // logger.info("login try as mobile");
            queryInfo.setMobile(username);
            user = userDao.getByMobileSimple(queryInfo);
            if (user == null) {
                throw new UsernameNotFoundException("Not found any user for username[" + username + "]");
            } else if (user.isDeleted()) {
                throw new UsernameNotFoundException("User for username[" + username + "] deleted");
            }
        }

        if (user == null) {
            // logger.info("login try as username");
            queryInfo.setLoginName(username);
            user = userDao.getByLoginNameSimple(queryInfo);
            if (user == null || user.isDeleted()) { // user.archived()
                throw new UsernameNotFoundException("Not found any user for username[" + username + "] or deleted");
            }
        }

        // return new CustomUserDetails(user);
        return user;
    }

    // /**
    // * 获取用户角色. --> 这部分是各种登录方式通用的，所以移到 {@link IntegrationUserDetailsService} 去
    // *
    // * @param user
    // * @return
    // */
    // public List<Role> findUserRoles(User user) {
    //
    // try {
    // return userDao.findUserRoles(user);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    //
    // return null;
    // }
}
