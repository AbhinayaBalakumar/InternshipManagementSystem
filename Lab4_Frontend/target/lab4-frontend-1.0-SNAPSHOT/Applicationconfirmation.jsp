<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Application Submitted - IPMS</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
        .container { max-width: 600px; margin: 60px auto; background: white;
                     padding: 40px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; }
        .checkmark { font-size: 60px; margin-bottom: 10px; }
        h1 { color: #28a745; }
        .details { background: #e8f4f8; padding: 15px; border-radius: 5px; text-align: left; margin: 20px 0; }
        .details p { margin: 8px 0; }
        .jwt-note { background:#fff3cd; border:1px solid #ffc107; border-radius:4px;
                    padding:8px 12px; font-size:12px; color:#856404; margin:10px 0; }
        .btn { display: inline-block; padding: 12px 24px; margin: 8px;
               background-color: #003366; color: white; text-decoration: none;
               border-radius: 5px; font-size: 14px; }
        .btn-green { background-color: #28a745; }
    </style>
</head>
<body>
<%
    String jwt = (String) session.getAttribute("jwt");
    if (jwt == null) { response.sendRedirect("login.html"); return; }
    String studentName  = (String) request.getAttribute("studentName");
    String jobTitle     = (String) request.getAttribute("jobTitle");
    String companyName  = (String) request.getAttribute("companyName");
    String applicationId= (String) request.getAttribute("applicationId");
    String applyDate    = (String) request.getAttribute("applyDate");
%>
<div class="container">
    <div class="checkmark">&#10004;&#65039;</div>
    <h1>Application Submitted!</h1>
    <div class="jwt-note">&#128274; Authenticated via JWT &mdash; backend verified your identity from the token.</div>
    <div class="details">
        <p><strong>Student:</strong> <%= studentName %></p>
        <p><strong>Position:</strong> <%= jobTitle %></p>
        <p><strong>Company:</strong> <%= companyName %></p>
        <p><strong>Date Applied:</strong> <%= applyDate %></p>
        <% if (applicationId != null && !applicationId.isEmpty()) { %>
        <p><strong>Application ID:</strong> <%= applicationId %></p>
        <% } %>
        <p><strong>Status:</strong> <span style="color:#28a745;font-weight:bold;">Submitted</span></p>
    </div>
    <a href="SearchOpportunities" class="btn btn-green">Browse More Opportunities</a>
    <a href="ViewApplicationStatus" class="btn">View All My Applications</a>
</div>
</body>
</html>
