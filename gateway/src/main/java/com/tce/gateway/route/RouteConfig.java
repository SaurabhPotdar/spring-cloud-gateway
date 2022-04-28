package com.tce.gateway.route;

import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class RouteConfig implements RewriteFunction<String, String> {

    @Bean
    public RouteLocator myRouteSavingRequestBody(RouteLocatorBuilder builder) {
        //https://stackoverflow.com/a/64535228/12021132
        return builder.routes()
                .route("my-route-id",
                        p -> p.path("/ms2/**") //your own path filter
                                .filters(f -> f
                                        .modifyResponseBody(String.class, String.class,
                                                (webExchange, originalBody) -> {
                                                    if (originalBody != null) {
                                                        webExchange.getAttributes().put("cachedResponseBodyObject", originalBody);
                                                        //Can get body here
                                                        return Mono.just(originalBody);
                                                    } else {
                                                        return Mono.empty();
                                                    }
                                                })
                                        .modifyResponseBody(String.class, String.class, new RouteConfig())
                                )
                                .uri("http://localhost:9092/")
                )
                .build();
    }

    @Override
    public Publisher<String> apply(ServerWebExchange serverWebExchange, String s) {
        return null;
    }
}
