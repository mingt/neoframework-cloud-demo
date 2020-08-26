
package com.anilallewar.microservices.auth;

import com.anilallewar.microservices.auth.dao.MyBatisRepository;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;

/**
 * The Main Spring Boot Application class that starts the authorization
 * server.
 *
 * <p>
 * Note that the server is also a Eureka client so as to register with the
 * Eureka server and be auto-discovered by other Eureka clients.
 * </p>
 *
 * @author anilallewar
 */
@SpringBootApplication
@EnableEurekaClient
// @EnableResourceServer // 与 ResourceServerConfiguration 里的重复了
@SessionAttributes("authorizationRequest")
@ComponentScan(basePackages = {"com.anilallewar.microservices.auth", "com.neoframework.common.config",
    "com.neoframework.common.redis"})
@MapperScan(basePackages = {"com.anilallewar.microservices.auth.dao", "com.anilallewar.microservices.auth.common.dao"},
    annotationClass = MyBatisRepository.class)
public class AuthServerApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);

        // testPassword();
    }

    /**
     * Rest template rest template.
     *
     * @param builder the builder
     * @return the rest template
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
