package com.tce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

/**
 * Call other microservices via Gateway
 */
@FeignClient("gateway-service")
public interface GatewayFeign {

    //MS2 does not have an implementation /get2 url, but it gets intercepted by filter and filter returns response
    @GetMapping(value = "/ms2/get2")
    Mono<String> getData();

}
