package bto.Entities;

import bto.EntitiesProjectRelated.*;
import bto.Enums.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Applicant extends User {
	 private ProjectApplication appliedProject;
	 private FlatBooking bookedFlat;
	 
	 // Constructors
	 public Applicant() {
	     super();
	 }
	 
	 public Applicant(String nric, String password, int age, MaritalStatus maritalStatus, String name) {
	     super(nric, password, age, maritalStatus, name);
	 }
	 
	 // Methods
	 
	 /**
	  * Allows an applicant to apply for a BTO project.
	  * Singles age 35 and above can only apply for 2-Room flats.
	  * Married couples age 21 and above can apply for any flat type.
	  * Applicants cannot apply for multiple projects simultaneously.
	  * 
	  * @param project The project the applicant wishes to apply for
	  * @return true if application was successful, false otherwise
	  */
	 public boolean applyForProject(Project project) {
	     // Check if applicant already has an active application
	     if (this.appliedProject != null) {
	         System.out.println("Error: You already have an active application. Only one application is allowed at a time.");
	         return false;
	     }
	     
	     // Check if the project is visible (toggled "on" by HDB managers)
	     if (!project.isVisible()) {
	         System.out.println("Error: This project is not currently open for applications.");
	         return false;
	     }
	     
	     // Check if application period is valid
	     Date currentDate = new Date();
	     if (currentDate.before(project.getApplicationOpenDate())) {
	         System.out.println("Error: Applications for this project are not yet open. Opening date: " + project.getApplicationOpenDate());
	         return false;
	     }
	     if (currentDate.after(project.getApplicationCloseDate())) {
	         System.out.println("Error: Applications for this project are already closed. Closing date: " + project.getApplicationCloseDate());
	         return false;
	     }
	     
	     // Check if the applicant is eligible for any flat type in this project
	     List<FlatType> eligibleTypes = project.getEligibleFlatTypes(this);
	     if (eligibleTypes.isEmpty()) {
	         System.out.println("Error: You are not eligible for any flat types in this project based on your age and marital status.");
	         return false;
	     }
	     
	     // Check if there are any available flats of eligible types
	     boolean hasAvailableFlats = false;
	     for (FlatType type : eligibleTypes) {
	         if (project.getAvailableFlatCount(type) > 0) {
	             hasAvailableFlats = true;
	             break;
	         }
	     }
	     
	     if (!hasAvailableFlats) {
	         System.out.println("Error: No available flats of eligible types remaining in this project.");
	         return false;
	     }
	     
	  // Create a new application without specifying a flat type yet
	     ProjectApplication application = new ProjectApplication();
	     application.setApplicant(this);
	     application.setProject(project);
	     application.setStatus(ApplicationStatus.PENDING);
	     
	     // Set this application as the applicant's current application
	     this.appliedProject = application;
	     
	     // Add the application to the project's list of applications
	     project.addApplication(application);
	     
	     System.out.println("Application submitted successfully for " + project.getProjectName() + ".");
	     System.out.println("Your application is now PENDING. You will be notified once it has been processed by the HDB Manager.");
	     System.out.println("If approved, you will be able to select a flat type with an HDB Officer.");
	     return true;
	 }

	 /**
	  * Gets a list of all projects that are visible and that the applicant is eligible for.
	  * 
	  * @param allProjects List of all projects in the system
	  * @return List of projects that are visible and for which the applicant is eligible
	  */
	 public List<Project> getEligibleVisibleProjects(List<Project> allProjects) {
	     List<Project> eligibleProjects = new ArrayList<>();
	     
	     for (Project project : allProjects) {
	         // Check if project is visible (toggled "on" by managers)
	         if (!project.isVisible()) {
	             continue;
	         }
	     }
	     
	     return eligibleProjects;
	 }

	 public boolean submitEnquiry(String enquiryContent) {
	     // Implementation for submitting an enquiry
	     return false; // Placeholder
	 }
	 
	 public ApplicationStatus viewApplicationStatus() {
	     if (appliedProject != null) {
	         return appliedProject.getStatus();
	     }
	     return null;
	 }
	 
	 public boolean requestWithdrawal() {
	     // Implementation for withdrawal request
	     return false; // Placeholder
	 }
	 
	 public String viewEnquiry() {
	     // Implementation to view enquiries
	     return ""; // Placeholder
	 }
	 
	 public boolean editEnquiry() {
	     // Implementation to edit enquiry
	     return false; // Placeholder
	 }
	 
	 public boolean deleteEnquiry() {
	     // Implementation to delete enquiry
	     return false; // Placeholder
	 }
	 
	 // Getters and Setters
	 public ProjectApplication getAppliedProject() {
	     return appliedProject;
	 }
	
	 public void setAppliedProject(ProjectApplication appliedProject) {
	     this.appliedProject = appliedProject;
	 }
	
	 public FlatBooking getBookedFlat() {
	     return bookedFlat;
	 }
	
	 public void setBookedFlat(FlatBooking bookedFlat) {
	     this.bookedFlat = bookedFlat;
	 }

   
    // In Applicant.java - without storing a Receipt reference
	public String generateReceipt(BookingController bookingController) {
	    if (bookedFlat != null) {
	        // Check if a receipt exists in the BookingController
	        if (bookingController.hasReceipt(this.getNric())) {
	            Receipt storedReceipt = bookingController.getReceiptForApplicant(this.getNric());
	            return storedReceipt.getContent();
	        } else {
	            return "No receipt has been generated for your booking yet.\nPlease contact an HDB Officer to generate a receipt.";
	        }
	    } else if (appliedProject != null) {
	        return "No booking found. Your application status is: " + appliedProject.getStatus() + 
	               ". Please contact an HDB Officer to process your booking if your application is successful.";
	    } else {
	        return "No active applications or bookings found.";
	    }
	}
