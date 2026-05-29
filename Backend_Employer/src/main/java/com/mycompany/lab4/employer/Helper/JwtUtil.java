package com.mycompany.lab4.employer.Helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.nio.charset.StandardCharsets;

/**
 * JwtUtil - Validates and parses JWT tokens received from the frontend.
 *
 * The frontend generates the JWT after a successful login and sends it
 * in the Authorization header as: "Bearer <token>"
 *
 * The backend only VALIDATES tokens - it never creates them.
 * The secret key must match the one used by the frontend (Lab 4 requirement).
 */
public class JwtUtil {

    /**
     * Shared secret key - must be the same string used by the frontend's JwtUtil.
     * Minimum 32 characters for HS256.
     */
    private static final String SECRET = "IPMS_Lab4_SecretKey_2024_SharedKey!";

    private static Key getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extract the Bearer token from an Authorization header value.
     * e.g. "Bearer eyJhb..." → "eyJhb..."
     * Returns null if the header is missing or malformed.
     */
    public static String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    /**
     * Validate a JWT token.
     * Returns true if the token is well-formed, signed correctly, and not expired.
     */
    public static boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract all claims from a valid JWT token.
     * Call isTokenValid() first to avoid exceptions.
     */
    private static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getKey())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    /**
     * Get the user ID (studentId or employerId) from the token.
     * The frontend stores it as the "sub" (subject) claim.
     */
    public static int getUserIdFromToken(String token) {
        try {
            return Integer.parseInt(getClaims(token).getSubject());
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Get the role from the token ("STUDENT", "EMPLOYER", or "COORDINATOR").
     * The frontend stores it as the "role" claim.
     */
    public static String getRoleFromToken(String token) {
        try {
            return (String) getClaims(token).get("role");
        } catch (Exception e) {
            return null;
        }
    }
}
