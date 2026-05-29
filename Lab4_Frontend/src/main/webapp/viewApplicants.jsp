<%@page import="java.util.List, java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>View Applicants - IPMS</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
        .container { max-width: 950px; margin: 0 auto; background-color: white;
                     padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { color: #1565c0; border-bottom: 3px solid #1565c0; padding-bottom: 10px; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th { background-color: #1565c0; color: white; padding: 10px; text-align: left; font-size: 13px; }
        td { padding: 10px; border-bottom: 1px solid #ddd; font-size: 13px; vertical-align: middle; }
        tr:hover { background-color: #f0f4ff; }
        .status { padding: 4px 10px; border-radius: 3px; font-weight: bold; font-size: 12px; display:inline-block; }
        .status-Submitted    { background: #fff3cd; color: #856404; }
        .status-Under_Review { background: #cce5ff; color: #004085; }
        .status-Shortlisted  { background: #d4edda; color: #155724; }
        .status-Rejected     { background: #f8d7da; color: #721c24; }
        select { padding: 5px 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 12px; }
        .btn { display: inline-block; padding: 10px 20px; background-color: #1565c0;
               color: white; text-decoration: none; border-radius: 5px; font-size: 14px; }
        .btn:hover { background-color: #0d47a1; }
        .btn-update { padding: 5px 12px; background-color: #e65100; color: white;
                      border: none; border-radius: 4px; cursor: pointer; font-size: 12px; }
        .btn-update:hover { background-color: #bf360c; }
        .jwt-note { background:#fff3cd; border:1px solid #ffc107; border-radius:4px;
                    padding:8px 12px; font-size:12px; color:#856404; margin-bottom:15px; }
        .no-data { color: #888; font-style: italic; padding: 15px 0; }
    </style>
</head>
<body>
<%
    String jwt = (String) session.getAttribute("jwt");
    if (jwt == null || !"EMPLOYER".equals(session.getAttribute("role"))) {
        response.sendRedirect("employerLogin.html"); return;
    }
    List<Map<String, String>> applicants =
        (List<Map<String, String>>) request.getAttribute("applicants");
    String opportunityId = (String) request.getAttribute("opportunityId");
    String companyName   = (String) session.getAttribute("companyName");
%>
<div class="container">
    <h1>&#128101; Applicants for Opportunity #<%= opportunityId %></h1>
    <div class="jwt-note">&#128274; Fetched using JWT &mdash; backend verified you own this posting.</div>
    <p style="color:#555;">Company: <strong><%= companyName %></strong></p>

    <% if (applicants == null || applicants.isEmpty()) { %>
        <p class="no-data">No applicants yet for this opportunity.</p>
    <% } else { %>
        <p style="color:#555;">Total applicants: <strong><%= applicants.size() %></strong></p>
        <table>
            <thead>
                <tr>
                    <th>App ID</th>
                    <th>Student Name</th>
                    <th>Date Applied</th>
                    <th>Current Status</th>
                    <th>Update Status</th>
                </tr>
            </thead>
            <tbody>
                <% for (Map<String, String> a : applicants) {
                    String status = a.get("status");
                    String statusClass = "status status-" + status.replace(" ", "_");
                %>
                <tr>
                    <td><%= a.get("applicationId") %></td>
                    <td><strong><%= a.get("studentName") %></strong></td>
                    <td><%= a.get("applicationDate") %></td>
                    <td><span class="<%= statusClass %>"><%= status %></span></td>
                    <td>
                        <form action="ViewApplicants" method="POST" style="display:inline-flex; gap:6px; align-items:center;">
                            <input type="hidden" name="applicationId" value="<%= a.get("applicationId") %>">
                            <input type="hidden" name="opportunityId" value="<%= opportunityId %>">
                            <select name="status">
                                <option value="Submitted"    <%= "Submitted".equals(status)    ? "selected" : "" %>>Submitted</option>
                                <option value="Under Review" <%= "Under Review".equals(status) ? "selected" : "" %>>Under Review</option>
                                <option value="Shortlisted"  <%= "Shortlisted".equals(status)  ? "selected" : "" %>>Shortlisted</option>
                                <option value="Rejected"     <%= "Rejected".equals(status)     ? "selected" : "" %>>Rejected</option>
                            </select>
                            <button type="submit" class="btn-update">Update</button>
                        </form>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>
    <a href="EmployerDashboard" class="btn">&#8592; Back to Dashboard</a>
</div>
</body>
</html>
