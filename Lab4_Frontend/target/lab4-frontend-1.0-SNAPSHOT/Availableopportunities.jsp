<%@page import="java.util.List, java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Available Opportunities - IPMS</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
        .container { max-width: 1100px; margin: 0 auto; background-color: white;
                     padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { color: #003366; border-bottom: 3px solid #003366; padding-bottom: 10px; }
        .search-bar { background: #e8f4f8; padding: 15px; border-radius: 6px; margin: 20px 0; }
        .search-bar form { display: flex; gap: 10px; flex-wrap: wrap; align-items: center; }
        .search-bar input { padding: 8px; border: 1px solid #ccc; border-radius: 4px; font-size: 13px; flex: 1; min-width: 100px; }
        .search-bar button { padding: 8px 18px; background: #004085; color: white; border: none; border-radius: 4px; cursor: pointer; }
        .auth-banner {
            padding: 10px 15px; border-radius: 5px; margin: 10px 0; font-size: 13px;
        }
        .auth-banner.logged-in  { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .auth-banner.logged-out { background: #fff3cd; color: #856404; border: 1px solid #ffc107; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th { background-color: #003366; color: white; padding: 12px; text-align: left; font-size: 13px; }
        td { padding: 10px; border-bottom: 1px solid #ddd; font-size: 13px; vertical-align: top; }
        tr:hover { background-color: #f5f5f5; }
        .btn-apply {
            padding: 7px 16px; background-color: #28a745; color: white;
            border: none; border-radius: 4px; cursor: pointer; font-size: 12px;
            font-weight: bold;
        }
        .btn-apply:hover { background-color: #218838; }
        .btn-login-to-apply {
            padding: 7px 16px; background-color: #6c757d; color: white;
            border: none; border-radius: 4px; font-size: 12px; text-decoration: none;
            display: inline-block;
        }
        .no-results { text-align: center; padding: 30px; color: #666; }
        .back-link { margin-top: 20px; }
        .back-link a { color: #004085; }
        .skills-tag { background: #e8f4f8; padding: 2px 8px; border-radius: 10px; font-size: 11px; color: #004085; }
    </style>
</head>
<body>
<%
    List<Map<String, String>> opportunities =
        (List<Map<String, String>>) request.getAttribute("opportunities");
    Boolean isLoggedIn = (Boolean) request.getAttribute("isLoggedIn");
    String  role       = (String)  request.getAttribute("role");
    String  company    = (String)  request.getAttribute("company");
    String  title      = (String)  request.getAttribute("title");
    String  skills     = (String)  request.getAttribute("skills");
    if (isLoggedIn == null)  isLoggedIn = false;
    if (company == null) company = "";
    if (title   == null) title   = "";
    if (skills  == null) skills  = "";
%>
<div class="container">
    <h1>&#128269; Available Opportunities</h1>

    <!-- Auth status banner -->
    <% if (isLoggedIn) { %>
        <div class="auth-banner logged-in">
            &#128274; You are logged in &mdash; your JWT is being sent to the backend. You can apply for positions below.
        </div>
    <% } else { %>
        <div class="auth-banner logged-out">
            &#128275; You are browsing as a guest (no JWT). <a href="login.html">Log in</a> to apply for opportunities.
        </div>
    <% } %>

    <!-- Search form -->
    <div class="search-bar">
        <form action="SearchOpportunities" method="GET">
            <input type="text" name="company" placeholder="Company..." value="<%= company %>">
            <input type="text" name="title"   placeholder="Job title..."  value="<%= title %>">
            <input type="text" name="skills"  placeholder="Skills..."     value="<%= skills %>">
            <button type="submit">Search</button>
        </form>
    </div>

    <% if (opportunities == null || opportunities.isEmpty()) { %>
        <div class="no-results">
            <p>&#128203; No opportunities found. Try a different search.</p>
        </div>
    <% } else { %>
        <p style="color:#555;">Found <strong><%= opportunities.size() %></strong> opportunity(ies).</p>
        <table>
            <thead>
                <tr>
                    <th>Company</th>
                    <th>Job Title</th>
                    <th>Type</th>
                    <th>Location</th>
                    <th>Skills</th>
                    <th>Deadline</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% for (Map<String, String> opp : opportunities) { %>
                <tr>
                    <td><strong><%= opp.get("companyName") %></strong></td>
                    <td><%= opp.get("jobTitle") %></td>
                    <td><%= opp.get("jobType") %></td>
                    <td><%= opp.get("location") %></td>
                    <td><span class="skills-tag"><%= opp.get("requiredSkills") %></span></td>
                    <td><%= opp.get("deadline") %></td>
                    <td>
                        <% if (isLoggedIn && "STUDENT".equals(role)) { %>
                            <form action="ApplyInternship" method="POST" style="margin:0;">
                                <input type="hidden" name="opportunityId" value="<%= opp.get("opportunityId") %>">
                                <button type="submit" class="btn-apply">Apply &#128196;</button>
                            </form>
                        <% } else if (!isLoggedIn) { %>
                            <a href="login.html" class="btn-login-to-apply">Login to Apply</a>
                        <% } %>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>

    <div class="back-link">
        <% if (isLoggedIn) { %>
            <a href="studentDashboard.jsp">&#8592; Back to Dashboard</a>
        <% } else { %>
            <a href="index.html">&#8592; Back to Home</a>
        <% } %>
    </div>
</div>
</body>
</html>
