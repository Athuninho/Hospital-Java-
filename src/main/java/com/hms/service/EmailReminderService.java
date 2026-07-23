package com.hms.service;

import com.hms.util.DatabaseUtil;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmailReminderService {

    private ScheduledExecutorService scheduler;
    private Properties smtpProps;
    private String fromEmail;
    private String password;

    public EmailReminderService() {
        loadConfig();
    }

    private void loadConfig() {
        smtpProps = new Properties();
        try (InputStream input = EmailReminderService.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                Properties config = new Properties();
                config.load(input);
                
                smtpProps.put("mail.smtp.auth", "true");
                smtpProps.put("mail.smtp.starttls.enable", "true");
                smtpProps.put("mail.smtp.host", config.getProperty("smtp.host"));
                smtpProps.put("mail.smtp.port", config.getProperty("smtp.port"));
                
                fromEmail = config.getProperty("smtp.username");
                password = config.getProperty("smtp.password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startService() {
        scheduler = Executors.newScheduledThreadPool(1);
        
        // Run once every day to check for appointments tomorrow
        scheduler.scheduleAtFixedRate(this::sendReminders, 0, 1, TimeUnit.DAYS);
        System.out.println("Email Reminder Service started.");
    }
    
    public void stopService() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            System.out.println("Email Reminder Service stopped.");
        }
    }

    private void sendReminders() {
        System.out.println("Checking for appointments to send reminders...");
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        String query = "SELECT a.appointment_time, p.name as patient_name, p.contact, d.name as doctor_name " +
                       "FROM appointments a " +
                       "JOIN patients p ON a.patient_id = p.id " +
                       "JOIN doctors d ON a.doctor_id = d.id " +
                       "WHERE a.appointment_date = ? AND a.status = 'Confirmed'";
                       
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
             pstmt.setDate(1, java.sql.Date.valueOf(tomorrow));
             ResultSet rs = pstmt.executeQuery();
             
             while (rs.next()) {
                 String patientName = rs.getString("patient_name");
                 String doctorName = rs.getString("doctor_name");
                 String time = rs.getString("appointment_time");
                 // In a real app, 'contact' would hold the email address. We assume it does here.
                 String email = rs.getString("contact"); 
                 
                 if (email != null && email.contains("@")) {
                     sendEmail(email, patientName, doctorName, tomorrow.format(DateTimeFormatter.ISO_DATE), time);
                 }
             }
             
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String toAddress, String patientName, String doctorName, String date, String time) {
        Session session = Session.getInstance(smtpProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject("Appointment Reminder - Hospital Management System");
            
            String content = "Dear " + patientName + ",\n\n" +
                    "This is a reminder for your upcoming appointment with Dr. " + doctorName + ".\n" +
                    "Date: " + date + "\n" +
                    "Time: " + time + "\n\n" +
                    "Please arrive 15 minutes early.\n\n" +
                    "Thank you,\nHospital Administration";
                    
            message.setText(content);
            Transport.send(message);
            System.out.println("Sent reminder email to: " + toAddress);
            
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email to: " + toAddress);
        }
    }
}
