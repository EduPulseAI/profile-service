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
import xyz.catuns.edupulse.profile.domain.dto.profile.EducationDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.EducationRequest;
import xyz.catuns.edupulse.profile.domain.dto.profile.ExperienceItemDto;
import xyz.catuns.edupulse.profile.domain.dto.profile.ExperienceRequest;
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
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    @ApiResponse(responseCode = "404", description = "Profile not found")
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
    @ApiResponse(responseCode = "200", description = "Personal information updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<PersonalDto> updatePersonalInfo(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody UpdatePersonalRequest request
    ) {
        PersonalDto updatedPersonal = profileService.updatePersonalInfo(username, request);
        return ResponseEntity.ok(updatedPersonal);
    }


    /* ──────────────────────────────────────────────
            Experience CRUD Endpoints
    ────────────────────────────────────────────── */


    @PostMapping("/experience")
    @Operation(
            summary = "Add Experience",
            description = "Add a new work experience entry to the current user's profile")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Experience added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<ExperienceItemDto> addExperience(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody ExperienceRequest request
    ) {
        ExperienceItemDto experience = profileService.addExperience(username, request);
        return ResponseEntity.ok(experience);
    }

    @PutMapping("/experience/{index}")
    @Operation(
            summary = "Update Experience",
            description = "Update a work experience entry by index")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Experience updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data or index out of bounds")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<ExperienceItemDto> updateExperience(
            @AuthenticationPrincipal String username,
            @PathVariable int index,
            @Valid @RequestBody ExperienceRequest request
    ) {
        ExperienceItemDto experience = profileService.updateExperience(username, index, request);
        return ResponseEntity.ok(experience);
    }

    @DeleteMapping("/experience/{index}")
    @Operation(
            summary = "Delete Experience",
            description = "Delete a work experience entry by index")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Experience deleted successfully")
    @ApiResponse(responseCode = "400", description = "Index out of bounds")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<Void> deleteExperience(
            @AuthenticationPrincipal String username,
            @PathVariable int index
    ) {
        profileService.deleteExperience(username, index);
        return ResponseEntity.noContent().build();
    }


    /* ──────────────────────────────────────────────
            Education CRUD Endpoints
    ────────────────────────────────────────────── */

    @PostMapping("/education")
    @Operation(
            summary = "Add Education",
            description = "Add a new education entry to the current user's profile")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Education added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<EducationDto> addEducation(
            @AuthenticationPrincipal String username,
            @Valid @RequestBody EducationRequest request
    ) {
        EducationDto education = profileService.addEducation(username, request);
        return ResponseEntity.ok(education);
    }

    @PutMapping("/education/{index}")
    @Operation(
            summary = "Update Education",
            description = "Update an education entry by index")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Education updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data or index out of bounds")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<EducationDto> updateEducation(
            @AuthenticationPrincipal String username,
            @PathVariable int index,
            @Valid @RequestBody EducationRequest request
    ) {
        EducationDto education = profileService.updateEducation(username, index, request);
        return ResponseEntity.ok(education);
    }

    @DeleteMapping("/education/{index}")
    @Operation(
            summary = "Delete Education",
            description = "Delete an education entry by index")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Education deleted successfully")
    @ApiResponse(responseCode = "400", description = "Index out of bounds")
    @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required")
    @ApiResponse(responseCode = "404", description = "Profile not found")
    public ResponseEntity<Void> deleteEducation(
            @AuthenticationPrincipal String username,
            @PathVariable int index
    ) {
        profileService.deleteEducation(username, index);
        return ResponseEntity.noContent().build();
    }
}
