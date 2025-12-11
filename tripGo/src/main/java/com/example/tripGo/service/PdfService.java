package com.example.tripGo.service;

import com.example.tripGo.entity.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public byte[] generateBookingReceiptPdf(Booking booking) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Set up fonts
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Header
            Paragraph header = new Paragraph("TRIPGO - BOOKING RECEIPT")
                    .setFont(boldFont)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.RED);
            document.add(header);

            document.add(new Paragraph(" "));

            // Booking Information
            document.add(new Paragraph("Reference Number: " + booking.getReferenceNumber())
                    .setFont(boldFont)
                    .setFontSize(12));

            document.add(new Paragraph("Booking Date: " +
                    (booking.getBookingDate() != null ?
                            booking.getBookingDate().format(DATETIME_FORMATTER) : "N/A"))
                    .setFont(normalFont)
                    .setFontSize(10));

            document.add(new Paragraph("Status: " + booking.getBookingStatus())
                    .setFont(normalFont)
                    .setFontSize(10));

            document.add(new Paragraph(" "));

            // Journey Details
            Schedule schedule = booking.getSchedule();
            if (schedule != null) {
                Route route = schedule.getRoute();
                if (route != null) {
                    document.add(new Paragraph("Journey: " + route.getStartPoint() + " to " + route.getEndPoint())
                            .setFont(boldFont)
                            .setFontSize(12));
                }

                document.add(new Paragraph("Date: " + schedule.getJourneyDate().format(DATE_FORMATTER))
                        .setFont(normalFont)
                        .setFontSize(10));

                document.add(new Paragraph("Departure: " + schedule.getStartTime())
                        .setFont(normalFont)
                        .setFontSize(10));

                if (schedule.getBus() != null) {
                    document.add(new Paragraph("Bus: " + schedule.getBus().getBusNumber())
                            .setFont(normalFont)
                            .setFontSize(10));
                }
            }

            // Boarding & Dropping
            if (booking.getBoardingPoint() != null) {
                document.add(new Paragraph("Boarding: " + booking.getBoardingPoint().getLocationName() +
                        (booking.getBoardingPoint().getDepartureTime() != null ?
                                " (" + booking.getBoardingPoint().getDepartureTime() + ")" : ""))
                        .setFont(normalFont)
                        .setFontSize(10));
            }

            if (booking.getDroppingPoint() != null) {
                document.add(new Paragraph("Dropping: " + booking.getDroppingPoint().getLocationName() +
                        (booking.getDroppingPoint().getArrivalTime() != null ?
                                " (" + booking.getDroppingPoint().getArrivalTime() + ")" : ""))
                        .setFont(normalFont)
                        .setFontSize(10));
            }

            document.add(new Paragraph(" "));

            // Passenger Table
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 1, 1, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Table headers
            table.addHeaderCell(new Cell().add(new Paragraph("Seat").setFont(boldFont)));
            table.addHeaderCell(new Cell().add(new Paragraph("Passenger").setFont(boldFont)));
            table.addHeaderCell(new Cell().add(new Paragraph("Age").setFont(boldFont)));
            table.addHeaderCell(new Cell().add(new Paragraph("Gender").setFont(boldFont)));
            table.addHeaderCell(new Cell().add(new Paragraph("Fare (₹)").setFont(boldFont)));

            // Table rows
            for (BookingSeat bs : booking.getBookingSeats()) {
                Passenger passenger = bs.getPassenger();
                table.addCell(new Cell().add(new Paragraph(bs.getSeat().getSeatNumber()).setFont(normalFont)));
                table.addCell(new Cell().add(new Paragraph(passenger.getName()).setFont(normalFont)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(passenger.getAge())).setFont(normalFont)));
                table.addCell(new Cell().add(new Paragraph(passenger.getGender()).setFont(normalFont)));
                table.addCell(new Cell().add(new Paragraph("₹" + bs.getPrice()).setFont(normalFont)));
            }

            document.add(table);

            // Total
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Amount: ₹" + booking.getTotalAmount())
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT));

            // Contact Info
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Contact Information:")
                    .setFont(boldFont)
                    .setFontSize(12));

            document.add(new Paragraph("Name: " + booking.getContactName())
                    .setFont(normalFont)
                    .setFontSize(10));
            document.add(new Paragraph("Phone: " + booking.getContactPhone())
                    .setFont(normalFont)
                    .setFontSize(10));
            document.add(new Paragraph("Email: " + booking.getContactEmail())
                    .setFont(normalFont)
                    .setFontSize(10));

            // Footer
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Thank you for choosing TripGo!")
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic());

            document.add(new Paragraph("Generated on: " + java.time.LocalDateTime.now().format(DATETIME_FORMATTER))
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate PDF receipt for booking {}", booking.getBookingId(), e);
            throw new RuntimeException("Failed to generate PDF receipt", e);
        }
    }
}