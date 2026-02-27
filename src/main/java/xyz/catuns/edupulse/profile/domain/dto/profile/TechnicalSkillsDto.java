package xyz.catuns.edupulse.profile.domain.dto.profile;

import java.util.List;

public record TechnicalSkillsDto(
        List<String> languages,
        List<String> backend,
        List<String> frontend,
        List<String> database,
        List<String> cloud,
        List<String> tools,
        List<String> methodologies
) {
}
