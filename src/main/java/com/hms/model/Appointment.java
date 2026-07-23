package com.hms.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private int patientId;
    private int doctorId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private LocalDateTime createdAt;
    private String patientName;
    private String doctorName;

    public Appointment() {}

    public Appointment(int id, int patientId, int doctorId, LocalDate appointmentDate, LocalTime appointmentTime, String status, LocalDateTime createdAt, String patientName, String doctorName) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.createdAt = createdAt;
        this.patientName = patientName;
        this.doctorName = doctorName;
    }

    public static AppointmentBuilder builder() {
        return new AppointmentBuilder();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public static class AppointmentBuilder {
        private int id;
        private int patientId;
        private int doctorId;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private String status;
        private LocalDateTime createdAt;
        private String patientName;
        private String doctorName;

        public AppointmentBuilder id(int id) { this.id = id; return this; }
        public AppointmentBuilder patientId(int patientId) { this.patientId = patientId; return this; }
        public AppointmentBuilder doctorId(int doctorId) { this.doctorId = doctorId; return this; }
        public AppointmentBuilder appointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; return this; }
        public AppointmentBuilder appointmentTime(LocalTime appointmentTime) { this.appointmentTime = appointmentTime; return this; }
        public AppointmentBuilder status(String status) { this.status = status; return this; }
        public AppointmentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public AppointmentBuilder patientName(String patientName) { this.patientName = patientName; return this; }
        public AppointmentBuilder doctorName(String doctorName) { this.doctorName = doctorName; return this; }

        public Appointment build() {
            return new Appointment(id, patientId, doctorId, appointmentDate, appointmentTime, status, createdAt, patientName, doctorName);
        }
    }
}
