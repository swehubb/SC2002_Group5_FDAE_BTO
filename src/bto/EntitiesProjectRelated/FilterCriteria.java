package bto.EntitiesProjectRelated;

import bto.Entities.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class FilterCriteria {
    private Map<String, Object> criteria;
    
    // Constructors
    public FilterCriteria() {
        this.criteria = new HashMap<>();
    }
    
    // Methods
    public boolean addCriterion(String key, Object value) {
        criteria.put(key, value);
        return true;
    }
    
    public boolean removeCriterion(String key) {
        if (criteria.containsKey(key)) {
            criteria.remove(key);
            return true;
        }
        return false;
    }
    
    // Filtering method for projects that belong to the current HDBManager
    public List<Project> ownProjects(List<Project> allProjects, HDBManager currentManager) {
        List<Project> ownedProjects = new ArrayList<>();

        // Loop through all projects and check if their managerInCharge matches the currentManager
        for (Project project : allProjects) {
            // Here, we're assuming that the project has a 'managerInCharge' field of type 'HDBManager'
            if (project.getManagerInCharge().equals(currentManager)) {
                ownedProjects.add(project);
            }
        }

        return ownedProjects;
    }
    
    public Map<String, Object> getCriteria() {
        return criteria;
    }
    
    public boolean matches(Object obj) {
        // Implementation to check if object matches criteria
        return false; // Placeholder
    }
    
    // Getters and Setters
    public void setCriteria(Map<String, Object> criteria) {
        this.criteria = criteria;
    }
}