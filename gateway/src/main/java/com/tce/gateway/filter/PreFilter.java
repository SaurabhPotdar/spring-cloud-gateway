package com.tce.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class PreFilter extends AbstractGatewayFilterFactory<PreFilter.Config> {

    public PreFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            System.out.println("inside SCGWPreFilter.apply method");
            ServerHttpRequest request = exchange.getRequest().mutate().header("scgw-pre-header", Math.random() * 10 + "").build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    public static class Config {
    }
}
