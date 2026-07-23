package com.hms.model;

public class BillItem {
    private int id;
    private int billId;
    private String description;
    private int quantity;
    private double unitPrice;

    public BillItem() {}

    public BillItem(int id, int billId, String description, int quantity, double unitPrice) {
        this.id = id;
        this.billId = billId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static BillItemBuilder builder() {
        return new BillItemBuilder();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public static class BillItemBuilder {
        private int id;
        private int billId;
        private String description;
        private int quantity;
        private double unitPrice;

        public BillItemBuilder id(int id) { this.id = id; return this; }
        public BillItemBuilder billId(int billId) { this.billId = billId; return this; }
        public BillItemBuilder description(String description) { this.description = description; return this; }
        public BillItemBuilder quantity(int quantity) { this.quantity = quantity; return this; }
        public BillItemBuilder unitPrice(double unitPrice) { this.unitPrice = unitPrice; return this; }

        public BillItem build() {
            return new BillItem(id, billId, description, quantity, unitPrice);
        }
    }
}
