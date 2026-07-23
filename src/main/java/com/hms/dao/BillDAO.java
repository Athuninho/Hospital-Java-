package com.hms.dao;

import com.hms.model.Bill;
import com.hms.model.BillItem;
import com.hms.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {

    public int addBill(Bill bill) throws SQLException {
        String billQuery = "INSERT INTO bills (patient_id, appointment_id, subtotal, tax, discount, total, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String itemQuery = "INSERT INTO bill_items (bill_id, description, quantity, unit_price) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            int generatedId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(billQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, bill.getPatientId());
                pstmt.setInt(2, bill.getAppointmentId());
                pstmt.setDouble(3, bill.getSubtotal());
                pstmt.setDouble(4, bill.getTax());
                pstmt.setDouble(5, bill.getDiscount());
                pstmt.setDouble(6, bill.getTotal());
                pstmt.setString(7, bill.getStatus());
                
                pstmt.executeUpdate();
                
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
            
            if (generatedId != -1 && bill.getItems() != null) {
                try (PreparedStatement itemStmt = conn.prepareStatement(itemQuery)) {
                    for (BillItem item : bill.getItems()) {
                        itemStmt.setInt(1, generatedId);
                        itemStmt.setString(2, item.getDescription());
                        itemStmt.setInt(3, item.getQuantity());
                        itemStmt.setDouble(4, item.getUnitPrice());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }
            }
            
            conn.commit();
            return generatedId;
            
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    public List<Bill> getBillsByPatient(int patientId) throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT * FROM bills WHERE patient_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
             pstmt.setInt(1, patientId);
             try (ResultSet rs = pstmt.executeQuery()) {
                 while (rs.next()) {
                     Bill bill = extractBillFromResultSet(rs);
                     bills.add(bill);
                 }
             }
        }
        return bills;
    }

    private Bill extractBillFromResultSet(ResultSet rs) throws SQLException {
        return Bill.builder()
                .id(rs.getInt("id"))
                .patientId(rs.getInt("patient_id"))
                .appointmentId(rs.getInt("appointment_id"))
                .subtotal(rs.getDouble("subtotal"))
                .tax(rs.getDouble("tax"))
                .discount(rs.getDouble("discount"))
                .total(rs.getDouble("total"))
                .status(rs.getString("status"))
                .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .build();
    }
}
