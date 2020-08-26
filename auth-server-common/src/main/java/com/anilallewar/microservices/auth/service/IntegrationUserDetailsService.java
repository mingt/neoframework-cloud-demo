
package com.anilallewar.microservices.auth.service;

import com.anilallewar.microservices.auth.dao.UserDao;
import com.anilallewar.microservices.auth.integration.IntegrationAuthentication;
import com.anilallewar.microservices.auth.integration.IntegrationAuthenticationContext;
import com.anilallewar.microservices.auth.integration.authenticator.IntegrationAuthenticator;
import com.neoframework.common.auth.model.CustomUserDetails;
import com.thinkgem.jeesite.modules.sys.entity.User;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 集成认证用户服务
 *
 * @author LIQIU
 * @since 2018-3-7
 *
 * @author integrated and extended by ahming
 **/
@Service
public class IntegrationUserDetailsService implements UserDetailsService {

    // @Autowired
    // private SysAuthorizeClient sysAuthorizeClient;

    private Logger logger = LoggerFactory.getLogger(IntegrationUserDetailsService.class);

    @Autowired
    private UserDao userDao;
    // @Autowired
    // RedisUtils redisUtils;

    private List<IntegrationAuthenticator> authenticators;

    @Autowired(required = false)
    public void setIntegrationAuthenticators(List<IntegrationAuthenticator> authenticators) {
        this.authenticators = authenticators;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        IntegrationAuthentication integrationAuthentication = IntegrationAuthenticationContext.get();
        // 判断是否是集成登录
        if (integrationAuthentication == null) {
            integrationAuthentication = new IntegrationAuthentication();
        }
        integrationAuthentication.setUsername(username);
        // SysUserAuthentication sysUserAuthentication = this.authenticate(integrationAuthentication);
        User user = this.authenticate(integrationAuthentication);

        // TODO: 处理当 {@link com.anilallewar.microservices.auth.integration.authenticator.UsernamePasswordAuthenticator}
        // 返回 exceptionUser 的情况
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // List<Role> roles = findUserRoles(user);
        // user.setRoleList(roles);

        // // CustomUserDetails customUserDetails = new CustomUserDetails();
        // // BeanUtils.copyProperties(user, customUserDetails);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // this.setAuthorize(customUserDetails);

        return customUserDetails;

    }

    // /**
    // * 设置授权信息
    // *
    // * 相当于 {@link #findUserRoles}
    // *
    // * @ param user
    // */
    // public void setAuthorize(User user) {
    // Authorize authorize = this.sysAuthorizeClient.getAuthorize(user.getId());
    // user.setRoles(authorize.getRoles());
    // user.setResources(authorize.getResources());
    // }

    // /**
    // * 获取用户角色，设置授权信息.
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

    private User authenticate(IntegrationAuthentication integrationAuthentication) {
        if (this.authenticators != null) {
            for (IntegrationAuthenticator authenticator : authenticators) {
                if (authenticator.support(integrationAuthentication)) {
                    return authenticator.authenticate(integrationAuthentication);
                }
            }
        }
        return null;
    }

    /**
     * 如果账号未激活则激活此账号，更新用户的登录时间
     *
     * @param user the user
     * @param username the username
     */
    public void activeAccount(User user, String username) {
        logger.info("IntegrationUserDetailsService active account in");
        // Assert.notNull(user, "user must be not null");

        // TODO: 其他更新 action

        // logger.info("IntegrationUserDetailsService complete active account");
    }
}
