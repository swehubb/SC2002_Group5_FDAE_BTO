package bto.Controllers;

import bto.Entities.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class AuthController {
 private Map<String, User> users; // Map of NRIC to User
 
 // Constructor
 public AuthController() {
     users = new HashMap<>();
 }
 
 // Methods
 public boolean validateNRIC(String nric) {
     // NRIC format: S/T followed by 7 digits and a letter
     Pattern pattern = Pattern.compile("^[ST]\\d{7}[A-Z]$");
     return pattern.matcher(nric).matches();
 }
 
 public boolean validatePassword(String password) {
     // Password requirements: at least 8 characters
     return password != null && password.length() >= 8;
 }
 
 public boolean validateName(String name) {
     // Name requirements: non-empty and contains only letters, spaces, and hyphens
     return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z\\s\\-]+$");
 }
 
 public User loginUser(String nric, String password) {
     if (!validateNRIC(nric)) {
         return null;
     }
     
     User user = users.get(nric);
     
     if (user != null && user.getPassword().equals(password)) {
         return user;
     }
     
     return null;
 }
 
 public boolean updateNewPassword(User user, String oldPassword, String newPassword) {
     if (user != null && validatePassword(newPassword)) {
         return user.changePassword(oldPassword, newPassword);
     }
     return false;
 }
 
 // Methods to get all users of a specific type
 public List<Applicant> getAllApplicants() {
     List<Applicant> applicants = new ArrayList<>();
     
     for (User user : users.values()) {
         if (user instanceof Applicant && !(user instanceof HDBOfficer)) {
             applicants.add((Applicant) user);
         }
     }
     
     return applicants;
 }
 
 public List<HDBOfficer> getAllOfficers() {
     List<HDBOfficer> officers = new ArrayList<>();
     
     for (User user : users.values()) {
         if (user instanceof HDBOfficer) {
             officers.add((HDBOfficer) user);
         }
     }
     
     return officers;
 }
 
 public List<HDBManager> getAllManagers() {
     List<HDBManager> managers = new ArrayList<>();
     
     for (User user : users.values()) {
         if (user instanceof HDBManager) {
             managers.add((HDBManager) user);
         }
     }
     
     return managers;
 }
 
 // Method to add a new user
 public boolean addUser(User user) {
     if (user != null && validateNRIC(user.getNric()) && validatePassword(user.getPassword()) && validateName(user.getName())) {
         if (!users.containsKey(user.getNric())) {
             users.put(user.getNric(), user);
             return true;
         }
     }
     return false;
 }
 
 // Method to remove a user
 public boolean removeUser(String nric) {
     if (users.containsKey(nric)) {
         users.remove(nric);
         return true;
     }
     return false;
 }
 
 // Method to find a user by name
 public User findUserByName(String name) {
     for (User user : users.values()) {
         if (user.getName().equalsIgnoreCase(name)) {
             return user;
         }
     }
     return null;
 }
 
 // Getter and setter for the users map
 public Map<String, User> getUsers() {
     return users;
 }
 
 public void setUsers(Map<String, User> users) {
     this.users = users;
 }
}