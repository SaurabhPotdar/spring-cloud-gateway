package com.tce.controller;

import com.google.gson.Gson;
import com.tce.dto.LambdaVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ms1")
@Slf4j
public class SimpleController {

    @Autowired
    private Gson gson;

    @GetMapping(value = "/get", produces = "application/json")
    public ResponseEntity<?> getData2(@RequestBody LambdaVo lambdaVo) {
        log.info("Inside MS1 /get2 method");
        log.info("Request body inside MS1 {}", lambdaVo);
        return ResponseEntity.ok("From MS1 /get2");
    }


}
