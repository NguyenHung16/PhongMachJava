package com.vnh.dto;

public class PrescriptionDTO {
    private String medicineName;
    private Integer quantity;
    private String instruction;
    private String notes;

    // Getters and Setters
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}