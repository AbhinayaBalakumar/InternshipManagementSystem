package com.mycompany.lab4.student.Helper;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JsonUtil - Helper for writing JSON responses in REST API endpoints.
 * Keeps endpoint code clean - just call sendJson() or sendError().
 */
public class JsonUtil {

    /**
     * Write a JSON string as the HTTP response with status 200 OK.
     */
    public static void sendJson(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(json);
    }

    /**
     * Write a JSON error response with a specific HTTP status code.
     * e.g. sendError(response, 401, "unauthorized")
     */
    public static void sendError(HttpServletResponse response, int statusCode, String message)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(statusCode);
        response.getWriter().write("{\"error\":\"" + escapeJson(message) + "\"}");
    }

    /** Escape special characters for safe JSON string embedding. */
    public static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /** Build a simple key-value JSON object string. */
    public static String kv(Object... pairs) {
        if (pairs.length % 2 != 0) throw new IllegalArgumentException("Must pass key-value pairs");
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < pairs.length; i += 2) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(pairs[i]).append("\":");
            Object val = pairs[i + 1];
            if (val instanceof Number || val instanceof Boolean) {
                sb.append(val);
            } else {
                sb.append("\"").append(escapeJson(String.valueOf(val))).append("\"");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
