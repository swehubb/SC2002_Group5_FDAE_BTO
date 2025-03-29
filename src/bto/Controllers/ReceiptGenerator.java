package bto.Controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import bto.Enums.*;
import bto.EntitiesProjectRelated.*;
import bto.Entities.*;

public class ReceiptGenerator {
    // Constructor
    public ReceiptGenerator() {
    }

    // Methods
    public String generateReceipt(FlatBooking booking) {
        if (booking == null) {
            return null;
        }

        return formatReceipt(booking);
    }
    
    // Method that accepts a ProjectApplication
    public boolean generateReceipt(ProjectApplication application) {
        if (application == null || application.getApplicant() == null || 
            application.getProject() == null || application.getSelectedFlatType() == null) {
            return false;
        }
        
        // Create a temporary booking object for receipt generation
        FlatBooking tempBooking = new FlatBooking(
            application.getApplicant(),
            application.getProject(),
            application.getSelectedFlatType(),
            0  // We don't have a flat ID yet
        );
        
        String receiptContent = formatReceipt(tempBooking);
        return receiptContent != null && !receiptContent.isEmpty();
    }
    
    // Enhanced format receipt method that uses FlatBooking
    private String formatReceipt(FlatBooking booking) {
        User user = booking.getApplicant();
        Project project = booking.getProject();
        FlatType flatType = booking.getFlatType();
        int flatId = booking.getFlatId();
        Date bookingDate = booking.getBookingDate();
        
        StringBuilder receipt = new StringBuilder();

        // Format current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(new Date());
        String bookingDateStr = dateFormat.format(bookingDate);

        receipt.append("BOOKING RECEIPT\n");
        receipt.append("==============\n\n");
        receipt.append("Receipt Date: ").append(currentDate).append("\n");
        receipt.append("Booking Date: ").append(bookingDateStr).append("\n\n");

        receipt.append("Applicant Information:\n");
        receipt.append("---------------------\n");
        receipt.append("Name: ").append(user.getName()).append("\n");
        receipt.append("NRIC: ").append(user.getNric()).append("\n");
        receipt.append("Age: ").append(user.getAge()).append("\n");
        receipt.append("Marital Status: ").append(user.getMaritalStatus()).append("\n\n");

        receipt.append("Project Information:\n");
        receipt.append("-------------------\n");
        receipt.append("Project Name: ").append(project.getProjectName()).append("\n");
        receipt.append("Neighborhood: ").append(project.getNeighborhood()).append("\n");
        receipt.append("Flat Type: ").append(flatType).append("\n");
        
        if (flatId > 0) {
            receipt.append("Flat ID: ").append(flatId).append("\n\n");
        } else {
            receipt.append("\n");
        }

        receipt.append("This receipt confirms your booking of the above flat unit. ");
        receipt.append("Please retain this document for your records.\n\n");
        
        receipt.append("Important Information:\n");
        receipt.append("--------------------\n");
        receipt.append("1. Further instructions regarding payment will be sent to you separately.\n");
        receipt.append("2. For enquiries, please contact HDB at 1800-123-4567.\n");
        receipt.append("3. Please quote your NRIC and Flat ID in all communications.\n\n");
        
        receipt.append("Thank you for choosing HDB.");

        return receipt.toString();
    }
    
    // Original method maintained for backward compatibility
    public String formatReceipt(User user, Project project, FlatType flatType) {
        StringBuilder receipt = new StringBuilder();

        // Format current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(new Date());

        receipt.append("RECEIPT\n");
        receipt.append("=======\n\n");
        receipt.append("Date: ").append(currentDate).append("\n\n");

        receipt.append("Applicant Information:\n");
        receipt.append("---------------------\n");
        receipt.append("NRIC: ").append(user.getNric()).append("\n");
        receipt.append("Age: ").append(user.getAge()).append("\n");
        receipt.append("Marital Status: ").append(user.getMaritalStatus()).append("\n\n");

        receipt.append("Project Information:\n");
        receipt.append("-------------------\n");
        receipt.append("Project Name: ").append(project.getProjectName()).append("\n");
        receipt.append("Neighborhood: ").append(project.getNeighborhood()).append("\n");
        receipt.append("Flat Type: ").append(flatType).append("\n\n");

        receipt.append("Thank you for your booking. Please keep this receipt for your records.");

        return receipt.toString();
    }
}