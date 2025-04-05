package bto.Entities;

import bto.EntitiesProjectRelated.*;
import bto.Enums.*;
import bto.Controllers.*;
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

	 
	 public ApplicationStatus viewApplicationStatus() {
	     if (appliedProject != null) {
	         return appliedProject.getStatus();
	     }
	     return null;
	 }
	 
	 public boolean requestWithdrawal() {
	     // Implementation for withdrawal request
		 if (appliedProject != null){
			return true;
		 }
	     return false; // Placeholder
	 }
	 
	 public boolean submitEnquiry(String enquiryContent, Project project) {
	if (enquiryContent == null || enquiryContent.trim().isEmpty()) {
	    System.out.println("Error: Enquiry content cannot be empty.");
	        return false;
	    }
	    
	    EnquiryController enquiryController = new EnquiryController();
	    Enquiry newEnquiry = enquiryController.createEnquiry(this, project, enquiryContent);
	    return (newEnquiry != null);
	}
	
	/**
	 * View all enquiries submitted by this applicant.
	 * 
	 * @return A string representation of all enquiries submitted by this applicant
	 */
	public String viewEnquiry() {
	    EnquiryController enquiryController = new EnquiryController();
	    List<Enquiry> myEnquiries = enquiryController.getEnquiriesByApplicant(this);
	    
	    if (myEnquiries.isEmpty()) {
	        return "You have not submitted any enquiries.";
	}
	
	StringBuilder result = new StringBuilder("Your Enquiries:\n");
	for (Enquiry enquiry : myEnquiries) {
	    String projectName = enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General";
	    String status = enquiry.isResponded() ? "Responded" : "Pending";
	    
	    result.append("Project: ").append(projectName)
	          .append(", Status: ").append(status)
	          .append(", Content: ").append(enquiry.getEnquiryContent())
	          .append("\n");
	    
	    if (enquiry.getResponse() != null) {
	        result.append("Response: ").append(enquiry.getResponse()).append("\n");
	    }
	    
	    result.append("-----------------------\n");
	    }
	    
	    return result.toString();
	}
	
	/**
	 * Edit an existing enquiry.
	 * 
	 * @param enquiry The enquiry to edit
	 * @param newContent The new content for the enquiry
	 * @return true if the enquiry was edited successfully, false otherwise
	 */
	public boolean editEnquiry(Enquiry enquiry, String newContent) {
	    if (enquiry == null || newContent == null || newContent.trim().isEmpty()) {
	        System.out.println("Error: Invalid enquiry or content.");
	    return false;
		}
		
		// Check if the enquiry belongs to this applicant
		if (!this.getNric().equals(enquiry.getApplicant().getNric())) {
		    System.out.println("Error: You can only edit your own enquiries.");
		    return false;
		}
		
		// Check if the enquiry has already been responded to
		if (enquiry.isResponded()) {
		    System.out.println("Error: You cannot edit an enquiry that has already been responded to.");
		        return false;
		    }
		    
		    EnquiryController enquiryController = new EnquiryController();
		    return enquiryController.editEnquiry(enquiry, newContent);
		}
	
	/**
	 * Delete an existing enquiry.
	 * 
	 * @param enquiry The enquiry to delete
	 * @return true if the enquiry was deleted successfully, false otherwise
	 */
	public boolean deleteEnquiry(Enquiry enquiry) {
	    if (enquiry == null) {
	        System.out.println("Error: Invalid enquiry.");
	    return false;
	    }
	
		// Check if the enquiry belongs to this applicant
		if (!this.getNric().equals(enquiry.getApplicant().getNric())) {
		    System.out.println("Error: You can only delete your own enquiries.");
		        return false;
		    }
		    
		    EnquiryController enquiryController = new EnquiryController();
		    return enquiryController.deleteEnquiry(enquiry);
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
	 

	
}