package com.mycompany.lab4.employer.Endpoint;

import com.mycompany.lab4.employer.Helper.JsonUtil;
import com.mycompany.lab4.employer.Helper.StudentInfo;
import com.mycompany.lab4.employer.Helper.EmployerInfo;
import com.mycompany.lab4.employer.Helper.Opportunity;
import com.mycompany.lab4.employer.Helper.Application;
import com.mycompany.lab4.employer.Persistence.Student_CRUD;
import com.mycompany.lab4.employer.Persistence.Employer_CRUD;
import com.mycompany.lab4.employer.Persistence.Opportunity_CRUD;
import com.mycompany.lab4.employer.Persistence.Application_CRUD;
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
 * SyncEndpoint - Syncs registrations, postings and applications into Employer_IPMS.
 * POST /api/sync/student      - sync new student
 * POST /api/sync/employer     - sync new employer
 * POST /api/sync/opportunity  - sync new job posting
 * POST /api/sync/application  - sync new application
 * GET  /api/sync/updateEmployerStatus?employerId=X&status=Y   - coordinator disables/enables employer
 * GET  /api/sync/updatePostingStatus?opportunityId=X&status=Y - coordinator removes/restores posting
 */
@WebServlet(name = "SyncEndpoint", urlPatterns = {"/api/sync/*"})
public class SyncEndpoint extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Handles GET requests from the coordinator to update employer/posting status.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        if ("/updateEmployerStatus".equals(pathInfo)) {
            String employerIdStr = request.getParameter("employerId");
            String status        = request.getParameter("status");
            if (employerIdStr != null && status != null) {
                boolean updated = Employer_CRUD.updateStatus(Integer.parseInt(employerIdStr), status);
                if (updated) {
                    JsonUtil.sendJson(response, "{\"message\":\"Employer status updated in Employer_IPMS\"}");
                } else {
                    JsonUtil.sendError(response, 500, "Failed to update employer status");
                }
            } else {
                JsonUtil.sendError(response, 400, "employerId and status required");
            }

        } else if ("/updatePostingStatus".equals(pathInfo)) {
            String opportunityIdStr = request.getParameter("opportunityId");
            String status           = request.getParameter("status");
            if (opportunityIdStr != null && status != null) {
                boolean updated = Opportunity_CRUD.updateStatus(Integer.parseInt(opportunityIdStr), status);
                if (updated) {
                    JsonUtil.sendJson(response, "{\"message\":\"Posting status updated in Employer_IPMS\"}");
                } else {
                    JsonUtil.sendError(response, 500, "Failed to update posting status");
                }
            } else {
                JsonUtil.sendError(response, 400, "opportunityId and status required");
            }

        } else {
            JsonUtil.sendError(response, 404, "Unknown sync GET endpoint: " + pathInfo);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String body = readBody(request);
        JsonNode json;
        try { json = mapper.readTree(body); }
        catch (Exception e) { JsonUtil.sendError(response, 400, "Invalid JSON"); return; }

        if ("/student".equals(pathInfo)) {
            StudentInfo s = new StudentInfo(
                    0,
                    json.path("firstName").asText(""),
                    json.path("lastName").asText(""),
                    json.path("email").asText(""),
                    json.path("program").asText(""),
                    json.path("gpa").asDouble(0.0),
                    json.path("username").asText(""),
                    json.path("password").asText(""),
                    "ACTIVE"
            );
            Student_CRUD.create(s);
            JsonUtil.sendJson(response, "{\"message\":\"Student synced to Employer_IPMS\"}");

        } else if ("/employer".equals(pathInfo)) {
            EmployerInfo emp = new EmployerInfo(
                    0,
                    json.path("companyName").asText(""),
                    json.path("industry").asText(""),
                    json.path("location").asText(""),
                    json.path("username").asText(""),
                    json.path("password").asText(""),
                    "ACTIVE"
            );
            Employer_CRUD.create(emp);
            JsonUtil.sendJson(response, "{\"message\":\"Employer synced to Employer_IPMS\"}");

        } else if ("/opportunity".equals(pathInfo)) {
            Opportunity opp = new Opportunity(
                    0,
                    json.path("employerId").asInt(0),
                    json.path("companyName").asText(""),
                    json.path("jobTitle").asText(""),
                    json.path("jobType").asText(""),
                    json.path("location").asText(""),
                    json.path("requiredSkills").asText(""),
                    json.path("deadline").asText(""),
                    json.path("description").asText(""),
                    "OPEN"
            );
            Opportunity_CRUD.create(opp);
            JsonUtil.sendJson(response, "{\"message\":\"Opportunity synced to Employer_IPMS\"}");

        } else if ("/application".equals(pathInfo)) {
            Application app = new Application(
                    json.path("studentId").asInt(0),
                    json.path("opportunityId").asInt(0),
                    json.path("jobTitle").asText(""),
                    json.path("companyName").asText(""),
                    json.path("applicationDate").asText(""),
                    "Submitted"
            );
            Application_CRUD.create(app);
            JsonUtil.sendJson(response, "{\"message\":\"Application synced to Employer_IPMS\"}");

        } else {
            JsonUtil.sendError(response, 404, "Unknown sync endpoint: " + pathInfo);
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