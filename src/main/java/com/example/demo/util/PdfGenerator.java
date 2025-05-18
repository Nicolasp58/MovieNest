package com.example.demo.util;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfGenerator {

    public static byte[] generatePaymentInvoice(String username, String paymentMethod, double total) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("✅ PAGO EJECUTADO").setBold().setFontSize(16));
        document.add(new Paragraph("Usuario: " + username));
        document.add(new Paragraph("Método de pago: " + paymentMethod));
        document.add(new Paragraph("Total pagado: $" + String.format("%.2f", total)));

        document.close();

        return baos.toByteArray();
    }
}
