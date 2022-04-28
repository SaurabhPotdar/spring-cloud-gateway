package com.tce.gateway.filter;

import com.tce.gateway.service.LambdaInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Component
public class PostFilter extends AbstractGatewayFilterFactory<PostFilter.Config> {

    private static final String functionName = "question";

    @Autowired
    private LambdaInvoker lambdaInvoker;

    public PostFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        System.out.println("inside SCGWPostFilter.apply method...");
        return (exchange, chain) -> {
            if (!exchange.getResponse().getStatusCode().is2xxSuccessful()) {
                // Return the response as it is
                return chain.filter(exchange);
            }

            // Modify response if status is OK
            try {
                String requestBody = exchange.getAttribute("cachedRequestBodyObject");
                System.out.println(requestBody);
                return chain.filter(exchange);
                //return chain.filter(exchange).then(Mono.from(modifyResponse(exchange)));
            } catch (RuntimeException e) {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                return chain.filter(exchange.mutate()
                        .request(exchange.getRequest().mutate().build())
                        .build());
            }
        };
    }

    private String yourMethodToModifyRequestBody(String originalRequestBody) {
        System.out.println(originalRequestBody);
        return originalRequestBody + "****";
    }

    public Mono<Void> modifyResponse(ServerWebExchange exchange) throws RuntimeException {
        //https://stackoverflow.com/questions/48491098/how-to-add-some-data-in-body-of-response-for-cloud-api-gateway
        ServerHttpResponse currentResponse = exchange.getResponse();

        //TODO How to read response?
        //TODO Read response and check if data or error
        System.out.println(currentResponse);

        //TODO Catch exception
        String responseString = lambdaInvoker.invoke(functionName, currentResponse);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(responseString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = bos.toByteArray();
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
