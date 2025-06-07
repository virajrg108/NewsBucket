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

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewsService {

    static String url = "https://newsapi.org/v2/everything";
    RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${newsapi.key}")
    private String newsApiKey;

    public Map<String, List<Article>> fetchAndGroupArticles(String keyword, String intervalType, int number) throws JsonProcessingException {
        List<Article> articles = fetchArticlesFromNewsApi(keyword);
        return groupArticlesByInterval(articles, intervalType, number);
    }

    private ChronoUnit toChronoUnit(String intervalType) {
        return switch (intervalType.toLowerCase()) {
            case "minutes" -> ChronoUnit.MINUTES;
            case "hours" -> ChronoUnit.HOURS;
            case "days" -> ChronoUnit.DAYS;
            case "weeks" -> ChronoUnit.WEEKS;
            case "months" -> ChronoUnit.MONTHS;
            case "years" -> ChronoUnit.YEARS;
            default -> throw new IllegalArgumentException("Unsupported interval type: " + intervalType);
        };
    }

    private Map<String, List<Article>> groupArticlesByInterval(List<Article> articles, String intervalType, int number) {
        ChronoUnit unit = toChronoUnit(intervalType);
        Instant now = Instant.now();
        Map<String, List<Article>> grouped = new HashMap<>();

        for (Article article : articles) {
            Instant publishedAt = Instant.parse(article.getPublishedAt());

            long age = unit.between(publishedAt, now); // e.g., 37 hours
            int bucket = (int) (age / number);         // e.g., 37 / 12 = 3 (bucket 3: 36–48 hours ago)

            String bucketLabel = String.valueOf(bucket);
            grouped.computeIfAbsent(bucketLabel, k -> new ArrayList<>()).add(article);
        }

        return grouped.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getKey())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public List<Article> fetchArticlesFromNewsApi(String keyword) throws JsonProcessingException {
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
                params);
        String rawJson = response.getBody();
        JsonNode jsonObj = objectMapper.readTree(rawJson);
        System.out.println(jsonObj.get("status"));
        System.out.println(jsonObj.get("totalResults"));
        if (!jsonObj.get("status").textValue().replace("\"", "").equalsIgnoreCase("ok")) {
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
