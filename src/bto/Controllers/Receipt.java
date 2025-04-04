package bto.EntitiesProjectRelated;

import java.util.Date;
import bto.Entities.Applicant;
import bto.Entities.HDBOfficer;

/**
 * Entity class representing a receipt for a flat booking.
 */
public class Receipt {
    private String receiptId;
    private String applicantNric;
    private String officerNric;
    private String projectName;
    private String flatType;
    private int flatId;
    private Date generationDate;
    private String content; // Optional: store the formatted receipt content

    /**
     * Default constructor
     */
    public Receipt() {
        this.generationDate = new Date();
    }

    /**
     * Constructor with essential parameters
     */
    public Receipt(String applicantNric, String officerNric, String projectName, String flatType, int flatId) {
        this();
        this.applicantNric = applicantNric;
        this.officerNric = officerNric;
        this.projectName = projectName;
        this.flatType = flatType;
        this.flatId = flatId;
        this.receiptId = generateReceiptId(applicantNric);
    }

    /**
     * Generate a unique receipt ID
     */
    private String generateReceiptId(String nric) {
        // Create a receipt ID using format: REC-[First 3 chars of NRIC]-[Timestamp]
        String prefix = nric.substring(0, Math.min(3, nric.length()));
        long timestamp = System.currentTimeMillis();
        return "REC-" + prefix + "-" + timestamp;
    }

    // Getters and setters
    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

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

    public Date getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(Date generationDate) {
        this.generationDate = generationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
