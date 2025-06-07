package com.newsbucket.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@Data
public class ArticleGroup {
    private String bucketLabel;              // e.g. "0"
    private String timeRangeDisplay;         // e.g. "Jun 5, 2025, 9:06 PM - Jun 6, 2025, 9:06 AM"
    private List<Article> articles;

    public String getBucketLabel() {
        return bucketLabel;
    }

    public void setBucketLabel(String bucketLabel) {
        this.bucketLabel = bucketLabel;
    }

    public String getTimeRangeDisplay() {
        return timeRangeDisplay;
    }

    public void setTimeRangeDisplay(String timeRangeDisplay) {
        this.timeRangeDisplay = timeRangeDisplay;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public ArticleGroup(String bucketLabel, String timeRangeDisplay, List<Article> articles) {
        this.bucketLabel = bucketLabel;
        this.timeRangeDisplay = timeRangeDisplay;
        this.articles = articles;
    }
// Getters, setters, constructors
}