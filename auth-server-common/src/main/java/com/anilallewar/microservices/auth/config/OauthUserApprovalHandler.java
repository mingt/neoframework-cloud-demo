
package com.anilallewar.microservices.auth.config;

// import com.monkeyk.sos.domain.oauth.OauthClientDetails;
// import com.monkeyk.sos.service.OauthService;

import com.anilallewar.microservices.auth.model.OauthClientDetails;
import com.anilallewar.microservices.auth.service.OauthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;

/**
 * The type Oauth user approval handler.
 *
 * @author Shengzhao Li
 */
public class OauthUserApprovalHandler extends TokenStoreUserApprovalHandler {

    private OauthService oauthService;

    /**
     * Instantiates a new Oauth user approval handler.
     */
    public OauthUserApprovalHandler() {}

    /**
     * Whether is approved.
     *
     * @param authorizationRequest AuthorizationRequest
     * @param userAuthentication Authentication
     * @return
     */
    public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
        if (super.isApproved(authorizationRequest, userAuthentication)) {
            return true;
        }
        if (!userAuthentication.isAuthenticated()) {
            return false;
        }

        OauthClientDetails clientDetails = oauthService.loadOauthClientDetails(authorizationRequest.getClientId());
        return clientDetails != null && clientDetails.trusted();

    }

    /**
     * Sets oauth service.
     *
     * @param oauthService the oauth service
     */
    public void setOauthService(OauthService oauthService) {
        this.oauthService = oauthService;
    }
}
