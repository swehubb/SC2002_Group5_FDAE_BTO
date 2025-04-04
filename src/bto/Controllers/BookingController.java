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
    private ReceiptGenerator receiptGenerator;

    // Constructor
    public BookingController() {
        bookings = new HashMap<>();
        receiptGenerator = new ReceiptGenerator();
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

        // Set status to approved automatically when created by officer
        booking.setBookingStatus(FlatBooking.STATUS_APPROVED);
        
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

    // Method to process a booking (old version preserved for compatibility)
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
        booking.setBookingStatus(FlatBooking.STATUS_APPROVED); // Approve immediately

        // Set the booking for the applicant
        applicant.setBookedFlat(booking);

        // Update application status
        application.setStatus(ApplicationStatus.BOOKED);

        // Store in the database
        bookings.put(applicant.getNric(), booking);

        return booking;
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
        booking.setBookingStatus(FlatBooking.STATUS_APPROVED); // Approve immediately

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

        String nric = application.getApplicant().getNric();
        
        // Check if there's an existing booking
        FlatBooking existingBooking = bookings.get(nric);
        
        if (existingBooking != null) {
            // Update existing booking with rejection
            existingBooking.setBookingStatus(FlatBooking.STATUS_REJECTED);
            existingBooking.setRejectionReason(rejectionReason);
        } else {
            // Create a rejected booking record
            FlatBooking booking = new FlatBooking();
            booking.setApplicant(application.getApplicant());
            booking.setProject(application.getProject());
            booking.setFlatType(application.getSelectedFlatType());
            booking.setBookingStatus(FlatBooking.STATUS_REJECTED);
            booking.setRejectionReason(rejectionReason);
            
            // Store in the database
            bookings.put(nric, booking);
            
            // Set the booking for the applicant
            application.getApplicant().setBookedFlat(booking);
        }
        
        return true;
    }

    // Method to check if an application has an approved booking
    public boolean hasApprovedBooking(ProjectApplication application) {
        if (application == null || application.getApplicant() == null) {
            return false;
        }

        String nric = application.getApplicant().getNric();
        FlatBooking booking = bookings.get(nric);
        
        return booking != null && booking.isApproved();
    }

    // Method to get booking status for an application
    public String getBookingStatus(ProjectApplication application) {
        if (application == null || application.getApplicant() == null) {
            return null;
        }

        String nric = application.getApplicant().getNric();
        FlatBooking booking = bookings.get(nric);
        
        return booking != null ? booking.getBookingStatus() : null;
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

    // Method to get pending bookings
    public List<FlatBooking> getPendingBookings() {
        List<FlatBooking> pendingBookings = new ArrayList<>();
        
        for (FlatBooking booking : bookings.values()) {
            if (booking.isPending()) {
                pendingBookings.add(booking);
            }
        }
        
        return pendingBookings;
    }
    
    // Method to get approved bookings
    public List<FlatBooking> getApprovedBookings() {
        List<FlatBooking> approvedBookings = new ArrayList<>();
        
        for (FlatBooking booking : bookings.values()) {
            if (booking.isApproved()) {
                approvedBookings.add(booking);
            }
        }
        
        return approvedBookings;
    }
    
    // Method to get rejected bookings
    public List<FlatBooking> getRejectedBookings() {
        List<FlatBooking> rejectedBookings = new ArrayList<>();
        
        for (FlatBooking booking : bookings.values()) {
            if (booking.isRejected()) {
                rejectedBookings.add(booking);
            }
        }
        
        return rejectedBookings;
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