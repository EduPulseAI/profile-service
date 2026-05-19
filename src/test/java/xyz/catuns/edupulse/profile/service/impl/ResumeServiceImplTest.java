package xyz.catuns.edupulse.profile.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.catuns.edupulse.profile.domain.dto.ParsedResumeDto;
import xyz.catuns.edupulse.profile.domain.repository.ProfileRepository;
import xyz.catuns.edupulse.profile.domain.repository.ResumeRepository;
import xyz.catuns.edupulse.profile.exception.ResumeParseException;
import xyz.catuns.edupulse.profile.messaging.producer.ResumeUploadProducer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResumeServiceImplTest {

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private ChatClient parsingChatClient;

    @Mock private VectorStore vectorStore;
    @Mock private ProfileRepository profileRepository;
    @Mock private ResumeRepository resumeRepository;
    @Mock private ResumeUploadProducer resumeUploadProducer;

    private ResumeServiceImpl resumeService;

    @BeforeEach
    void setUp() {
        resumeService = new ResumeServiceImpl(
                vectorStore, profileRepository, resumeRepository,
                resumeUploadProducer, parsingChatClient);
        ReflectionTestUtils.setField(resumeService, "resumePromptResource",
                new ClassPathResource("prompts/resume.st"));
    }

    @Test
    void parseProfile_returnsPopulatedDto_forValidResumeText() throws ResumeParseException {
        ParsedResumeDto expected = buildDto("Jane", "Doe", "jane@example.com");

        when(parsingChatClient.prompt().user(anyString()).call().entity(ParsedResumeDto.class))
                .thenReturn(expected);

        ParsedResumeDto result = resumeService.parseProfile("Jane Doe, Software Engineer");

        assertThat(result).isEqualTo(expected);
        assertThat(result.personal().firstName()).isEqualTo("Jane");
        assertThat(result.personal().email()).isEqualTo("jane@example.com");
    }

    @Test
    void parseProfile_renderedPromptContainsDocumentText() throws ResumeParseException {
        ParsedResumeDto expected = buildDto("Jane", "Doe", "jane@example.com");
        String documentText = "unique-marker-12345";

        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        when(parsingChatClient.prompt().user(promptCaptor.capture()).call().entity(ParsedResumeDto.class))
                .thenReturn(expected);

        resumeService.parseProfile(documentText);

        assertThat(promptCaptor.getValue()).contains(documentText);
    }

    @Test
    void parseProfile_returnsEmptyCollections_forMissingSections() throws ResumeParseException {
        ParsedResumeDto emptyDto = new ParsedResumeDto(
                new ParsedResumeDto.PersonalDto(null, null, null, null, null, null),
                List.of(), List.of(), List.of(),
                new ParsedResumeDto.TechnicalSkillsDto(
                        List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of()),
                List.of(), null);

        when(parsingChatClient.prompt().user(anyString()).call().entity(ParsedResumeDto.class))
                .thenReturn(emptyDto);

        ParsedResumeDto result = resumeService.parseProfile("minimal resume text");

        assertThat(result.certifications()).isEmpty();
        assertThat(result.languages()).isEmpty();
        assertThat(result.experience()).isEmpty();
    }

    @Test
    void parseProfile_throwsResumeParseException_whenEntityCallThrows() {
        when(parsingChatClient.prompt().user(anyString()).call().entity(ParsedResumeDto.class))
                .thenThrow(new RuntimeException("AI upstream failure"));

        assertThatThrownBy(() -> resumeService.parseProfile("resume text"))
                .isInstanceOf(ResumeParseException.class)
                .hasMessageContaining("AI upstream failure");
    }

    @Test
    void parseProfile_neverPropagatesUncheckedExceptions() {
        when(parsingChatClient.prompt().user(anyString()).call().entity(ParsedResumeDto.class))
                .thenThrow(new IllegalStateException("internal bad state"));

        assertThatThrownBy(() -> resumeService.parseProfile("resume text"))
                .isInstanceOf(ResumeParseException.class)
                .isNotInstanceOf(IllegalStateException.class);
    }

    // ---

    private static ParsedResumeDto buildDto(String firstName, String lastName, String email) {
        return new ParsedResumeDto(
                new ParsedResumeDto.PersonalDto(firstName, lastName, null, null, email, null),
                List.of(new ParsedResumeDto.ExperienceItemDto(
                        "Engineer", "Corp", "2020-01/2023-01", null, null, List.of(), List.of())),
                List.of(),
                List.of(),
                new ParsedResumeDto.TechnicalSkillsDto(
                        List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of()),
                List.of(),
                null);
    }
}
