
package com.anilallewar.microservices.auth.model;

import com.anilallewar.microservices.auth.common.GuidGenerator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * The type Oauth client details dto.
 *
 * @author Shengzhao Li
 */
public class OauthClientDetailsDto implements Serializable {

    private static final long serialVersionUID = -1L;

    private String createTime;
    private boolean archived;

    private String clientId = GuidGenerator.generate();
    private String resourceIds;

    private String clientSecret = GuidGenerator.generateClientSecret();

    private String scope;

    private String authorizedGrantTypes;

    private String webServerRedirectUri;

    private String authorities;

    private Integer accessTokenValidity;

    private Integer refreshTokenValidity;

    // optional
    private String additionalInformation;

    private boolean trusted;

    /**
     * Instantiates a new Oauth client details dto.
     */
    public OauthClientDetailsDto() {}

    /**
     * Instantiates a new Oauth client details dto.
     *
     * @param clientDetails the client details
     */
    public OauthClientDetailsDto(OauthClientDetails clientDetails) {
        this.clientId = clientDetails.clientId();
        this.clientSecret = clientDetails.clientSecret();
        this.scope = clientDetails.scope();

        // com.monkeyk.sos.infrastructure.DateUtils;
        // DateUtils.toDateTime(clientDetails.createTime())
        if (null == clientDetails.createTime()) {
            // this.createTime = DateUtils.formatDateTime(new Date());
            this.createTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        } else {
            // this.createTime = DateUtils.formatDateTime(clientDetails.createTime());
            this.createTime = DateFormatUtils.format(clientDetails.createTime(), "yyyy-MM-dd HH:mm:ss");
        }
        this.archived = clientDetails.archived();
        this.resourceIds = clientDetails.resourceIds();

        this.webServerRedirectUri = clientDetails.webServerRedirectUri();
        this.authorities = clientDetails.authorities();
        this.accessTokenValidity = clientDetails.accessTokenValidity();

        this.refreshTokenValidity = clientDetails.refreshTokenValidity();
        this.additionalInformation = clientDetails.additionalInformation();
        this.trusted = clientDetails.trusted();

        this.authorizedGrantTypes = clientDetails.authorizedGrantTypes();
    }

    /**
     * Gets create time.
     *
     * @return the create time
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * Sets create time.
     *
     * @param createTime the create time
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * Is archived boolean.
     *
     * @return the boolean
     */
    public boolean isArchived() {
        return archived;
    }

    /**
     * Sets archived.
     *
     * @param archived the archived
     */
    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    /**
     * Gets client id.
     *
     * @return the client id
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets client id.
     *
     * @param clientId the client id
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets resource ids.
     *
     * @return the resource ids
     */
    public String getResourceIds() {
        return resourceIds;
    }

    /**
     * Sets resource ids.
     *
     * @param resourceIds the resource ids
     */
    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    /**
     * Gets client secret.
     *
     * @return the client secret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Sets client secret.
     *
     * @param clientSecret the client secret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * Gets scope.
     *
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Gets scope with blank.
     *
     * @return the scope with blank
     */
    public String getScopeWithBlank() {
        if (scope != null && scope.contains(",")) {
            return scope.replaceAll(",", " ");
        }
        return scope;
    }

    /**
     * Sets scope.
     *
     * @param scope the scope
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Gets authorized grant types.
     *
     * @return the authorized grant types
     */
    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    /**
     * Sets authorized grant types.
     *
     * @param authorizedGrantTypes the authorized grant types
     */
    public void setAuthorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
    }

    /**
     * Gets web server redirect uri.
     *
     * @return the web server redirect uri
     */
    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
    }

    /**
     * Sets web server redirect uri.
     *
     * @param webServerRedirectUri the web server redirect uri
     */
    public void setWebServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
    }

    /**
     * Gets authorities.
     *
     * @return the authorities
     */
    public String getAuthorities() {
        return authorities;
    }

    /**
     * Sets authorities.
     *
     * @param authorities the authorities
     */
    public void setAuthorities(String authorities) {
        this.authorities = authorities;
    }

    /**
     * Gets access token validity.
     *
     * @return the access token validity
     */
    public Integer getAccessTokenValidity() {
        return accessTokenValidity;
    }

    /**
     * Sets access token validity.
     *
     * @param accessTokenValidity the access token validity
     */
    public void setAccessTokenValidity(Integer accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }

    /**
     * Gets refresh token validity.
     *
     * @return the refresh token validity
     */
    public Integer getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    /**
     * Sets refresh token validity.
     *
     * @param refreshTokenValidity the refresh token validity
     */
    public void setRefreshTokenValidity(Integer refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }

    /**
     * Gets additional information.
     *
     * @return the additional information
     */
    public String getAdditionalInformation() {
        return additionalInformation;
    }

    /**
     * Sets additional information.
     *
     * @param additionalInformation the additional information
     */
    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    /**
     * Is trusted boolean.
     *
     * @return the boolean
     */
    public boolean isTrusted() {
        return trusted;
    }

    /**
     * Sets trusted.
     *
     * @param trusted the trusted
     */
    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
    }

    /**
     * To dtos list.
     *
     * @param clientDetailses the client detailses
     * @return the list
     */
    public static List<OauthClientDetailsDto> toDtos(List<OauthClientDetails> clientDetailses) {
        List<OauthClientDetailsDto> dtos = new ArrayList<>(clientDetailses.size());
        for (OauthClientDetails clientDetailse : clientDetailses) {
            dtos.add(new OauthClientDetailsDto(clientDetailse));
        }
        return dtos;
    }

    /**
     * Is contains authorization code boolean.
     *
     * @return the boolean
     */
    public boolean isContainsAuthorizationCode() {
        return this.authorizedGrantTypes.contains("authorization_code");
    }

    /**
     * Is contains password boolean.
     *
     * @return the boolean
     */
    public boolean isContainsPassword() {
        return this.authorizedGrantTypes.contains("password");
    }

    /**
     * Is contains implicit boolean.
     *
     * @return the boolean
     */
    public boolean isContainsImplicit() {
        return this.authorizedGrantTypes.contains("implicit");
    }

    /**
     * Is contains client credentials boolean.
     *
     * @return the boolean
     */
    public boolean isContainsClientCredentials() {
        return this.authorizedGrantTypes.contains("client_credentials");
    }

    /**
     * Is contains refresh token boolean.
     *
     * @return the boolean
     */
    public boolean isContainsRefreshToken() {
        return this.authorizedGrantTypes.contains("refresh_token");
    }

    /**
     * Create domain oauth client details.
     *
     * @return the oauth client details
     */
    public OauthClientDetails createDomain() {
        OauthClientDetails clientDetails = new OauthClientDetails().clientId(clientId).clientSecret(clientSecret)
                .resourceIds(resourceIds).authorizedGrantTypes(authorizedGrantTypes).scope(scope);

        if (StringUtils.isNotEmpty(webServerRedirectUri)) {
            clientDetails.webServerRedirectUri(webServerRedirectUri);
        }

        if (StringUtils.isNotEmpty(authorities)) {
            clientDetails.authorities(authorities);
        }

        clientDetails.accessTokenValidity(accessTokenValidity).refreshTokenValidity(refreshTokenValidity)
                .trusted(trusted);

        if (StringUtils.isNotEmpty(additionalInformation)) {
            clientDetails.additionalInformation(additionalInformation);
        }

        return clientDetails;
    }
}
