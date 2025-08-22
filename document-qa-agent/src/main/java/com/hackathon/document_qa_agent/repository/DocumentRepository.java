package com.hackathon.document_qa_agent.repository;

import com.hackathon.document_qa_agent.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, String> {

}
