# SmartTeam AI — Intelligent Student Project Team Formation System

An advanced college project combining Advanced Java + Machine Learning.

## 1. What this project does

Students create profiles with skills, interests, experience and availability. A student creates a project and enters required skills. The system uses a Java/Spring application for authentication, database CRUD and web pages, then calls a Python ML service to estimate teammate compatibility. Results are sorted into a recommendation list.

## 2. Recommended stack for this project

- Java 25
- Spring Boot 3.5.16
- Spring MVC
- Spring Data JPA / Hibernate ORM
- JSP + JSTL
- Jakarta Servlet
- JDBC via JdbcTemplate
- MySQL from XAMPP
- Maven
- Python 3.11 for ML
- FastAPI
- pandas, NumPy, scikit-learn, joblib

Spring Boot 3.5.16 supports Java 25. JSP is supported when the application is packaged as a WAR; this project is therefore configured as a WAR. See the official Spring documentation before changing the Spring version.

## 3. Architecture

Browser/JSP
   |
   v
Spring MVC Controllers
   |
   +--> Session Authentication
   |
   +--> Service Layer
   |       |
   |       +--> Matching Service ----HTTP----> Python FastAPI ML Service
   |       |                                  |
   |       |                                  +--> Ensemble Classifier
   |       |                                  +--> K-Means clustering
   |       |
   |       +--> Project Service
   |
   +--> DAO/JDBC ----> MySQL
   |
   +--> JPA/Hibernate ----> MySQL

Java side: UI + web workflow + security/session + business logic + ORM/JDBC.
Python side: model training + validation + prediction API.

## 4. Folder structure

SmartTeamAI/
├── java/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/smartteam/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dao/
│       │   ├── entity/
│       │   ├── repository/
│       │   ├── service/
│       │   └── servlet/
│       ├── resources/application.properties
│       └── webapp/WEB-INF/views/
├── ml/
│   ├── main.py
│   ├── train_models.py
│   └── requirements.txt
├── sql/smart_team_ai.sql
└── README.md

## 5. Step-by-step setup on Windows

### STEP 1 — Verify Java

In Command Prompt:

```text
java -version
javac -version
```

Use Java 25 for this project. Your JAVA_HOME should point to the JDK 25 installation.

### STEP 2 — Verify Maven

```text
mvn -version
```

If Maven is already configured through NetBeans, you can also build from NetBeans. If `mvn` is not recognized in Command Prompt, use the Maven executable configured by NetBeans or add Maven's bin directory to PATH.

### STEP 3 — Start MySQL using XAMPP

Open XAMPP Control Panel and start **MySQL**.

Apache is not required for the Spring Boot application because Spring Boot runs the Java web application with Tomcat. XAMPP is being used here mainly for MySQL/phpMyAdmin.

### STEP 4 — Create the database

Open phpMyAdmin or MySQL console and run:

```sql
CREATE DATABASE smart_team_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Then import `sql/smart_team_ai.sql`.

The database intentionally has a unique name so it does not conflict with an older college project.

### STEP 5 — Check database credentials

Open:

`java/src/main/resources/application.properties`

Change:

```properties
spring.datasource.username=root
spring.datasource.password=
```

If your XAMPP MySQL root user has a password, enter it after `password=`.

### STEP 6 — Open Java project in NetBeans

1. NetBeans -> File -> Open Project.
2. Select the `java` folder.
3. Let Maven resolve dependencies.
4. Right-click project -> Clean and Build.
5. The expected artifact is:

`java/target/smart-team-ai.war`

If you use Spring Boot directly, the WAR can be run as an executable WAR after a successful Maven build.

### STEP 7 — Start the ML service

Open a separate VS Code terminal:

```powershell
cd SmartTeamAI\ml
py -3.11 -m venv .venv
.venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
pip install -r requirements.txt
python train_models.py
uvicorn main:app --reload --port 8000
```

If PowerShell blocks activation, use Command Prompt and run:

```text
.venv\Scripts\activate.bat
```

The ML service should be available at:

`http://127.0.0.1:8000`

