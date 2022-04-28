package com.tce.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PostFilter extends AbstractGatewayFilterFactory<PostFilter.Config> {

    private final ModifyRequestBodyGatewayFilterFactory factory;

    private final LambdaRewriteFunction lambdaRewriteFunction;

    public PostFilter(ModifyRequestBodyGatewayFilterFactory factory, LambdaRewriteFunction lambdaRewriteFunction) {
        super(Config.class);
        this.factory = factory;
        this.lambdaRewriteFunction = lambdaRewriteFunction;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            System.out.println("inside SCGWPostFilter.apply method...");

            if (!exchange.getResponse().getStatusCode().is2xxSuccessful()) {
                // Return the response as it is
                return chain.filter(exchange);
            }

            ModifyRequestBodyGatewayFilterFactory.Config cfg = new ModifyRequestBodyGatewayFilterFactory.Config();
            cfg.setRewriteFunction(String.class, String.class, lambdaRewriteFunction);

            GatewayFilter modifyBodyFilter = factory.apply(cfg);

            return modifyBodyFilter.filter(exchange, ch -> Mono.empty())
                    .then(chain.filter(exchange));
        };
    }

    public static class Config {
    }

}
