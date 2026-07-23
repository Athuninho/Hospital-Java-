package com.hms.model;

import java.time.LocalDateTime;

public class MedicalHistory {
    private int id;
    private int patientId;
    private int appointmentId;
    private String diagnoses;
    private String prescriptions;
    private String allergies;
    private String testResults;
    private LocalDateTime recordedAt;

    public MedicalHistory() {}

    public MedicalHistory(int id, int patientId, int appointmentId, String diagnoses, String prescriptions, String allergies, String testResults, LocalDateTime recordedAt) {
        this.id = id;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.diagnoses = diagnoses;
        this.prescriptions = prescriptions;
        this.allergies = allergies;
        this.testResults = testResults;
        this.recordedAt = recordedAt;
    }

    public static MedicalHistoryBuilder builder() {
        return new MedicalHistoryBuilder();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public String getDiagnoses() { return diagnoses; }
    public void setDiagnoses(String diagnoses) { this.diagnoses = diagnoses; }
    public String getPrescriptions() { return prescriptions; }
    public void setPrescriptions(String prescriptions) { this.prescriptions = prescriptions; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getTestResults() { return testResults; }
    public void setTestResults(String testResults) { this.testResults = testResults; }
    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public static class MedicalHistoryBuilder {
        private int id;
        private int patientId;
        private int appointmentId;
        private String diagnoses;
        private String prescriptions;
        private String allergies;
        private String testResults;
        private LocalDateTime recordedAt;

        public MedicalHistoryBuilder id(int id) { this.id = id; return this; }
        public MedicalHistoryBuilder patientId(int patientId) { this.patientId = patientId; return this; }
        public MedicalHistoryBuilder appointmentId(int appointmentId) { this.appointmentId = appointmentId; return this; }
        public MedicalHistoryBuilder diagnoses(String diagnoses) { this.diagnoses = diagnoses; return this; }
        public MedicalHistoryBuilder prescriptions(String prescriptions) { this.prescriptions = prescriptions; return this; }
        public MedicalHistoryBuilder allergies(String allergies) { this.allergies = allergies; return this; }
        public MedicalHistoryBuilder testResults(String testResults) { this.testResults = testResults; return this; }
        public MedicalHistoryBuilder recordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; return this; }

        public MedicalHistory build() {
            return new MedicalHistory(id, patientId, appointmentId, diagnoses, prescriptions, allergies, testResults, recordedAt);
        }
    }
}
