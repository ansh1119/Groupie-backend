package com.ansh.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for authenticating an existing user")
public record LoginRequest(
		@Schema(description = "Username or email address", example = "johndoe", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Username or email is required")
		String username,

		@Schema(description = "Account password", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Password is required")
		String password
) {
}
