package com.mycompany.lab4.employer.Business;

import com.mycompany.lab4.employer.Helper.EmployerInfo;
import com.mycompany.lab4.employer.Helper.StudentInfo;
import com.mycompany.lab4.employer.Persistence.Employer_CRUD;
import com.mycompany.lab4.employer.Persistence.Student_CRUD;

/**
 * AuthBusiness - Business logic for authenticating students and employers.
 * Called by the AuthEndpoint REST servlet.
 * Returns the user info object if credentials are valid, null otherwise.
 */
public class AuthBusiness {

    /** Authenticate a student. Returns StudentInfo if valid, null if not. */
    public static StudentInfo authenticateStudent(String username, String password) {
        if (username == null || password == null) return null;
        return Student_CRUD.read(username, password);
    }

    /** Authenticate an employer. Returns EmployerInfo if valid, null if not. */
    public static EmployerInfo authenticateEmployer(String username, String password) {
        if (username == null || password == null) return null;
        return Employer_CRUD.read(username, password);
    }

    /**
     * Authenticate the placement coordinator.
     * Hardcoded credentials (same as Lab 3 ManageSystem).
     */
    public static boolean authenticateCoordinator(String username, String password) {
        return "coordinator".equals(username) && "coord123".equals(password);
    }
}
