📄 AI-Powered Multi-Document Q&A System

An AI-powered backend application built with Spring Boot that enables users to:

Upload PDFs and image documents (JPG/PNG).

Automatically extract text using Apache PDFBox (PDF) and Tesseract OCR (images).

Ask questions across multiple uploaded documents using Google Gemini LLM.

Retrieve context-aware answers following a Retrieval-Augmented Generation (RAG) pattern.

🚀 Features

- Upload multiple documents and persist extracted text in a database.

- Extract content from PDFs and images using PDFBox and Tesseract OCR.

- AI-powered Q&A using Google Gemini LLM API.

- Implements a RAG approach — retrieves relevant content before passing to the LLM.

- RESTful APIs with robust error handling and scalability.

 Tech Stack :- 

Language & Framework: Java, Spring Boot

AI & ML: Google Gemini API, Large Language Models (LLMs), RAG, Prompt Engineering

Document Processing: Apache PDFBox, Tesseract OCR

Database: JPA/Hibernate (or your DB, e.g., PostgreSQL/MySQL)

Build Tool: Maven/Gradle

Version Control: Git & GitHub
