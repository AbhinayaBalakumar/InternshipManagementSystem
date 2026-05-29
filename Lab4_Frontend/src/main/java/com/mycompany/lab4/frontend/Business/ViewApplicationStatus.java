package com.mycompany.lab4.frontend.Business;

import com.mycompany.lab4.frontend.Helper.JwtUtil;
import com.mycompany.lab4.frontend.Helper.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.RequestDispatcher;
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

/**
 * ViewApplicationStatus - Protected. Student views their own application statuses.
 * JWT is sent to backend to identify which student's records to return.
 */
@WebServlet(name = "ViewApplicationStatus", urlPatterns = {"/ViewApplicationStatus"})
public class ViewApplicationStatus extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

        // Call backend REST API - JWT identifies the student automatically
        String result = RestClient.get("/applications/my-status", jwtToken);

        List<Map<String, String>> applications = new ArrayList<>();
        try {
            JsonNode arr = mapper.readTree(result);
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    Map<String, String> app = new LinkedHashMap<>();
                    app.put("applicationId",  node.path("applicationId").asText());
                    app.put("jobTitle",       node.path("jobTitle").asText());
                    app.put("companyName",    node.path("companyName").asText());
                    app.put("applicationDate",node.path("applicationDate").asText());
                    app.put("status",         node.path("status").asText());
                    applications.add(app);
                }
            }
        } catch (Exception e) {
            request.setAttribute("errorMsg", "Could not load applications: " + e.getMessage());
        }

        request.setAttribute("applications", applications);
        request.setAttribute("studentName",  session.getAttribute("studentName"));
        request.getRequestDispatcher("Applicationstatus.jsp").forward(request, response);
    }
}
