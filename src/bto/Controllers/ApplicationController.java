package bto.Controllers;
import bto.Enums.*;
import bto.Entities.*;
import bto.EntitiesProjectRelated.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationController {
 private Map<String, ProjectApplication> applications; // Map of applicant NRIC to application
 
 // Constructor
 public ApplicationController() {
     applications = new HashMap<>();
 }
 
 // Methods
 public Boolean submitApplication(Applicant applicant, Project project) {

     // Check if the applicant already has an active application
     if (applicant.getAppliedProject() != null) {
         return false; // Already has an application
     }
     
     // Create new application
     ProjectApplication application = new ProjectApplication(applicant, project);
     
     // Set the application for the applicant
     applicant.setAppliedProject(application);
     
     // Add to the project's applications
     project.addApplication(application);
     
     // Store in the map
     applications.put(applicant.getNric(), application);
     
     return true;
 }
 
 public boolean processApplication(ProjectApplication application) {
     if (application == null || !applications.containsValue(application)) {
         return false;
     }
     
     // Check if there are available units of the requested flat type
     FlatType requestedType = application.getSelectedFlatType();
     Project project = application.getProject();
     
     int availableUnits = project.getFlatTypeUnits().getOrDefault(requestedType, 0);
     
     if (availableUnits > 0) {
         // Update application status
         application.setStatus(ApplicationStatus.SUCCESSFUL);
         
         // Reduce available units
         project.updateFlatAvailability(requestedType, availableUnits - 1);
         
         return true;
     } else {
         // No available units
         application.setStatus(ApplicationStatus.UNSUCCESSFUL);
         return false;
     }
 }
 
 public boolean updateApplicationStatus(ProjectApplication application, ApplicationStatus status) {
     if (application == null || !applications.containsValue(application)) {
         return false;
     }
     
     application.setStatus(status);
     return true;
 }
 
 public ProjectApplication getApplicationByApplicantNRIC(String nric) {
     return applications.get(nric);
 }
 
 public List<ProjectApplication> getApplicationsByProject(Project project) {
     List<ProjectApplication> projectApplications = new ArrayList<>();
     
     for (ProjectApplication application : applications.values()) {
         if (application.getProject().equals(project)) {
             projectApplications.add(application);
         }
     }
     
     return projectApplications;
 }
 
 public List<ProjectApplication> getAllApplications() {
     return new ArrayList<>(applications.values());
 }
 
 public boolean requestWithdrawal(ProjectApplication application) {
     if (application == null || !applications.containsValue(application)) {
         return false;
     }
     
     // Update application withdrawal status
     application.setWithdrawalStatus("PENDING");
     
     return true;
 }
 
 public boolean approveWithdrawal(ProjectApplication application) {
     if (application == null || !applications.containsValue(application) || 
         !"PENDING".equals(application.getWithdrawalStatus())) {
         return false;
     }
     
     // Update application withdrawal status
     application.setWithdrawalStatus("APPROVED");
     
     // Remove the application from the applicant
     application.getApplicant().setAppliedProject(null);
     
     // If the application was successful, increase the available units
     if (application.getStatus() == ApplicationStatus.SUCCESSFUL) {
         FlatType flatType = application.getSelectedFlatType();
         Project project = application.getProject();
         
         int currentUnits = project.getFlatTypeUnits().getOrDefault(flatType, 0);
         project.updateFlatAvailability(flatType, currentUnits + 1);
     }
     
     return true;
 }
 
 public boolean rejectWithdrawal(ProjectApplication application) {
     if (application == null || !applications.containsValue(application) || 
         !"PENDING".equals(application.getWithdrawalStatus())) {
         return false;
     }
     
     // Update application withdrawal status
     application.setWithdrawalStatus("REJECTED");
     
     return true;
 }
 
 // Getter and setter for applications map
 public Map<String, ProjectApplication> getApplications() {
     return applications;
 }
 
 public void setApplications(List<ProjectApplication> applicationList) {
     this.applications.clear();
     
     for (ProjectApplication application : applicationList) {
         this.applications.put(application.getApplicant().getNric(), application);
     }
 }
}