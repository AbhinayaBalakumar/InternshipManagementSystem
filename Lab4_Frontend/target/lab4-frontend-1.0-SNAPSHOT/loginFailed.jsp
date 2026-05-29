<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Login Failed - IPMS</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }
        .container { max-width: 400px; margin: 80px auto; background: white;
                     padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); text-align: center; }
        h1 { color: #dc3545; }
        .error-msg { background: #f8d7da; border: 1px solid #f5c6cb; border-radius: 4px;
                     padding: 12px; color: #721c24; margin: 15px 0; }
        .btn { display: inline-block; padding: 10px 22px; margin: 6px;
               background-color: #004085; color: white; text-decoration: none;
               border-radius: 5px; font-size: 14px; }
    </style>
</head>
<body>
<div class="container">
    <h1>&#10060; Login Failed</h1>
    <div class="error-msg">
        <% String errMsg = (String) request.getAttribute("errorMsg");
           if (errMsg != null && !errMsg.isEmpty()) { %>
            <%= errMsg %>
        <% } else { %>
            Invalid username or password. Please try again.
        <% } %>
    </div>
    <p style="font-size:13px;color:#555;">JWT was not issued &mdash; authentication unsuccessful.</p>
    <a href="login.html" class="btn">&#8592; Try Again</a>
    <a href="index.html" class="btn" style="background:#666;">Home</a>
</div>
</body>
</html>
