
package com.anilallewar.microservices.auth.integration.authenticator;

import com.anilallewar.microservices.auth.integration.IntegrationAuthentication;
import com.thinkgem.jeesite.modules.sys.entity.User;
import javax.servlet.ServletRequest;

/**
 * 集成认证 interface 接口.
 *
 * @author LIQIU
 * @since 2018-3-31
 */
public interface IntegrationAuthenticator {

    /**
     * 处理集成认证
     *
     * @param integrationAuthentication the integration authentication
     * @return user
     */
    User authenticate(IntegrationAuthentication integrationAuthentication);

    /**
     * 进行预处理
     *
     * @param integrationAuthentication the integration authentication
     * @param servletRequest the servlet request
     */
    void prepare(IntegrationAuthentication integrationAuthentication, ServletRequest servletRequest);

    /**
     * 判断是否支持集成认证类型
     *
     * @param integrationAuthentication the integration authentication
     * @return boolean
     */
    boolean support(IntegrationAuthentication integrationAuthentication);

    /**
     * 认证结束后执行
     *
     * @param integrationAuthentication the integration authentication
     */
    void complete(IntegrationAuthentication integrationAuthentication);

}
