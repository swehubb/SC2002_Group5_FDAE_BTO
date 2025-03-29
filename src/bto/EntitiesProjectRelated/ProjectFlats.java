package bto.EntitiesProjectRelated;

import bto.Enums.FlatType;
import java.util.HashMap;
import java.util.Map;

public class ProjectFlats {
    // Map of flat IDs to their types
    private Map<Integer, FlatType> flats;
    
    // Map of flat IDs to their availability status (true = available, false = booked)
    private Map<Integer, Boolean> flatAvailability;
    
    // Count of available flats by type
    private Map<FlatType, Integer> availableFlatCounts;
    
    // The project this flat collection belongs to
    private Project project;
    
    /**
     * Creates a new ProjectFlats instance for the specified project.
     * 
     * @param project The project these flats belong to
     */
    public ProjectFlats(Project project) {
        this.project = project;
        this.flats = new HashMap<>();
        this.flatAvailability = new HashMap<>();
        this.availableFlatCounts = new HashMap<>();
        
        // Initialize available flat counts to zero
        for (FlatType type : FlatType.values()) {
            availableFlatCounts.put(type, 0);
        }
    }
    
    /**
     * Initializes the flats for this project based on the specified counts for each type.
     * 
     * @param flatTypeCounts Map of flat types to their respective counts
     */
    public void initializeFlats(Map<FlatType, Integer> flatTypeCounts) {
        int flatId = 1;
        
        // Create flats for each type based on the counts
        for (Map.Entry<FlatType, Integer> entry : flatTypeCounts.entrySet()) {
            FlatType type = entry.getKey();
            int count = entry.getValue();
            
            // Update the available count for this flat type
            availableFlatCounts.put(type, count);
            
            // Create the specified number of flats of this type
            for (int i = 0; i < count; i++) {
                flats.put(flatId, type);
                flatAvailability.put(flatId, true); // All flats are initially available
                flatId++;
            }
        }
    }
    
    /**
     * Books a flat of the specified type, if available.
     * 
     * @param type The type of flat to book
     * @return The ID of the booked flat, or -1 if no flat of that type is available
     */
    public int bookFlat(FlatType type) {
        // Check if any flats of this type are available
        if (availableFlatCounts.getOrDefault(type, 0) <= 0) {
            return -1;
        }
        
        // Find the first available flat of this type
        for (Map.Entry<Integer, FlatType> entry : flats.entrySet()) {
            int flatId = entry.getKey();
            FlatType flatType = entry.getValue();
            
            if (flatType == type && flatAvailability.get(flatId)) {
                // Mark the flat as unavailable
                flatAvailability.put(flatId, false);
                
                // Decrease the available count for this flat type
                int currentCount = availableFlatCounts.get(type);
                availableFlatCounts.put(type, currentCount - 1);
                
                // Update the project's flat count
                project.updateFlatTypeCount(type, currentCount - 1);
                
                return flatId;
            }
        }
        
        return -1; // Should not reach here if count is accurate
    }
    
    /**
     * Releases a previously booked flat.
     * 
     * @param flatId The ID of the flat to release
     * @return true if the flat was successfully released, false otherwise
     */
    public boolean releaseFlat(int flatId) {
        // Check if the flat exists and is currently booked
        if (!flats.containsKey(flatId) || flatAvailability.get(flatId)) {
            return false;
        }
        
        // Mark the flat as available
        flatAvailability.put(flatId, true);
        
        // Get the type of this flat
        FlatType type = flats.get(flatId);
        
        // Increase the available count for this flat type
        int currentCount = availableFlatCounts.get(type);
        availableFlatCounts.put(type, currentCount + 1);
        
        // Update the project's flat count
        project.updateFlatTypeCount(type, currentCount + 1);
        
        return true;
    }
    
    /**
     * Gets the number of available flats of the specified type.
     * 
     * @param type The type of flat
     * @return The number of available flats of the specified type
     */
    public int getAvailableFlatCount(FlatType type) {
        return availableFlatCounts.getOrDefault(type, 0);
    }
    
    /**
     * Gets the total number of flats of the specified type (both available and booked).
     * 
     * @param type The type of flat
     * @return The total number of flats of the specified type
     */
    public int getTotalFlatCount(FlatType type) {
        int count = 0;
        for (FlatType flatType : flats.values()) {
            if (flatType == type) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Gets the type of the specified flat.
     * 
     * @param flatId The ID of the flat
     * @return The type of the flat, or null if the flat doesn't exist
     */
    public FlatType getFlatType(int flatId) {
        return flats.get(flatId);
    }
    
    /**
     * Checks if the specified flat is available.
     * 
     * @param flatId The ID of the flat
     * @return true if the flat is available, false otherwise
     */
    public boolean isFlatAvailable(int flatId) {
        return flatAvailability.getOrDefault(flatId, false);
    }
    
    /**
     * Gets a map of all available flats by type.
     * 
     * @return A map of flat types to their available counts
     */
    public Map<FlatType, Integer> getAvailableFlatCounts() {
        return new HashMap<>(availableFlatCounts);
    }
}