package com.mycompany.lab4.student.Persistence;

import com.mycompany.lab4.student.Helper.StudentInfo;
import java.sql.*;
import java.util.*;

/**
 * Student_CRUD - Handles all database operations for the STUDENT table.
 * Ported from Lab 3 Persistence layer - same SQL, new package.
 * Used by REST API endpoints in the Endpoint layer.
 */
public class Student_CRUD {

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
         	"jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&allowPublicKeyRetrieval=true",
	       user, password);
        } catch (Exception e) {
            System.out.println(e);
        }
        return con;
    }

    /** Authenticate student by username and password. */
    public static StudentInfo read(String username, String password) {
        StudentInfo bean = null;
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM STUDENT WHERE username = ? AND status = 'ACTIVE'");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getString("password").equals(password)) {
                bean = new StudentInfo(
                    rs.getInt("studentId"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("program"),
                    rs.getDouble("gpa"),
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

    /** Register a new student account. */
    public static boolean create(StudentInfo s) {
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO STUDENT (firstName, lastName, email, program, gpa, " +
                "username, password, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')");
            ps.setString(1, s.getFirstName());
            ps.setString(2, s.getLastName());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getProgram());
            ps.setDouble(5, s.getGpa());
            ps.setString(6, s.getUsername());
            ps.setString(7, s.getPassword());
            ps.executeUpdate();
            con.close();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    /** Retrieve all students (coordinator use case). */
    public static List<StudentInfo> readAll() {
        List<StudentInfo> list = new ArrayList<>();
        try {
            Connection con = getCon();
            ResultSet rs = con.prepareStatement("SELECT * FROM STUDENT").executeQuery();
            while (rs.next()) {
                list.add(new StudentInfo(
                    rs.getInt("studentId"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("program"),
                    rs.getDouble("gpa"),
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

    /** Enable or disable a student account. */
    public static boolean updateStatus(int studentId, String status) {
        try {
            Connection con = getCon();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE STUDENT SET status = ? WHERE studentId = ?");
            ps.setString(1, status);
            ps.setInt(2, studentId);
            ps.executeUpdate();
            con.close();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
