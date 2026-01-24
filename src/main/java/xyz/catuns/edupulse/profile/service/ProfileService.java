package xyz.catuns.edupulse.profile.service;

import xyz.catuns.edupulse.profile.domain.dto.profile.PersonalDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;
import xyz.catuns.edupulse.profile.domain.dto.profile.UpdatePersonalRequest;

public interface ProfileService {

    ProfileResponse getProfileForCurrentUser(String username);

    PersonalDto updatePersonalInfo(String username, UpdatePersonalRequest request);
}
