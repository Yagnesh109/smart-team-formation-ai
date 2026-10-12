# Smart Team Formation System — Advanced Java + ML

A standalone college project that combines Advanced Java web development with a Python Machine Learning service.

## Core idea

A student creates a project with required skills and availability. The Java web application sends candidate/project data to a Python ML service. The ML service calculates a compatibility score using:

- TF-IDF text similarity
- Cosine similarity
- Skill overlap
- Availability similarity
- Experience similarity
- Weighted final score

The Java application displays ranked teammates and supports project invitations.

## Technology stack

### Advanced Java
- JSP
- Servlets
- HTTP GET/POST
- HttpSession
- RequestDispatcher
- JDBC
- DAO pattern
- Maven
- Tomcat 9
- Java 17 source compatibility / JDK 25 runtime is supported
- Java HttpClient for Java → ML REST calls
- Gson for JSON

### Machine Learning
- Python 3.11 recommended
- Flask REST API
- pandas
- scikit-learn
- TF-IDF Vectorizer
- Cosine similarity
- Feature weighting / scoring

### Database
- MySQL / MariaDB through XAMPP
- Normalized tables
- Foreign keys
- Many-to-many student-skill and project-skill relationships

## Architecture

Browser
  |
  v
JSP pages
  |
  v
Servlet Controller
  |
  +----------------------+
  |                      |
  v                      v
DAO + JDBC           Python ML API
  |                      |
  v                      v
MySQL              scikit-learn
                         |
                         v
                  Match score
                         |
                         +----> Java Servlet ----> JSP

## Project flow

1. Student registers.
2. Student logs in.
3. Student adds skills and availability.
4. Student creates a project.
5. Student enters required skills.
6. Find Teammates calls StudentDAO to retrieve candidates.
7. Java calls Python `/predict-match`.
8. Python calculates ML compatibility.
9. Java sorts candidates by ML score.
10. Student sends an invitation.
11. Invitation is stored in `project_requests`.

## Why this is both Advanced Java and ML

The Java side owns:
- authentication
- session management
- CRUD
- database access
- project management
- requests
- web UI
- integration with an external REST service

The ML side owns:
- feature engineering
- TF-IDF
- cosine similarity
- similarity scoring
- candidate ranking

This is a strong separation of concerns for a college project.

## Recommended environment

Use:
- JDK 25
- NetBeans
- Maven
- Apache Tomcat 9
- XAMPP MySQL
- VS Code
- Python 3.11

Prefer Python 3.11 for the ML service. Python 3.14 may require newer versions of some ML packages.

## Folder structure

SmartTeamFormation-Advanced/
  java-web/
    pom.xml
    src/main/java/com/smartteam/...
    src/main/webapp/...
  ml-service/
    app.py
    requirements.txt
  database/
    schema.sql
    sample_data.sql
  docs/
    ARCHITECTURE.md
    BUILD_GUIDE.md
    VIVA_TOPICS.md

## Quick start

### 1. Database

Start MySQL from XAMPP.

Open phpMyAdmin and run:
database/schema.sql
then:
database/sample_data.sql

### 2. ML service

Open VS Code terminal:

cd ml-service
py -3.11 -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
python app.py

ML service:
http://127.0.0.1:5000

Health check:
http://127.0.0.1:5000/health

### 3. Java web app

Open `java-web` as a Maven project in NetBeans.

Check DB credentials in:
`src/main/java/com/smartteam/util/DBConnection.java`

Default:
host = localhost
port = 3306
database = smart_team_ml_db
user = root
password = empty

Run:
Clean and Build
then Run

Application:
http://localhost:8080/SmartTeamFormationML/

### 4. Test

Login with sample users:
aakash@example.com / 1234
aisha@example.com / 1234
aditya@example.com / 1234

Open:
Find Teammates

Project ID 1 is included in sample data.

Enter:
Project ID: 1
Project Name: Smart Attendance System
Required Skills: Java, Python, Machine Learning
Availability: Monday, Saturday

The Java app will call the ML service and display match scores.

## Important

If port 5000 is busy, change Flask port in `ml-service/app.py` and update:
`MLServiceClient.java`

If MySQL uses a password, update `DBConnection.java`.

This package is designed as a clean standalone project so it does not overwrite your existing `SmartTeamFormation` project.
