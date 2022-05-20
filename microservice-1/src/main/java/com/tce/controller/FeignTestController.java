package com.tce.controller;

import com.tce.feign.GatewayFeign;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/feign")
public class FeignTestController {

    private GatewayFeign gatewayFeign;

    @GetMapping(value = "/test")
    public String get() {
        return gatewayFeign.getData().block();
    }

}
