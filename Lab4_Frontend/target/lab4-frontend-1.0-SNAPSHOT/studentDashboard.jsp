<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Student Dashboard - IPMS Lab 4</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: red; }
        .container { max-width: 1000px; margin: 0 auto; background-color: white;
                     padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { color: #003366; border-bottom: 3px solid #003366; padding-bottom: 10px; }
        h2 { color: #005599; margin-top: 25px; }
        .student-info { background-color: #e8f4f8; padding: 15px; border-radius: 5px; margin: 20px 0; }
        .student-info p { margin: 8px 0; }
        .jwt-panel {
            background: #fff8e1; border: 1px solid #ffc107; border-radius: 5px;
            padding: 12px 15px; margin: 15px 0; font-size: 13px;
        }
        .jwt-panel summary { cursor: pointer; font-weight: bold; color: #856404; }
        .jwt-token {
            word-break: break-all; font-family: monospace; font-size: 11px;
            background: #f5f5f5; padding: 8px; border-radius: 4px; margin-top: 8px;
            color: #333;
        }
        .nav-buttons { margin: 25px 0; }
        .btn { display: inline-block; padding: 12px 24px; margin: 5px;
               background-color: #003366; color: white; text-decoration: none;
               border-radius: 5px; border: none; cursor: pointer; font-size: 14px; }
        .btn:hover { background-color: #005599; }
        .btn-green { background-color: #28a745; }
        .btn-green:hover { background-color: #218838; }
        .btn-red { background-color: #dc3545; }
        .btn-red:hover { background-color: #c82333; }
        .btn-gray { background-color: #666; }
        .btn-gray:hover { background-color: #888; }
    </style>
</head>
<body>
<%
    // Check session - redirect to login if no JWT
    String jwt         = (String)  session.getAttribute("jwt");
    String studentName = (String)  session.getAttribute("studentName");
    String email       = (String)  session.getAttribute("email");
    String program     = (String)  session.getAttribute("program");
    Object userIdObj   = session.getAttribute("userId");

    if (jwt == null || studentName == null) {
        response.sendRedirect("login.html");
        return;
    }

    // Also check attributes passed directly from Login servlet (first load)
    if (studentName == null) studentName = (String) request.getAttribute("studentName");

    // Decode JWT to show role and expiry (for demo purposes)
    String[] jwtParts   = jwt.split("\\.");
    String jwtPayload   = jwtParts.length > 1 ? jwtParts[1] : "";
%>
<div class="container">
    <h1>&#127891; Student Dashboard</h1>

    <div class="student-info">
        <h2>Welcome, <%= studentName %>!</h2>
        <% if (email != null && !email.isEmpty()) { %>
            <p><strong>Email:</strong> <%= email %></p>
        <% } %>
        <% if (program != null && !program.isEmpty()) { %>
            <p><strong>Program:</strong> <%= program %></p>
        <% } %>
        <p><strong>Role:</strong> <span style="color:#28a745;font-weight:bold;">STUDENT</span></p>
    </div>

    <!-- JWT Display Panel (for Lab demo) -->
    <details class="jwt-panel">
        <summary>&#128274; JWT Authentication Token (click to expand)</summary>
        <p style="color:#555;margin:8px 0 4px;">
            Your identity is verified with this JSON Web Token.
            It is sent in the <code>Authorization: Bearer &lt;token&gt;</code> header
            with every request to backend microservices.
        </p>
        <div class="jwt-token"><%= jwt %></div>
        <p style="font-size:11px;color:#888;margin-top:5px;">
            Token expires in 2 hours. Contains: username, role=STUDENT, userId.
        </p>
    </details>

    <h2>What would you like to do?</h2>
    <div class="nav-buttons">
        <a href="SearchOpportunities" class="btn btn-green">&#128269; Browse &amp; Apply for Opportunities</a>
        <a href="ViewApplicationStatus" class="btn">&#128203; View My Application Status</a>
        <a href="Logout" class="btn btn-red">&#128275; Logout</a>
    </div>
</div>
</body>
</html>
