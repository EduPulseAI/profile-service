package xyz.catuns.edupulse.profile.domain.dto.profile;

import java.util.List;

public record ExperienceItemDto(
        String title,
        String company,
        String period,
        String description,
        List<String> achievements,
        List<String> technologies
) {
}
