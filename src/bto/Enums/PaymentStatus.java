package bto.Enums;

/**
 * Enum representing the different payment statuses for a flat booking.
 */
public enum PaymentStatus {
    PENDING("Payment Pending"),
    PAID("Payment Complete"),
    PARTIALLY_PAID("Partially Paid"),
    REFUNDED("Refunded"),
    CANCELLED("Payment Cancelled");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
