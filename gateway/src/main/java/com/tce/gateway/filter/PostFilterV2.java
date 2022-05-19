package com.tce.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class PostFilterV2 extends AbstractGatewayFilterFactory<PostFilterV2.Config> {

    public PostFilterV2() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            try {
                final ServerHttpRequest mutatedHttpRequest = getServerHttpRequest(exchange, dataBuffer);
                return chain.filter(exchange).then(Mono.fromRunnable(()->{
                    ServerHttpResponse response = exchange.getResponse();
                    HttpHeaders headers = response.getHeaders();
                    headers.forEach((k,v)->{
                        System.out.println(k + " : " + v);
                    });
                }));
            } catch (Exception e) {
                log.error("Error", e);
                throw new RuntimeException(e);
            }
        });
    }

    //Try to read request body in post filter and then return response

    //Feign will send request body as response and filter reads this -> Try to read response body in post filter

    public static class Config {
    }

    private ServerHttpRequest getServerHttpRequest(final ServerWebExchange exchange, final DataBuffer dataBuffer) {
        DataBufferUtils.retain(dataBuffer);
        final Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
        final String requestBody = toRaw(cachedFlux);
        log.info("requestBody {}", requestBody);
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public Flux<DataBuffer> getBody() {
                return Flux.just(requestBody).
                        map(s -> new DefaultDataBufferFactory().wrap(requestBody.getBytes(StandardCharsets.UTF_8)));
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
        String out = rawRef.get();
        log.info("toRaw() {}", out);
        return out;
    }

}
