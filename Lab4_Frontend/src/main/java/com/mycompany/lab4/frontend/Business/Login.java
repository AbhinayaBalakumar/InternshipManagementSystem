package com.mycompany.lab4.frontend.Business;

import com.mycompany.lab4.frontend.Helper.JwtUtil;
import com.mycompany.lab4.frontend.Helper.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Login - Frontend servlet that handles student login.
 *
 * Flow:
 *  1. Receive username + password from login form
 *  2. Call backend REST API POST /api/auth/student-login with credentials
 *  3. If authenticated, backend returns userId and studentName
 *  4. Frontend generates a JWT with username, role=STUDENT, userId
 *  5. JWT is stored in:
 *       - HttpSession attribute "jwt"   (for server-side JSP use)
 *       - Cookie "jwt"                  (for JavaScript fetch calls)
 *  6. Redirect to student dashboard
 */
@WebServlet(name = "Login", urlPatterns = {"/Login"})
public class Login extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Step 1: Call backend REST API to authenticate
        String jsonBody = "{\"username\":\"" + escapeJson(username)
                        + "\",\"password\":\"" + escapeJson(password) + "\"}";
        String result = RestClient.post("/auth/student-login", jsonBody, null);

        try {
            JsonNode json = mapper.readTree(result);

            if (json.has("error") || !json.has("userId")) {
                // Authentication failed
                request.setAttribute("errorMsg", "Invalid username or password.");
                RequestDispatcher rd = request.getRequestDispatcher("loginFailed.jsp");
                rd.forward(request, response);
                return;
            }

            // Step 2: Extract user info from backend response
            int    userId      = json.get("userId").asInt();
            String studentName = json.get("studentName").asText();
            String email       = json.has("email") ? json.get("email").asText() : "";
            String program     = json.has("program") ? json.get("program").asText() : "";

            // Step 3: Generate JWT
            String token = JwtUtil.generateToken(username, "STUDENT", userId);

            // Step 4: Store JWT in session
            HttpSession session = request.getSession();
            session.setAttribute("jwt",         token);
            session.setAttribute("username",    username);
            session.setAttribute("studentName", studentName);
            session.setAttribute("userId",      userId);
            session.setAttribute("role",        "STUDENT");
            session.setAttribute("email",       email);
            session.setAttribute("program",     program);

            // Step 5: Also store JWT in a cookie for JavaScript fetch calls
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);   // prevents XSS access from JS
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(2 * 60 * 60); // 2 hours
            response.addCookie(jwtCookie);

            // Step 6: Forward to student dashboard
            request.setAttribute("studentName", studentName);
            request.setAttribute("email",       email);
            request.setAttribute("program",     program);
            request.setAttribute("userId",      userId);
            RequestDispatcher rd = request.getRequestDispatcher("studentDashboard.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMsg", "Login service unavailable. Please try again.");
            request.getRequestDispatcher("loginFailed.jsp").forward(request, response);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
