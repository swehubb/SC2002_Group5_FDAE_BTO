package bto.EntitiesProjectRelated;

import java.util.Date;
import bto.Entities.*;
import bto.Enums.*;

public class FlatBooking {
    private Applicant applicant;
    private Project project;
    private FlatType flatType;
    private int flatId;  // New field to store the specific flat ID
    private Date bookingDate;
    private String bookingStatus; // Added booking status field
    private String rejectionReason; // Added to store reason if rejected
    private HDBOfficer processedByOfficer;
    
    // Constants for booking status
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_PENDING = "PENDING";
    
    // Constructors
    public FlatBooking() {
        this.bookingDate = new Date(); // Sets current date as booking date
        this.bookingStatus = STATUS_PENDING; // Default status is pending
    }
    
    public FlatBooking(Applicant applicant, Project project, FlatType flatType, int flatId) {
        this();
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.flatId = flatId;
    }
    
    // Methods
    public String getBookingDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Booking Information\n");
        details.append("===================\n");
        details.append("Applicant: ").append(applicant.getName()).append(" (").append(applicant.getNric()).append(")\n");
        details.append("Project: ").append(project.getProjectName()).append("\n");
        details.append("Flat Type: ").append(flatType).append("\n");
        details.append("Flat ID: ").append(flatId).append("\n");
        details.append("Booking Date: ").append(bookingDate).append("\n");
        details.append("Status: ").append(bookingStatus).append("\n");
        
        if (STATUS_REJECTED.equals(bookingStatus) && rejectionReason != null) {
            details.append("Rejection Reason: ").append(rejectionReason).append("\n");
        }
        
        return details.toString();
    }
    
    public String generateReceipt() {
        return getBookingDetails();
    }
    
    /**
     * Approves this booking
     */
    public void approve() {
        this.bookingStatus = STATUS_APPROVED;
    }
    
    /**
     * Rejects this booking with a reason
     * @param reason The reason for rejection
     */
    public void reject(String reason) {
        this.bookingStatus = STATUS_REJECTED;
        this.rejectionReason = reason;
    }
    
    /**
     * Checks if the booking is approved
     * @return true if the booking is approved, false otherwise
     */
    public boolean isApproved() {
        return STATUS_APPROVED.equals(bookingStatus);
    }
    
    /**
     * Checks if the booking is rejected
     * @return true if the booking is rejected, false otherwise
     */
    public boolean isRejected() {
        return STATUS_REJECTED.equals(bookingStatus);
    }
    
    /**
     * Checks if the booking is pending
     * @return true if the booking is pending, false otherwise
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(bookingStatus);
    }
    
    // Getters and Setters
    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }
    
    public int getFlatId() {
        return flatId;
    }
    
    public void setFlatId(int flatId) {
        this.flatId = flatId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public String getBookingStatus() {
        return bookingStatus;
    }
    
    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public HDBOfficer getProcessedByOfficer() {
        return processedByOfficer;
    }

    public void setProcessedByOfficer(HDBOfficer officer) {
        this.processedByOfficer = officer;
    }
}