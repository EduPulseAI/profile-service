package xyz.catuns.edupulse.profile.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.catuns.edupulse.profile.domain.dto.ParsedResumeDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.*;
import xyz.catuns.edupulse.profile.domain.entity.Profile;
import xyz.catuns.edupulse.profile.domain.entity.embeddable.*;
import xyz.catuns.edupulse.profile.domain.mapper.ProfileMapper;
import xyz.catuns.edupulse.profile.domain.repository.ProfileRepository;
import xyz.catuns.edupulse.profile.service.ProfileService;
import xyz.catuns.spring.base.exception.controller.BadRequestException;
import xyz.catuns.spring.base.exception.controller.NotFoundException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional
    public ProfileResponse getProfileForCurrentUser(String username) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseGet(() -> {
                    Profile p = new Profile();
                    p.setUsername(username);
                    return profileRepository.save(p);
                });

        return profileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public PersonalDto updatePersonalInfo(String username, UpdatePersonalRequest request) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));


        Personal personal = profile.getPersonal();
        if (personal == null) {
            personal = new Personal();
            profile.setPersonal(personal);
        }

        profileMapper.updatePersonalFromRequest(request, personal);

        profile = profileRepository.save(profile);

        return profileMapper.toPersonalDto(personal);
    }

    @Override
    @Transactional
    public AboutDto updateAbout(String username, UpdateAboutRequest request) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));


        About about = profile.getAbout();
        if (about == null) {
            about = new About();
            profile.setAbout(about);
        }

        // Update simple fields
