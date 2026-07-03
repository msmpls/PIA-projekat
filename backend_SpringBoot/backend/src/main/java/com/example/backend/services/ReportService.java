package com.example.backend.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final JdbcTemplate jdbcTemplate;

    public ReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Generise PDF izvestaj o popunjenosti terena za izabrani mesec.
     * Pokazuje procenat zauzetosti po svakom terenu.
     */
    public byte[] generateCourtUsageReport(String month, String employeeUsername) {
        List<Map<String, Object>> facilities = getEmployeeFacilities(employeeUsername);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // Naslov
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Izvestaj o popunjenosti terena", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            Font subFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Paragraph subtitle = new Paragraph("Mesec: " + month, subFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Tabela po objektima i terenima
            for (Map<String, Object> facility : facilities) {
                Long facilityId = ((Number) facility.get("id")).longValue();
                String facilityName = (String) facility.get("name");

                Font hFont = new Font(Font.HELVETICA, 14, Font.BOLD);
                document.add(new Paragraph(facilityName, hFont));
                document.add(Chunk.NEWLINE);

                // Podaci o zauzetosti terena
                String sql = "SELECT c.court_name, c.sport_type, c.type, " +
                        "COUNT(r.id) AS reservation_count, " +
                        "SUM(CASE WHEN r.status IN ('CONFIRMED','COMPLETED') THEN 1 ELSE 0 END) AS confirmed_count " +
                        "FROM courts c " +
                        "LEFT JOIN reservations r ON c.id = r.court_id " +
                        "AND DATE_FORMAT(r.reservation_date, '%Y-%m') = ? " +
                        "WHERE c.facility_id = ? " +
                        "GROUP BY c.id, c.court_name, c.sport_type, c.type " +
                        "ORDER BY c.court_name";

                List<Map<String, Object>> courts = jdbcTemplate.queryForList(sql, month, facilityId);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setSpacingBefore(5);
                table.setSpacingAfter(20);
                table.setWidths(new float[]{3, 2, 1.5f, 2, 2});

                // Header
                Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
                String[] headers = {"Teren", "Sport", "Tip", "Rezervacije", "Potvrdjene"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(new Color(37, 99, 235));
                    cell.setPadding(6);
                    table.addCell(cell);
                }

                // Podaci
                Font dataFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
                for (Map<String, Object> court : courts) {
                    table.addCell(new Phrase(String.valueOf(court.get("court_name")), dataFont));
                    table.addCell(new Phrase(String.valueOf(court.get("sport_type")), dataFont));
                    table.addCell(new Phrase(String.valueOf(court.get("type")), dataFont));
                    table.addCell(new Phrase(String.valueOf(court.get("reservation_count")), dataFont));
                    table.addCell(new Phrase(String.valueOf(court.get("confirmed_count")), dataFont));
                }

                document.add(table);
            }

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Greska pri generisanju PDF-a: " + e.getMessage());
        }

        return baos.toByteArray();
    }

    /**
     * Generise PDF izvestaj o prometu opreme za izabrani mesec.
     */
    public byte[] generateEquipmentSalesReport(String month, String employeeUsername) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Izvestaj o prometu opreme", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            Font subFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Paragraph subtitle = new Paragraph("Mesec: " + month, subFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Ukupan promet
            String totalSql = "SELECT COALESCE(SUM(o.total_price), 0) AS total " +
                    "FROM orders o WHERE DATE_FORMAT(o.order_date, '%Y-%m') = ? AND o.status != 'CANCELLED'";
            Double total = jdbcTemplate.queryForObject(totalSql, Double.class, month);
            if (total == null) total = 0.0;

            Font totalFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            document.add(new Paragraph("Ukupan promet: " + String.format("%.2f", total) + " RSD", totalFont));
            document.add(Chunk.NEWLINE);

            // Tabela stavki
            String sql = "SELECT e.name AS equipment_name, e.sport_type, " +
                    "SUM(oi.quantity) AS total_sold, " +
                    "SUM(oi.final_price) AS total_revenue " +
                    "FROM order_items oi " +
                    "JOIN orders o ON oi.order_id = o.id " +
                    "JOIN equipment e ON oi.equipment_id = e.id " +
                    "WHERE DATE_FORMAT(o.order_date, '%Y-%m') = ? AND o.status != 'CANCELLED' " +
                    "GROUP BY e.id, e.name, e.sport_type " +
                    "ORDER BY total_revenue DESC";

            List<Map<String, Object>> items = jdbcTemplate.queryForList(sql, month);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setWidths(new float[]{3, 2, 1.5f, 2});

            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            String[] headers = {"Oprema", "Sport", "Prodato komada", "Ukupan prihod"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new Color(37, 99, 235));
                cell.setPadding(6);
                table.addCell(cell);
            }

            Font dataFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            if (items.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("Nema podataka za izabrani mesec.", dataFont));
                empty.setColspan(4);
                empty.setPadding(8);
                table.addCell(empty);
            } else {
                for (Map<String, Object> item : items) {
                    table.addCell(new Phrase(String.valueOf(item.get("equipment_name")), dataFont));
                    table.addCell(new Phrase(String.valueOf(item.get("sport_type")), dataFont));
                    table.addCell(new Phrase(String.valueOf(item.get("total_sold")), dataFont));
                    table.addCell(new Phrase(String.format("%.2f", ((Number) item.get("total_revenue")).doubleValue()) + " RSD", dataFont));
                }
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Greska pri generisanju PDF-a: " + e.getMessage());
        }

        return baos.toByteArray();
    }

    private List<Map<String, Object>> getEmployeeFacilities(String username) {
        String sql = "SELECT f.id, f.name FROM facilities f " +
                "JOIN facility_employees fe ON f.id = fe.facility_id " +
                "JOIN users u ON fe.user_id = u.id " +
                "WHERE u.username = ?";
        return jdbcTemplate.queryForList(sql, username);
    }
}
