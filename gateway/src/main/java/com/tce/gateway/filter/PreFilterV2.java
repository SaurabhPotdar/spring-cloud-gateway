package com.tce.gateway.filter;

import com.tce.gateway.service.LambdaService;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Reading request body
 */
@Component
@Slf4j
public class PreFilterV2 extends AbstractGatewayFilterFactory<PreFilterV2.Config> {

    private static final String FUNCTION_NAME = "question";

    @Autowired
    private LambdaService lambdaService;

    public PreFilterV2() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            try {
                //Prefilter
                final ServerHttpRequest mutatedHttpRequest = getServerHttpRequest(exchange, dataBuffer);
                exchange.mutate().request(mutatedHttpRequest).build();
                //return chain.filter(exchange.mutate().request(mutatedHttpRequest).build());

                //Post filter - Does not reach Controller and throws exception
                final ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                //response.writeWith(Mono.just(response.bufferFactory().wrap("file key name should be lpFile".getBytes())));
                return chain.filter(exchange.mutate().response(response).build());
            } catch (Exception e) {
                log.error("Error", e);
                throw new RuntimeException(e);
            }
        });
    }

    private ServerHttpRequest getServerHttpRequest(final ServerWebExchange exchange, final DataBuffer dataBuffer) {
        DataBufferUtils.retain(dataBuffer);
        final Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
        final String requestBody = toRaw(cachedFlux);
        log.info("requestBody inside filter {}", requestBody);
        String response = lambdaService.invoke(FUNCTION_NAME, requestBody);  //json string
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return Flux.just(response).
                        map(s -> new DefaultDataBufferFactory().wrap(response.getBytes(StandardCharsets.UTF_8)));
            }
        };
    }

    private static String toRaw(final Flux<DataBuffer> body) {
        final AtomicReference<String> rawRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            final byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            rawRef.set(Strings.fromUTF8ByteArray(bytes));
        });
        return rawRef.get();
    }

    public static class Config {
    }
}