package com.ansh.focus.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Standard error response returned by GlobalExceptionHandler")
public record ErrorResponse(
		@Schema(description = "Error timestamp", example = "2026-05-27T12:00:00")
		LocalDateTime timestamp,

		@Schema(description = "HTTP status code", example = "400")
		int status,

		@Schema(description = "HTTP status reason phrase", example = "Bad Request")
		String error,

		@Schema(description = "Human-readable error message", example = "Validation failed")
		String message,

		@Schema(description = "Field-level validation errors (present on 400 responses only)", nullable = true, example = "{\"username\": \"Username is required\"}")
		Map<String, String> fieldErrors
) {

	public static ErrorResponse of(int status, String error, String message) {
		return new ErrorResponse(LocalDateTime.now(), status, error, message, null);
	}

	public static ErrorResponse of(int status, String error, String message, Map<String, String> fieldErrors) {
		return new ErrorResponse(LocalDateTime.now(), status, error, message, fieldErrors);
	}
}
