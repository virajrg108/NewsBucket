package com.newsbucket.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsbucket.model.Article;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class NewsService {

    static String url = "https://newsapi.org/v2/everything";
    RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

     @Value("${newsapi.key}")
     private String newsApiKey;

    public List<Article> fetchNews(String keyword) throws JsonProcessingException {
         System.out.println("newsAPiKey: " + newsApiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String urlTemplate = UriComponentsBuilder.fromUriString(url)
                .queryParam("q", "{q}")
                .queryParam("apiKey", "{apiKey}")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("q", keyword);
        params.put("apiKey", newsApiKey);

        HttpEntity<String> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params
        );
        String rawJson = response.getBody();
        JsonNode jsonObj = objectMapper.readTree(rawJson);
        System.out.println(jsonObj.get("status"));
        System.out.println(jsonObj.get("totalResults"));
        if(!jsonObj.get("status").textValue().replace("\"","").equalsIgnoreCase("ok")) {
            // TODO: error handling
            return null;
        } else {
            JsonNode articlesNode = jsonObj.get("articles");
            String articlesJson = articlesNode.toString();
            List<Article> articles = Arrays.asList(objectMapper.readValue(articlesJson, Article[].class));
            System.out.println(articles.get(0).toString());
            return articles;
        }
    }
}
