package bto.Entities;

import java.util.ArrayList;
import java.util.List;
import bto.Enums.*;
import bto.EntitiesProjectRelated.*;
import bto.Controllers.ReceiptGenerator;

public class HDBOfficer extends User {
     private List<Project> assignedProjects;
     private List<OfficerRegistration> registrations;
     private Applicant applicantRole; // Composition - HDBOfficer has an Applicant role
     
     // Constructors
     public HDBOfficer() {
         super();
         assignedProjects = new ArrayList<>();
         registrations = new ArrayList<>();
         // Create the applicant role with the same user details
         applicantRole = new Applicant();
     }
     
     public HDBOfficer(String nric, String password, int age, MaritalStatus maritalStatus, String name) {
         super(nric, password, age, maritalStatus, name);
         assignedProjects = new ArrayList<>();
         registrations = new ArrayList<>();
         // Create the applicant role with the same user details
         applicantRole = new Applicant(nric, password, age, maritalStatus, name);
     }
     
     // Original Officer-specific Methods retained
     
/**
 * Generates a receipt for a project application.
 * Officers cannot generate receipts for their own applications.
 * 
 * @param application The project application to generate a receipt for
 * @return The receipt as a formatted string, or null if the application is invalid or belongs to the officer
 */
public String generateReceipt(ProjectApplication application) {
    // Check if the application belongs to the officer
    if (application == null || application.getApplicant().getNric().equals(this.getNric())) {
        // Not allowed to generate receipt for their own application
        return "Cannot generate receipt for your own application";
    }

         // Use the ReceiptGenerator to generate the receipt
    ReceiptGenerator generator = new ReceiptGenerator();
    
    // Check if the application has a booking
    FlatBooking booking = application.getApplicant().getBookedFlat();
    if (booking != null) {
        // Set the officer as the processor if not already set
        if (booking.getProcessedByOfficer() == null) {
            booking.setProcessedByOfficer(this);
        }
        return generator.generateReceipt(booking);
    } else if (application.getStatus() == ApplicationStatus.SUCCESSFUL) {
        // Create a temporary booking object for receipt generation
        FlatBooking tempBooking = new FlatBooking(
            application.getApplicant(),
            application.getProject(),
            application.getSelectedFlatType(),
            0  // No flat ID yet
        );
        
        // Set the officer as the processor
        tempBooking.setProcessedByOfficer(this);
        
        return generator.generateReceipt(tempBooking);
    } else {
        return "Cannot generate receipt. Application status: " + application.getStatus();
    }
}
}


