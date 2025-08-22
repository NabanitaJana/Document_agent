package com.hackathon.document_qa_agent.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class OCRService {

    public String extractText(MultipartFile file) {
        String fileType = file.getContentType();
        try {
            if (fileType != null && fileType.contains("pdf")) {
                return extractTextFromPdf(file);
            } else if (fileType != null && (fileType.contains("image") || fileType.contains("jpeg") || fileType.contains("png"))) {
                return extractTextFromImage(file);
            } else {
                return "Unsupported file type.";
            }
        } catch (Exception e) {
            throw new RuntimeException("Text extraction failed: " + e.getMessage(), e);
        }
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();
        return text;
    }

    private String extractTextFromImage(MultipartFile file) throws Exception {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        file.transferTo(convFile);

        ITesseract tesseract = new Tesseract();
        // Set the Tesseract data path. This needs to be configured based on your OS.
        // For local development on macOS with Homebrew, this is a common path:
        // tesseract.setDatapath("/opt/homebrew/share/tessdata");

        String text = tesseract.doOCR(convFile);
        convFile.delete(); // Clean up the temporary file
        return text;
    }
}

