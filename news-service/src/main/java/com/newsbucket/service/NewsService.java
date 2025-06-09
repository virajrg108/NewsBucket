package com.newsbucket.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newsbucket.model.Article;
import com.newsbucket.model.ArticleGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${newsapi.key}")
    private String newsApiKey;

    public NewsService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://newsapi.org").build();
    }

    public Flux<ArticleGroup> streamGroupedArticlesByPage(String keyword, String intervalType, int number) {
        ChronoUnit unit = toChronoUnit(intervalType);
        Instant nowInstant = Instant.now();
        ZonedDateTime now = nowInstant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a").withZone(ZoneId.systemDefault());

        return Flux.create(sink -> {
            fetchPage(1, keyword, intervalType, number, sink, unit, now, formatter);
        });
    }

    private void fetchPage(int page, String keyword, String intervalType, int number,
                           FluxSink<ArticleGroup> sink, ChronoUnit unit, ZonedDateTime now,
                           DateTimeFormatter formatter) {

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/everything")
                        .queryParam("q", keyword)
                        .queryParam("apiKey", newsApiKey)
                        .queryParam("page", page)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    try {
                        JsonNode root = objectMapper.readTree(json);
                        if ("error".equals(root.path("status").asText()) &&
                                "maximumResultsReached".equals(root.path("code").asText())) {
                            sink.complete();
                            return Collections.<ArticleGroup>emptyList();
                        }

                        JsonNode articlesNode = root.get("articles");
                        List<Article> articles = objectMapper.readerForListOf(Article.class).readValue(articlesNode);

                        // Grouping
                        Map<String, List<Article>> grouped = new HashMap<>();
                        for (Article article : articles) {
                            Instant publishedAt = Instant.parse(article.getPublishedAt());
                            long age = unit.between(publishedAt, now);
                            int bucket = (int) (age / number);
                            String bucketLabel = String.valueOf(bucket);
                            grouped.computeIfAbsent(bucketLabel, k -> new ArrayList<>()).add(article);
                        }

                        return grouped.entrySet().stream()
                                .map(entry -> {
                                    int bucket = Integer.parseInt(entry.getKey());
                                    ZonedDateTime endTime = now.minus(unit.getDuration().multipliedBy(bucket));
                                    ZonedDateTime startTime = now.minus(unit.getDuration().multipliedBy(bucket + 1));
                                    String label = formatter.format(startTime) + " - " + formatter.format(endTime);
                                    return new ArticleGroup(entry.getKey(), label, entry.getValue());
                                })
                                .collect(Collectors.toList());

                    } catch (Exception e) {
                        sink.error(e);
                        return null;
                    }
                })
                .subscribe(groups -> {
                    if (groups != null && !groups.isEmpty()) {
                        for (ArticleGroup group : groups) {
                            sink.next(group);
                        }
                        fetchPage(page + 1, keyword, intervalType, number, sink, unit, now, formatter);
                    }
                }, sink::error);
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
}