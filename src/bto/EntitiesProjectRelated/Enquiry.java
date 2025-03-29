package bto.EntitiesProjectRelated;

import java.util.Date;
import bto.Entities.*;


public class Enquiry {
    private Applicant applicant;
    private Project project;
    private String enquiryContent;
    private String response;
    private Date submissionDate;
    
    // Constructors
    public Enquiry() {
        this.submissionDate = new Date(); // Sets current date as submission date
        this.response = null;
    }
    
    public Enquiry(Applicant applicant, Project project, String enquiryContent) {
        this();
        this.applicant = applicant;
        this.project = project;
        this.enquiryContent = enquiryContent;
    }
    
    // Methods
    public String getContent() {
        return enquiryContent;
    }
    
    public String getResponse() {
        return response;
    }
    
    public boolean setResponse(String response) {
        this.response = response;
        return true;
    }
    
    public String getApplicantDetails() {
        return applicant.getDetails();
    }
    
    public String getProjectDetails() {
        return project.getDetails();
    }
    
    public boolean updateContent(String newContent) {
        this.enquiryContent = newContent;
        return true;
    }
    
    // Getters and Setters
    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getEnquiryContent() {
        return enquiryContent;
    }

    public void setEnquiryContent(String enquiryContent) {
        this.enquiryContent = enquiryContent;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
}
