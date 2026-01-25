package xyz.catuns.edupulse.profile.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import xyz.catuns.edupulse.profile.domain.dto.profile.AboutDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.CertificationDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.CredentialsDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.EducationDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.EducationRequest;
import xyz.catuns.edupulse.profile.domain.dto.profile.ExperienceItemDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.ExperienceRequest;
import xyz.catuns.edupulse.profile.domain.dto.profile.LanguageDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.PersonalDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;
import xyz.catuns.edupulse.profile.domain.dto.profile.SocialLinkDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.TechnicalSkillsDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.UpdatePersonalRequest;
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

    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "badges", ignore = true)
    @Mapping(target = "social", ignore = true)
    void updatePersonalFromRequest(UpdatePersonalRequest request, @MappingTarget Personal personal);

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

    // Experience mapping
    Experience toExperience(ExperienceRequest request);

    void updateExperienceFromRequest(ExperienceRequest request, @MappingTarget Experience experience);

    // Education mapping
    Education toEducation(EducationRequest request);

    void updateEducationFromRequest(EducationRequest request, @MappingTarget Education education);
}
