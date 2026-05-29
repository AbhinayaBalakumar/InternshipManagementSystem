package com.mycompany.lab4.student.Business;

import com.mycompany.lab4.student.Helper.Opportunity;
import com.mycompany.lab4.student.Persistence.Opportunity_CRUD;

import java.util.List;

/**
 * OpportunityBusiness - Business logic for searching, posting, and viewing opportunities.
 */
public class OpportunityBusiness {

    /** Search open opportunities with optional filters. */
    public static List<Opportunity> search(String company, String title, String skills) {
        return Opportunity_CRUD.readByFilter(company, title, skills);
    }

    /** Get all postings by a specific employer (for employer dashboard). */
    public static List<Opportunity> getByEmployer(int employerId) {
        return Opportunity_CRUD.readByEmployer(employerId);
    }

    /** Post a new opportunity. employerId comes from the JWT. */
    public static int post(int employerId, String jobTitle, String jobType, String location,
                           String requiredSkills, String deadline, String description) {
        Opportunity opp = new Opportunity(
            0, employerId, "",
            jobTitle, jobType, location,
            requiredSkills, deadline, description, "OPEN"
        );
        return Opportunity_CRUD.create(opp);
    }

    /** Get a single opportunity by ID. */
    public static Opportunity getById(int opportunityId) {
        return Opportunity_CRUD.readById(opportunityId);
    }
}