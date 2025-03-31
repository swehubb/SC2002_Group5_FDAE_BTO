package bto.EntitiesProjectRelated;

import java.util.Date;
import bto.Entities.*;
import bto.Enums.*;

/**
 * Entity class representing a flat booking in the BTO system.
 * Contains information about the applicant, project, flat type and ID.
 */
public class FlatBooking {
    private User applicant;
    private Project project;
    private FlatType flatType;
    private int flatId;
    private Date bookingDate;
    private HDBOfficer processedByOfficer;
    
    /**
     * Default constructor
     */
    public FlatBooking() {
        this.bookingDate = new Date(); // Set booking date to current date
    }
    
    /**
     * Constructor with parameters
     * 
     * @param applicant The applicant who booked the flat
     * @param project The project the flat is part of
     * @param flatType The type of flat booked
     * @param flatId The ID of the flat booked
     */
    public FlatBooking(User applicant, Project project, FlatType flatType, int flatId) {
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.flatId = flatId;
        this.bookingDate = new Date(); // Set booking date to current date
    }
    
    // Getters and Setters
    
    /**
     * Gets the applicant who booked the flat
     * 
     * @return The applicant
     */
    public User getApplicant() {
        return applicant;
    }

    /**
     * Sets the applicant who booked the flat
     * 
     * @param applicant The applicant to set
     */
    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    /**
     * Gets the project the flat is part of
     * 
     * @return The project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets the project the flat is part of
     * 
     * @param project The project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Gets the flat type
     * 
     * @return The flat type
     */
    public FlatType getFlatType() {
        return flatType;
    }

    /**
     * Sets the flat type
     * 
     * @param flatType The flat type to set
     */
    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }

    /**
     * Gets the flat ID
     * 
     * @return The flat ID
     */
    public int getFlatId() {
        return flatId;
    }

    /**
     * Sets the flat ID
     * 
     * @param flatId The flat ID to set
     */
    public void setFlatId(int flatId) {
        this.flatId = flatId;
    }

    /**
     * Gets the booking date
     * 
     * @return The booking date
     */
    public Date getBookingDate() {
        return bookingDate;
    }

    /**
     * Sets the booking date
     * 
     * @param bookingDate The booking date to set
     */
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    /**
     * Gets the officer who processed the booking
     * 
     * @return The processing officer
     */
    public HDBOfficer getProcessedByOfficer() {
        return processedByOfficer;
    }

    /**
     * Sets the officer who processed the booking
     * 
     * @param processedByOfficer The processing officer to set
     */
    public void setProcessedByOfficer(HDBOfficer processedByOfficer) {
        this.processedByOfficer = processedByOfficer;
    }
    
    /**
     * String representation of the FlatBooking
     * 
     * @return A string representation of this booking
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FlatBooking [");
        sb.append("Applicant: ").append(applicant != null ? applicant.getName() : "null").append(", ");
        sb.append("Project: ").append(project != null ? project.getProjectName() : "null").append(", ");
        sb.append("FlatType: ").append(flatType).append(", ");
        sb.append("FlatID: ").append(flatId).append(", ");
        sb.append("BookingDate: ").append(bookingDate);
        sb.append("]");
        return sb.toString();
    }
}
