package com.mycompany.lab4.employer.Business;

import com.mycompany.lab4.employer.Helper.Application;
import com.mycompany.lab4.employer.Helper.Opportunity;
import com.mycompany.lab4.employer.Persistence.Application_CRUD;
import com.mycompany.lab4.employer.Persistence.Opportunity_CRUD;

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

    /**
     * Employer updates the status of an application.
     * After a successful update, publishes a KubeMQ message so the
     * student microservice can react asynchronously.
     * Message format: STATUS_UPDATE:$applicationId, $newStatus
     */
    public static boolean updateStatus(int applicationId, String status) {
        boolean updated = Application_CRUD.updateStatus(applicationId, status);
        if (updated) {
            try {
                // Get the application to include studentId in the message
                Application app = Application_CRUD.readById(applicationId);
                String studentId = (app != null) ? String.valueOf(app.getStudentId()) : "unknown";
                String message = "STATUS_UPDATE:" + applicationId + ":" + studentId + ":" + status;
                Message.sendmessage(message);
                System.out.println("KubeMQ message sent: " + message);
            } catch (Exception e) {
                // Log but don't fail the update if messaging fails
                System.out.println("Warning: Could not send KubeMQ message: " + e.getMessage());
            }
        }
        return updated;
    }
}