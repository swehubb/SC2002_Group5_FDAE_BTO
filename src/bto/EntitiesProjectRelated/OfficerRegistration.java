package bto.EntitiesProjectRelated;

import bto.Entities.*;

public class OfficerRegistration {
    private HDBOfficer hdbOfficer;
    private Project project;
    private String registrationStatus;

    // Constructors
    public OfficerRegistration() {
        this.registrationStatus = "PENDING";
    }

    public OfficerRegistration(HDBOfficer hdbOfficer, Project project) {
        this();
        this.hdbOfficer = hdbOfficer;
        this.project = project;
    }

    // Methods
    public String getStatus() {
        return registrationStatus;
    }

    public boolean updateStatus(String status) {
        if (status.equals("APPROVED") || status.equals("REJECTED")) {
            this.registrationStatus = status;
            return true;
        }
        return false;
    }

    public String getOfficerDetails() {
        return hdbOfficer.getDetails();
    }

    public String getProjectDetails() {
        return project.getDetails();
    }

    // Getters and Setters
    public HDBOfficer getHdbOfficer() {
        return hdbOfficer;
    }

    public void setHdbOfficer(HDBOfficer hdbOfficer) {
        this.hdbOfficer = hdbOfficer;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }
    
    public void setRegistrationStatus(String registrationStatus) {
        // Only allow changing to APPROVED or REJECTED
        if (registrationStatus.equals("APPROVED") || registrationStatus.equals("REJECTED")) {
            this.registrationStatus = registrationStatus;
        }
        // If an invalid status is provided, the status remains unchanged
    }

}