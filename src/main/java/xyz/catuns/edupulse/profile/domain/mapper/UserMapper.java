package xyz.catuns.edupulse.profile.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import xyz.catuns.edupulse.profile.domain.dto.CreateUserRequest;
import xyz.catuns.edupulse.profile.domain.dto.UserResponse;
import xyz.catuns.edupulse.profile.domain.entity.Profile;
import xyz.catuns.edupulse.profile.domain.entity.User;
import xyz.catuns.edupulse.profile.domain.entity.embeddable.Personal;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
    componentModel = SPRING,
    injectionStrategy = CONSTRUCTOR,
    unmappedTargetPolicy = IGNORE)
public interface UserMapper {

    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "profile", source = "request", qualifiedByName = "toProfile")
    User toEntity(CreateUserRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "profile", qualifiedByName = "getFullName")
    @Mapping(target = "role", source = "role.name")
    UserResponse toResponse(User newUser);

    @Named("toProfile")
    default Profile toProfile(CreateUserRequest request) {
        Profile profile = new Profile();
        Personal personal = new Personal();
        String[] nameParts = request.name().split(" ", 2);
        personal.setFirstName(nameParts[0]);
        personal.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        personal.setEmail(request.email());
        profile.setPersonal(personal);
        return profile;
    }

    @Named("getFullName")
    default String getFullName(Profile profile) {
        if (profile == null || profile.getPersonal() == null) {
            return null;
        }
        Personal personal = profile.getPersonal();
        String firstName = personal.getFirstName() != null ? personal.getFirstName() : "";
        String lastName = personal.getLastName() != null ? personal.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }
}
