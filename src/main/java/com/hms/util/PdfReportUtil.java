package com.hms.util;

import com.hms.model.Bill;
import com.hms.model.BillItem;
import com.hms.model.Patient;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;

public class PdfReportUtil {

    public static void generateBillPdf(Bill bill, Patient patient, String destPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("HOSPITAL MANAGEMENT SYSTEM");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(50, 650);
                contentStream.showText("INVOICE / BILL");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, 600);
                contentStream.setLeading(15f);
                contentStream.showText("Bill ID: " + bill.getId());
                contentStream.newLine();
                contentStream.showText("Patient Name: " + patient.getName());
                contentStream.newLine();
                contentStream.showText("Contact: " + patient.getContact());
                contentStream.newLine();
                contentStream.showText("Status: " + bill.getStatus());
                contentStream.endText();

                // Draw Table Header
                int yPosition = 500;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Description");
                contentStream.newLineAtOffset(250, 0);
                contentStream.showText("Qty");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Unit Price");
                contentStream.newLineAtOffset(100, 0);
                contentStream.showText("Total");
                contentStream.endText();

                // Draw Table Rows
                yPosition -= 20;
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                if (bill.getItems() != null) {
                    for (BillItem item : bill.getItems()) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, yPosition);
                        contentStream.showText(item.getDescription());
                        contentStream.newLineAtOffset(250, 0);
                        contentStream.showText(String.valueOf(item.getQuantity()));
                        contentStream.newLineAtOffset(100, 0);
                        contentStream.showText(String.format("%.2f", item.getUnitPrice()));
                        contentStream.newLineAtOffset(100, 0);
                        contentStream.showText(String.format("%.2f", item.getQuantity() * item.getUnitPrice()));
                        contentStream.endText();
                        yPosition -= 20;
                    }
                }

                // Draw Totals
                yPosition -= 20;
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(350, yPosition);
                contentStream.showText("Subtotal: $" + String.format("%.2f", bill.getSubtotal()));
                contentStream.newLine();
                yPosition -= 15;
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Tax: $" + String.format("%.2f", bill.getTax()));
                contentStream.newLine();
                yPosition -= 15;
                contentStream.newLineAtOffset(0, -15);
                contentStream.showText("Discount: $" + String.format("%.2f", bill.getDiscount()));
                contentStream.newLine();
                yPosition -= 20;
                contentStream.newLineAtOffset(0, -20);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.showText("Total: $" + String.format("%.2f", bill.getTotal()));
                contentStream.endText();
            }

            document.save(new File(destPath));
        }
    }
}
