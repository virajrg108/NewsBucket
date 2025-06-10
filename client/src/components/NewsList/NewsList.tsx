import axios from 'axios';
import { useEffect, useState } from 'react';
import { Accordion, AccordionItem, AccordionTrigger, AccordionContent } from "@/components/ui/accordion";
import { Card, CardAction, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { ChevronDown, ChevronRight, Clock, MoreHorizontal } from "lucide-react";
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

type Bucket = {
  [bucketLabel: string]: Article[];
};

const NewsList = () => {
  const [buckets, setBuckets] = useState<any[]>([]);
  const [isExpanded, setIsExpanded] = useState(false);

  useEffect(() => {
    console.log("Starting api request");
    axios
      .get('http://localhost:8081/api/news?keyword=apple&intervalType=days&number=12')
      .then((response) => {
        console.log(response.data);
        setBuckets(response.data);
      })
      .catch((error) => {
        console.error('Error fetching articles:', error);
      });
  }, []);

  return (
    <div className="news-list max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div className="mt-8">
        <h3 className="text-lg font-semibold mb-4">Search Results for "keyword"</h3>

        <Accordion type="single" collapsible className="mb-4 bg-white shadow-sm rounded-lg">
          {buckets.map((bucket, idx) => (

            <AccordionItem value={`item-${idx}`} key={idx} className='sm:p-2 md:p-4'>
              <AccordionTrigger>{bucket.timeRangeDisplay} ({bucket.articles?.length} articles)</AccordionTrigger>
              <AccordionContent>
                {bucket.articles.map((article: Article, idx: number) => {
                  return <Card key={article.url || idx} className="mb-4 hover:shadow-lg transition-all duration-300 bg-gray-50"> {/* Use a more stable key if possible */}
                    <CardContent> {/* Added pt-6 to mimic CardHeader padding if CardHeader is removed or content moved here */}
                      <div className="flex justify-between items-start">
                        <div className="flex-grow mr-4"> {/* Text content on the left */}
                          <CardTitle className="text-lg font-semibold mb-1">{article.title}</CardTitle>
                          <CardDescription className="text-sm text-muted-foreground mb-2 text-ellipsis overflow-hidden line-clamp-2">
                            {article.description}
                          </CardDescription>
                          <div className="flex flex-wrap items-center gap-x-2 gap-y-1">
                            <span className="text-xs">
                              By {article.author}
                            </span>

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
                          {/* <p className="text-sm text-muted-foreground text-ellipsis overflow-hidden line-clamp-2">{article.content}</p> Added line-clamp for brevity */}
                        </div>
                        {article.urlToImage && (
                          <img src={article.urlToImage} alt={article.title} className="w-20 h-20 object-cover rounded-md flex-shrink-0" /> /* Image on the right */
                        )}
                      </div>
                    </CardContent>
                  </Card>
                })}
              </AccordionContent>
            </AccordionItem>


          ))

          }
        </Accordion>
        {/* {groups.length === 0 && !isLoading && (
          <p className="text-sm text-muted-foreground">No articles found.</p>
        )} */}
      </div >
    </div >
  );
};

export default NewsList;