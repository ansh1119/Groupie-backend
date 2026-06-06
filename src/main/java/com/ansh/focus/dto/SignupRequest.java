package com.ansh.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for registering a new user account")
public record SignupRequest(
		@Schema(description = "Unique username", example = "johndoe", minLength = 3, maxLength = 50, requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Username is required")
		@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
		String username,

		@Schema(description = "Unique email address", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Email is required")
		@Email(message = "Email must be valid")
		String email,

		@Schema(description = "Plain-text password (BCrypt-hashed server-side; never returned in responses)", example = "password123", minLength = 8, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Password is required")
		@Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
		String password
) {
}
