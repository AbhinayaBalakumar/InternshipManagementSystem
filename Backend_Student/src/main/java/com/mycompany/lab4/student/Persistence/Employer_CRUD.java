package com.mycompany.lab4.student.Persistence;

import com.mycompany.lab4.student.Helper.EmployerInfo;
import java.sql.*;
import java.util.*;

/**
 * Employer_CRUD - Handles all database operations for the EMPLOYER table.
 * Ported from Lab 3 Persistence layer - same SQL, new package.
 */
public class Employer_CRUD {

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

    /** Authenticate employer by username and password. */
    public static EmployerInfo read(String username, String password) {
        EmployerInfo bean = null;
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM EMPLOYER WHERE username = ? AND status = 'ACTIVE'");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getString("password").equals(password)) {
                bean = new EmployerInfo(
                    rs.getInt("employerId"),
                    rs.getString("companyName"),
                    rs.getString("industry"),
                    rs.getString("location"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("status")
                );
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return bean;
    }

    /** Register a new employer account. */
    public static boolean create(EmployerInfo emp) {
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO EMPLOYER (companyName, industry, location, username, password, status) " +
                "VALUES (?, ?, ?, ?, ?, 'ACTIVE')");
            ps.setString(1, emp.getCompanyName());
            ps.setString(2, emp.getIndustry());
            ps.setString(3, emp.getLocation());
            ps.setString(4, emp.getUsername());
            ps.setString(5, emp.getPassword());
            ps.executeUpdate();
            con.close();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    /** Retrieve all employers (coordinator use case). */
    public static List<EmployerInfo> readAll() {
        List<EmployerInfo> list = new ArrayList<>();
        try {
            Connection con = getCon();
            ResultSet rs = con.prepareStatement("SELECT * FROM EMPLOYER").executeQuery();
            while (rs.next()) {
                list.add(new EmployerInfo(
                    rs.getInt("employerId"),
                    rs.getString("companyName"),
                    rs.getString("industry"),
                    rs.getString("location"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("status")
                ));
            }
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return list;
    }

    /** Enable or disable an employer account. */
    public static boolean updateStatus(int employerId, String status) {
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE EMPLOYER SET status = ? WHERE employerId = ?");
            ps.setString(1, status);
            ps.setInt(2, employerId);
            ps.executeUpdate();
            con.close();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
