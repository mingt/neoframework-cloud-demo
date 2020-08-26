
package com.anilallewar.microservices.auth.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 定义OAuth中的 Client, 也称 ClientDetails
 *
 * @author Shengzhao Li
 */
public class OauthClientDetails implements Serializable {

    private static final long serialVersionUID = -1L;

    // private LocalDateTime createTime = DateUtils.now();
    private Date createTime;

    private boolean archived = false;

    private String clientId;
    private String resourceIds;

    private String clientSecret;

    /**
     * Available values: read,write
     */
    private String scope;

    /**
     * grant types include
     * "authorization_code", "password", "assertion", and "refresh_token".
     * Default value is "authorization_code,refresh_token".
     */
    private String authorizedGrantTypes = "authorization_code,refresh_token";

    /**
     * The re-direct URI(s) established during registration (optional, comma separated).
     */
    private String webServerRedirectUri;

    /**
     * Authorities that are granted to the client (comma-separated). Distinct from the authorities
     * granted to the user on behalf of whom the client is acting.
     * <p/>
     * For example: ROLE_USER
     */
    private String authorities;

    /**
     * The access token validity period in seconds (optional).
     * If unspecified a global default will be applied by the token services.
     */
    private Integer accessTokenValidity;

    /**
     * The refresh token validity period in seconds (optional).
     * If unspecified a global default will be applied by the token services.
     */
    private Integer refreshTokenValidity;

    // optional
    private String additionalInformation;

    /**
     * The client is trusted or not. If it is trust, will skip approve step
     * default false.
     */
    private boolean trusted = false;

    /**
     * Value is 'true' or 'false', default 'false'
     */
    private String autoApprove;

    /**
     * Instantiates a new Oauth client details.
     */
    public OauthClientDetails() {}

    /**
     * Auto approve string.
     *
     * @return the string
     */
    public String autoApprove() {
        return autoApprove;
    }

    /**
     * Auto approve oauth client details.
     *
     * @param autoApprove the auto approve
     * @return the oauth client details
     */
    public OauthClientDetails autoApprove(String autoApprove) {
        this.autoApprove = autoApprove;
        return this;
    }

    // CHECKSTYLE:OFF // 避免： 重载方法应写在一起，上一个重载方法位于第'107'行。 (290:0) [OverloadMethodsDeclarationOrder]

    /**
     * Trusted boolean.
     *
     * @return the boolean
     */
    public boolean trusted() {
        return trusted;
    }

    /**
     * Create time date.
     *
     * @return the date
     */
    public Date createTime() {
        return createTime;
    }

    /**
     * Create time oauth client details.
     *
     * @param createTime the create time
     * @return the oauth client details
     */
    public OauthClientDetails createTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    /**
     * Archived boolean.
     *
     * @return the boolean
     */
    public boolean archived() {
        return archived;
    }

    /**
     * Client id string.
     *
     * @return the string
     */
    public String clientId() {
        return clientId;
    }

    /**
     * Resource ids string.
     *
     * @return the string
     */
    public String resourceIds() {
        return resourceIds;
    }

    /**
     * Client secret string.
     *
     * @return the string
     */
    public String clientSecret() {
        return clientSecret;
    }

    /**
     * Scope string.
     *
     * @return the string
     */
    public String scope() {
        return scope;
    }

    /**
     * Authorized grant types string.
     *
     * @return the string
     */
    public String authorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    /**
     * Web server redirect uri string.
     *
     * @return the string
     */
    public String webServerRedirectUri() {
        return webServerRedirectUri;
    }

    /**
     * Authorities string.
     *
     * @return the string
     */
    public String authorities() {
        return authorities;
    }

    /**
     * Access token validity integer.
     *
     * @return the integer
     */
    public Integer accessTokenValidity() {
        return accessTokenValidity;
    }

    /**
     * Refresh token validity integer.
     *
     * @return the integer
     */
    public Integer refreshTokenValidity() {
        return refreshTokenValidity;
    }

    /**
     * Additional information string.
     *
     * @return the string
     */
    public String additionalInformation() {
        return additionalInformation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("OauthClientDetails");
        sb.append("{createTime=").append(createTime);
        sb.append(", archived=").append(archived);
        sb.append(", clientId='").append(clientId).append('\'');
        sb.append(", resourceIds='").append(resourceIds).append('\'');
        sb.append(", clientSecret='").append(clientSecret).append('\'');
        sb.append(", scope='").append(scope).append('\'');
        sb.append(", authorizedGrantTypes='").append(authorizedGrantTypes).append('\'');
        sb.append(", webServerRedirectUri='").append(webServerRedirectUri).append('\'');
        sb.append(", authorities='").append(authorities).append('\'');
        sb.append(", accessTokenValidity=").append(accessTokenValidity);
        sb.append(", refreshTokenValidity=").append(refreshTokenValidity);
        sb.append(", additionalInformation='").append(additionalInformation).append('\'');
        sb.append(", trusted=").append(trusted);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Client id oauth client details.
     *
     * @param clientId the client id
     * @return the oauth client details
     */
    public OauthClientDetails clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    /**
     * Client secret oauth client details.
     *
     * @param clientSecret the client secret
     * @return the oauth client details
     */
    public OauthClientDetails clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    /**
     * Resource ids oauth client details.
     *
     * @param resourceIds the resource ids
     * @return the oauth client details
     */
    public OauthClientDetails resourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
        return this;
    }

    /**
     * Authorized grant types oauth client details.
     *
     * @param authorizedGrantTypes the authorized grant types
     * @return the oauth client details
     */
    public OauthClientDetails authorizedGrantTypes(String authorizedGrantTypes) {
        this.authorizedGrantTypes = authorizedGrantTypes;
        return this;
    }

    /**
     * Scope oauth client details.
     *
     * @param scope the scope
     * @return the oauth client details
     */
    public OauthClientDetails scope(String scope) {
        this.scope = scope;
        return this;
    }

    /**
     * Web server redirect uri oauth client details.
     *
     * @param webServerRedirectUri the web server redirect uri
     * @return the oauth client details
     */
    public OauthClientDetails webServerRedirectUri(String webServerRedirectUri) {
        this.webServerRedirectUri = webServerRedirectUri;
        return this;
    }

    /**
     * Authorities oauth client details.
     *
     * @param authorities the authorities
     * @return the oauth client details
     */
    public OauthClientDetails authorities(String authorities) {
        this.authorities = authorities;
        return this;
    }

    /**
     * Access token validity oauth client details.
     *
     * @param accessTokenValidity the access token validity
     * @return the oauth client details
     */
    public OauthClientDetails accessTokenValidity(Integer accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
        return this;
    }

    /**
     * Refresh token validity oauth client details.
     *
     * @param refreshTokenValidity the refresh token validity
     * @return the oauth client details
     */
    public OauthClientDetails refreshTokenValidity(Integer refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
        return this;
    }

    /**
     * Trusted oauth client details.
     *
     * @param trusted the trusted
     * @return the oauth client details
     */
    public OauthClientDetails trusted(boolean trusted) {
        this.trusted = trusted;
        return this;
    }

    /**
     * Additional information oauth client details.
     *
     * @param additionalInformation the additional information
     * @return the oauth client details
     */
    public OauthClientDetails additionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
        return this;
    }

    /**
     * Archived oauth client details.
     *
     * @param archived the archived
     * @return the oauth client details
     */
    public OauthClientDetails archived(boolean archived) {
        this.archived = archived;
        return this;
    }

    // CHECKSTYLE:ON
}
