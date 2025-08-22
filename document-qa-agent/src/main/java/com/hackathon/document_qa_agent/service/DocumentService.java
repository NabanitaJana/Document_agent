//package com.hackathon.document_qa_agent.service;
//
//import org.springframework.ai.chat.ChatClient;
//import org.springframework.ai.chat.messages.UserMessage;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//@Service
//public class DocumentService {
//
//    @Autowired
//    private OCRService ocrService;
//
//    @Autowired
//    private ChatClient chatClient; // Spring AI ChatClient for LLM interaction
//
//    // In-memory store for document content. Use a VectorStore for a real RAG system.
//    private final Map<String, String> documentStore = new HashMap<>();
//
//    public String processDocument(MultipartFile file) {
//        String extractedText = ocrService.extractText(file);
//        String documentId = UUID.randomUUID().toString();
//        documentStore.put(documentId, extractedText);
//        return documentId;
//    }
//
//    public String askQuestion(String question) {
//        // For this simplified example, we'll retrieve the last processed document.
//        // In a real RAG system, you would retrieve relevant chunks based on the question.
//        String documentId = documentStore.keySet().stream().findFirst().orElse(null);
//
//        if (documentId == null) {
//            return "No document has been uploaded yet. Please upload a document first.";
//        }
//
//        String documentContent = documentStore.get(documentId);
//
//        // Build the prompt with the document content as context
//        String promptText = """
//            You are a helpful assistant. Answer the user's question based on the following document content.
//            If the answer is not in the document, say so.
//
//            Document Content:
//            %s
//
//            Question:
//            %s
//            """.formatted(documentContent, question);
//
//        UserMessage userMessage = new UserMessage(promptText);
//        Prompt prompt = new Prompt(userMessage);
//
//        return chatClient.call(prompt).getResult().getOutput().getContent();
//    }
//}
//
