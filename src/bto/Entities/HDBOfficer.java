package bto.Entities;

import java.util.ArrayList;
import java.util.List;
import bto.Enums.*;
import bto.EntitiesProjectRelated.*;
import bto.Controllers.ReceiptGenerator;

public class HDBOfficer extends User {
     private List<Project> assignedProjects;
     private List<OfficerRegistration> registrations;
     private Applicant applicantRole; // Composition - HDBOfficer has an Applicant role
     
     // Constructors
     public HDBOfficer() {
         super();
         assignedProjects = new ArrayList<>();
         registrations = new ArrayList<>();
         // Create the applicant role with the same user details
         applicantRole = new Applicant();
     }
     
     public HDBOfficer(String nric, String password, int age, MaritalStatus maritalStatus, String name) {
         super(nric, password, age, maritalStatus, name);
         assignedProjects = new ArrayList<>();
         registrations = new ArrayList<>();
         // Create the applicant role with the same user details
         applicantRole = new Applicant(nric, password, age, maritalStatus, name);
     }
     
     // Officer-specific Methods
     public boolean registerForProject(Project project) {
         // Check if the officer has applied for this project as an applicant
         if (applicantRole.getAppliedProject() != null && 
             applicantRole.getAppliedProject().getProject().equals(project)) {
             // Cannot register for a project that the officer has applied for
             return false;
         }
         
         // Implementation for registering for a project
         return false; // Placeholder
     }
     
     public String viewRegistrationStatus() {
         // Implementation to view registration status
         return ""; // Placeholder
     }
     
     public boolean updateFlatAvailability(FlatType flatType) {
         // Implementation to update flat availability
         return false; // Placeholder
     }
     
     public ProjectApplication retrieveApplication(String nric) {
         // Check if the officer is trying to access their own application
         if (nric.equals(this.getNric())) {
             // Not allowed to process their own application
             return null;
         }
         
         // Implementation to retrieve application by NRIC
         return null; // Placeholder
     }
     
     public boolean updateBookingStatus(ProjectApplication application, FlatType flatType) {
         // Check if the application belongs to the officer
         if (application.getApplicant().getNric().equals(this.getNric())) {
             // Not allowed to update their own application
             return false;
         }
         
         // Implementation to update booking status
         return false; // Placeholder
     }
     
     /**
      * Generates a receipt for a project application.
      * Officers cannot generate receipts for their own applications.
      * 
      * @param application The project application to generate a receipt for
      * @return The receipt as a formatted string, or null if the application is invalid or belongs to the officer
      */
     public String generateReceipt(ProjectApplication application) {
         // Check if the application belongs to the officer
         if (application == null || application.getApplicant().getNric().equals(this.getNric())) {
             // Not allowed to generate receipt for their own application
             return null;
         }
         
         // Use the ReceiptGenerator to generate the receipt
         ReceiptGenerator generator = new ReceiptGenerator();
         boolean success = generator.generateReceipt(application);
         
         if (success) {
             return "Receipt generated successfully for applicant: " + 
                    application.getApplicant().getName() + " (NRIC: " + 
                    application.getApplicant().getNric() + ") for " + 
                    application.getProject().getProjectName() + ".";
         } else {
             return null;
         }
     }
     
     /**
      * Generates a receipt for a flat booking.
      * Officers cannot generate receipts for their own bookings.
      * 
      * @param booking The flat booking to generate a receipt for
      * @return The receipt as a formatted string, or null if the booking is invalid or belongs to the officer
      */
     public String generateReceiptForBooking(FlatBooking booking) {
         // Check if the booking belongs to the officer
         if (booking == null || booking.getApplicant().getNric().equals(this.getNric())) {
             // Not allowed to generate receipt for their own booking
             return null;
         }
         
         // Use the ReceiptGenerator to generate the receipt
         ReceiptGenerator generator = new ReceiptGenerator();
         String receipt = generator.generateReceipt(booking);
         
         if (receipt != null && !receipt.isEmpty()) {
             // Add officer signature to the receipt
             receipt += "\n\nProcessed by Officer: " + this.getName() + " (" + this.getNric() + ")";
             
             // Record that this officer processed the booking receipt
             booking.setProcessedByOfficer(this);
             
             // Log the receipt generation
             System.out.println("Receipt generated by Officer " + this.getName() + " for booking ID: " + booking.getFlatId());
             
             return receipt;
         } else {
             return null;
         }
     }
     
