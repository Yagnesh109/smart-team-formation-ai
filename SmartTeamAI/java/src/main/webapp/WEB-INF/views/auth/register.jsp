<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register - SmartTeam AI</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="card wide">
    <h1>Create Student Profile</h1>
    <p>Sign up to find project teammates and collaborate on projects.</p>

    <% if(request.getAttribute("error") != null){ %>
        <div class="err"><%=request.getAttribute("error")%></div>
    <% } %>

    <form method="post" action="/register" class="grid">
        <div class="form-group">
            <label for="reg-name">Full Name</label>
            <input id="reg-name" name="name" placeholder="Enter your full name" required>
        </div>

        <div class="form-group">
            <label for="reg-email">College Email Address</label>
            <input id="reg-email" name="email" type="email" placeholder="e.g. aakash@example.com" required>
        </div>

        <div class="form-group">
            <label for="reg-password">Account Password</label>
            <input id="reg-password" name="password" type="password" placeholder="Create a strong password" required>
        </div>

        <div class="form-group">
            <label for="reg-department">Department / Branch</label>
            <input id="reg-department" name="department" placeholder="e.g. Computer Science (CSE)" required>
        </div>

        <div class="form-group">
            <label for="reg-year">Year of Study</label>
            <input id="reg-year" name="studyYear" type="number" min="1" max="4" placeholder="1 to 4" required>
        </div>

        <div class="form-group">
            <label for="reg-exp">Experience Level</label>
            <select id="reg-exp" name="experienceLevel">
                <option value="Beginner">Beginner</option>
                <option value="Intermediate" selected>Intermediate</option>
                <option value="Advanced">Advanced</option>
            </select>
        </div>

        <div class="form-group full">
            <label for="reg-availability">Weekly Availability</label>
            <input id="reg-availability" name="availability" placeholder="e.g. Weekends, Monday & Wednesday evenings" required>
        </div>

        <div class="form-group full">
            <label for="reg-interests">Primary Technical Interests</label>
            <input id="reg-interests" name="interests" placeholder="e.g. AI/ML, Web Development, Mobile Apps">
        </div>

        <div class="form-group full">
            <label for="reg-bio">Student Bio & Skills Summary</label>
            <textarea id="reg-bio" name="bio" placeholder="Describe your technical skills, programming languages, past project experience..."></textarea>
        </div>

        <button type="submit" class="full btn-success">Create Student Profile</button>
    </form>

    <div style="margin-top: 15px; text-align: center;">
        <a href="/login">Already have an account? Back to login</a>
    </div>
</div>
</body>
</html>
