package bto.Entities;

import bto.EntitiesProjectRelated.*;
import bto.Enums.*;
import bto.Controllers.ReceiptGenerator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Applicant extends User {
    private ProjectApplication appliedProject;
    private FlatBooking bookedFlat;
    
    // Constructors
    public Applicant() {
        super();
    }
    
    public Applicant(String nric, String password, int age, MaritalStatus maritalStatus, String name) {
        super(nric, password, age, maritalStatus, name);
    }
    
    // Original methods retained (not showing all for brevity)
    
    /**
     * Allows an applicant to apply for a BTO project.
     * Singles age 35 and above can only apply for 2-Room flats.
     * Married couples age 21 and above can apply for any flat type.
     * Applicants cannot apply for multiple projects simultaneously.
     * 
     * @param project The project the applicant wishes to apply for
     * @return true if application was successful, false otherwise
     */
    public boolean applyForProject(Project project) {
        // Implementation remains the same as your original code
        // This is just a placeholder - keep your original implementation
        return false;
    }
    
    // Other original methods retained
    
   /**
 * Generates a receipt for the applicant's booking.
 * If the applicant has a booked flat, it returns the receipt with the booking details.
 * 
 * @return The receipt as a formatted string, or a message if no booking exists
 */
public String generateReceipt() {
    if (bookedFlat != null) {
        ReceiptGenerator generator = new ReceiptGenerator();
        String receipt = generator.generateReceipt(bookedFlat);
        
        if (receipt != null && !receipt.isEmpty()) {
            return receipt;
        } else {
            return "Failed to generate receipt for your booking. Please contact a HDB Officer.";
        }
    } else if (appliedProject != null) {
        return "No booking found. Your application status is: " + appliedProject.getStatus() + 
               ". Please contact an HDB Officer to process your booking if your application is successful.";
    } else {
        return "No active applications or bookings found.";
    }
}
