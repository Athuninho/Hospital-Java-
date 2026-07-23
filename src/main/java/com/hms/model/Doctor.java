package com.hms.model;

import java.time.LocalDateTime;

public class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String contact;
    private double consultationFee;
    private String availableDays;
    private String availableHours;
    private LocalDateTime createdAt;

    public Doctor() {}

    public Doctor(int id, String name, String specialization, String contact, double consultationFee, String availableDays, String availableHours, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.contact = contact;
        this.consultationFee = consultationFee;
        this.availableDays = availableDays;
        this.availableHours = availableHours;
        this.createdAt = createdAt;
    }

    public static DoctorBuilder builder() {
        return new DoctorBuilder();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
    public String getAvailableDays() { return availableDays; }
    public void setAvailableDays(String availableDays) { this.availableDays = availableDays; }
    public String getAvailableHours() { return availableHours; }
    public void setAvailableHours(String availableHours) { this.availableHours = availableHours; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class DoctorBuilder {
        private int id;
        private String name;
        private String specialization;
        private String contact;
        private double consultationFee;
        private String availableDays;
        private String availableHours;
        private LocalDateTime createdAt;

        public DoctorBuilder id(int id) { this.id = id; return this; }
        public DoctorBuilder name(String name) { this.name = name; return this; }
        public DoctorBuilder specialization(String specialization) { this.specialization = specialization; return this; }
        public DoctorBuilder contact(String contact) { this.contact = contact; return this; }
        public DoctorBuilder consultationFee(double consultationFee) { this.consultationFee = consultationFee; return this; }
        public DoctorBuilder availableDays(String availableDays) { this.availableDays = availableDays; return this; }
        public DoctorBuilder availableHours(String availableHours) { this.availableHours = availableHours; return this; }
        public DoctorBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Doctor build() {
            return new Doctor(id, name, specialization, contact, consultationFee, availableDays, availableHours, createdAt);
        }
    }
}
