package com.mycompany.lab4.frontend.Business;

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
import java.util.List;
import java.util.Map;

/**
 * SearchOpportunities - Fetches opportunities from the backend REST API.
 * Public endpoint: no login required to search.
 * If logged in, shows "Apply" buttons alongside results.
 */
@WebServlet(name = "SearchOpportunities", urlPatterns = {"/SearchOpportunities"})
public class SearchOpportunities extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String company = request.getParameter("company");
        String title   = request.getParameter("title");
        String skills  = request.getParameter("skills");

        // Build query string
        StringBuilder query = new StringBuilder("/opportunities?");
        if (company != null && !company.isEmpty()) query.append("company=").append(company).append("&");
        if (title   != null && !title.isEmpty())   query.append("title=").append(title).append("&");
        if (skills  != null && !skills.isEmpty())  query.append("skills=").append(skills).append("&");

        // Get JWT from session if available (search works without login too)
        HttpSession session  = request.getSession(false);
        String      jwtToken = (session != null) ? (String) session.getAttribute("jwt") : null;

        // Call backend REST API
        String result = RestClient.get(query.toString(), jwtToken);

        List<Map<String, String>> opportunities = new ArrayList<>();
try {
    JsonNode arr = mapper.readTree(result);
    if (arr.isArray()) {
        for (JsonNode node : arr) {
            Map<String, String> opp = new java.util.LinkedHashMap<>();
            opp.put("opportunityId",  node.path("opportunityId").asText());
            opp.put("companyName",    node.path("companyName").asText());
            opp.put("jobTitle",       node.path("jobTitle").asText());
            opp.put("jobType",        node.path("jobType").asText());
            opp.put("location",       node.path("location").asText());
            opp.put("requiredSkills", node.path("requiredSkills").asText());
            opp.put("deadline",       node.path("deadline").asText());
            opp.put("description",    node.path("description").asText());
            opportunities.add(opp);
        }
    } else {
        request.setAttribute("errorMsg", "Backend returned: " + result);
    }
} catch (Exception e) {
    request.setAttribute("errorMsg", "Could not load opportunities: " + result);
}
        request.setAttribute("opportunities", opportunities);
        request.setAttribute("company",       company);
        request.setAttribute("title",         title);
        request.setAttribute("skills",        skills);
        request.setAttribute("isLoggedIn",    session != null && session.getAttribute("jwt") != null);
        request.setAttribute("role",          session != null ? session.getAttribute("role") : null);

        RequestDispatcher rd = request.getRequestDispatcher("Availableopportunities.jsp");
        rd.forward(request, response);
    }
}
