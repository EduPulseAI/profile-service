package xyz.catuns.edupulse.profile.domain.mapper;

import org.mapstruct.*;
import xyz.catuns.edupulse.profile.domain.dto.ParsedResumeDto;
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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePersonalFromRequest(UpdatePersonalRequest request, @MappingTarget Personal personal);

    AboutDto toAboutDto(About about);

    // About mapping

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

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateExperienceFromRequest(ExperienceRequest request, @MappingTarget Experience experience);

    // Education mapping
    Education toEducation(EducationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEducationFromRequest(EducationRequest request, @MappingTarget Education education);

    Profile toEntity(ProfileResponse resume);

    // ParsedResumeDto → entity mapping (used by profile population)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Experience toExperience(ParsedResumeDto.ExperienceItemDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Education toEducation(ParsedResumeDto.EducationDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Certification toCertification(ParsedResumeDto.CertificationDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Language toLanguage(ParsedResumeDto.LanguageDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SocialLink toSocialLink(ParsedResumeDto.SocialLinkDto dto);
}
