
package com.anilallewar.microservices.auth.integration;

import java.util.Map;

/**
 * The type Integration authentication.
 *
 * @author LIQIU
 * @since 2018 -3-30
 */
// @Data
public class IntegrationAuthentication {

    /**
     * {@link IntegrationAuthenticationFilter} 中的 AUTH_TYPE_PARM_NAME , 两者相同 , 因为前者为 private , 且放这里更好
     */
    public static final String AUTH_TYPE_PARM_NAME = "auth_type";

    private String authType;
    private String username;
    private String client; // 例如 xxxxweb xxxxios xxxxandroid 等等
    private String machineIdHash; // 因为machineId长度参差较大，做一次hash
    private Map<String, String[]> authParameters;

    /**
     * Gets auth parameter.
     *
     * @param parameter the parameter
     * @return the auth parameter
     */
    public String getAuthParameter(String parameter) {
        String[] values = this.authParameters.get(parameter);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    /**
     * Gets auth type.
     *
     * @return the auth type
     */
    public String getAuthType() {
        return authType;
    }

    /**
     * Sets auth type.
     *
     * @param authType the auth type
     */
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets client.
     *
     * @return the client
     */
    public String getClient() {
        return client;
    }

    /**
     * Sets client.
     *
     * @param client the client
     */
    public void setClient(String client) {
        this.client = client;
    }

    /**
     * Gets machine id hash.
     *
     * @return the machine id hash
     */
    public String getMachineIdHash() {
        return machineIdHash;
    }

    /**
     * Sets machine id hash.
     *
     * @param machineIdHash the machine id hash
     */
    public void setMachineIdHash(String machineIdHash) {
        this.machineIdHash = machineIdHash;
    }

    /**
     * Gets auth parameters.
     *
     * @return the auth parameters
     */
    public Map<String, String[]> getAuthParameters() {
        return authParameters;
    }

    /**
     * Sets auth parameters.
     *
     * @param authParameters the auth parameters
     */
    public void setAuthParameters(Map<String, String[]> authParameters) {
        this.authParameters = authParameters;
    }
}
