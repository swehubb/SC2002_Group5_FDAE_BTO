package bto.Controllers;

import bto.EntitiesProjectRelated.*;
import bto.Entities.*;
import bto.Enums.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class ReportController {
    
    // Constructor
    public ReportController() {
        // No need to maintain a list of reports
    }
    
    // Methods
    public Report generateReport(String reportType, FilterCriteria criteria, List<Project> projects) {
        // Create a new report
        Report report = new Report(reportType, criteria);
        
        // Collect all relevant bookings from projects
        collectBookings(report, projects, criteria);
        
        return report;
    }
    
    private void collectBookings(Report report, List<Project> projects, FilterCriteria criteria) {
        // Use a set to track applicants who already have a booking added to the report
        Set<String> addedApplicantNrics = new HashSet<>();
        
        for (Project project : projects) {
            // Check if project filter is applied
            if (criteria.getCriteria().containsKey("project") && 
                !criteria.getCriteria().get("project").equals(project)) {
                continue;
            }
            
            // Get applications with BOOKED status
            for (ProjectApplication application : project.getApplications()) {
                if (application.getStatus() == ApplicationStatus.BOOKED) {
                    Applicant applicant = application.getApplicant();
                    
                    // Skip if this applicant already has a booking in the report
                    if (addedApplicantNrics.contains(applicant.getNric())) {
                        continue;
                    }
                    
                    // Apply marital status filter
                    if (criteria.getCriteria().containsKey("maritalStatus") && 
                        !criteria.getCriteria().get("maritalStatus").equals(applicant.getMaritalStatus())) {
                        continue;
                    }
                    
                    // Apply age filter
                    if (criteria.getCriteria().containsKey("minAge") && 
                        applicant.getAge() < (int)criteria.getCriteria().get("minAge")) {
                        continue;
                    }
                    if (criteria.getCriteria().containsKey("maxAge") && 
                        applicant.getAge() > (int)criteria.getCriteria().get("maxAge")) {
                        continue;
                    }
                    
                    // Apply flat type filter
                    if (criteria.getCriteria().containsKey("flatType") && 
                        !criteria.getCriteria().get("flatType").equals(application.getSelectedFlatType())) {
                        continue;
                    }
                    
                    // Check if applicant already has a booking (from database)
                    FlatBooking existingBooking = applicant.getBookedFlat();
                    
                    if (existingBooking != null) {
                        // Use existing booking
                        report.addBooking(existingBooking);
                    } else {
                        // Create a new temporary booking from the application data
                        FlatBooking booking = new FlatBooking(
                            applicant,
                            project,
                            application.getSelectedFlatType(),
                            0  // Default value for flat ID
                        );
                        report.addBooking(booking);
                    }
                    
                    // Track that this applicant is added
                    addedApplicantNrics.add(applicant.getNric());
                }
            }
        }
    }
    
    public String getFormattedReport(Report report) {
        if (report == null) {
            return "Invalid report.";
        }
        
        StringBuilder formattedReport = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        // Report header
        formattedReport.append("=== " + report.getReportType() + " ===\n");
        formattedReport.append("Generated On: " + dateFormat.format(report.getGenerationDate()) + "\n\n");
        
        // Filter criteria applied
        formattedReport.append("Filter Criteria:\n");
        if (report.getFilters() != null && !report.getFilters().getCriteria().isEmpty()) {
            for (Map.Entry<String, Object> entry : report.getFilters().getCriteria().entrySet()) {
                formattedReport.append("- " + entry.getKey() + ": ");
                
                if (entry.getValue() instanceof Project) {
                    formattedReport.append(((Project) entry.getValue()).getProjectName());
                } else if (entry.getValue() instanceof MaritalStatus) {
                    formattedReport.append(entry.getValue().toString());
                } else if (entry.getValue() instanceof FlatType) {
                    formattedReport.append(entry.getValue().toString());
                } else {
                    formattedReport.append(entry.getValue().toString());
                }
                
                formattedReport.append("\n");
            }
        } else {
            formattedReport.append("- No filters applied (all records)\n");
        }
        formattedReport.append("\n");
        
        // Booking information
        List<FlatBooking> bookings = report.getBookings();
        if (bookings.isEmpty()) {
            formattedReport.append("No bookings found matching the criteria.");
        } else {
            formattedReport.append("Booking Information:\n");
            formattedReport.append("===================\n");
            
            formattedReport.append(String.format("%-15s %-25s %-15s %-15s %-15s\n", 
                "Applicant", "Project", "Flat Type", "Status", "Booking Date"));
            formattedReport.append("------------------------------------------------------------------------------\n");
            
            for (FlatBooking booking : bookings) {
                formattedReport.append(String.format("%-15s %-25s %-15s %-15s %-15s\n", 
                    booking.getApplicant().getName(),
                    booking.getProject().getProjectName(),
                    booking.getFlatType().toString(),
                    booking.getBookingStatus(),
                    dateFormat.format(booking.getBookingDate())
                ));
            }
            
            formattedReport.append("\nTotal Bookings: " + bookings.size());
        }
        
        return formattedReport.toString();
    }
    
    public boolean exportReport(Report report, String filename) {
        if (report == null) {
            return false;
        }
        
        // In a real implementation, this would write the report to a file
        System.out.println("Exporting report to " + filename);
        return true;
    }
    
    // Simple method stub to satisfy the requirements
    public List<Report> getAllReports() {
        // Just return an empty list since we're not tracking reports
        return new ArrayList<>();
    }
}