<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Dashboard - SmartTeam AI</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<nav>
    <b>SmartTeam AI</b>
    <span>Welcome, ${student.name}</span>
    <a href="/matching">AI Teammate Finder</a>
    <a href="/projects/new">Create Project</a>
    <a href="/logout">Logout</a>
</nav>

<div class="container">
    <h1>Personalized Student Dashboard</h1>

    <c:if test="${not empty successMessage}">
        <div class="ok">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="err">${errorMessage}</div>
    </c:if>

    <!-- Requirement 1: Personalized Dashboard Stats -->
    <div class="stats">
        <div>
            <b>${stats.myProjectsCount}</b>
            <small>My Projects Created</small>
        </div>
        <div>
            <b>${stats.collaboratingProjectsCount}</b>
            <small>Collaborating Projects</small>
        </div>
        <div>
            <b>${stats.totalCollaboratorsCount}</b>
            <small>Total Collaborators</small>
        </div>
        <div>
            <b>★ ${stats.avgRating} / 5.0</b>
            <small>Rating (${stats.ratingsCount} reviews)</small>
        </div>
    </div>

    <!-- Student Profile summary -->
    <div class="card">
        <h2>My Profile & Skills</h2>
        <p><b>${student.department}</b> · Year ${student.studyYear} · <span>${student.experienceLevel} Level</span></p>
        <p><b>Interests:</b> ${student.interests}</p>
        <p><b>Availability:</b> ${student.availability}</p>
        <p>${student.bio}</p>
    </div>

    <!-- Requirement 5 & 6: Pending Invitations & Join Requests -->
    <c:if test="${not empty pendingRequests}">
        <div class="card" style="border-left: 4px solid #123b4a;">
            <h2>Pending Requests & Invitations</h2>
            <c:forEach var="req" items="${pendingRequests}">
                <div class="request-card">
                    <c:choose>
                        <c:when test="${req.requestType == 'INVITATION'}">
                            <h4>Project Invitation from ${req.requestedBy.name}</h4>
                            <p>You have been invited to join project <b>${req.project.name}</b>.</p>
                            <c:if test="${not empty req.message}"><p><i>"${req.message}"</i></p></c:if>

                            <form method="post" action="/projects/requests/${req.id}/respond" class="action-bar">
                                <input type="hidden" name="accept" value="true">
                                <div class="form-group" style="margin: 0;">
                                    <label for="role-${req.id}" style="display:inline; margin-right:6px;">Your Role:</label>
                                    <input id="role-${req.id}" name="roleName" value="Collaborator" style="width:160px; display:inline;" required>
                                </div>
                                <button type="submit" class="btn-sm btn-success">Accept Invitation</button>
                            </form>
                            <form method="post" action="/projects/requests/${req.id}/respond" style="display:inline; margin-top:6px;">
                                <input type="hidden" name="accept" value="false">
                                <button type="submit" class="btn-sm btn-danger">Decline</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <h4>Join Application from ${req.requestedBy.name}</h4>
                            <p>Student <b>${req.requestedBy.name}</b> (${req.requestedBy.department}) requested to join your project <b>${req.project.name}</b>.</p>
                            <c:if test="${not empty req.message}"><p><i>"${req.message}"</i></p></c:if>

                            <form method="post" action="/projects/requests/${req.id}/respond" class="action-bar">
                                <input type="hidden" name="accept" value="true">
                                <div class="form-group" style="margin: 0;">
                                    <label for="app-role-${req.id}" style="display:inline; margin-right:6px;">Assign Role:</label>
                                    <input id="app-role-${req.id}" name="roleName" value="Collaborator" style="width:160px; display:inline;" required>
                                </div>
                                <button type="submit" class="btn-sm btn-success">Accept & Add Member</button>
                            </form>
                            <form method="post" action="/projects/requests/${req.id}/respond" style="display:inline; margin-top:6px;">
                                <input type="hidden" name="accept" value="false">
                                <button type="submit" class="btn-sm btn-danger">Decline</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:forEach>
        </div>
    </c:if>

    <!-- Requirement 3: Filter Options (My Projects vs Collaborating Projects vs All) -->
    <div class="filter-bar">
        <span><b>Filter Projects:</b></span>
        <a href="/dashboard?filter=all" class="${activeFilter == 'all' ? 'active' : ''}">All Projects</a>
        <a href="/dashboard?filter=my" class="${activeFilter == 'my' ? 'active' : ''}">My Projects (${stats.myProjectsCount})</a>
        <a href="/dashboard?filter=collaborating" class="${activeFilter == 'collaborating' ? 'active' : ''}">Collaborating Projects (${stats.collaboratingProjectsCount})</a>
    </div>

    <!-- Requirement 2: List Projects with Collaborators & Roles -->
    <h2>Projects Directory</h2>
    <c:choose>
        <c:when test="${empty projects}">
            <div class="card"><p>No projects found for the selected filter.</p></div>
        </c:when>
        <c:otherwise>
            <c:forEach var="p" items="${projects}">
                <div class="project">
                    <div class="project-header">
                        <div>
                            <h3>${p.name}</h3>
                            <p style="color:#52616b; margin-top:-6px;">Created by <b>${p.owner.name}</b> (${p.owner.department})</p>
                        </div>
                        <span class="status-badge ${p.status == 'COMPLETED' ? 'status-completed' : 'status-open'}">${p.status}</span>
                    </div>

                    <p>${p.description}</p>
                    <p><b>Max Team Size:</b> ${p.teamSize} &nbsp; | &nbsp; <b>Required Skills:</b> ${p.requiredSkills}</p>

                    <!-- Collaborators & Members List with Roles (Requirement 2 & 3) -->
                    <div class="collaborators-section">
                        <div class="collaborators-title">Team Members & Collaborators:</div>
                        <div class="member-list">
                            <c:forEach var="m" items="${membersMap[p.id]}">
                                <span class="member-tag">
                                    <b>${m.student.name}</b>
                                    <span class="member-role">${m.roleName}</span>
                                </span>
                            </c:forEach>
                        </div>
                    </div>

                    <!-- Project Actions (Requirement 4, 5, 6) -->
                    <div class="action-bar" style="margin-top: 16px;">
                        <!-- Requirement 4: Creator can complete project -->
                        <c:if test="${p.owner.id == student.id}">
                            <c:if test="${p.status != 'COMPLETED'}">
                                <form method="post" action="/projects/${p.id}/complete" style="display:inline;">
                                    <button type="submit" class="btn-sm btn-secondary">Mark Project as Completed</button>
                                </form>
                            </c:if>
                        </c:if>

                        <c:if test="${p.status == 'COMPLETED'}">
                            <a href="/projects/${p.id}/feedback" class="btn btn-sm btn-success">Rate & Feedback Teammates</a>
                        </c:if>

                        <!-- Requirement 5: Owner can send invitation to a candidate -->
                        <c:if test="${p.owner.id == student.id && p.status != 'COMPLETED'}">
                            <form method="post" action="/projects/${p.id}/invite" style="display:inline-flex; gap:6px; align-items:center;">
                                <label for="invite-cand-${p.id}" style="margin:0; font-size:12px;">Invite:</label>
                                <select id="invite-cand-${p.id}" name="candidateId" style="width:auto; padding:4px 8px; margin:0;" required>
                                    <c:forEach var="c" items="${candidates}">
                                        <option value="${c.id}">${c.name} (${c.department})</option>
                                    </c:forEach>
                                </select>
                                <button type="submit" class="btn-sm btn-success">Send Invite</button>
                            </form>
                        </c:if>

                        <!-- Requirement 6: Collaborator can request Project Owner to join -->
                        <c:if test="${p.owner.id != student.id && p.status != 'COMPLETED'}">
                            <form method="post" action="/projects/${p.id}/request-join" style="display:inline;">
                                <button type="submit" class="btn-sm btn-outline">Request Owner to Join Project</button>
                            </form>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    <!-- Requirement 7: Received Feedback & Ratings Section -->
    <c:if test="${not empty studentRatings}">
        <div class="card">
            <h2>Feedback & Ratings Received</h2>
            <p>Contribution feedback left by teammates on completed projects:</p>
            <c:forEach var="r" items="${studentRatings}">
                <div class="rating-item">
                    <p><b>Project: ${r.project.name}</b> — Rating by <b>${r.fromStudent.name}</b></p>
                    <p class="rating-stars">Score: ★ ${r.averageScore} / 5.0 (Tech: ${r.technicalScore}/5, Teamwork: ${r.teamworkScore}/5, Reliability: ${r.reliabilityScore}/5)</p>
                    <p><i>"${r.review}"</i></p>
                </div>
            </c:forEach>
        </div>
    </c:if>
</div>
</body>
</html>
