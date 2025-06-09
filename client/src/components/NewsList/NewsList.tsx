import axios from 'axios';
import { useEffect, useState, useCallback } from 'react';
import { Accordion, AccordionItem, AccordionTrigger, AccordionContent } from "@/components/ui/accordion";
import { Card, CardContent, CardDescription, CardTitle } from "@/components/ui/card";
import { Clock } from "lucide-react";
import { Button } from "@/components/ui/button";

export interface Article {
  author: string | null;
  title: string;
  description: string;
  url: string;
  urlToImage: string;
  publishedAt: string;
  content: string;
}

interface ArticleGroup {
  bucketLabel: string;
  timeRangeDisplay: string;
  articles: Article[];
}

const NewsList = () => {
  const [bucketMap, setBucketMap] = useState<Record<string, ArticleGroup>>({});

  // Merge a new ArticleGroup into existing bucketMap
  const mergeGroup = useCallback((newGroup: ArticleGroup) => {
    setBucketMap(prevMap => {
      const existingGroup = prevMap[newGroup.bucketLabel];
      const mergedArticles = existingGroup
        ? [...existingGroup.articles, ...newGroup.articles]
        : [...newGroup.articles];

      return {
        ...prevMap,
        [newGroup.bucketLabel]: {
          bucketLabel: newGroup.bucketLabel,
          timeRangeDisplay: newGroup.timeRangeDisplay,
          articles: mergedArticles,
        }
      };
    });
  }, []);

  useEffect(() => {
    const eventSource = new EventSource('http://localhost:8081/api/news/stream?keyword=apple&intervalType=days&number=2');

    eventSource.onmessage = (event) => {
      const data: ArticleGroup = JSON.parse(event.data);
      mergeGroup(data);
    };

    eventSource.onerror = (err) => {
      console.error("Streaming error:", err);
      eventSource.close();
    };

    return () => eventSource.close();
  }, [mergeGroup]);

  // Convert map to sorted array (newest first)
  const sortedBuckets = Object.values(bucketMap).sort(
    (a, b) => parseInt(b.bucketLabel) - parseInt(a.bucketLabel)
  );

  return (
    <div className="news-list max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div className="mt-8">
        <h3 className="text-lg font-semibold mb-4">Search Results for "apple"</h3>

        <Accordion type="single" collapsible className="mb-4 bg-white shadow-sm rounded-lg">
          {sortedBuckets.map((bucket, idx) => (
            <AccordionItem value={`item-${idx}`} key={bucket.bucketLabel} className="sm:p-2 md:p-4">
              <AccordionTrigger>
                {bucket.timeRangeDisplay} ({bucket.articles?.length} articles)
              </AccordionTrigger>
              <AccordionContent>
                {bucket.articles.map((article, idx) => (
                  <Card key={article.url || idx} className="mb-4 hover:shadow-lg transition-all duration-300 bg-gray-50">
                    <CardContent>
                      <div className="flex justify-between items-start">
                        <div className="flex-grow mr-4">
                          <CardTitle className="text-lg font-semibold mb-1">{article.title}</CardTitle>
                          <CardDescription className="text-sm text-muted-foreground mb-2 text-ellipsis overflow-hidden line-clamp-2">
                            {article.description}
                          </CardDescription>
                          <div className="flex flex-wrap items-center gap-x-2 gap-y-1">
                            <span className="text-xs">By {article.author}</span>
                            <span className="text-xs pl-1">
                              <Clock size={14} className="inline mr-1" />
                              {new Date(article.publishedAt).toLocaleDateString('en-US', {
                                month: 'short',
                                day: 'numeric',
                                year: 'numeric',
                              })} - {new Date(article.publishedAt).toLocaleTimeString('en-US', {
                                hour: '2-digit',
                                minute: '2-digit',
                                hour12: true,
                              })}
                            </span>
                            <div className="w-full sm:w-auto mt-1 sm:mt-0">
                              <a href={article.url} target="_blank" rel="noopener noreferrer">
                                <Button variant="link">Read more</Button>
                              </a>
                            </div>
                          </div>
                        </div>
                        {article.urlToImage && (
                          <img src={article.urlToImage} alt={article.title} className="w-20 h-20 object-cover rounded-md flex-shrink-0" />
                        )}
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </AccordionContent>
            </AccordionItem>
          ))}
        </Accordion>
      </div>
    </div>
  );
};

export default NewsList;