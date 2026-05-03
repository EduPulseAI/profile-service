package xyz.catuns.edupulse.profile.domain.dto;

import org.springframework.ai.document.Document;

import java.util.List;
import java.util.UUID;

public record UploadResumeResponse(
        UUID resumeId,
        List<Document> documents
) {
}
