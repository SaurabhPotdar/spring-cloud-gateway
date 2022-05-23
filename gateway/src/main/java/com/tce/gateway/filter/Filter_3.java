package com.tce.gateway.filter;

import com.google.gson.Gson;
import com.tce.gateway.BusinessException;
import com.tce.gateway.dto.LambdaVo;
import com.tce.gateway.service.LambdaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(0)
public class Filter_3 extends AbstractGatewayFilterFactory<Filter_3.Config>  {

    @Autowired
    LambdaService lambdaService;

    @Autowired
    Gson gson;

    public Filter_3() {
        super(Filter_3.Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            try {
                //Read request body
                RequestDecorator requestDecorator = new RequestDecorator(exchange, dataBuffer);
                String requestBody = requestDecorator.getRequestBody();
                LambdaVo lambdaVo = gson.fromJson(requestBody, LambdaVo.class);

                //Modify response
                String response = lambdaService.invoke(lambdaVo.getFunctionName(), lambdaVo.getJsonPayload());
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(response.getBytes(StandardCharsets.UTF_8))));
            } catch (Exception e) {
                throw new BusinessException(e.getMessage());
            }
        });
    }

    public static class Config {
    }

}
