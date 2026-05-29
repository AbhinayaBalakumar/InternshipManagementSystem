<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Result - IPMS</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
        .container { max-width: 500px; margin: 80px auto; background: white;
                     padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; }
        .msg { background: #e8f4f8; padding: 15px; border-radius: 5px; color: #333; margin: 20px 0; }
        .btn { display: inline-block; padding: 10px 22px; margin: 6px;
               background-color: #004085; color: white; text-decoration: none;
               border-radius: 5px; font-size: 14px; }
    </style>
</head>
<body>
<div class="container">
    <h1>&#8505;&#65039; Result</h1>
    <div class="msg"><%= request.getAttribute("message") %></div>
    <%
        String role = (String) session.getAttribute("role");
        String backLink = "index.html";
        if ("STUDENT".equals(role))  backLink = "studentDashboard.jsp";
        if ("EMPLOYER".equals(role)) backLink = "employerDashboard.jsp";
    %>
    <a href="<%= backLink %>" class="btn">&#8592; Back to Dashboard</a>
</div>
</body>
</html>
