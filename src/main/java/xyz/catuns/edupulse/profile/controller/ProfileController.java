package xyz.catuns.edupulse.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.catuns.edupulse.profile.domain.dto.profile.PersonalDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.ProfileResponse;
import xyz.catuns.edupulse.profile.domain.dto.profile.UpdatePersonalRequest;
import xyz.catuns.edupulse.profile.service.ProfileService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/profiles")
@Tag(name = "Profile API")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    @Operation(
            summary = "Get Current User Profile",
            description = "Retrieve the profile of the currently authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Profile retrieved successfully")
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - authentication required")
    @ApiResponse(
            responseCode = "404",
            description = "Profile not found")
    public ResponseEntity<ProfileResponse> getMyProfile(
            @AuthenticationPrincipal String username
    ) {
        ProfileResponse profile = profileService.getProfileForCurrentUser(username);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/personal")
    @Operation(
            summary = "Update Personal Information",
            description = "Update the personal information of the currently authenticated user's profile")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Personal information updated successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request data")
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - authentication required")
    @ApiResponse(
            responseCode = "404",
            description = "Profile not found")
    public ResponseEntity<PersonalDto> updatePersonalInfo(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody UpdatePersonalRequest request
    ) {
        PersonalDto updatedPersonal = profileService.updatePersonalInfo(username, request);
        return ResponseEntity.ok(updatedPersonal);
    }
}
