package bto.Controllers;

import bto.EntitiesProjectRelated.*;
import bto.Entities.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationController {
    private Map<String, List<OfficerRegistration>> registrations; // Simulate a database of registrations
    
    // Constructor
    public RegistrationController() {
        registrations = new HashMap<>();
    }
    
    /**
     * Registers an officer for a project
     * 
     * @param officer The HDBOfficer to register
     * @param project The Project to register for
     * @return The created OfficerRegistration object, or null if no slots available
     */
    public OfficerRegistration registerOfficer(HDBOfficer officer, Project project) {

        // Check if the project has available slots
        if (project.getAvailableHDBOfficerSlots() <= 0) {
            return null; // No available slots
        }

        // Create a new registration
        OfficerRegistration registration = new OfficerRegistration(officer, project);

        // Add to the officer's registrations
        officer.addRegistration(registration);

        // Add to the project's registrations
        project.addOfficerRegistration(registration);

        // Store in the database
        if (!registrations.containsKey(officer.getNric())) {
            registrations.put(officer.getNric(), new ArrayList<>());
        }
        registrations.get(officer.getNric()).add(registration);

        return registration;
    }
    
    /**
     * Approves an officer registration
     * 
     * @param registration The OfficerRegistration to approve
     * @return true if successful, false otherwise
     */
    public boolean approveRegistration(OfficerRegistration registration) {
        if (registration == null) {
            return false;
        }
        
        // Update registration status
        registration.setRegistrationStatus("APPROVED");
        
        // Add the project to the officer's assigned projects
        registration.getHdbOfficer().addAssignedProject(registration.getProject());
        
        // Reduce available slots
        updateProjectSlots(registration.getProject());
        
        return true;
    }
    
    /**
     * Rejects an officer registration
     * 
     * @param registration The OfficerRegistration to reject
     * @return true if successful, false otherwise
     */
    public boolean rejectRegistration(OfficerRegistration registration) {
        if (registration == null) {
            return false;
        }
        
        // Update registration status
        registration.setRegistrationStatus("REJECTED");
        
        return true;
    }
    
    /**
     * Gets all registrations for a specific officer
     * 
     * @param officer The HDBOfficer whose registrations to view
     * @return List of OfficerRegistration objects
     */
    public List<OfficerRegistration> viewRegistrationStatus(HDBOfficer officer) {
        return registrations.getOrDefault(officer.getNric(), new ArrayList<>());
    }
    
    /**
     * Gets all registrations in the system
     * 
     * @return List of all OfficerRegistration objects
     */
    public List<OfficerRegistration> getAllRegistrations() {
        List<OfficerRegistration> allRegistrations = new ArrayList<>();
        
        
        for (Map.Entry<String, List<OfficerRegistration>> entry : registrations.entrySet()) {
            
            for (OfficerRegistration reg : entry.getValue()) {
            }
            
            allRegistrations.addAll(entry.getValue());
        }
        
        return allRegistrations;
    }
    
    /**
     * Updates the available officer slots for a project
     * 
     * @param project The Project to update
     * @return true if slots were updated, false if no slots available
     */
    public boolean updateProjectSlots(Project project) {
        int availableSlots = project.getAvailableHDBOfficerSlots();
        
        if (availableSlots > 0) {
            project.setAvailableHDBOfficerSlots(availableSlots - 1);
            return true;
        }
        
        return false;
    }
    public void setRegistrations(List<OfficerRegistration> registrationList) {
        // Clear existing registrations
        this.registrations.clear();
        
        // Debug: Print incoming registrations
        System.out.println("Setting registrations. Total count: " + registrationList.size());
        
        for (OfficerRegistration registration : registrationList) {
            String officerNric = registration.getHdbOfficer().getNric();
            
            // Ensure the list exists for this officer
            if (!this.registrations.containsKey(officerNric)) {
                this.registrations.put(officerNric, new ArrayList<>());
            }
            
            // Add the registration to the list
            this.registrations.get(officerNric).add(registration);
            
            // Debug: Print each registration as it's added
            System.out.println("Added registration for Officer: " + registration.getHdbOfficer().getName() 
                               + ", Project: " + registration.getProject().getProjectName() 
                               + ", Status: " + registration.getRegistrationStatus());
        }
        
        // Verify registrations after setting
        System.out.println("Registrations map size after setting: " + this.registrations.size());
    }
    
}