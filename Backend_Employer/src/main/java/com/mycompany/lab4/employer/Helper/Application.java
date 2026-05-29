package com.mycompany.lab4.employer.Helper;

/**
 * Application - Data transfer object for an internship application.
 * Used by ApplicationBusiness and Application_CRUD.
 */
public class Application {

    private int    applicationId;
    private int    studentId;
    private int    opportunityId;
    private String jobTitle;
    private String companyName;
    private String applicationDate;
    private String status;

    public Application() {}

    public Application(int studentId, int opportunityId,
                       String jobTitle, String companyName,
                       String applicationDate, String status) {
        this.studentId       = studentId;
        this.opportunityId   = opportunityId;
        this.jobTitle        = jobTitle;
        this.companyName     = companyName;
        this.applicationDate = applicationDate;
        this.status          = status;
    }

    public Application(int applicationId, int studentId, int opportunityId,
                       String jobTitle, String companyName,
                       String applicationDate, String status) {
        this.applicationId   = applicationId;
        this.studentId       = studentId;
        this.opportunityId   = opportunityId;
        this.jobTitle        = jobTitle;
        this.companyName     = companyName;
        this.applicationDate = applicationDate;
        this.status          = status;
    }

    public int    getApplicationId()   { return applicationId; }
    public int    getStudentId()       { return studentId; }
    public int    getOpportunityId()   { return opportunityId; }
    public String getJobTitle()        { return jobTitle; }
    public String getCompanyName()     { return companyName; }
    public String getApplicationDate() { return applicationDate; }
    public String getStatus()          { return status; }

    public void setApplicationId(int applicationId)       { this.applicationId   = applicationId; }
    public void setStudentId(int studentId)               { this.studentId       = studentId; }
    public void setOpportunityId(int opportunityId)       { this.opportunityId   = opportunityId; }
    public void setJobTitle(String jobTitle)              { this.jobTitle        = jobTitle; }
    public void setCompanyName(String companyName)        { this.companyName     = companyName; }
    public void setApplicationDate(String applicationDate){ this.applicationDate = applicationDate; }
    public void setStatus(String status)                  { this.status          = status; }
}
