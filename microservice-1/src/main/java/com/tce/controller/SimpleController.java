package com.tce.controller;

import com.google.gson.Gson;
import com.tce.dto.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/ms1")
public class SimpleController {

    private final List<Question> questionList = List.of(
            new Question("id1"),
            new Question("id2"),
            new Question("id3"));

    @GetMapping(value = "/get")
    public Flux<Question> getData(ServerHttpRequest request, ServerHttpResponse response) {
        System.out.println("Inside MS1 getData method");
        HttpHeaders headers = request.getHeaders();

        headers.forEach((k, v) -> {
            System.out.println(k + " : " + v);
        });

        return Flux.fromIterable(questionList);
    }


}
