package xyz.catuns.edupulse.profile.service;

import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;

public interface ProfileService {

    ProfileResponse getProfileForCurrentUser(String username);
}