FastAPI's interactive documentation is at `/docs`.

### STEP 8 — Start the Java application

From the `java` folder, after Maven is available:

```text
mvn spring-boot:run
```

Or run the main class from NetBeans:

`com.smartteam.SmartTeamAiApplication`

Open:

`http://localhost:8080`

### STEP 9 — Test the complete flow

1. Register Student A.
2. Register Student B with Python/ML interests in the profile text.
3. Register Student C with UI/UX interests.
4. Login as Student A.
5. Create a project such as `AI Attendance System`.
6. Enter required skills: `Java, Python, Machine Learning, UI/UX`.
7. Open `AI Matching`.
8. Enter the same skills, an interest such as `AI/ML`, an availability such as `Weekends`, and experience level.
9. Submit.
10. Java sends candidate features to FastAPI.
11. The ensemble ML model returns a probability.
12. Java displays the final match score.

## 6. ML pipeline

Student/project data -> feature engineering -> train/test split -> model training -> 5-fold cross-validation -> confusion matrix/accuracy -> ensemble -> joblib model -> FastAPI -> Java RestClient -> JSP recommendation page.

### Models included

- Logistic Regression
- KNN
- Decision Tree
- SVM
- Naive Bayes
- Random Forest
- Soft Voting Ensemble
- K-Means clustering

This maps directly to the supplied Machine Learning syllabus: classification, common classifiers, ensemble models, validation, bias/variance discussion, model evaluation, training/testing and unsupervised learning.

## 7. Advanced Java syllabus mapping

### Unit I — JDBC

- MySQL database
- JdbcTemplate/SQL DAO
- CRUD foundation
- Database statistics

### Unit II — Servlets and Session Management

- Jakarta Servlet `LegacyHealthServlet`
- GET/POST controllers
- HttpSession login state
- Request routing

### Unit III — JSP

- JSP views
- JSP directives
- JSTL tag library
- JSP expression language
- Dynamic dashboard and matching result pages

### Unit IV — Spring Framework

- Spring Boot
- Dependency Injection
- Spring MVC
- Controllers
- Service layer
- REST endpoint
- WAR deployment

### Unit V — Hibernate

- JPA entities
- Hibernate ORM
- Repository abstraction
- Student/Project relationships
- CRUD persistence

## 8. Features to add for the final advanced version

The supplied code is a strong foundation. For the final college demonstration, add these modules in this order:

Phase 2:
- StudentSkill entity and skill-level CRUD
- ProjectSkill entity and required skill level
- Invite/request-to-join workflow
- TeamMember entity
- Notifications

Phase 3:
- Admin dashboard
- Report user/project
- User activation/deactivation
- Project moderation
- Statistics charts

Phase 4:
- Rating and reputation module
- Completed-project history
- Explainable match breakdown
- K-Means profile segmentation dashboard
- ML metrics dashboard showing accuracy, confusion matrix and cross-validation

Phase 5:
- Spring Security + role-based access
- REST API documentation
- Unit/integration tests
- Audit logs
- Docker deployment (optional for presentation)

## 9. Suggested final UI

1. Landing/Login
2. Student Registration
3. Student Dashboard
4. Edit Profile
5. Skill Management
6. Create Project
7. Project Details
8. AI Teammate Finder
9. Match Explanation page
10. Requests/Invitations
11. My Team
12. Ratings
13. Notifications
14. Admin Dashboard
15. ML Model Metrics

## 10. Viva explanation of the AI score

The project should not claim that a match percentage is an objective measure of a person's ability. Present it as a model-generated compatibility estimate based on the selected features.

Example feature vector:

[skill_match, interest_match, availability, experience, communication, reliability]

The trained classifier estimates the probability that the candidate belongs to the compatible class. The Java application converts that probability into a presentation-friendly percentage and combines it with a deterministic skill score.

## 11. Presentation title

**SmartTeam AI: Intelligent Project Team Formation and Skill-Based Collaboration Platform**

Subtitle:
**An Advanced Java + Machine Learning Web Application**
