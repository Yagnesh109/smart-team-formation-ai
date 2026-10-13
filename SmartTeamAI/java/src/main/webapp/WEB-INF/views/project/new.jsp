<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Project - SmartTeam AI</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<nav>
    <a href="/dashboard" class="nav-brand">
        <b>SmartTeam AI</b>
    </a>
    <div class="nav-links">
        <a href="/dashboard" class="nav-link">Dashboard</a>
        <a href="/matching" class="nav-link">AI Teammate Finder</a>
        <a href="/projects/new" class="nav-link active">Create Project</a>
    </div>
    <div class="nav-right">
        <span class="nav-user">
            Welcome, <b>${not empty student.name ? student.name : (not empty sessionScope.studentName ? sessionScope.studentName : 'Student')}</b>
        </span>
        <a href="/logout" class="nav-logout">Logout</a>
    </div>
</nav>

<div class="card wide">
    <h1>Create a New Project</h1>
    <p>Define project scope, maximum team size, and skill requirements for recruiting collaborators.</p>

    <form method="post" action="/projects/new">
        <div class="form-group">
            <label for="proj-name">Project Name / Title</label>
            <input id="proj-name" name="name" placeholder="e.g. Smart Attendance System" required>
        </div>

        <div class="form-group">
            <label for="proj-desc">Detailed Description</label>
            <textarea id="proj-desc" name="description" placeholder="Describe the objectives, architecture, and scope of this project..." required></textarea>
        </div>

        <div class="grid">
            <div class="form-group">
                <label for="proj-size">Total Team Size (including owner)</label>
                <input id="proj-size" name="teamSize" type="number" min="2" max="12" value="4" required>
            </div>

            <div class="form-group">
                <label for="proj-skills">Required Technical Skills</label>
                <input id="proj-skills" name="requiredSkills" placeholder="e.g. Java, Spring Boot, React, MySQL" required>
            </div>
        </div>

        <div class="action-bar">
            <button type="submit" class="btn-success">Create Project</button>
            <a href="/dashboard" class="btn btn-secondary">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