//        about.setBio(request.bio());

        about.getSummary().clear();
        if (request.summary() != null) {
            about.getSummary().addAll(request.summary());
        }

        profile = profileRepository.save(profile);

        return profileMapper.toAboutDto(about);
    }

    // Experience CRUD operations

    @Override
    @Transactional
    public ExperienceItemDto addExperience(String username, ExperienceRequest request) {
        Profile profile = getProfileByUsername(username);

        Experience experience = profileMapper.toExperience(request);
        profile.getExperiences().add(experience);

        profile = profileRepository.save(profile);

        return profileMapper.toExperienceItemDto(experience);
    }

    @Override
    @Transactional
    public ExperienceItemDto updateExperience(String username, int index, ExperienceRequest request) {
        Profile profile = getProfileByUsername(username);

        List<Experience> experiences = profile.getExperiences();
        validateIndex(index, experiences.size(), "Experience");

        Experience experience = experiences.get(index);
        profileMapper.updateExperienceFromRequest(request, experience);

        profile = profileRepository.save(profile);

        return profileMapper.toExperienceItemDto(experience);
    }

    @Override
    @Transactional
    public void deleteExperience(String username, int index) {
        Profile profile = getProfileByUsername(username);

        List<Experience> experiences = profile.getExperiences();
        validateIndex(index, experiences.size(), "Experience");

        experiences.remove(index);

        profile = profileRepository.save(profile);
    }

    // Education CRUD operations

    @Override
    @Transactional
    public EducationDto addEducation(String username, EducationRequest request) {
        Profile profile = getProfileByUsername(username);

        Credentials credentials = profile.getCredentials();
        if (credentials == null) {
            credentials = new Credentials();
            profile.setCredentials(credentials);
        }

        Education education = profileMapper.toEducation(request);
        credentials.getEducation().add(education);

        profileRepository.save(profile);

        return profileMapper.toEducationDto(education);
    }

    @Override
    @Transactional
    public EducationDto updateEducation(String username, int index, EducationRequest request) {
        Profile profile = getProfileByUsername(username);

        Credentials credentials = profile.getCredentials();
        if (credentials == null) {
            throw new NotFoundException("Credentials not found for user: " + username);
        }

        List<Education> educationList = credentials.getEducation();
        validateIndex(index, educationList.size(), "Education");

        Education education = educationList.get(index);
        profileMapper.updateEducationFromRequest(request, education);

        profileRepository.save(profile);

        return profileMapper.toEducationDto(education);
    }

    @Override
    @Transactional
    public void deleteEducation(String username, int index) {
        Profile profile = getProfileByUsername(username);

        Credentials credentials = profile.getCredentials();
        if (credentials == null) {
            throw new NotFoundException("Credentials not found for user: " + username);
        }

        List<Education> educationList = credentials.getEducation();
        validateIndex(index, educationList.size(), "Education");

        educationList.remove(index);

        profileRepository.save(profile);

    }

    @Override
    @Transactional
    public void populateFromResume(String userId, ParsedResumeDto parsedResume) {
        Profile profile = profileRepository.findByUsername(userId)
                .orElseGet(() -> {
                    Profile p = new Profile();
                    p.setUsername(userId);
                    return profileRepository.save(p);
                });

        mergePersonal(profile, parsedResume.personal());

        for (ParsedResumeDto.ExperienceItemDto expDto : parsedResume.experience()) {
            profile.getExperiences().add(profileMapper.toExperience(expDto));
        }

        Credentials credentials = profile.getCredentials();
        if (credentials == null) {
            credentials = new Credentials();
            profile.setCredentials(credentials);
        }

        for (ParsedResumeDto.EducationDto eduDto : parsedResume.education()) {
            credentials.getEducation().add(profileMapper.toEducation(eduDto));
        }

        List<Certification> existingCerts = credentials.getCertifications();
        for (ParsedResumeDto.CertificationDto certDto : parsedResume.certifications()) {
            boolean duplicate = existingCerts.stream()
                    .anyMatch(c -> Objects.equals(c.getName(), certDto.name())
                               && Objects.equals(c.getIssuer(), certDto.issuer()));
            if (!duplicate) {
                existingCerts.add(profileMapper.toCertification(certDto));
            }
        }

        if (parsedResume.technicalSkills() != null) {
            mergeTechnicalSkills(profile, parsedResume.technicalSkills());
        }

        for (ParsedResumeDto.LanguageDto langDto : parsedResume.languages()) {
            profile.getLanguages().add(profileMapper.toLanguage(langDto));
        }

        mergeSocialLink(profile, parsedResume.socialLinks());

        profileRepository.save(profile);
    }

    private void mergePersonal(Profile profile, ParsedResumeDto.PersonalDto parsed) {
        if (parsed == null) return;
        Personal personal = profile.getPersonal();
        if (personal == null) {
            personal = new Personal();
            profile.setPersonal(personal);
        }
        if (isBlank(personal.getFirstName()) && !isBlank(parsed.firstName())) personal.setFirstName(parsed.firstName());
        if (isBlank(personal.getLastName()) && !isBlank(parsed.lastName())) personal.setLastName(parsed.lastName());
        if (isBlank(personal.getTitle()) && !isBlank(parsed.title())) personal.setTitle(parsed.title());
        if (isBlank(personal.getLocation()) && !isBlank(parsed.location())) personal.setLocation(parsed.location());
        if (isBlank(personal.getEmail()) && !isBlank(parsed.email())) personal.setEmail(parsed.email());
        if (isBlank(personal.getPhone()) && !isBlank(parsed.phone())) personal.setPhone(parsed.phone());
    }

    private void mergeTechnicalSkills(Profile profile, ParsedResumeDto.TechnicalSkillsDto parsed) {
        TechnicalSkills skills = profile.getTechnicalSkills();
        if (skills == null) {
            skills = new TechnicalSkills();
            profile.setTechnicalSkills(skills);
        }
        skills.setLanguages(mergeStringList(skills.getLanguages(), parsed.languages()));
        skills.setBackend(mergeStringList(skills.getBackend(), parsed.backend()));
        skills.setFrontend(mergeStringList(skills.getFrontend(), parsed.frontend()));
        skills.setDatabase(mergeStringList(skills.getDatabase(), parsed.database()));
        skills.setCloud(mergeStringList(skills.getCloud(), parsed.cloud()));
        skills.setTools(mergeStringList(skills.getTools(), parsed.tools()));
        skills.setMethodologies(mergeStringList(skills.getMethodologies(), parsed.methodologies()));
    }

    private void mergeSocialLink(Profile profile, ParsedResumeDto.SocialLinkDto parsed) {
        if (parsed == null) return;
        SocialLink socialLink = profile.getSocialLink();
        if (socialLink == null) {
            socialLink = new SocialLink();
            profile.setSocialLink(socialLink);
        }
        if (isBlank(socialLink.getGithub()) && !isBlank(parsed.github())) socialLink.setGithub(parsed.github());
        if (isBlank(socialLink.getLinkedin()) && !isBlank(parsed.linkedin())) socialLink.setLinkedin(parsed.linkedin());
        if (isBlank(socialLink.getDiscord()) && !isBlank(parsed.discord())) socialLink.setDiscord(parsed.discord());
        if (isBlank(socialLink.getTwitter()) && !isBlank(parsed.twitter())) socialLink.setTwitter(parsed.twitter());
        if (isBlank(socialLink.getInstagram()) && !isBlank(parsed.instagram())) socialLink.setInstagram(parsed.instagram());
    }

    private static List<String> mergeStringList(List<String> current, List<String> parsed) {
        LinkedHashSet<String> merged = new LinkedHashSet<>(current != null ? current : List.of());
        if (parsed != null) merged.addAll(parsed);
        return new ArrayList<>(merged);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // Helper methods

    private Profile getProfileByUsername(String username) {
        return profileRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Profile not found for user: " + username));
    }

    private void validateIndex(int index, int size, String entityName) {
        if (index < 0 || index >= size) {
            throw new BadRequestException(entityName + " index out of bounds: " + index);
        }
    }
}
