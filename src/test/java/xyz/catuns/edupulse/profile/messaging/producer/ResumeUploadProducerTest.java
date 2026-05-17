package xyz.catuns.edupulse.profile.messaging.producer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeStatus;
import xyz.catuns.edupulse.common.messaging.events.resume.ResumeUpload;
import xyz.catuns.edupulse.profile.domain.entity.Profile;
import xyz.catuns.edupulse.profile.domain.entity.Resume;
import xyz.catuns.edupulse.profile.domain.mapper.ProfileMapper;
import xyz.catuns.edupulse.profile.domain.repository.ProfileRepository;
import xyz.catuns.edupulse.profile.domain.repository.ResumeRepository;
import xyz.catuns.edupulse.profile.service.impl.ResumeServiceImpl;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResumeUploadProducerTest {

    @Mock private VectorStore vectorStore;
    @Mock private ProfileMapper profileMapper;
    @Mock private ProfileRepository profileRepository;
    @Mock private ResumeRepository resumeRepository;
    @Mock private ResumeUploadProducer resumeUploadProducer;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private Profile profile;
    private Resume savedResume;

    @BeforeEach
    void setUp() {
        profile = new Profile();
        profile.setUsername("testuser");

        savedResume = Resume.builder()
                .profile(profile)
                .originalFileName("resume.pdf")
                .build();
        // simulate DB-assigned UUID
        var id = UUID.randomUUID();
        try {
            var field = Resume.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(savedResume, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void uploadResume_publishesUploadedEvent_withCorrectUserIdAndStatus() {
        when(profileRepository.findByUsername("testuser")).thenReturn(Optional.of(profile));
        when(resumeRepository.save(any(Resume.class))).thenReturn(savedResume);
        doNothing().when(vectorStore).accept(any());

        var file = new MockMultipartFile("file", "resume.pdf", "application/pdf",
                "fake pdf content".getBytes());

        resumeService.uploadResume(file, "testuser");

        ArgumentCaptor<ResumeUpload> captor = ArgumentCaptor.forClass(ResumeUpload.class);
        verify(resumeUploadProducer, times(1)).publish(captor.capture());

        ResumeUpload event = captor.getValue();
        assertThat(event.getUserId()).isEqualTo("testuser");
        assertThat(event.getStatus()).isEqualTo(ResumeStatus.UPLOADED);
        assertThat(event.getResumeId()).isEqualTo(savedResume.getId().toString());
        assertThat(event.getId()).isNotBlank();
        assertThat(event.getTimestamp()).isNotNull();
    }

    @Test
    void uploadResume_noEventPublished_whenSaveFails() {
        when(profileRepository.findByUsername("testuser")).thenReturn(Optional.of(profile));
        when(resumeRepository.save(any(Resume.class))).thenThrow(new RuntimeException("db error"));

        var file = new MockMultipartFile("file", "resume.pdf", "application/pdf",
                "fake pdf content".getBytes());

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> resumeService.uploadResume(file, "testuser"));

        verify(resumeUploadProducer, never()).publish(any());
    }
}