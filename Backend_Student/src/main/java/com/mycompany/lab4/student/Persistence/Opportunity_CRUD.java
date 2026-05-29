package com.mycompany.lab4.student.Persistence;

import com.mycompany.lab4.student.Helper.Opportunity;
import java.sql.*;
import java.util.*;

/**
 * Opportunity_CRUD - Handles all database operations for the OPPORTUNITY table.
 * Ported from Lab 3 Persistence layer - same SQL, new package.
 */
public class Opportunity_CRUD {

    private static Connection getCon() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String host     = System.getenv("DB_HOST")     != null ? System.getenv("DB_HOST")     : "localhost";
            String port     = System.getenv("DB_PORT")     != null ? System.getenv("DB_PORT")     : "3306";
            String db       = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "Student_IPMS";
            String user     = System.getenv("DB_USER")     != null ? System.getenv("DB_USER")     : "root";
            String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "student123";
            con = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
                user, password);
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }

    /** Search/filter opportunities by company, title, or skills. */
    public static List<Opportunity> readByFilter(String company, String title, String skills) {
        List<Opportunity> list = new ArrayList<>();
        try {
            Connection con = getCon();
            String q = "SELECT o.*, e.companyName FROM OPPORTUNITY o " +
                       "JOIN EMPLOYER e ON o.employerId = e.employerId " +
                       "WHERE o.status = 'OPEN'";
            if (company != null && !company.trim().isEmpty())
                q += " AND e.companyName LIKE '%" + company.trim() + "%'";
            if (title != null && !title.trim().isEmpty())
                q += " AND o.jobTitle LIKE '%" + title.trim() + "%'";
            if (skills != null && !skills.trim().isEmpty())
                q += " AND o.requiredSkills LIKE '%" + skills.trim() + "%'";
            ResultSet rs = con.prepareStatement(q).executeQuery();
            while (rs.next()) {
                list.add(new Opportunity(
                    rs.getInt("opportunityId"),
                    rs.getInt("employerId"),
                    rs.getString("companyName"),
                    rs.getString("jobTitle"),
                    rs.getString("jobType"),
                    rs.getString("location"),
                    rs.getString("requiredSkills"),
                    rs.getString("deadline"),
                    rs.getString("description"),
                    rs.getString("status")
                ));
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    /** Read a single opportunity by ID. */
    public static Opportunity readById(int opportunityId) {
        Opportunity opp = null;
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                "SELECT o.*, e.companyName FROM OPPORTUNITY o " +
                "JOIN EMPLOYER e ON o.employerId = e.employerId " +
                "WHERE o.opportunityId = ?");
            ps.setInt(1, opportunityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                opp = new Opportunity(
                    rs.getInt("opportunityId"),
                    rs.getInt("employerId"),
                    rs.getString("companyName"),
                    rs.getString("jobTitle"),
                    rs.getString("jobType"),
                    rs.getString("location"),
                    rs.getString("requiredSkills"),
                    rs.getString("deadline"),
                    rs.getString("description"),
                    rs.getString("status")
                );
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return opp;
    }


    /** Read all postings by a specific employer (for employer dashboard). */
    public static List<Opportunity> readByEmployer(int employerId) {
        List<Opportunity> list = new ArrayList<>();
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                "SELECT o.*, e.companyName FROM OPPORTUNITY o " +
                "JOIN EMPLOYER e ON o.employerId = e.employerId " +
                "WHERE o.employerId = ? ORDER BY o.opportunityId DESC");
            ps.setInt(1, employerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Opportunity(
                    rs.getInt("opportunityId"),
                    rs.getInt("employerId"),
                    rs.getString("companyName"),
                    rs.getString("jobTitle"),
                    rs.getString("jobType"),
                    rs.getString("location"),
                    rs.getString("requiredSkills"),
                    rs.getString("deadline"),
                    rs.getString("description"),
                    rs.getString("status")
                ));
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }
    /** Create a new job posting. Returns the generated opportunityId, or -1 on failure. */
    public static int create(Opportunity opp) {
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO OPPORTUNITY (employerId, jobTitle, jobType, location, " +
                "requiredSkills, deadline, description, status) VALUES (?,?,?,?,?,?,?,'OPEN')",
                Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, opp.getEmployerId());
            ps.setString(2, opp.getJobTitle());
            ps.setString(3, opp.getJobType());
            ps.setString(4, opp.getLocation());
            ps.setString(5, opp.getRequiredSkills());
            ps.setString(6, opp.getDeadline());
            ps.setString(7, opp.getDescription());
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

    /** Update posting status (OPEN, CLOSED, REMOVED). */
    public static boolean updateStatus(int opportunityId, String status) {
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE OPPORTUNITY SET status = ? WHERE opportunityId = ?");
            ps.setString(1, status);
            ps.setInt(2, opportunityId);
            ps.executeUpdate();
            con.close();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    /** Read all postings (coordinator use). */
    public static List<Opportunity> readAll() {
        List<Opportunity> list = new ArrayList<>();
        try {
            Connection con = getCon();
            ResultSet rs = con.prepareStatement(
                "SELECT o.*, e.companyName FROM OPPORTUNITY o " +
                "JOIN EMPLOYER e ON o.employerId = e.employerId").executeQuery();
            while (rs.next()) {
                list.add(new Opportunity(
                    rs.getInt("opportunityId"),
                    rs.getInt("employerId"),
                    rs.getString("companyName"),
                    rs.getString("jobTitle"),
                    rs.getString("jobType"),
                    rs.getString("location"),
                    rs.getString("requiredSkills"),
                    rs.getString("deadline"),
                    rs.getString("description"),
                    rs.getString("status")
                ));
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }
}