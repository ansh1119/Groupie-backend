package com.ansh.focus.controller;

import com.ansh.focus.dto.ErrorResponse;
import com.ansh.focus.dto.MyPhotoPageResponse;
import com.ansh.focus.security.UserPrincipal;
import com.ansh.focus.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me/photos")
@Tag(name = "My Photos", description = "Personalized photo album — photos in which the authenticated user is tagged.")
@SecurityRequirement(name = "bearerAuth")
public class UserPhotoController {

	private final PhotoService photoService;

	public UserPhotoController(PhotoService photoService) {
		this.photoService = photoService;
	}

	@GetMapping
	@Operation(
			summary = "Get my tagged photos",
			description = """
					Returns photos in which the authenticated user is tagged, ordered by newest first.
					Uses an efficient JOIN query on photo_user_tags. Thumbnail URLs are included for list display.
					"""
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Photos retrieved",
					content = @Content(schema = @Schema(implementation = MyPhotoPageResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	})
	public ResponseEntity<MyPhotoPageResponse> getMyPhotos(
			@Parameter(description = "Page index (0-based)", example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size (max 50)", example = "20")
			@RequestParam(defaultValue = "20") int size,
			@AuthenticationPrincipal UserPrincipal principal
	) {
		MyPhotoPageResponse response = photoService.getMyPhotos(principal.getUser(), page, size);
		return ResponseEntity.ok(response);
	}
}
