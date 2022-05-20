package com.tce.gateway.filter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Strings;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class RequestDecorator extends ServerHttpRequestDecorator {

    @Getter
    private final String requestBody;

    public RequestDecorator(ServerWebExchange exchange, DataBuffer dataBuffer) {
        super(exchange.getRequest());
        DataBufferUtils.retain(dataBuffer);
        final Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
        requestBody = toRaw(cachedFlux);
        log.info("requestBody {}", requestBody);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return Flux.just(requestBody).
                map(s -> new DefaultDataBufferFactory().wrap(requestBody.getBytes(StandardCharsets.UTF_8)));
    }

    private String toRaw(final Flux<DataBuffer> body) {
        final AtomicReference<String> rawRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            final byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            rawRef.set(Strings.fromUTF8ByteArray(bytes));
        });
        return rawRef.get();
    }

}
