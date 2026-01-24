package xyz.catuns.edupulse.profile.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;
import xyz.catuns.edupulse.profile.domain.entity.Profile;
import xyz.catuns.edupulse.profile.domain.entity.User;
import xyz.catuns.edupulse.profile.domain.mapper.ProfileMapper;
import xyz.catuns.edupulse.profile.domain.repository.UserRepository;
import xyz.catuns.edupulse.profile.service.ProfileService;
import xyz.catuns.spring.base.exception.controller.NotFoundException;

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
}
