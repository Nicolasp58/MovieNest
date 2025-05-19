package com.example.demo.util;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PdfGenerator {

    public static Map<String, byte[]> generatePaymentDocuments(String username, String paymentMethod, double total) throws IOException {
        Map<String, byte[]> files = new HashMap<>();

        // ✅ Generar PDF
        ByteArrayOutputStream pdfBaos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(pdfBaos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("✅ PAGO EJECUTADO").setBold().setFontSize(16));
        document.add(new Paragraph("Usuario: " + username));
        document.add(new Paragraph("Método de pago: " + paymentMethod));
        document.add(new Paragraph("Total pagado: $" + String.format("%.2f", total)));

        document.close();
        files.put("pago-ejecutado.pdf", pdfBaos.toByteArray());

        // ✅ Generar Excel
        ByteArrayOutputStream excelBaos = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Factura");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Campo");
        header.createCell(1).setCellValue("Valor");

        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("Usuario");
        row1.createCell(1).setCellValue(username);

        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("Método de pago");
        row2.createCell(1).setCellValue(paymentMethod);

        Row row3 = sheet.createRow(3);
        row3.createCell(0).setCellValue("Total pagado");
        row3.createCell(1).setCellValue(String.format("%.2f", total));

        workbook.write(excelBaos);
        workbook.close();

        files.put("pago-ejecutado.xlsx", excelBaos.toByteArray());

        return files;
    }
}