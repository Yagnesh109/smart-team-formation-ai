<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Project - SmartTeam AI</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<nav>
    <b>SmartTeam AI</b>
    <a href="/dashboard">Dashboard</a>
    <a href="/logout">Logout</a>
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
