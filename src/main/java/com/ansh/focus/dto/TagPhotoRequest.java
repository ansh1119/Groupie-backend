package com.ansh.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request body for manually tagging users in a photo")
public record TagPhotoRequest(
		@Schema(description = "IDs of group members to tag in the photo", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty(message = "At least one user ID is required")
		List<Long> userIds
) {
}
