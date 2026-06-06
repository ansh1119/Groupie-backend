package com.ansh.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating a new focus group")
public record CreateGroupRequest(
		@Schema(description = "Group display name", example = "Study Group", maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Name is required")
		@Size(max = 100, message = "Name must be at most 100 characters")
		String name,

		@Schema(description = "Optional group description", example = "Daily focus sessions", maxLength = 500)
		@Size(max = 500, message = "Description must be at most 500 characters")
		String description
) {
}
