package com.ansh.focus.exception;

import com.ansh.focus.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				ErrorResponse.of(
						HttpStatus.BAD_REQUEST.value(),
						"Bad Request",
						"Validation failed",
						fieldErrors
				)
		);
	}

	@ExceptionHandler(DuplicateUserException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(
				ErrorResponse.of(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage())
		);
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
				ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage())
		);
	}

	@ExceptionHandler(GroupNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleGroupNotFound(GroupNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage())
		);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage())
		);
	}

	@ExceptionHandler(DuplicateGroupMemberException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateGroupMember(DuplicateGroupMemberException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(
				ErrorResponse.of(HttpStatus.CONFLICT.value(), "Conflict", ex.getMessage())
		);
	}

	@ExceptionHandler(NotGroupMemberException.class)
	public ResponseEntity<ErrorResponse> handleNotGroupMember(NotGroupMemberException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
				ErrorResponse.of(HttpStatus.FORBIDDEN.value(), "Forbidden", ex.getMessage())
		);
	}

	@ExceptionHandler(InvalidImageException.class)
	public ResponseEntity<ErrorResponse> handleInvalidImage(InvalidImageException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage())
		);
	}

	@ExceptionHandler(PhotoNotFoundException.class)
	public ResponseEntity<ErrorResponse> handlePhotoNotFound(PhotoNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				ErrorResponse.of(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage())
		);
	}

	@ExceptionHandler(InvalidTagTargetException.class)
	public ResponseEntity<ErrorResponse> handleInvalidTagTarget(InvalidTagTargetException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage())
		);
	}

	@ExceptionHandler(CloudinaryUploadException.class)
	public ResponseEntity<ErrorResponse> handleCloudinaryUpload(CloudinaryUploadException ex) {
		log.error("Cloudinary upload failed: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", ex.getMessage())
		);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
		log.warn("Upload rejected: file size exceeds limit", ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "Bad Request", "File size exceeds the maximum allowed limit")
		);
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<ErrorResponse> handleMissingPart(MissingServletRequestPartException ex) {
		log.error("Missing required multipart part: {}", ex.getRequestPartName(), ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				ErrorResponse.of(
						HttpStatus.BAD_REQUEST.value(),
						"Bad Request",
						"Missing required multipart part: " + ex.getRequestPartName()
				)
		);
	}

	@ExceptionHandler(MultipartException.class)
	public ResponseEntity<ErrorResponse> handleMultipart(MultipartException ex) {
		log.error("Multipart request failed: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				ErrorResponse.of(
						HttpStatus.BAD_REQUEST.value(),
						"Bad Request",
						"Invalid multipart request: " + ex.getMessage()
				)
		);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex) {
		log.error("Request body could not be read: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				ErrorResponse.of(
						HttpStatus.BAD_REQUEST.value(),
						"Bad Request",
						"Request body could not be read: " + ex.getMostSpecificCause().getMessage()
				)
		);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
		log.error("Unhandled exception [{}]: {}", ex.getClass().getName(), ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				ErrorResponse.of(
						HttpStatus.INTERNAL_SERVER_ERROR.value(),
						"Internal Server Error",
						"An unexpected error occurred"
				)
		);
	}
}
