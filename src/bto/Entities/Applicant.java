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
     * Generates a receipt for the applicant's booking or application.
     * If the applicant has a booked flat, it generates a receipt with the flat details.
     * If the applicant only has an application (no booking yet), it generates a provisional receipt.
     * 
     * @return The receipt as a formatted string, or null if neither booking nor application exists
     */
    public String generateReceipt() {
        ReceiptGenerator generator = new ReceiptGenerator();
        
        // If the applicant has a booked flat, generate a receipt for it
        if (bookedFlat != null) {
            return generator.generateReceipt(bookedFlat);
        }
        
        // If the applicant has an application but no booking yet, generate a provisional receipt
        if (appliedProject != null) {
            boolean success = generator.generateReceipt(appliedProject);
            if (success) {
                return "Provisional receipt generated for your application to " + 
                       appliedProject.getProject().getProjectName() + ".\n" +
                       "A complete receipt will be provided once your flat is booked.";
            }
        }
        
        // If the applicant has neither booking nor application
        return "No active bookings or applications found.";
    }
    
    // Getters and Setters
    public ProjectApplication getAppliedProject() {
        return appliedProject;
    }
   
    public void setAppliedProject(ProjectApplication appliedProject) {
        this.appliedProject = appliedProject;
    }
   
    public FlatBooking getBookedFlat() {
        return bookedFlat;
    }
   
    public void setBookedFlat(FlatBooking bookedFlat) {
        this.bookedFlat = bookedFlat;
    }
}
