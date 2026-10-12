<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Project Feedback & Ratings - SmartTeam AI</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<nav>
    <b>SmartTeam AI</b>
    <a href="/dashboard">Dashboard</a>
    <a href="/logout">Logout</a>
</nav>

<div class="container">
    <div class="card">
        <h1>Feedback & Contribution Ratings</h1>
        <h3>Project: ${project.name}</h3>
        <p>Status: <span class="status-badge status-completed">${project.status}</span></p>
        <p>Give feedback and rate the contribution of your fellow team members to help build their professional reputation for future projects.</p>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="ok">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="err">${errorMessage}</div>
    </c:if>

    <div class="card">
        <h2>Submit Teammate Rating</h2>
        <c:choose>
            <c:when test="${empty teammates}">
                <p>No other teammates found to rate for this project.</p>
            </c:when>
            <c:otherwise>
                <form method="post" action="/projects/${project.id}/rate">
                    <div class="form-group">
                        <label for="rate-target">Select Teammate to Rate</label>
                        <select id="rate-target" name="toStudentId" required>
                            <c:forEach var="t" items="${teammates}">
                                <option value="${t.id}">${t.name} (${t.department} - Year ${t.studyYear})</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="grid">
                        <div class="form-group">
                            <label for="rate-tech">Technical Contribution (1 to 5 Stars)</label>
                            <select id="rate-tech" name="technicalScore" required>
                                <option value="5" selected>5 ★★★★★ (Outstanding Code & Technical Skills)</option>
                                <option value="4">4 ★★★★☆ (Good Quality Output)</option>
                                <option value="3">3 ★★★☆☆ (Average Contribution)</option>
                                <option value="2">2 ★★☆☆☆ (Below Expectation)</option>
                                <option value="1">1 ★☆☆☆☆ (Minimal Contribution)</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="rate-teamwork">Teamwork & Communication (1 to 5 Stars)</label>
                            <select id="rate-teamwork" name="teamworkScore" required>
                                <option value="5" selected>5 ★★★★★ (Great Collaborator & Communicator)</option>
                                <option value="4">4 ★★★★☆ (Good Collaboration)</option>
                                <option value="3">3 ★★★☆☆ (Satisfactory)</option>
                                <option value="2">2 ★★☆☆☆ (Needs Improvement)</option>
                                <option value="1">1 ★☆☆☆☆ (Poor Communication)</option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="rate-reliability">Reliability & Deadlines (1 to 5 Stars)</label>
                            <select id="rate-reliability" name="reliabilityScore" required>
                                <option value="5" selected>5 ★★★★★ (Always Delivered on Time)</option>
                                <option value="4">4 ★★★★☆ (Mostly On Time)</option>
                                <option value="3">3 ★★★☆☆ (Delivered Eventually)</option>
                                <option value="2">2 ★★☆☆☆ (Often Late)</option>
                                <option value="1">1 ★☆☆☆☆ (Did Not Deliver)</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="rate-review">Contribution Review & Feedback Comments</label>
                        <textarea id="rate-review" name="review" placeholder="Describe how this teammate contributed, key features built, and work ethic..." required></textarea>
                    </div>

                    <button type="submit" class="btn-success">Submit Feedback & Rating</button>
                    <a href="/dashboard" class="btn btn-secondary">Back to Dashboard</a>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
