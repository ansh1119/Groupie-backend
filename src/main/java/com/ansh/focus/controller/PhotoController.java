package com.ansh.focus.controller;

import com.ansh.focus.dto.ErrorResponse;
import com.ansh.focus.dto.PhotoPageResponse;
import com.ansh.focus.dto.PhotoUploadResponse;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups/{groupId}/photos")
@Tag(name = "Group Photos", description = "Upload and view group photos. Images are stored on Cloudinary; only URLs and metadata are persisted in PostgreSQL.")
@SecurityRequirement(name = "bearerAuth")
public class PhotoController {

	private static final Logger log = LoggerFactory.getLogger(PhotoController.class);

	private final PhotoService photoService;

	public PhotoController(PhotoService photoService) {
		this.photoService = photoService;
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
			summary = "Upload one or more photos to a group",
			description = """
					Uploads image files to Cloudinary and persists photo metadata in PostgreSQL.
					Only group members may upload. Each photo starts with processingStatus **PENDING**
					for future asynchronous ML processing.
					Supported formats: JPEG, PNG, GIF, WEBP. Max 10 MB per file.
					"""
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "201",
					description = "Photos uploaded successfully",
					content = @Content(schema = @Schema(implementation = PhotoUploadResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "Invalid or missing image files",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "Missing or invalid JWT token",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "403",
					description = "User is not a member of the group",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "Group not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "Cloudinary upload or server error",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
	})
	public ResponseEntity<PhotoUploadResponse> uploadPhotos(
			@Parameter(description = "Group UUID")
			@PathVariable UUID groupId,
			@Parameter(description = "One or more image files to upload")
			@RequestParam("files") List<MultipartFile> files,
			@AuthenticationPrincipal UserPrincipal principal
	) {
		if (principal == null) {
			log.error("Photo upload rejected: authenticated principal is null for groupId={}", groupId);
			throw new IllegalStateException("Authenticated user is required for photo upload");
		}

		log.info(
				"Photo upload request: groupId={}, uploader={}, fileCount={}",
				groupId,
				principal.getUsername(),
				files == null ? null : files.size()
		);
		if (files == null) {
			log.warn("Photo upload request received with null files list for groupId={}", groupId);
		} else {
			for (int i = 0; i < files.size(); i++) {
				MultipartFile file = files.get(i);
				if (file == null) {
					log.warn("Photo upload file[{}] is null for groupId={}", i, groupId);
					continue;
				}
				log.info(
						"Photo upload file[{}]: originalFilename={}, size={}, contentType={}, empty={}",
						i,
						file.getOriginalFilename(),
						file.getSize(),
						file.getContentType(),
						file.isEmpty()
				);
			}
		}

		PhotoUploadResponse response = photoService.uploadPhotos(groupId, files, principal.getUser());
		log.info(
				"Photo upload completed: groupId={}, uploader={}, uploadedCount={}",
				groupId,
				principal.getUsername(),
				response.count()
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	@Operation(
			summary = "Get all photos for a group (paginated)",
			description = """
					Returns all photos uploaded to the group by any member.
					Only group members may view photos. Results are ordered by createdAt descending.
					"""
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "Photos retrieved successfully",
					content = @Content(schema = @Schema(implementation = PhotoPageResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "Missing or invalid JWT token",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "403",
					description = "User is not a member of the group",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "Group not found",
					content = @Content(schema = @Schema(implementation = ErrorResponse.class))
			)
	})
	public ResponseEntity<PhotoPageResponse> getPhotos(
			@Parameter(description = "Group UUID")
			@PathVariable UUID groupId,
			@Parameter(description = "Page index (0-based)", example = "0")
			@RequestParam(defaultValue = "0") int page,
			@Parameter(description = "Page size (max 50)", example = "20")
			@RequestParam(defaultValue = "20") int size,
			@AuthenticationPrincipal UserPrincipal principal
	) {
		PhotoPageResponse response = photoService.getGroupPhotos(groupId, principal.getUser(), page, size);
		return ResponseEntity.ok(response);
	}
}
