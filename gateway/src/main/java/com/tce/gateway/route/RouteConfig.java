package com.tce.gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator myRouteSavingRequestBody(RouteLocatorBuilder builder) {
        //https://stackoverflow.com/a/64535228/12021132
        //https://github.com/spring-cloud/spring-cloud-gateway/issues/747
        return builder.routes()
                .route("my-route-id",
                        p -> p.path("/ms2/**") //your own path filter
                                .filters(f -> f
                                        .modifyResponseBody(String.class, String.class,
                                                (webExchange, originalBody) -> {
                                                    if (originalBody != null) {
                                                        String modifiedResponseBody = modifyResponseBody(webExchange, originalBody);
                                                        webExchange.getAttributes().put("cachedResponseBodyObject", originalBody);
                                                        //Can get body here
                                                        return Mono.just(modifiedResponseBody);
                                                    } else {
                                                        return Mono.empty();
                                                    }
                                                })
                                )
                                .uri("http://localhost:9092/")
                )
                .build();
    }

    public String modifyResponseBody(ServerWebExchange serverWebExchange, String responseBody) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.CREATED);
        return "--" + responseBody + "--";
    }
}
