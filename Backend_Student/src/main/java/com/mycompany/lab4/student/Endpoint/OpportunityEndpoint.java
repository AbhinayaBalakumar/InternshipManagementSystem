package com.mycompany.lab4.student.Endpoint;

import com.mycompany.lab4.student.Business.OpportunityBusiness;
import com.mycompany.lab4.student.Helper.JsonUtil;
import com.mycompany.lab4.student.Helper.JwtUtil;
import com.mycompany.lab4.student.Helper.Opportunity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * OpportunityEndpoint - REST API for internship/job opportunities.
 *
 * Routes:
 *   GET  /api/opportunities   - Search all open opportunities (PUBLIC)
 *   POST /api/opportunities   - Post new opportunity (EMPLOYER JWT required)
 *   GET  /api/my-postings     - Get this employer's own postings (EMPLOYER JWT required)
 */
@WebServlet(name = "OpportunityEndpoint", urlPatterns = {"/api/opportunities", "/api/my-postings"})
public class OpportunityEndpoint extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servlet = request.getServletPath();

        // GET /api/my-postings - employer views their own postings
        if ("/api/my-postings".equals(servlet)) {
            String token = JwtUtil.extractBearerToken(request.getHeader("Authorization"));
            if (token == null || !JwtUtil.isTokenValid(token)) {
                JsonUtil.sendError(response, 401, "unauthorized");
                return;
            }
            if (!"EMPLOYER".equals(JwtUtil.getRoleFromToken(token))) {
                JsonUtil.sendError(response, 403, "Employers only");
                return;
            }
            int employerId = JwtUtil.getUserIdFromToken(token);
            List<Opportunity> myPostings = OpportunityBusiness.getByEmployer(employerId);
            JsonUtil.sendJson(response, buildOpportunityArray(myPostings));
            return;
        }

        // GET /api/opportunities - public search
        String company = request.getParameter("company");
        String title   = request.getParameter("title");
        String skills  = request.getParameter("skills");

        List<Opportunity> opportunities = OpportunityBusiness.search(company, title, skills);
        JsonUtil.sendJson(response, buildOpportunityArray(opportunities));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = JwtUtil.extractBearerToken(request.getHeader("Authorization"));
        if (token == null || !JwtUtil.isTokenValid(token)) {
            JsonUtil.sendError(response, 401, "unauthorized");
            return;
        }
        if (!"EMPLOYER".equals(JwtUtil.getRoleFromToken(token))) {
            JsonUtil.sendError(response, 403, "Only employers can post opportunities");
            return;
        }

        int employerId = JwtUtil.getUserIdFromToken(token);

        String body = readBody(request);
        JsonNode json;
        try {
            json = mapper.readTree(body);
        } catch (Exception e) {
            JsonUtil.sendError(response, 400, "Invalid JSON body");
            return;
        }

        String jobTitle       = json.path("jobTitle").asText(null);
        String jobType        = json.path("jobType").asText("Internship");
        String location       = json.path("location").asText("");
        String requiredSkills = json.path("requiredSkills").asText("");
        String deadline       = json.path("deadline").asText("");
        String description    = json.path("description").asText("");

        if (jobTitle == null || jobTitle.trim().isEmpty()) {
            JsonUtil.sendError(response, 400, "jobTitle is required");
            return;
        }

        int newId = OpportunityBusiness.post(employerId, jobTitle, jobType, location,
                                              requiredSkills, deadline, description);
        if (newId > 0) {
            JsonUtil.sendJson(response, JsonUtil.kv(
                "opportunityId", newId,
                "message",       "Opportunity posted successfully"
            ));
        } else {
            JsonUtil.sendError(response, 500, "Failed to post opportunity");
        }
    }

    private String buildOpportunityArray(List<Opportunity> opportunities) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < opportunities.size(); i++) {
            Opportunity o = opportunities.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"opportunityId\":").append(o.getOpportunityId()).append(",")
              .append("\"companyName\":\"").append(JsonUtil.escapeJson(o.getCompanyName())).append("\",")
              .append("\"jobTitle\":\"").append(JsonUtil.escapeJson(o.getJobTitle())).append("\",")
              .append("\"jobType\":\"").append(JsonUtil.escapeJson(o.getJobType())).append("\",")
              .append("\"location\":\"").append(JsonUtil.escapeJson(o.getLocation())).append("\",")
              .append("\"requiredSkills\":\"").append(JsonUtil.escapeJson(o.getRequiredSkills())).append("\",")
              .append("\"deadline\":\"").append(JsonUtil.escapeJson(o.getDeadline())).append("\",")
              .append("\"description\":\"").append(JsonUtil.escapeJson(o.getDescription())).append("\",")
              .append("\"status\":\"").append(JsonUtil.escapeJson(o.getStatus())).append("\"")
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String readBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}