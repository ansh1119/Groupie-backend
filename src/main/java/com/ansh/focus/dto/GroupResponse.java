package com.ansh.focus.dto;

import com.ansh.focus.model.Group;
import com.ansh.focus.model.GroupMember;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Group details including creator info and current members")
public record GroupResponse(
		@Schema(description = "Group UUID", example = "550e8400-e29b-41d4-a716-446655440000")
		UUID id,

		@Schema(description = "Group name", example = "Study Group")
		String name,

		@Schema(description = "Group description", example = "Daily focus sessions")
		String description,

		@Schema(description = "ID of the user who created the group", example = "1")
		Long createdById,

		@Schema(description = "Username of the group creator", example = "johndoe")
		String createdByUsername,

		@Schema(description = "Group creation timestamp", example = "2026-05-27T12:00:00")
		LocalDateTime createdAt,

		@Schema(description = "List of group members (creator as OWNER on creation)")
		List<GroupMemberResponse> members
) {

	public static GroupResponse from(Group group, List<GroupMember> members) {
		List<GroupMemberResponse> memberResponses = members.stream()
				.map(GroupMemberResponse::from)
				.toList();

		return new GroupResponse(
				group.getId(),
				group.getName(),
				group.getDescription(),
				group.getCreatedBy().getId(),
				group.getCreatedBy().getUsername(),
				group.getCreatedAt(),
				memberResponses
		);
	}
}
