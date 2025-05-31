package com.newsbucket.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


@Service
public class NewsService {

    static String url = "https://example.com/api/items?q=apple&apiKey=ccaf5d41cc5140c984818c344edcc14d";
    RestTemplate restTemplate;

    public String fetchNews(String keyword) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
//                .queryParam("q", "{q}")
//                .queryParam("apiKey", "{apiKey}")
//                .encode()
//                .toUriString();
//
//        Map<String, String> params = new HashMap<>();
//        params.put("q", keyword);
//        params.put("apiKey", "ccaf5d41cc5140c984818c344edcc14d");

//        HttpEntity<String> response = restTemplate.exchange(
//                urlTemplate,
//                HttpMethod.GET,
//                entity,
//                String.class,
//                params
//        );

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return response.getBody();

    }

}
