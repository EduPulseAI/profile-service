package xyz.catuns.edupulse.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.catuns.edupulse.profile.domain.dto.UploadResumeResponse;
import xyz.catuns.edupulse.profile.service.ResumeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resume api")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "UploadResume",description = "Upload Resume")
    @ApiResponse(responseCode = "202", description = "HTTP Status ACCEPTED")
    public ResponseEntity<UploadResumeResponse> uploadResume(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal String username
    ) {
        UploadResumeResponse response = resumeService.uploadResume(file, username);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
