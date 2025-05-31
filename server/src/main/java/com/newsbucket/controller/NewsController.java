package com.newsbucket.controller;

import com.newsbucket.model.News;
import com.newsbucket.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NewsController {

//    @Autowired
//    NewsService newsService;

    @GetMapping("")
    public String getNews() {
        return "Hello World!!";
    }

//    public String getNews(
//            @RequestParam(name="q", required = false) String keyword
//    ) {
//        String output = newsService.fetchNews(keyword);
//        return output;
//    }

}
