<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>SmartTeam AI - Login</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
<div class="card auth">
    <h1>SmartTeam AI</h1>
    <p>AI-assisted Project Team Formation</p>

    <% if(request.getParameter("registered") != null){ %>
        <div class="ok">Registration successful. Login now.</div>
    <% } %>
    <% if(request.getAttribute("error") != null){ %>
        <div class="err"><%=request.getAttribute("error")%></div>
    <% } %>

    <form method="post" action="/login">
        <div class="form-group">
            <label for="login-email">College Email Address</label>
            <input id="login-email" name="email" type="email" placeholder="e.g. student@college.edu" required>
        </div>

        <div class="form-group">
            <label for="login-password">Password</label>
            <input id="login-password" name="password" type="password" placeholder="Enter password" required>
        </div>

        <button type="submit" class="full">Login</button>
    </form>

    <div style="margin-top: 15px;">
        <a href="/register" class="btn btn-outline btn-sm">Create Student Profile</a>
    </div>
</div>
</body>
</html>
