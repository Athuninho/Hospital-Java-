package com.hms.dao;

import com.hms.model.Appointment;
import com.hms.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public void addAppointment(Appointment appointment) throws SQLException {
        String query = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setDate(3, java.sql.Date.valueOf(appointment.getAppointmentDate()));
            pstmt.setTime(4, java.sql.Time.valueOf(appointment.getAppointmentTime()));
            pstmt.setString(5, appointment.getStatus());
            
            pstmt.executeUpdate();
        }
    }

    public void updateAppointmentStatus(int id, String status) throws SQLException {
        String query = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }
    }
    
    public void deleteAppointment(int id) throws SQLException {
        String query = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Appointment> getAllAppointments() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT a.*, p.name as patient_name, d.name as doctor_name FROM appointments a " +
                       "JOIN patients p ON a.patient_id = p.id " +
                       "JOIN doctors d ON a.doctor_id = d.id ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                appointments.add(extractAppointmentFromResultSet(rs));
            }
        }
        return appointments;
    }
    
    public List<Appointment> getAppointmentsByDoctor(int doctorId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT a.*, p.name as patient_name, d.name as doctor_name FROM appointments a " +
                       "JOIN patients p ON a.patient_id = p.id " +
                       "JOIN doctors d ON a.doctor_id = d.id WHERE a.doctor_id = ? ORDER BY a.appointment_date, a.appointment_time";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
             pstmt.setInt(1, doctorId);
             try (ResultSet rs = pstmt.executeQuery()) {
                 while (rs.next()) {
                     appointments.add(extractAppointmentFromResultSet(rs));
                 }
             }
        }
        return appointments;
    }

    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        return Appointment.builder()
                .id(rs.getInt("id"))
                .patientId(rs.getInt("patient_id"))
                .doctorId(rs.getInt("doctor_id"))
                .appointmentDate(rs.getDate("appointment_date").toLocalDate())
                .appointmentTime(rs.getTime("appointment_time").toLocalTime())
                .status(rs.getString("status"))
                .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .patientName(rs.getString("patient_name"))
                .doctorName(rs.getString("doctor_name"))
                .build();
    }
}
