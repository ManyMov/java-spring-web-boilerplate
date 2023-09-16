package com.leknarm.boilerplate.controller;

import com.leknarm.boilerplate.model.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<BaseResponse<String>> hello() {
        return ResponseEntity.ok(BaseResponse.<String>builder().data("hello").build());
    }

    @PostMapping("/hello")
    public ResponseEntity<BaseResponse<String>> helloWithName(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(BaseResponse.<String>builder().data(String.format("hello: %s", body.get("name"))).build());
    }

}
