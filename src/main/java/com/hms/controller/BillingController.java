package com.hms.controller;

import com.hms.dao.AppointmentDAO;
import com.hms.dao.BillDAO;
import com.hms.dao.PatientDAO;
import com.hms.model.Appointment;
import com.hms.model.Bill;
import com.hms.model.BillItem;
import com.hms.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BillingController implements Initializable {

    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private ComboBox<Appointment> appointmentComboBox;
    @FXML private TextField taxField;
    @FXML private TextField discountField;
    @FXML private ComboBox<String> statusComboBox;
    
    // Line Item fields
    @FXML private TextField descField;
    @FXML private TextField qtyField;
    @FXML private TextField priceField;
    
    @FXML private TableView<BillItem> itemTable;
    @FXML private TableColumn<BillItem, String> descCol;
    @FXML private TableColumn<BillItem, Integer> qtyCol;
    @FXML private TableColumn<BillItem, Double> priceCol;
    @FXML private TableColumn<BillItem, Double> totalCol;
    
    @FXML private Label subtotalLabel;
    @FXML private Label totalLabel;
    
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private BillDAO billDAO;
    
    private ObservableList<BillItem> itemList;
    private ObservableList<Appointment> allAppointments;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        patientDAO = new PatientDAO();
        appointmentDAO = new AppointmentDAO();
        billDAO = new BillDAO();
        itemList = FXCollections.observableArrayList();
        
        statusComboBox.getItems().addAll("Unpaid", "Partial", "Paid");
        statusComboBox.setValue("Unpaid");
        
        taxField.setText("0");
        discountField.setText("0");
        
        setupTable();
        loadPatients();
        setupConverters();
        
        patientComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterAppointments(newVal.getId());
            }
        });
        
        taxField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotals());
        discountField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotals());
    }

    private void setupTable() {
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        
        // Calculate row total
        totalCol.setCellValueFactory(cellData -> {
            BillItem item = cellData.getValue();
            double rowTotal = item.getQuantity() * item.getUnitPrice();
            return new javafx.beans.property.SimpleDoubleProperty(rowTotal).asObject();
        });
        
        itemTable.setItems(itemList);
    }

    private void setupConverters() {
        patientComboBox.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient p) {
                return p == null ? null : p.getName() + " (ID: " + p.getId() + ")";
            }
            @Override
            public Patient fromString(String s) { return null; }
        });

        appointmentComboBox.setConverter(new StringConverter<Appointment>() {
            @Override
            public String toString(Appointment a) {
                return a == null ? null : a.getAppointmentDate() + " " + a.getAppointmentTime() + " - " + a.getDoctorName();
            }
            @Override
            public Appointment fromString(String s) { return null; }
        });
    }

    private void loadPatients() {
        try {
            List<Patient> patients = patientDAO.getAllPatients();
            patientComboBox.getItems().setAll(patients);
            
            allAppointments = FXCollections.observableArrayList(appointmentDAO.getAllAppointments());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load data.");
        }
    }
    
    private void filterAppointments(int patientId) {
        ObservableList<Appointment> filtered = FXCollections.observableArrayList();
        for (Appointment a : allAppointments) {
            if (a.getPatientId() == patientId) {
                filtered.add(a);
            }
        }
        appointmentComboBox.setItems(filtered);
    }

    @FXML
    private void handleAddItem() {
        if (descField.getText().isEmpty() || qtyField.getText().isEmpty() || priceField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Description, Quantity, and Price are required.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyField.getText());
            double price = Double.parseDouble(priceField.getText());
            
            BillItem item = new BillItem(0, 0, descField.getText(), qty, price);
            itemList.add(item);
            
            descField.clear();
            qtyField.clear();
            priceField.clear();
            
            calculateTotals();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Quantity and Price must be numbers.");
        }
    }
    
    @FXML
    private void handleRemoveItem() {
        BillItem selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            itemList.remove(selected);
            calculateTotals();
        }
    }

    private void calculateTotals() {
        double subtotal = 0;
        for (BillItem item : itemList) {
            subtotal += (item.getQuantity() * item.getUnitPrice());
        }
        subtotalLabel.setText(String.format("%.2f", subtotal));
        
        try {
            double tax = taxField.getText().isEmpty() ? 0 : Double.parseDouble(taxField.getText());
            double discount = discountField.getText().isEmpty() ? 0 : Double.parseDouble(discountField.getText());
            
            double total = subtotal + tax - discount;
            totalLabel.setText(String.format("%.2f", total));
        } catch (NumberFormatException e) {
            // ignore while typing
        }
    }

    @FXML
    private void handleSaveBill() {
        if (patientComboBox.getValue() == null || appointmentComboBox.getValue() == null || itemList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Patient, Appointment, and at least one item are required.");
            return;
        }
        
        try {
            double tax = taxField.getText().isEmpty() ? 0 : Double.parseDouble(taxField.getText());
            double discount = discountField.getText().isEmpty() ? 0 : Double.parseDouble(discountField.getText());
            double subtotal = Double.parseDouble(subtotalLabel.getText());
            double total = Double.parseDouble(totalLabel.getText());
            
            Bill bill = Bill.builder()
                    .patientId(patientComboBox.getValue().getId())
                    .appointmentId(appointmentComboBox.getValue().getId())
                    .subtotal(subtotal)
                    .tax(tax)
                    .discount(discount)
                    .total(total)
                    .status(statusComboBox.getValue())
                    .items(new ArrayList<>(itemList))
                    .build();
                    
            billDAO.addBill(bill);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Bill generated successfully.");
            
            // clear form
            itemList.clear();
            patientComboBox.setValue(null);
            appointmentComboBox.setValue(null);
            taxField.setText("0");
            discountField.setText("0");
            calculateTotals();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save bill: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
