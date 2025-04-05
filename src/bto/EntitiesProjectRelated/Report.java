package bto.EntitiesProjectRelated;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Report {
    private String reportType;
    private List<FlatBooking> bookings;
    private FilterCriteria filters;
    private Date generationDate;
    
    // Constructors
    public Report() {
        this.bookings = new ArrayList<>();
        this.generationDate = new Date();
    }
    
    public Report(String reportType, FilterCriteria filters) {
        this();
        this.reportType = reportType;
        this.filters = filters;
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
    
    public Date getGenerationDate() {
        return generationDate;
    }
    
    public void setGenerationDate(Date generationDate) {
        this.generationDate = generationDate;
    }
    
    // Helper method to add bookings
    public void addBooking(FlatBooking booking) {
        if (!bookings.contains(booking)) {
            bookings.add(booking);
        }
    }
    
    @Override
    public String toString() {
        return "Report [type=" + reportType + ", filters=" + filters + ", bookings=" + bookings.size() + "]";
    }
}