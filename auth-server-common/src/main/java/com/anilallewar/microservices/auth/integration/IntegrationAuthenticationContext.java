
package com.anilallewar.microservices.auth.integration;

/**
 * The type Integration authentication context.
 *
 * @author LIQIU
 * @since 2018-3-30
 */
public class IntegrationAuthenticationContext {

    private static ThreadLocal<IntegrationAuthentication> holder = new ThreadLocal<>();

    /**
     * Set.
     *
     * @param integrationAuthentication the integration authentication
     */
    public static void set(IntegrationAuthentication integrationAuthentication) {
        holder.set(integrationAuthentication);
    }

    /**
     * Get integration authentication.
     *
     * @return the integration authentication
     */
    public static IntegrationAuthentication get() {
        return holder.get();
    }

    /**
     * Clear.
     */
    public static void clear() {
        holder.remove();
    }
}
