<%@page import="java.util.List, java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Coordinator Dashboard - IPMS</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }
        .container { max-width: 1100px; margin: 0 auto; background-color: white;
                     padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        h1 { color: #4a148c; border-bottom: 3px solid #4a148c; padding-bottom: 10px; }
        h2 { color: #6a1b9a; margin-top: 30px; }
        .info { background-color: #f3e5f5; padding: 12px 15px; border-radius: 5px; margin: 15px 0;
                font-size: 13px; color: #4a148c; border-left: 4px solid #4a148c; }
        table { width: 100%; border-collapse: collapse; margin: 15px 0 30px; }
        th { background-color: #4a148c; color: white; padding: 10px 12px; text-align: left; font-size: 13px; }
        td { padding: 9px 12px; border-bottom: 1px solid #eee; font-size: 12px; }
        tr:hover { background-color: #f3e5f5; }
        .ACTIVE      { color: #2e7d32; font-weight: bold; }
        .DISABLED    { color: #c62828; font-weight: bold; }
        .OPEN        { color: #2e7d32; font-weight: bold; }
        .REMOVED     { color: #c62828; font-weight: bold; }
        .btn-dis { padding: 4px 10px; background-color: #c62828; color: white;
                   border: none; border-radius: 3px; cursor: pointer; font-size: 11px; }
        .btn-en  { padding: 4px 10px; background-color: #2e7d32; color: white;
                   border: none; border-radius: 3px; cursor: pointer; font-size: 11px; }
        .btn-dis:hover { background-color: #8b0000; }
        .btn-en:hover  { background-color: #1b5e20; }
        .nav { margin-bottom: 20px; }
        .nav a { margin-right: 15px; color: #4a148c; text-decoration: none; font-weight: bold; }
    </style>
</head>
<body>
<%
    List<Map<String,String>> students  = (List<Map<String,String>>) request.getAttribute("studentList");
    List<Map<String,String>> employers = (List<Map<String,String>>) request.getAttribute("employerList");
    List<Map<String,String>> postings  = (List<Map<String,String>>) request.getAttribute("postingList");
    String uname = (String) request.getAttribute("username");
    String upass = (String) request.getAttribute("password");
%>
<div class="container">
    <div class="nav">
        <a href="index.html">&#8592; Home</a> |
        <a href="coordinatorLogin.html">Logout</a>
    </div>
    <h1>&#127891; Placement Coordinator Dashboard</h1>
    <div class="info">&#128274; Coordinator access &mdash; data fetched via REST API from backend microservice. No JWT required for this role.</div>

    <!-- STUDENTS -->
    <h2>&#127891; Student Accounts (<%= students != null ? students.size() : 0 %>)</h2>
    <table>
        <tr><th>ID</th><th>Name</th><th>Email</th><th>Program</th><th>GPA</th><th>Status</th><th>Action</th></tr>
        <% if (students != null) for (Map<String,String> s : students) {
            String status = s.get("status"); %>
        <tr>
            <td><%= s.get("studentId") %></td>
            <td><%= s.get("firstName") %> <%= s.get("lastName") %></td>
            <td><%= s.get("email") %></td>
            <td><%= s.get("program") %></td>
            <td><%= s.get("gpa") %></td>
            <td><span class="<%= status %>"><%= status %></span></td>
            <td>
                <% if ("ACTIVE".equals(status)) { %>
                    <form action="ManageSystem" method="POST" style="display:inline;">
                        <input type="hidden" name="username"  value="<%= uname %>">
                        <input type="hidden" name="password"  value="<%= upass %>">
                        <input type="hidden" name="studentId" value="<%= s.get("studentId") %>">
                        <input type="hidden" name="action"    value="disableStudent">
                        <button type="submit" class="btn-dis">Disable</button>
                    </form>
                <% } else { %>
                    <form action="ManageSystem" method="POST" style="display:inline;">
                        <input type="hidden" name="username"  value="<%= uname %>">
                        <input type="hidden" name="password"  value="<%= upass %>">
                        <input type="hidden" name="studentId" value="<%= s.get("studentId") %>">
                        <input type="hidden" name="action"    value="enableStudent">
                        <button type="submit" class="btn-en">Enable</button>
                    </form>
                <% } %>
            </td>
        </tr>
        <% } %>
    </table>

    <!-- EMPLOYERS -->
    <h2>&#127970; Employer Accounts (<%= employers != null ? employers.size() : 0 %>)</h2>
    <table>
        <tr><th>ID</th><th>Company</th><th>Industry</th><th>Location</th><th>Status</th><th>Action</th></tr>
        <% if (employers != null) for (Map<String,String> e : employers) {
            String status = e.get("status"); %>
        <tr>
            <td><%= e.get("employerId") %></td>
            <td><%= e.get("companyName") %></td>
            <td><%= e.get("industry") %></td>
            <td><%= e.get("location") %></td>
            <td><span class="<%= status %>"><%= status %></span></td>
            <td>
                <% if ("ACTIVE".equals(status)) { %>
                    <form action="ManageSystem" method="POST" style="display:inline;">
                        <input type="hidden" name="username"   value="<%= uname %>">
                        <input type="hidden" name="password"   value="<%= upass %>">
                        <input type="hidden" name="employerId" value="<%= e.get("employerId") %>">
                        <input type="hidden" name="action"     value="disableEmployer">
                        <button type="submit" class="btn-dis">Disable</button>
                    </form>
                <% } else { %>
                    <form action="ManageSystem" method="POST" style="display:inline;">
                        <input type="hidden" name="username"   value="<%= uname %>">
                        <input type="hidden" name="password"   value="<%= upass %>">
                        <input type="hidden" name="employerId" value="<%= e.get("employerId") %>">
                        <input type="hidden" name="action"     value="enableEmployer">
                        <button type="submit" class="btn-en">Enable</button>
                    </form>
                <% } %>
            </td>
        </tr>
        <% } %>
    </table>

    <!-- POSTINGS -->
    <h2>&#128203; Job Postings (<%= postings != null ? postings.size() : 0 %>)</h2>
    <table>
        <tr><th>ID</th><th>Title</th><th>Company</th><th>Type</th><th>Deadline</th><th>Status</th><th>Action</th></tr>
        <% if (postings != null) for (Map<String,String> p : postings) {
            String status = p.get("status"); %>
        <tr>
            <td><%= p.get("opportunityId") %></td>
            <td><%= p.get("jobTitle") %></td>
            <td><%= p.get("companyName") %></td>
            <td><%= p.get("jobType") %></td>
            <td><%= p.get("deadline") %></td>
            <td><span class="<%= status %>"><%= status %></span></td>
            <td>
                <% if (!"REMOVED".equals(status)) { %>
                    <form action="ManageSystem" method="POST" style="display:inline;">
                        <input type="hidden" name="username"      value="<%= uname %>">
                        <input type="hidden" name="password"      value="<%= upass %>">
                        <input type="hidden" name="opportunityId" value="<%= p.get("opportunityId") %>">
                        <input type="hidden" name="action"        value="removePosting">
                        <button type="submit" class="btn-dis">Remove</button>
                    </form>
                <% } else { %>
                    <form action="ManageSystem" method="POST" style="display:inline;">
                        <input type="hidden" name="username"      value="<%= uname %>">
                        <input type="hidden" name="password"      value="<%= upass %>">
                        <input type="hidden" name="opportunityId" value="<%= p.get("opportunityId") %>">
                        <input type="hidden" name="action"        value="restorePosting">
                        <button type="submit" class="btn-en">Restore</button>
                    </form>
                <% } %>
            </td>
        </tr>
        <% } %>
    </table>
</div>
</body>
</html>
