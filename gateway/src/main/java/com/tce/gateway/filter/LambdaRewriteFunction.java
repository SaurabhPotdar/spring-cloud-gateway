package com.tce.gateway.filter;

import com.tce.gateway.service.LambdaInvoker;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
@Component
@Slf4j
public class LambdaRewriteFunction implements RewriteFunction<String, String> {

    private static final String functionName = "question";

    @Autowired
    private LambdaInvoker lambdaInvoker;

    @Override
    public Publisher<String> apply(ServerWebExchange serverWebExchange, String responseBody) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        if (!Objects.requireNonNull(response.getStatusCode()).is2xxSuccessful()) {
            //Send response without modifying
            return Mono.just(responseBody);
        }

        try {
            //Getting response body as null, will have to add cache in RouteConfig
            log.info("Response {}", responseBody);
            return Mono.just(lambdaInvoker.invoke(functionName, responseBody));
        } catch (RuntimeException e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return Mono.just(e.getMessage());
        }
    }

}
