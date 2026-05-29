package com.mycompany.lab4.frontend.Business;

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
 * ManageSystem - Coordinator login and management actions.
 * Credentials: coordinator / coord123 (hardcoded, no JWT)
 * Calls backend /api/coordinator/* endpoints.
 */
@WebServlet(name = "ManageSystem", urlPatterns = {"/ManageSystem"})
public class ManageSystem extends HttpServlet {

    private static final String COORD_USER = "coordinator";
    private static final String COORD_PASS = "coord123";
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("coordinatorLogin.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Authenticate coordinator
        if (!COORD_USER.equals(username) || !COORD_PASS.equals(password)) {
            request.getRequestDispatcher("loginFailed.jsp").forward(request, response);
            return;
        }

        // Store credentials in session
        HttpSession session = request.getSession();
        session.setAttribute("coordUser", username);
        session.setAttribute("coordPass", password);

        // Handle action if present
        String action        = request.getParameter("action");
        String studentId     = request.getParameter("studentId");
        String employerId    = request.getParameter("employerId");
        String opportunityId = request.getParameter("opportunityId");

        if (action != null && !action.equals("dashboard")) {
            String params = "action=" + action;
            if (studentId     != null) params += "&studentId="     + studentId;
            if (employerId    != null) params += "&employerId="    + employerId;
            if (opportunityId != null) params += "&opportunityId=" + opportunityId;
            RestClient.postCoordinator("/coordinator/action", params, username, password);
        }

        // Load all data from backend
        List<Map<String, String>> students  = fetchList("/coordinator/students",  username, password);
        List<Map<String, String>> employers = fetchList("/coordinator/employers", username, password);
        List<Map<String, String>> postings  = fetchList("/coordinator/postings",  username, password);

        request.setAttribute("username",     username);
        request.setAttribute("password",     password);
        request.setAttribute("studentList",  students);
        request.setAttribute("employerList", employers);
        request.setAttribute("postingList",  postings);
        request.getRequestDispatcher("coordinatorDashboard.jsp").forward(request, response);
    }

    private List<Map<String, String>> fetchList(String path, String user, String pass) {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            String result = RestClient.getCoordinator(path, user, pass);
            JsonNode arr = mapper.readTree(result);
            if (arr.isArray()) {
                for (JsonNode node : arr) {
                    Map<String, String> row = new LinkedHashMap<>();
                    node.fields().forEachRemaining(e -> row.put(e.getKey(), e.getValue().asText()));
                    list.add(row);
                }
            }
        } catch (Exception e) {
            System.out.println("ManageSystem fetch error: " + e.getMessage());
        }
        return list;
    }
}