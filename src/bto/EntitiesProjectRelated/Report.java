package bto.EntitiesProjectRelated;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private String reportType;
    private List<FlatBooking> bookings;
    private FilterCriteria filters;
    
    // Constructors
    public Report() {
        this.bookings = new ArrayList<>();
    }
    
    public Report(String reportType, FilterCriteria filters) {
        this();
        this.reportType = reportType;
        this.filters = filters;
    }
    
    // Methods
    public String getFormattedReport() {
        StringBuilder report = new StringBuilder();
        report.append("Report Type: ").append(reportType).append("\n");
        report.append("Generated On: ").append(new Date()).append("\n\n");
        
        if (bookings.isEmpty()) {
            report.append("No bookings found matching the criteria.");
        } else {
            report.append("Booking Information:\n");
            report.append("===================\n");
            
            for (FlatBooking booking : bookings) {
                report.append(booking.getBookingDetails()).append("\n");
            }
            
            report.append("Total Bookings: ").append(bookings.size());
        }
        
        return report.toString();
    }
    
    public boolean applyFilters(FilterCriteria newFilters) {
        this.filters = newFilters;
        return true;
    }
    
    public boolean exportToFile(String filename) {
        // Implementation to export report to file
        return false; // Placeholder
    }
    
    // Getters and Setters
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public List<FlatBooking> getBookings() {
        return bookings;
    }

    public void setBookings(List<FlatBooking> bookings) {
        this.bookings = bookings;
    }

    public FilterCriteria getFilters() {
        return filters;
    }

    public void setFilters(FilterCriteria filters) {
        this.filters = filters;
    }
    
    // Helper methods
    public void addBooking(FlatBooking booking) {
        if (!bookings.contains(booking)) {
            bookings.add(booking);
        }
    }
}