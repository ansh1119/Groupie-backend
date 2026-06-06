package com.ansh.focus.dto;

import com.ansh.focus.model.PhotoUserTag;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "A user tagged in a photo")
public record TaggedUserResponse(
		@Schema(description = "Tag record UUID")
		UUID id,

		@Schema(description = "Tagged user ID", example = "2")
		Long userId,

		@Schema(description = "Tagged user username", example = "janedoe")
		String username,

		@Schema(description = "ID of the user who created the tag", example = "1")
		Long taggedById,

		@Schema(description = "Username of the user who created the tag", example = "johndoe")
		String taggedByUsername,

		@Schema(description = "How the tag was created", example = "MANUAL", allowableValues = {"MANUAL", "AUTO"})
		String tagType,

		@Schema(description = "When the tag was created")
		LocalDateTime createdAt
) {

	public static TaggedUserResponse from(PhotoUserTag tag) {
		return new TaggedUserResponse(
				tag.getId(),
				tag.getUser().getId(),
				tag.getUser().getUsername(),
				tag.getTaggedBy().getId(),
				tag.getTaggedBy().getUsername(),
				tag.getTagType().name(),
				tag.getCreatedAt()
		);
	}
}
