package xyz.catuns.edupulse.profile.service;

import xyz.catuns.edupulse.profile.domain.dto.profile.AboutDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.EducationDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.EducationRequest;
import xyz.catuns.edupulse.profile.domain.dto.profile.ExperienceItemDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.ExperienceRequest;
import xyz.catuns.edupulse.profile.domain.dto.profile.PersonalDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;
import xyz.catuns.edupulse.profile.domain.dto.profile.UpdateAboutRequest;
import xyz.catuns.edupulse.profile.domain.dto.profile.UpdatePersonalRequest;

public interface ProfileService {

    ProfileResponse getProfileForCurrentUser(String username);

    PersonalDto updatePersonalInfo(String username, UpdatePersonalRequest request);

    AboutDto updateAbout(String username, UpdateAboutRequest request);

    // Experience CRUD
    ExperienceItemDto addExperience(String username, ExperienceRequest request);

    ExperienceItemDto updateExperience(String username, int index, ExperienceRequest request);

    void deleteExperience(String username, int index);

    // Education CRUD
    EducationDto addEducation(String username, EducationRequest request);

    EducationDto updateEducation(String username, int index, EducationRequest request);

    void deleteEducation(String username, int index);
}
