package com.mycompany.lab4.employer.Persistence;

import com.mycompany.lab4.employer.Helper.Application;
import java.sql.*;
import java.util.*;

/**
 * Application_CRUD - Handles all database operations for the APPLICATION table.
 * DB connection uses environment variables so it works in Docker/Kubernetes.
 */
public class Application_CRUD {

    private static Connection getCon() {
        Connection con = null;
        try {
            String host     = System.getenv("DB_HOST")     != null ? System.getenv("DB_HOST")     : "localhost";
            String port     = System.getenv("DB_PORT")     != null ? System.getenv("DB_PORT")     : "3306";
            String db       = System.getenv("DB_NAME")     != null ? System.getenv("DB_NAME")     : "Employer_IPMS";
            String user     = System.getenv("DB_USER")     != null ? System.getenv("DB_USER")     : "root";
            String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "student123";

            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
                    user, password);
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }

    /**
     * Get a single application by its ID.
     * Used by ApplicationBusiness to get studentId for KubeMQ messaging.
     */
    public static Application readById(int applicationId) {
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT a.*, o.jobTitle, e.companyName FROM APPLICATION a " +
                            "JOIN OPPORTUNITY o ON a.opportunityId = o.opportunityId " +
                            "JOIN EMPLOYER e ON o.employerId = e.employerId " +
                            "WHERE a.applicationId = ?");
            ps.setInt(1, applicationId);
            ResultSet rs = ps.executeQuery();
            Application app = null;
            if (rs.next()) {
                app = new Application(
                        rs.getInt("applicationId"),
                        rs.getInt("studentId"),
                        rs.getInt("opportunityId"),
                        rs.getString("jobTitle"),
                        rs.getString("companyName"),
                        rs.getString("applicationDate"),
                        rs.getString("status")
                );
            }
            con.close();
            return app;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Check if a student has already applied to a specific opportunity.
     */
    public static boolean alreadyApplied(int studentId, int opportunityId) {
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT applicationId FROM APPLICATION " +
                            "WHERE studentId = ? AND opportunityId = ?");
            ps.setInt(1, studentId);
            ps.setInt(2, opportunityId);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            con.close();
            return exists;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    /**
     * Create a new application record.
     * Returns the generated applicationId, or -1 on failure.
     */
    public static int create(Application app) {
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO APPLICATION (studentId, opportunityId, applicationDate, status) " +
                            "VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, app.getStudentId());
            ps.setInt(2, app.getOpportunityId());
            ps.setString(3, app.getApplicationDate());
            ps.setString(4, app.getStatus());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            int newId = keys.next() ? keys.getInt(1) : -1;
            con.close();
            return newId;
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
    }

    /**
     * Get all applications submitted by a specific student.
     */
    public static List<Application> readByStudent(int studentId) {
        List<Application> list = new ArrayList<>();
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT a.*, o.jobTitle, e.companyName FROM APPLICATION a " +
                            "JOIN OPPORTUNITY o ON a.opportunityId = o.opportunityId " +
                            "JOIN EMPLOYER e ON o.employerId = e.employerId " +
                            "WHERE a.studentId = ? ORDER BY a.applicationDate DESC");
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Application(
                        rs.getInt("applicationId"),
                        rs.getInt("studentId"),
                        rs.getInt("opportunityId"),
                        rs.getString("jobTitle"),
                        rs.getString("companyName"),
                        rs.getString("applicationDate"),
                        rs.getString("status")
                ));
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    /**
     * Get all applications for a specific opportunity (employer view).
     */
    public static List<Application> readByOpportunity(int opportunityId) {
        List<Application> list = new ArrayList<>();
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT a.*, CONCAT(s.firstName, ' ', s.lastName) AS studentName, " +
                            "o.jobTitle, e.companyName FROM APPLICATION a " +
                            "JOIN STUDENT s ON a.studentId = s.studentId " +
                            "JOIN OPPORTUNITY o ON a.opportunityId = o.opportunityId " +
                            "JOIN EMPLOYER e ON o.employerId = e.employerId " +
                            "WHERE a.opportunityId = ? ORDER BY a.applicationDate DESC");
            ps.setInt(1, opportunityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Application(
                        rs.getInt("applicationId"),
                        rs.getInt("studentId"),
                        rs.getInt("opportunityId"),
                        rs.getString("jobTitle"),
                        rs.getString("studentName"),
                        rs.getString("applicationDate"),
                        rs.getString("status")
                ));
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    /**
     * Update the status of an application.
     */
    public static boolean updateStatus(int applicationId, String status) {
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE APPLICATION SET status = ? WHERE applicationId = ?");
            ps.setString(1, status);
            ps.setInt(2, applicationId);
            ps.executeUpdate();
            con.close();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}