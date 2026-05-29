package com.mycompany.lab4.frontend.Business;

import com.mycompany.lab4.frontend.Helper.JwtUtil;
import com.mycompany.lab4.frontend.Helper.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "PostOpportunity", urlPatterns = {"/PostOpportunity"})
public class PostOpportunity extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"EMPLOYER".equals(session.getAttribute("role"))) {
            response.sendRedirect("employerLogin.html");
            return;
        }
        request.getRequestDispatcher("postOpportunity.html").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("jwt") == null
                || !"EMPLOYER".equals(session.getAttribute("role"))) {
            response.sendRedirect("employerLogin.html");
            return;
        }

        String jwtToken = (String) session.getAttribute("jwt");
        if (!JwtUtil.isTokenValid(jwtToken)) {
            session.invalidate();
            response.sendRedirect("employerLogin.html");
            return;
        }

        String jobTitle       = request.getParameter("jobTitle");
        String jobType        = request.getParameter("jobType");
        String location       = request.getParameter("location");
        String requiredSkills = request.getParameter("requiredSkills");
        String deadline       = request.getParameter("deadline");
        String description    = request.getParameter("description");
        String companyName    = (String) session.getAttribute("companyName");
        int    userId         = (int) session.getAttribute("userId");

        String jsonBody = "{"
            + "\"jobTitle\":\""       + escapeJson(jobTitle)       + "\","
            + "\"jobType\":\""        + escapeJson(jobType)        + "\","
            + "\"location\":\""       + escapeJson(location)       + "\","
            + "\"requiredSkills\":\"" + escapeJson(requiredSkills) + "\","
            + "\"deadline\":\""       + escapeJson(deadline)       + "\","
            + "\"description\":\""    + escapeJson(description)    + "\""
            + "}";

        // Step 1: POST to Employer backend (Employer_IPMS)
        String result = RestClient.postToEmployer("/opportunities", jsonBody, jwtToken);

        try {
            JsonNode json = mapper.readTree(result);
            if (json.has("error")) {
                request.setAttribute("message", "Failed to post opportunity: " + json.get("error").asText());
                request.getRequestDispatcher("actionResult.jsp").forward(request, response);
                return;
            }

            // Step 2: Build sync body with all fields including companyName and employerId
            // so Student_IPMS and Coordinator_IPMS can show it in searches
            String opportunityId = json.path("opportunityId").asText("0");
            String syncBody = "{"
                + "\"opportunityId\":"    + opportunityId                  + ","
                + "\"employerId\":"       + userId                         + ","
                + "\"companyName\":\""    + escapeJson(companyName)        + "\","
                + "\"jobTitle\":\""       + escapeJson(jobTitle)           + "\","
                + "\"jobType\":\""        + escapeJson(jobType)            + "\","
                + "\"location\":\""       + escapeJson(location)           + "\","
                + "\"requiredSkills\":\"" + escapeJson(requiredSkills)     + "\","
                + "\"deadline\":\""       + escapeJson(deadline)           + "\","
                + "\"description\":\""    + escapeJson(description)        + "\","
                + "\"status\":\"OPEN\""
                + "}";

            // Step 3: Sync to Student_IPMS (Backend 1) so students can search it
            RestClient.postToBase(RestClient.STUDENT_BASE, "/sync/opportunity", syncBody);

            // Step 4: Sync to Coordinator_IPMS (Backend 3) so coordinator can see it
            RestClient.postToBase(RestClient.COORDINATOR_BASE, "/sync/opportunity", syncBody);

            request.setAttribute("message", "Opportunity posted successfully! ID: " + opportunityId);

        } catch (Exception e) {
            request.setAttribute("message", "Unexpected error: " + e.getMessage());
        }

        request.getRequestDispatcher("actionResult.jsp").forward(request, response);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}