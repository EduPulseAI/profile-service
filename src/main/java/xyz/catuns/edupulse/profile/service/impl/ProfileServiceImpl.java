package xyz.catuns.edupulse.profile.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.catuns.edupulse.profile.domain.dto.profile.AboutDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.EducationDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.EducationRequest;
import xyz.catuns.edupulse.profile.domain.dto.profile.ExperienceItemDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.ExperienceRequest;
import xyz.catuns.edupulse.profile.domain.dto.profile.PersonalDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;
import xyz.catuns.edupulse.profile.domain.dto.profile.UpdateAboutRequest;
import xyz.catuns.edupulse.profile.domain.dto.profile.UpdatePersonalRequest;
import xyz.catuns.edupulse.profile.domain.entity.Profile;
import xyz.catuns.edupulse.profile.domain.entity.User;
import xyz.catuns.edupulse.profile.domain.entity.embeddable.About;
import xyz.catuns.edupulse.profile.domain.entity.embeddable.Credentials;
import xyz.catuns.edupulse.profile.domain.entity.embeddable.Education;
import xyz.catuns.edupulse.profile.domain.entity.embeddable.Experience;
import xyz.catuns.edupulse.profile.domain.entity.embeddable.Personal;
import xyz.catuns.edupulse.profile.domain.mapper.ProfileMapper;
import xyz.catuns.edupulse.profile.domain.repository.UserRepository;
import xyz.catuns.edupulse.profile.service.ProfileService;
import xyz.catuns.spring.base.exception.controller.BadRequestException;
import xyz.catuns.spring.base.exception.controller.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileForCurrentUser(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Profile profile = user.getProfile();
        if (profile == null) {
            throw new NotFoundException("Profile not found for user: " + username);
        }

        return profileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public PersonalDto updatePersonalInfo(String username, UpdatePersonalRequest request) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Profile profile = user.getProfile();
        if (profile == null) {
            throw new NotFoundException("Profile not found for user: " + username);
        }

        Personal personal = profile.getPersonal();
        if (personal == null) {
            personal = new Personal();
            profile.setPersonal(personal);
        }

        profileMapper.updatePersonalFromRequest(request, personal);

        return profileMapper.toPersonalDto(personal);
    }

    @Override
    @Transactional
    public AboutDto updateAbout(String username, UpdateAboutRequest request) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Profile profile = user.getProfile();
        if (profile == null) {
            throw new NotFoundException("Profile not found for user: " + username);
        }

        About about = profile.getAbout();
        if (about == null) {
            about = new About();
            profile.setAbout(about);
        }

        // Update simple fields
        about.setBio(request.bio());

        // Update lists - clear and repopulate
        about.getFocus().clear();
        if (request.focus() != null) {
            about.getFocus().addAll(request.focus());
        }

        about.getInterests().clear();
        if (request.interests() != null) {
            about.getInterests().addAll(request.interests());
        }

        about.getLanguages().clear();
        if (request.languages() != null) {
            about.getLanguages().addAll(profileMapper.toLanguageList(request.languages()));
        }

        return profileMapper.toAboutDto(about);
    }

    // Experience CRUD operations

    @Override
    @Transactional
    public ExperienceItemDto addExperience(String username, ExperienceRequest request) {
        Profile profile = getProfileByUsername(username);

        Experience experience = profileMapper.toExperience(request);
        profile.getExperiences().add(experience);

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

        return profileMapper.toExperienceItemDto(experience);
    }

    @Override
    @Transactional
    public void deleteExperience(String username, int index) {
        Profile profile = getProfileByUsername(username);

        List<Experience> experiences = profile.getExperiences();
        validateIndex(index, experiences.size(), "Experience");

        experiences.remove(index);
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
    }

    // Helper methods

    private Profile getProfileByUsername(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Profile profile = user.getProfile();
        if (profile == null) {
            throw new NotFoundException("Profile not found for user: " + username);
        }

        return profile;
    }

    private void validateIndex(int index, int size, String entityName) {
        if (index < 0 || index >= size) {
            throw new BadRequestException(entityName + " index out of bounds: " + index);
        }
    }
}
