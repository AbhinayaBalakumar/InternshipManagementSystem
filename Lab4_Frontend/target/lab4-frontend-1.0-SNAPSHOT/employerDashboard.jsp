<%@page import="java.util.List, java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Employer Dashboard - IPMS</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
        .container { max-width: 1000px; margin: 0 auto; background-color: white;
                     padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { color: #e65100; border-bottom: 3px solid #e65100; padding-bottom: 10px; }
        h2 { color: #bf360c; margin-top: 25px; }
        .info { background-color: #fff3e0; padding: 15px; border-radius: 5px; margin: 20px 0; }
        .jwt-panel { background:#fff8e1; border:1px solid #ffc107; border-radius:5px;
                     padding:12px 15px; margin:15px 0; font-size:13px; }
        .jwt-panel summary { cursor:pointer; font-weight:bold; color:#856404; }
        .jwt-token { word-break:break-all; font-family:monospace; font-size:11px;
                     background:#f5f5f5; padding:8px; border-radius:4px; margin-top:8px; }
        table { width: 100%; border-collapse: collapse; margin: 15px 0; }
        th { background-color: #e65100; color: white; padding: 10px; text-align: left; font-size: 13px; }
        td { padding: 10px; border-bottom: 1px solid #ddd; font-size: 13px; }
        tr:hover { background-color: #fff8f5; }
        .btn { display: inline-block; padding: 10px 20px; margin: 5px;
               background-color: #e65100; color: white; text-decoration: none;
               border-radius: 5px; font-size: 14px; border: none; cursor: pointer; }
        .btn:hover { background-color: #bf360c; }
        .btn-blue { background-color: #1565c0; }
        .btn-blue:hover { background-color: #0d47a1; }
        .btn-red { background-color: #dc3545; }
        .btn-red:hover { background-color: #c82333; }
        .btn-small { padding: 5px 12px; font-size: 12px; margin: 0; }
        .status-open     { background: #d4edda; color: #155724; padding: 3px 8px; border-radius: 3px; font-size: 11px; font-weight: bold; }
        .status-closed   { background: #f8d7da; color: #721c24; padding: 3px 8px; border-radius: 3px; font-size: 11px; font-weight: bold; }
        .no-data { color: #888; font-style: italic; padding: 15px 0; }
        .error-msg { background: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px;
                     padding: 10px; color: #721c24; margin: 10px 0; }
    </style>
</head>
<body>
<%
    String jwt         = (String) session.getAttribute("jwt");
    String companyName = (String) session.getAttribute("companyName");
    if (jwt == null) { response.sendRedirect("employerLogin.html"); return; }
    if (companyName == null) companyName = (String) request.getAttribute("companyName");

    List<Map<String, String>> postings =
        (List<Map<String, String>>) request.getAttribute("postings");
    String errorMsg = (String) request.getAttribute("errorMsg");
%>
<div class="container">
    <h1>&#127970; Employer Dashboard</h1>

    <div class="info">
        <h2 style="margin-top:0;">Welcome, <%= companyName %>!</h2>
        <p style="margin:0;"><strong>Role:</strong> <span style="color:#e65100;font-weight:bold;">EMPLOYER</span></p>
    </div>

    <details class="jwt-panel">
        <summary>&#128274; JWT Authentication Token (click to expand)</summary>
        <p style="color:#555;margin:8px 0 4px;">Sent with every request to backend microservices.</p>
        <div class="jwt-token"><%= jwt %></div>
    </details>

    <% if (errorMsg != null) { %>
        <div class="error-msg"><%= errorMsg %></div>
    <% } %>

    <!-- Action buttons -->
    <div style="margin: 20px 0;">
        <a href="PostOpportunity" class="btn">&#128196; Post New Opportunity</a>
        <a href="Logout" class="btn btn-red">&#128275; Logout</a>
    </div>

    <!-- My Postings -->
    <h2>&#128203; My Job Postings</h2>
    <% if (postings == null || postings.isEmpty()) { %>
        <p class="no-data">You have not posted any opportunities yet.</p>
    <% } else { %>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Job Title</th>
                    <th>Type</th>
                    <th>Location</th>
                    <th>Deadline</th>
                    <th>Status</th>
                    <th>Applicants</th>
                </tr>
            </thead>
            <tbody>
                <% for (Map<String, String> p : postings) {
                    String status = p.get("status");
                    String statusClass = "OPEN".equals(status) ? "status-open" : "status-closed";
                %>
                <tr>
                    <td><%= p.get("opportunityId") %></td>
                    <td><strong><%= p.get("jobTitle") %></strong></td>
                    <td><%= p.get("jobType") %></td>
                    <td><%= p.get("location") %></td>
                    <td><%= p.get("deadline") %></td>
                    <td><span class="<%= statusClass %>"><%= status %></span></td>
                    <td>
                        <a href="ViewApplicants?opportunityId=<%= p.get("opportunityId") %>"
                           class="btn btn-blue btn-small">View Applicants</a>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>
</div>
</body>
</html>
