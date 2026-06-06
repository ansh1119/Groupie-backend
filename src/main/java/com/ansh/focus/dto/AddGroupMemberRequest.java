package com.ansh.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for adding a user to a group")
public record AddGroupMemberRequest(
		@Schema(description = "Username of the user to add as a MEMBER", example = "janedoe", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank(message = "Username is required")
		String username
) {
}
