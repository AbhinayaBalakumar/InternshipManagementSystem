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
 * EmployerLogin - Authenticates employers via REST API and issues JWT.
 */
@WebServlet(name = "EmployerLogin", urlPatterns = {"/EmployerLogin"})
public class EmployerLogin extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("employerLogin.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Call backend REST API to authenticate employer
        String jsonBody = "{\"username\":\"" + escapeJson(username)
                        + "\",\"password\":\"" + escapeJson(password) + "\"}";
        String result = RestClient.post("/auth/employer-login", jsonBody, null);

        try {
            JsonNode json = mapper.readTree(result);

            if (json.has("error") || !json.has("userId")) {
                request.setAttribute("errorMsg", "Invalid employer credentials.");
                request.getRequestDispatcher("loginFailed.jsp").forward(request, response);
                return;
            }

            int    userId       = json.get("userId").asInt();
            String companyName  = json.has("companyName") ? json.get("companyName").asText() : username;

            // Generate JWT with EMPLOYER role
            String token = JwtUtil.generateToken(username, "EMPLOYER", userId);

            // Store in session
            HttpSession session = request.getSession();
            session.setAttribute("jwt",         token);
            session.setAttribute("username",    username);
            session.setAttribute("companyName", companyName);
            session.setAttribute("userId",      userId);
            session.setAttribute("role",        "EMPLOYER");

            // Store in cookie
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(2 * 60 * 60);
            response.addCookie(jwtCookie);

            request.setAttribute("companyName", companyName);
            request.setAttribute("userId",      userId);
            response.sendRedirect("EmployerDashboard");

        } catch (Exception e) {
            request.setAttribute("errorMsg", "Login service unavailable.");
            request.getRequestDispatcher("loginFailed.jsp").forward(request, response);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}