package bto.Boundaries;

import java.util.Scanner;
import bto.Controllers.*;
import bto.Entities.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import bto.EntitiesProjectRelated.*;
import bto.Enums.*;
import java.util.ArrayList;

public class OfficerInterface {
    private Scanner scanner;
    
    // Controllers (same instance shared across all interfaces)
    private AuthController authController;
    private ProjectController projectController;
    private ApplicationController applicationController;
    private EnquiryController enquiryController;
    private ReportController reportController;
    private RegistrationController registrationController;
    private WithdrawalController withdrawalController;
    private BookingController bookingController;
    private ReceiptGenerator receiptGenerator;
    
    private UserInterface userInterface;
    
    // Current logged-in applicant
    private HDBOfficer currentOfficer;

    // Constructor takes in controllers
    public OfficerInterface(Scanner scanner, AuthController authController, ProjectController projectController, 
            ApplicationController applicationController, EnquiryController enquiryController, 
            ReportController reportController, RegistrationController registrationController, 
            WithdrawalController withdrawalController, BookingController bookingController, 
            ReceiptGenerator receiptGenerator, UserInterface userInterface) {
		this.scanner = scanner;
		this.authController = authController;
		this.projectController = projectController;
		this.applicationController = applicationController;
		this.enquiryController = enquiryController;
		this.reportController = reportController;
		this.registrationController = registrationController;
		this.withdrawalController = withdrawalController;
		this.bookingController = bookingController;
		this.receiptGenerator = receiptGenerator;
		this.userInterface = userInterface;
    }
    
    // Method to set the current logged-in applicant
    public void setCurrentOfficer(HDBOfficer Officer) {
        this.currentOfficer = Officer;
    }
    
    // Method to get the current logged-in applicant
    public HDBOfficer getCurrentOfficer() {
        return this.currentOfficer;
    }
    
