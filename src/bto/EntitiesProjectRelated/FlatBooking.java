package bto.EntitiesProjectRelated;

import java.util.Date;
import bto.Entities.*;
import bto.Enums.*;

public class FlatBooking {
 private Applicant applicant;
 private Project project;
 private FlatType flatType;
 private int flatId;  // New field to store the specific flat ID
 private Date bookingDate;
 
 // Constructors
 public FlatBooking() {
     this.bookingDate = new Date(); // Sets current date as booking date
 }
 
 public FlatBooking(Applicant applicant, Project project, FlatType flatType, int flatId) {
     this();
     this.applicant = applicant;
     this.project = project;
     this.flatType = flatType;
     this.flatId = flatId;
 }
 
 // Methods
 public String getBookingDetails() {
     StringBuilder details = new StringBuilder();
     details.append("Booking Information\n");
     details.append("===================\n");
     details.append("Applicant: ").append(applicant.getName()).append(" (").append(applicant.getNric()).append(")\n");
     details.append("Project: ").append(project.getProjectName()).append("\n");
     details.append("Flat Type: ").append(flatType).append("\n");
     details.append("Flat ID: ").append(flatId).append("\n");
     details.append("Booking Date: ").append(bookingDate).append("\n");
     
     return details.toString();
 }
 
 public String generateReceipt() {
     return getBookingDetails();
 }
 
 // Getters and Setters
 public Applicant getApplicant() {
     return applicant;
 }

 public void setApplicant(Applicant applicant) {
     this.applicant = applicant;
 }

 public Project getProject() {
     return project;
 }

 public void setProject(Project project) {
     this.project = project;
 }

 public FlatType getFlatType() {
     return flatType;
 }

 public void setFlatType(FlatType flatType) {
     this.flatType = flatType;
 }
 
 public int getFlatId() {
     return flatId;
 }
 
 public void setFlatId(int flatId) {
     this.flatId = flatId;
 }

 public Date getBookingDate() {
     return bookingDate;
 }

 public void setBookingDate(Date bookingDate) {
     this.bookingDate = bookingDate;
 }
}