package com.newsbucket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newsbucket.model.Article;
import com.newsbucket.model.ArticleGroup;
import com.newsbucket.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api")
public class NewsController {

    @Autowired
    NewsService newsService;

    // public String getNews() {
    // return "Hello World!!";
    // }
    @GetMapping("/news")
    public ResponseEntity<List<ArticleGroup>> getNews(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "hours") String intervalType,
            @RequestParam(defaultValue = "12") int number) throws JsonProcessingException {
        List<ArticleGroup> groupedArticles = newsService.fetchAndGroupArticles(keyword, intervalType, number);
        return ResponseEntity.ok(groupedArticles);
    }

}
