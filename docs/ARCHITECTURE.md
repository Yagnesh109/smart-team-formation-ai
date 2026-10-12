# Architecture and Module Mapping

## High-level architecture

+--------------------------+
|       Web Browser        |
| JSP / HTML / CSS         |
+------------+-------------+
             |
             | HTTP GET/POST
             v
+--------------------------+
| Apache Tomcat 9          |
| Java Web Application     |
|                          |
| JSP -> Servlet           |
|        |                 |
|        +--> Service      |
|        |                 |
|        +--> DAO -> JDBC  |
|        |          |      |
|        |          v      |
|        |       MySQL     |
|        |                 |
|        +--> ML Client   |
+--------+-----------------+
         |
         | HTTP JSON
         v
+--------------------------+
| Python Flask ML Service  |
|                          |
| Feature Engineering      |
| TF-IDF                   |
| Cosine Similarity        |
| Weighted Score           |
+--------------------------+

## Java modules

### Model
Student, Teammate, ProjectRequest.

### DAO
StudentDAO, ProjectDAO, ProjectRequestDAO.

DAO keeps SQL away from JSP and Servlets.

### Servlet
LoginServlet
RegisterServlet
FindTeammatesServlet
SendRequestServlet

Servlets receive HTTP requests and create responses.

### Utility
DBConnection
MLServiceClient

DBConnection manages JDBC connection creation.
MLServiceClient integrates Java with Python ML.

### JSP
login.jsp
register.jsp
dashboard.jsp
find-teammates.jsp
request-success.jsp

## ML pipeline

Input:
- project name
- required skills
- preferred availability
- candidate skills
- candidate interests
- candidate availability
- candidate experience

Feature 1: text similarity
- combine project requirements and candidate profile text
- TF-IDF transforms text into vectors
- cosine similarity gives semantic/text similarity

Feature 2: skill overlap
- exact normalized skill intersection / required skill count

Feature 3: availability
- compare normalized availability tokens

Feature 4: experience
- map Beginner, Intermediate, Advanced to numeric values

Final score:

final =
  0.50 * skill_score +
  0.25 * text_similarity +
  0.15 * availability_score +
  0.10 * experience_score

The score is converted to a percentage.

## Where Advanced Java is demonstrated

1. Servlet lifecycle
2. JSP
3. Session management
4. GET/POST
5. Request parameters
6. RequestDispatcher / redirects
7. JDBC PreparedStatement
8. DAO pattern
9. Maven dependency management
10. Java HTTP client
11. JSON serialization/deserialization
12. Tomcat deployment
13. MVC-style separation

## Where ML is demonstrated

1. Data preprocessing
2. Text normalization
3. TF-IDF
4. Cosine similarity
5. Feature engineering
6. Weighted scoring
7. Ranking
8. REST model/service integration

## How Java and ML are combined

Java does not execute Python code inside the servlet.

Instead:

FindTeammatesServlet
    |
    v
StudentDAO gets candidates from MySQL
    |
    v
MLServiceClient creates JSON
    |
    v
POST http://127.0.0.1:5000/predict-match
    |
    v
Python Flask
    |
    v
scikit-learn scoring
    |
    v
JSON response
    |
    v
Java servlet
    |
    v
JSP results

This is easier to explain, debug and demonstrate than embedding Python inside Java.
