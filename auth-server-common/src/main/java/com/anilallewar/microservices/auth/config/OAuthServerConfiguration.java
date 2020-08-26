
package com.anilallewar.microservices.auth.config;

import com.anilallewar.microservices.auth.integration.IntegrationAuthenticationFilter;
import com.anilallewar.microservices.auth.service.IntegrationUserDetailsService;
import java.security.KeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * The Class defines the authorization server that would authenticate the user
 * and define the client that seeks authorization on the resource owner's
 * behalf.
 *
 * @author anilallewar
 */
@Configuration
@EnableAuthorizationServer
// extends AuthorizationServerConfigurerAdapter
public class OAuthServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebResponseExceptionTranslator customWebResponseExceptionTranslator;

    // @Autowired
    // private DataSource dataSource;
    //
    // @Autowired
    // private JdbcUserDetailsService jdbcUserDetailsService;

    // @Autowired
    // private UserService userService;
    @Autowired
    private IntegrationUserDetailsService integrationUserDetailsService;

    // --------------- spring-oauth-server import starts -----------

    @Autowired
    private DefaultTokenServices tokenServices;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private UserApprovalHandler userApprovalHandler;

    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;
    // @Autowired
    // private ClientDetailsService clientDetailsService;
    // @Autowired
    // private OAuth2AccessDeniedHandler oauth2AccessDeniedHandler;
    // @Autowired
    // private OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint;

    // --------------- spring-oauth-server import ends -----------

    /**
     * Jwt access token converter jwt access token converter.
     *
     * @return the jwt access token converter
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();

        // TODO: 加强安全性，签名文件可以改由系统文件数据加载。必须独立于代码库

        // Keypair is the alias name -> anilkeystore.jks / password / anila
        KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("anilkeystore.jks"), "password".toCharArray())
                .getKeyPair("anila");
        converter.setKeyPair(keyPair);
        return converter;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // clients.jdbc(this.dataSource).withClient("acme1").secret("xxxyyyzzz888")
        // .authorizedGrantTypes("authorization_code",
        // "client_credentials", "password", "implicit", "refresh_token").scopes("openid");

        // clients.jdbc(this.dataSource);
    }

    /**
     * 注意这里的 userDetailsService 不能缺少，否则 refresh_token 是提示没有它.
     *
     * @param endpoints 配置
     * @throws Exception ex
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager).accessTokenConverter(jwtAccessTokenConverter())
                .userDetailsService(integrationUserDetailsService).tokenStore(tokenStore).tokenServices(tokenServices)
                .userApprovalHandler(userApprovalHandler).authorizationCodeServices(authorizationCodeServices);

        // 自定义 Oauth 的错误显示
        endpoints.exceptionTranslator(customWebResponseExceptionTranslator);
    }

    // @Autowired
    // OAuth2AuthenticationEntryPoint oauth2AuthenticationEntryPoint;

    @Autowired
    private IntegrationAuthenticationFilter integrationAuthenticationFilter;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");

        // 确定不要这个： IntegrationAuthenticationFilter 已足以被系统感知并进入 filter chain ，如果再增加，就重复了
        // .addTokenEndpointAuthenticationFilter(integrationAuthenticationFilter);

        // .authenticationEntryPoint(oauth2AuthenticationEntryPoint); // 无效 new CustomAuthenticationEntryPoint
    }

}
