//package com.tce.gateway.route;
//
//import com.tce.gateway.service.LambdaInvoker;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.Objects;
//
//@Configuration
//@Slf4j
//public class RouteConfig {
//
//    private static final String functionName = "question";
//
//    @Autowired
//    private LambdaInvoker lambdaInvoker;
//
//    @Bean
//    public RouteLocator myRouteSavingRequestBody(RouteLocatorBuilder builder) {
//        //https://stackoverflow.com/a/64535228/12021132
//        //https://github.com/spring-cloud/spring-cloud-gateway/issues/747
//        //https://javamana.com/2021/09/20210910235604203Z.html  //https://github.com/zq2599/blog_demos/tree/master/spring-cloud-tutorials/gateway-change-body
//        return builder.routes().route("my-route-id", p -> p.path("/ms1/**")
//                .filters(f -> f.modifyResponseBody(String.class, String.class, MediaType.APPLICATION_JSON_VALUE,
//                        (webExchange, originalBody) -> {
//                            if (originalBody != null) {
//                                //Can get body here
//                                log.info("Original response {}", originalBody);
//                                String modifiedResponseBody = modifyResponseBody(webExchange, originalBody);
//                                webExchange.getAttributes().put("cachedResponseBodyObject", originalBody);
//                                log.info("Modified response {}", modifiedResponseBody);
//                                return Mono.just(modifiedResponseBody);
//                            } else {
//                                return Mono.empty();
//                            }
//                        })
//                )
//                .uri("http://localhost:9091/")).build();
//    }
//
//    public String modifyResponseBody(ServerWebExchange serverWebExchange, String responseBody) {
//        ServerHttpResponse response = serverWebExchange.getResponse();
//        if (!Objects.requireNonNull(response.getStatusCode()).is2xxSuccessful()) {
//            //Send response without modifying in case of error
//            return responseBody;
//        }
//
//        try {
//            return lambdaInvoker.invoke(functionName, responseBody);
//        } catch (RuntimeException e) {
//            response.setStatusCode(HttpStatus.BAD_REQUEST);
//            return e.getMessage();
//        }
//    }
//}
