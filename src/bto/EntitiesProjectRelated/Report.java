package bto.EntitiesProjectRelated;

import bto.Entities.Applicant;
import bto.Enums.FlatType;
import bto.Enums.MaritalStatus;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Report {
   private String reportType;
   private List<FlatBooking> bookings;
   private FilterCriteria filters;
   private String title;
   private String content;

   public Report() {
      this.bookings = new ArrayList();
   }

   public Report(String reportType, FilterCriteria filters) {
      this();
      this.reportType = reportType;
      this.filters = filters;
   }

   public String getFormattedReport() {
      // Apply filters to bookings if filters are set
      List<FlatBooking> filteredBookings = applyFiltersToBookings();
      
      // Generate the formatted report content
      generateReportContent(filteredBookings);
      
      return content;
   }

   private List<FlatBooking> applyFiltersToBookings() {
    if (filters == null || filters.getCriteria().isEmpty()) {
        return new ArrayList<>(bookings);
    }
    
    List<FlatBooking> filteredList = new ArrayList<>(bookings);
    Map<String, Object> appliedCriteria = filters.getCriteria();
    
    // Apply marital status filter
    if (appliedCriteria.containsKey("maritalStatus")) {
        MaritalStatus status = (MaritalStatus) appliedCriteria.get("maritalStatus");
        filteredList = filteredList.stream()
              .filter(booking -> booking.getApplicant().getMaritalStatus() == status)
              .collect(Collectors.toList());
    }
    
    // Apply flat type filter
    if (appliedCriteria.containsKey("flatType")) {
        FlatType type = (FlatType) appliedCriteria.get("flatType");
        filteredList = filteredList.stream()
              .filter(booking -> booking.getFlatType() == type)
              .collect(Collectors.toList());
    }
    
    // Apply project name filter
    if (appliedCriteria.containsKey("projectName")) {
        String projectName = (String) appliedCriteria.get("projectName");
        filteredList = filteredList.stream()
              .filter(booking -> booking.getProject().getProjectName().equalsIgnoreCase(projectName))
              .collect(Collectors.toList());
    }
    
    return filteredList;
}

private void generateReportContent(List<FlatBooking> filteredBookings) {
   StringBuilder sb = new StringBuilder();
   
   // Report header
   sb.append("============= BTO FLAT BOOKING REPORT =============\n");
   sb.append("Report Type: ").append(reportType).append("\n");
   sb.append("Date: ").append(new Date()).append("\n");
   sb.append("Filters Applied: ");
   
   // Add filter information...
   
   sb.append("\n\n");
   
   // Table header - simplified to only show requested fields
   String header = String.format("%-10s | %-15s | %-4s | %-15s", 
           "Flat Type", "Project Name", "Age", "Marital Status");
   String separator = "=".repeat(header.length());
   
   sb.append(separator).append("\n");
   sb.append(header).append("\n");
   sb.append(separator).append("\n");
   
   if (filteredBookings.isEmpty()) {
       sb.append("No bookings match the specified criteria.\n");
   } else {
       // Table rows - only include requested fields
       for (FlatBooking booking : filteredBookings) {
           Applicant applicant = booking.getApplicant();
           String row = String.format("%-10s | %-15s | %-4d | %-15s",
                   booking.getFlatType(),
                   booking.getProject().getProjectName(),
                   applicant.getAge(),
                   applicant.getMaritalStatus());
           sb.append(row).append("\n");
       }
   }
   
   sb.append(separator).append("\n");
   sb.append("Total bookings: ").append(filteredBookings.size()).append("\n");
   
   this.content = sb.toString();
}

   public boolean applyFilters(FilterCriteria newFilters) {
      this.filters = newFilters;
      return true;
   }

   public boolean exportToFile(String filename) {
      if (content == null) {
         // Generate content if it hasn't been generated yet
         getFormattedReport();
      }
      
      if (content == null || content.isEmpty()) {
         return false;
      }
      
      try {
         PrintWriter writer = new PrintWriter(filename);
         writer.println(content);
         writer.close();
         return true;
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }

   // Existing getters and setters
   public String getReportType() {
      return this.reportType;
   }

   public void setReportType(String reportType) {
      this.reportType = reportType;
   }

   public List<FlatBooking> getBookings() {
      return this.bookings;
   }

   public void setBookings(List<FlatBooking> bookings) {
      this.bookings = bookings;
   }

   public FilterCriteria getFilters() {
      return this.filters;
   }

   public void setFilters(FilterCriteria filters) {
      this.filters = filters;
   }

   public void addBooking(FlatBooking booking) {
      if (!this.bookings.contains(booking)) {
         this.bookings.add(booking);
      }
   }
   
   // New getters and setters for title and content
   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }
}