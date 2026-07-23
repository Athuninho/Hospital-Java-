package com.hms.model;

import java.time.LocalDateTime;

public class Patient {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String contact;
    private String address;
    private String bloodGroup;
    private String emergencyContact;
    private LocalDateTime createdAt;

    public Patient() {}

    public Patient(int id, String name, int age, String gender, String contact, String address, String bloodGroup, String emergencyContact, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contact = contact;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
        this.createdAt = createdAt;
    }

    public static PatientBuilder builder() {
        return new PatientBuilder();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class PatientBuilder {
        private int id;
        private String name;
        private int age;
        private String gender;
        private String contact;
        private String address;
        private String bloodGroup;
        private String emergencyContact;
        private LocalDateTime createdAt;

        public PatientBuilder id(int id) { this.id = id; return this; }
        public PatientBuilder name(String name) { this.name = name; return this; }
        public PatientBuilder age(int age) { this.age = age; return this; }
        public PatientBuilder gender(String gender) { this.gender = gender; return this; }
        public PatientBuilder contact(String contact) { this.contact = contact; return this; }
        public PatientBuilder address(String address) { this.address = address; return this; }
        public PatientBuilder bloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; return this; }
        public PatientBuilder emergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; return this; }
        public PatientBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Patient build() {
            return new Patient(id, name, age, gender, contact, address, bloodGroup, emergencyContact, createdAt);
        }
    }
}
