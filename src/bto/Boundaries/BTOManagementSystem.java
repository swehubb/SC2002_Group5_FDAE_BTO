package bto.Boundaries;

import bto.Controllers.*;
import bto.Entities.*;
import bto.EntitiesProjectRelated.*;
import bto.Enums.*;
import java.util.List;
import java.util.ArrayList;

public class BTOManagementSystem {
    private static UserInterface ui;
    private static FileManager fileManager;
    private static AuthController authController;
    private static ProjectController projectController;
    private static ApplicationController applicationController;
    private static EnquiryController enquiryController;
    
    public static void main(String[] args) {
        initialize();
        ui.displayLoginMenu();
    }
    
    public static void initialize() {
        // Initialize only the core controllers needed for data management
        authController = new AuthController();
        projectController = new ProjectController();
        applicationController = new ApplicationController();
        enquiryController = new EnquiryController();
        
        // Initialize file manager
        fileManager = new FileManager();
        
        // Load data
        loadData();
        
        // Initialize UI after loading data
        ui = new UserInterface(authController, projectController, applicationController, enquiryController);
    }
    
    public static void loadData() {
        // Load users of different types
        List<Applicant> applicants = fileManager.loadApplicants();
        List<HDBOfficer> officers = fileManager.loadOfficers();
        List<HDBManager> managers = fileManager.loadManagers();
        
        // Initialize controllers with the loaded data
        initializeAuthController(applicants, officers, managers);
        
        // Load projects AFTER users are loaded
        List<Project> projects = fileManager.loadProjects(officers, managers); // Pass officers and managers directly
        
        // Initialize project controller
        initializeProjectController(projects);
        
        // Load applications and enquiries
        List<ProjectApplication> applications = fileManager.loadApplications();
        List<Enquiry> enquiries = fileManager.loadEnquiries();
        
        // Initialize other controllers
        initializeApplicationController(applications);
        initializeEnquiryController(enquiries);
        
        // Print status
        int totalUsers = applicants.size() + officers.size() + managers.size();
        System.out.println("========== System Initialization ==========");
        System.out.println("Loaded " + totalUsers + " users");
        System.out.println("Loaded " + projects.size() + " projects");
        System.out.println("Loaded " + applications.size() + " applications");
        System.out.println("Loaded " + enquiries.size() + " enquiries");
        System.out.println("==========================================");
    }
    
    public static void saveData() {
        // Get data from controllers
        List<Applicant> applicants = authController.getAllApplicants();
        List<HDBOfficer> officers = authController.getAllOfficers();
        List<HDBManager> managers = authController.getAllManagers();
        
        List<Project> projects = projectController.getAllProjects();
        List<ProjectApplication> applications = applicationController.getAllApplications();
        List<Enquiry> enquiries = enquiryController.getAllEnquiries();
        
        // Save data
        boolean applicantsSuccess = fileManager.saveApplicants(applicants);
        boolean officersSuccess = fileManager.saveOfficers(officers);
        boolean managersSuccess = fileManager.saveManagers(managers);
        boolean projectsSuccess = fileManager.saveProjects(projects);
        
        // Print status
        System.out.println("============= Save Status =============");
        System.out.println("Applicants: " + (applicantsSuccess ? "Success" : "Failed"));
        System.out.println("Officers: " + (officersSuccess ? "Success" : "Failed"));
        System.out.println("Managers: " + (managersSuccess ? "Success" : "Failed"));
        System.out.println("Projects: " + (projectsSuccess ? "Success" : "Failed"));
        System.out.println("======================================");
    }
    
    // Helper methods to initialize controllers with loaded data
    private static void initializeAuthController(List<Applicant> applicants, List<HDBOfficer> officers, List<HDBManager> managers) {
        // Add all users to the auth controller
        for (Applicant applicant : applicants) {
            authController.addUser(applicant);
        }
        
        for (HDBOfficer officer : officers) {
            authController.addUser(officer);
        }
        
        for (HDBManager manager : managers) {
            authController.addUser(manager);
        }
    }
    
    private static void initializeProjectController(List<Project> projects) {
        projectController.setProjects(projects);
    }
    
    private static void initializeApplicationController(List<ProjectApplication> applications) {
        applicationController.setApplications(applications);
    }
    
    private static void initializeEnquiryController(List<Enquiry> enquiries) {
        enquiryController.setEnquiries(enquiries);
    }
}