    // Helper method for getting integer input with validation
    private int getValidIntegerInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please enter a valid number.");
                continue;
            }
            
            try {
                int value = Integer.parseInt(input);
                
                if (value < min || value > max) {
                    System.out.println("Please enter a number between " + min + " and " + max);
                    continue;
                }
                
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    // Helper method for getting integer input with only minimum bound
    private int getValidIntegerInput(String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please enter a valid number.");
                continue;
            }
            
            try {
                int value = Integer.parseInt(input);
                
                if (value < min) {
                    System.out.println("Please enter a number of at least " + min);
                    continue;
                }
                
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    public void displayOfficerMenu(HDBOfficer officer) {
    	
    	setCurrentOfficer(officer);
    	
        System.out.println("\n======== HDB OFFICER PORTAL ========");
        System.out.println("Welcome, " + officer.getName() + " (" + officer.getNric() + ")");
        System.out.println("========================================");
        
        System.out.println("\n--- OFFICER FUNCTIONS ---");
        System.out.println("1. View All Projects");
        System.out.println("2. Register for Project Assignment");
        System.out.println("3. View My Project Assignments");
        System.out.println("4. Manage Flat Bookings");
        System.out.println("5. Generate Receipt for Booking");
        System.out.println("6. View Pending Enquiries");
        System.out.println("7. Respond to Enquiries");
        
        System.out.println("\n--- APPLICANT FUNCTIONS ---");
        System.out.println("8. Browse Available Projects");
        System.out.println("9. View My Application Status");
        System.out.println("10. Submit Enquiry");
        System.out.println("11. View My Enquiries");
        System.out.println("12. Edit My Enquiry");
        System.out.println("13. Delete My Enquiry");
        System.out.println("14. Request Withdrawal");
        
        System.out.println("\n--- ACCOUNT MANAGEMENT ---");
        System.out.println("15. Display Profile");
        System.out.println("16. Change Password");
        System.out.println("0. Logout");
        
        int choice = getValidIntegerInput("Enter your choice: ", 0, 16);
        
        // Implementation for each menu option would go here
        switch (choice) {
            case 1:
                viewAllProjects(currentOfficer);
                break;
            case 2:
                registerForProjectAssignment(currentOfficer);
                break;
            case 3:
                viewProjectAssignments(currentOfficer);
                break;
            case 4:
                manageApplicationsAndBookings(currentOfficer);
                break;
            case 5:
            	generateReceiptForBooking();
                break;
            case 6:
				viewPendingEnquiries(currentOfficer);
				break;
			case 7:
				respondToEnquiries(currentOfficer);
				break;
            case 8:
                browseProjects(currentOfficer);
                break;
            case 9:
                viewOfficerApplicationStatus(currentOfficer);
                break;
			case 10:
				submitEnquiry(currentOfficer);
				break;
			case 11:
				viewMyEnquiries(currentOfficer);
				break;
			case 12:
				// This will now be handled within viewMyEnquiries
				viewMyEnquiries(currentOfficer);
				break;
			case 13:
				// This will now be handled within viewMyEnquiries
				viewMyEnquiries(currentOfficer);
				break;
	        case 14:
                // Request Withdrawal - implementation needed
                System.out.println("Function not implemented yet.");
                displayOfficerMenu(officer);
                break;
            case 15:
            	displayOfficerProfile(currentOfficer);
                break;
            case 16:
                // Change Password - implementation needed
                changePasswordInterface();
                break;
            case 0:
                userInterface.displayLoginMenu();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                displayOfficerMenu(officer);
                break;
        }
    }
    
    private void viewAllProjects(HDBOfficer officer) {
        System.out.println("\n======== ALL PROJECTS ========");
        
        // Get all projects from the project controller
        List<Project> allProjects = projectController.getAllProjects();
        
        if (allProjects.isEmpty()) {
            System.out.println("No projects available in the system.");
        } else {
            System.out.println("ID\tProject Name\t\tNeighborhood\t\tApplication Period\t\tStatus");
            System.out.println("----------------------------------------------------------------------------------------------------");
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            for (int i = 0; i < allProjects.size(); i++) {
                Project project = allProjects.get(i);
                
                // Format dates for display
                String openDate = dateFormat.format(project.getApplicationOpenDate());
                String closeDate = dateFormat.format(project.getApplicationCloseDate());
                
                // Determine if officer is assigned to this project
                String status = officer.getAssignedProjects().contains(project) ? "Assigned" : "Unassigned";
                
                // Check if officer has applied for this project as an applicant
                ProjectApplication application = applicationController.getApplicationByApplicantNRIC(officer.getNric());
                if (application != null && application.getProject().equals(project)) {
                    status += " (Applied as Applicant)";
                }
                
                System.out.printf("%-7d %-25s %-20s %-10s - %-10s %18s%n", 
                	    i + 1, 
                	    project.getProjectName(), 
                	    project.getNeighborhood(), 
                	    openDate, 
                	    closeDate,
                	    status
                	);
            }
        }
        
        System.out.println("\nOptions:");
        System.out.println("1. View Project Details");
        System.out.println("0. Back to Main Menu");
        
        int choice = getValidIntegerInput("\nEnter your choice: ", 0, 1);
        
        switch (choice) {
            case 1:
                int projectId = getValidIntegerInput("Enter project ID to view details: ", 1, allProjects.size());
                viewProjectDetails(allProjects.get(projectId - 1), officer);
                
                // After viewing details, return to project list
                viewAllProjects(officer);
                break;
                
            case 0:
                displayOfficerMenu(officer);
                break;
                
            default:
                System.out.println("Invalid choice.");
                viewAllProjects(officer);
                break;
        }
    }
    

	private void viewProjectDetails(Project project, HDBOfficer officer) {
	    System.out.println("\n======== PROJECT DETAILS ========");
	    System.out.println("Project Name: " + project.getProjectName());
	    System.out.println("Neighborhood: " + project.getNeighborhood());
	    
	    // Format dates for display
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    System.out.println("Application Period: " + 
	        dateFormat.format(project.getApplicationOpenDate()) + " - " + 
	        dateFormat.format(project.getApplicationCloseDate()));
	    
	    // Manager in Charge
	    System.out.println("Manager in Charge: " + project.getManagerInCharge().getName());
	    
	    // Display Flat Types and Availability
	    System.out.println("\nFlat Types and Availability:");
	    Map<FlatType, Integer> flatTypeUnits = project.getFlatTypeUnits();
	    ProjectFlats projectFlats = project.getProjectFlats();
	    
	    for (FlatType flatType : flatTypeUnits.keySet()) {
	        int totalUnits = flatTypeUnits.get(flatType);
	        int availableUnits = projectFlats.getAvailableFlatCount(flatType);
	        
	        System.out.printf("%s Flats: Total %d, Available %d%n", 
	            flatType.name(), 
	            totalUnits, 
	            availableUnits
	        );
	    }
	    
	    // Display application statistics
	    List<ProjectApplication> applications = applicationController.getApplicationsByProject(project);
	    System.out.println("\nApplication Statistics:");
	    System.out.println("Total Applications: " + applications.size());
	    
	    int pendingCount = 0;
	    int successfulCount = 0;
	    int unsuccessfulCount = 0;
	    int bookedCount = 0;
	    
	    for (ProjectApplication app : applications) {
	        switch (app.getStatus()) {
	            case PENDING:
	                pendingCount++;
	                break;
	            case SUCCESSFUL:
	                successfulCount++;
	                break;
	            case UNSUCCESSFUL:
	                unsuccessfulCount++;
	                break;
	            case BOOKED:
	            	bookedCount++;
	        }
	    }
	    
	    System.out.println("Pending: " + pendingCount);
	    System.out.println("Successful: " + successfulCount);
	    System.out.println("Unsuccessful: " + unsuccessfulCount);
	    System.out.println("Booked: " + bookedCount);
	    
	    // Display officer's status with this project
	    boolean isAssigned = officer.getAssignedProjects().contains(project);
	    ProjectApplication officerApplication = applicationController.getApplicationByApplicantNRIC(officer.getNric());
	    boolean hasApplied = (officerApplication != null && officerApplication.getProject().equals(project));
	    
	    System.out.println("\nYour Status:");
	    System.out.println("Assigned as Officer: " + (isAssigned ? "Yes" : "No"));
	    System.out.println("Applied as Applicant: " + (hasApplied ? "Yes" : "No"));
	    
	    // Wait for user input before returning
	    System.out.println("\nPress Enter to continue...");
	    scanner.nextLine();
	}
	
	private boolean datesOverlap(Project project1, Project project2) {
        // Check if the date ranges overlap
        // If one project's start date is after another's end date, they don't overlap
        return !(project1.getApplicationCloseDate().before(project2.getApplicationOpenDate()) ||
                 project2.getApplicationCloseDate().before(project1.getApplicationOpenDate()));
    }
	
	private void registerForProjectAssignment(HDBOfficer officer) {
	    System.out.println("\n======== REGISTER FOR PROJECT ASSIGNMENT ========");
	    
	    // Get available projects (not assigned to this officer)
	    List<Project> availableProjects = new ArrayList<>();
	    List<Project> allProjects = projectController.getAllProjects();
	    
	    // Get officer's application to check conflicts
	    ProjectApplication officerApplication = applicationController.getApplicationByApplicantNRIC(officer.getNric());
	    
	    for (Project project : allProjects) {
	        // Skip if already assigned
	        if (officer.getAssignedProjects().contains(project)) {
	            continue;
	        }
	        
	        // Skip if officer has applied for this project as an applicant
	        if (officerApplication != null && officerApplication.getProject().equals(project)) {
	            continue;
	        }
	        
	        // Check if there are slots available
	        if (project.getAvailableHDBOfficerSlots() <= 0) {
	            continue; // Skip projects with no available slots
	        }
	        
	        // Check for date overlaps with currently assigned projects
	        boolean hasDateOverlap = false;
	        for (Project assignedProject : officer.getAssignedProjects()) {
	            if (datesOverlap(assignedProject, project)) {
	                hasDateOverlap = true;
	                break;
	            }
	        }
	        
	        if (hasDateOverlap) {
	            continue; // Skip projects with date overlaps
	        }
	        
	        availableProjects.add(project);
	    }
	    
	    if (availableProjects.isEmpty()) {
	        System.out.println("No available projects to register for.");
	        System.out.println("(Projects you're already assigned to, have applied for as an applicant, have no available slots, or have date overlaps with your current assignments are excluded)");
	    } else {
	    	System.out.println("Available Projects:");
	    	System.out.println("ID      Project Name             Neighborhood           Application Period            Available Slots");
            System.out.println("----------------------------------------------------------------------------------------------------");

	    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	    	for (int i = 0; i < availableProjects.size(); i++) {
	    	    Project project = availableProjects.get(i);
	    	    String openDate = dateFormat.format(project.getApplicationOpenDate());
	    	    String closeDate = dateFormat.format(project.getApplicationCloseDate());
	    	    
	    	    System.out.printf("%-7d %-25s %-20s %-12s - %-12s %10d%n", 
	    	        i + 1, 
	    	        project.getProjectName(), 
	    	        project.getNeighborhood(),
	    	        openDate,
	    	        closeDate,
	    	        project.getAvailableHDBOfficerSlots()
	    	    );
	    	}
	        
	        int projectId = getValidIntegerInput("\nEnter project ID to register (or 0 to cancel): ", 0, availableProjects.size());
	        
	        if (projectId > 0) {
	            Project selectedProject = availableProjects.get(projectId - 1);
	            
	            // Register officer for the project using RegistrationController
	            OfficerRegistration registration = registrationController.registerOfficer(officer, selectedProject);
	            
	            if (registration != null) {
	                System.out.println("\nSubmitting registration request for " + selectedProject.getProjectName() + "...");
	                System.out.println("Registration request submitted successfully!");
	                System.out.println("Status: " + registration.getRegistrationStatus() + " (Awaiting manager approval)");
	            } else {
	                System.out.println("Failed to register for project. No slots available.");
	            }
	        }
	    }
	    BTOManagementSystem.saveData();
	    
	    // Wait for user input before returning to menu
	    System.out.println("\nPress Enter to return to the main menu...");
	    scanner.nextLine();
	    
	    // Return to the officer menu
	    displayOfficerMenu(officer);
	}
	
	private void viewProjectAssignments(HDBOfficer officer) {
	    System.out.println("\n======== MY PROJECT ASSIGNMENTS ========");
	    
	    // Direct check against all projects
	    List<Project> allProjects = projectController.getAllProjects();
	    List<Project> directAssignments = new ArrayList<>();
	    
	    for (Project project : allProjects) {
	        // Check officer registrations directly
	        for (OfficerRegistration reg : project.getOfficerRegistrations()) {
	            if (reg.getHdbOfficer() != null && 
	                reg.getHdbOfficer().getNric().equals(officer.getNric()) &&
	                reg.getRegistrationStatus().equals("APPROVED")) {
	                directAssignments.add(project);
	                break;
	            }
	        }
	    }
	    
	    if (directAssignments.isEmpty()) {
	        System.out.println("You have no project registration requests or assignments.");
	    } else {
	        System.out.println("\n=== ASSIGNED PROJECTS ===");
	        for (Project project : directAssignments) {
	            System.out.printf("Project: %-20s\tNeighborhood: %-15s\tStatus: %s%n", 
	                project.getProjectName(), 
	                project.getNeighborhood(),
	                "APPROVED"
	            );
	        }
	    }
	    
	    // Get all registrations for the officer
	    List<OfficerRegistration> registrations = registrationController.viewRegistrationStatus(officer);
	    
	    if (registrations.isEmpty()) {
	        System.out.println("You have no project registration requests or assignments.");
	    } else {
	        // Display pending registrations
	        System.out.println("\n=== PENDING REGISTRATIONS ===");
	        boolean hasPending = false;
	        
	        for (OfficerRegistration reg : registrations) {
	            if (reg.getRegistrationStatus().equals("PENDING")) {
	                Project project = reg.getProject();
	                System.out.printf("Project: %-20s\tNeighborhood: %-15s\tStatus: %s%n", 
	                    project.getProjectName(), 
	                    project.getNeighborhood(),
	                    reg.getRegistrationStatus()
	                );
	                hasPending = true;
	            }
	        }
	        
	        if (!hasPending) {
	            System.out.println("No pending registration requests.");
	        }
	        
	        
	        // Display rejected registrations
	        System.out.println("\n=== REJECTED REGISTRATIONS ===");
	        boolean hasRejected = false;
	        
	        for (OfficerRegistration reg : registrations) {
	            if (reg.getRegistrationStatus().equals("REJECTED")) {
	                Project project = reg.getProject();
	                System.out.printf("Project: %-20s\tNeighborhood: %-15s\tStatus: %s%n", 
	                    project.getProjectName(), 
	                    project.getNeighborhood(),
	                    reg.getRegistrationStatus()
	                );
	                hasRejected = true;
	            }
	        }
	        
	        if (!hasRejected) {
	            System.out.println("No rejected registration requests.");
	        }
	    }
	    
	    // Display options
	    System.out.println("\nOptions:");
	    System.out.println("1. View Assigned Project Details");
	    System.out.println("0. Back to Main Menu");
	    
	    int choice = getValidIntegerInput("\nEnter your choice: ", 0, 1);
	    
	    switch (choice) {
	        case 1:
	            List<Project> assignedProjects = officer.getAssignedProjects();
	            
	            if (assignedProjects.isEmpty()) {
	                System.out.println("\nYou are not currently assigned to any projects.");
	            } else {
	                System.out.println("\nSelect a project to view details:");
	                
	                for (int i = 0; i < assignedProjects.size(); i++) {
	                    System.out.printf("%d. %s%n", i + 1, assignedProjects.get(i).getProjectName());
	                }
	                
	                int projectChoice = getValidIntegerInput("\nEnter project number (or 0 to cancel): ", 0, assignedProjects.size());
	                
	                if (projectChoice > 0) {
	                    Project selectedProject = assignedProjects.get(projectChoice - 1);
	                    viewProjectDetails(selectedProject, officer);
	                }
	            }
	            break;
	            
	        case 0:
	            displayOfficerMenu(currentOfficer);
	            break;
	            
	        default:
	            System.out.println("Invalid choice.");
	            break;
	    }
	    
	    // Wait for user input before returning to menu
	    System.out.println("\nPress Enter to return to the main menu...");
	    scanner.nextLine();
	    
	    // Return to the officer menu
	    displayOfficerMenu(officer);
	}
	
	private void manageApplicationsAndBookings(HDBOfficer officer) {
	    while (true) {
	        System.out.println("\n======== MANAGE FLAT BOOKINGS ========");
	        System.out.println("1. View Pending Flat Booking Requests");
	        System.out.println("2. View Approved Flat Bookings");
	        System.out.println("0. Back to Main Menu");
	        
	        int choice = getValidIntegerInput("\nEnter your choice: ", 0, 2);
	        
	        switch (choice) {
	            case 1:
	                viewPendingFlatBookingRequests(officer);
	                break;
	                
	            case 2:
	                viewApprovedFlatBookings(officer);
	                break;
	                
	            case 0:
	                displayOfficerMenu(officer);
	                return;
	                
	            default:
	                System.out.println("Invalid choice. Please try again.");
	                break;
	        }
	    }
	}

	private void viewPendingFlatBookingRequests(HDBOfficer officer) {
	    System.out.println("\n======== PENDING FLAT BOOKING REQUESTS ========");
	    
	    // Get all applications that are SUCCESSFUL but don't have a booking yet
	    List<ProjectApplication> allApplications = applicationController.getAllApplications();
	    
	    // Filter to only show successful applications with no booking yet
	    List<ProjectApplication> pendingBookingRequests = new ArrayList<>();
	    List<Project> assignedProjects = officer.getAssignedProjects();
	    
	    for (ProjectApplication app : allApplications) {
	        // Only show applications that are:
	        // 1. Successful (already approved by manager/system)
	        // 2. For projects this officer is assigned to
	        // 3. Have a selected flat type (applicant has chosen a flat type)
	        // 4. Don't have an approved booking yet
	        if (app.getStatus() == ApplicationStatus.SUCCESSFUL &&
	            assignedProjects.contains(app.getProject()) &&
	            app.getSelectedFlatType() != null &&
	            !bookingController.hasApprovedBooking(app)) {
	            pendingBookingRequests.add(app);
	        }
	    }
	    
	    if (pendingBookingRequests.isEmpty()) {
	        System.out.println("No pending flat booking requests for your assigned projects.");
	    } else {
	    	System.out.println("Pending Flat Booking Requests:");
	    	System.out.println("ID      Applicant Name           Project Name             Requested Flat Type");
	    	System.out.println("------------------------------------------------------------------------------------------------");
	    	for (int i = 0; i < pendingBookingRequests.size(); i++) {
	    	    ProjectApplication app = pendingBookingRequests.get(i);
	    	    Applicant applicant = app.getApplicant();
	    	    Project project = app.getProject();
	    	    FlatType requestedType = app.getSelectedFlatType();
	    	    
	    	    System.out.printf("%-7d %-25s %-25s %10s%n", 
	    	        i + 1, 
	    	        applicant.getName(), 
	    	        project.getProjectName(),
	    	        requestedType.toString()
	    	    );
	    	}
	        
	        int requestId = getValidIntegerInput("\nEnter booking request ID to process (or 0 to cancel): ", 0, pendingBookingRequests.size());
	        
	        if (requestId > 0) {
	            processBookingRequest(pendingBookingRequests.get(requestId - 1), officer);
	        }
        }
	    
	    // Wait for user input before returning
	    System.out.println("\nPress Enter to continue...");
	    scanner.nextLine();
	    
	}

	private void processBookingRequest(ProjectApplication application, HDBOfficer officer) {
	    Applicant applicant = application.getApplicant();
	    Project project = application.getProject();
	    FlatType requestedType = application.getSelectedFlatType();
	    
	    System.out.println("\n======== PROCESS FLAT BOOKING REQUEST ========");
	    System.out.println("Applicant: " + applicant.getName() + " (" + applicant.getNric() + ")");
	    System.out.println("Project: " + project.getProjectName());
	    System.out.println("Application Status: " + application.getStatus().toString());
	    System.out.println("Requested Flat Type: " + requestedType.toString());
	    
	    // Check flat availability
	    ProjectFlats projectFlats = project.getProjectFlats();
	    int availableUnits = projectFlats.getAvailableFlatCount(requestedType);
	    
	    System.out.println("\nFlat Availability:");
	    System.out.println("Flat Type: " + requestedType.toString());
	    System.out.println("Available Units: " + availableUnits);
	    
	    if (availableUnits <= 0) {
	        System.out.println("\nWARNING: No units of this type are currently available.");
	    }
	    
	    // Options
	    System.out.println("\nOptions:");
	    System.out.println("1. Approve Booking Request");
	    System.out.println("2. Reject Booking Request");
	    System.out.println("0. Back");
	    
	    int choice = getValidIntegerInput("\nEnter your choice: ", 0, 2);
	    
	    switch (choice) {
	        case 1:
	            if (availableUnits <= 0) {
	                System.out.println("Cannot approve booking - no units available of the requested type.");
	                
	                // Auto-reject the booking due to no available units
	                String autoRejectionReason = "No available units of type " + requestedType.toString() + " in project " + project.getProjectName();
	                boolean rejectionResult = bookingController.rejectBooking(application, autoRejectionReason);
	                
	                if (rejectionResult) {
	                    System.out.println("Booking has been automatically rejected due to lack of available units.");
	                    // Reset the flat type selection to allow the applicant to choose another type
	                    application.setSelectedFlatType(null);
	                    System.out.println("The applicant will be notified and may select a different flat type.");
	                } else {
	                    System.out.println("Failed to record booking rejection. Please try again or reject manually.");
	                }
	            } else {
	                // Book a flat of the requested type
	                int flatId = project.bookFlat(requestedType);
	                
	                if (flatId != -1) {
	                    // Create a booking record with APPROVED status
	                    FlatBooking booking = new FlatBooking(applicant, project, requestedType, flatId);
	                    booking.setBookingStatus(FlatBooking.STATUS_APPROVED); // Explicitly set status to approved
	                    
	                    boolean bookingCreated = bookingController.createBooking(booking);
	                    
	                    if (bookingCreated) {
	                        System.out.println("Booking approved successfully!");
	                        System.out.println("Flat ID: " + flatId);
	                        System.out.println("A notification has been sent to the applicant.");
	                    } else {
	                        // If booking creation failed, release the flat
	                        project.releaseFlat(flatId);
	                        System.out.println("Failed to create booking record. The flat has been released.");
	                    }
	                } else {
	                    System.out.println("Failed to book flat. No units available.");
	                    
	                    // Auto-reject in case bookFlat failed
	                    String autoRejectionReason = "Unable to allocate a flat unit of type " + requestedType.toString();
	                    boolean rejectionResult = bookingController.rejectBooking(application, autoRejectionReason);
	                    
	                    if (rejectionResult) {
	                        System.out.println("Booking has been automatically rejected due to allocation failure.");
	                        // Reset the flat type selection to allow the applicant to choose another type
	                        application.setSelectedFlatType(null);
	                        System.out.println("The applicant will be notified and may select a different flat type.");
	                    }
	                }
	            }
	            break;
	            
	        case 2:
	            // Reject booking request
	            System.out.print("Enter reason for rejection: ");
	            String rejectionReason = scanner.nextLine();
	            
	            boolean rejectionResult = bookingController.rejectBooking(application, rejectionReason);
	            
	            if (rejectionResult) {
	                System.out.println("Booking request rejected successfully.");
	                // Reset the flat type selection to allow the applicant to choose another type
	                application.setSelectedFlatType(null);
	                System.out.println("The applicant will be notified and may select a different flat type.");
	            } else {
	                System.out.println("Failed to reject booking request. Please try again.");
	            }
	            break;
	            
	        case 0:
	            return;
	    }
	}

	private void viewApprovedFlatBookings(HDBOfficer officer) {
	    System.out.println("\n======== APPROVED FLAT BOOKINGS ========");
	    
	    // Get all approved bookings for the officer's assigned projects
	    List<FlatBooking> approvedBookings = new ArrayList<>();
	    List<Project> assignedProjects = officer.getAssignedProjects();
	    
	    // Assuming bookingController has a method to get all bookings
	    List<FlatBooking> allBookings = bookingController.getAllBookings();
	    
	    for (FlatBooking booking : allBookings) {
	        if (assignedProjects.contains(booking.getProject())) {
	            approvedBookings.add(booking);
	        }
	    }
	    
	    if (approvedBookings.isEmpty()) {
	        System.out.println("No approved flat bookings for your assigned projects.");
	    } else {
	    	System.out.println("Approved Flat Bookings:");
	    	System.out.println("ID      Applicant Name           Project Name             Flat Type          Flat ID       Booking Date");
	    	System.out.println("--------------------------------------------------------------------------------------------------------");
	    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    	for (int i = 0; i < approvedBookings.size(); i++) {
	    	    FlatBooking booking = approvedBookings.get(i);
	    	    Applicant applicant = booking.getApplicant();
	    	    Project project = booking.getProject();
	    	    
	    	    System.out.printf("%-7d %-25s %-25s %-18s %10d %12s%n", 
	    	        i + 1, 
	    	        applicant.getName(), 
	    	        project.getProjectName(),
	    	        booking.getFlatType().toString(),
	    	        booking.getFlatId(),
	    	        dateFormat.format(booking.getBookingDate())
	    	    );
	    	}
	        
	        int bookingId = getValidIntegerInput("\nEnter booking ID to view details (or 0 to cancel): ", 0, approvedBookings.size());
	        
	        if (bookingId > 0) {
	            viewBookingDetails(approvedBookings.get(bookingId - 1));
	        }
        }
	    
	    // Wait for user input before returning
	    System.out.println("\nPress Enter to continue...");
	    scanner.nextLine();
	}

	private void viewBookingDetails(FlatBooking booking) {
	    System.out.println("\n======== BOOKING DETAILS ========");
	    System.out.println(booking.getBookingDetails());
	    
	    // Print receipt option
	    System.out.println("\nOptions:");
	    System.out.println("1. Generate Receipt");
	    System.out.println("0. Back");
	    
	    int choice = getValidIntegerInput("\nEnter your choice: ", 0, 1);
	    
	    if (choice == 1) {
	        // Check if receipt already exists
	        if (bookingController.hasReceipt(booking.getApplicant().getNric())) {
	            System.out.println("\nA receipt for this booking has already been generated.");
	            Receipt existingReceipt = bookingController.getReceiptForApplicant(booking.getApplicant().getNric());
	            System.out.println(existingReceipt.getContent());
	        } else {
	            // Generate and store the receipt
	            Receipt receipt = bookingController.generateAndStoreReceipt(booking);
	            
	            if (receipt != null) {
	                System.out.println("\n======== BOOKING RECEIPT ========");
	                System.out.println(receipt.getContent());
	                System.out.println("\nReceipt generated and stored successfully.");
	            } else {
	                System.out.println("Failed to generate receipt. Please try again later.");
	            }
	        }
	    }
	    
	    // Wait for user input before returning
	    System.out.println("\nPress Enter to continue...");
	    scanner.nextLine();
	}
	
	private void generateReceiptForBooking() {
	    System.out.println("\n=== GENERATE RECEIPT FOR BOOKING ===");
	    
	    // First check if officer has any assigned projects
	    if (currentOfficer.getAssignedProjects().isEmpty()) {
	        System.out.println("You are not assigned to any projects yet.");
	        System.out.println("Please register for and get approved for a project first.");
	        
	        // Wait for user input before returning to menu
	        System.out.println("\nPress Enter to return to the main menu...");
	        scanner.nextLine();
	        displayOfficerMenu(currentOfficer);
	        return;
	    }
	    
	    // Get list of booked applications for projects the officer is handling
	    List<ProjectApplication> bookedApplications = new ArrayList<>();
	    List<Project> assignedProjects = currentOfficer.getAssignedProjects();
	    
	    for (Project project : assignedProjects) {
	        List<ProjectApplication> projectApplications = applicationController.getApplicationsByProject(project);
	        
	        for (ProjectApplication app : projectApplications) {
	            if (app.getStatus() == ApplicationStatus.BOOKED && 
	                !bookingController.hasReceipt(app.getApplicant().getNric())) {
	                bookedApplications.add(app);
	            }
	        }
	    }
	    
	    if (bookedApplications.isEmpty()) {
	        System.out.println("No bookings available for receipt generation.");
	        System.out.println("All booked applications already have receipts or no bookings exist.");
	        
	        // Wait for user input before returning to menu
	        System.out.println("\nPress Enter to return to the main menu...");
	        scanner.nextLine();
	        displayOfficerMenu(currentOfficer);
	        return;
	    }
	    
	    // Display bookings without receipts
	    System.out.println("Bookings Available for Receipt Generation:");
	    System.out.println("ID      Applicant Name           Project Name             Flat Type");
	    System.out.println("----------------------------------------------------------------");
	    
	    for (int i = 0; i < bookedApplications.size(); i++) {
	        ProjectApplication app = bookedApplications.get(i);
	        System.out.printf("%-7d %-25s %-25s %10s%n", 
	            i + 1, 
	            app.getApplicant().getName(), 
	            app.getProject().getProjectName(),
	            app.getSelectedFlatType().toString()
	        );
	    }
	    
	    // Select booking for receipt generation
	    int choice = getValidIntegerInput("\nEnter the ID of the booking to generate receipt (or 0 to cancel): ", 0, bookedApplications.size());
	    
	    if (choice > 0) {
	        ProjectApplication selectedApplication = bookedApplications.get(choice - 1);
	        FlatBooking booking = bookingController.getBookingForApplication(selectedApplication);
	        
	        if (booking != null) {
	            // Generate and store the receipt
	            Receipt receipt = bookingController.generateAndStoreReceipt(booking);
	            
	            if (receipt != null) {
	                System.out.println("\n======== GENERATED RECEIPT ========");
	                System.out.println(receipt.getContent());
	                System.out.println("\nReceipt generated and stored successfully.");
	            } else {
	                System.out.println("Failed to generate receipt. Please try again later.");
	            }
	        } else {
	            System.out.println("No booking found for the selected application.");
	        }
	    }
	    
	    // Wait for user input before returning to menu
	    System.out.println("\nPress Enter to return to the main menu...");
	    scanner.nextLine();
	    
	    // Return to the officer menu
	    displayOfficerMenu(currentOfficer);
	}

	private void browseProjects(HDBOfficer officer) {
	    System.out.println("\n======== AVAILABLE PROJECTS (APPLICANT VIEW) ========");
	    
	    // Get visible projects for applications
	    List<Project> visibleProjects = projectController.getVisibleProjectsForApplicant(officer.getApplicantRole());
	    
	    if (visibleProjects.isEmpty()) {
	        System.out.println("No available projects at the moment.");
	    } else {
	        System.out.println("ID\tProject Name\t\tNeighborhood\t\tApplication Period\t\tStatus");
	        System.out.println("---------------------------------------------------------------------------------");
	        
	        // Get officer's assigned projects to check conflicts
	        List<Project> assignedProjects = officer.getAssignedProjects();
	        
	        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        
	        for (int i = 0; i < visibleProjects.size(); i++) {
	            Project project = visibleProjects.get(i);
	            
	            // Format dates for display
	            String openDate = dateFormat.format(project.getApplicationOpenDate());
	            String closeDate = dateFormat.format(project.getApplicationCloseDate());
	            
	            // Check if officer is assigned to this project (conflict)
	            boolean isAssigned = assignedProjects.contains(project);
	            
	            // Check for date overlaps with assigned projects
	            boolean hasDateOverlap = false;
	            for (Project assignedProject : assignedProjects) {
	                if (datesOverlap(assignedProject, project)) {
	                    hasDateOverlap = true;
	                    break;
	                }
	            }
	            
	            // Determine status based on constraints
	            String status = "";
	            if (isAssigned) {
	                status = "(Cannot apply - you are assigned as an officer)";
	            } else if (hasDateOverlap) {
	                status = "(Cannot apply - date overlap with assigned project)";
	            }
	            
	            System.out.printf("%-7d %-25s %-20s %-12s - %-12s %10s%n", 
	                    i + 1, 
	                    project.getProjectName(), 
	                    project.getNeighborhood(), 
	                    openDate, 
	                    closeDate,
	                    status
	                );
	        }
	        
	        System.out.println("\nOptions:");
	        System.out.println("1. View Project Details");
	        System.out.println("2. Apply for Project");
	        System.out.println("0. Back to Main Menu");
	        
	        int choice = getValidIntegerInput("\nEnter your choice: ", 0, 2);
	        
	        switch (choice) {
	            case 1:
	                int projectId = getValidIntegerInput("Enter project ID to view details: ", 1, visibleProjects.size());
	                viewProjectDetailsAsApplicant(visibleProjects.get(projectId - 1), officer);
	                browseProjects(officer);
	                break;
	                
	            case 2:
	                int applyProjectId = getValidIntegerInput("Enter project ID to apply: ", 1, visibleProjects.size());
	                Project selectedProject = visibleProjects.get(applyProjectId - 1);
	                
	                // Check constraints before allowing application
	                if (officer.getAssignedProjects().contains(selectedProject)) {
	                    System.out.println("\nERROR: You cannot apply for a project you are assigned to as an officer.");
	                    System.out.println("This would create a conflict of interest.");
	                } else {
	                    // Check for date overlaps
	                    boolean hasDateOverlap = false;
	                    for (Project assignedProject : officer.getAssignedProjects()) {
	                        if (datesOverlap(assignedProject, selectedProject)) {
	                            hasDateOverlap = true;
	                            break;
	                        }
	                    }
	                    
	                    if (hasDateOverlap) {
	                        System.out.println("\nERROR: You cannot apply for a project that overlaps with your assigned project dates.");
	                        System.out.println("Please select a project with non-overlapping dates.");
	                    } else {
	                        // Apply for project
	                        applyForProject(selectedProject, officer);
	                    }
	                }
	                browseProjects(officer);
	                break;
	                
	            case 0:
	                displayOfficerMenu(officer);
	                break;
	                
	            default:
	                System.out.println("Invalid choice.");
	                browseProjects(officer);
	                break;
	        }
	    }
	}

	private void viewProjectDetailsAsApplicant(Project project, HDBOfficer officer) {
	    System.out.println("\n======== PROJECT DETAILS (APPLICANT VIEW) ========");
	    System.out.println("Project Name: " + project.getProjectName());
	    System.out.println("Neighborhood: " + project.getNeighborhood());
	    
	    // Format dates for display
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    System.out.println("Application Period: " + 
	        dateFormat.format(project.getApplicationOpenDate()) + " - " + 
	        dateFormat.format(project.getApplicationCloseDate()));
	    
	    // Manager in Charge
	    System.out.println("Manager in Charge: " + project.getManagerInCharge().getName());
	    
	    // Display Flat Types and Availability
	    System.out.println("\nFlat Types and Availability:");
	    Map<FlatType, Integer> flatTypeUnits = project.getFlatTypeUnits();
	    ProjectFlats projectFlats = project.getProjectFlats();
	    
	    for (FlatType flatType : flatTypeUnits.keySet()) {
	        int totalUnits = flatTypeUnits.get(flatType);
	        int availableUnits = projectFlats.getAvailableFlatCount(flatType);
	        
	        System.out.printf("%s Flats: Total %d, Available %d%n", 
	            flatType.name(), 
	            totalUnits, 
	            availableUnits
	        );
	    }
	    
	    // Check Eligibility as an Applicant
	    List<FlatType> eligibleTypes = project.getEligibleFlatTypes(officer.getApplicantRole());
	    System.out.println("\nYour Eligible Flat Types:");
	    if (eligibleTypes.isEmpty()) {
	        System.out.println("  No flat types currently available for your profile.");
	    } else {
	        for (FlatType type : eligibleTypes) {
	            System.out.println("  - " + type.name());
	        }
	    }
	    
	    // Check constraints
	    boolean isAssigned = officer.getAssignedProjects().contains(project);
	    
	    // Check for date overlaps
	    boolean hasDateOverlap = false;
	    for (Project assignedProject : officer.getAssignedProjects()) {
	        if (datesOverlap(assignedProject, project)) {
	            hasDateOverlap = true;
	            break;
	        }
	    }
	    
	    if (isAssigned) {
	        System.out.println("\nNOTE: You cannot apply for this project as you are assigned to it as an officer.");
	        System.out.println("This would create a conflict of interest.");
	    } else if (hasDateOverlap) {
	        System.out.println("\nNOTE: You cannot apply for this project as its dates overlap with your assigned project dates.");
	        System.out.println("Projects must have non-overlapping application periods.");
	    }
	    
	    // Wait for user input before returning
	    System.out.println("\nPress Enter to continue...");
	    scanner.nextLine();
	}

	private void applyForProject(Project project, HDBOfficer officer) {
	    System.out.println("\n======== APPLY FOR PROJECT ========");
	    System.out.println("Project: " + project.getProjectName());
	    
	    System.out.println("\nDo you want to submit an application for this project?");
	    System.out.println("1. Yes");
	    System.out.println("0. No (Cancel)");
	    
	    int choice = getValidIntegerInput("\nEnter your choice: ", 0, 1);
	    
	    if (choice == 1) {
	        // Submit application
	        boolean result = applicationController.submitApplication(officer.getApplicantRole(), project);
	        
	        if (result) {
	            System.out.println("\n=== APPLICATION SUBMITTED ===");
	            System.out.println("Your application for " + project.getProjectName() + " has been submitted.");
	            System.out.println("Status: PENDING");
	        } else {
	            System.out.println("\nFailed to submit application. You may already have an active application.");
	        }
	    } else {
	        System.out.println("\nApplication canceled.");
	    }
	    
	    // Wait for user input before returning
	    System.out.println("\nPress Enter to continue...");
	    scanner.nextLine();
	}

	private void viewOfficerApplicationStatus(HDBOfficer officer) {
	    System.out.println("\n======== MY APPLICATION STATUS ========");
	    
	    // Get the officer's application as an applicant
	    ProjectApplication application = applicationController.getApplicationByApplicantNRIC(officer.getNric());
	    
	    // Check if the officer has an application
	    if (application == null) {
	        System.out.println("You currently have no active applications as an applicant.");
	    } else {
	        // Display application details
	        Project project = application.getProject();
	        
	        System.out.println("Project Name: " + project.getProjectName());
	        System.out.println("Neighborhood: " + project.getNeighborhood());
	        System.out.println("Status: " + application.getStatus().toString());
	        
	        // Format dates for display
	        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	        System.out.println("Application Period: " + 
	            dateFormat.format(project.getApplicationOpenDate()) + " - " + 
	            dateFormat.format(project.getApplicationCloseDate()));
	        
	        // If a flat type has been selected, show it. Otherwise, show "Not yet chosen"
	        FlatType selectedFlatType = application.getSelectedFlatType();
	        if (selectedFlatType != null) {
	            System.out.println("Selected Flat Type: " + selectedFlatType.toString());
	        } else {
	            System.out.println("Selected Flat Type: Not yet chosen");
	        }
	        
	        // If there's a withdrawal request, show its status
	        String withdrawalStatus = application.getWithdrawalStatus();
	        if (withdrawalStatus != null) {
	            System.out.println("Withdrawal Status: " + withdrawalStatus);
	        }
	        
	        // Show options based on application status
	        System.out.println("\nOptions:");
	        
	        int maxChoice = 0;
	        
	        if (application.getStatus() == ApplicationStatus.SUCCESSFUL && selectedFlatType == null) {
	            // For successful applications with no flat type selected yet, show option to select flat type
	            System.out.println("1. Select Flat Type");
	            maxChoice = 1;
	        } else if (application.getStatus() == ApplicationStatus.BOOKED) {
	            // For booked applications, show option to view receipt
	            System.out.println("1. View Booking Receipt");
	            maxChoice = 1;
	        }
	        
	        System.out.println("0. Back to Main Menu");
	        
	        int choice = getValidIntegerInput("\nEnter your choice: ", 0, maxChoice);
	        
	        if (choice == 1) {
	            if (application.getStatus() == ApplicationStatus.SUCCESSFUL && selectedFlatType == null) {
	                // Select flat type
	                selectFlatType(application, project, officer);
	            } else if (application.getStatus() == ApplicationStatus.BOOKED) {
	                // View booking receipt
	                viewBookingReceipt(officer.getApplicantRole());
	            }
	        }
	    }
	    
	    // Wait for user input before returning to menu
	    System.out.println("\nPress Enter to return to the main menu...");
	    scanner.nextLine();
	    
	    displayOfficerMenu(officer);
	}

	private void selectFlatType(ProjectApplication application, Project project, HDBOfficer officer) {
	    System.out.println("\n=== SELECT FLAT TYPE ===");
	    
	    // Get eligible flat types for the applicant
	    List<FlatType> eligibleTypes = project.getEligibleFlatTypes(officer.getApplicantRole());
	    
	    if (eligibleTypes.isEmpty()) {
	        System.out.println("You are not eligible for any flat types in this project.");
	    } else {
	        System.out.println("Available and Eligible Flat Types:");
	        System.out.println("ID\tFlat Type\tAvailable Units");
	        System.out.println("----------------------------------------");
	        
	        ProjectFlats projectFlats = project.getProjectFlats();
	        List<FlatType> availableTypes = new ArrayList<>();
	        
	        for (int i = 0; i < eligibleTypes.size(); i++) {
	            FlatType flatType = eligibleTypes.get(i);
	            int availableUnits = projectFlats.getAvailableFlatCount(flatType);
	            
	            if (availableUnits > 0) {
	                availableTypes.add(flatType);
	                System.out.printf("%d\t%s\t\t%d%n", 
	                    availableTypes.size(), 
	                    flatType.toString(),
	                    availableUnits
	                );
	            }
	        }
	        
	        if (availableTypes.isEmpty()) {
	            System.out.println("No eligible flat types currently have available units.");
	        } else {
	            System.out.println("\nOptions:");
	            System.out.println("1. Select a Flat Type");
	            System.out.println("0. Cancel");
	            
	            int choice = getValidIntegerInput("\nEnter your choice: ", 0, 1);
	            
	            if (choice == 1) {
	                int flatTypeId = getValidIntegerInput("Enter the ID of the flat type you want to select: ", 1, availableTypes.size());
	                FlatType selectedType = availableTypes.get(flatTypeId - 1);
	                
	                // Update the application with the selected flat type
	                application.setSelectedFlatType(selectedType);
	                
	                System.out.println("\nFlat type " + selectedType.toString() + " has been selected.");
	                System.out.println("Your selection has been submitted and is pending officer approval.");
	                System.out.println("You will be notified once your booking is confirmed.");
	            }
	        }
	    }
	    
	    // Wait for user input before returning
	    System.out.println("\nPress Enter to continue...");
	    scanner.nextLine();
	}

	private void viewBookingReceipt(Applicant applicant) {
	    System.out.println("\n======== BOOKING RECEIPT ========");
	    
	    // Check if a receipt exists in the BookingController
	    if (bookingController.hasReceipt(applicant.getNric())) {
	        // Retrieve the receipt from BookingController
	        Receipt receipt = bookingController.getReceiptForApplicant(applicant.getNric());
	        
	        // Display the receipt content
	        System.out.println(receipt.getContent());
	    } else {
	        System.out.println("No receipt available. Please contact an HDB Officer to generate your receipt.");
	    }
	    
	    System.out.println("\nNote: To save this receipt, you can copy and paste the text above.");
	    
	    // Wait for user input before returning to menu
	    System.out.println("\nPress Enter to return to the main menu...");
	    scanner.nextLine();
	    
	    // Return to the applicant menu
	    displayOfficerMenu(currentOfficer);
	}
	
	private void displayOfficerProfile(HDBOfficer officer) {
	    System.out.println("\n=== OFFICER PROFILE ===");
	    
	    // Display basic officer information
	    System.out.println("Name: " + officer.getName());
	    System.out.println("NRIC: " + officer.getNric());
	    System.out.println("Age: " + officer.getAge());
	    System.out.println("Marital Status: " + officer.getMaritalStatus().toString());
	    
	    // Display assigned projects if any
	    List<Project> assignedProjects = officer.getAssignedProjects();
	    if (assignedProjects.isEmpty()) {
	        System.out.println("Assigned Projects: None");
	    } else {
	        System.out.println("Assigned Projects:");
	        for (Project project : assignedProjects) {
	            System.out.println("  - " + project.getProjectName());
	        }
	    }
	    
	    // Get the officer's application as an applicant
	    ProjectApplication application = applicationController.getApplicationByApplicantNRIC(officer.getNric());
	    
	    // Display applied project information if it exists
	    if (application == null) {
	        System.out.println("Project Applied: None");
	    } else {
	        Project project = application.getProject();
	        ApplicationStatus status = application.getStatus();
	        
	        // Get the flat type, or "Not yet chosen" if null
	        String flatTypeStr = "Not yet chosen";
	        if (application.getSelectedFlatType() != null) {
	            flatTypeStr = application.getSelectedFlatType().toString();
	        }
	        
	        System.out.println("Project Applied: " + project.getProjectName() + 
	                          " (" + status.toString() + ", " + flatTypeStr + ")");
	    }
	    
	    // Wait for user input before returning to menu
	    System.out.println("\nPress Enter to return to the main menu...");
	    scanner.nextLine();
	    
	    // Return to the officer menu
	    displayOfficerMenu(officer);
	}
	
	private void changePasswordInterface() {
        System.out.println("\n=== CHANGE PASSWORD ===");
        
        // Ask for old password
        System.out.print("Enter your current password: ");
        String oldPassword = scanner.nextLine();
        
        // Validate old password
        if (!currentOfficer.getPassword().equals(oldPassword)) {
            System.out.println("Incorrect current password. Password change canceled.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            displayOfficerMenu(currentOfficer);
            return;
        }
        
        // Ask for new password
        String newPassword;
        while (true) {
            System.out.print("Enter new password: ");
            newPassword = scanner.nextLine();
            
            // Validate new password using the AuthController
            if (authController.validatePassword(newPassword)) {
                break;  // If valid, break out of the loop
            } else {
                System.out.println("Password must be at least 8 characters. Please try again.");
            }
        }
        
        // Confirm new password
        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();
        
        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Password change canceled.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            displayOfficerMenu(currentOfficer);
            return;
        }
        
        // Change the password
        boolean success = currentOfficer.changePassword(oldPassword, newPassword);
        
        if (success) {
            System.out.println("Password changed successfully. Please log in again.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            
            // Return to login menu
            userInterface.displayLoginMenu();
        } else {
            System.out.println("Failed to change password. Please try again later.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            displayOfficerMenu(currentOfficer);
        }
    }
	
	private void viewPendingEnquiries(HDBOfficer officer) {
		System.out.println("\n======== PENDING ENQUIRIES ========");
		
		// Get enquiries for projects this officer is assigned to
		List<Enquiry> pendingEnquiries = new ArrayList<>();
		List<Project> assignedProjects = officer.getAssignedProjects();
		
		for (Enquiry enquiry : enquiryController.getAllEnquiries()) {
			// Include enquiries that have no response and are for projects this officer is assigned to
			if (enquiry.getResponse() == null && 
				(enquiry.getProject() == null || assignedProjects.contains(enquiry.getProject()))) {
				pendingEnquiries.add(enquiry);
			}
		}
		
		if (pendingEnquiries.isEmpty()) {
			System.out.println("No pending enquiries for your assigned projects.");
		} else {
			System.out.println("ID\tApplicant\t\tProject\t\tSubmission Date\t\tEnquiry Content");
			System.out.println("------------------------------------------------------------------------------------------");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			for (int i = 0; i < pendingEnquiries.size(); i++) {
				Enquiry enquiry = pendingEnquiries.get(i);
				String projectName = enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General";
				
				System.out.printf("%-3d %-20s %-20s %-12s %s%n", 
					i + 1, 
					enquiry.getApplicant().getName(), 
					projectName,
					dateFormat.format(enquiry.getSubmissionDate()),
					enquiry.getEnquiryContent().length() > 30 ? 
						enquiry.getEnquiryContent().substring(0, 27) + "..." : 
						enquiry.getEnquiryContent()
				);
			}
			
			System.out.println("\nOptions:");
			System.out.println("1. View Enquiry Details");
			System.out.println("2. Respond to Enquiry");
			System.out.println("0. Back to Main Menu");
			
			System.out.print("\nEnter your choice: ");
			try {
				int choice = getValidIntegerInput("Enter your choice: ", 0, 2);
				
				switch (choice) {
					case 1:
						System.out.print("Enter enquiry ID to view details: ");
						int viewId = getValidIntegerInput("Enter your choice: ", 1, pendingEnquiries.size());
						
						if (viewId > 0 && viewId <= pendingEnquiries.size()) {
							viewEnquiryDetails(pendingEnquiries.get(viewId - 1));
						} else {
							System.out.println("Invalid enquiry ID.");
						}
						viewPendingEnquiries(officer); // Return to the pending enquiries list
						break;
						
					case 2:
						System.out.print("Enter enquiry ID to respond: ");
						int respondId = getValidIntegerInput("Enter your choice: ", 1, pendingEnquiries.size());
						
						if (respondId > 0 && respondId <= pendingEnquiries.size()) {
							respondToEnquiry(pendingEnquiries.get(respondId - 1), officer);
						} else {
							System.out.println("Invalid enquiry ID.");
							viewPendingEnquiries(officer);
						}
						break;
						
					case 0:
						// Return to menu happens after this function
						break;
						
					default:
						System.out.println("Invalid choice.");
						viewPendingEnquiries(officer);
						break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a number.");
				viewPendingEnquiries(officer);
			}
		}
		
		// Wait for user input before returning to menu
		System.out.println("\nPress Enter to return to the main menu...");
		scanner.nextLine();
		
		// Return to the officer menu
		displayOfficerMenu(officer);
	}
	
	private void viewEnquiryDetails(Enquiry enquiry) {
		System.out.println("\n======== ENQUIRY DETAILS ========");
		System.out.println("Applicant: " + enquiry.getApplicant().getName() + " (" + enquiry.getApplicant().getNric() + ")");
		
		String projectName = enquiry.getProject() != null ? 
							   enquiry.getProject().getProjectName() : "General Enquiry";
		System.out.println("Project: " + projectName);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		System.out.println("Submission Date: " + dateFormat.format(enquiry.getSubmissionDate()));
		
		System.out.println("\nEnquiry Content:");
		System.out.println(enquiry.getEnquiryContent());
		
		if (enquiry.getResponse() != null && !enquiry.getResponse().isEmpty()) {
			System.out.println("\nResponse:");
			System.out.println(enquiry.getResponse());
		} else {
			System.out.println("\nStatus: Pending Response");
		}
		
		// Wait for user input before returning
		System.out.println("\nPress Enter to continue...");
		scanner.nextLine();
	}

	private void respondToEnquiries(HDBOfficer officer) {
		System.out.println("\n======== RESPOND TO ENQUIRIES ========");
		
		// Get all pending enquiries for this officer's projects
		List<Enquiry> pendingEnquiries = new ArrayList<>();
		List<Project> assignedProjects = officer.getAssignedProjects();
		
		for (Enquiry enquiry : enquiryController.getAllEnquiries()) {
			if (enquiry.getResponse() == null && 
				(enquiry.getProject() == null || assignedProjects.contains(enquiry.getProject()))) {
				pendingEnquiries.add(enquiry);
			}
		}
		
		if (pendingEnquiries.isEmpty()) {
			System.out.println("No pending enquiries to respond to.");
		} else {
			System.out.println("Pending Enquiries:");
			System.out.println("ID\tApplicant\t\tProject\t\tSubmission Date\t\tEnquiry Content");
			System.out.println("------------------------------------------------------------------------------------------");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			for (int i = 0; i < pendingEnquiries.size(); i++) {
				Enquiry enquiry = pendingEnquiries.get(i);
				String projectName = enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General";
				
				System.out.printf("%-3d %-20s %-20s %-12s %s%n", 
					i + 1, 
					enquiry.getApplicant().getName(), 
					projectName,
					dateFormat.format(enquiry.getSubmissionDate()),
					enquiry.getEnquiryContent().length() > 30 ? 
						enquiry.getEnquiryContent().substring(0, 27) + "..." : 
						enquiry.getEnquiryContent()
				);
			}
			
			System.out.print("\nEnter enquiry ID to respond to (or 0 to cancel): ");
			try {
				int enquiryId = getValidIntegerInput("Enter your choice: ", 0, pendingEnquiries.size());
				
				if (enquiryId > 0 && enquiryId <= pendingEnquiries.size()) {
					respondToEnquiry(pendingEnquiries.get(enquiryId - 1), officer);
				} else if (enquiryId != 0) {
					System.out.println("Invalid enquiry ID.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a number.");
			}
		}
		
		// Wait for user input before returning to menu
		System.out.println("\nPress Enter to return to the main menu...");
		scanner.nextLine();
		
		// Return to the officer menu
		displayOfficerMenu(officer);
	}
	
	private void respondToEnquiry(Enquiry enquiry, HDBOfficer officer) {
		System.out.println("\n======== RESPOND TO ENQUIRY ========");
		
		// Display enquiry details
		viewEnquiryDetails(enquiry);
		
		// Get response from officer
		System.out.println("\nEnter your response:");
		String response = scanner.nextLine();
		
		if (response.trim().isEmpty()) {
			System.out.println("Response cannot be empty. Operation cancelled.");
		} else {
			// Update the enquiry with the response
			boolean success = enquiryController.respondToEnquiry(enquiry, response, officer);
			
			if (success) {
				System.out.println("\nResponse submitted successfully!");
				System.out.println("The applicant will be notified of your response.");
			} else {
				System.out.println("\nFailed to submit response. Please try again later.");
			}
		}
	}

	private void submitEnquiry(HDBOfficer officer) {
		System.out.println("\n======== SUBMIT ENQUIRY ========");
		
		// Get the applicant role of the officer
		Applicant applicantRole = officer.getApplicantRole();
		
		// Ask if enquiry is about a specific project
		System.out.println("Is this enquiry about a specific project?");
		System.out.println("1. Yes");
		System.out.println("2. No (General Enquiry)");
		
		System.out.print("\nEnter your choice: ");
		int projectChoice = getValidIntegerInput("Enter your choice: ", 1, 2);
		
		Project selectedProject = null;
		
		if (projectChoice == 1) {
			// Get visible projects
			List<Project> visibleProjects = projectController.getVisibleProjectsForApplicant(applicantRole);
			
			if (visibleProjects.isEmpty()) {
				System.out.println("No projects available. Submitting as a general enquiry.");
			} else {
				System.out.println("\nSelect Project:");
				for (int i = 0; i < visibleProjects.size(); i++) {
					System.out.println((i+1) + ". " + visibleProjects.get(i).getProjectName());
				}
				
				System.out.print("\nEnter project number (or 0 for general enquiry): ");
				int projectNum = getValidIntegerInput("Enter your choice: ", 0, visibleProjects.size());
				
				if (projectNum > 0 && projectNum <= visibleProjects.size()) {
					selectedProject = visibleProjects.get(projectNum - 1);
					System.out.println("Selected project: " + selectedProject.getProjectName());
				} else {
					System.out.println("No project selected. Submitting as a general enquiry.");
				}
			}
		}
		
		// Get enquiry content
		System.out.println("\nEnter your enquiry:");
		String enquiryContent = scanner.nextLine();
		
		if (enquiryContent.trim().isEmpty()) {
			System.out.println("Enquiry content cannot be empty. Operation cancelled.");
		} else {
			// Create the enquiry
			Enquiry newEnquiry = enquiryController.createEnquiry(applicantRole, selectedProject, enquiryContent);
			
			if (newEnquiry != null) {
				System.out.println("\nEnquiry submitted successfully!");
				System.out.println("You will be notified when a response is available.");
			} else {
				System.out.println("\nFailed to submit enquiry. Please try again later.");
			}
		}
		
		// Wait for user input before returning to menu
		System.out.println("\nPress Enter to return to the main menu...");
		scanner.nextLine();
		
		// Return to the officer menu
		displayOfficerMenu(officer);
	}

	private void viewMyEnquiries(HDBOfficer officer) {
		System.out.println("\n======== MY ENQUIRIES ========");
		
		// Get the officer's enquiries as an applicant
		Applicant applicantRole = officer.getApplicantRole();
		List<Enquiry> myEnquiries = enquiryController.getEnquiriesByApplicant(applicantRole);
		
		if (myEnquiries.isEmpty()) {
			System.out.println("You have not submitted any enquiries.");
		} else {
			System.out.println("Your Enquiries:");
			System.out.println("ID\tProject\t\tSubmission Date\t\tStatus\t\tEnquiry Content");
			System.out.println("------------------------------------------------------------------------------------------");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			for (int i = 0; i < myEnquiries.size(); i++) {
				Enquiry enquiry = myEnquiries.get(i);
				String projectName = enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General";
				String status = enquiry.getResponse() != null ? "Responded" : "Pending";
				
				System.out.printf("%-3d %-20s %-12s %-12s %s%n", 
					i + 1, 
					projectName,
					dateFormat.format(enquiry.getSubmissionDate()),
					status,
					enquiry.getEnquiryContent().length() > 30 ? 
						enquiry.getEnquiryContent().substring(0, 27) + "..." : 
						enquiry.getEnquiryContent()
				);
			}
			
			System.out.println("\nOptions:");
			System.out.println("1. View Enquiry Details");
			System.out.println("2. Edit Enquiry");
			System.out.println("3. Delete Enquiry");
			System.out.println("0. Back to Main Menu");
			
			System.out.print("\nEnter your choice: ");
				int choice = getValidIntegerInput("\nEnter your choice: ", 0, 3);
				
				switch (choice) {
					case 1:
						System.out.print("Enter enquiry ID to view details: ");
						int viewId = getValidIntegerInput("\nEnter your choice: ", 1, myEnquiries.size());
						
						if (viewId > 0 && viewId <= myEnquiries.size()) {
							viewEnquiryDetails(myEnquiries.get(viewId - 1));
						} else {
							System.out.println("Invalid enquiry ID.");
						}
						viewMyEnquiries(officer); // Return to list
						break;
						
					case 2:
						System.out.print("Enter enquiry ID to edit: ");
						int editId = getValidIntegerInput("\nEnter your choice: ", 1, myEnquiries.size());
						
						if (editId > 0 && editId <= myEnquiries.size()) {
							editMyEnquiry(myEnquiries.get(editId - 1), officer);
						} else {
							System.out.println("Invalid enquiry ID.");
							viewMyEnquiries(officer);
						}
						break;
						
					case 3:
						System.out.print("Enter enquiry ID to delete (0 to cancel): ");
						int deleteId = getValidIntegerInput("\nEnter your choice: ", 0, myEnquiries.size());
						
						if (deleteId > 0 && deleteId <= myEnquiries.size()) {
							deleteMyEnquiry(myEnquiries.get(deleteId - 1), officer);
						} else {
							System.out.println("Invalid enquiry ID.");
							viewMyEnquiries(officer);
						}
						break;
						
					case 0:
						displayOfficerMenu(currentOfficer);
						break;
						
					default:
						System.out.println("Invalid choice.");
						viewMyEnquiries(officer);
						break;
				}
		}
		
		// Wait for user input before returning to menu
		System.out.println("\nPress Enter to return to the main menu...");
		scanner.nextLine();
		
		// Return to the officer menu
		displayOfficerMenu(officer);
	}

	private void editMyEnquiry(Enquiry enquiry, HDBOfficer officer) {
		System.out.println("\n======== EDIT ENQUIRY ========");
		
		// Check if enquiry already has a response
		if (enquiry.getResponse() != null) {
			System.out.println("You cannot edit an enquiry that has already been responded to.");
			
			// Wait for user input before returning
			System.out.println("\nPress Enter to continue...");
			scanner.nextLine();
			
			// Return to view my enquiries
			viewMyEnquiries(officer);
			return;
		}
		
		// Display current enquiry
		System.out.println("Current Enquiry Content:");
		System.out.println(enquiry.getEnquiryContent());
		
		// Get new content
		System.out.println("\nEnter new enquiry content (or leave empty to cancel):");
		String newContent = scanner.nextLine();
		
		if (newContent.trim().isEmpty()) {
			System.out.println("Edit cancelled.");
		} else {
			// Update the enquiry
			boolean success = enquiryController.editEnquiry(enquiry, newContent);
			
			if (success) {
				System.out.println("\nEnquiry updated successfully!");
			} else {
				System.out.println("\nFailed to update enquiry. Please try again later.");
			}
		}
		
		// Wait for user input before returning
		System.out.println("\nPress Enter to continue...");
		scanner.nextLine();
		
		// Return to view my enquiries
		viewMyEnquiries(officer);
	}

	private void deleteMyEnquiry(Enquiry enquiry, HDBOfficer officer) {
		System.out.println("\n======== DELETE ENQUIRY ========");
		
		// Display enquiry to be deleted
		System.out.println("Enquiry to be deleted:");
		System.out.println("Project: " + (enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General"));
		System.out.println("Submission Date: " + new SimpleDateFormat("dd/MM/yyyy").format(enquiry.getSubmissionDate()));
		System.out.println("Content: " + enquiry.getEnquiryContent());
		
		// Confirmation
		System.out.print("\nAre you sure you want to delete this enquiry? (Y/N): ");
		String confirm = scanner.nextLine();
		
		if (confirm.equalsIgnoreCase("Y")) {
			// Delete the enquiry
			boolean success = enquiryController.deleteEnquiry(enquiry);
			
			if (success) {
				System.out.println("\nEnquiry deleted successfully!");
			} else {
				System.out.println("\nFailed to delete enquiry. Please try again later.");
			}
		} else {
			System.out.println("Delete operation cancelled.");
		}
		
		// Wait for user input before returning
		System.out.println("\nPress Enter to continue...");
		scanner.nextLine();
		
		// Return to view my enquiries
		viewMyEnquiries(officer);
	}
	
	private void withdrawalRequest() {
        System.out.println("\n=== WITHDRAWAL REQUEST ===");
        
        // Get the current applicant's application
        ProjectApplication application = applicationController.getApplicationByApplicantNRIC(currentOfficer.getApplicantRole().getNric());
        
        // Check if the applicant has an application
        if (!currentOfficer.getApplicantRole().requestWithdrawal() || application == null) {
            System.out.println("You currently have no active applications to withdraw.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }
        
        // Check if there's already a withdrawal request
        if (application.getWithdrawalStatus() != null) {
            System.out.println("You already have a withdrawal request with status: " + application.getWithdrawalStatus());
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            return;
        }
        
        // Display application details
        Project project = application.getProject();
        System.out.println("You are about to request withdrawal for:");
        System.out.println("Project: " + project.getProjectName());
        System.out.println("Status: " + application.getStatus().toString());
        
        // Ask for confirmation
        System.out.println("\nWarning: Withdrawal requests are subject to approval and may incur penalties.");
        System.out.println("Do you want to proceed?");
        System.out.println("1. Yes, submit withdrawal request");
        System.out.println("0. No, cancel and return to menu");
        
        System.out.print("\nEnter your choice: ");
        	int choice = getValidIntegerInput("Enter enquiry ID to delete: ", 0, 1);
            
            if (choice == 1) {
                // Submit withdrawal request through controller
                Withdrawal withdrawal = withdrawalController.submitWithdrawal(currentOfficer.getApplicantRole(), application);
                
                if (withdrawal != null) {
                    System.out.println("\nWithdrawal request submitted successfully.");
                    System.out.println("Your request is now pending approval from an HDB Manager.");
                    System.out.println("You will be notified once your request has been processed.");
                } else {
                    System.out.println("\nFailed to submit withdrawal request. Please try again later.");
                }
            } else if (choice != 0) {
                System.out.println("Invalid choice.");
            }
        
        // Wait for user input before returning to menu
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }
	
}