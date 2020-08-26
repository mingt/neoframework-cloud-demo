
package com.anilallewar.microservices.auth.config;

import com.anilallewar.microservices.auth.common.ExtendedBCryptPasswordEncoder;
import com.anilallewar.microservices.auth.service.CustomJdbcClientDetailsService;
import com.anilallewar.microservices.auth.service.CustomJdbcTokenStore;
import com.anilallewar.microservices.auth.service.IntegrationUserDetailsService;
import com.anilallewar.microservices.auth.service.OauthService;
import com.neoframework.common.constant.SecurityConstant;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * The type O auth web form configuration.
 */
@Configuration
public class OAuthWebFormConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/oauth/confirm_access").setViewName("authorize");
    }

    /**
     * The type Login config.
     */
    @Configuration
    @Order(-20)
    protected static class LoginConfig extends WebSecurityConfigurerAdapter {

        // @Autowired
        // private AuthenticationManager authenticationManager;

        // @Autowired
        // private UserService userService;

        @Autowired
        private IntegrationUserDetailsService integrationUserDetailsService;

        @Override
        public void configure(WebSecurity web) throws Exception {

            // 下面此处忽略无效， token 相关应该处理 ResourceServerConfigurerAdapter，见各项目 ResourceServerConfiguration
            //
            // web.ignoring().antMatchers("/user/register","/api/user/register",
            // "/user/registerOfStudent","/api/user/registerOfStudent"
            // );

            web.expressionHandler(new OAuth2WebSecurityExpressionHandler());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.formLogin().loginPage("/login").permitAll().and().requestMatchers()
                    .antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access") // 表示这些若未登录时跳转到loginPage
                    .and().authorizeRequests()
                    // 下面此处忽略无效，其他项目应在各自项目上处理。见各项目的 ResourceServerConfiguration
                    // .antMatchers("/user/registerOfStudent", "/user/hadRegistered",
                    // "/qrcodeTest","/qrcodeTest3").permitAll()
                    .anyRequest().authenticated();

        }

        /**
         *
         * <p>TODO: 当前这个配置文件有点乱了， 下面的 authenticationManagerBean 覆盖了前面
         * {@link #configure(AuthenticationManagerBuilder auth)} 的配置。即前面的重复了，应该考虑去掉前面的</p>
         *
         * @return AuthenticationManager
         * @throws Exception ex
         */
        @Override
        @Bean(name = "authenticationManager")
        public AuthenticationManager authenticationManagerBean() throws Exception {
            // return super.authenticationManagerBean();

            CustomDaoAuthenticationProvider daoAuthenticationProvider = new CustomDaoAuthenticationProvider();
            // detailsService -> userService -> integrationUserDetailsService
            daoAuthenticationProvider.setUserDetailsService(integrationUserDetailsService);
            daoAuthenticationProvider
                .setPasswordEncoder(new ExtendedBCryptPasswordEncoder(SecurityConstant.BCRYPT_STRENGTH));

            List<AuthenticationProvider> providers = Arrays.asList((AuthenticationProvider) daoAuthenticationProvider);
            return new ProviderManager(providers);
        }

        /*
         * OAuth2 Configuration start
         */

        /**
         * Client details service client details service.
         *
         * @param dataSource the data source
         * @return the client details service
         */
        @Bean(name = "clientDetailsService")
        public ClientDetailsService clientDetailsService(DataSource dataSource) {
            return new CustomJdbcClientDetailsService(dataSource);
        }

        /**
         * Token store token store.
         *
         * @param dataSource the data source
         * @return the token store
         */
        @Bean(name = "tokenStore")
        public TokenStore tokenStore(DataSource dataSource) {
            // return new JdbcTokenStore(dataSource);
            return new CustomJdbcTokenStore(dataSource);
        }

        /**
         * 注意：
         *
         * <p>注意这里的@Primary 非常重要,否则会有3个同类型的Bean,无法注入,会抛出以下异常
         *
         * org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type
         * 'org.springframework.security.oauth2.provider.token.ResourceServerTokenServices' available:
         * expected single matching bean but found 3:
         * consumerTokenServices,defaultAuthorizationServerTokenServices,tokenServices
         *
         * Method springSecurityFilterChain in
         * org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration
         * required a single bean, but 3 were found</p>
         *
         * @param tokenStore the token store
         * @param clientDetailsService the client details service
         * @param enhancer the enhancer
         * @return default token services
         */
        @Primary
        @Bean(name = "tokenServices")
        public DefaultTokenServices tokenServices(TokenStore tokenStore, ClientDetailsService clientDetailsService,
                TokenEnhancer enhancer) {
            final DefaultTokenServices tokenServices = new CustomTokenService();
            ((CustomTokenService) tokenServices).setCustomTokenStore(tokenStore);
            tokenServices.setTokenStore(tokenStore);
            tokenServices.setClientDetailsService(clientDetailsService);
            tokenServices.setTokenEnhancer(enhancer);
            tokenServices.setSupportRefreshToken(true);

            // 目的是下面这个。如果使用默认的true，refresh_token+jwt会有个 Bug ：
            // https://stackoverflow.com/questions/47318215/refresh-token-grant-type-supplies-another-refresh-token
            // 如果有这个 Bug ，前端就无法一直使用 refresh_token 刷新
            // 所以这里设为 false
            tokenServices.setReuseRefreshToken(false);

            return tokenServices;
        }

        /**
         * O auth 2 request factory o auth 2 request factory.
         *
         * @param clientDetailsService the client details service
         * @return the o auth 2 request factory
         */
        @Bean(name = "oAuth2RequestFactory")
        public OAuth2RequestFactory oAuth2RequestFactory(ClientDetailsService clientDetailsService) {
            return new DefaultOAuth2RequestFactory(clientDetailsService);
        }

        /**
         * Oauth user approval handler user approval handler.
         *
         * @param tokenStore the token store
         * @param clientDetailsService the client details service
         * @param oAuth2RequestFactory the o auth 2 request factory
         * @param oauthService the oauth service
         * @return the user approval handler
         */
        @Bean(name = "oauthUserApprovalHandler")
        public UserApprovalHandler oauthUserApprovalHandler(TokenStore tokenStore,
                ClientDetailsService clientDetailsService, OAuth2RequestFactory oAuth2RequestFactory,
                OauthService oauthService) {
            OauthUserApprovalHandler userApprovalHandler = new OauthUserApprovalHandler();
            userApprovalHandler.setTokenStore(tokenStore);
            userApprovalHandler.setClientDetailsService(clientDetailsService);
            userApprovalHandler.setRequestFactory(oAuth2RequestFactory);
            userApprovalHandler.setOauthService(oauthService);
            return userApprovalHandler;
        }

        /**
         * Jdbc authorization code services authorization code services.
         *
         * @param dataSource the data source
         * @return the authorization code services
         */
        @Bean(name = "jdbcAuthorizationCodeServices")
        public AuthorizationCodeServices jdbcAuthorizationCodeServices(DataSource dataSource) {
            return new JdbcAuthorizationCodeServices(dataSource);
        }

        /**
         * Oauth 2 authentication entry point o auth 2 authentication entry point.
         *
         * @return the o auth 2 authentication entry point
         */
        @Bean(name = "oauth2AuthenticationEntryPoint")
        public OAuth2AuthenticationEntryPoint oauth2AuthenticationEntryPoint() {
            OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
            // 自定义错误 ==> 改由 OAuthServerConfiguration.configure(AuthorizationServerEndpointsConfigurer) 配置
            // entryPoint.setExceptionTranslator(new CustomWebResponseExceptionTranslator());
            return entryPoint;
        }

        /**
         * Oauth 2 client details user service client details user details service.
         *
         * @param clientDetailsService the client details service
         * @return the client details user details service
         */
        @Bean(name = "oauth2ClientDetailsUserService")
        public ClientDetailsUserDetailsService oauth2ClientDetailsUserService(
                ClientDetailsService clientDetailsService) {
            return new ClientDetailsUserDetailsService(clientDetailsService);
        }

        /**
         * Oauth 2 access denied handler o auth 2 access denied handler.
         *
         * @return the oAuth2 access denied handler
         */
        @Bean(name = "oauth2AccessDeniedHandler")
        public OAuth2AccessDeniedHandler oauth2AccessDeniedHandler() {
            return new OAuth2AccessDeniedHandler();
        }

        // @Bean(name = "clientCredentialsTokenEndpointFilter")
        // public ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter(AuthenticationManager
        // oauth2AuthenticationManager) {
        // ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter = new
        // ClientCredentialsTokenEndpointFilter();
        // clientCredentialsTokenEndpointFilter.setAuthenticationManager(oauth2AuthenticationManager);
        // return clientCredentialsTokenEndpointFilter;
        // }

    }

}
