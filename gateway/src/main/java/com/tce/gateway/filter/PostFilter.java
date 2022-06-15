package com.tce.gateway.filter;

import com.google.gson.Gson;
import com.tce.gateway.service.LambdaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(0)
public class PostFilter extends AbstractGatewayFilterFactory<PostFilter.Config> {

    @Autowired
    LambdaService lambdaService;

    @Autowired
    Gson gson;

    public PostFilter() {
        super(PostFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    log.info("Post Filter executed");
                }));
    }

    public static class Config {
    }

}
