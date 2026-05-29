package com.mycompany.lab4.frontend.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RestClient {

    // Read service hostnames from environment variables (set in docker-compose / Kubernetes)
    // Falls back to localhost for local development
    private static final String STUDENT_HOST     = System.getenv("STUDENT_SERVICE")     != null ? System.getenv("STUDENT_SERVICE")     : "localhost:8081";
    private static final String EMPLOYER_HOST    = System.getenv("EMPLOYER_SERVICE")    != null ? System.getenv("EMPLOYER_SERVICE")    : "localhost:8082";
    private static final String COORDINATOR_HOST = System.getenv("COORDINATOR_SERVICE") != null ? System.getenv("COORDINATOR_SERVICE") : "localhost:8083";

    public static final String STUDENT_BASE     = "http://" + STUDENT_HOST     + "/student/api";
    public static final String EMPLOYER_BASE    = "http://" + EMPLOYER_HOST    + "/employer/api";
    public static final String COORDINATOR_BASE = "http://" + COORDINATOR_HOST + "/coordinator/api";

    private static String resolveBase(String endpoint) {
        if (endpoint.startsWith("/coordinator/"))                return COORDINATOR_BASE;
        if (endpoint.startsWith("/auth/employer-login"))         return EMPLOYER_BASE;
        if (endpoint.startsWith("/auth/student-login"))          return STUDENT_BASE;
        if (endpoint.startsWith("/employers/"))                  return EMPLOYER_BASE;
        if (endpoint.startsWith("/students/"))                   return STUDENT_BASE;
        if (endpoint.startsWith("/my-postings"))                 return EMPLOYER_BASE;
        if (endpoint.startsWith("/apply"))                       return STUDENT_BASE;
        if (endpoint.startsWith("/applications/my-status"))      return STUDENT_BASE;
        if (endpoint.startsWith("/applications/by-opportunity")) return EMPLOYER_BASE;
        if (endpoint.startsWith("/applications/update-status"))  return EMPLOYER_BASE;
        if (endpoint.startsWith("/opportunities"))               return STUDENT_BASE;
        if (endpoint.startsWith("/sync/"))                       return STUDENT_BASE;
        return STUDENT_BASE;
    }

    public static String get(String endpoint, String jwtToken) {
        try {
            URL url = new URL(resolveBase(endpoint) + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (jwtToken != null && !jwtToken.isEmpty())
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int status = conn.getResponseCode();
            if (status == 401) return "{\"error\":\"unauthorized\"}";
            return readResponse(conn, status);
        } catch (IOException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public static String post(String endpoint, String jsonBody, String jwtToken) {
        try {
            URL url = new URL(resolveBase(endpoint) + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            if (jwtToken != null && !jwtToken.isEmpty())
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }
            int status = conn.getResponseCode();
            if (status == 401) return "{\"error\":\"unauthorized\"}";
            return readResponse(conn, status);
        } catch (IOException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public static String postToEmployer(String endpoint, String jsonBody, String jwtToken) {
        try {
            URL url = new URL(EMPLOYER_BASE + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            if (jwtToken != null && !jwtToken.isEmpty())
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }
            int status = conn.getResponseCode();
            if (status == 401) return "{\"error\":\"unauthorized\"}";
            return readResponse(conn, status);
        } catch (IOException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public static String postToBase(String base, String endpoint, String jsonBody) {
        try {
            URL url = new URL(base + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }
            return readResponse(conn, conn.getResponseCode());
        } catch (IOException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public static String getCoordinator(String endpoint, String user, String pass) {
        try {
            URL url = new URL(COORDINATOR_BASE + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-Coord-User", user);
            conn.setRequestProperty("X-Coord-Pass", pass);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            return readResponse(conn, conn.getResponseCode());
        } catch (IOException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public static String postCoordinator(String endpoint, String formParams, String user, String pass) {
        try {
            URL url = new URL(COORDINATOR_BASE + endpoint + "?" + formParams);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-Coord-User", user);
            conn.setRequestProperty("X-Coord-Pass", pass);
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.getOutputStream().write(new byte[0]);
            return readResponse(conn, conn.getResponseCode());
        } catch (IOException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    private static String readResponse(HttpURLConnection conn, int status) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
            status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream(),
            StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }
}