     /**
      * Validates a booking and generates an official receipt.
      * This method should be called when an officer has verified all the booking details
      * and is ready to finalize the booking with an official receipt.
      * 
      * @param booking The flat booking to validate and generate a receipt for
      * @return The official receipt as a formatted string, or null if validation fails
      */
     public String validateAndGenerateOfficialReceipt(FlatBooking booking) {
         // Check if the booking belongs to the officer
         if (booking == null || booking.getApplicant().getNric().equals(this.getNric())) {
             // Not allowed to validate their own booking
             return null;
         }
         
         // Check if the officer is assigned to the project
         boolean isAssigned = false;
         for (Project project : assignedProjects) {
             if (project.equals(booking.getProject())) {
                 isAssigned = true;
                 break;
             }
         }
         
         if (!isAssigned) {
             // Officer not assigned to this project
             return null;
         }
         
         // Validate the booking details
         // For example, check if the flat is still available, if the applicant is eligible, etc.
         // This is just a simple validation, you can add more checks as needed
         if (booking.getFlatId() <= 0 || booking.getFlatType() == null || 
             booking.getProject() == null || booking.getApplicant() == null) {
             return null;
         }
         
         // Mark the booking as validated by this officer
         booking.setProcessedByOfficer(this);
         booking.setValidated(true);
         
         // Generate the official receipt
         ReceiptGenerator generator = new ReceiptGenerator();
         String receipt = generator.generateReceipt(booking);
         
         if (receipt != null && !receipt.isEmpty()) {
             // Add validation information to the receipt
             receipt += "\n\nOFFICIALLY VALIDATED";
             receipt += "\nValidated by Officer: " + this.getName() + " (" + this.getNric() + ")";
             receipt += "\nValidation Date: " + new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
             receipt += "\n\nThis is an official HDB receipt. Please retain for your records.";
             
             return receipt;
         } else {
             return null;
         }
     }
     
     public boolean respondToEnquiry(Enquiry enquiry) {
         // Check if the enquiry belongs to the officer
         if (enquiry.getApplicant().getNric().equals(this.getNric())) {
             // Not allowed to respond to their own enquiry
             return false;
         }
         
         // Implementation to respond to enquiry
         return false; // Placeholder
     }
     
     public String displayProfile() {
         // Implementation to display profile
         StringBuilder profile = new StringBuilder(getDetails());
         
         // Add officer-specific details
         profile.append("\nOFFICER ROLE");
         profile.append("\nAssigned Projects: ").append(assignedProjects.size());
         
         // Add applicant-specific details
         profile.append("\n\nAPPLICANT ROLE");
         if (applicantRole.getAppliedProject() != null) {
             profile.append("\nCurrent Application: ").append(applicantRole.getAppliedProject().getProject().getProjectName());
             profile.append("\nApplication Status: ").append(applicantRole.getAppliedProject().getStatus());
         } else {
             profile.append("\nNo current applications");
         }
         
         return profile.toString();
     }
     
     public List<Project> getAssignedProjectDetails() {
         return assignedProjects;
     }
     
     // Applicant role delegation methods
     public boolean applyForProject(Project project) {
         // Check if the officer is assigned to this project
         for (Project assignedProject : assignedProjects) {
             if (assignedProject.equals(project)) {
                 // Cannot apply for a project that the officer is assigned to
                 return false;
             }
         }
         
         // Delegate to the applicant role
         return applicantRole.applyForProject(project);
     }
     
     public boolean submitEnquiry(String enquiryContent) {
         // Delegate to the applicant role
         return applicantRole.submitEnquiry(enquiryContent);
     }
     
     public ApplicationStatus viewApplicationStatus() {
         // Delegate to the applicant role
         return applicantRole.viewApplicationStatus();
     }
     
     public boolean requestWithdrawal() {
         // Delegate to the applicant role
         return applicantRole.requestWithdrawal();
     }
     
     public String viewEnquiry() {
         // Delegate to the applicant role
         return applicantRole.viewEnquiry();
     }
     
     public boolean editEnquiry() {
         // Delegate to the applicant role
         return applicantRole.editEnquiry();
     }
     
     public boolean deleteEnquiry() {
         // Delegate to the applicant role
         return applicantRole.deleteEnquiry();
     }
     
     // Getters and Setters
     public List<Project> getAssignedProjects() {
         return assignedProjects;
     }
    
     public void setAssignedProjects(List<Project> assignedProjects) {
         this.assignedProjects = assignedProjects;
     }
    
     public List<OfficerRegistration> getRegistrations() {
         return registrations;
     }
    
     public void setRegistrations(List<OfficerRegistration> registrations) {
         this.registrations = registrations;
     }
     
     public Applicant getApplicantRole() {
         return applicantRole;
     }
     
     public void setApplicantRole(Applicant applicantRole) {
         this.applicantRole = applicantRole;
     }
     
     // Helper methods
     public void addAssignedProject(Project project) {
         if (!assignedProjects.contains(project)) {
             assignedProjects.add(project);
         }
     }
     
     public void addRegistration(OfficerRegistration registration) {
         if (!registrations.contains(registration)) {
             registrations.add(registration);
         }
     }
}
