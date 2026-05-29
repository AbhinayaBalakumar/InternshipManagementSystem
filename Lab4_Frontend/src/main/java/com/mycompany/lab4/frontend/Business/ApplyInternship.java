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

@WebServlet(name = "ApplyInternship", urlPatterns = {"/ApplyInternship"})
public class ApplyInternship extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("jwt") == null) {
            response.sendRedirect("login.html");
            return;
        }
        response.sendRedirect("SearchOpportunities");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("jwt") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String jwtToken = (String) session.getAttribute("jwt");
        if (!JwtUtil.isTokenValid(jwtToken)) {
            session.invalidate();
            response.sendRedirect("login.html");
            return;
        }

        String opportunityId = request.getParameter("opportunityId");
        String studentName   = (String) session.getAttribute("studentName");
        int    studentId     = (int) session.getAttribute("userId");

        // Step 1: POST apply to Student backend (Student_IPMS)
        String jsonBody = "{\"opportunityId\":" + opportunityId + "}";
        String result   = RestClient.post("/apply", jsonBody, jwtToken);

        try {
            JsonNode json = mapper.readTree(result);

            if (json.has("error")) {
                String msg = json.get("error").asText();
                if ("unauthorized".equals(msg)) {
                    request.setAttribute("message", "You must be logged in to apply.");
                } else {
                    request.setAttribute("message", "Could not apply: " + msg);
                }
                request.getRequestDispatcher("actionResult.jsp").forward(request, response);
                return;
            }

            // Step 2: Sync application to Employer_IPMS so employer can see applicants
            String applicationId   = json.path("applicationId").asText("0");
            String applicationDate = json.path("applicationDate").asText("");
            String jobTitle        = json.path("jobTitle").asText("");
            String companyName     = json.path("companyName").asText("");

            String syncBody = "{"
                + "\"applicationId\":"   + applicationId   + ","
                + "\"studentId\":"       + studentId       + ","
                + "\"opportunityId\":"   + opportunityId   + ","
                + "\"studentName\":\""   + escapeJson(studentName)   + "\","
                + "\"jobTitle\":\""      + escapeJson(jobTitle)      + "\","
                + "\"companyName\":\""   + escapeJson(companyName)   + "\","
                + "\"applicationDate\":\"" + escapeJson(applicationDate) + "\","
                + "\"status\":\"Submitted\""
                + "}";

            // Sync to Employer_IPMS so employer can see the applicant
            RestClient.postToBase(RestClient.EMPLOYER_BASE, "/sync/application", syncBody);

            // Step 3: Show confirmation
            request.setAttribute("jobTitle",      jobTitle);
            request.setAttribute("companyName",   companyName);
            request.setAttribute("applicationId", applicationId);
            request.setAttribute("studentName",   studentName);
            request.setAttribute("applyDate",     applicationDate);
            request.getRequestDispatcher("Applicationconfirmation.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("message", "An unexpected error occurred: " + e.getMessage());
            request.getRequestDispatcher("actionResult.jsp").forward(request, response);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}