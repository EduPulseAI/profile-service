package xyz.catuns.edupulse.profile.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.catuns.edupulse.profile.domain.dto.UploadResumeResponse;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;

public interface ResumeService {
    ProfileResponse parseProfile(MultipartFile file);

    UploadResumeResponse uploadResume(MultipartFile file, String username);
}
