package com.mycompany.lab4.frontend.Business;

import com.mycompany.lab4.frontend.Helper.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "EmployerRegister", urlPatterns = {"/EmployerRegister"})
public class EmployerRegister extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("employerRegister.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String companyName = request.getParameter("companyName");
        String industry    = request.getParameter("industry");
        String location    = request.getParameter("location");
        String username    = request.getParameter("username");
        String password    = request.getParameter("password");

        if (companyName == null || username == null || password == null
                || companyName.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("message", "Company name, username and password are required.");
            request.getRequestDispatcher("actionResult.jsp").forward(request, response);
            return;
        }

        String jsonBody = "{"
            + "\"companyName\":\"" + escapeJson(companyName) + "\","
            + "\"industry\":\""    + escapeJson(industry)    + "\","
            + "\"location\":\""    + escapeJson(location)    + "\","
            + "\"username\":\""    + escapeJson(username)    + "\","
            + "\"password\":\""    + escapeJson(password)    + "\""
            + "}";

        // Step 1: Register in Employer_IPMS (Backend 2)
        String result = RestClient.post("/employers/register", jsonBody, null);

        try {
            JsonNode json = mapper.readTree(result);
            if (json.has("error")) {
                request.setAttribute("message", "Registration failed: " + json.get("error").asText()
                    + " — username may already be taken.");
                request.getRequestDispatcher("actionResult.jsp").forward(request, response);
                return;
            }

            // Step 2: Sync to Student_IPMS (Backend 1) so opportunity joins work
            RestClient.postToBase(RestClient.STUDENT_BASE, "/sync/employer", jsonBody);

            // Step 3: Sync to Coordinator_IPMS (Backend 3) so coordinator can see them
            RestClient.postToBase(RestClient.COORDINATOR_BASE, "/sync/employer", jsonBody);

            // Success
            response.sendRedirect("employerLogin.html?registered=true");

        } catch (Exception e) {
            request.setAttribute("message", "Registration service unavailable: " + e.getMessage());
            request.getRequestDispatcher("actionResult.jsp").forward(request, response);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}