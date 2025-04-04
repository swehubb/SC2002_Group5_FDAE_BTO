package bto.Controllers;

import bto.Entities.*;
import bto.EntitiesProjectRelated.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnquiryController {
    private Map<String, List<Enquiry>> enquiriesByApplicant;
    private List<Enquiry> allEnquiries;
    private int nextEnquiryId = 1;
    
    // Constructor
    public EnquiryController() {
        enquiriesByApplicant = new HashMap<>();
        allEnquiries = new ArrayList<>();
    }
    
    
    // Create a new enquiry
    public Enquiry createEnquiry(Applicant applicant, Project project, String content) {
        Enquiry enquiry = new Enquiry(applicant, project, content);
        enquiry.setEnquiryId(nextEnquiryId++);
        
        // Add to project's enquiries if project is not null
        if (project != null) {
            project.addEnquiry(enquiry);
        }
        
        // Add to our collections
        allEnquiries.add(enquiry);
        
        if (!enquiriesByApplicant.containsKey(applicant.getNric())) {
            enquiriesByApplicant.put(applicant.getNric(), new ArrayList<>());
        }
        enquiriesByApplicant.get(applicant.getNric()).add(enquiry);
        
        return enquiry;
    }
    
    // Get enquiries for a project
    public List<Enquiry> getEnquiriesByProject(Project project) {
        if (project == null) return new ArrayList<>();
        return project.getEnquiries();
    }
    
    // Get enquiries submitted by an applicant
    public List<Enquiry> getEnquiriesByApplicant(Applicant applicant) {
        if (applicant == null) return new ArrayList<>();
        return enquiriesByApplicant.getOrDefault(applicant.getNric(), new ArrayList<>());
    }
    
    // Edit an existing enquiry
    public boolean editEnquiry(Enquiry enquiry, String newContent) {
        if (enquiry == null || !allEnquiries.contains(enquiry)) {
            return false;
        }
        
        // Can't edit if already responded to
        if (enquiry.isResponded()) {
            return false;
        }
        
        // Update enquiry content
        enquiry.setEnquiryContent(newContent);
        
        return true;
    }
    
    // Delete an enquiry
    public boolean deleteEnquiry(Enquiry enquiry) {
        if (enquiry == null || !allEnquiries.contains(enquiry)) {
            return false;
        }
        
        // Remove from the project's enquiries if project is not null
        if (enquiry.getProject() != null) {
            enquiry.getProject().getEnquiries().remove(enquiry);
        }
        
        // Remove from our collections
        allEnquiries.remove(enquiry);
        
        Applicant applicant = enquiry.getApplicant();
        if (enquiriesByApplicant.containsKey(applicant.getNric())) {
            enquiriesByApplicant.get(applicant.getNric()).remove(enquiry);
            return true;
        }
        
        return false;
    }
    
    // Respond to an enquiry
    public boolean respondToEnquiry(Enquiry enquiry, String response, User respondedBy) {
        if (enquiry == null || !allEnquiries.contains(enquiry)) {
            return false;
        }
        
        // Set the response and respondent
        enquiry.setResponse(response);
        enquiry.setRespondedBy(respondedBy);
        
        return true;
    }
    

    // Get all enquiries
    public List<Enquiry> getAllEnquiries() {
        return allEnquiries;
    }
    
    // Get all pending (unanswered) enquiries
    public List<Enquiry> getPendingEnquiries() {
        List<Enquiry> pendingEnquiries = new ArrayList<>();
        
        for (Enquiry enquiry : allEnquiries) {
            if (!enquiry.isResponded()) {
                pendingEnquiries.add(enquiry);
            }
        }
        
        return pendingEnquiries;
    }
    
    // Find an enquiry by ID
    public Enquiry getEnquiryById(int enquiryId) {
        for (Enquiry enquiry : allEnquiries) {
            if (enquiry.getEnquiryId() == enquiryId) {
                return enquiry;
            }
        }
        return null;
    }
    
    // Setter for enquiries (for persistence loading)
    public void setEnquiries(List<Enquiry> enquiries) {
        this.allEnquiries.clear();
        this.enquiriesByApplicant.clear();
        
        for (Enquiry enquiry : enquiries) {
            // Ensure enquiry has an ID
            if (enquiry.getEnquiryId() >= nextEnquiryId) {
                nextEnquiryId = enquiry.getEnquiryId() + 1;
            }
            
            this.allEnquiries.add(enquiry);
            
            Applicant applicant = enquiry.getApplicant();
            if (!this.enquiriesByApplicant.containsKey(applicant.getNric())) {
                this.enquiriesByApplicant.put(applicant.getNric(), new ArrayList<>());
            }
            this.enquiriesByApplicant.get(applicant.getNric()).add(enquiry);
        }
    }
}