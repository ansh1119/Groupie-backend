package com.ansh.focus.dto;

import com.ansh.focus.model.GroupMember;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "A user's membership in a group via the GroupMember junction entity")
public record GroupMemberResponse(
		@Schema(description = "GroupMember junction record UUID", example = "660e8400-e29b-41d4-a716-446655440001")
		UUID id,

		@Schema(description = "Member user ID", example = "1")
		Long userId,

		@Schema(description = "Member username", example = "johndoe")
		String username,

		@Schema(description = "Member role in the group", example = "OWNER", allowableValues = {"OWNER", "MEMBER"})
		String role,

		@Schema(description = "Timestamp when the user joined the group", example = "2026-05-27T12:00:00")
		LocalDateTime joinedAt
) {

	public static GroupMemberResponse from(GroupMember member) {
		return new GroupMemberResponse(
				member.getId(),
				member.getUser().getId(),
				member.getUser().getUsername(),
				member.getRole().name(),
				member.getJoinedAt()
		);
	}
}
