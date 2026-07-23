# Hospital Management System

A comprehensive desktop application built with Java, JavaFX, and MySQL to manage hospital operations including patient registration, doctor scheduling, appointment booking, billing, and medical history.

## Technologies Used
* **Java 17**
* **JavaFX** (UI Framework)
* **MySQL** (Database)
* **Maven** (Dependency Management)
* **HikariCP** (Connection Pooling)
* **Apache PDFBox** (PDF Generation)
* **Jakarta Mail** (Email Reminders)

## Setup Instructions

### 1. Database Setup
1. Install MySQL Server.
2. Open a MySQL client (like MySQL Workbench or command line) and run the provided `database_setup.sql` script to create the `hospital_db` database and its tables.

### 2. Configuration
1. Open `src/main/resources/config.properties`.
2. Update the `db.user` and `db.password` to match your MySQL credentials.
3. Update the SMTP configuration (`smtp.username` and `smtp.password`) with a valid email account. **Note:** If using Gmail, you may need to generate an "App Password" in your Google Account security settings.

### 3. Running the Application
This project uses the `javafx-maven-plugin`. To run the application, execute the following command from the project root directory:

```bash
mvn clean javafx:run
```

Alternatively, you can compile and package the application:
```bash
mvn clean package
```

## Features Implemented
* **Dashboard:** Overview of total patients, doctors, and today's appointments.
* **Patient Registration:** Add, edit, delete, and search patient profiles.
* **Doctor Scheduling:** Manage doctor profiles and availability.
* **Appointment Booking:** Schedule appointments and prevent double-booking.
* **Billing & Invoicing:** Add line items, calculate totals with tax/discount, and generate bills.
* **Medical History:** Track diagnoses, prescriptions, and test results chronologically.
* **Advanced Features:** 
  * Utility class (`PdfReportUtil`) ready for PDF invoice generation.
  * Background scheduled service (`EmailReminderService`) that runs daily to send email reminders for tomorrow's appointments.
