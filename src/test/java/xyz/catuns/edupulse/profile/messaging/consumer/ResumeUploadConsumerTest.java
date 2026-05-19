package xyz.catuns.edupulse.profile.messaging.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeStatus;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeUpload;
import xyz.catuns.edupulse.profile.domain.dto.ParsedResumeDto;
import xyz.catuns.edupulse.profile.domain.entity.ProcessedResumeEvent;
import xyz.catuns.edupulse.profile.domain.entity.Resume;
import xyz.catuns.edupulse.profile.domain.repository.ProcessedResumeEventRepository;
import xyz.catuns.edupulse.profile.domain.repository.ResumeRepository;
import xyz.catuns.edupulse.profile.exception.ResumeParseException;
import xyz.catuns.edupulse.profile.messaging.producer.ResumeUploadProducer;
import xyz.catuns.edupulse.profile.service.ProfileService;
import xyz.catuns.edupulse.profile.service.ResumeService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeUploadConsumerTest {

    @Mock private ResumeService resumeService;
    @Mock private ProfileService profileService;
    @Mock private ResumeRepository resumeRepository;
    @Mock private ResumeUploadProducer resumeUploadProducer;
    @Mock private ProcessedResumeEventRepository processedResumeEventRepository;

    @InjectMocks
    private ResumeUploadConsumer consumer;

    private String eventId;
    private String userId;
    private String resumeId;
    private Resume resume;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID().toString();
        userId = "user-123";
        resumeId = UUID.randomUUID().toString();

        resume = Resume.builder()
                .originalFileName("resume.pdf")
                .rawText("John Doe — Senior Engineer...")
                .build();
        try {
            var field = Resume.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(resume, UUID.fromString(resumeId));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void consume_parsedStatusEvent_returnsImmediately() {
        ResumeUpload event = buildEvent(eventId, ResumeStatus.PARSED);

        consumer.consume(event);

        verifyNoInteractions(resumeService, profileService, processedResumeEventRepository, resumeUploadProducer);
    }

    @Test
    void consume_failedStatusEvent_returnsImmediately() {
        ResumeUpload event = buildEvent(eventId, ResumeStatus.FAILED);

        consumer.consume(event);

        verifyNoInteractions(resumeService, profileService, processedResumeEventRepository, resumeUploadProducer);
    }

    @Test
    void consume_duplicateEventId_returnsImmediately() {
        when(processedResumeEventRepository.existsById(eventId)).thenReturn(true);
        ResumeUpload event = buildEvent(eventId, ResumeStatus.UPLOADED);

        consumer.consume(event);

        verifyNoInteractions(resumeService, profileService, resumeUploadProducer);
        verify(processedResumeEventRepository, never()).save(any());
    }

    @Test
    void consume_validUploadedEvent_callsParseAndPopulate() throws ResumeParseException {
        when(processedResumeEventRepository.existsById(eventId)).thenReturn(false);
        when(resumeRepository.findById(UUID.fromString(resumeId))).thenReturn(Optional.of(resume));
        ParsedResumeDto parsed = new ParsedResumeDto(null, null, null, null, null, null, null);
        when(resumeService.parseProfile(resume.getRawText())).thenReturn(parsed);
        when(processedResumeEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResumeUpload event = buildEvent(eventId, ResumeStatus.UPLOADED);
        consumer.consume(event);

        verify(resumeService, times(1)).parseProfile(resume.getRawText());
        verify(profileService, times(1)).populateFromResume(userId, parsed);
        verify(processedResumeEventRepository, times(1)).save(any(ProcessedResumeEvent.class));

        ArgumentCaptor<ResumeUpload> captor = ArgumentCaptor.forClass(ResumeUpload.class);
        verify(resumeUploadProducer, times(1)).publish(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ResumeStatus.PARSED);
        assertThat(captor.getValue().getDetails()).isEqualTo(eventId);
    }

    @Test
    void consume_parseProfileThrows_publishesFailedEventAndNeverPopulates() throws ResumeParseException {
        when(processedResumeEventRepository.existsById(eventId)).thenReturn(false);
        when(resumeRepository.findById(UUID.fromString(resumeId))).thenReturn(Optional.of(resume));
        when(resumeService.parseProfile(anyString())).thenThrow(new ResumeParseException("AI error"));
        when(processedResumeEventRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResumeUpload event = buildEvent(eventId, ResumeStatus.UPLOADED);
        consumer.consume(event);

        verify(profileService, never()).populateFromResume(anyString(), any());
        verify(processedResumeEventRepository, times(1)).save(any(ProcessedResumeEvent.class));

        ArgumentCaptor<ResumeUpload> captor = ArgumentCaptor.forClass(ResumeUpload.class);
        verify(resumeUploadProducer, times(1)).publish(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ResumeStatus.FAILED);
        assertThat(captor.getValue().getDetails()).isEqualTo("AI error");
    }

    private ResumeUpload buildEvent(String id, ResumeStatus status) {
        return ResumeUpload.newBuilder()
                .setId(id)
                .setStatus(status)
                .setUserId(userId)
                .setResumeId(resumeId)
                .setTimestamp(Instant.now())
                .build();
    }
}
