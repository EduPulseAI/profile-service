package xyz.catuns.edupulse.profile.domain.dto.profile;

import java.util.List;

public record PersonalDto(
        String firstName,
        String lastName,
        String title,
        String location,
        String avatar,
        String email,
        String phone,
        String workingHours,
        boolean availableForWork,
        List<String> badges,
        SocialLinkDto social
) {
}
