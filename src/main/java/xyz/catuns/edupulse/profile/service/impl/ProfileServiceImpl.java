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

import java.util.List;

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
        // Implemented in Story 3 — profile population from parsed resume data
        throw new UnsupportedOperationException("populateFromResume not yet implemented");
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
