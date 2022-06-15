package com.tce.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ms2")
public class SimpleController {

    @GetMapping(value = "/get")
    public Mono<String> getData(ServerHttpRequest request, ServerHttpResponse response) {
        System.out.println("Inside MS2 getData method");
        HttpHeaders headers = request.getHeaders();
        headers.forEach((k, v) -> {
            System.out.println(k + " : " + v);
        });
        return Mono.just("Hello from Reactive SC-MS2 getData method!!");
    }
}
