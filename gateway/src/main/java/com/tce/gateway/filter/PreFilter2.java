package com.tce.gateway.filter;

import com.google.gson.Gson;
import com.tce.gateway.dto.LambdaVo;
import com.tce.gateway.service.LambdaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Reads response from PreFilter1 as requestBody
 * Sends a modified response
 */
@Component
@Slf4j
public class PreFilter2 extends AbstractGatewayFilterFactory<PreFilter2.Config> {

    @Autowired
    private LambdaService lambdaService;

    @Autowired
    private Gson gson;

    public PreFilter2() {
        super(PreFilter2.Config.class);
    }

    @Override
    public GatewayFilter apply(PreFilter2.Config config) {
        return (exchange, chain) -> DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            try {
                //Read request body
                final RequestDecorator requestDecorator = new RequestDecorator(exchange, dataBuffer);
                final String requestBody = requestDecorator.getRequestBody();
                final LambdaVo lambdaVo = gson.fromJson(requestBody, LambdaVo.class);
                log.info("RequestBody in PreFilter2 {}", lambdaVo);

                //Modify response
                final String response = lambdaService.invoke(lambdaVo.getFunctionName(), lambdaVo.getJsonPayload());
                exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(response.getBytes(StandardCharsets.UTF_8))));
                return chain.filter(exchange);
            } catch (Exception e) {
                log.error("Error", e);
                throw new RuntimeException(e);
            }
        });
    }

    public static class Config {
    }

}
