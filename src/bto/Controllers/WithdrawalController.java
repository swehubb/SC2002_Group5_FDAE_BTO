package bto.Controllers;

import bto.Entities.*;
import bto.EntitiesProjectRelated.*;
import java.util.HashMap;
import java.util.Map;

public class WithdrawalController {
    private Map<String, Withdrawal> withdrawals; // Simulate a database of withdrawals
    
    // Constructor
    public WithdrawalController() {
        withdrawals = new HashMap<>();
    }
    
    // Methods
    public Withdrawal submitWithdrawal(Applicant applicant, ProjectApplication application) {
        // Check if the applicant has an application
        if (application == null || application.getApplicant() != applicant) {
            return null; // No matching application
        }
        
        // Check if the application already has a withdrawal
        if (application.getWithdrawalStatus() != null) {
            return null; // Already has a withdrawal
        }
        
        // Create a new withdrawal
        Withdrawal withdrawal = new Withdrawal(applicant, application);
        
        // Update application withdrawal status
        application.setWithdrawalStatus("PENDING");
        
        // Store in the database
        withdrawals.put(applicant.getNric(), withdrawal);
        
        return withdrawal;
    }
    
    public boolean approveWithdrawal(Withdrawal withdrawal) {
        if (withdrawal == null) {
            return false;
        }
        
        // Update withdrawal status
        withdrawal.setStatus("APPROVED");
        
        // Update application withdrawal status
        withdrawal.getApplication().setWithdrawalStatus("APPROVED");
        
        // Remove the application from the applicant
        withdrawal.getApplicant().setAppliedProject(null);
        
        // Notify the applicant
        notifyApplicantStatus(withdrawal);
        
        return true;
    }
    
    public boolean rejectWithdrawal(Withdrawal withdrawal) {
        if (withdrawal == null) {
            return false;
        }
        
        // Update withdrawal status
        withdrawal.setStatus("REJECTED");
        
        // Update application withdrawal status
        withdrawal.getApplication().setWithdrawalStatus("REJECTED");
        
        // Notify the applicant
        notifyApplicantStatus(withdrawal);
        
        return true;
    }
    
    public void notifyApplicantStatus(Withdrawal withdrawal) {
        // Implementation to notify the applicant about the withdrawal status
        // This could be via email, SMS, or other means
        
        // Placeholder
        System.out.println("Notification sent to " + withdrawal.getApplicant().getNric() + 
                           " regarding withdrawal status: " + withdrawal.getStatus());
    }
}