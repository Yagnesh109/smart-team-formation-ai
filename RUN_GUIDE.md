# Smart Team Formation System — Complete Execution & Run Guide

This guide provides step-by-step instructions for setting up, running, and verifying all root folders of the **Smart Team Formation System** (Spring Boot + Machine Learning).

---

## 📁 Root Folder Overview

| Folder Path | Purpose / Description | Port / Executable |
| :--- | :--- | :--- |
| **`SmartTeamAI/sql/`** | Database scripts (`smart_team_ai.sql`) | MySQL (`3306`) |
| **`SmartTeamAI/java/`** | **Spring Boot 3 Java Backend** (JPA, Servlets/JSP, Controllers, Services) | Spring Boot (`8085`) |
| **`ml/`** | **Unified Python ML Microservice** (FastAPI, Soft Voting Ensemble, TF-IDF, K-Means, Scikit-learn) | REST API (`8000`) |

---

## 🗄️ Step 1: Database Setup (MySQL via XAMPP)

1. Open **XAMPP Control Panel** and start **MySQL**.
2. Open phpMyAdmin in your browser: `http://localhost/phpmyadmin`
3. Create a database named `smart_team_ai` (or select SQL tab).
4. Import / Execute the SQL script file:
   - [SmartTeamAI/sql/smart_team_ai.sql](file:///c:/Users/new/Documents/SmartTeamFormation-Advanced/SmartTeamAI/sql/smart_team_ai.sql)

*Database Name*: `smart_team_ai`  
*Default User*: `root`  
*Default Password*: *(empty)*  

---

## 🤖 Step 2: Run Unified Python Machine Learning Microservice (`ml`)

The unified Python ML service provides teammate compatibility prediction using the Soft Voting Ensemble classifier, K-Means clustering, and TF-IDF similarity.

### Run via Windows PowerShell / Terminal:
```powershell
# 1. Navigate to the ml folder from workspace root
cd ml

# 2. Activate virtual environment
.\.venv\Scripts\activate

# 3. (Optional) Re-train models if needed
python train_models.py

# 4. Start unified FastAPI server
uvicorn main:app --reload --port 8000
```

- **ML API Endpoint**: `http://127.0.0.1:8000`
- **Health Check**: `http://127.0.0.1:8000/health`
- **Interactive Swagger Documentation**: `http://127.0.0.1:8000/docs`

---

## 🍃 Step 3: Run Spring Boot Java Backend (`SmartTeamAI/java`)

This is the primary Spring Boot implementation featuring:
- Personalized Dashboard stats (My Projects, Collaborating Projects, Total Collaborators, Average Rating)
- Project directory with Collaborators & Member Role badges
- Interactive filtering ("My Projects", "Collaborating Projects", "All")
- Project completion workflows for project creators
- Invitation system & Join Requests with Accept/Decline options
- Peer contribution ratings and feedback history

### Method A: Run via Maven (Terminal):
```powershell
# 1. Navigate to SmartTeamAI/java folder from workspace root
cd SmartTeamAI/java

# 2. Compile and run Spring Boot application
C:\netbeans\java\maven\bin\mvn.cmd spring-boot:run
```

### Method B: Run via IDE (NetBeans / IntelliJ / Eclipse):
1. Open the folder `SmartTeamAI/java` as a Maven project.
2. Locate the Spring Boot application main class:
   `src/main/java/com/smartteam/SmartTeamAiApplication.java`
3. Right-click `SmartTeamAiApplication.java` ➔ Select **Run File** (or **Debug**).

---

## 🌐 Step 4: Access Application in Browser

- **Application URL**: [http://localhost:8085/](http://localhost:8085/)
- **Login Page**: [http://localhost:8085/login](http://localhost:8085/login)
- **Registration Page**: [http://localhost:8085/register](http://localhost:8085/register)

### Test Workflow:
1. **Register/Login** as a student.
2. View **Personalized Dashboard** (`/dashboard`):
   - Check stats: *My Projects*, *Collaborating Projects*, *Total Collaborators*, *Average Rating*.
   - Filter projects using the *All Projects*, *My Projects*, or *Collaborating Projects* tabs.
3. **Create a Project** (`/projects/new`):
   - Enter title, description, team size limit, and required skills.
4. **Invite Collaborators & Accept Requests**:
   - Invite candidate students to your project from the Dashboard or AI Teammate Finder (`/matching`).
   - Log in as the invited student to **Accept** or **Decline** the invitation under *Pending Requests*.
5. **Complete Project & Submit Ratings**:
   - As project owner, click **Mark Project as Completed**.
   - Click **Rate & Feedback Teammates** to rate teammate contributions (Technical, Teamwork, Reliability) and submit feedback.

---

## 🔍 System Verification Summary

| Component | Port | Technology Stack | Location |
| :--- | :--- | :--- | :--- |
| **MySQL Database** | `3306` | MySQL / MariaDB via XAMPP | `SmartTeamAI/sql/smart_team_ai.sql` |
| **Python ML API** | `8000` | FastAPI, scikit-learn, Soft Voting Ensemble | `ml/main.py` |
| **Spring Boot Backend** | `8080` | Spring Boot 3, JPA, Hibernate, JSP | `SmartTeamAI/java/` |
