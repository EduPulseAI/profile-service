package xyz.catuns.edupulse.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.catuns.edupulse.profile.domain.dto.CreateUserRequest;
import xyz.catuns.edupulse.profile.domain.dto.UserResponse;
import xyz.catuns.edupulse.profile.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "User API")
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/student")
    @Operation(
            summary = "Create Student",
            description = "Register a new student user")
    @ApiResponse(
            responseCode = "201",
            description = "HTTP Status CREATED")
    public ResponseEntity<UserResponse> createStudent(
            @Valid @RequestBody CreateUserRequest request
    ) {
        UserResponse response = userService.createUser(request, "STUDENT");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
