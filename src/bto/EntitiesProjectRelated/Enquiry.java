package bto.EntitiesProjectRelated;

import bto.Entities.Applicant;
import bto.Entities.User;
import java.util.Date;

public class Enquiry {
    private int enquiryId;
    private Applicant applicant;
    private Project project; // Can be null for general enquiries
    private String enquiryContent;
    private String response;
    private Date submissionDate;
    private User respondedBy;
    private Date responseDate;
    private boolean isResponded;
    
    // Constructor for new enquiries
    public Enquiry(Applicant applicant, Project project, String enquiryContent) {
        this.applicant = applicant;
        this.project = project;
        this.enquiryContent = enquiryContent;
        this.submissionDate = new Date(); // Set current date
        this.isResponded = false;
    }
    
    // Getters and Setters
    public int getEnquiryId() {
        return enquiryId;
    }
    
    public void setEnquiryId(int enquiryId) {
        this.enquiryId = enquiryId;
    }
    
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
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
        this.isResponded = (response != null && !response.isEmpty());
        if (this.isResponded) {
            this.responseDate = new Date();
        }
    }
    
    public Date getSubmissionDate() {
        return submissionDate;
    }
    
    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    public User getRespondedBy() {
        return respondedBy;
    }
    
    public void setRespondedBy(User respondedBy) {
        this.respondedBy = respondedBy;
    }
    
    public Date getResponseDate() {
        return responseDate;
    }
    
    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }
    
    public boolean isResponded() {
        return isResponded || (response != null && !response.isEmpty());
    }
    
    public void setResponded(boolean bool) {
    	this.isResponded = bool;
    }
    
    // For backward compatibility with code checking null response
    public String getMessage() {
        return enquiryContent;
    }
    
    public String getSubject() {
        return "Enquiry about " + (project != null ? project.getProjectName() : "General Topics");
    }
    
    public User getSender() {
        // For backward compatibility with ManagerInterface
        return applicant;
    }
}