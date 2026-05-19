package xyz.catuns.edupulse.profile.domain.dto;

import java.util.List;

public record ParsedResumeDto(
        PersonalDto personal,
        List<ExperienceItemDto> experience,
        List<EducationDto> education,
        List<CertificationDto> certifications,
        TechnicalSkillsDto technicalSkills,
        List<LanguageDto> languages,
        SocialLinkDto socialLinks
) {
    public ParsedResumeDto {
        if (experience == null) experience = List.of();
        if (education == null) education = List.of();
        if (certifications == null) certifications = List.of();
        if (languages == null) languages = List.of();
    }

    public record PersonalDto(
            String firstName,
            String lastName,
            String title,
            String location,
            String email,
            String phone
    ) {}

    public record ExperienceItemDto(
            String title,
            String company,
            String period,
            String location,
            String description,
            List<String> responsibilities,
            List<String> technologies
    ) {
        public ExperienceItemDto {
            if (responsibilities == null) responsibilities = List.of();
            if (technologies == null) technologies = List.of();
        }
    }

    public record EducationDto(
            String degree,
            String institution,
            String year
    ) {}

    public record CertificationDto(
            String name,
            String issuer,
            String date
    ) {}

    public record TechnicalSkillsDto(
            List<String> languages,
            List<String> backend,
            List<String> frontend,
            List<String> database,
            List<String> cloud,
            List<String> tools,
            List<String> methodologies
    ) {
        public TechnicalSkillsDto {
            if (languages == null) languages = List.of();
            if (backend == null) backend = List.of();
            if (frontend == null) frontend = List.of();
            if (database == null) database = List.of();
            if (cloud == null) cloud = List.of();
            if (tools == null) tools = List.of();
            if (methodologies == null) methodologies = List.of();
        }
    }

    public record LanguageDto(
            String name,
            String proficiency
    ) {}

    public record SocialLinkDto(
            String github,
            String linkedin,
            String discord,
            String twitter,
            String instagram
    ) {}
}
