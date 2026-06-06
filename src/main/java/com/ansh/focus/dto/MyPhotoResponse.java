package com.ansh.focus.dto;

import com.ansh.focus.model.Photo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Photo in which the authenticated user is tagged (personal album item)")
public record MyPhotoResponse(
		@Schema(description = "Photo UUID")
		UUID id,

		@Schema(description = "Group UUID the photo belongs to")
		UUID groupId,

		@Schema(description = "Thumbnail URL for list display")
		String thumbnailUrl,

		@Schema(description = "Full-resolution image URL")
		String imageUrl,

		@Schema(description = "Username of the uploader", example = "johndoe")
		String uploadedByUsername,

		@Schema(description = "Photo upload timestamp")
		LocalDateTime createdAt,

		@Schema(description = "ML processing status", example = "PENDING")
		String processingStatus
) {

	public static MyPhotoResponse from(Photo photo) {
		String thumbnailUrl = photo.getThumbnailUrl() != null ? photo.getThumbnailUrl() : photo.getImageUrl();
		return new MyPhotoResponse(
				photo.getId(),
				photo.getGroup().getId(),
				thumbnailUrl,
				photo.getImageUrl(),
				photo.getUploadedBy().getUsername(),
				photo.getCreatedAt(),
				photo.getProcessingStatus().name()
		);
	}
}
