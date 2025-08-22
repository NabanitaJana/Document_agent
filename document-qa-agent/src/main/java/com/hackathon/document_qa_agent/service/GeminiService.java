package com.hackathon.document_qa_agent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final String API_KEY = "AIzaSyBfTKQXCLRqBB3xfH28_3yaZyPHTfqCdSg"; // Replace with your actual API key
    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;


    public String askGemini(String context, String question) throws Exception {
        String prompt = String.format("""
You are a document reader assistant. Your job is to read the following document and answer questions about it.

Document Content:
-----------------
%s

Question: %s

Answer the question clearly and precisely. If the answer is not in the document, say: "The document does not contain that information."
""", context, question);

        // ✅ DEBUG LOGS
        System.out.println(" Gemini Prompt:\n" + prompt);

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(textPart), "role", "user");
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        ObjectMapper mapper = new ObjectMapper();
        String body = mapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("🔁 Gemini Raw Response:\n" + response.body());

        Map<String, Object> responseMap = mapper.readValue(response.body(), Map.class);
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseMap.get("candidates");

        if (candidates != null && !candidates.isEmpty()) {
            Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, String>> parts = (List<Map<String, String>>) contentMap.get("parts");
            String answer = parts.get(0).get("text");
            answer = answer.replaceAll("\\r?\\n", " ");
            answer = answer.replaceAll("\\*", "");
            answer = answer.replaceAll("-", "");
            answer = answer.replaceAll("\\s+", " ").trim();

            return answer;

        } else {
            return "No answer from Gemini.";
        }
    }
}
