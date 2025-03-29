package bto.Entities;

import bto.Enums.*;
import bto.EntitiesProjectRelated.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;

public class HDBManager extends User {
    private List<Project> managedProjects;
    
    // Constructors
    public HDBManager() {
        super();
        this.managedProjects = new ArrayList<>();
    }
    
    public HDBManager(String nric, String password, int age, MaritalStatus maritalStatus, String name) {
        super(nric, password, age, maritalStatus, name);
        this.managedProjects = new ArrayList<>();
    }
    
    // Methods
    public Project createProject(Project projectDetails) {
        // Check if already managing a project with overlapping application period
        Date newOpenDate = projectDetails.getApplicationOpenDate();
        Date newCloseDate = projectDetails.getApplicationCloseDate();
        
        for (Project existingProject : managedProjects) {
            Date existingOpenDate = existingProject.getApplicationOpenDate();
            Date existingCloseDate = existingProject.getApplicationCloseDate();
            
            // Check for overlap in application periods
            if ((newOpenDate.before(existingCloseDate) && newCloseDate.after(existingOpenDate)) ||
                (existingOpenDate.before(newCloseDate) && existingCloseDate.after(newOpenDate))) {
                return null; // Overlapping application period
            }
        }
        
        // Validate project details
        if (projectDetails == null || 
            projectDetails.getProjectName() == null || 
            projectDetails.getProjectName().isEmpty()) {
            return null;
        }
        
        // Set the manager in charge to this manager
        projectDetails.setManagerInCharge(this);
        
        // Add to managed projects
        managedProjects.add(projectDetails);
        
        return projectDetails;
    }
    
    public boolean editProject(Project project, String attribute, Object newValue) {
        // Check if project belongs to this manager
        if (project == null || !project.getManagerInCharge().equals(this)) {
            return false;
        }
        
        // Check if project is in application period
        Date currentDate = new Date();
        if (currentDate.after(project.getApplicationOpenDate()) && 
            currentDate.before(project.getApplicationCloseDate())) {
            return false; // Cannot edit during application period
        }
        
        // Update specific attribute based on input
        switch (attribute.toLowerCase()) {
            case "projectname":
                project.setProjectName((String) newValue);
                return true;
            case "neighborhood":
                project.setNeighborhood((String) newValue);
                return true;
            case "applicationopendate":
                project.setApplicationOpenDate((Date) newValue);
                return true;
            case "applicationclosedate":
                project.setApplicationCloseDate((Date) newValue);
                return true;
            case "visible":
                project.setVisible((Boolean) newValue);
                return true;
            case "flattypeunits":
                if (newValue instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<FlatType, Integer> flatTypeUnits = (Map<FlatType, Integer>) newValue;
                    project.setFlatTypeUnits(flatTypeUnits);
                    return true;
                }
                return false;
            case "availablehdbofficerunits":
                if (newValue instanceof Integer) {
                    project.setAvailableHDBOfficerSlots((Integer) newValue);
                    return true;
                }
                return false;
            default:
                return false; // Unknown attribute
        }
    }
    
    public boolean deleteProject(Project project) {
        // Check if project belongs to this manager
        if (project == null || !project.getManagerInCharge().equals(this)) {
            return false;
        }
        
        // Remove from managed projects
        boolean removed = managedProjects.remove(project);
        
        // Nullify any references to this project if needed
        if (removed) {
            // Clear officer registrations
            for (OfficerRegistration registration : project.getOfficerRegistrations()) {
                registration.setProject(null);
            }
            
            // Clear applications
            for (ProjectApplication application : project.getApplications()) {
                application.setProject(null);
                application.getApplicant().setAppliedProject(null);
            }
            
            // Clear enquiries
            for (Enquiry enquiry : project.getEnquiries()) {
                enquiry.setProject(null);
            }
        }
        
        return removed;
    }
    
    public boolean toggleProjectVisibility(Project project) {
        // Check if project belongs to this manager
        if (project == null || !project.getManagerInCharge().equals(this)) {
            return false;
        }
        
        // Toggle visibility
        project.setVisible(!project.isVisible());
        
        return true;
    }
    
    public boolean approveRegistration(OfficerRegistration registration) {
        // Check if registration is valid and belongs to a project managed by this manager
        if (registration == null || 
            registration.getProject() == null || 
            !registration.getProject().getManagerInCharge().equals(this)) {
            return false;
        }
        
        // Check if there are available officer slots
        if (registration.getProject().getAvailableHDBOfficerSlots() <= 0) {
            return false; // No available slots
        }
        
        // Update registration status
        registration.setRegistrationStatus("APPROVED");
        
        // Add the project to the officer's assigned projects
        registration.getHdbOfficer().addAssignedProject(registration.getProject());
        
        return true;
    }
    
    public boolean approveApplication(ProjectApplication application) {
        // Check if application is valid and belongs to a project managed by this manager
        if (application == null || 
            application.getProject() == null || 
            !application.getProject().getManagerInCharge().equals(this)) {
            return false;
        }
        
        // Get the applicant and project
        Applicant applicant = application.getApplicant();
        Project project = application.getProject();
        
        // Get eligible flat types for this applicant
        List<FlatType> eligibleTypes = project.getEligibleFlatTypes(applicant);
        
        // Check if there are available units for any eligible flat type
        boolean unitsAvailable = false;
        for (FlatType flatType : eligibleTypes) {
            if (project.getAvailableFlatCount(flatType) > 0) {
                unitsAvailable = true;
                break;
            }
        }
        
        if (unitsAvailable) {
            // Update application status
            application.setStatus(ApplicationStatus.SUCCESSFUL);
            return true;
        } else {
            // No available units for any eligible flat type
            application.setStatus(ApplicationStatus.UNSUCCESSFUL);
            return false;
        }
    }
    
