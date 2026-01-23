package xyz.catuns.edupulse.profile.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import xyz.catuns.edupulse.profile.domain.dto.CreateUserRequest;
import xyz.catuns.edupulse.profile.domain.dto.UserResponse;
import xyz.catuns.edupulse.profile.domain.entity.User;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(
    componentModel = SPRING,
    injectionStrategy = CONSTRUCTOR,
    unmappedTargetPolicy = IGNORE)
public interface UserMapper {

    @Mapping(target = "email", source = "request.email")
    @Mapping(target = "role.name", source = "roleName")
    @Mapping(target = "profile.fullName", source = "request.name")
    User toEntity(CreateUserRequest request, String roleName);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "profile.fullName")
    @Mapping(target = "role", source = "role.name")
    UserResponse toResponse(User newUser);
}
