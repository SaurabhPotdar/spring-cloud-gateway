package com.tce.gateway.filter;

import com.tce.gateway.service.LambdaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class Filter_3 extends AbstractGatewayFilterFactory<Filter_3.Config>  {

    private static final String FUNCTION_NAME = "question";

    @Autowired
    LambdaService lambdaService;

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
                log.info(requestBody);

                //Modify response
                String response = lambdaService.invoke(FUNCTION_NAME, requestBody);
                //exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);  //set status code
                exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(response.getBytes())));
            } catch (Exception e) {
                log.error("Error", e);
                throw new RuntimeException(e);
            }
        });
    }

    public static class Config {
    }

}
