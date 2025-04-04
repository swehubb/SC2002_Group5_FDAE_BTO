package bto.EntitiesProjectRelated;

import bto.Enums.*;
import bto.Entities.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Project {
    private String projectName;
    private String neighborhood;
    private Map<FlatType, Integer> flatTypeUnits; // Total units allocated for each flat type
    private boolean isVisible;
    private Date applicationOpenDate;
    private Date applicationCloseDate;
    private HDBManager managerInCharge;
    private int availableHDBOfficerSlots;
    private List<Enquiry> enquiries;
    private List<ProjectApplication> applications;
    private List<OfficerRegistration> officerRegistrations;
    private int totalOfficerSlots; // New field to store total slots

    
    // New field for managing individual flats
    private ProjectFlats projectFlats;
    
    // Constructors
    public Project() {
        flatTypeUnits = new HashMap<>();
        enquiries = new ArrayList<>();
        applications = new ArrayList<>();
        officerRegistrations = new ArrayList<>();
        isVisible = false;
        projectFlats = new ProjectFlats(this);
    }
    
    public Project(String projectName, String neighborhood, HDBManager managerInCharge) {
        this();
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.managerInCharge = managerInCharge;
    }
    
    /**
     * Initializes the flat units for this project.
     * This method should be called after setting the flatTypeUnits map.
     */
    public void initializeProjectFlats() {
        projectFlats.initializeFlats(flatTypeUnits);
    }
    
    /**
     * Updates the count of available flats for a specific flat type.
     * This method is called by ProjectFlats when a flat is booked or released.
     * 
     * @param flatType The type of flat
     * @param newCount The new count of available flats
     * @return true if the update was successful, false otherwise
     */
    public boolean updateFlatTypeCount(FlatType flatType, int newCount) {
        if (newCount < 0) {
            return false;
        }
        
        flatTypeUnits.put(flatType, newCount);
        return true;
    }
    
    /**
     * Books a flat of the specified type for an applicant.
     * 
     * @param flatType The type of flat to book
     * @return The ID of the booked flat, or -1 if no flat of that type is available
     */
    public int bookFlat(FlatType flatType) {
        return projectFlats.bookFlat(flatType);
    }
    
    /**
     * Releases a previously booked flat.
     * 
     * @param flatId The ID of the flat to release
     * @return true if the flat was successfully released, false otherwise
     */
    public boolean releaseFlat(int flatId) {
        return projectFlats.releaseFlat(flatId);
    }
    
    /**
     * Gets the number of available flats of the specified type.
     * 
     * @param flatType The type of flat
     * @return The number of available flats of the specified type
     */
    public int getAvailableFlatCount(FlatType flatType) {
        return projectFlats.getAvailableFlatCount(flatType);
    }
    
    // Methods
    public List<FlatType> getEligibleFlatTypes(User user) {
        List<FlatType> eligibleTypes = new ArrayList<>();
        
        // Implementation for eligibility check based on user type, age, marital status
        // Singles >= 35 years old are eligible for 2-Room flats only
        // Married couples >= 21 years old are eligible for all flat types
        if (user.getMaritalStatus() == MaritalStatus.SINGLE && user.getAge() >= 35) {
            eligibleTypes.add(FlatType.TWO_ROOM);
        } else if (user.getMaritalStatus() == MaritalStatus.MARRIED && user.getAge() >= 21) {
            eligibleTypes.add(FlatType.TWO_ROOM);
            eligibleTypes.add(FlatType.THREE_ROOM);
        }
        
        return eligibleTypes;
    }
    
    public String getDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Project Name: ").append(projectName).append("\n");
        details.append("Neighborhood: ").append(neighborhood).append("\n");
        details.append("Application Period: ").append(applicationOpenDate).append(" to ").append(applicationCloseDate).append("\n");
        details.append("Available Flat Types:\n");
        
        for (Map.Entry<FlatType, Integer> entry : flatTypeUnits.entrySet()) {
            details.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append(" units\n");
        }
        
        return details.toString();
    }
    
    /**
     * Updates the flat availability for a specific flat type directly.
     * This method is kept for backward compatibility but now delegates to ProjectFlats.
     * 
     * @param flatType The type of flat
     * @param quantity The new quantity of available flats
     * @return true if the update was successful, false otherwise
     */
    public boolean updateFlatAvailability(FlatType flatType, int quantity) {
        if (quantity < 0) {
            return false;
        }
        
        flatTypeUnits.put(flatType, quantity);
        
        // Re-initialize project flats with the updated quantities
        Map<FlatType, Integer> updatedFlats = new HashMap<>(flatTypeUnits);
        projectFlats = new ProjectFlats(this);
        projectFlats.initializeFlats(updatedFlats);
        
        return true;
    }
    
    public boolean toggleVisibility(boolean visible) {
        this.isVisible = visible;
        return true;
    }
    
    public List<Enquiry> getProjectEnquiries() {
        return enquiries;
    }
    
    /**
     * Gets a list of all HDB officers with approved registrations for this project.
     * 
     * @return List of HDBOfficer objects with approved registrations
     */
    public List<HDBOfficer> getApprovedOfficers() {
        List<HDBOfficer> approvedOfficers = new ArrayList<>();
        
        for (OfficerRegistration registration : officerRegistrations) {
            if (registration.getRegistrationStatus().equals("APPROVED")) {
                approvedOfficers.add(registration.getHdbOfficer());
            }
        }
        
        return approvedOfficers;
    }
    
    // Getters and Setters
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Map<FlatType, Integer> getFlatTypeUnits() {
        return flatTypeUnits;
    }

    public void setFlatTypeUnits(Map<FlatType, Integer> flatTypeUnits) {
        this.flatTypeUnits = flatTypeUnits;
        initializeProjectFlats(); // Initialize the project flats with the new units
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Date getApplicationOpenDate() {
        return applicationOpenDate;
    }

    public void setApplicationOpenDate(Date applicationOpenDate) {
        this.applicationOpenDate = applicationOpenDate;
    }

    public Date getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public void setApplicationCloseDate(Date applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    public HDBManager getManagerInCharge() {
        return managerInCharge;
    }

    public void setManagerInCharge(HDBManager managerInCharge) {
        this.managerInCharge = managerInCharge;
    }

    public int getAvailableHDBOfficerSlots() {
        return availableHDBOfficerSlots;
    }

    public void setAvailableHDBOfficerSlots(int availableHDBOfficerSlots) {
        this.availableHDBOfficerSlots = availableHDBOfficerSlots;
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    public void setEnquiries(List<Enquiry> enquiries) {
        this.enquiries = enquiries;
    }

    public List<ProjectApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<ProjectApplication> applications) {
        this.applications = applications;
    }

    public List<OfficerRegistration> getOfficerRegistrations() {
        return officerRegistrations;
    }

    public void setOfficerRegistrations(List<OfficerRegistration> officerRegistrations) {
        this.officerRegistrations = officerRegistrations;
    }
    
    public ProjectFlats getProjectFlats() {
        return projectFlats;
    }
    
    // Helper methods
    public void addEnquiry(Enquiry enquiry) {
        if (!enquiries.contains(enquiry)) {
            enquiries.add(enquiry);
        }
    }
    
    public void addApplication(ProjectApplication application) {
        if (!applications.contains(application)) {
            applications.add(application);
        }
    }
    
    public void addOfficerRegistration(OfficerRegistration registration) {
        if (!officerRegistrations.contains(registration)) {
            officerRegistrations.add(registration);
        }
    }
    
    public int getTotalOfficerSlots() {
        return totalOfficerSlots;
    }

    public void setTotalOfficerSlots(int totalSlots) {
        this.totalOfficerSlots = totalSlots;
    }

}