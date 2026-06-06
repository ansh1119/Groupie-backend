package com.ansh.focus.dto;

import com.ansh.focus.model.Photo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Photo metadata stored in PostgreSQL (image binary hosted on Cloudinary)")
public record PhotoResponse(
		@Schema(description = "Photo UUID", example = "770e8400-e29b-41d4-a716-446655440002")
		UUID id,

		@Schema(description = "Group UUID this photo belongs to")
		UUID groupId,

		@Schema(description = "ID of the user who uploaded the photo", example = "1")
		Long uploadedById,

		@Schema(description = "Username of the uploader", example = "johndoe")
		String uploadedByUsername,

		@Schema(description = "Full-resolution Cloudinary secure URL")
		String imageUrl,

		@Schema(description = "Thumbnail Cloudinary URL (preferred for list views)")
		String thumbnailUrl,

		@Schema(description = "Cloudinary public ID (used for future deletion)")
		String publicId,

		@Schema(description = "Upload timestamp")
		LocalDateTime createdAt,

		@Schema(description = "ML processing status", example = "PENDING", allowableValues = {"PENDING", "PROCESSING", "COMPLETED", "FAILED"})
		String processingStatus
) {

	public static PhotoResponse from(Photo photo) {
		return new PhotoResponse(
				photo.getId(),
				photo.getGroup().getId(),
				photo.getUploadedBy().getId(),
				photo.getUploadedBy().getUsername(),
				photo.getImageUrl(),
				resolveThumbnailUrl(photo),
				photo.getPublicId(),
				photo.getCreatedAt(),
				photo.getProcessingStatus().name()
		);
	}

	private static String resolveThumbnailUrl(Photo photo) {
		if (photo.getThumbnailUrl() != null) {
			return photo.getThumbnailUrl();
		}
		return photo.getImageUrl();
	}

	public static PhotoResponse fromListItem(Photo photo) {
		return from(photo);
	}
}
