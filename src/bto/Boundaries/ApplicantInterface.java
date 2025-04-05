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
                System.out.println("0. Logout");
                
                int choice = getValidIntegerInput("Enter your choice: ", 0, 9);
                
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
                        submitEnquiry();
                        break;
                    case 4:
                        viewEnquiries();
                        break;
                    case 5:
                        editEnquiryFromMenu();
                        break;
                    case 6:
                        deleteEnquiryFromMenu();
                        break;
                    case 7:
                    	withdrawalRequest();
                        break;
                    case 8:
                        changePasswordInterface();
                        break;
                    case 9:
                        displayProfileInterface();
                        break;
                    case 0:
                        userInterface.displayLoginMenu();
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
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
        if (visibleProjects.isEmpty()) {
            return null;
        }
        
        int projectChoice = getValidIntegerInput("Enter the project number to view details (or 0 to go back): ", 0, visibleProjects.size());
        
        if (projectChoice == 0) {
            // Return to previous menu
            displayApplicantMenu(currentApplicant);
            return null;
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
    }
    
    private void applicationConfirmation(Project selectedProject) {
        if (selectedProject == null) {
            return;
        }
        
        System.out.println("\nOptions:");
        System.out.println("1) Apply");
        System.out.println("0) Go Back");
        
        int choice = getValidIntegerInput("Enter your choice: ", 0, 1);

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
                    selectFlatType(application, project);
                } else if (application.getStatus() == ApplicationStatus.BOOKED) {
                    // View booking receipt
                    viewBookingReceipt(currentApplicant);
                }
            } else {
                // Return to main menu
                displayApplicantMenu(currentApplicant);
            }
        }
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
        displayApplicantMenu(currentApplicant);
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
        displayApplicantMenu(applicant);
    }
    
    private void submitEnquiry() {
        System.out.println("\n======== SUBMIT ENQUIRY ========");
        
        // Ask if enquiry is about a specific project
        System.out.println("Is this enquiry about a specific project?");
        System.out.println("1. Yes");
        System.out.println("2. No (General Enquiry)");
        
        System.out.print("\nEnter your choice: ");
        int projectChoice = getValidIntegerInput("Enter your choice: ", 1, 2);
        
        Project selectedProject = null;
        
        if (projectChoice == 1) {
            // Get visible projects
            List<Project> visibleProjects = projectController.getVisibleProjectsForApplicant(currentApplicant);
            
            if (visibleProjects.isEmpty()) {
                System.out.println("No projects available. Submitting as a general enquiry.");
            } else {
                System.out.println("\nSelect Project:");
                for (int i = 0; i < visibleProjects.size(); i++) {
                    System.out.println((i+1) + ". " + visibleProjects.get(i).getProjectName());
                }
                
                System.out.print("\nEnter project number (or 0 for general enquiry): ");
                int projectNum = getValidIntegerInput("Enter your choice: ", 0, visibleProjects.size() );
                
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
            Enquiry newEnquiry = enquiryController.createEnquiry(currentApplicant, selectedProject, enquiryContent);
            
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
    }
    
    private void viewEnquiries() {
        System.out.println("\n======== MY ENQUIRIES ========");
        
        // Get the applicant's enquiries
        List<Enquiry> myEnquiries = enquiryController.getEnquiriesByApplicant(currentApplicant);
        
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
                String status = enquiry.isResponded() ? "Responded" : "Pending";
                
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
                        int viewId = Integer.parseInt(scanner.nextLine());
                        
                        if (viewId > 0 && viewId <= myEnquiries.size()) {
                            viewEnquiryDetails(myEnquiries.get(viewId - 1));
                        } else {
                            System.out.println("Invalid enquiry ID.");
                        }
                        viewEnquiries(); // Return to list
                        break;
                        
                    case 2:
                        System.out.print("Enter enquiry ID to edit: ");
                        int editId = Integer.parseInt(scanner.nextLine());
                        
                        if (editId > 0 && editId <= myEnquiries.size()) {
                            editEnquiry(myEnquiries.get(editId - 1));
                        } else {
                            System.out.println("Invalid enquiry ID.");
                            viewEnquiries();
                        }
                        break;
                        
                    case 3:
                        System.out.print("Enter enquiry ID to delete: ");
                        int deleteId = Integer.parseInt(scanner.nextLine());
                        
                        if (deleteId > 0 && deleteId <= myEnquiries.size()) {
                            deleteEnquiry(myEnquiries.get(deleteId - 1));
                        } else {
                            System.out.println("Invalid enquiry ID.");
                            viewEnquiries();
                        }
                        break;
                        
                    case 0:
                        // Return to menu happens after this function
                        break;
                        
                    default:
                        System.out.println("Invalid choice.");
                        viewEnquiries();
                        break;
                }
        }
        
        // Wait for user input before returning to menu
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }
    
    private void viewEnquiryDetails(Enquiry enquiry) {
        System.out.println("\n======== ENQUIRY DETAILS ========");
        
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
    
    private void editEnquiry(Enquiry enquiry) {
        System.out.println("\n======== EDIT ENQUIRY ========");
        
        // Check if enquiry already has a response
        if (enquiry.isResponded()) {
            System.out.println("You cannot edit an enquiry that has already been responded to.");
            
            // Wait for user input before returning
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            
            // Return to view enquiries
            viewEnquiries();
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
        
        // Return to view enquiries
        viewEnquiries();
    }
    
    private void deleteEnquiry(Enquiry enquiry) {
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
        
        // Return to view enquiries
        viewEnquiries();
    }
    private void editEnquiryFromMenu() {
        System.out.println("\n======== EDIT ENQUIRY ========");
        
        // Get the applicant's enquiries
        List<Enquiry> myEnquiries = enquiryController.getEnquiriesByApplicant(currentApplicant);
        
        if (myEnquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries.");
        } else {
            // Display the list of enquiries
            System.out.println("Your Enquiries:");
            System.out.println("ID\tProject\t\tSubmission Date\t\tStatus\t\tEnquiry Content");
            System.out.println("------------------------------------------------------------------------------------------");
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            // Only show enquiries that can be edited (haven't been responded to)
            List<Enquiry> editableEnquiries = new ArrayList<>();
            
            for (Enquiry enquiry : myEnquiries) {
                if (enquiry.getResponse() == null) {
                    editableEnquiries.add(enquiry);
                    
                    String projectName = enquiry.getProject() != null ? enquiry.getProject().getProjectName() : "General";
                    
                    System.out.printf("%-3d %-20s %-12s %-12s %s%n", 
                        editableEnquiries.size(), 
                        projectName,
                        dateFormat.format(enquiry.getSubmissionDate()),
                        "Pending",
                        enquiry.getEnquiryContent().length() > 30 ? 
                            enquiry.getEnquiryContent().substring(0, 27) + "..." : 
                            enquiry.getEnquiryContent()
                    );
                }
            }
            
            if (editableEnquiries.isEmpty()) {
                System.out.println("You don't have any enquiries that can be edited. Only pending enquiries can be edited.");
            } else {
                System.out.print("\nEnter enquiry ID to edit (or 0 to cancel): ");
                try {
                	int editId = getValidIntegerInput("\nEnter your choice: ", 0, editableEnquiries.size());
                    
                    if (editId > 0 && editId <= editableEnquiries.size()) {
                        editEnquiry(editableEnquiries.get(editId - 1));
                    } else if (editId != 0) {
                        System.out.println("Invalid enquiry ID.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
        }
        
        // Wait for user input before returning to menu
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }
    
    private void deleteEnquiryFromMenu() {
        System.out.println("\n======== DELETE ENQUIRY ========");
        
        // Get the applicant's enquiries
        List<Enquiry> myEnquiries = enquiryController.getEnquiriesByApplicant(currentApplicant);
        
        if (myEnquiries.isEmpty()) {
            System.out.println("You have not submitted any enquiries.");
        } else {
            // Display the list of enquiries
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
            
            System.out.print("\nEnter enquiry ID to delete (or 0 to cancel): ");
            	int deleteId = getValidIntegerInput("Enter enquiry ID to delete: ", 0, myEnquiries.size());
                
                if (deleteId > 0 && deleteId <= myEnquiries.size()) {
                    deleteEnquiry(myEnquiries.get(deleteId - 1));
                } else if (deleteId != 0) {
                    System.out.println("Invalid enquiry ID.");
                }

        }
        
        // Wait for user input before returning to menu
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }
    
    private void withdrawalRequest() {
        System.out.println("\n=== WITHDRAWAL REQUEST ===");
        
        // Get the current applicant's application
        ProjectApplication application = applicationController.getApplicationByApplicantNRIC(currentApplicant.getNric());
        
        // Check if the applicant has an application
        if (!currentApplicant.requestWithdrawal() || application == null) {
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
                Withdrawal withdrawal = withdrawalController.submitWithdrawal(currentApplicant, application);
                
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