/*
 * Copyright (c) 2019. Project: Elearning.
 *
 * All rights reserved.
 */

package com.anilallewar.microservices.auth.common.code;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * The type Filter ignore properties config.
 *
 * @author lengleng
 * @since 2018/7/22 网关不校验终端配置
 */
// @Data
@Configuration
@RefreshScope
@ConditionalOnExpression("!'${ignore}'.isEmpty()")
@ConfigurationProperties(prefix = "ignore")
public class FilterIgnorePropertiesConfig {

    private List<String> clients = new ArrayList<>();
    private List<String> swaggerProviders = new ArrayList<>();

    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }

    public List<String> getSwaggerProviders() {
        return swaggerProviders;
    }

    public void setSwaggerProviders(List<String> swaggerProviders) {
        this.swaggerProviders = swaggerProviders;
    }
}
