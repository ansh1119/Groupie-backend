package com.ansh.focus.controller;

import com.ansh.focus.dto.AuthResponse;
import com.ansh.focus.dto.ErrorResponse;
import com.ansh.focus.dto.LoginRequest;
import com.ansh.focus.dto.SignupRequest;
import com.ansh.focus.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Public endpoints for user registration and login. Returns a JWT Bearer token on success.")
@SecurityRequirements
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	@Operation(
			summary = "Register a new user",
			description = """
					Creates a new user account with a BCrypt-hashed password.
					Returns a JWT token immediately so the user can access protected endpoints without a separate login.
					Username and email must be unique across the system.
					"""
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "201",
					description = "User registered successfully",
					content = @Content(schema = @Schema(implementation = AuthResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "Validation failed (invalid username, email, or password format)",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "409",
					description = "Username or email already exists",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "Unexpected server error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
	})
	public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
		AuthResponse response = authService.signup(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	@Operation(
			summary = "Authenticate an existing user",
			description = """
					Authenticates a user by username **or** email plus password.
					Returns a JWT Bearer token valid for 10 years (configured via jwt.expiration-ms).
					"""
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Login successful",
					content = @Content(schema = @Schema(implementation = AuthResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "Validation failed (missing username or password)",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "Invalid username/email or password",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "Unexpected server error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
	})
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}
}
