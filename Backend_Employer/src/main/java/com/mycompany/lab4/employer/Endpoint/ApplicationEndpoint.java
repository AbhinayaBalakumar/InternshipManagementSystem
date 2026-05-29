package com.mycompany.lab4.employer.Endpoint;

import com.mycompany.lab4.employer.Business.ApplicationBusiness;
import com.mycompany.lab4.employer.Helper.Application;
import com.mycompany.lab4.employer.Helper.JsonUtil;
import com.mycompany.lab4.employer.Helper.JwtUtil;
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
 * ApplicationEndpoint - REST API for internship applications.
 *
 * Routes:
 *   POST /api/apply                                          - Apply (STUDENT JWT)
 *   GET  /api/applications/my-status                        - My applications (STUDENT JWT)
 *   GET  /api/applications/by-opportunity?opportunityId=X   - View applicants (EMPLOYER JWT)
 *   POST /api/applications/update-status                    - Update app status (EMPLOYER JWT)
 */
@WebServlet(name = "ApplicationEndpoint", urlPatterns = {"/api/apply", "/api/applications/*"})
public class ApplicationEndpoint extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = JwtUtil.extractBearerToken(request.getHeader("Authorization"));
        if (token == null || !JwtUtil.isTokenValid(token)) {
            JsonUtil.sendError(response, 401, "unauthorized");
            return;
        }

        String pathInfo = request.getPathInfo();

        // POST /api/applications/update-status - employer updates application status
        if ("/update-status".equals(pathInfo)) {
            if (!"EMPLOYER".equals(JwtUtil.getRoleFromToken(token))) {
                JsonUtil.sendError(response, 403, "Only employers can update application status");
                return;
            }
            String body = readBody(request);
            JsonNode json;
            try { json = mapper.readTree(body); }
            catch (Exception e) { JsonUtil.sendError(response, 400, "Invalid JSON"); return; }

            int    applicationId = json.path("applicationId").asInt(-1);
            String newStatus     = json.path("status").asText(null);

            if (applicationId <= 0 || newStatus == null) {
                JsonUtil.sendError(response, 400, "applicationId and status required");
                return;
            }

            boolean ok = ApplicationBusiness.updateStatus(applicationId, newStatus);
            if (ok) {
                JsonUtil.sendJson(response, "{\"message\":\"Status updated successfully\"}");
            } else {
                JsonUtil.sendError(response, 500, "Failed to update status");
            }
            return;
        }

        // POST /api/apply - student applies for an opportunity
        if (!"STUDENT".equals(JwtUtil.getRoleFromToken(token))) {
            JsonUtil.sendError(response, 403, "Only students can apply for opportunities");
            return;
        }

        int studentId = JwtUtil.getUserIdFromToken(token);

        String body = readBody(request);
        JsonNode json;
        try { json = mapper.readTree(body); }
        catch (Exception e) { JsonUtil.sendError(response, 400, "Invalid JSON body"); return; }

        int opportunityId = json.path("opportunityId").asInt(-1);
        if (opportunityId <= 0) {
            JsonUtil.sendError(response, 400, "opportunityId is required");
            return;
        }

        Application app = ApplicationBusiness.apply(studentId, opportunityId);
        if (app == null) {
            JsonUtil.sendError(response, 409,
                "Could not apply: opportunity may be closed or you already applied");
            return;
        }

        JsonUtil.sendJson(response, JsonUtil.kv(
            "applicationId",   app.getApplicationId(),
            "jobTitle",        app.getJobTitle(),
            "companyName",     app.getCompanyName(),
            "applicationDate", app.getApplicationDate(),
            "status",          app.getStatus()
        ));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = JwtUtil.extractBearerToken(request.getHeader("Authorization"));
        if (token == null || !JwtUtil.isTokenValid(token)) {
            JsonUtil.sendError(response, 401, "unauthorized");
            return;
        }

        String pathInfo = request.getPathInfo();
        String role     = JwtUtil.getRoleFromToken(token);
        int    userId   = JwtUtil.getUserIdFromToken(token);

        if ("/my-status".equals(pathInfo)) {
            if (!"STUDENT".equals(role)) {
                JsonUtil.sendError(response, 403, "Only students can view their application status");
                return;
            }
            List<Application> apps = ApplicationBusiness.getMyApplications(userId);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < apps.size(); i++) {
                Application a = apps.get(i);
                if (i > 0) sb.append(",");
                sb.append("{")
                  .append("\"applicationId\":").append(a.getApplicationId()).append(",")
                  .append("\"jobTitle\":\"").append(JsonUtil.escapeJson(a.getJobTitle())).append("\",")
                  .append("\"companyName\":\"").append(JsonUtil.escapeJson(a.getCompanyName())).append("\",")
                  .append("\"applicationDate\":\"").append(JsonUtil.escapeJson(a.getApplicationDate())).append("\",")
                  .append("\"status\":\"").append(JsonUtil.escapeJson(a.getStatus())).append("\"")
                  .append("}");
            }
            sb.append("]");
            JsonUtil.sendJson(response, sb.toString());

        } else if ("/by-opportunity".equals(pathInfo)) {
            if (!"EMPLOYER".equals(role)) {
                JsonUtil.sendError(response, 403, "Only employers can view applicants");
                return;
            }
            String oppIdParam = request.getParameter("opportunityId");
            if (oppIdParam == null) {
                JsonUtil.sendError(response, 400, "opportunityId parameter required");
                return;
            }
            int opportunityId = Integer.parseInt(oppIdParam);
            List<Application> apps = ApplicationBusiness.getApplicantsForOpportunity(opportunityId, userId);
            if (apps == null) {
                JsonUtil.sendError(response, 403, "You do not own this opportunity");
                return;
            }
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < apps.size(); i++) {
                Application a = apps.get(i);
                if (i > 0) sb.append(",");
                sb.append("{")
                  .append("\"applicationId\":").append(a.getApplicationId()).append(",")
                  .append("\"studentName\":\"").append(JsonUtil.escapeJson(a.getCompanyName())).append("\",")
                  .append("\"applicationDate\":\"").append(JsonUtil.escapeJson(a.getApplicationDate())).append("\",")
                  .append("\"status\":\"").append(JsonUtil.escapeJson(a.getStatus())).append("\"")
                  .append("}");
            }
            sb.append("]");
            JsonUtil.sendJson(response, sb.toString());

        } else {
            JsonUtil.sendError(response, 404, "Unknown applications endpoint: " + pathInfo);
        }
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