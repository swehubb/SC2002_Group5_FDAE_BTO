package bto.Boundaries;

import bto.Controllers.*;
import bto.Entities.*;
import bto.EntitiesProjectRelated.*;
import bto.Enums.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
                        // Request Withdrawal
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
    
    private void viewBookingReceipt(Applicant applicant) {
        System.out.println("\n======== BOOKING RECEIPT ========");
        
        // Get the applicant's booking
        FlatBooking booking = applicant.getBookedFlat();
        
        if (booking == null) {
            // Try to get the application and generate receipt from there
            ProjectApplication application = applicant.getAppliedProject();
            
            if (application != null && application.getStatus() == ApplicationStatus.BOOKED) {
                // Generate and display receipt based on application
                String receipt = receiptGenerator.formatReceipt(
                    applicant,
                    application.getProject(),
                    application.getSelectedFlatType()
                );
                
                System.out.println(receipt);
            } else {
                System.out.println("You do not have any confirmed flat bookings.");
            }
        } else {
            // Generate and display the receipt from booking
            String receipt = receiptGenerator.generateReceipt(booking);
            System.out.println(receipt);
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
        int projectChoice = Integer.parseInt(scanner.nextLine());
        
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
                int projectNum = Integer.parseInt(scanner.nextLine());
                
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
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
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
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                viewEnquiries();
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
                    int editId = Integer.parseInt(scanner.nextLine());
                    
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
            try {
                int deleteId = Integer.parseInt(scanner.nextLine());
                
                if (deleteId > 0 && deleteId <= myEnquiries.size()) {
                    deleteEnquiry(myEnquiries.get(deleteId - 1));
                } else if (deleteId != 0) {
                    System.out.println("Invalid enquiry ID.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        
        // Wait for user input before returning to menu
        System.out.println("\nPress Enter to return to the main menu...");
        scanner.nextLine();
    }
}