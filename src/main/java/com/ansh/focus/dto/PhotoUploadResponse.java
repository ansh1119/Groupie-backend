package com.ansh.focus.dto;

import com.ansh.focus.model.Photo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response after uploading one or more photos to a group")
public record PhotoUploadResponse(
		@Schema(description = "Successfully uploaded photos")
		List<PhotoResponse> photos,

		@Schema(description = "Number of photos uploaded", example = "3")
		int count
) {

	public static PhotoUploadResponse from(List<Photo> photos) {
		List<PhotoResponse> responses = photos.stream()
				.map(PhotoResponse::from)
				.toList();
		return new PhotoUploadResponse(responses, responses.size());
	}
}
