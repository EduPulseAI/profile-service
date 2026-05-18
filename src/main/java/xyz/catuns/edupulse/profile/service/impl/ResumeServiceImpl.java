package xyz.catuns.edupulse.profile.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeStatus;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeUpload;
import xyz.catuns.edupulse.profile.domain.dto.UploadResumeResponse;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;
import xyz.catuns.edupulse.profile.domain.entity.Profile;
import xyz.catuns.edupulse.profile.domain.entity.Resume;
import xyz.catuns.edupulse.profile.domain.mapper.ProfileMapper;
import xyz.catuns.edupulse.profile.domain.repository.ProfileRepository;
import xyz.catuns.edupulse.profile.domain.repository.ResumeRepository;
import xyz.catuns.edupulse.profile.messaging.producer.ResumeUploadProducer;
import xyz.catuns.edupulse.profile.service.ResumeService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final VectorStore vectorStore;
    private final ProfileMapper profileMapper;
    private final ProfileRepository profileRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeUploadProducer resumeUploadProducer;

    @Value("classpath:prompts/resume.st")
    private Resource resumePromptResource;

    @Override
    public ProfileResponse parseProfile(MultipartFile file) {
//        log.info("Uploading {}", file.getOriginalFilename());
//        TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
//        String text = documentReader.read().stream()
//                .map(Document::getText)
//                .collect(Collectors.joining("\n"));
//
//        PromptTemplate template = new PromptTemplate(resumePromptResource);
//        Map<String, Object> map = Map.of("resume", text);
//
//
//        log.info("Parsing {}", file.getOriginalFilename());
//        return chatClient.prompt()
//                .user(spec -> spec.text(template.render(map)))
//                .call()
//                .entity(ProfileResponse.class);
        return null;
    }

    @Override
    @Transactional
    public UploadResumeResponse uploadResume(MultipartFile file, String username) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("no user " + username));

        // generate unique id
        Resume resume = Resume.builder()
                .profile(profile)
                .originalFileName(file.getOriginalFilename())
                .build();
        resume = resumeRepository.save(resume);

        // split document
        TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> documents = documentReader.get();

        // set metadata
        for (Document document : documents) {
            var metadata = document.getMetadata();
            metadata.put("resumeId", resume.getId().toString());
        }

        vectorStore.accept(textSplitter.apply(documents));

        resumeUploadProducer.publish(ResumeUpload.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setStatus(ResumeStatus.UPLOADED)
                .setUserId(username)
                .setResumeId(resume.getId().toString())
                .setTimestamp(Instant.now())
                .build());

        return new UploadResumeResponse(resume.getId(), documents);
    }
}
