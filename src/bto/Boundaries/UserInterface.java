package bto.Boundaries;

import java.util.Scanner;
import bto.Controllers.*;
import bto.Entities.*;
import bto.Enums.*;

public class UserInterface {
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
    
    
    // Boundaries (interfaces)
    private ApplicantInterface applicantInterface;
    private OfficerInterface officerInterface;
    private ManagerInterface managerInterface;

    // Constructor
    public UserInterface(AuthController authController, ProjectController projectController, 
            ApplicationController applicationController, EnquiryController enquiryController) {
        scanner = new Scanner(System.in);
        
        // Use the provided controllers
        this.authController = authController;
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiryController = enquiryController;
        
        // Initialize other controllers that aren't passed in
        this.reportController = new ReportController();
        this.registrationController = new RegistrationController();
        this.withdrawalController = new WithdrawalController();
        this.bookingController = new BookingController();
        this.receiptGenerator = new ReceiptGenerator();
        
        // Pass controllers to boundary classes (interfaces)
        applicantInterface = new ApplicantInterface(scanner, this.authController, this.projectController, 
            this.applicationController, this.enquiryController, 
            this.reportController, this.registrationController, 
            this.withdrawalController, this.bookingController, 
            this.receiptGenerator, this);
        officerInterface = new OfficerInterface(scanner, this.authController, this.projectController, 
            this.applicationController, this.enquiryController, 
            this.reportController, this.registrationController, 
            this.withdrawalController, this.bookingController, 
            this.receiptGenerator, this);
        managerInterface = new ManagerInterface(scanner, this.authController, this.projectController, 
            this.applicationController, this.enquiryController, 
            this.reportController, this.registrationController, 
            this.withdrawalController, this.bookingController, 
            this.receiptGenerator, this);
    }
    
