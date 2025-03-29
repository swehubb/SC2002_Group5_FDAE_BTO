package bto.EntitiesProjectRelated;
import bto.Enums.*;
import bto.Entities.*;

public class ProjectApplication {
    private Applicant applicant;
    private Project project;
    private ApplicationStatus status;
    private String withdrawalStatus;
    private FlatType selectedFlatType;
    
    // Constructors
    public ProjectApplication() {
        status = ApplicationStatus.PENDING;
        withdrawalStatus = null;
    }
    
    public ProjectApplication(Applicant applicant, Project project) {
        this();
        this.applicant = applicant;
        this.project = project;
    }
    
    // Methods
    public boolean updateStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
        return true;
    }
    
    public String getProjectDetails() {
        return project.getDetails();
    }
    
    public String getApplicantDetails() {
        return applicant.getDetails();
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

    public String getWithdrawalStatus() {
        return withdrawalStatus;
    }

    public void setWithdrawalStatus(String withdrawalStatus) {
        this.withdrawalStatus = withdrawalStatus;
    }

    public FlatType getSelectedFlatType() {
        return selectedFlatType;
    }

    public void setSelectedFlatType(FlatType selectedFlatType) {
        this.selectedFlatType = selectedFlatType;
    }   
    
    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
}