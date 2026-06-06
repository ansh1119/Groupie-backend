package com.ansh.focus.dto;

import com.ansh.focus.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Authentication response containing JWT token and user profile (password never included)")
public record AuthResponse(
		@Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
		String token,

		@Schema(description = "Token type", example = "Bearer")
		String type,

		@Schema(description = "User database ID", example = "1")
		Long id,

		@Schema(description = "Username", example = "johndoe")
		String username,

		@Schema(description = "Email address", example = "john@example.com")
		String email,

		@Schema(description = "Account creation timestamp", example = "2026-05-27T12:00:00")
		LocalDateTime createdAt
) {

	private static final String BEARER = "Bearer";

	public static AuthResponse from(User user, String token) {
		return new AuthResponse(
				token,
				BEARER,
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getCreatedAt()
		);
	}
}
