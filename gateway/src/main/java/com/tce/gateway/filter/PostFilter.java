package com.tce.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class PostFilter extends AbstractGatewayFilterFactory<PostFilter.Config> {

    private static final String functionName = "question";

    public PostFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        System.out.println("inside SCGWPostFilter.apply method...");

        return (exchange, chain) ->
                chain.filter(exchange).then(Mono.from(modifyResponse(exchange)));
    }

    public Mono<Void> modifyResponse(ServerWebExchange exchange) {
        //https://stackoverflow.com/questions/48491098/how-to-add-some-data-in-body-of-response-for-cloud-api-gateway
        ServerHttpResponse currentResponse = exchange.getResponse();
        System.out.println(currentResponse);
        byte[] bytes = "Some text".getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    public static class Config {
        private String name;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
