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

@WebServlet(name = "Register", urlPatterns = {"/Register"})
public class Register extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("register.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firstName = request.getParameter("firstName");
        String lastName  = request.getParameter("lastName");
        String email     = request.getParameter("email");
        String program   = request.getParameter("program");
        String gpaStr    = request.getParameter("gpa");
        String username  = request.getParameter("username");
        String password  = request.getParameter("password");

        if (firstName == null || lastName == null || username == null || password == null
                || firstName.trim().isEmpty() || lastName.trim().isEmpty()
                || username.trim().isEmpty()  || password.trim().isEmpty()) {
            request.setAttribute("message", "First name, last name, username and password are required.");
            request.getRequestDispatcher("actionResult.jsp").forward(request, response);
            return;
        }

        double gpa = 0.0;
        try { gpa = Double.parseDouble(gpaStr); } catch (Exception e) { gpa = 0.0; }

        String jsonBody = "{"
            + "\"firstName\":\"" + escapeJson(firstName) + "\","
            + "\"lastName\":\""  + escapeJson(lastName)  + "\","
            + "\"email\":\""     + escapeJson(email)     + "\","
            + "\"program\":\""   + escapeJson(program)   + "\","
            + "\"gpa\":"         + gpa                   + ","
            + "\"username\":\""  + escapeJson(username)  + "\","
            + "\"password\":\""  + escapeJson(password)  + "\""
            + "}";

        // Step 1: Register in Student_IPMS (Backend 1)
        String result = RestClient.post("/students/register", jsonBody, null);

        try {
            JsonNode json = mapper.readTree(result);
            if (json.has("error")) {
                request.setAttribute("message", "Registration failed: " + json.get("error").asText()
                    + " — username may already be taken.");
                request.getRequestDispatcher("actionResult.jsp").forward(request, response);
                return;
            }

            // Step 2: Sync to Employer_IPMS (Backend 2) so applicant joins work
            RestClient.postToBase(RestClient.EMPLOYER_BASE, "/sync/student", jsonBody);

            // Step 3: Sync to Coordinator_IPMS (Backend 3) so coordinator can see them
            RestClient.postToBase(RestClient.COORDINATOR_BASE, "/sync/student", jsonBody);

            // Success
            response.sendRedirect("login.html?registered=true");

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