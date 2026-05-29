package com.mycompany.lab4.employer.Endpoint;

import com.mycompany.lab4.employer.Business.AuthBusiness;
import com.mycompany.lab4.employer.Helper.EmployerInfo;
import com.mycompany.lab4.employer.Helper.JsonUtil;
import com.mycompany.lab4.employer.Helper.StudentInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * AuthEndpoint - REST API for login authentication.
 *
 * Routes:
 *   POST /api/auth/student-login    - Authenticate student credentials
 *   POST /api/auth/employer-login   - Authenticate employer credentials
 *
 * Request body (JSON):
 *   { "username": "...", "password": "..." }
 *
 * Response (JSON) on success:
 *   Student:  { "userId": 1, "studentName": "...", "email": "...", "program": "..." }
 *   Employer: { "userId": 2, "companyName": "..." }
 *
 * Response on failure:
 *   { "error": "invalid credentials" }
 *
 * NOTE: This endpoint does NOT generate a JWT. That is the frontend's job.
 * The backend only verifies credentials and returns user info.
 */
@WebServlet(name = "AuthEndpoint", urlPatterns = {"/api/auth/*"})
public class AuthEndpoint extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo(); // e.g. "/student-login"

        // Parse JSON body
        String body = readBody(request);
        JsonNode json;
        try {
            json = mapper.readTree(body);
        } catch (Exception e) {
            JsonUtil.sendError(response, 400, "Invalid JSON body");
            return;
        }

        String username = json.path("username").asText(null);
        String password = json.path("password").asText(null);

        if (username == null || password == null) {
            JsonUtil.sendError(response, 400, "Username and password are required");
            return;
        }

        if ("/student-login".equals(pathInfo)) {
            // --- Student authentication ---
            StudentInfo student = AuthBusiness.authenticateStudent(username, password);
            if (student == null) {
                JsonUtil.sendError(response, 401, "invalid credentials");
                return;
            }
            // Return user info - frontend will use this to generate the JWT
            JsonUtil.sendJson(response, JsonUtil.kv(
                "userId",      student.getStudentId(),
                "studentName", student.getFullName(),
                "email",       student.getEmail(),
                "program",     student.getProgram()
            ));

        } else if ("/employer-login".equals(pathInfo)) {
            // --- Employer authentication ---
            EmployerInfo employer = AuthBusiness.authenticateEmployer(username, password);
            if (employer == null) {
                JsonUtil.sendError(response, 401, "invalid credentials");
                return;
            }
            JsonUtil.sendJson(response, JsonUtil.kv(
                "userId",      employer.getEmployerId(),
                "companyName", employer.getCompanyName()
            ));

        } else {
            JsonUtil.sendError(response, 404, "Unknown auth endpoint: " + pathInfo);
        }
    }

    /** Read the full request body as a String. */
    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}
