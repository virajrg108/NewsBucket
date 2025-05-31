package com.newsbucket.model;

import lombok.Data;

record Source(String id, int name) {}

@Data
public class News {

    Source source;
    String author;
    String title;
    String description;
    String url;
    String urlToImage;
    String publishedAt;
    String content;

}
