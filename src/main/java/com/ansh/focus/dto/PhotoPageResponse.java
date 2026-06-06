package com.ansh.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Paginated list of photos for a group")
public record PhotoPageResponse(
		@Schema(description = "Photos on the current page")
		List<PhotoResponse> photos,

		@Schema(description = "Current page index (0-based)", example = "0")
		int page,

		@Schema(description = "Page size", example = "20")
		int size,

		@Schema(description = "Total number of photos in the group", example = "42")
		long totalElements,

		@Schema(description = "Total number of pages", example = "3")
		int totalPages,

		@Schema(description = "Whether this is the last page", example = "false")
		boolean last
) {

	public static PhotoPageResponse from(Page<PhotoResponse> page) {
		return new PhotoPageResponse(
				page.getContent(),
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				page.isLast()
		);
	}
}
