package com.newsbucket.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/news")
    public Mono<Map<String, String>> newsFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "News service is currently unavailable. Please try again later.");
        return Mono.just(response);
    }

    @GetMapping("/default")
    public Mono<Map<String, String>> defaultFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "The requested service is currently unavailable. Please try again later.");
        return Mono.just(response);
    }
}