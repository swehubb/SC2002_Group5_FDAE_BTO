package bto.Controllers;

import bto.Entities.*;
import bto.EntitiesProjectRelated.*;
import bto.Enums.*;
import java.util.Date;

public class ReceiptGenerator {
    // Constructor
    public ReceiptGenerator() {
        // Initialize if needed
    }
    
    /**
     * Generates a receipt for a flat booking.
     * 
     * @param booking The flat booking to generate a receipt for
     * @return The receipt as a formatted string
     */
    public String generateReceipt(FlatBooking booking) {
        if (booking == null) {
            return null;
        }
        
        StringBuilder receipt = new StringBuilder();
        receipt.append("======== BOOKING RECEIPT ========\n");
        receipt.append("Date: ").append(booking.getBookingDate()).append("\n\n");
        
        // Applicant Details
        User applicant = booking.getApplicant();
        receipt.append("APPLICANT DETAILS:\n");
        receipt.append("Name: ").append(applicant.getName()).append("\n");
        receipt.append("NRIC: ").append(applicant.getNric()).append("\n");
        receipt.append("Age: ").append(applicant.getAge()).append("\n");
        receipt.append("Marital Status: ").append(applicant.getMaritalStatus()).append("\n\n");
        
        // Project Details
        Project project = booking.getProject();
        receipt.append("PROJECT DETAILS:\n");
        receipt.append("Project Name: ").append(project.getProjectName()).append("\n");
        receipt.append("Location: ").append(project.getNeighborhood()).append("\n\n");
        
        // Flat Details
        receipt.append("FLAT DETAILS:\n");
        receipt.append("Flat Type: ").append(booking.getFlatType()).append("\n");
        receipt.append("Flat ID: ").append(booking.getFlatId()).append("\n\n");
        
        // Processing Officer Details
        HDBOfficer officer = booking.getProcessedByOfficer();
        if (officer != null) {
            receipt.append("PROCESSED BY:\n");
            receipt.append("Officer Name: ").append(officer.getName()).append("\n");
            receipt.append("Officer ID: ").append(officer.getNric()).append("\n\n");
        }
        
        receipt.append("Thank you for choosing HDB for your housing needs.\n");
        receipt.append("================================");
        
        return receipt.toString();
    }
}
