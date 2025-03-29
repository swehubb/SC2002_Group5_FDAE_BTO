package bto.Controllers;

import bto.Entities.*;
import bto.EntitiesProjectRelated.*;
import bto.Enums.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {
	private static final String APPLICANT_FILE = "./src/bto/data/Applicant List.txt";
	private static final String PROJECT_FILE = "./src/bto/data/Project List.txt";
	private static final String OFFICER_FILE = "./src/bto/data/Officer List.txt";
	private static final String MANAGER_FILE = "./src/bto/data/Manager List.txt";
 
 private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
 
 // User loading methods
 public List<User> loadAllUsers() {
     List<User> allUsers = new ArrayList<>();
     
     // Load users of each type
     List<Applicant> applicants = loadApplicants();
     List<HDBOfficer> officers = loadOfficers();
     List<HDBManager> managers = loadManagers();
     
     // Add all users to the combined list
     allUsers.addAll(applicants);
     allUsers.addAll(officers);
     allUsers.addAll(managers);
     
     return allUsers;
 }
 
 public List<Applicant> loadApplicants() {
     List<Applicant> applicants = new ArrayList<>();
     
     try (BufferedReader reader = new BufferedReader(new FileReader(APPLICANT_FILE))) {
         String line;
         // Skip header line
         reader.readLine();
         
         while ((line = reader.readLine()) != null) {
             if (line.trim().isEmpty()) continue;
             
             String[] parts = line.split("\\t");
             if (parts.length < 5) continue;
             
             String name = parts[0].trim();
             String nric = parts[1].trim();
             int age = Integer.parseInt(parts[2].trim());
             MaritalStatus maritalStatus = convertToMaritalStatus(parts[3].trim());
             String password = parts[4].trim();
             
             Applicant applicant = new Applicant(nric, password, age, maritalStatus, name);
             
             applicants.add(applicant);
         }
         
     } catch (IOException e) {
         System.out.println("Warning: Failed to load applicants data. " + e.getMessage());
     }
     
     return applicants;
 }
 
 public List<HDBOfficer> loadOfficers() {
     List<HDBOfficer> officers = new ArrayList<>();
     
     try (BufferedReader reader = new BufferedReader(new FileReader(OFFICER_FILE))) {
         String line;
         // Skip header line
         reader.readLine();
         
         while ((line = reader.readLine()) != null) {
             if (line.trim().isEmpty()) continue;
             
             String[] parts = line.split("\\t");
             if (parts.length < 5) continue;
             
             String name = parts[0].trim();
             String nric = parts[1].trim();
             int age = Integer.parseInt(parts[2].trim());
             MaritalStatus maritalStatus = convertToMaritalStatus(parts[3].trim());
             String password = parts[4].trim();
             
             HDBOfficer officer = new HDBOfficer(nric, password, age, maritalStatus, name);
             
             officers.add(officer);
         }
         
     } catch (IOException e) {
         System.out.println("Warning: Failed to load officers data. " + e.getMessage());
     }
     
     return officers;
 }
 
 public List<HDBManager> loadManagers() {
     List<HDBManager> managers = new ArrayList<>();
     
     try (BufferedReader reader = new BufferedReader(new FileReader(MANAGER_FILE))) {
         String line;
         // Skip header line
         reader.readLine();
         
         while ((line = reader.readLine()) != null) {
             if (line.trim().isEmpty()) continue;
             
             String[] parts = line.split("\\t");
             if (parts.length < 5) continue;
             
             String name = parts[0].trim();
             String nric = parts[1].trim();
             int age = Integer.parseInt(parts[2].trim());
             MaritalStatus maritalStatus = convertToMaritalStatus(parts[3].trim());
             String password = parts[4].trim();
             
             HDBManager manager = new HDBManager(nric, password, age, maritalStatus, name);
             
             managers.add(manager);
         }
         
     } catch (IOException e) {
         System.out.println("Warning: Failed to load managers data. " + e.getMessage());
     }
     
     return managers;
 }

 // Project loading method
 public List<Project> loadProjects(List<HDBOfficer> loadedOfficers, List<HDBManager> loadedManagers) {
	    List<Project> projects = new ArrayList<>();
	    Map<String, HDBManager> managerMap = createManagerMap(loadedManagers);
	    Map<String, HDBOfficer> officerMap = createOfficerMap(loadedOfficers);
     
     try (BufferedReader reader = new BufferedReader(new FileReader(PROJECT_FILE))) {
         String line;
         // Skip header line
         reader.readLine();
         
         while ((line = reader.readLine()) != null) {
             if (line.trim().isEmpty()) continue;
             
             String[] parts = line.split("\\t");
             if (parts.length < 12) continue;
             
             String projectName = parts[0].trim();
             String neighborhood = parts[1].trim();
             
             // Parse flat types and units
             FlatType type1 = convertToFlatType(parts[2].trim());
             int units1 = Integer.parseInt(parts[3].trim());
             int price1 = Integer.parseInt(parts[4].trim());
             
             FlatType type2 = convertToFlatType(parts[5].trim());
             int units2 = Integer.parseInt(parts[6].trim());
             int price2 = Integer.parseInt(parts[7].trim());
             
             // Parse dates
             Date openDate = null;
             Date closeDate = null;
             try {
                 openDate = DATE_FORMAT.parse(parts[8].trim());
                 closeDate = DATE_FORMAT.parse(parts[9].trim());
             } catch (ParseException e) {
                 System.out.println("Warning: Failed to parse date for project " + projectName + ". " + e.getMessage());
                 continue;
             }
             
             // Parse manager and officer assignments
             String managerName = parts[10].trim();
             int officerSlots = Integer.parseInt(parts[11].trim());
             
             String officersString = parts.length > 12 ? parts[12].trim() : "";
             String[] officerNames = officersString.replace("\"", "").split(",");
             
             // Create the project
             Project project = new Project();
             project.setProjectName(projectName);
             project.setNeighborhood(neighborhood);
             project.setApplicationOpenDate(openDate);
             project.setApplicationCloseDate(closeDate);
             project.setAvailableHDBOfficerSlots(officerSlots);
             
             // Set flat type units
             Map<FlatType, Integer> flatTypeUnits = new HashMap<>();
             flatTypeUnits.put(type1, units1);
             flatTypeUnits.put(type2, units2);
             project.setFlatTypeUnits(flatTypeUnits);
             
             // Set visibility (default to false for now)
             project.setVisible(true);
             
             // Set manager
             HDBManager manager = findManagerByName(managerName, managerMap);
             if (manager != null) {
                 project.setManagerInCharge(manager);
                 
                 // Also set this project as the managed project for the manager
                 manager.setManagedProject(project);
             }
             
          // Inside the loadProjects method, add these debug lines:
             System.out.println("Loading projects...");
             System.out.println("Available officers: " + officerMap.keySet());

             // Inside the officer assignment loop:
             for (String officerName : officerNames) {
                 System.out.println("Processing officer name: '" + officerName.trim() + "'");
                 if (officerName.trim().isEmpty()) {
                     System.out.println("Empty officer name, skipping");
                     continue;
                 }
                 
                 HDBOfficer officer = findOfficerByName(officerName.trim(), officerMap);
                 System.out.println("Found officer: " + (officer != null ? officer.getName() : "null"));
                 
                 if (officer != null) {
                     System.out.println("Adding project " + project.getProjectName() + " to officer " + officer.getName());
                     // Rest of the code...
                 }
             }
             // Assign officers
          // In FileManager's loadProjects method:
          // When processing assigned officers:
          for (String officerName : officerNames) {
              if (officerName.trim().isEmpty()) continue;
              
              HDBOfficer officer = findOfficerByName(officerName.trim(), officerMap);
              if (officer != null) {
                  // Add project to officer's assigned projects
                  officer.addAssignedProject(project);
                  
                  // Create officer registration (with APPROVED status)
                  OfficerRegistration registration = new OfficerRegistration(officer, project);
                  registration.setRegistrationStatus("APPROVED");
                  
                  // Add registration to both officer and project
                  officer.addRegistration(registration);
                  project.addOfficerRegistration(registration);
                  
                  // Reduce available slots (do this for each assigned officer)
                  project.setAvailableHDBOfficerSlots(project.getAvailableHDBOfficerSlots() - 1);
              }
          }
             
             projects.add(project);
         }
         
     } catch (IOException e) {
         System.out.println("Warning: Failed to load projects data. " + e.getMessage());
     }
     
     return projects;
 }
 
 // Application loading method - placeholder, implement when you have the file
 public List<ProjectApplication> loadApplications() {
     List<ProjectApplication> applications = new ArrayList<>();
     
     // Implementation would depend on the format of your Application List.txt file
     // For now, this returns an empty list
     
     return applications;
 }
 
 // Enquiry loading method - placeholder, implement when you have the file
 public List<Enquiry> loadEnquiries() {
     List<Enquiry> enquiries = new ArrayList<>();
     
     // Implementation would depend on the format of your Enquiry List.txt file
     // For now, this returns an empty list
     
     return enquiries;
 }
 
 // Save methods
 public boolean saveApplicants(List<Applicant> applicants) {
     try (BufferedWriter writer = new BufferedWriter(new FileWriter(APPLICANT_FILE))) {
         // Write header
         writer.write("Name\tNRIC\tAge\tMarital Status\tPassword");
         writer.newLine();
         
         // Write data
         for (Applicant applicant : applicants) {
             writer.write(String.format("%s\t%s\t%d\t%s\t%s",
                 applicant.getName(),
                 applicant.getNric(),
                 applicant.getAge(),
                 applicant.getMaritalStatus().toString(),
                 applicant.getPassword()));
             writer.newLine();
         }
         
         return true;
     } catch (IOException e) {
         System.out.println("Error: Failed to save applicants data. " + e.getMessage());
         return false;
     }
 }
 
 public boolean saveOfficers(List<HDBOfficer> officers) {
     try (BufferedWriter writer = new BufferedWriter(new FileWriter(OFFICER_FILE))) {
         // Write header
         writer.write("Name\tNRIC\tAge\tMarital Status\tPassword");
         writer.newLine();
         
         // Write data
         for (HDBOfficer officer : officers) {
             writer.write(String.format("%s\t%s\t%d\t%s\t%s",
                 officer.getName(),
                 officer.getNric(),
                 officer.getAge(),
                 officer.getMaritalStatus().toString(),
                 officer.getPassword()));
             writer.newLine();
         }
         
         return true;
     } catch (IOException e) {
         System.out.println("Error: Failed to save officers data. " + e.getMessage());
         return false;
     }
 }
 
 public boolean saveManagers(List<HDBManager> managers) {
     try (BufferedWriter writer = new BufferedWriter(new FileWriter(MANAGER_FILE))) {
         // Write header
         writer.write("Name\tNRIC\tAge\tMarital Status\tPassword");
         writer.newLine();
         
         // Write data
         for (HDBManager manager : managers) {
             writer.write(String.format("%s\t%s\t%d\t%s\t%s",
                 manager.getName(),
                 manager.getNric(),
                 manager.getAge(),
                 manager.getMaritalStatus().toString(),
                 manager.getPassword()));
             writer.newLine();
         }
         
         return true;
     } catch (IOException e) {
         System.out.println("Error: Failed to save managers data. " + e.getMessage());
         return false;
     }
 }
 
 public boolean saveProjects(List<Project> projects) {
     try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROJECT_FILE))) {
         // Write header
         writer.write("Project Name\tNeighborhood\tType 1\tNumber of units for Type 1\tSelling price for Type 1\tType 2\tNumber of units for Type 2\tSelling price for Type 2\tApplication opening date\tApplication closing date\tManager\tOfficer Slot\tOfficer");
         writer.newLine();
         
         // Write data
         for (Project project : projects) {
             // Extract flat types and units
             Map<FlatType, Integer> flatTypeUnits = project.getFlatTypeUnits();
             FlatType[] flatTypes = flatTypeUnits.keySet().toArray(new FlatType[0]);
             
             FlatType type1 = flatTypes.length > 0 ? flatTypes[0] : FlatType.TWO_ROOM;
             int units1 = flatTypeUnits.getOrDefault(type1, 0);
             int price1 = 0; // Placeholder, as price isn't stored in your current model
             
             FlatType type2 = flatTypes.length > 1 ? flatTypes[1] : FlatType.THREE_ROOM;
             int units2 = flatTypeUnits.getOrDefault(type2, 0);
             int price2 = 0; // Placeholder
             
             // Get manager name
             String managerName = "Unknown";
             if (project.getManagerInCharge() != null) {
                 managerName = project.getManagerInCharge().getName();
             }
             
             // Get assigned officers
             StringBuilder officerNames = new StringBuilder();
             boolean first = true;
             
             for (OfficerRegistration reg : project.getOfficerRegistrations()) {
                 if ("APPROVED".equals(reg.getRegistrationStatus())) {
                     if (!first) {
                         officerNames.append(",");
                     }
                     officerNames.append(reg.getHdbOfficer().getName());
                     first = false;
                 }
             }
             
             // Format and write the project data
             writer.write(String.format("%s\t%s\t%s\t%d\t%d\t%s\t%d\t%d\t%s\t%s\t%s\t%d\t\"%s\"",
                 project.getProjectName(),
                 project.getNeighborhood(),
                 formatFlatType(type1),
                 units1,
                 price1,
                 formatFlatType(type2),
                 units2,
                 price2,
                 DATE_FORMAT.format(project.getApplicationOpenDate()),
                 DATE_FORMAT.format(project.getApplicationCloseDate()),
                 managerName,
                 project.getAvailableHDBOfficerSlots(),
                 officerNames.toString()));
             writer.newLine();
         }
         
         return true;
     } catch (IOException e) {
         System.out.println("Error: Failed to save projects data. " + e.getMessage());
         return false;
     }
 }
 
 // Helper methods
 private MaritalStatus convertToMaritalStatus(String status) {
     if (status.equalsIgnoreCase("Married")) {
         return MaritalStatus.MARRIED;
     } else {
         return MaritalStatus.SINGLE;
     }
 }
 
 private FlatType convertToFlatType(String type) {
     if (type.contains("2-Room") || type.equalsIgnoreCase("TWO_ROOM")) {
         return FlatType.TWO_ROOM;
     } else {
         return FlatType.THREE_ROOM;
     }
 }
 
 private String formatFlatType(FlatType type) {
     switch (type) {
         case TWO_ROOM:
             return "2-Room";
         case THREE_ROOM:
             return "3-Room";
         default:
             return type.toString();
     }
 }
 
 private Map<String, HDBManager> createManagerMap(List<HDBManager> managers) {
     Map<String, HDBManager> map = new HashMap<>();
     
     for (HDBManager manager : managers) {
         // Use name as the key
         map.put(manager.getName(), manager);
         // Also map by NRIC for backup lookup
         map.put(manager.getNric(), manager);
     }
     
     return map;
 }
 
 private Map<String, HDBOfficer> createOfficerMap(List<HDBOfficer> officers) {
     Map<String, HDBOfficer> map = new HashMap<>();
     
     for (HDBOfficer officer : officers) {
         // Use name as the key
         map.put(officer.getName(), officer);
         // Also map by NRIC for backup lookup
         map.put(officer.getNric(), officer);
     }
     
     return map;
 }
 
 // These methods find users by name
 private HDBManager findManagerByName(String name, Map<String, HDBManager> managerMap) {
     // Try to find by name
     HDBManager manager = managerMap.get(name);
     
     // If not found and we have managers, return the first one
     if (manager == null && !managerMap.isEmpty()) {
         // Get any manager that isn't keyed by an NRIC (which starts with S or T)
         for (Map.Entry<String, HDBManager> entry : managerMap.entrySet()) {
             if (!entry.getKey().matches("^[ST].*")) {
                 return entry.getValue();
             }
         }
         
         // If all else fails, return any manager
         return managerMap.values().iterator().next();
     }
     
     return manager;
 }
 
 private HDBOfficer findOfficerByName(String name, Map<String, HDBOfficer> officerMap) {
     // Try to find by name
     HDBOfficer officer = officerMap.get(name);
     
     // If not found and we have officers, return the first one
     if (officer == null && !officerMap.isEmpty()) {
         // Get any officer that isn't keyed by an NRIC (which starts with S or T)
         for (Map.Entry<String, HDBOfficer> entry : officerMap.entrySet()) {
             if (!entry.getKey().matches("^[ST].*")) {
                 return entry.getValue();
             }
         }
         
         // If all else fails, return any officer
         return officerMap.values().iterator().next();
     }
     
     return officer;
 }
}