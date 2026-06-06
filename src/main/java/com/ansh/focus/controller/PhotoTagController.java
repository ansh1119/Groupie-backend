package com.ansh.focus.controller;

import com.ansh.focus.dto.ErrorResponse;
import com.ansh.focus.dto.PhotoTagResponse;
import com.ansh.focus.dto.TagPhotoRequest;
import com.ansh.focus.security.UserPrincipal;
import com.ansh.focus.service.PhotoTagService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/photos/{photoId}/tags")
@Tag(name = "Photo Tags", description = "Manual and future AI-generated user tags on photos. Both use the same PhotoUserTag table with tagType MANUAL or AUTO.")
@SecurityRequirement(name = "bearerAuth")
public class PhotoTagController {

	private final PhotoTagService photoTagService;

	public PhotoTagController(PhotoTagService photoTagService) {
		this.photoTagService = photoTagService;
	}

	@PostMapping
	@Operation(
			summary = "Tag users in a photo",
			description = """
					Manually tags one or more group members in a photo.
					Only group members may tag. Tagged users must belong to the same group as the photo.
					Duplicate tags are skipped silently. Tags are created with tagType **MANUAL**.
					"""
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Tags applied (returns all tags for the photo)",
					content = @Content(schema = @Schema(implementation = PhotoTagResponse.class))),
			@ApiResponse(responseCode = "400", description = "Validation failed or tagged user is not a group member",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Requester is not a group member",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Photo or user not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<PhotoTagResponse> tagUsers(
			@PathVariable UUID photoId,
			@Valid @RequestBody TagPhotoRequest request,
			@AuthenticationPrincipal UserPrincipal principal
	) {
		PhotoTagResponse response = photoTagService.tagUsers(photoId, request, principal.getUser());
		return ResponseEntity.ok(response);
	}

	@GetMapping
	@Operation(summary = "Get all tags for a photo", description = "Returns all users tagged in the photo. Only group members may view tags.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Tags retrieved",
					content = @Content(schema = @Schema(implementation = PhotoTagResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Requester is not a group member",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Photo not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<PhotoTagResponse> getTags(
			@PathVariable UUID photoId,
			@AuthenticationPrincipal UserPrincipal principal
	) {
		PhotoTagResponse response = photoTagService.getTags(photoId, principal.getUser());
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{userId}")
	@Operation(
			summary = "Remove a tag from a photo",
			description = "Removes a user tag from a photo. Only group members may remove tags. Deleting a non-existing tag succeeds silently."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Tag removed or did not exist"),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "403", description = "Requester is not a group member",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "404", description = "Photo not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<Void> removeTag(
			@PathVariable UUID photoId,
			@Parameter(description = "ID of the tagged user to remove") @PathVariable Long userId,
			@AuthenticationPrincipal UserPrincipal principal
	) {
		photoTagService.removeTag(photoId, userId, principal.getUser());
		return ResponseEntity.noContent().build();
	}
}
