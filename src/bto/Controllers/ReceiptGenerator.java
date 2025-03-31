
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
        return null;
    }
    
    // Use the ReceiptGenerator to generate the receipt
    ReceiptGenerator generator = new ReceiptGenerator();
    boolean success = generator.generateReceipt(application);
    
    if (success) {
        // Create a temporary booking object for receipt generation
        FlatBooking tempBooking = new FlatBooking(
            application.getApplicant(),
            application.getProject(),
            application.getSelectedFlatType(),
            0  // No flat ID yet
        );
        
        // Set the officer as the processor
        try {
            tempBooking.setProcessedByOfficer(this);
        } catch (Exception e) {
            // ProcessedByOfficer might not be available in the FlatBooking class
        }
        
        return generator.generateReceipt(tempBooking);
    } else {
        return null;
    }
}

/**
 * Generates a receipt for a flat booking.
 * Officers cannot generate receipts for their own bookings.
 * 
 * @param booking The flat booking to generate a receipt for
 * @return The receipt as a formatted string, or null if the booking is invalid or belongs to the officer
 */
public String generateReceiptForBooking(FlatBooking booking) {
    // Check if the booking belongs to the officer
    if (booking == null || booking.getApplicant().getNric().equals(this.getNric())) {
        // Not allowed to generate receipt for their own booking
        return null;
    }
    
    // Set the officer as the processor
    try {
        booking.setProcessedByOfficer(this);
    } catch (Exception e) {
        // ProcessedByOfficer might not be available in the FlatBooking class
    }
    
    // Use the ReceiptGenerator to generate the receipt
    ReceiptGenerator generator = new ReceiptGenerator();
    return generator.generateReceipt(booking);
}
