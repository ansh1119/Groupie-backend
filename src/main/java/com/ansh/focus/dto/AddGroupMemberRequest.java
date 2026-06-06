package com.ansh.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for adding a user to a group")
public record AddGroupMemberRequest(
		@Schema(description = "ID of the user to add as a MEMBER", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull(message = "User ID is required")
		Long userId
) {
}
