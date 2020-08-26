
package com.anilallewar.microservices.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

/**
 * 配置 ResourceServerConfigurerAdapter 支持忽略路径.
 *
 * <p>另一种实现方法是直接由 application 类，如 AuthApplication, UserApplication 继承 ResourceServerConfigurerAdapter，
 * 同时，同样添加下面配置 HttpSecurity 的方法（不要使用注释掉的 WebSecurityConfigurerAdapter）</p>
 *
 **/
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    // @Autowired
    // ConsumerTokenServices tokenServices;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // //logout setting
        // CustomLogoutSuccessHandler successHandler = new CustomLogoutSuccessHandler();
        // successHandler.setTokenServices(tokenServices);
        // http.logout().logoutSuccessHandler(successHandler).deleteCookies("JSESSIONID").clearAuthentication(true);

        http.csrf().disable()
                // 下面没有 qrcodeTest3 ，所以它需要认证 token 才能访问
                .authorizeRequests().antMatchers("/code", "/qrCodeInfo", "/scanCheck", "/sys/version", "/qrcodeTest")
                .permitAll().anyRequest().authenticated().and().logout().permitAll().and().formLogin().permitAll().and()
                .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
}
