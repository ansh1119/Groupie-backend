package com.ansh.focus.controller;

import com.ansh.focus.dto.AddGroupMemberRequest;
import com.ansh.focus.dto.CreateGroupRequest;
import com.ansh.focus.dto.ErrorResponse;
import com.ansh.focus.dto.GroupMemberResponse;
import com.ansh.focus.dto.GroupResponse;
import com.ansh.focus.security.UserPrincipal;
import com.ansh.focus.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/groups")
@Tag(name = "Groups", description = "Protected endpoints for creating and managing focus groups. Requires JWT authentication.")
@SecurityRequirement(name = "bearerAuth")
public class GroupController {

	private final GroupService groupService;

	public GroupController(GroupService groupService) {
		this.groupService = groupService;
	}

	@PostMapping
	@Operation(
			summary = "Create a new group",
			description = """
					Creates a group and automatically adds the authenticated user as the **OWNER** via a `GroupMember` junction record.
					Does not accept initial member IDs — only the creator is added at creation time.
					Group and GroupMember IDs are UUIDs; user IDs remain Long.
					"""
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "201",
					description = "Group created successfully with creator as OWNER",
					content = @Content(schema = @Schema(implementation = GroupResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "Validation failed (missing or invalid name/description)",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "Missing or invalid JWT token",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "Unexpected server error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
	})
	public ResponseEntity<GroupResponse> createGroup(
			@Valid @RequestBody CreateGroupRequest request,
			@AuthenticationPrincipal UserPrincipal principal
	) {
		GroupResponse response = groupService.createGroup(request, principal.getUser());
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/{groupId}/members")
	@Operation(
			summary = "Add a member to a group",
			description = """
					Adds an existing user to a group with the **MEMBER** role via a new `GroupMember` junction record.
					Any authenticated user may invite members — no role or membership checks are enforced yet.
					The group owner retains the **OWNER** role; invited users are always added as **MEMBER**.
					"""
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "201",
					description = "Member added successfully",
					content = @Content(schema = @Schema(implementation = GroupMemberResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "Validation failed (missing user ID)",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "Missing or invalid JWT token",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "Group or user not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "409",
					description = "User is already a member of this group",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "Unexpected server error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
	})
	public ResponseEntity<GroupMemberResponse> addMember(
			@Parameter(description = "Group UUID", example = "550e8400-e29b-41d4-a716-446655440000")
			@PathVariable UUID groupId,
			@Valid @RequestBody AddGroupMemberRequest request,
			@AuthenticationPrincipal UserPrincipal principal
	) {
		GroupMemberResponse response = groupService.addMember(groupId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
