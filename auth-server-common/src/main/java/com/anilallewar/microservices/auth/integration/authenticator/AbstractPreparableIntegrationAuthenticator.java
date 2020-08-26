
package com.anilallewar.microservices.auth.integration.authenticator;

import com.anilallewar.microservices.auth.integration.IntegrationAuthentication;
import com.thinkgem.jeesite.modules.sys.entity.User;
import javax.servlet.ServletRequest;

/**
 * 集成认证基类.
 *
 * @author LIQIU
 * @since 2018-4-4
 **/
public abstract class AbstractPreparableIntegrationAuthenticator implements IntegrationAuthenticator {

    @Override
    public abstract User authenticate(IntegrationAuthentication integrationAuthentication);

    @Override
    public abstract void prepare(IntegrationAuthentication integrationAuthentication, ServletRequest servletRequest);

    @Override
    public abstract boolean support(IntegrationAuthentication integrationAuthentication);

    @Override
    public void complete(IntegrationAuthentication integrationAuthentication) {

    }
}
