package xyz.catuns.edupulse.profile.domain.dto.profile;

import java.util.List;

public record ExperienceItemDto(
        String title,
        String company,
        String location,
        String period,
        String description,
        List<String> responsibilities,
        List<String> technologies
) {
}
