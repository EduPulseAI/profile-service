package xyz.catuns.edupulse.profile.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeStatus;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeUpload;
import xyz.catuns.edupulse.profile.domain.entity.ProcessedResumeEvent;
import xyz.catuns.edupulse.profile.domain.repository.ProcessedResumeEventRepository;
import xyz.catuns.edupulse.profile.domain.repository.ResumeRepository;
import xyz.catuns.edupulse.profile.exception.ResumeParseException;
import xyz.catuns.edupulse.profile.messaging.producer.ResumeUploadProducer;
import xyz.catuns.edupulse.profile.service.ProfileService;
import xyz.catuns.edupulse.profile.service.ResumeService;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResumeUploadConsumer {

    private final ResumeService resumeService;
    private final ProfileService profileService;
    private final ResumeRepository resumeRepository;
    private final ResumeUploadProducer resumeUploadProducer;
    private final ProcessedResumeEventRepository processedResumeEventRepository;

    @KafkaListener(topics = "${app.kafka.topics.resume-upload}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ResumeUpload event) {
        log.debug("Consumed event {}", event);
        if (event.getStatus() != ResumeStatus.UPLOADED) return;
        if (processedResumeEventRepository.existsById(event.getId())) return;

        try {
            var resume = resumeRepository.findById(UUID.fromString(event.getResumeId()))
                    .orElseThrow(() -> new IllegalStateException("Resume not found: " + event.getResumeId()));
            var parsedDto = resumeService.parseProfile(resume.getRawText());
            profileService.populateFromResume(event.getUserId(), parsedDto);
            processedResumeEventRepository.save(new ProcessedResumeEvent(event.getId()));
            resumeUploadProducer.publish(buildEvent(event.getUserId(), event.getResumeId(), ResumeStatus.PARSED, event.getId()));
        } catch (ResumeParseException ex) {
            log.error("Failed to parse resume for userId={} resumeId={}: {}",
                    event.getUserId(), event.getResumeId(), ex.getMessage(), ex);
            processedResumeEventRepository.save(new ProcessedResumeEvent(event.getId()));
            resumeUploadProducer.publish(buildEvent(event.getUserId(), event.getResumeId(), ResumeStatus.FAILED, ex.getMessage()));
        }
    }

    private ResumeUpload buildEvent(String userId, String resumeId, ResumeStatus status, String details) {
        return ResumeUpload.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setStatus(status)
                .setUserId(userId)
                .setResumeId(resumeId)
                .setTimestamp(Instant.now())
                .setDetails(details)
                .build();
    }
}
