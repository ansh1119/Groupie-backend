package com.ansh.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "All user tags for a photo")
public record PhotoTagResponse(
		@Schema(description = "Photo UUID")
		UUID photoId,

		@Schema(description = "Tagged users in the photo")
		List<TaggedUserResponse> tags
) {

	public static PhotoTagResponse of(UUID photoId, List<TaggedUserResponse> tags) {
		return new PhotoTagResponse(photoId, tags);
	}
}
