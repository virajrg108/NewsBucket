package com.newsbucket.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GatewayController {

    @GetMapping("/gateway/info")
    public Mono<Map<String, Object>> getGatewayInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "NewsBucket API Gateway");
        info.put("version", "1.0.0");
        info.put("status", "running");
        info.put("routes", new String[]{"/api/news/**", "/**"});
        return Mono.just(info);
    }
}