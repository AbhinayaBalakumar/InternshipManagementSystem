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

/**
 * EmployerDashboard - Loads employer's own postings from backend and
 * forwards to employerDashboard.jsp
 */
@WebServlet(name = "EmployerDashboard", urlPatterns = {"/EmployerDashboard"})
public class EmployerDashboard extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("jwt") == null
                || !"EMPLOYER".equals(session.getAttribute("role"))) {
            response.sendRedirect("employerLogin.html");
            return;
        }

        String jwtToken    = (String) session.getAttribute("jwt");
        String companyName = (String) session.getAttribute("companyName");

        if (!JwtUtil.isTokenValid(jwtToken)) {
            session.invalidate();
            response.sendRedirect("employerLogin.html");
            return;
        }

        // Fetch this employer's postings from backend
        String result = RestClient.get("/my-postings", jwtToken);
        List<Map<String, String>> postings = new ArrayList<>();
        try {
            JsonNode arr = mapper.readTree(result);
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    Map<String, String> p = new LinkedHashMap<>();
                    p.put("opportunityId",  node.path("opportunityId").asText());
                    p.put("jobTitle",       node.path("jobTitle").asText());
                    p.put("jobType",        node.path("jobType").asText());
                    p.put("location",       node.path("location").asText());
                    p.put("deadline",       node.path("deadline").asText());
                    p.put("status",         node.path("status").asText());
                    postings.add(p);
                }
            }
        } catch (Exception e) {
            request.setAttribute("errorMsg", "Could not load postings: " + e.getMessage());
        }

        request.setAttribute("postings",    postings);
        request.setAttribute("companyName", companyName);
        request.getRequestDispatcher("employerDashboard.jsp").forward(request, response);
    }
}