package xyz.catuns.edupulse.profile.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import xyz.catuns.edupulse.profile.domain.dto.UploadResumeResponse;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;
import xyz.catuns.edupulse.profile.domain.mapper.ProfileMapper;
import xyz.catuns.edupulse.profile.domain.repository.ProfileRepository;
import xyz.catuns.edupulse.profile.service.ResumeService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl implements ResumeService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ProfileMapper profileMapper;
    private final ProfileRepository profileRepository;

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
    public UploadResumeResponse uploadResume(MultipartFile file, String username) {
        TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> documents = documentReader.get();
        vectorStore.accept(textSplitter.apply(documents));
        return new UploadResumeResponse(username, file.getOriginalFilename());
    }
}
