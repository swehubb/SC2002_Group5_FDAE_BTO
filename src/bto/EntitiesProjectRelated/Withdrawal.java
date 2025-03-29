package bto.EntitiesProjectRelated;

import bto.Entities.*;
import java.util.Date;

public class Withdrawal {
    private Applicant applicant;
    private ProjectApplication application;
    private String status;
    private Date requestDate;
    
    // Constructors
    public Withdrawal() {
        this.status = "PENDING";
        this.requestDate = new Date(); // Sets current date as request date
    }
    
    public Withdrawal(Applicant applicant, ProjectApplication application) {
        this();
        this.applicant = applicant;
        this.application = application;
    }
    
    // Methods
    public String getStatus() {
        return status;
    }
    
    public boolean updateStatus(String newStatus) {
        this.status = newStatus;
        return true;
    }
    
    public String getApplicantDetails() {
        return applicant.getDetails();
    }
    
    public String getApplicationDetails() {
        return "Application for " + application.getProject().getProjectName() + 
               ", Status: " + application.getStatus();
    }
    
    // Getters and Setters
    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public ProjectApplication getApplication() {
        return application;
    }

    public void setApplication(ProjectApplication application) {
        this.application = application;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}