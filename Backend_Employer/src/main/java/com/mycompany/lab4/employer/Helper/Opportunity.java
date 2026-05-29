package com.mycompany.lab4.employer.Helper;

/**
 * Opportunity - Data transfer object for a job/internship posting.
 * Used by OpportunityBusiness and Opportunity_CRUD.
 */
public class Opportunity {

    private int    opportunityId;
    private int    employerId;
    private String companyName;
    private String jobTitle;
    private String jobType;
    private String location;
    private String requiredSkills;
    private String deadline;
    private String description;
    private String status;

    public Opportunity() {}

    public Opportunity(int opportunityId, int employerId, String companyName,
                       String jobTitle, String jobType, String location,
                       String requiredSkills, String deadline,
                       String description, String status) {
        this.opportunityId  = opportunityId;
        this.employerId     = employerId;
        this.companyName    = companyName;
        this.jobTitle       = jobTitle;
        this.jobType        = jobType;
        this.location       = location;
        this.requiredSkills = requiredSkills;
        this.deadline       = deadline;
        this.description    = description;
        this.status         = status;
    }

    public int    getOpportunityId()  { return opportunityId; }
    public int    getEmployerId()     { return employerId; }
    public String getCompanyName()    { return companyName; }
    public String getJobTitle()       { return jobTitle; }
    public String getJobType()        { return jobType; }
    public String getLocation()       { return location; }
    public String getRequiredSkills() { return requiredSkills; }
    public String getDeadline()       { return deadline; }
    public String getDescription()    { return description; }
    public String getStatus()         { return status; }

    public void setOpportunityId(int opportunityId)      { this.opportunityId  = opportunityId; }
    public void setEmployerId(int employerId)             { this.employerId     = employerId; }
    public void setCompanyName(String companyName)        { this.companyName    = companyName; }
    public void setJobTitle(String jobTitle)              { this.jobTitle       = jobTitle; }
    public void setJobType(String jobType)                { this.jobType        = jobType; }
    public void setLocation(String location)              { this.location       = location; }
    public void setRequiredSkills(String requiredSkills)  { this.requiredSkills = requiredSkills; }
    public void setDeadline(String deadline)              { this.deadline       = deadline; }
    public void setDescription(String description)        { this.description    = description; }
    public void setStatus(String status)                  { this.status         = status; }
}