    public boolean rejectApplication(ProjectApplication application) {
        // Check if application is valid and belongs to a project managed by this manager
        if (application == null || 
            application.getProject() == null || 
            !application.getProject().getManagerInCharge().equals(this)) {
            return false;
        }
        
        // Update application status
        application.setStatus(ApplicationStatus.UNSUCCESSFUL);
        
        return true;
    }
    
    public boolean approveWithdrawal(ProjectApplication application) {
        // Check if application is valid and belongs to a project managed by this manager
        if (application == null || 
            application.getProject() == null || 
            !application.getProject().getManagerInCharge().equals(this) || 
            !"PENDING".equals(application.getWithdrawalStatus())) {
            return false;
        }
        
        // Update application withdrawal status
        application.setWithdrawalStatus("APPROVED");
        
        // Remove the application from the applicant
        application.getApplicant().setAppliedProject(null);
        
        // If the application status is BOOKED, release the flat
        if (application.getStatus() == ApplicationStatus.BOOKED) {
            FlatType flatType = application.getSelectedFlatType();
            Project project = application.getProject();
            
            // In a real implementation, we would have a flatId stored in the application
            // and would call project.releaseFlat(flatId)
            
            // For now, we'll just increment the available units
            int currentUnits = project.getFlatTypeUnits().getOrDefault(flatType, 0);
            project.updateFlatAvailability(flatType, currentUnits + 1);
        }
        
        return true;
    }
    
    public boolean rejectWithdrawal(ProjectApplication application) {
        // Check if application is valid and belongs to a project managed by this manager
        if (application == null || 
            application.getProject() == null || 
            !application.getProject().getManagerInCharge().equals(this) || 
            !"PENDING".equals(application.getWithdrawalStatus())) {
            return false;
        }
        
        // Update application withdrawal status
        application.setWithdrawalStatus("REJECTED");
        
        return true;
    }
    
    // Filtering method for projects that belong to the current HDBManager
    public List<Project> ownProjects(List<Project> allProjects, HDBManager currentManager) {
        List<Project> ownedProjects = new ArrayList<>();

        // Loop through all projects and check if their managerInCharge matches the currentManager
        for (Project project : allProjects) {
            // Here, we're assuming that the project has a 'managerInCharge' field of type 'HDBManager'
            if (project.getManagerInCharge().equals(currentManager)) {
                ownedProjects.add(project);
            }
        }

        return ownedProjects;
    }
    
    public Report generateReport(FilterCriteria criteria) {
        // Create a new report
        Report report = new Report();
        
        // Set report title based on criteria
        StringBuilder title = new StringBuilder("Applicant Report");
        
        // Process filter criteria
        for (Map.Entry<String, Object> entry : criteria.getCriteria().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            switch (key) {
                case "maritalStatus":
                    title.append(" - ").append(value).append(" Applicants");
                    break;
                case "flatType":
                    title.append(" - ").append(value).append(" Flats");
                    break;
                case "project":
                    title.append(" - Project: ").append(((Project) value).getProjectName());
                    break;
                case "minAge":
                    title.append(" - Min Age: ").append(value);
                    break;
                case "maxAge":
                    title.append(" - Max Age: ").append(value);
                    break;
            }
        }
        
        report.setTitle(title.toString());
        
        // Generate report content based on criteria
        // This is a simplified implementation; in reality, we would query the applications
        // and generate detailed report content
        report.setContent("Report generated on " + new Date() + "\n\n" + 
                          "Filter Criteria: " + criteria.toString() + "\n\n" + 
                          "Report Details: ...");
        
        return report;
    }
    
    public List<Enquiry> viewAllEnquiries() {
        // This would typically involve querying a database or repository
        // For simplicity, we're returning an empty list
        return new ArrayList<>();
    }
    
    public boolean respondToEnquiry(Enquiry enquiry) {
        // Check if enquiry is valid
        if (enquiry == null || enquiry.isResponded()) {
            return false;
        }
        
        // If enquiry is related to a project, check if it belongs to this manager
        if (enquiry.getProject() != null && !enquiry.getProject().getManagerInCharge().equals(this)) {
            return false;
        }
        
        // Mark as responded (actual response text is set by the caller)
        enquiry.setResponded(true);
        enquiry.setRespondedBy(this);
        enquiry.setResponseDate(new Date());
        
        return true;
    }
    
    // Getters and Setters
    public List<Project> getManagedProjects() {
        return managedProjects;
    }
   
    public void addManagedProject(Project project) {
        if (project != null && !managedProjects.contains(project)) {
            managedProjects.add(project);
        }
    }
    
    public void removeManagedProject(Project project) {
        managedProjects.remove(project);
    }
    
    // Legacy getter/setter for backward compatibility
    public Project getManagedProject() {
        return managedProjects.isEmpty() ? null : managedProjects.get(0);
    }
   
    public void setManagedProject(Project project) {
        if (project == null) {
            managedProjects.clear();
        } else if (!managedProjects.contains(project)) {
            managedProjects.add(project);
        }
    }
}