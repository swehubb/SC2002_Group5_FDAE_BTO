package bto.Boundaries;

import java.util.Scanner;
import bto.Controllers.*;
import bto.Entities.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import bto.EntitiesProjectRelated.*;
import bto.Enums.*;

public class ApplicantInterface {

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
    private Applicant currentApplicant;

    // Constructor
    public ApplicantInterface(Scanner scanner, AuthController authController, ProjectController projectController, 
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
    public void setCurrentApplicant(Applicant applicant) {
        this.currentApplicant = applicant;
    }
    
    // Method to get the current logged-in applicant
    public Applicant getCurrentApplicant() {
        return this.currentApplicant;
    }
    
    public void displayApplicantMenu(Applicant applicant) {
        setCurrentApplicant(applicant);
        
        while (true) {
            try {
                System.out.println("\n=== APPLICANT MENU ===");
                System.out.println("1. View Projects");
                System.out.println("2. View Application Status");
                System.out.println("3. Submit Enquiry");
                System.out.println("4. View Enquiries");
                System.out.println("5. Edit Enquiry");
                System.out.println("6. Delete Enquiry");
                System.out.println("7. Request Withdrawal");
                System.out.println("8. Change Password");
                System.out.println("9. Display Profile");
                System.out.println("10. Generate Receipt"); // Added this option
                System.out.println("0. Logout");
                System.out.print("Enter your choice: ");
                
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    System.out.println("Please enter a valid option.");
                    continue;
                }
                
                int choice = Integer.parseInt(input);
                
                switch(choice) {
                    case 1:
                        List<Project> visibleProjects = displayProjects();
                        Project selectedProject = displayProjectDetails(visibleProjects);
                        applicationConfirmation(selectedProject);
                        break;
                    case 2:
                        viewApplicationStatus();
                        break;
                    case 3:
                        // Submit Enquiry
                        break;
                    case 4:
                        // View Enquiries
                        break;
                    case 5:
                        // Edit Enquiry
                        break;
                    case 6:
                        // Delete Enquiry
                        break;
                    case 7:
                        // Request Withdrawal
                        break;
                    case 8:
                        changePasswordInterface();
                        break;
                    case 9:
                        displayProfileInterface();
                        break;
                    // In the switch statement, add a case for generating receipt
                    case 10:
                        generateReceiptInterface();
                        break;
                    case 0:
                        userInterface.displayLoginMenu();
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
    }
    
    private List<Project> displayProjects() {
        System.out.println("\n=== AVAILABLE PROJECTS ===");

        // Get visible projects using the existing method in ProjectController
        List<Project> visibleProjects = projectController.getVisibleProjectsForApplicant(currentApplicant);

        if (visibleProjects.isEmpty()) {
            System.out.println("No available projects at the moment.");
        } else {
            System.out.println("Available Projects:");
            System.out.println("ID      Project Name             Neighborhood           Application Period");
            System.out.println("--------------------------------------------------------------------------------");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (int i = 0; i < visibleProjects.size(); i++) {
                Project project = visibleProjects.get(i);
                String openDate = dateFormat.format(project.getApplicationOpenDate());
                String closeDate = dateFormat.format(project.getApplicationCloseDate());

                System.out.printf("%-7d %-25s %-20s %-12s - %-12s%n", 
                    i + 1, 
                    project.getProjectName(), 
                    project.getNeighborhood(), 
                    openDate, 
                    closeDate
                );
            }
        }
        return visibleProjects;
    }
    
    private Project displayProjectDetails(List<Project> visibleProjects) {
        System.out.print("Enter the project number to view details (or 0 to go back): ");
        try {
            int projectChoice = Integer.parseInt(scanner.nextLine());
            
            // Check if the choice is valid
            if (projectChoice == 0) {
                // Return to previous menu
                displayApplicantMenu(currentApplicant);
                return null;
            }
            
            if (projectChoice < 1 || projectChoice > visibleProjects.size()) {
                System.out.println("Invalid project number. Please try again.");
                return displayProjectDetails(visibleProjects);
                
            }
            
            // Get the selected project (subtract 1 because list is 0-indexed)
            Project selectedProject = visibleProjects.get(projectChoice - 1);
            
            // Display detailed project information
            System.out.println("\n=== PROJECT DETAILS ===");
            System.out.println("Project Name: " + selectedProject.getProjectName());
            System.out.println("Neighborhood: " + selectedProject.getNeighborhood());
            
            // Format dates for display
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            System.out.println("Application Open Date: " + dateFormat.format(selectedProject.getApplicationOpenDate()));
            System.out.println("Application Close Date: " + dateFormat.format(selectedProject.getApplicationCloseDate()));
            
            // Manager in Charge
            System.out.println("Manager in Charge: " + selectedProject.getManagerInCharge().getName());
            
            // Display Flat Types and Availability
            System.out.println("\nFlat Types and Availability:");
            System.out.println("Flat Type                Total Units           Available Units");
            System.out.println("----------------------------------------------------------------------");
            Map<FlatType, Integer> flatTypeUnits = selectedProject.getFlatTypeUnits();
            ProjectFlats projectFlats = selectedProject.getProjectFlats();

            for (FlatType flatType : flatTypeUnits.keySet()) {
                int totalUnits = flatTypeUnits.get(flatType);
                int availableUnits = projectFlats.getAvailableFlatCount(flatType);
                
                System.out.printf("%-25s %10d %20d%n", 
                    flatType.name(), 
                    totalUnits, 
                    availableUnits
                );
            }
            
            // Check Eligibility for the Current Applicant
            List<FlatType> eligibleTypes = selectedProject.getEligibleFlatTypes(currentApplicant);
            System.out.println("\nYour Eligible Flat Types:");
            if (eligibleTypes.isEmpty()) {
                System.out.println("  No flat types currently available for your profile.");
            } else {
                for (FlatType type : eligibleTypes) {
                    System.out.println("  - " + type.name());
                }
            }
            return selectedProject;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return displayProjectDetails(visibleProjects);
        }
    }
    
    private void applicationConfirmation(Project selectedProject) {
        System.out.println("\nOptions:");
        System.out.println("1) Apply");
        System.out.println("0) Go Back");
        System.out.print("Enter your choice: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    // Attempt to submit application
                    Boolean applicationResult = applicationController.submitApplication(currentApplicant, selectedProject);
                    
                    if (applicationResult) {
                        System.out.println("\n=== APPLICATION SUBMITTED ===");
                        System.out.println("Your application is pending.");
                        System.out.println("Project: " + selectedProject.getProjectName());
                        System.out.println("Status: Pending");
                        
                        // Prompt to continue
                        System.out.println("\nPress Enter to continue...");
                        scanner.nextLine();
                        
                        // Return to projects list
                        displayProjects();
                    } else {
                        System.out.println("Failed to submit application. You may already have an active application.");
                        displayProjects();
                    }
                    break;
                
                case 0:
                    // Go back to projects list
                    displayProjects();
                    break;
                
                default:
                    System.out.println("Invalid choice. Returning to projects list.");
                    displayProjects();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to projects list.");
            displayProjects();
        }
    }
    
    private void viewApplicationStatus() {
        System.out.println("\n=== APPLICATION STATUS ===");
        
        // Get the current applicant's application
        ProjectApplication application = applicationController.getApplicationByApplicantNRIC(currentApplicant.getNric());
        
        // Check if the applicant has an application
        if (application == null) {
            System.out.println("You currently have no active applications.");
        } else {
            // Display application details
            Project project = application.getProject();
            
            System.out.println("Project Name: " + project.getProjectName());
            System.out.println("Neighborhood: " + project.getNeighborhood());
            System.out.println("Status: " + application.getStatus().toString());
            
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
            
            if (application.getStatus() == ApplicationStatus.SUCCESSFUL && selectedFlatType == null) {
                // For successful applications with no flat type selected yet, show option to select flat type
                System.out.println("1. Select Flat Type");
            } else if (application.getStatus() == ApplicationStatus.BOOKED) {
                // For booked applications, show option to view receipt
                System.out.println("1. View Booking Receipt");
            }
            
            System.out.println("0. Back to Main Menu");
            
            System.out.print("\nEnter your choice: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                if (choice == 1) {
                    if (application.getStatus() == ApplicationStatus.SUCCESSFUL && selectedFlatType == null) {
                        // Select flat type
                        selectFlatType(application, project);
                    } else if (application.getStatus() == ApplicationStatus.BOOKED) {
                        // View booking receipt
                        viewBookingReceipt(currentApplicant);
                    }
                } else if (choice != 0) {
                    System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        
        // Wait for user input before returning to menu
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
        
        displayApplicantMenu(currentApplicant);
    }

    private void selectFlatType(ProjectApplication application, Project project) {
        System.out.println("\n=== SELECT FLAT TYPE ===");
        
        // Get eligible flat types for the applicant
        List<FlatType> eligibleTypes = project.getEligibleFlatTypes(currentApplicant);
        
        if (eligibleTypes.isEmpty()) {
            System.out.println("You are not eligible for any flat types in this project.");
        } else {
            System.out.println("Available and Eligible Flat Types:");
            System.out.println("ID      Flat Type                Available Units");
            System.out.println("-------------------------------------------------------");
            ProjectFlats projectFlats = project.getProjectFlats();
            List<FlatType> availableTypes = new ArrayList<>();

            for (int i = 0; i < eligibleTypes.size(); i++) {
                FlatType flatType = eligibleTypes.get(i);
                int availableUnits = projectFlats.getAvailableFlatCount(flatType);
                
                if (availableUnits > 0) {
                    availableTypes.add(flatType);
                    System.out.printf("%-7d %-25s %20d%n", 
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
                
                System.out.print("\nEnter your choice: ");
                try {
                    int choice = Integer.parseInt(scanner.nextLine());
                    
                    if (choice == 1) {
                        System.out.print("Enter the ID of the flat type you want to select: ");
                        int flatTypeId = Integer.parseInt(scanner.nextLine());
                        
                        if (flatTypeId < 1 || flatTypeId > availableTypes.size()) {
                            System.out.println("Invalid flat type ID.");
                        } else {
                            FlatType selectedType = availableTypes.get(flatTypeId - 1);
                            
                            // Update the application with the selected flat type
                            application.setSelectedFlatType(selectedType);
                            
                            System.out.println("\nFlat type " + selectedType.toString() + " has been selected.");
                            System.out.println("Your selection has been submitted and is pending officer approval.");
                            System.out.println("You will be notified once your booking is confirmed.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
        }
        
        // Wait for user input before returning
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private void changePasswordInterface() {
        System.out.println("\n=== CHANGE PASSWORD ===");
        
        // Ask for old password
        System.out.print("Enter your current password: ");
        String oldPassword = scanner.nextLine();
        
        // Validate old password
        if (!currentApplicant.getPassword().equals(oldPassword)) {
            System.out.println("Incorrect current password. Password change canceled.");
            System.out.println("\nPress Enter to return to the main menu...");
            scanner.nextLine();
            displayApplicantMenu(currentApplicant);
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
            displayApplicantMenu(currentApplicant);
            return;
        }
        
        // Change the password
        boolean success = currentApplicant.changePassword(oldPassword, newPassword);
        
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
            displayApplicantMenu(currentApplicant);
        }
    }
    
    private void displayProfileInterface() {
        System.out.println("\n=== APPLICANT PROFILE ===");
        
        // Display basic applicant information
        System.out.println("Name: " + currentApplicant.getName());
        System.out.println("NRIC: " + currentApplicant.getNric());
        System.out.println("Age: " + currentApplicant.getAge());
        System.out.println("Marital Status: " + currentApplicant.getMaritalStatus().toString());
        
        // Get the current applicant's application
        ProjectApplication application = applicationController.getApplicationByApplicantNRIC(currentApplicant.getNric());
        
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
        
        // Return to the applicant menu
        displayApplicantMenu(currentApplicant);
    }
    
// Add this new method
private void generateReceiptInterface() {
    System.out.println("\n=== GENERATE RECEIPT ===");
    
    ProjectApplication application = applicationController.getApplicationByApplicantNRIC(currentApplicant.getNric());
    
    if (application != null && application.getStatus() == ApplicationStatus.BOOKED) {
        // Generate receipt using the BookingController
        String receipt = currentApplicant.generateReceipt(bookingController);
        System.out.println(receipt);
        System.out.println("\nNote: To save this receipt, you can copy and paste the text above.");
    } else {
        System.out.println("You do not have any confirmed flat bookings.");
        System.out.println("A receipt can only be generated after a successful booking.");
    }
    
    // Wait for user input before returning to menu
    System.out.println("\nPress Enter to return to the main menu...");
    scanner.nextLine();
    
    // Return to the applicant menu
    displayApplicantMenu(currentApplicant);
}
// Update the viewBookingReceipt method to use applicant.generateReceipt()
private void viewBookingReceipt(Applicant applicant) {
    System.out.println("\n======== BOOKING RECEIPT ========");
    
    // Use the Applicant's generateReceipt method directly
    String receipt = applicant.generateReceipt();
    System.out.println(receipt);
    
    System.out.println("\nNote: To save this receipt, you can copy and paste the text above.");
    
    // Wait for user input before returning to menu
    System.out.println("\nPress Enter to return to the main menu...");
    scanner.nextLine();
    
    // Return to the applicant menu
    displayApplicantMenu(applicant);
}
