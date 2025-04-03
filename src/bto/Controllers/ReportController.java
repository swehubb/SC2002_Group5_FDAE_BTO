package bto.Controllers;

import bto.Entities.HDBManager;
import bto.EntitiesProjectRelated.*;
import bto.Enums.FlatType;
import bto.Enums.MaritalStatus;

import java.util.Scanner;

public class ReportController {
    private HDBManager manager;
    private Scanner scanner;
    
    // Constructor
    public ReportController() {
    }
    
    // Constructor with dependencies
    public ReportController(HDBManager manager, Scanner scanner) {
        this.manager = manager;
        this.scanner = scanner;
    }
    
    // Methods
    public Report generateReport(String reportType, FilterCriteria criteria) {
        // Create a new report
        Report report = new Report(reportType, criteria);
        
        // Apply filters
        applyFilters(report, criteria);
        
        return report;
    }
    
    public boolean applyFilters(Report report, FilterCriteria criteria) {
        if (report == null) {
            return false;
        }
        
        // Apply filters to report
        report.setFilters(criteria);
        
        return true;
    }
    
    public boolean exportReport(Report report, String filename) {
        if (report == null) {
            return false;
        }
        
        // Export report to file
        return report.exportToFile(filename);
    }
    
    // Display menu for report generation
    public void displayReportMenu() {
        if (manager == null || scanner == null) {
            System.out.println("Error: HDBManager or Scanner not initialized.");
            return;
        }
        
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n===== REPORT GENERATION MENU =====");
            System.out.println("1. Generate Full Report (All Bookings)");
            System.out.println("2. Filter by Marital Status");
            System.out.println("3. Filter by Flat Type");
            System.out.println("4. Filter by Project Name");
            System.out.println("5. Custom Report (Multiple Filters)");
            System.out.println("6. Export Report to File");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        displayFullReport();
                        break;
                    case 2:
                        displayReportByMaritalStatus();
                        break;
                    case 3:
                        displayReportByFlatType();
                        break;
                    case 4:
                        displayReportByProject();
                        break;
                    case 5:
                        displayCustomReport();
                        break;
                    case 6:
                        exportReportToFile();
                        break;
                    case 0:
                        exit = true;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private void displayFullReport() {
        Report report = generateFullReport();
        System.out.println(report.getFormattedReport());
    }
    
    private Report generateFullReport() {
        Report report = generateReport("Full BTO Flat Booking Report", null);
        // Collect all bookings from all managed projects
        collectAllBookings(report);
        return report;
    }
    
    private void collectAllBookings(Report report) {
        for (Project project : manager.getManagedProjects()) {
            // Get applications with BOOKED status
            for (ProjectApplication application : project.getApplications()) {
                if (application.getStatus() == bto.Enums.ApplicationStatus.BOOKED) {
                    // Create a booking from the application data
                    FlatBooking booking = new FlatBooking(
                        application.getApplicant(),
                        project,
                        application.getSelectedFlatType(),
                        0  // Default or placeholder value for flat ID
                    );
                    report.addBooking(booking);
                }
            }
        }
    }
    
    private void displayReportByMaritalStatus() {
        System.out.println("\n--- Filter by Marital Status ---");
        System.out.println("1. SINGLE");
        System.out.println("2. MARRIED");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            MaritalStatus status;
            if (choice == 1) {
                status = MaritalStatus.SINGLE;
            } else if (choice == 2) {
                status = MaritalStatus.MARRIED;
            } else {
                System.out.println("Invalid choice. Returning to previous menu.");
                return;
            }
            
            // Create filter criteria and add marital status criterion
            FilterCriteria criteria = new FilterCriteria();
            criteria.addCriterion("maritalStatus", status);
            
            Report report = generateReport("BTO Flat Booking Report - By Marital Status: " + status, criteria);
            collectAllBookings(report);
            
            System.out.println(report.getFormattedReport());
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private void displayReportByFlatType() {
        System.out.println("\n--- Filter by Flat Type ---");
        System.out.println("1. TWO_ROOM");
        System.out.println("2. THREE_ROOM");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            FlatType type;
            if (choice == 1) {
                type = FlatType.TWO_ROOM;
            } else if (choice == 2) {
                type = FlatType.THREE_ROOM;
            } else {
                System.out.println("Invalid choice. Returning to previous menu.");
                return;
            }
            
            // Create filter criteria and add flat type criterion
            FilterCriteria criteria = new FilterCriteria();
            criteria.addCriterion("flatType", type);
            
            Report report = generateReport("BTO Flat Booking Report - By Flat Type: " + type, criteria);
            collectAllBookings(report);
            
            System.out.println(report.getFormattedReport());
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private void displayReportByProject() {
        System.out.println("\n--- Filter by Project Name ---");
        System.out.println("Available Projects:");
        
        int index = 1;
        for (Project project : manager.getManagedProjects()) {
            System.out.println(index + ". " + project.getProjectName());
            index++;
        }
        
        if (index == 1) {
            System.out.println("No projects available.");
            return;
        }
        
        System.out.print("Enter project number (or 0 to enter project name manually): ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            String projectName;
            if (choice == 0) {
                System.out.print("Enter project name: ");
                projectName = scanner.nextLine().trim();
            } else if (choice > 0 && choice < index) {
                projectName = manager.getManagedProjects().get(choice - 1).getProjectName();
            } else {
                System.out.println("Invalid choice. Returning to previous menu.");
                return;
            }
            
            // Create filter criteria and add project name criterion
            FilterCriteria criteria = new FilterCriteria();
            criteria.addCriterion("projectName", projectName);
            
            Report report = generateReport("BTO Flat Booking Report - By Project: " + projectName, criteria);
            collectAllBookings(report);
            
            System.out.println(report.getFormattedReport());
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    private void displayCustomReport() {
        FilterCriteria criteria = new FilterCriteria();
        
        // Marital Status
        System.out.println("\n--- Custom Report (Multiple Filters) ---");
        System.out.println("Marital Status:");
        System.out.println("1. SINGLE");
        System.out.println("2. MARRIED");
        System.out.println("3. Any (No filter)");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 1) {
                criteria.addCriterion("maritalStatus", MaritalStatus.SINGLE);
            } else if (choice == 2) {
                criteria.addCriterion("maritalStatus", MaritalStatus.MARRIED);
            } else if (choice != 3) {
                System.out.println("Invalid choice. No filter will be applied for marital status.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. No filter will be applied for marital status.");
        }
        
        // Flat Type
        System.out.println("\nFlat Type:");
        System.out.println("1. TWO_ROOM");
        System.out.println("2. THREE_ROOM");
        System.out.println("3. Any (No filter)");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 1) {
                criteria.addCriterion("flatType", FlatType.TWO_ROOM);
            } else if (choice == 2) {
                criteria.addCriterion("flatType", FlatType.THREE_ROOM);
            } else if (choice != 3) {
                System.out.println("Invalid choice. No filter will be applied for flat type.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. No filter will be applied for flat type.");
        }
        
        // Project Name
        System.out.println("\nProject Filter:");
        System.out.println("1. Filter by project");
        System.out.println("2. Any project (No filter)");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 1) {
                System.out.println("\nAvailable Projects:");
                
                int index = 1;
                for (Project project : manager.getManagedProjects()) {
                    System.out.println(index + ". " + project.getProjectName());
                    index++;
                }
                
                if (index == 1) {
                    System.out.println("No projects available.");
                } else {
                    System.out.print("Enter project number (or 0 to enter project name manually): ");
                    
                    int projectChoice = Integer.parseInt(scanner.nextLine().trim());
                    
                    if (projectChoice == 0) {
                        System.out.print("Enter project name: ");
                        criteria.addCriterion("projectName", scanner.nextLine().trim());
                    } else if (projectChoice > 0 && projectChoice < index) {
                        criteria.addCriterion("projectName", manager.getManagedProjects().get(projectChoice - 1).getProjectName());
                    } else {
                        System.out.println("Invalid choice. No filter will be applied for project.");
                    }
                }
            } else if (choice != 2) {
                System.out.println("Invalid choice. No filter will be applied for project.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. No filter will be applied for project.");
        }
        
        // Generate and display report
        Report report = generateReport("Custom BTO Flat Booking Report", criteria);
        collectAllBookings(report);
        
        System.out.println(report.getFormattedReport());
    }
    
    private void exportReportToFile() {
        System.out.println("\n--- Export Report to File ---");
        
        // First, generate the report
        System.out.println("Generate report to export:");
        System.out.println("1. Full Report (All Bookings)");
        System.out.println("2. Custom Report (With Filters)");
        System.out.print("Enter your choice: ");
        
        Report report = null;
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 1) {
                report = generateFullReport();
            } else if (choice == 2) {
                // Get filters for custom report
                FilterCriteria criteria = getCustomFilters();
                report = generateReport("Custom BTO Flat Booking Report", criteria);
                collectAllBookings(report);
            } else {
                System.out.println("Invalid choice. Generating full report.");
                report = generateFullReport();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Generating full report.");
            report = generateFullReport();
        }
        
        // Ask for file name
        System.out.print("Enter filename to save report (e.g., report.txt): ");
        String fileName = scanner.nextLine().trim();
        
        if (fileName.isEmpty()) {
            fileName = "bto_report_" + System.currentTimeMillis() + ".txt";
            System.out.println("Using default filename: " + fileName);
        }
        
        // Export report
        boolean success = exportReport(report, fileName);
        
        if (success) {
            System.out.println("Report successfully exported to " + fileName);
        } else {
            System.out.println("Failed to export report. Please try again.");
        }
    }
    
    private FilterCriteria getCustomFilters() {
        FilterCriteria criteria = new FilterCriteria();
        
        // Marital Status
        System.out.println("\nMarital Status:");
        System.out.println("1. SINGLE");
        System.out.println("2. MARRIED");
        System.out.println("3. Any (No filter)");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 1) {
                criteria.addCriterion("maritalStatus", MaritalStatus.SINGLE);
            } else if (choice == 2) {
                criteria.addCriterion("maritalStatus", MaritalStatus.MARRIED);
            }
            // choice 3 keeps maritalStatus criterion unset
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. No filter will be applied for marital status.");
        }
        
