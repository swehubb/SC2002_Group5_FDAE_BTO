package bto.Controllers;

import bto.Entities.*;
import bto.EntitiesProjectRelated.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

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
    
 // Add to WithdrawalController class
    public Withdrawal getWithdrawalByApplicant(Applicant applicant) {
        if (applicant == null) {
            return null;
        }
        return withdrawals.get(applicant.getNric());
    }

    public void addWithdrawal(Withdrawal withdrawal) {
        if (withdrawal != null && withdrawal.getApplicant() != null) {
            withdrawals.put(withdrawal.getApplicant().getNric(), withdrawal);
        }
    }
    
    public void notifyApplicantStatus(Withdrawal withdrawal) {
        // Implementation to notify the applicant about the withdrawal status
        // This could be via email, SMS, or other means
        
        // Placeholder
        System.out.println("Notification sent to " + withdrawal.getApplicant().getNric() + 
                           " regarding withdrawal status: " + withdrawal.getStatus());
    }
    
 // Add to WithdrawalController class
    /**
     * Gets all withdrawals in the system
     * @return List of all withdrawals
     */
    public List<Withdrawal> getAllWithdrawals() {
        return new ArrayList<>(withdrawals.values());
    }

    /**
     * Sets the withdrawals in the controller (used when loading from file)
     * @param withdrawalsList List of withdrawals to set
     */
    public void setWithdrawals(List<Withdrawal> withdrawalsList) {
        this.withdrawals.clear();
        for (Withdrawal withdrawal : withdrawalsList) {
            this.withdrawals.put(withdrawal.getApplicant().getNric(), withdrawal);
        }
    }
}