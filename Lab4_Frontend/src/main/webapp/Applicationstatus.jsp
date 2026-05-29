<%@page import="java.util.List, java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>My Application Status - IPMS</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
        .container { max-width: 900px; margin: 0 auto; background-color: white;
                     padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { color: #003366; border-bottom: 3px solid #003366; padding-bottom: 10px; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th { background-color: #003366; color: white; padding: 12px; text-align: left; }
        td { padding: 10px; border-bottom: 1px solid #ddd; }
        tr:hover { background-color: #f5f5f5; }
        .status { padding: 4px 10px; border-radius: 3px; font-weight: bold; font-size: 12px; }
        .status-submitted   { background-color: #ffd700; color: #333; }
        .status-review      { background-color: #87ceeb; color: #333; }
        .status-shortlisted { background-color: #90ee90; color: #333; }
        .status-rejected    { background-color: #f8d7da; color: #721c24; }
        .btn { display: inline-block; padding: 10px 20px; margin: 5px;
               background-color: #003366; color: white; text-decoration: none;
               border-radius: 5px; font-size: 14px; }
        .btn:hover { background-color: #005599; }
        .jwt-note { background: #fff3cd; border: 1px solid #ffc107; border-radius: 4px;
                    padding: 8px 12px; font-size: 12px; color: #856404; margin-bottom: 15px; }
    </style>
</head>
<body>
<%
    String jwt = (String) session.getAttribute("jwt");
    if (jwt == null) { response.sendRedirect("login.html"); return; }
    List<Map<String, String>> applications =
        (List<Map<String, String>>) request.getAttribute("applications");
    String studentName = (String) request.getAttribute("studentName");
    if (studentName == null) studentName = (String) session.getAttribute("studentName");
%>
<div class="container">
    <h1>&#128203; My Application Status</h1>
    <div class="jwt-note">&#128274; Fetched using JWT &mdash; backend identified you from the token, no session ID needed.</div>
    <p>Showing applications for: <strong><%= studentName %></strong></p>

    <% if (applications == null || applications.isEmpty()) { %>
        <p style="color:#666;">You have not submitted any applications yet.</p>
    <% } else { %>
        <table>
            <thead>
                <tr>
                    <th>App ID</th><th>Job Title</th><th>Company</th><th>Date Applied</th><th>Status</th>
                </tr>
            </thead>
            <tbody>
                <% for (Map<String, String> app : applications) {
                    String status = app.get("status");
                    String statusClass = "status ";
                    if ("Submitted".equalsIgnoreCase(status))         statusClass += "status-submitted";
                    else if (status.toLowerCase().contains("review")) statusClass += "status-review";
                    else if (status.toLowerCase().contains("short"))  statusClass += "status-shortlisted";
                    else if (status.toLowerCase().contains("reject")) statusClass += "status-rejected";
                %>
                <tr>
                    <td><%= app.get("applicationId") %></td>
                    <td><%= app.get("jobTitle") %></td>
                    <td><%= app.get("companyName") %></td>
                    <td><%= app.get("applicationDate") %></td>
                    <td><span class="<%= statusClass %>"><%= status %></span></td>
                </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>
    <a href="studentDashboard.jsp" class="btn">&#8592; Back to Dashboard</a>
</div>
</body>
</html>
