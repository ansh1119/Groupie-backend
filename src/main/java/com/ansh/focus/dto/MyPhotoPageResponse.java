package com.ansh.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Paginated list of photos in which the authenticated user is tagged")
public record MyPhotoPageResponse(
		@Schema(description = "Photos on the current page")
		List<MyPhotoResponse> photos,

		@Schema(description = "Current page index (0-based)", example = "0")
		int page,

		@Schema(description = "Page size", example = "20")
		int size,

		@Schema(description = "Total number of tagged photos", example = "15")
		long totalElements,

		@Schema(description = "Total number of pages", example = "1")
		int totalPages,

		@Schema(description = "Whether this is the last page", example = "true")
		boolean last
) {

	public static MyPhotoPageResponse from(Page<MyPhotoResponse> page) {
		return new MyPhotoPageResponse(
				page.getContent(),
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				page.isLast()
		);
	}
}
