package com.tce.gateway.filter;

import com.tce.gateway.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
@Order(1)
//Order is ...,-2,-1,0,1,2,...
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        log.error("Global {}", throwable.getMessage());
        final DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (throwable instanceof ResponseStatusException) {
            final ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
            exchange.getResponse().setStatusCode(responseStatusException.getStatus());
            return exchange.getResponse().writeWith(Mono.empty());
        } else if (throwable instanceof BusinessException) {
            final BusinessException businessException = (BusinessException) throwable;
            final DataBuffer dataBuffer = bufferFactory.wrap(businessException.getMessage().getBytes(StandardCharsets.UTF_8));
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        }
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return exchange.getResponse().writeWith(Mono.empty());
    }

}
