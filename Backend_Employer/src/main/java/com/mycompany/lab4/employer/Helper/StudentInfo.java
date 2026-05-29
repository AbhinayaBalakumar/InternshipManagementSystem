package com.mycompany.lab4.employer.Helper;

/**
 * StudentInfo - Data transfer object for student information.
 * Used by AuthBusiness and Student_CRUD.
 */
public class StudentInfo {

    private int    studentId;
    private String firstName;
    private String lastName;
    private String email;
    private String program;
    private double gpa;
    private String username;
    private String password;
    private String status;

    public StudentInfo() {}

    public StudentInfo(int studentId, String firstName, String lastName,
                       String email, String program, double gpa,
                       String username, String password, String status) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.program   = program;
        this.gpa       = gpa;
        this.username  = username;
        this.password  = password;
        this.status    = status;
    }

    public int    getStudentId()  { return studentId; }
    public String getFirstName()  { return firstName; }
    public String getLastName()   { return lastName; }
    public String getFullName()   { return firstName + " " + lastName; }
    public String getEmail()      { return email; }
    public String getProgram()    { return program; }
    public double getGpa()        { return gpa; }
    public String getUsername()   { return username; }
    public String getPassword()   { return password; }
    public String getStatus()     { return status; }

    public void setStudentId(int studentId)    { this.studentId = studentId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName  = lastName; }
    public void setEmail(String email)         { this.email     = email; }
    public void setProgram(String program)     { this.program   = program; }
    public void setGpa(double gpa)             { this.gpa       = gpa; }
    public void setUsername(String username)   { this.username  = username; }
    public void setPassword(String password)   { this.password  = password; }
    public void setStatus(String status)       { this.status    = status; }
}
