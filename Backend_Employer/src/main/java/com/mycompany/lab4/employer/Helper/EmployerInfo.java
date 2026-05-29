package com.mycompany.lab4.employer.Helper;

/**
 * EmployerInfo - Data transfer object for employer information.
 * Used by AuthBusiness and Employer_CRUD.
 */
public class EmployerInfo {

    private int    employerId;
    private String companyName;
    private String industry;
    private String location;
    private String username;
    private String password;
    private String status;

    public EmployerInfo() {}

    public EmployerInfo(int employerId, String companyName, String industry,
                        String location, String username, String password, String status) {
        this.employerId  = employerId;
        this.companyName = companyName;
        this.industry    = industry;
        this.location    = location;
        this.username    = username;
        this.password    = password;
        this.status      = status;
    }

    public int    getEmployerId()  { return employerId; }
    public String getCompanyName() { return companyName; }
    public String getIndustry()    { return industry; }
    public String getLocation()    { return location; }
    public String getUsername()    { return username; }
    public String getPassword()    { return password; }
    public String getStatus()      { return status; }

    public void setEmployerId(int employerId)     { this.employerId  = employerId; }
    public void setCompanyName(String companyName){ this.companyName = companyName; }
    public void setIndustry(String industry)      { this.industry    = industry; }
    public void setLocation(String location)      { this.location    = location; }
    public void setUsername(String username)      { this.username    = username; }
    public void setPassword(String password)      { this.password    = password; }
    public void setStatus(String status)          { this.status      = status; }
}
