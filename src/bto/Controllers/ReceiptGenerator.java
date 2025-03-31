package bto.Controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import bto.Enums.*;
import bto.EntitiesProjectRelated.*;
import bto.Entities.*;

/**
 * Controller class for generating receipts for flat bookings.
 * This class is responsible for formatting and generating receipts 
 * that include applicant details, project information, and flat booking data.
 */
public class ReceiptGenerator {
    /**
     * Default constructor
     */
    public ReceiptGenerator() {
    }

    /**
     * Generates a receipt for a flat booking.
     * The receipt includes applicant information, project details, and flat type.
     * 
     * @param booking The FlatBooking object containing all the necessary information
     * @return A formatted receipt as a String
     */
    public String generateReceipt(FlatBooking booking) {
        if (booking == null) {
            return null;
        }

        User user = booking.getApplicant();
        Project project = booking.getProject();
        FlatType flatType = booking.getFlatType();
        int flatId = booking.getFlatId();
        
        StringBuilder receipt = new StringBuilder();

        // Format current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(new Date());
        
        // Generate receipt ID for reference
        String receiptId = "REC-" + dateFormat.format(new Date()).replace("-", "") + "-" + 
                           String.format("%04d", (int)(Math.random() * 10000));

        // Header
        receipt.append("BOOKING RECEIPT\n");
        receipt.append("==============\n\n");
        receipt.append("Receipt ID: ").append(receiptId).append("\n");
        receipt.append("Date: ").append(currentDate).append("\n\n");

        // Applicant Information
        receipt.append("Applicant Information:\n");
        receipt.append("---------------------\n");
        receipt.append("Name: ").append(user.getName()).append("\n");
        receipt.append("NRIC: ").append(user.getNric()).append("\n");
        receipt.append("Age: ").append(user.getAge()).append("\n");
        receipt.append("Marital Status: ").append(user.getMaritalStatus()).append("\n\n");

        // Project Information
        receipt.append("Project Information:\n");
        receipt.append("-------------------\n");
        receipt.append("Project Name: ").append(project.getProjectName()).append("\n");
        receipt.append("Neighborhood: ").append(project.getNeighborhood()).append("\n");
        receipt.append("Flat Type: ").append(flatType).append("\n");
        
        if (flatId > 0) {
            receipt.append("Flat ID: ").append(flatId).append("\n");
        }
        receipt.append("\n");

        // Confirmation and additional information
        receipt.append("This receipt confirms your booking of the above flat unit. ");
        receipt.append("Please retain this document for your records.\n\n");
        
        receipt.append("Important Information:\n");
        receipt.append("--------------------\n");
        receipt.append("1. Further instructions regarding payment will be sent to you separately.\n");
        receipt.append("2. For enquiries, please contact HDB at 1800-123-4567.\n");
        receipt.append("3. Please quote your NRIC and Receipt ID in all communications.\n\n");
        
        // Add processed by officer information if available
        try {
            HDBOfficer officer = booking.getProcessedByOfficer();
            if (officer != null) {
                receipt.append("Processed by Officer: ").append(officer.getName())
                       .append(" (").append(officer.getNric()).append(")\n\n");
            }
        } catch (Exception e) {
            // Officer information might not be available
        }
        
        receipt.append("Thank you for choosing HDB.");

        return receipt.toString();
    }
    
    /**
     * Generates a receipt for a ProjectApplication.
     * This is used when an applicant has applied but not yet booked a flat.
     * 
     * @param application The ProjectApplication to generate a receipt for
     * @return true if receipt generation was successful, false otherwise
     */
    public boolean generateReceipt(ProjectApplication application) {
        if (application == null || application.getApplicant() == null || 
            application.getProject() == null) {
            return false;
        }
        
        // If application has a selected flat type, use it; otherwise, this is just an application receipt
        FlatType flatType = application.getSelectedFlatType();
        
        // Create a temporary booking object for receipt generation
        FlatBooking tempBooking = new FlatBooking(
            application.getApplicant(),
            application.getProject(),
            flatType, // This could be null for initial applications
            0  // No flat ID yet
        );
        
        String receiptContent = generateReceipt(tempBooking);
        return receiptContent != null && !receiptContent.isEmpty();
    }
}
