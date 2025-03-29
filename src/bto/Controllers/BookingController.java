package bto.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import bto.Enums.*;
import bto.EntitiesProjectRelated.*;
import bto.Entities.*;

public class BookingController {
    private Map<String, FlatBooking> bookings; // Simulate a database of bookings
    private Map<String, String> rejectedBookings; // Track rejected bookings and reasons
    private ReceiptGenerator receiptGenerator;

    // Constructor
    public BookingController() {
        bookings = new HashMap<>();
        rejectedBookings = new HashMap<>();
        receiptGenerator = new ReceiptGenerator();
    }

    // Original method
    public FlatBooking processBooking(Applicant applicant, Project project, FlatType flatType) {
        // Check if the applicant has a successful application
        ProjectApplication application = applicant.getAppliedProject();

        if (application == null || application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return null; // No successful application
        }

        // Book a specific flat of the selected type
        int flatId = project.bookFlat(flatType);

        if (flatId == -1) {
            return null; // No available flats of the selected type
        }

        // Create a new booking with the specific flat ID
        FlatBooking booking = new FlatBooking(applicant, project, flatType, flatId);

        // Set the booking for the applicant
        applicant.setBookedFlat(booking);

        // Update application status
        application.setStatus(ApplicationStatus.BOOKED);

        // Store in the database
        bookings.put(applicant.getNric(), booking);

        return booking;
    }

    // Method used in officer interface
    public boolean createBooking(FlatBooking booking) {
        if (booking == null || booking.getApplicant() == null) {
            return false;
        }

        // Check if the applicant already has a booking
        String nric = booking.getApplicant().getNric();
        if (bookings.containsKey(nric)) {
            return false; // Applicant already has a booking
        }

        // Store the booking
        bookings.put(nric, booking);

        // Set the booking for the applicant
        booking.getApplicant().setBookedFlat(booking);

        // Update application status if applicable
        ProjectApplication application = booking.getApplicant().getAppliedProject();
        if (application != null && application.getStatus() == ApplicationStatus.SUCCESSFUL) {
            application.setStatus(ApplicationStatus.BOOKED);
        }

        return true;
    }

    // Overloaded createBooking method used in officer interface
    public boolean createBooking(ProjectApplication application, HDBOfficer officer) {
        if (application == null || officer == null) {
            return false;
        }

        Applicant applicant = application.getApplicant();
        Project project = application.getProject();
        FlatType flatType = application.getSelectedFlatType();

        if (applicant == null || project == null || flatType == null) {
            return false;
        }

        // Book a specific flat of the selected type
        int flatId = project.bookFlat(flatType);

        if (flatId == -1) {
            return false; // No available flats of the selected type
        }

        // Create a new booking with the specific flat ID
        FlatBooking booking = new FlatBooking(applicant, project, flatType, flatId);

        // Set the booking for the applicant
        applicant.setBookedFlat(booking);

        // Update application status
        application.setStatus(ApplicationStatus.BOOKED);

        // Store in the database
        bookings.put(applicant.getNric(), booking);

        return true;
    }

    // Method to reject a booking request
    public boolean rejectBooking(ProjectApplication application, String rejectionReason) {
        if (application == null || application.getApplicant() == null) {
            return false;
        }

        // Store rejection reason
        String nric = application.getApplicant().getNric();
        rejectedBookings.put(nric, rejectionReason);

        return true;
    }

    // Method to check if an application has an approved booking
    public boolean hasApprovedBooking(ProjectApplication application) {
        if (application == null || application.getApplicant() == null) {
            return false;
        }

        String nric = application.getApplicant().getNric();
        return bookings.containsKey(nric);
    }

    // Method to get booking status
    public boolean getBookingStatus(ProjectApplication application) {
        if (application == null || application.getApplicant() == null) {
            return false;
        }

        String nric = application.getApplicant().getNric();
        return bookings.containsKey(nric);
    }

    // Method to get booking for an application
    public FlatBooking getBookingForApplication(ProjectApplication application) {
        if (application == null || application.getApplicant() == null) {
            return null;
        }

        String nric = application.getApplicant().getNric();
        return bookings.get(nric);
    }

    // Method to get a list of all bookings
    public List<FlatBooking> getAllBookings() {
        return new ArrayList<>(bookings.values());
    }

    // Original method
    public boolean updateFlatAvailability(Project project, FlatType flatType) {
        int availableUnits = project.getFlatTypeUnits().getOrDefault(flatType, 0);

        if (availableUnits > 0) {
            project.updateFlatAvailability(flatType, availableUnits - 1);
            return true;
        }

        return false;
    }

    // Original method
    public String generateReceipt(FlatBooking booking) {
        return receiptGenerator.generateReceipt(booking);
    }
    
    // Method to generate receipt directly from booking controller
    public boolean generateReceipt(ProjectApplication application) {
        if (application == null || application.getApplicant() == null) {
            return false;
        }
        
        String nric = application.getApplicant().getNric();
        FlatBooking booking = bookings.get(nric);
        
        if (booking == null) {
            return false;
        }
        
        // Generate receipt
        String receipt = receiptGenerator.generateReceipt(booking);
        return receipt != null && !receipt.isEmpty();
    }
}