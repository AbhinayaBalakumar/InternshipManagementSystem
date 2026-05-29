package com.mycompany.lab4.employer.Endpoint;

import com.mycompany.lab4.employer.Helper.JsonUtil;
import com.mycompany.lab4.employer.Helper.EmployerInfo;
import com.mycompany.lab4.employer.Persistence.Employer_CRUD;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "EmployerRegisterEndpoint", urlPatterns = {"/api/employers/*"})
public class EmployerRegisterEndpoint extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String body = readBody(request);
        JsonNode json;
        try { json = mapper.readTree(body); }
        catch (Exception e) { JsonUtil.sendError(response, 400, "Invalid JSON body"); return; }

        String companyName = json.path("companyName").asText(null);
        String industry    = json.path("industry").asText("");
        String location    = json.path("location").asText("");
        String username    = json.path("username").asText(null);
        String password    = json.path("password").asText(null);

        if (companyName == null || username == null || password == null
                || companyName.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            JsonUtil.sendError(response, 400, "companyName, username and password are required");
            return;
        }

        EmployerInfo emp = new EmployerInfo(0, companyName, industry, location, username, password, "ACTIVE");
        boolean ok = Employer_CRUD.create(emp);

        if (ok) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.sendJson(response, JsonUtil.kv(
                "message",     "Employer registered successfully",
                "companyName", companyName,
                "username",    username
            ));
        } else {
            JsonUtil.sendError(response, 409, "Registration failed: username may already be taken");
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