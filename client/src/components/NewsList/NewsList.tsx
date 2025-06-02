import axios from 'axios';
import { useEffect, useState } from 'react';

export interface Article {
  author: string | null;
  title: string;
  description: string;
  url: string;
  urlToImage: string;
  publishedAt: string;
  content: string;
}


const NewsList = () => {
  const [articles, setArticles] = useState<Article[]>([]);

  useEffect(() => {
    axios
      .get("http://localhost:8081/api/news?keyword=apple")
      .then((response) => {
        console.log(response.data);
        setArticles(response.data);
      })
      .catch((error) => {
        console.error("Error fetching articles:", error);
      });
  }, []);

  return (
    <div className="news-list">
      {
      articles.map((article: Article, index) => (
        <div key={index} className="news-card">
          <h2>{article.title}</h2>
          <p><strong>Author:</strong> {article.author || 'Unknown'}</p>
          <p><strong>Published At:</strong> {new Date(article.publishedAt).toLocaleString()}</p>
          {article.urlToImage && (
            <img src={article.urlToImage} alt={article.title} width="300" />
          )}
          <p>{article.description}</p>
          <a href={article.url} target="_blank" rel="noopener noreferrer">Read more</a>
        </div>
      ))
      }
    </div>
  );
};

export default NewsList;