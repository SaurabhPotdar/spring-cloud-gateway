package com.tce.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SinkFilter extends AbstractGatewayFilterFactory<SinkFilter.Config> {

    public SinkFilter() {
        super(SinkFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            final ServerHttpResponse response = exchange.getResponse();
            log.info("Sink filter");
            response.getHeaders().set("x-intercepted", "true");
            response.setRawStatusCode(200);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(new byte[0])));
        };
    }

    public static class Config {
    }

}
