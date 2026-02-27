package xyz.catuns.edupulse.profile.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;
import xyz.catuns.edupulse.profile.domain.entity.Profile;
import xyz.catuns.edupulse.profile.domain.mapper.ProfileMapper;
import xyz.catuns.edupulse.profile.domain.repository.ProfileRepository;
import xyz.catuns.edupulse.profile.service.ResumeService;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ChatClient chatClient;
    private final ProfileMapper profileMapper;
    private final ProfileRepository profileRepository;

    @Value("classpath:prompts/resume.st")
    private Resource resumePromptResource;

    @Override
    public ProfileResponse parseProfile(MultipartFile file) {
        log.info("Uploading {}", file.getOriginalFilename());
        TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
        String text = documentReader.read().stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        PromptTemplate template = new PromptTemplate(resumePromptResource);
        Map<String, Object> map = Map.of("resume", text);

        log.info("Parsing {}", file.getOriginalFilename());
        return chatClient.prompt()
                .user(spec -> spec.text(template.render(map)))
                .call()
                .entity(ProfileResponse.class);

    }

    @Override
    public ProfileResponse uploadResume(MultipartFile file, String username) {
        ProfileResponse generatedProfile = this.parseProfile(file);
        Profile profile = profileMapper.toEntity(generatedProfile);
        profile.setUsername(username);
        profileRepository.save(profile);
        return generatedProfile;
    }
}
