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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ViewApplicants", urlPatterns = {"/ViewApplicants"})
public class ViewApplicants extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadAndForward(request, response);
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

        String jwtToken      = (String) session.getAttribute("jwt");
        String applicationId = request.getParameter("applicationId");
        String newStatus     = request.getParameter("status");
        String opportunityId = request.getParameter("opportunityId");

        // Step 1: Update status in Employer_IPMS (Backend 2)
        String jsonBody = "{\"applicationId\":" + applicationId
                        + ",\"status\":\"" + newStatus + "\"}";
        RestClient.post("/applications/update-status", jsonBody, jwtToken);

        // Step 2: Sync status update to Student_IPMS (Backend 1)
        // so the student can see their updated status
        String syncBody = "{\"applicationId\":" + applicationId
                        + ",\"status\":\"" + newStatus + "\"}";
        RestClient.postToBase(RestClient.STUDENT_BASE, "/sync/application-status", syncBody);

        // Reload the applicants page
        response.sendRedirect("ViewApplicants?opportunityId=" + opportunityId);
    }

    private void loadAndForward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("jwt") == null
                || !"EMPLOYER".equals(session.getAttribute("role"))) {
            response.sendRedirect("employerLogin.html");
            return;
        }

        String jwtToken      = (String) session.getAttribute("jwt");
        String opportunityId = request.getParameter("opportunityId");

        if (opportunityId == null) {
            response.sendRedirect("EmployerDashboard");
            return;
        }

        String result = RestClient.get("/applications/by-opportunity?opportunityId=" + opportunityId, jwtToken);
        List<Map<String, String>> applicants = new ArrayList<>();
        try {
            JsonNode arr = mapper.readTree(result);
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    Map<String, String> a = new LinkedHashMap<>();
                    a.put("applicationId",   node.path("applicationId").asText());
                    a.put("studentName",     node.path("studentName").asText());
                    a.put("applicationDate", node.path("applicationDate").asText());
                    a.put("status",          node.path("status").asText());
                    applicants.add(a);
                }
            }
        } catch (Exception e) {
            request.setAttribute("errorMsg", "Could not load applicants: " + e.getMessage());
        }

        request.setAttribute("applicants",    applicants);
        request.setAttribute("opportunityId", opportunityId);
        request.getRequestDispatcher("viewApplicants.jsp").forward(request, response);
    }
}