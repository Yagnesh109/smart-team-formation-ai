# Detailed Build and Run Guide

## Part A — XAMPP / MySQL

1. Open XAMPP Control Panel.
2. Start Apache and MySQL.
3. Open:
   http://localhost/phpmyadmin
4. Create/select database by running:
   database/schema.sql
5. Run:
   database/sample_data.sql
6. Check tables:
   students
   skills
   student_skills
   projects
   project_skills
   project_requests
   team_members
   ratings
   notifications

## Part B — Python ML service

Use Python 3.11.

Open VS Code in `ml-service`.

Windows:

py -3.11 -m venv venv
venv\Scripts\activate
pip install -r requirements.txt
python app.py

Expected:
Running on http://127.0.0.1:5000

Test in browser:
http://127.0.0.1:5000/health

Expected JSON:
{"status":"ok","service":"smart-team-ml"}

If `py -3.11` does not work, use the full Python 3.11 executable path.

Do not use Python 3.14 for the first setup if scikit-learn installation fails.

## Part C — NetBeans

1. Open NetBeans.
2. File -> Open Project.
3. Select the `java-web` folder.
4. Wait for Maven dependencies.
5. Open:
   DBConnection.java
6. Set MySQL username/password.
7. Confirm ML URL in:
   MLServiceClient.java
8. Add Tomcat 9 if not already configured.
9. Right-click project -> Clean and Build.
10. Right-click project -> Run.

Expected URL:
http://localhost:8080/SmartTeamFormationML/

## Part D — Login

Use sample:
aakash@example.com
1234

or:
aisha@example.com
1234

## Part E — Test ML matching

Project ID:
1

Project:
Smart Attendance System

Skills:
Java, Python, Machine Learning

Availability:
Monday, Saturday

Click Find Teammates.

The servlet:
- loads candidates from MySQL
- sends each candidate to Python
- receives a score
- sorts by score
- displays candidates

## Part F — Test invitation

Click Invite to Project.

The request is inserted into project_requests.

Verify:
SELECT * FROM project_requests;

## Common errors

### MySQL connection error
Check:
- XAMPP MySQL running
- database name
- username
- password
- port 3306

### 404
Clean and Build, then Run again.
Confirm context path.

### ML connection refused
Start:
python app.py

Confirm:
http://127.0.0.1:5000/health

### ModuleNotFoundError
Activate venv:
venv\Scripts\activate
pip install -r requirements.txt

### Maven compiler issue
The pom uses Java release 17. JDK 25 can compile it.

### Tomcat package issue
This project uses `javax.servlet` because it targets Tomcat 9, not Jakarta/Tomcat 10+.
