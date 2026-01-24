package xyz.catuns.edupulse.profile.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import xyz.catuns.edupulse.profile.domain.dto.profile.*;
import xyz.catuns.edupulse.profile.domain.entity.Profile;
import xyz.catuns.edupulse.profile.domain.entity.embeddable.*;

import java.util.List;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
    componentModel = SPRING,
    injectionStrategy = CONSTRUCTOR,
    unmappedTargetPolicy = IGNORE)
public interface ProfileMapper {

    @Mapping(target = "experience", source = "experiences")
    ProfileResponse toResponse(Profile profile);

    PersonalDto toPersonalDto(Personal personal);

    SocialLinkDto toSocialLinkDto(SocialLink socialLink);

    AboutDto toAboutDto(About about);

    LanguageDto toLanguageDto(Language language);

    List<LanguageDto> toLanguageDtoList(List<Language> languages);

    ExperienceItemDto toExperienceItemDto(Experience experience);

    List<ExperienceItemDto> toExperienceItemDtoList(List<Experience> experiences);

    CredentialsDto toCredentialsDto(Credentials credentials);

    CertificationDto toCertificationDto(Certification certification);

    List<CertificationDto> toCertificationDtoList(List<Certification> certifications);

    EducationDto toEducationDto(Education education);

    List<EducationDto> toEducationDtoList(List<Education> educations);

    TechnicalSkillsDto toTechnicalSkillsDto(TechnicalSkills technicalSkills);
}
