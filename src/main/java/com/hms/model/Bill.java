package com.hms.model;

import java.time.LocalDateTime;
import java.util.List;

public class Bill {
    private int id;
    private int patientId;
    private int appointmentId;
    private double subtotal;
    private double tax;
    private double discount;
    private double total;
    private String status;
    private LocalDateTime createdAt;
    private List<BillItem> items;

    public Bill() {}

    public Bill(int id, int patientId, int appointmentId, double subtotal, double tax, double discount, double total, String status, LocalDateTime createdAt, List<BillItem> items) {
        this.id = id;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.subtotal = subtotal;
        this.tax = tax;
        this.discount = discount;
        this.total = total;
        this.status = status;
        this.createdAt = createdAt;
        this.items = items;
    }

    public static BillBuilder builder() {
        return new BillBuilder();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<BillItem> getItems() { return items; }
    public void setItems(List<BillItem> items) { this.items = items; }

    public static class BillBuilder {
        private int id;
        private int patientId;
        private int appointmentId;
        private double subtotal;
        private double tax;
        private double discount;
        private double total;
        private String status;
        private LocalDateTime createdAt;
        private List<BillItem> items;

        public BillBuilder id(int id) { this.id = id; return this; }
        public BillBuilder patientId(int patientId) { this.patientId = patientId; return this; }
        public BillBuilder appointmentId(int appointmentId) { this.appointmentId = appointmentId; return this; }
        public BillBuilder subtotal(double subtotal) { this.subtotal = subtotal; return this; }
        public BillBuilder tax(double tax) { this.tax = tax; return this; }
        public BillBuilder discount(double discount) { this.discount = discount; return this; }
        public BillBuilder total(double total) { this.total = total; return this; }
        public BillBuilder status(String status) { this.status = status; return this; }
        public BillBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public BillBuilder items(List<BillItem> items) { this.items = items; return this; }

        public Bill build() {
            return new Bill(id, patientId, appointmentId, subtotal, tax, discount, total, status, createdAt, items);
        }
    }
}
