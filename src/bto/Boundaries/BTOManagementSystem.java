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
    private static ReportController reportController;
    private static RegistrationController registrationController;
    private static WithdrawalController withdrawalController;
    private static BookingController bookingController;
    private static ReceiptGenerator receiptGenerator;
    
    public static void main(String[] args) {
    	
        // Add shutdown hook to save data
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveData();
            System.out.println("System data saved successfully.");
        }));
        
        initialize();
        ui.displayLoginMenu();
    }
    
    public static void initialize() {
        // Initialize only the core controllers needed for data management
        authController = new AuthController();
        projectController = new ProjectController();
        applicationController = new ApplicationController();
        enquiryController = new EnquiryController();
        registrationController = new RegistrationController();
        reportController = new ReportController();
        withdrawalController = new WithdrawalController();
        bookingController = new BookingController();
        receiptGenerator = new ReceiptGenerator();
        
        // Initialize file manager
        fileManager = new FileManager();
        
        // Load data
        loadData();
        
        // Initialize UI after loading data
        ui = new UserInterface(authController, projectController, applicationController, enquiryController, 
                registrationController, withdrawalController, bookingController, 
                receiptGenerator, reportController);
    }
    
    public static void loadData() {
        try {
            // Load users first
            List<Applicant> applicants = fileManager.loadApplicants();
            List<HDBOfficer> officers = fileManager.loadOfficers();
            List<HDBManager> managers = fileManager.loadManagers();

            // Create a combined list of all users for enquiry responder lookup
            List<User> allUsers = new ArrayList<>();
            allUsers.addAll(applicants);
            allUsers.addAll(officers);
            allUsers.addAll(managers);

            // Initialize controllers with users
            initializeAuthController(applicants, officers, managers);

            // Load projects (passing officers and managers)
            List<Project> projects = fileManager.loadProjects(officers, managers);
            initializeProjectController(projects);

            // Load applications and enquiries (passing all users for responder lookup)
            List<ProjectApplication> applications = fileManager.loadApplications(applicants, projects);
            List<Enquiry> enquiries = fileManager.loadEnquiries(applicants, projects, allUsers);

            // Load additional entities
            List<OfficerRegistration> registrations = fileManager.loadOfficerRegistrations(officers, projects);
            List<FlatBooking> bookings = fileManager.loadBookings(applicants, projects);
            List<Receipt> receipts = fileManager.loadReceipts(applicants, projects);

            // Print loading status
            printLoadingStatus(
                applicants, officers, managers, 
                projects, applications, enquiries, 
                registrations, bookings, receipts
            );

            // Post-loading setup
            postLoadSetup(
                applicants, officers, managers, 
                projects, applications, enquiries, 
                bookings, receipts
            );

            // Initialize controllers
            initializeApplicationController(applications);
            initializeEnquiryController(enquiries);
            initializeRegistrationController(registrations);
            initializeBookingController(bookings, receipts);
            
            // Initialize report controller (no need to load reports from file)
            initializeReportController();

            System.out.println("All data loaded and initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error during load data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printLoadingStatus(
        List<Applicant> applicants,
        List<HDBOfficer> officers,
        List<HDBManager> managers,
        List<Project> projects,
        List<ProjectApplication> applications,
        List<Enquiry> enquiries,
        List<OfficerRegistration> registrations,
        List<FlatBooking> bookings,
        List<Receipt> receipts
    ) {
        System.out.println("========== System Initialization ==========");
        System.out.println("Loaded " + applicants.size() + " applicants");
        System.out.println("Loaded " + officers.size() + " officers");
        System.out.println("Loaded " + managers.size() + " managers");
        System.out.println("Loaded " + projects.size() + " projects");
        System.out.println("Loaded " + applications.size() + " applications");
        System.out.println("Loaded " + enquiries.size() + " enquiries");
        System.out.println("Loaded " + registrations.size() + " officer registrations");
        System.out.println("Loaded " + bookings.size() + " flat bookings");
        System.out.println("Loaded " + receipts.size() + " receipts");
        System.out.println("===========================================");
    }

    private static void postLoadSetup(
        List<Applicant> applicants,
        List<HDBOfficer> officers,
        List<HDBManager> managers,
        List<Project> projects,
        List<ProjectApplication> applications,
        List<Enquiry> enquiries,
        List<FlatBooking> bookings,
        List<Receipt> receipts
    ) {
        // Link applications back to applicants
        for (Applicant applicant : applicants) {
            for (ProjectApplication app : applications) {
                if (app.getApplicant().getNric().equals(applicant.getNric())) {
                    applicant.setAppliedProject(app);
                    break;
                }
            }
        }

        // Link bookings back to applicants
        for (Applicant applicant : applicants) {
            for (FlatBooking booking : bookings) {
                if (booking.getApplicant().getNric().equals(applicant.getNric())) {
                    applicant.setBookedFlat(booking);
                    break;
                }
            }
        }

        // Link enquiries to projects and applicants
        for (Project project : projects) {
            for (Enquiry enquiry : enquiries) {
                if (enquiry.getProject() != null && 
                    enquiry.getProject().getProjectName().equals(project.getProjectName())) {
                    project.addEnquiry(enquiry);
                }
            }
        }

        // Reinitialize project flats
        for (Project project : projects) {
            project.initializeProjectFlats();
        }
    }

    public static void saveData() {
        try {
            // Get data from controllers
            List<Applicant> applicants = authController.getAllApplicants();
            List<HDBOfficer> officers = authController.getAllOfficers();
            List<HDBManager> managers = authController.getAllManagers();

            List<Project> projects = projectController.getAllProjects();
            List<ProjectApplication> applications = applicationController.getAllApplications();
            List<Enquiry> enquiries = enquiryController.getAllEnquiries();
            
            // Get additional entities
            List<OfficerRegistration> registrations = registrationController.getAllRegistrations();
            List<FlatBooking> bookings = bookingController.getAllBookings();
            List<Receipt> receipts = bookingController.getAllReceipts();

            // Print summary of data being saved
            System.out.println("========== Saving System Data ==========");
            System.out.println("Saving " + applicants.size() + " applicants");
            System.out.println("Saving " + officers.size() + " officers");
            System.out.println("Saving " + managers.size() + " managers");
            System.out.println("Saving " + projects.size() + " projects");
            System.out.println("Saving " + applications.size() + " applications");
            System.out.println("Saving " + enquiries.size() + " enquiries");
            System.out.println("Saving " + registrations.size() + " officer registrations");
            System.out.println("Saving " + bookings.size() + " flat bookings");
            System.out.println("Saving " + receipts.size() + " receipts");
            System.out.println("========================================");

            // Save existing entities
            fileManager.saveApplicants(applicants);
            fileManager.saveOfficers(officers);
            fileManager.saveManagers(managers);
            fileManager.saveProjects(projects);
            fileManager.saveApplications(applications);
            fileManager.saveEnquiries(enquiries);

            // Save additional entities
            fileManager.saveOfficerRegistrations(registrations);
            fileManager.saveBookings(bookings);
            fileManager.saveReceipts(receipts);

            System.out.println("Data saved successfully.");
        } catch (Exception e) {
            System.err.println("Error during save data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Update the initialization method for BookingController
    private static void initializeBookingController(List<FlatBooking> bookings, List<Receipt> receipts) {
        bookingController.setBookings(bookings);
        bookingController.setReceipts(receipts);
    }

    // Initialization methods for controllers
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
    
    private static void initializeRegistrationController(List<OfficerRegistration> registrations) {
        registrationController.setRegistrations(registrations);
    }

    private static void initializeReportController() {
        // No need to load reports from file as they are generated on-demand
    }
}