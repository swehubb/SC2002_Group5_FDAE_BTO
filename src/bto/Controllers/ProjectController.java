package bto.Controllers;

import bto.EntitiesProjectRelated.*;
import bto.Entities.*;
import bto.Enums.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectController {
 private Map<String, Project> projects; // Map of project name to Project
 
 // Constructor
 public ProjectController() {
     projects = new HashMap<>();
 }
 
 // Methods
 public List<Project> filterProjects(FilterCriteria criteria) {
     List<Project> filtered = new ArrayList<>();
     
     for (Project project : projects.values()) {
         boolean match = true;
         
         for (Map.Entry<String, Object> entry : criteria.getCriteria().entrySet()) {
             String key = entry.getKey();
             Object value = entry.getValue();
             
             if (key.equals("isVisible") && (boolean) value != project.isVisible()) {
                 match = false;
                 break;
             }
             
             // Add more filter criteria as needed
         }
         
         if (match) {
             filtered.add(project);
         }
     }
     
     return filtered;
 }
 
 public Project getProjectByName(String projectName) {
     return projects.get(projectName);
 }
 
 public List<Project> getAllProjects() {
     return new ArrayList<>(projects.values());
 }
 
 public Project createProject(Project project) {
     if (project != null && !projects.containsKey(project.getProjectName())) {
         projects.put(project.getProjectName(), project);
         return project;
     }
     return null;
 }
 
 public boolean editProject(Project project, Map<String, Object> changes) {
     if (project != null && projects.containsKey(project.getProjectName())) {
         // Apply changes to project
         for (Map.Entry<String, Object> entry : changes.entrySet()) {
             String key = entry.getKey();
             Object value = entry.getValue();
             
             switch (key) {
                 case "neighborhood":
                     project.setNeighborhood((String) value);
                     break;
                 case "isVisible":
                     project.setVisible((boolean) value);
                     break;
                 case "applicationOpenDate":
                     project.setApplicationOpenDate((java.util.Date) value);
                     break;
                 case "applicationCloseDate":
                     project.setApplicationCloseDate((java.util.Date) value);
                     break;
                 // Add more fields as needed
             }
         }
         
         return true;
     }
     
     return false;
 }
 
 public boolean deleteProject(Project project) {
     if (project != null && projects.containsKey(project.getProjectName())) {
         projects.remove(project.getProjectName());
         return true;
     }
     
     return false;
 }
 
 public boolean toggleVisibility(Project project, boolean visible) {
     if (project != null && projects.containsKey(project.getProjectName())) {
         project.setVisible(visible);
         return true;
     }
     
     return false;
 }
 
 public List<Project> getVisibleProjectsForApplicant(User user) {
	    List<Project> visibleProjects = new ArrayList<>();
	    
	    for (Project project : projects.values()) {
	        // First check if project is visible (toggled "on" by managers)
	        if (!project.isVisible()) {
	            continue;
	        }
	        
	        // Then check if user is eligible for any flat type in the project
	        List<FlatType> eligibleTypes = project.getEligibleFlatTypes(user);
	        if (!eligibleTypes.isEmpty()) {
	            visibleProjects.add(project);
	        }
	    }
	    
	    return visibleProjects;
	}
 
 public List<FlatType> getEligibleFlatTypes(Project project, User user) {
     if (project != null && user != null) {
         return project.getEligibleFlatTypes(user);
     }
     
     return new ArrayList<>();
 }
 
 public boolean updateFlatAvailability(Project project, FlatType flatType, int quantity) {
     if (project != null && projects.containsKey(project.getProjectName())) {
         return project.updateFlatAvailability(flatType, quantity);
     }
     
     return false;
 }
 
 // Getter and setter for projects map
 public Map<String, Project> getProjects() {
     return projects;
 }
 
 public void setProjects(List<Project> projectList) {
     this.projects.clear();
     
     for (Project project : projectList) {
         this.projects.put(project.getProjectName(), project);
     }
 }
}