package com.mycompany.lab4.student.Endpoint;

import com.mycompany.lab4.student.Helper.JsonUtil;
import com.mycompany.lab4.student.Helper.StudentInfo;
import com.mycompany.lab4.student.Persistence.Student_CRUD;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "StudentRegisterEndpoint", urlPatterns = {"/api/students/*"})
public class StudentRegisterEndpoint extends HttpServlet {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String body = readBody(request);
        JsonNode json;
        try { json = mapper.readTree(body); }
        catch (Exception e) { JsonUtil.sendError(response, 400, "Invalid JSON body"); return; }

        String firstName = json.path("firstName").asText(null);
        String lastName  = json.path("lastName").asText(null);
        String email     = json.path("email").asText("");
        String program   = json.path("program").asText("");
        double gpa       = json.path("gpa").asDouble(0.0);
        String username  = json.path("username").asText(null);
        String password  = json.path("password").asText(null);

        if (firstName == null || lastName == null || username == null || password == null
                || firstName.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            JsonUtil.sendError(response, 400, "firstName, lastName, username and password are required");
            return;
        }

        StudentInfo s = new StudentInfo(0, firstName, lastName, email, program, gpa, username, password, "ACTIVE");
        boolean ok = Student_CRUD.create(s);

        if (ok) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            JsonUtil.sendJson(response, JsonUtil.kv(
                "message",   "Student registered successfully",
                "firstName", firstName,
                "lastName",  lastName,
                "username",  username
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