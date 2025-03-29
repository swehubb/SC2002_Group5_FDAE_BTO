package bto.Entities;
import bto.Enums.MaritalStatus;

public class User {
	 private String nric;
	 private String password;
	 private int age;
	 private MaritalStatus maritalStatus;
	 private String name; 
	 
	 // Constructors
	 public User() {}
	 
	 public User(String nric, String password, int age, MaritalStatus maritalStatus, String name) {
	     this.nric = nric;
	     this.password = password;
	     this.age = age;
	     this.maritalStatus = maritalStatus;
	     this.name = name;
	 }
	 
	 // Methods
	 public boolean login() {
	     // Implementation for login logic
	     return false; // Placeholder
	 }
	 
	 public boolean changePassword(String oldPassword, String newPassword) {
	     // Implementation for password change
	     if (this.password.equals(oldPassword)) {
	         this.password = newPassword;
	         return true;
	     }
	     return false;
	 }
	 
	 public String getDetails() {
	     return "Name: " + name + ", NRIC: " + nric + ", Age: " + age + ", Marital Status: " + maritalStatus;
	 }
	 
	 // Getters and Setters
	 public String getNric() {
	     return nric;
	 }
	
	 public void setNric(String nric) {
	     this.nric = nric;
	 }
	
	 public String getPassword() {
	     return password;
	 }
	
	 public void setPassword(String password) {
	     this.password = password;
	 }
	
	 public int getAge() {
	     return age;
	 }
	
	 public void setAge(int age) {
	     this.age = age;
	 }
	
	 public MaritalStatus getMaritalStatus() {
	     return maritalStatus;
	 }
	
	 public void setMaritalStatus(MaritalStatus maritalStatus) {
	     this.maritalStatus = maritalStatus;
	 }
	 
	 public String getName() {
	     return name;
	 }
	 
	 public void setName(String name) {
	     this.name = name;
	 }
}