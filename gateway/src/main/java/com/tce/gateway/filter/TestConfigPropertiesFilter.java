package com.tce.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TestConfigPropertiesFilter extends AbstractGatewayFilterFactory<TestConfigPropertiesFilter.Config> {

    public TestConfigPropertiesFilter() {
        super(TestConfigPropertiesFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(final Config config) {
        return (exchange, chain) -> {
            log.info("isReCaptaEnable {}", config.reCaptaEnable);
            return chain.filter(exchange);
        };
    }

    public static class Config {
        private boolean reCaptaEnable;

        Config() {
        }

        public boolean isReCaptaEnable() {
            return reCaptaEnable;
        }

        public void setReCaptaEnable(final boolean reCaptaEnable) {
            this.reCaptaEnable = reCaptaEnable;
        }
    }


    @Override
    public TestConfigPropertiesFilter.Config newConfig() {
        return new Config();
    }

}


//https://cloud.spring.io/spring-cloud-gateway/multi/multi__gatewayfilter_factories.html
