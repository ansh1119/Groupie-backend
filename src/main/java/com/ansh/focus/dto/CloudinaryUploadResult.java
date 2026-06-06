package com.ansh.focus.dto;

public record CloudinaryUploadResult(
		String imageUrl,
		String thumbnailUrl,
		String publicId
) {
}
