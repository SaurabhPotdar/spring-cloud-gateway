package com.tce.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Read request body and store it to attributes -> Can be accessed in Filter2
 * We can also modify request body
 */
@Component
@Slf4j
public class Filter_1 extends AbstractGatewayFilterFactory<Filter_1.Config> {

    public static final String PRINT_TEST_REQUEST_BODY = "print-test";

    public Filter_1() {
        super(Filter_1.Config.class);
    }

    @Override
    public GatewayFilter apply(Filter_1.Config config) {
        return (exchange, chain) -> DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            try {
                final ServerHttpRequest mutatedHttpRequest = getServerHttpRequest(exchange, dataBuffer);  //Modify request
                return chain.filter(exchange.mutate().request(mutatedHttpRequest).build());
            } catch (Exception e) {
                log.error("Error", e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Modify request body
     */
    private ServerHttpRequest getServerHttpRequest(final ServerWebExchange exchange, final DataBuffer dataBuffer) {
        DataBufferUtils.retain(dataBuffer);
        final Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
        final String requestBody = toRaw(cachedFlux);
        log.info("requestBody inside filter {}", requestBody);
        exchange.getAttributes().put(PRINT_TEST_REQUEST_BODY, requestBody);
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                //Modify request body
                String modifiedRequestBody = requestBody;
                return Flux.just(modifiedRequestBody).
                        map(s -> new DefaultDataBufferFactory().wrap(modifiedRequestBody.getBytes(StandardCharsets.UTF_8)));
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