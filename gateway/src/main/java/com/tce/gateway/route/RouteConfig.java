package com.tce.gateway.route;

import com.tce.gateway.service.LambdaInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
public class RouteConfig {

    private static final String functionName = "question";

    @Autowired
    private LambdaInvoker lambdaInvoker;

    @Bean
    public RouteLocator myRouteSavingRequestBody(RouteLocatorBuilder builder) {
        //https://stackoverflow.com/a/64535228/12021132
        //https://github.com/spring-cloud/spring-cloud-gateway/issues/747
        return builder.routes().route("my-route-id", p -> p.path("/ms2/**")
                .filters(f -> f.modifyResponseBody(String.class, String.class, (webExchange, originalBody) -> {
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
                .uri("http://localhost:9092/")).build();
    }

    public String modifyResponseBody(ServerWebExchange serverWebExchange, String responseBody) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        if (!Objects.requireNonNull(response.getStatusCode()).is2xxSuccessful()) {
            //Send response without modifying
            return responseBody;
        }

        try {
            return lambdaInvoker.invoke(functionName, responseBody);
        }
        catch(RuntimeException e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return e.getMessage();
        }
    }
}
