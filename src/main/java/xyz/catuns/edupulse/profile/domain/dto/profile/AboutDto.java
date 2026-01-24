package xyz.catuns.edupulse.profile.domain.dto.profile;

import java.util.List;

public record AboutDto(
        String bio,
        List<String> focus,
        List<LanguageDto> languages,
        List<String> interests
) {
}
