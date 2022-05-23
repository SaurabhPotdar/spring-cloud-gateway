package com.tce.controller;

import com.google.gson.Gson;
import com.tce.dto.LambdaVo;
import com.tce.dto.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/ms1")
@Slf4j
public class SimpleController {

    @Autowired
    private Gson gson;

    private final List<Question> questionList = List.of(
            new Question("id1"),
            new Question("id2"),
            new Question("id3"));

    @GetMapping(value = "/get")
    public Flux<Question> getData(ServerHttpRequest request, ServerHttpResponse response) {
        System.out.println("Inside MS1 /get method");
        HttpHeaders headers = request.getHeaders();

        headers.forEach((k, v) -> {
            System.out.println(k + " : " + v);
        });

        return Flux.fromIterable(questionList);
    }

    @GetMapping(value = "/get2", produces = "application/json")
    public ResponseEntity<?> getData2(@RequestBody LambdaVo lambdaVo) {
        log.info("Inside MS1 /get2 method");
        log.info("Request body inside MS1 {}", lambdaVo);
        return ResponseEntity.ok("From MS1 /get2");
    }

//    @GetMapping(value = "/get2", produces = "application/json")
//    public ResponseEntity<?> getData2(ServerHttpResponse response) {
//        log.info("Status code {}", response.getRawStatusCode());
//        log.info("Inside MS1 getData method");
//        return ResponseEntity.ok("MS");
//    }


}
