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
        <div style="display:flex; justify-content:space-between; align-items:center; margin-top:24px;">
            <h2>AI Recommended Candidates</h2>
            <span style="font-size:12px; color:#627d98;">Trained on Kaggle Dynamic Student-Project Matching & Collaboration Benchmarks</span>
        </div>
        <c:forEach var="r" items="${results}">
            <div class="match" style="padding: 20px; border-left: 5px solid ${r.matchScore >= 70 ? '#38ef7d' : (r.matchScore >= 50 ? '#3b82f6' : '#cbd5dc')};">
                <div>
                    <div style="display:flex; align-items:center; gap:8px; flex-wrap:wrap; margin-bottom:4px;">
                        <h3 style="margin:0;">${r.name}</h3>
                        <c:if test="${not empty r.archetype}">
                            <span style="background:#e0e7ff; color:#3730a3; padding:2px 8px; border-radius:12px; font-size:11px; font-weight:600;">
                                ✦ ${r.archetype}
                            </span>
                        </c:if>
                        <c:choose>
                            <c:when test="${r.label == 'HIGHLY_COMPATIBLE'}">
                                <span style="background:#dcfce7; color:#166534; padding:2px 8px; border-radius:12px; font-size:11px; font-weight:700;">
                                    HIGH COMPATIBILITY
                                </span>
                            </c:when>
                            <c:when test="${r.label == 'COMPATIBLE'}">
                                <span style="background:#fef3c7; color:#92400e; padding:2px 8px; border-radius:12px; font-size:11px; font-weight:700;">
                                    COMPATIBLE
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span style="background:#f1f5f9; color:#475569; padding:2px 8px; border-radius:12px; font-size:11px; font-weight:600;">
                                    MODERATE FIT
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <p style="margin:4px 0 6px 0; color:#4a5568;">
                        <b>${r.department}</b> · Year ${r.studyYear} · ${r.experience} Level · ${r.availability}
                    </p>
                    <c:if test="${not empty r.archetypeDescription}">
                        <p style="margin:0; font-size:12px; color:#64748b; font-style:italic;">
                            "${r.archetypeDescription}"
                        </p>
                    </c:if>
                </div>

                <div class="score">
                    <b>${r.matchScore}%</b>
                    <small>Overall AI Match</small>
                </div>

                <div>
                    <div class="mini"><b>Skill Match:</b> ${r.skillMatch}%</div>
                    <div class="mini"><b>ML Compatibility:</b> ${r.compatibilityScore}%</div>
                    <div class="mini"><b>Teamwork / Comm:</b> ${r.communicationScore}%</div>
                    <div class="mini"><b>Reliability:</b> ${r.reliabilityScore}%</div>

                    <c:if test="${not empty myProjects}">
                        <form method="post" action="/projects/${myProjects[0].id}/invite" style="margin-top: 10px;">
                            <input type="hidden" name="candidateId" value="${r.studentId}">
                            <div class="form-group" style="margin-bottom: 4px;">
                                <label for="invite-proj-${r.studentId}" style="font-size:11px; margin:0;">Invite to Project:</label>
                                <select id="invite-proj-${r.studentId}" onchange="this.form.action='/projects/' + this.value + '/invite'" style="padding:4px; font-size:12px; margin:2px 0;">
                                    <c:forEach var="mp" items="${myProjects}">
                                        <option value="${mp.id}">${mp.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-sm btn-success" style="width:100%;">Send Invite</button>
                        </form>
                    </c:if>
                </div>
            </div>
        </c:forEach>
    </c:if>

    <c:if test="${empty results and not empty requiredSkills}">
        <div class="card" style="margin-top:24px; text-align:center; padding:32px; border-left:4px solid #f59e0b;">
            <h3 style="color:#b45309; margin-top:0;">No Matching Candidates Found</h3>
            <p style="color:#64748b; margin-bottom:0;">
                No candidate profiles currently match your search criteria. Try broadening required skills or adjusting availability.
            </p>
        </div>
    </c:if>
</div>
</body>
</html>
