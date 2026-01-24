package xyz.catuns.edupulse.profile.domain.dto.profile;

import java.util.List;

public record TechnicalSkillsDto(
        List<String> design,
        List<String> development,
        List<String> uxMethods,
        List<String> softSkills
) {
}