    private User signUp() {
        String newnric = "";
        String newpassword = "";
        int newage = 0;
        MaritalStatus maritalStatus = null;
        String newname = "";
        
        while (true) {
            System.out.print("Enter NRIC: ");
            newnric = scanner.nextLine();
            if (authController.validateNRIC(newnric)) {
                break;  // If valid, break out of the loop
            } else {
                System.out.println("Invalid NRIC format. Please try again.");
            }
        }
        
        while (true) {
            System.out.print("Enter Password: ");
            newpassword = scanner.nextLine();
            if (authController.validatePassword(newpassword)) {
                break;  // If valid, break out of the loop
            } else {
                System.out.println("Password must be at least 8 characters. Please try again.");
            }
        }

        while (true) {
            try {
                System.out.print("Enter Age: ");
                String ageInput = scanner.nextLine().trim();
                
                if (ageInput.isEmpty()) {
                    System.out.println("Age cannot be empty. Please enter a valid age.");
                    continue;
                }
                
                newage = Integer.parseInt(ageInput);
                break;  // If parsing succeeds, break out of the loop
            } catch (NumberFormatException e) {
                System.out.println("Invalid age format. Please enter a number.");
            }
        }
        
        while (true) {
            try {
                System.out.println("Enter Marital Status:");
                System.out.println("1) Single");
                System.out.println("2) Married");
                System.out.print("Your choice: ");
                
                String maritalInput = scanner.nextLine().trim();
                
                if (maritalInput.isEmpty()) {
                    System.out.println("Please select a valid option.");
                    continue;
                }
                
                int newmarital = Integer.parseInt(maritalInput);
                if (newmarital == 1) {
                    maritalStatus = MaritalStatus.SINGLE;
                    break;
                } else if (newmarital == 2) {
                    maritalStatus = MaritalStatus.MARRIED;
                    break;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number (1 or 2).");
            }
        }
        
        while (true) {
            System.out.print("Enter Name: ");
            newname = scanner.nextLine();
            if (authController.validateName(newname)) {
                break;  // If valid, break out of the loop
            } else {
                System.out.println("Name requirements: non-empty and contains only letters, spaces, and hyphens. Please try again.");
            }
        }
        
        // Create a temporary User object with the collected information
        return new User(newnric, newpassword, newage, maritalStatus, newname);
    }
    
   
    public void displayLoginMenu() {

                System.out.println("=== BTO MANAGEMENT SYSTEM ===");
                System.out.println("1. Login");
                System.out.println("2. Sign Up");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");
                
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    System.out.println("Please enter a valid option.");
                    displayLoginMenu();
                }
                
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 1:
                        System.out.print("Enter NRIC: ");
                        String nric = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        String password = scanner.nextLine();
                        
                        User user = authController.loginUser(nric, password);
                        if (user != null) {
                            displayUserMenu(user);
                        } else {
                            System.out.println("Invalid credentials. Please try again.");
                            // Continue in the login menu
                        }
                        break;
                    case 2:
                        while (true) {
                            try {
                                System.out.println("Pick a Role:");
                                System.out.println("1) Applicant");
                                System.out.println("2) HDB Officer");
                                System.out.println("3) HDB Manager");
                                System.out.println("4) Go Back");
                                System.out.print("Enter your choice: ");
                                
                                String roleInput = scanner.nextLine().trim();
                                
                                if (roleInput.isEmpty()) {
                                    System.out.println("Please enter a valid option.");
                                    continue;
                                }
                                
                                int rolechoice = Integer.parseInt(roleInput);

                                switch(rolechoice) {
                                    case 1:
                                        User applicantData = signUp();
                                        Applicant applicant = new Applicant(
                                            applicantData.getNric(),
                                            applicantData.getPassword(),
                                            applicantData.getAge(),
                                            applicantData.getMaritalStatus(),
                                            applicantData.getName()
                                        );
                                        System.out.println("Applicant registered successfully!");
                                        if (authController.addUser(applicant)) {
                                            User newuser = authController.loginUser(applicantData.getNric(), applicantData.getPassword());
                                            if (newuser != null) {
                                                displayUserMenu(newuser);
                                            } else {
                                                System.out.println("Invalid credentials. Please try again.");
                                                // Continue in the login menu
                                            }
                                        }
                                        break;
                                        
                                    case 2:
                                        User officerData = signUp();
                                        HDBOfficer officer = new HDBOfficer(
                                            officerData.getNric(),
                                            officerData.getPassword(),
                                            officerData.getAge(),
                                            officerData.getMaritalStatus(),
                                            officerData.getName()
                                        );
                                        System.out.println("HDB Officer registered successfully!");
                                        if (authController.addUser(officer)) {
                                            User newuser = authController.loginUser(officerData.getNric(), officerData.getPassword());
                                            if (newuser != null) {
                                                displayUserMenu(newuser);
                                            } else {
                                                System.out.println("Invalid credentials. Please try again.");
                                                // Continue in the login menu
                                            }
                                        }
                                        break;

                                    case 3:
                                        User managerData = signUp();
                                        HDBManager manager = new HDBManager(
                                            managerData.getNric(),
                                            managerData.getPassword(),
                                            managerData.getAge(),
                                            managerData.getMaritalStatus(),
                                            managerData.getName()
                                        );
                                        System.out.println("HDB Manager registered successfully!");
                                        if (authController.addUser(manager)) {
                                            User newuser = authController.loginUser(managerData.getNric(), managerData.getPassword());
                                            if (newuser != null) {
                                                displayUserMenu(newuser);
                                            } else {
                                                System.out.println("Invalid credentials. Please try again.");
                                                // Continue in the login menu
                                            }
                                        }
                                        break;

                                    case 4:
                                        System.out.println("Back to Log in Menu.");
                                        break;

                                    default:
                                        System.out.println("Invalid choice. Please try again.");
                                        continue; // This will continue the loop and prompt the user again.
                                }
                                break; // Exit the role selection loop once a valid option is selected
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Please enter a number.");
                            } catch (Exception e) {
                                System.out.println("An error occurred: " + e.getMessage());
                                System.out.println("Please try again.");
                            }
                        }
                        break;
                    case 0:
                        System.out.println("Thank you for using BTO Management System.");
                        System.exit(0);
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        displayLoginMenu();
                        break;
                }
            }
       

    public void displayUserMenu(User user) {
        if (user instanceof Applicant) {
            applicantInterface.displayApplicantMenu((Applicant) user);
        } else if (user instanceof HDBOfficer) {
            officerInterface.displayOfficerMenu((HDBOfficer) user);
        } else if (user instanceof HDBManager) {
            managerInterface.displayManagerMenu((HDBManager) user);
        }
    }
}