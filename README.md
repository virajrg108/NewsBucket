# 📰 NewsBucket - Documentation

### Try it out 🔗 <br>
http://ec2-13-60-85-4.eu-north-1.compute.amazonaws.com:3000/<br>
OR<br>
http://13.60.85.4:3000/

### Github Repo :
https://github.com/virajrg108/newsbucket 

## Overview

**NewsBucket** is a modern web application that allows users to stream news articles in real-time based on their selected topic and time interval. It is built using **React** and **Spring Boot**, following reactive principles to ensure fast and seamless user experience.

---

## To build locally
  - Install Java, Maven, NodeJS and docker.
  - Inside client folder run:
  ```
    docker build --no-cache -t client:latest .
  ```
  - Inside news-service folder run:
  ```
    docker build --no-cache -t news-service:latest .
  ```
  - Then do docker compose:
  ```
    docker-compose up --build 
  ```
  - UI will be served on localhost:3000
  - backend is running on localhost:8081

---

## ⚙️ Tech Stack

| Layer         | Technology              |
|---------------|--------------------------|
| Frontend      | [React](https://reactjs.org/), [ShadCN UI](https://ui.shadcn.com/) |
| Backend       | [Spring Boot](https://spring.io/projects/spring-boot), [Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) |
| Communication | Server-Sent Events (SSE)|
| Containerization | [Docker](https://www.docker.com/) |
| CI/CD         | [Jenkins](https://www.jenkins.io/), GitHub Webhooks |
| Hosting       | AWS EC2, ECR (Elastic Container Registry) |
| Data Source   | [NewsAPI.org](https://newsapi.org/) |

---

## 🚀 Key Features

### ✅ Reactive News Streaming
Instead of fetching and returning all the data at once, NewsBucket streams articles reactively to the frontend in **real-time**.

- **Why Reactive?**  
  NewsAPI provides limited results per request. To fetch all articles, we must use `page=2`, `page=3`, etc.  
  A traditional `for` loop approach would have delayed the complete response, making users wait for the entire data to load.

- **Solution**  
  Using **Spring WebFlux** on the backend and **Server-Sent Events (SSE)** on the frontend, data is streamed to the client **as it arrives** from NewsAPI, offering a fast and responsive experience.

### ✅ Clean Component-Based UI
- Built with **ShadCN UI** component library for accessible, customizable, and aesthetic UI components.

### ✅ Full Containerization
- Backend (`news-service`) and frontend (`client`) are containerized using Docker.
- Docker Compose is used for orchestrating services.

### ✅ CI/CD Integration
- Integrated **Jenkins** pipeline that builds and pushes Docker images upon merging the `develop` branch into `main`.
- **GitHub Webhooks** are used to trigger Jenkins builds automatically.

---

## 📁 Project Structure
newsbucket/

├── client/ # React frontend using Vite + ShadCN UI

├── news-service/ # Spring Boot backend with WebFlux

├── Jenkinsfile # CI/CD pipeline script

├── docker-compose.yml # Container orchestration

<br>
<br>

## 🔮 Future Scope

Here are several potential enhancements planned for the future:

1. **Caching Mechanism for Resilience**
   - Implement a caching layer to serve **stale data** when NewsAPI.org is down or rate-limited.
   - This ensures uninterrupted user experience even during third-party API outages.

2. **Estimated Reading Time**
   - Calculate estimated **reading time** for each article based on:
     - Character count of the `content` field.
     - An average reading speed (e.g., 200–250 words per minute).
   - Display this in the UI to help users manage their reading.

3. **Authentication & Authorization**
   - Integrate user login and role-based access using tools like **JWT**, **OAuth2**, or third-party providers (Google, GitHub, etc.).
   - Enables personalisation and secure access to features like bookmarks, history, and preferences.

4. **Server-Side Rendering (SSR)**
   - Add SSR support using frameworks like **Next.js** (if migrating frontend) or **React SSR** to allow **SEO crawlers** to index the content effectively.
   - This will significantly improve the site’s **search engine visibility**.

5. **Personalized News Feed**
   - With authentication in place, track user’s previous searches or clicked articles.
   - When users revisit or don’t specify any keyword, automatically show them **personalized article recommendations** based on their reading habits.
6. **Microservices architechture**
   - Above enhancements requires us to build separate services which calls for microservice architechtures like API Gateway, service discovery, circuit breaker, etc.


## 🔮 Future "Future" Scope

### 🧠 Intelligence & UX Enhancements

1. **🗞️ Keyword Trends & Analytics**
   - Display trending keywords based on recent user searches.
   - Visualize keyword search frequency using charts.

2. **📌 Article Bookmarking**
   - Allow users to save/bookmark articles.
   - Enable offline access to bookmarked articles.

3. **📄 Article Summarization**
   - Use AI-based models to generate short summaries of articles.
   - Helps users quickly understand content without reading the full article.

4. **🗣️ Voice Search Integration**
   - Integrate voice search functionality via browser APIs or external services.




---

### ⚙️ Technical Enhancements

5. **🧪 Testing Improvements**
   - Frontend: Add end-to-end tests using Cypress.
   - Backend: Use JUnit and WebTestClient for reactive flow testing or BDD.
   - Include test coverage report in CI and README.

6. **📦 CI/CD Enhancements**
   - Use GitHub Actions for linting, testing, and build verification.
   - Create a staging pipeline before production deployment.

7. **📈 Monitoring & Observability**
   - Integrate with ELK Stack (Elasticsearch, Logstash, Kibana) for logs.
   - Use Prometheus + Grafana to track system metrics and health.
   - Add Spring Boot Actuator for health check endpoints.



---

### 🌍 Scalability & Distribution

8. **📤 Pagination & Infinite Scroll**
   - Implement infinite scroll for seamless article loading.

9. **🌐 Multi-language Support**
    - Support article translation for multilingual users.
    - Add i18n support in the frontend.

10. **📡 Web Push Notifications**
    - Notify users about new articles or updates on followed topics.

11. **🗃️ News Source & Category Filters**
    - Enable filtering based on news source or category (e.g., Sports, Tech, Health).


12. **🧑‍💼 User Dashboard**
    - Display user’s past searches, bookmarks, and reading history.

14. **🧩 Dark Mode**
    - Add a dark/light theme toggle using Tailwind CSS and ShadCN components.

15. **📊 Article Voting or Reactions**
    - Let users upvote/downvote articles or react with emojis (👍, 👎, 🔥).