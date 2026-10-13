<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>AI Teammate Finder - SmartTeam AI</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<nav>
    <a href="/dashboard" class="nav-brand">
        <b>SmartTeam AI</b>
    </a>
    <div class="nav-links">
        <a href="/dashboard" class="nav-link">Dashboard</a>
        <a href="/matching" class="nav-link active">AI Teammate Finder</a>
        <a href="/projects/new" class="nav-link">Create Project</a>
    </div>
    <div class="nav-right">
        <span class="nav-user">
            Welcome, <b>${not empty student.name ? student.name : (not empty sessionScope.studentName ? sessionScope.studentName : 'Student')}</b>
        </span>
        <a href="/logout" class="nav-logout">Logout</a>
    </div>
</nav>

<div class="container">
    <div class="card">
        <h1>AI Teammate Finder</h1>
        <p>Find compatible student candidates using AI feature matching based on skills, availability, and experience.</p>

        <form method="post" action="/matching">
            <div class="grid">
                <div class="form-group full">
                    <label for="match-skills">Required Skills (comma-separated)</label>
                    <input id="match-skills" name="requiredSkills" value="${requiredSkills}" placeholder="e.g. Java, Python, Machine Learning, UI/UX" required>
                </div>

                <div class="form-group">
                    <label for="match-interest">Primary Project Interest / Domain</label>
                    <input id="match-interest" name="interest" value="${interest}" placeholder="e.g. AI/ML, Web Development" required>
                </div>

                <div class="form-group">
                    <label for="match-availability">Preferred Availability</label>
                    <input id="match-availability" name="availability" value="${availability}" placeholder="e.g. Weekends" required>
                </div>

                <div class="form-group full">
                    <label for="match-experience">Target Experience Level</label>
                    <select id="match-experience" name="experience">
                        <option value="Beginner" ${experience == 'Beginner' ? 'selected' : ''}>Beginner</option>
                        <option value="Intermediate" ${experience == 'Intermediate' || empty experience ? 'selected' : ''}>Intermediate</option>
                        <option value="Advanced" ${experience == 'Advanced' ? 'selected' : ''}>Advanced</option>
                    </select>
                </div>
            </div>

            <button type="submit" class="full btn-success">Find Compatible Teammates</button>
        </form>
    </div>

    <c:if test="${not empty results}">
        <h2>AI Recommended Candidates</h2>
        <c:forEach var="r" items="${results}">
            <div class="match">
                <div>
                    <h3>${r.name}</h3>
                    <p><b>${r.department}</b> · ${r.experience} Level</p>
                </div>

                <div class="score">
                    <b>${r.matchScore}%</b>
                    <small>AI Match</small>
                </div>

                <div>
                    <div class="mini">Skill Match: ${r.skillMatch}%</div>
                    <div class="mini">ML Score: ${r.mlScore}%</div>

                    <c:if test="${not empty myProjects}">
                        <form method="post" action="/projects/${myProjects[0].id}/invite" style="margin-top: 8px;">
                            <input type="hidden" name="candidateId" value="${r.studentId}">
                            <div class="form-group" style="margin-bottom: 4px;">
                                <label for="invite-proj-${r.studentId}" style="font-size:11px; margin:0;">Invite to Project:</label>
                                <select id="invite-proj-${r.studentId}" onchange="this.form.action='/projects/' + this.value + '/invite'" style="padding:4px; font-size:12px; margin:2px 0;">
                                    <c:forEach var="mp" items="${myProjects}">
                                        <option value="${mp.id}">${mp.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-sm btn-success">Send Invite</button>
                        </form>
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </c:if>
</div>
</body>
</html>
