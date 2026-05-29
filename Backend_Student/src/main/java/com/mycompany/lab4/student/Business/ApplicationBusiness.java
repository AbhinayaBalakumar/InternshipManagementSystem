package com.mycompany.lab4.student.Business;

import com.mycompany.lab4.student.Helper.Application;
import com.mycompany.lab4.student.Helper.Opportunity;
import com.mycompany.lab4.student.Persistence.Application_CRUD;
import com.mycompany.lab4.student.Persistence.Opportunity_CRUD;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ApplicationBusiness - Business logic for applying and viewing application status.
 * Called by the ApplicationEndpoint REST servlet.
 * studentId is always extracted from the JWT by the endpoint - never from the request body.
 */
public class ApplicationBusiness {

    /**
     * Apply for an internship.
     * studentId comes from JWT (not request body) - this is the key Lab 4 requirement.
     * Returns the created Application, or null on failure.
     */
    public static Application apply(int studentId, int opportunityId) {

        // Step 1: Check opportunity is still OPEN
        Opportunity opp = Opportunity_CRUD.readById(opportunityId);
        if (opp == null || !"OPEN".equals(opp.getStatus())) {
            return null;
        }

        // Step 2: Check for duplicate application
        if (Application_CRUD.alreadyApplied(studentId, opportunityId)) {
            return null;
        }

        // Step 3: Create and save application
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Application app = new Application(
            studentId, opportunityId,
            opp.getJobTitle(), opp.getCompanyName(),
            today, "Submitted"
        );

        int newId = Application_CRUD.create(app);
        if (newId > 0) {
            app.setApplicationId(newId);
            return app;
        }
        return null;
    }

    /**
     * Get all applications for a student.
     * studentId comes from JWT.
     */
    public static List<Application> getMyApplications(int studentId) {
        return Application_CRUD.readByStudent(studentId);
    }

    /**
     * Get all applicants for an opportunity.
     * employerId from JWT is checked against the opportunity's employerId.
     */
    public static List<Application> getApplicantsForOpportunity(int opportunityId, int employerId) {
        Opportunity opp = Opportunity_CRUD.readById(opportunityId);
        if (opp == null || opp.getEmployerId() != employerId) {
            return null; // Not this employer's opportunity
        }
        return Application_CRUD.readByOpportunity(opportunityId);
    }

    /** Employer updates the status of an application. */
    public static boolean updateStatus(int applicationId, String status) {
        return Application_CRUD.updateStatus(applicationId, status);
    }
}