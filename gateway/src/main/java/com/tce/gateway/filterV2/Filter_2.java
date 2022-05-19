package com.tce.gateway.filterV2;

import com.tce.gateway.service.LambdaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.tce.gateway.filterV2.Filter_1.PRINT_TEST_REQUEST_BODY;

@Component
@Slf4j
public class Filter_2 extends AbstractGatewayFilterFactory<Filter_2.Config> {

    private static final String FUNCTION_NAME = "question";

    @Autowired
    LambdaService lambdaService;

    public Filter_2() {
        super(Filter_2.Config.class);
    }

    @Override
    public GatewayFilter apply(Filter_2.Config config) {
        return (exchange, chain) -> DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            try {
                String requestBody = exchange.getAttribute(PRINT_TEST_REQUEST_BODY);
                assert requestBody != null;
                String response = lambdaService.invoke(FUNCTION_NAME, requestBody);
                //exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);  //set status code
                //Add json as output type
                //Doesn't go to the controller
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
