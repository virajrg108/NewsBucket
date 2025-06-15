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
        System.out.println("new api key: " + newsApiKey);
        ChronoUnit unit = toChronoUnit(intervalType);
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm a");

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
                        if (articlesNode == null || !articlesNode.isArray() || articlesNode.size() == 0) {
                            sink.complete();
                            return Collections.<ArticleGroup>emptyList();
                        }

                        List<Article> articles = objectMapper.readerForListOf(Article.class).readValue(articlesNode);

                        // Grouping with corrected logic
                        Map<String, List<Article>> grouped = new HashMap<>();
                        for (Article article : articles) {
                            if (article.getPublishedAt() == null) continue;

                            try {
                                Instant publishedAt = Instant.parse(article.getPublishedAt());
                                ZonedDateTime publishedTime = publishedAt.atZone(now.getZone());

                                // Calculate which interval bucket this article belongs to
                                long totalUnits = unit.between(publishedTime, now);
                                int bucketIndex = (int) (totalUnits / number);

                                String bucketKey = String.valueOf(bucketIndex);
                                grouped.computeIfAbsent(bucketKey, k -> new ArrayList<>()).add(article);
                            } catch (Exception e) {
                                // Skip articles with invalid dates
                                System.err.println("Skipping article with invalid date: " + article.getPublishedAt());
                            }
                        }

                        return grouped.entrySet().stream()
                                .map(entry -> {
                                    int bucketIndex = Integer.parseInt(entry.getKey());

                                    // Calculate interval boundaries
                                    // For bucket 0: most recent interval (now - number*unit to now)
                                    // For bucket 1: next interval (now - 2*number*unit to now - number*unit)
                                    ZonedDateTime intervalEnd = subtractUnits(now, bucketIndex * number, unit);
                                    ZonedDateTime intervalStart = subtractUnits(now, (bucketIndex + 1) * number, unit);

                                    String timeRangeDisplay = formatter.format(intervalStart) + " - " + formatter.format(intervalEnd);
                                    return new ArticleGroup(entry.getKey(), timeRangeDisplay, entry.getValue());
                                })
                                .sorted(Comparator.comparing((ArticleGroup ag) -> Integer.parseInt(ag.getBucketLabel())).reversed()) // Sort by bucket index (0=most recent, 1=older, etc.)
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
                    } else {
                        // No more articles, complete the stream
                        sink.complete();
                    }
                }, sink::error);
    }

    /**
     * Properly subtract units from a ZonedDateTime based on the ChronoUnit type
     */
    private ZonedDateTime subtractUnits(ZonedDateTime dateTime, long amount, ChronoUnit unit) {
        return switch (unit) {
            case MINUTES -> dateTime.minusMinutes(amount);
            case HOURS -> dateTime.minusHours(amount);
            case DAYS -> dateTime.minusDays(amount);
            case WEEKS -> dateTime.minusWeeks(amount);
            case MONTHS -> dateTime.minusMonths(amount);
            case YEARS -> dateTime.minusYears(amount);
            default -> dateTime.minus(amount, unit);
        };
    }

    private ChronoUnit toChronoUnit(String intervalType) {
        return switch (intervalType.toLowerCase()) {
            case "minutes", "minute" -> ChronoUnit.MINUTES;
            case "hours", "hour" -> ChronoUnit.HOURS;
            case "days", "day" -> ChronoUnit.DAYS;
            case "weeks", "week" -> ChronoUnit.WEEKS;
            case "months", "month" -> ChronoUnit.MONTHS;
            case "years", "year" -> ChronoUnit.YEARS;
            default -> throw new IllegalArgumentException("Unsupported interval type: " + intervalType);
        };
    }
}