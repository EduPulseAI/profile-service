package xyz.catuns.edupulse.profile.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.catuns.edupulse.profile.domain.dto.ParsedResumeDto;
import xyz.catuns.edupulse.profile.domain.dto.UploadResumeResponse;
import xyz.catuns.edupulse.profile.exception.ResumeParseException;

public interface ResumeService {
    ParsedResumeDto parseProfile(String documentText) throws ResumeParseException;

    UploadResumeResponse uploadResume(MultipartFile file, String username);
}
