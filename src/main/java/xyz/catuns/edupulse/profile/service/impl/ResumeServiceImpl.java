package xyz.catuns.edupulse.profile.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeStatus;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeUpload;
import xyz.catuns.edupulse.profile.domain.dto.ParsedResumeDto;
import xyz.catuns.edupulse.profile.domain.dto.UploadResumeResponse;
import xyz.catuns.edupulse.profile.domain.entity.Profile;
import xyz.catuns.edupulse.profile.domain.entity.Resume;
import xyz.catuns.edupulse.profile.domain.repository.ProfileRepository;
import xyz.catuns.edupulse.profile.domain.repository.ResumeRepository;
import xyz.catuns.edupulse.profile.exception.ResumeParseException;
import xyz.catuns.edupulse.profile.messaging.producer.ResumeUploadProducer;
import xyz.catuns.edupulse.profile.service.ResumeService;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final VectorStore vectorStore;
    private final ProfileRepository profileRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeUploadProducer resumeUploadProducer;
    private final ChatClient parsingChatClient;

    @Value("classpath:prompts/resume.st")
    private Resource resumePromptResource;

    public ResumeServiceImpl(
            VectorStore vectorStore,
            ProfileRepository profileRepository,
            ResumeRepository resumeRepository,
            ResumeUploadProducer resumeUploadProducer,
            @Qualifier("parsingChatClient") ChatClient parsingChatClient) {
        this.vectorStore = vectorStore;
        this.profileRepository = profileRepository;
        this.resumeRepository = resumeRepository;
        this.resumeUploadProducer = resumeUploadProducer;
        this.parsingChatClient = parsingChatClient;
    }

    @Override
    public ParsedResumeDto parseProfile(String documentText) throws ResumeParseException {
        PromptTemplate template = new PromptTemplate(resumePromptResource);
        String rendered = template.render(Map.of("resume", documentText));
        try {
            ParsedResumeDto result = parsingChatClient.prompt()
                    .user(rendered)
                    .call()
                    .entity(ParsedResumeDto.class);
            if (result == null) {
                throw new ResumeParseException("Failed to parse resume: entity() returned null");
            }
            return result;
        } catch (ResumeParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse resume: {}", e.getMessage(), e);
            throw new ResumeParseException("Failed to parse resume: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public UploadResumeResponse uploadResume(MultipartFile file, String username) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("no user " + username));

        TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
        List<Document> documents = documentReader.get();

        String rawText = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        Resume resume = Resume.builder()
                .profile(profile)
                .originalFileName(file.getOriginalFilename())
                .rawText(rawText)
                .build();
        resume = resumeRepository.save(resume);

        TextSplitter textSplitter = new TokenTextSplitter();
        for (Document document : documents) {
            document.getMetadata().put("resumeId", resume.getId().toString());
        }
        vectorStore.accept(textSplitter.apply(documents));

        resumeUploadProducer.publish(ResumeUpload.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setStatus(ResumeStatus.UPLOADED)
                .setUserId(username)
                .setResumeId(resume.getId().toString())
                .setTimestamp(Instant.now())
                .build());

        return new UploadResumeResponse(resume.getId());
    }
}
