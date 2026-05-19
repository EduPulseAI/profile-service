package xyz.catuns.edupulse.profile.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.catuns.edupulse.profile.domain.dto.ParsedResumeDto;
import xyz.catuns.edupulse.profile.domain.entity.Profile;
import xyz.catuns.edupulse.profile.domain.entity.embeddable.*;
import xyz.catuns.edupulse.profile.domain.mapper.ProfileMapper;
import xyz.catuns.edupulse.profile.domain.repository.ProfileRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Test
    void populateFromResume_blankScalarField_isPopulated() {
        Profile profile = profileWithPersonal("", "", "", "", "", "");
        when(profileRepository.findByUsername("user1")).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenReturn(profile);

        profileService.populateFromResume("user1", dtoWithPersonal("Jane", "Doe", "Engineer", "NYC", "j@x.com", "555"));

        Personal p = profile.getPersonal();
        assertThat(p.getFirstName()).isEqualTo("Jane");
        assertThat(p.getLastName()).isEqualTo("Doe");
        assertThat(p.getTitle()).isEqualTo("Engineer");
        assertThat(p.getEmail()).isEqualTo("j@x.com");
    }

    @Test
    void populateFromResume_nonBlankScalarField_isPreserved() {
        Profile profile = profileWithPersonal("", "", "Senior Engineer", "", "", "");
        when(profileRepository.findByUsername("user1")).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenReturn(profile);

        profileService.populateFromResume("user1", dtoWithPersonal(null, null, "Software Developer", null, null, null));

        assertThat(profile.getPersonal().getTitle()).isEqualTo("Senior Engineer");
    }

    @Test
    void populateFromResume_appendsExperienceEntries() {
        Profile profile = profileWithOneExperience();
        when(profileRepository.findByUsername("user1")).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenReturn(profile);
        when(profileMapper.toExperience(any(ParsedResumeDto.ExperienceItemDto.class)))
                .thenReturn(new Experience(), new Experience());

        ParsedResumeDto dto = new ParsedResumeDto(
                null,
                List.of(
                        new ParsedResumeDto.ExperienceItemDto("Dev", "A", "2020", null, null, List.of(), List.of()),
                        new ParsedResumeDto.ExperienceItemDto("Lead", "B", "2022", null, null, List.of(), List.of())),
                List.of(), List.of(), null, List.of(), null);

        profileService.populateFromResume("user1", dto);

        assertThat(profile.getExperiences()).hasSize(3);
    }

    @Test
    void populateFromResume_technicalSkills_mergedWithDeduplication() {
        Profile profile = profileWithBackendSkills(List.of("Java", "Python"));
        when(profileRepository.findByUsername("user1")).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenReturn(profile);

        ParsedResumeDto dto = new ParsedResumeDto(
                null, List.of(), List.of(), List.of(),
                new ParsedResumeDto.TechnicalSkillsDto(
                        List.of(), List.of("Python", "Go"), List.of(), List.of(), List.of(), List.of(), List.of()),
                List.of(), null);

        profileService.populateFromResume("user1", dto);

        assertThat(profile.getTechnicalSkills().getBackend())
                .containsExactly("Java", "Python", "Go");
    }

    @Test
    void populateFromResume_duplicateCertification_notAppended() {
        Profile profile = profileWithCertification("AWS SAA", "Amazon");
        when(profileRepository.findByUsername("user1")).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenReturn(profile);

        ParsedResumeDto dto = new ParsedResumeDto(
                null, List.of(), List.of(),
                List.of(new ParsedResumeDto.CertificationDto("AWS SAA", "Amazon", "2023-01")),
                null, List.of(), null);

        profileService.populateFromResume("user1", dto);

        assertThat(profile.getCredentials().getCertifications()).hasSize(1);
        verify(profileMapper, never()).toCertification(any());
    }

    @Test
    void populateFromResume_profileAutoCreated_whenNotFound() {
        Profile newProfile = new Profile();
        newProfile.setUsername("newuser");
        newProfile.setPersonal(new Personal());
        newProfile.setCredentials(new Credentials());
        newProfile.setTechnicalSkills(new TechnicalSkills());
        newProfile.setSocialLink(new SocialLink());

        when(profileRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(profileRepository.save(any())).thenReturn(newProfile);

        profileService.populateFromResume("newuser", dtoWithPersonal("Alice", null, null, null, null, null));

        ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository, times(2)).save(captor.capture());
        Profile created = captor.getAllValues().get(0);
        assertThat(created.getUsername()).isEqualTo("newuser");

        assertThat(newProfile.getPersonal().getFirstName()).isEqualTo("Alice");
    }

    // --- builders ---

    private static Profile profileWithPersonal(String fn, String ln, String title, String loc, String email, String phone) {
        Profile p = new Profile();
        Personal personal = new Personal();
        personal.setFirstName(fn);
        personal.setLastName(ln);
        personal.setTitle(title);
        personal.setLocation(loc);
        personal.setEmail(email);
        personal.setPhone(phone);
        p.setPersonal(personal);
        p.setCredentials(new Credentials());
        p.setTechnicalSkills(new TechnicalSkills());
        p.setSocialLink(new SocialLink());
        return p;
    }

    private static Profile profileWithOneExperience() {
        Profile p = new Profile();
        p.setPersonal(new Personal());
        Experience existing = new Experience();
        existing.setTitle("Old Job");
        p.getExperiences().add(existing);
        p.setCredentials(new Credentials());
        p.setTechnicalSkills(new TechnicalSkills());
        p.setSocialLink(new SocialLink());
        return p;
    }

    private static Profile profileWithBackendSkills(List<String> backend) {
        Profile p = new Profile();
        p.setPersonal(new Personal());
        p.setCredentials(new Credentials());
        TechnicalSkills skills = new TechnicalSkills();
        skills.setBackend(new ArrayList<>(backend));
        p.setTechnicalSkills(skills);
        p.setSocialLink(new SocialLink());
        return p;
    }

    private static Profile profileWithCertification(String name, String issuer) {
        Profile p = new Profile();
        p.setPersonal(new Personal());
        Credentials credentials = new Credentials();
        Certification cert = new Certification();
        cert.setName(name);
        cert.setIssuer(issuer);
        credentials.getCertifications().add(cert);
        p.setCredentials(credentials);
        p.setTechnicalSkills(new TechnicalSkills());
        p.setSocialLink(new SocialLink());
        return p;
    }

    private static ParsedResumeDto dtoWithPersonal(String fn, String ln, String title, String loc, String email, String phone) {
        return new ParsedResumeDto(
                new ParsedResumeDto.PersonalDto(fn, ln, title, loc, email, phone),
                List.of(), List.of(), List.of(), null, List.of(), null);
    }
}
