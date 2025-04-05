package bto.EntitiesProjectRelated;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Receipt {
	private String applicantNric;
	private String officerNric;
	private String projectName;
	private String flatType;
	private int flatId;
	private Date receiptDate;
	private String content;
	
	public Receipt() {
	    this.receiptDate = new Date();
	}

	public Receipt(String applicantNric, String officerNric, String projectName, String flatType, int flatId) {
	    this.applicantNric = applicantNric;
	    this.officerNric = officerNric;
	    this.projectName = projectName;
	    this.flatType = flatType;
	    this.flatId = flatId;
	    this.receiptDate = new Date();
	    
	    // Automatically generate content when creating the receipt
	    generateContent();
	}

	// Getters and Setters
	public String getApplicantNric() {
	    return applicantNric;
	}

	public void setApplicantNric(String applicantNric) {
	    this.applicantNric = applicantNric;
	}

	public String getOfficerNric() {
	    return officerNric;
	}

	public void setOfficerNric(String officerNric) {
	    this.officerNric = officerNric;
	}

	public String getProjectName() {
	    return projectName;
	}

	public void setProjectName(String projectName) {
	    this.projectName = projectName;
	}

	public String getFlatType() {
	    return flatType;
	}

	public void setFlatType(String flatType) {
	    this.flatType = flatType;
	}

	public int getFlatId() {
	    return flatId;
	}

	public void setFlatId(int flatId) {
	    this.flatId = flatId;
	}

	public Date getReceiptDate() {
	    return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
	    this.receiptDate = receiptDate;
	}

	public String getContent() {
	    return content;
	}

	public void setContent(String content) {
	    this.content = content;
	}

	// Method to generate a printable version of the receipt
	public void generateContent() {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	    
	    StringBuilder printableReceipt = new StringBuilder();
	    
	    printableReceipt.append("HDB BOOKING RECEIPT\n");
	    printableReceipt.append("====================\n");
	    printableReceipt.append("Receipt Date: ").append(dateFormat.format(receiptDate)).append("\n\n");
	    
	    printableReceipt.append("Applicant Details:\n");
	    printableReceipt.append("NRIC: ").append(applicantNric).append("\n\n");
	    
	    printableReceipt.append("Project Details:\n");
	    printableReceipt.append("Project: ").append(projectName).append("\n");
	    printableReceipt.append("Flat Type: ").append(flatType).append("\n");
	    printableReceipt.append("Flat ID: ").append(flatId).append("\n\n");
	    
	    printableReceipt.append("Processed By:\n");
	    printableReceipt.append("Officer NRIC: ").append(officerNric).append("\n\n");
	    
	    printableReceipt.append("Important Information:\n");
	    printableReceipt.append("1. Please retain this receipt for your records.\n");
	    printableReceipt.append("2. For enquiries, contact HDB Customer Service.\n");
	    
	    // Set the generated content
	    this.content = printableReceipt.toString();
	}
	
}
