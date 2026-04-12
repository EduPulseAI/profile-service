package xyz.catuns.edupulse.profile.domain.dto;

public record UploadResumeResponse(
        xyz.catuns.edupulse.profile.domain.entity.Resume username,
        java.util.List<org.springframework.ai.document.Document> filename
) {
}
