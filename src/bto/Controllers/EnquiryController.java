package bto.Controllers;

import bto.EntitiesProjectRelated.*;
import bto.Entities.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnquiryController {
 private Map<String, List<Enquiry>> enquiriesByApplicant; // Map of applicant NRIC to list of enquiries
 private List<Enquiry> allEnquiries; // List of all enquiries
 
 // Constructor
 public EnquiryController() {
     enquiriesByApplicant = new HashMap<>();
     allEnquiries = new ArrayList<>();
 }
 
 // Methods
 public Enquiry createEnquiry(Applicant applicant, Project project, String content) {
     // Create a new enquiry
     Enquiry enquiry = new Enquiry(applicant, project, content);
     
     // Add to the project's enquiries
     project.addEnquiry(enquiry);
     
     // Add to our collections
     allEnquiries.add(enquiry);
     
     if (!enquiriesByApplicant.containsKey(applicant.getNric())) {
         enquiriesByApplicant.put(applicant.getNric(), new ArrayList<>());
     }
     enquiriesByApplicant.get(applicant.getNric()).add(enquiry);
     
     return enquiry;
 }
 
 public List<Enquiry> getEnquiriesByProject(Project project) {
     return project.getEnquiries();
 }
 
 public List<Enquiry> getEnquiriesByApplicant(Applicant applicant) {
     return enquiriesByApplicant.getOrDefault(applicant.getNric(), new ArrayList<>());
 }
 
 public boolean editEnquiry(Enquiry enquiry, String newContent) {
     if (enquiry == null || !allEnquiries.contains(enquiry)) {
         return false;
     }
     
     // Update enquiry content
     enquiry.setEnquiryContent(newContent);
     
     return true;
 }
 
 public boolean deleteEnquiry(Enquiry enquiry) {
     if (enquiry == null || !allEnquiries.contains(enquiry)) {
         return false;
     }
     
     // Remove from the project's enquiries
     enquiry.getProject().getEnquiries().remove(enquiry);
     
     // Remove from our collections
     allEnquiries.remove(enquiry);
     
     Applicant applicant = enquiry.getApplicant();
     if (enquiriesByApplicant.containsKey(applicant.getNric())) {
         enquiriesByApplicant.get(applicant.getNric()).remove(enquiry);
         return true;
     }
     
     return false;
 }
 
 public boolean respondToEnquiry(Enquiry enquiry, String response) {
     if (enquiry == null || !allEnquiries.contains(enquiry)) {
         return false;
     }
     
     // Set the response
     enquiry.setResponse(response);
     
     return true;
 }
 
 public List<Enquiry> getAllEnquiries() {
     return allEnquiries;
 }
 
 // Method to get all enquiries with no response
 public List<Enquiry> getPendingEnquiries() {
     List<Enquiry> pendingEnquiries = new ArrayList<>();
     
     for (Enquiry enquiry : allEnquiries) {
         if (enquiry.getResponse() == null || enquiry.getResponse().isEmpty()) {
             pendingEnquiries.add(enquiry);
         }
     }
     
     return pendingEnquiries;
 }
 
 // Setter for enquiries
 public void setEnquiries(List<Enquiry> enquiries) {
     this.allEnquiries.clear();
     this.enquiriesByApplicant.clear();
     
     for (Enquiry enquiry : enquiries) {
         this.allEnquiries.add(enquiry);
         
         Applicant applicant = enquiry.getApplicant();
         if (!this.enquiriesByApplicant.containsKey(applicant.getNric())) {
             this.enquiriesByApplicant.put(applicant.getNric(), new ArrayList<>());
         }
         this.enquiriesByApplicant.get(applicant.getNric()).add(enquiry);
     }
 }
}