package com.newsbucket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.newsbucket.model.Article;
import com.newsbucket.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("api")
public class NewsController {

    @Autowired
    NewsService newsService;


//    public String getNews() {
//        return "Hello World!!";
//    }
    @GetMapping("/news")
    public List<Article> getNews(
            @RequestParam(name="q", required = false) String keyword
    ) throws JsonProcessingException {
        return newsService.fetchNews(keyword);
    }

}
