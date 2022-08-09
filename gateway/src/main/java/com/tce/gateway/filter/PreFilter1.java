package com.tce.gateway.filter;

import com.google.gson.Gson;
import com.tce.gateway.dto.LambdaVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;

import static com.tce.gateway.constants.Constants.LAMBDA_VO;

/**
 * Modify requestBody and send it as response
 * Filter2 will read this response as requestBody
 */
@Component
@Slf4j
public class PreFilter1 extends AbstractGatewayFilterFactory<PreFilter1.Config> {

    @Autowired
    private Gson gson;

    public PreFilter1() {
        super(PreFilter1.Config.class);
    }

    @Override
    public GatewayFilter apply(PreFilter1.Config config) {
        return (exchange, chain) -> DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            try {
                //Read request body
                final RequestDecorator requestDecorator = new RequestDecorator(exchange, dataBuffer);
                final String requestBody = requestDecorator.getRequestBody();
                final LambdaVo lambdaVo = gson.fromJson(requestBody, LambdaVo.class);
                log.info("RequestBody in PreFilter1 {}", lambdaVo);
                exchange.getAttributes().put(LAMBDA_VO, lambdaVo);
                throw new Exception();
                //return chain.filter(exchange);
            } catch (Exception e) {
                log.error("Error {}", e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        });

    }

    public static class Config {
    }

}
