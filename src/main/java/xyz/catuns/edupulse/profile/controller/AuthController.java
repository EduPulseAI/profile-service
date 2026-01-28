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
import xyz.catuns.edupulse.profile.domain.dto.LoginRequest;
import xyz.catuns.edupulse.profile.domain.dto.AuthResponse;
import xyz.catuns.edupulse.profile.domain.dto.RefreshTokenRequest;
import xyz.catuns.edupulse.profile.service.AuthService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API")
public class AuthController {

    private final AuthService authService;


    @PostMapping(value = "/login")
    @Operation(
            summary = "Login",
            description = "REST API Post to Login")
    @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping(value = "/refresh")
    @Operation(
            summary = "Refresh Token",
            description = "Creates new token with refresh token")
    @ApiResponse(responseCode = "200",description = "HTTP Status OK")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ){
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