        // Flat Type
        System.out.println("\nFlat Type:");
        System.out.println("1. TWO_ROOM");
        System.out.println("2. THREE_ROOM");
        System.out.println("3. Any (No filter)");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 1) {
                criteria.addCriterion("flatType", FlatType.TWO_ROOM);
            } else if (choice == 2) {
                criteria.addCriterion("flatType", FlatType.THREE_ROOM);
            }
            // choice 3 keeps flatType criterion unset
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. No filter will be applied for flat type.");
        }
        
        // Project Name
        System.out.println("\nProject Filter:");
        System.out.println("1. Filter by project");
        System.out.println("2. Any project (No filter)");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            
            if (choice == 1) {
                System.out.println("\nAvailable Projects:");
                
                int index = 1;
                for (Project project : manager.getManagedProjects()) {
                    System.out.println(index + ". " + project.getProjectName());
                    index++;
                }
                
                System.out.print("Enter project number (or 0 to enter project name manually): ");
                
                int projectChoice = Integer.parseInt(scanner.nextLine().trim());
                
                if (projectChoice == 0) {
                    System.out.print("Enter project name: ");
                    criteria.addCriterion("projectName", scanner.nextLine().trim());
                } else if (projectChoice > 0 && projectChoice <= manager.getManagedProjects().size()) {
                    criteria.addCriterion("projectName", manager.getManagedProjects().get(projectChoice - 1).getProjectName());
                }
            }
            // choice 2 keeps projectName criterion unset
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. No filter will be applied for project.");
        }
        
        return criteria;
    }
}