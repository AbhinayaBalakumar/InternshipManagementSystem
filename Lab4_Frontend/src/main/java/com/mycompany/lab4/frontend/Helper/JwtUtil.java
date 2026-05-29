package com.mycompany.lab4.frontend.Helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/**
 * JwtUtil - Helper class for creating and validating JSON Web Tokens.
 * The frontend microservice generates a JWT after successful login.
 * The JWT is then sent to backend microservices in the Authorization header.
 *
 * Token payload includes: username, role (STUDENT / EMPLOYER / COORDINATOR), userId
 */
public class JwtUtil {

    // Secret key - must match the same key used in backend microservices
    private static final String SECRET = "IPMS_Lab4_SecretKey_2024_SharedKey!";
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Token valid for 2 hours (milliseconds)
    private static final long EXPIRATION_MS = 2 * 60 * 60 * 1000L;

    /**
     * Generate a JWT token for an authenticated user.
     *
     * @param username the authenticated user's username
     * @param role     STUDENT, EMPLOYER, or COORDINATOR
     * @param userId   the user's database ID
     * @return signed JWT string
     */
    public static String generateToken(String username, String role, int userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate a JWT token and return its claims.
     *
     * @param token the JWT string
     * @return Claims object, or null if invalid/expired
     */
    public static Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extract the username (subject) from a token.
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * Extract the role from a token.
     */
    public static String getRoleFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? (String) claims.get("role") : null;
    }

    /**
     * Extract the userId from a token.
     */
    public static int getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? (int) claims.get("userId") : -1;
    }

    /**
     * Check if a token is still valid (not expired, properly signed).
     */
    public static boolean isTokenValid(String token) {
        return validateToken(token) != null;
    }
}