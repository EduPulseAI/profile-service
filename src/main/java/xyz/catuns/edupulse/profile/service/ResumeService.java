package xyz.catuns.edupulse.profile.service;

import org.springframework.web.multipart.MultipartFile;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;

public interface ResumeService {
    ProfileResponse parseProfile(MultipartFile file);

    ProfileResponse uploadResume(MultipartFile file, String username);
}
