package com.hackathon.document_qa_agent.controller;

import com.hackathon.document_qa_agent.entity.DocumentEntity;
import com.hackathon.document_qa_agent.repository.DocumentRepository;
import com.hackathon.document_qa_agent.response.AnswerResponse;
import com.hackathon.document_qa_agent.service.GeminiService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private GeminiService geminiService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            String extractedText = extractText(file);
            System.out.println("📄 Extracted Text:\n" + extractedText);

            String documentId = UUID.randomUUID().toString();
            DocumentEntity document = new DocumentEntity(documentId, extractedText);
            documentRepository.save(document);

            return ResponseEntity.ok().body(Map.of(
                    "documentId", documentId,
                    "message", "Document uploaded and text extracted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * ✅ Ask question across ALL uploaded documents (RAG style)
     */
    @PostMapping("/ask")
    public ResponseEntity<?> askFromAllDocuments(@RequestParam("question") String question) {
        try {
            List<DocumentEntity> allDocs = documentRepository.findAll();

            if (allDocs.isEmpty()) {
                return ResponseEntity.badRequest().body("No documents available to search.");
            }
            StringBuilder relevantContext = new StringBuilder();
            for (DocumentEntity doc : allDocs) {
                if (doc.getContent().toLowerCase().contains(question.toLowerCase())) {
                    relevantContext.append(doc.getContent()).append("\n---\n");
                }
            }
            if (relevantContext.length() == 0) {
                for (DocumentEntity doc : allDocs) {
                    relevantContext.append(doc.getContent()).append("\n---\n");
                }
            }

            String answer = geminiService.askGemini(relevantContext.toString(), question);
            return ResponseEntity.ok().body(new AnswerResponse(answer));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    private String extractText(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new Exception("Invalid file name");

        if (fileName.endsWith(".pdf")) {
            return extractTextFromPDF(file.getInputStream());
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg")) {
            return extractTextFromImage(file.getInputStream());
        } else {
            throw new Exception("Unsupported file type");
        }
    }

    private String extractTextFromPDF(InputStream inputStream) throws IOException {
        PDDocument document = PDDocument.load(inputStream);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    private String extractTextFromImage(InputStream inputStream) throws IOException, TesseractException {
        BufferedImage image = ImageIO.read(inputStream);
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata"); // optional
        return tesseract.doOCR(image);
    }
}
