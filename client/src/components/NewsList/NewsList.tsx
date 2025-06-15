import { useEffect, useState, useCallback, type FormEvent } from 'react';
import { Accordion, AccordionItem, AccordionTrigger, AccordionContent } from "@/components/ui/accordion";
import { Card, CardContent, CardDescription, CardTitle } from "@/components/ui/card";
import { Clock, Loader } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

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
  const [keyword, setKeyword] = useState<string>("tech");
  const [intervalType, setIntervalType] = useState<string>("days");
  const [number, setNumber] = useState<string>("1");
  const [eventSource, setEventSource] = useState<EventSource | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

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

  const handleSubmit = (e: FormEvent) => {
    e.preventDefault();
    
    // Close existing event source if any
    if (eventSource) {
      eventSource.close();
      setIsLoading(false);
    }
    
    // Clear existing data and set loading state
    setBucketMap({});
    setIsLoading(true);
    
    console.log("calling backend", window.location.origin.replace(":3000", ":8081"));
    // Create new event source with the form parameters
    const newEventSource = new EventSource(
      `${window.location.href.replace(":3000",':8081')}api/news/stream?keyword=${encodeURIComponent(keyword)}&intervalType=${intervalType}&number=${number}`
    );
    
    newEventSource.onmessage = (event) => {
      const data: ArticleGroup = JSON.parse(event.data);
      mergeGroup(data);
      // setIsLoading(false); // Set loading to false when we receive data
    };
    
    newEventSource.onerror = (err) => {
      console.error("Streaming error:", err);
      newEventSource.close();
      setIsLoading(false); // Set loading to false on error
    };
    
    setEventSource(newEventSource);
  };

  useEffect(() => {

    // const API_BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8081";
    console.log("calling backend", window.location.href.replace(":3000",':8081'));
    const initialEventSource = new EventSource(
      `${window.location.href.replace(":3000",':8081')}api/news/stream?keyword=${encodeURIComponent(keyword)}&intervalType=${intervalType}&number=${number}`
    );

    initialEventSource.onmessage = (event) => {
      const data: ArticleGroup = JSON.parse(event.data);
      mergeGroup(data);
      setIsLoading(false);
    };

    initialEventSource.onerror = (err) => {
      console.error("Streaming error:", err);
      initialEventSource.close();
      setIsLoading(false);
    };

    setEventSource(initialEventSource);

    return () => {
      if (initialEventSource) {
        initialEventSource.close();
        setIsLoading(false);
      }
    };
  }, [mergeGroup]);

  const sortedBuckets = Object.values(bucketMap).sort(
    (a, b) => parseInt(b.bucketLabel) - parseInt(a.bucketLabel)
  );

  return (
    <div className="news-list max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div className="mt-8">
        <form onSubmit={handleSubmit} className="mb-6 p-4 bg-white shadow-sm rounded-lg">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="keyword">Keyword</Label>
              <Input 
                id="keyword" 
                value={keyword} 
                onChange={(e) => setKeyword(e.target.value)} 
                placeholder="Enter search term" 
              />
            </div>
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="intervalType">Interval Type</Label>
              <Select value={intervalType} onValueChange={setIntervalType}>
                <SelectTrigger id="intervalType">
                  <SelectValue placeholder="Select interval" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="hours">Hours</SelectItem>
                  <SelectItem value="days">Days</SelectItem>
                  <SelectItem value="weeks">Weeks</SelectItem>
                  <SelectItem value="months">Months</SelectItem>
                </SelectContent>
              </Select>
            </div>
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="number">Number</Label>
              <Input 
                id="number" 
                type="number" 
                value={number} 
                onChange={(e) => setNumber(e.target.value)} 
                placeholder="Enter number" 
                min="1"
              />
            </div>
            <div className="flex flex-col space-y-1.5">
              <Label htmlFor="number">&nbsp;</Label>
              <Button type="submit" className="w-full">Search</Button>
            </div>
          </div>
        </form>

        <div className="flex items-center gap-2 mb-4">
          <h3 className="text-lg font-semibold">Search Results for "{keyword}"</h3>
          {isLoading && (
            <div className="flex items-center text-sm text-gray-500">
              <Loader className="animate-spin h-4 w-4 mr-1" />
              <span>loading</span>
            </div>
          )}
        </div>

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