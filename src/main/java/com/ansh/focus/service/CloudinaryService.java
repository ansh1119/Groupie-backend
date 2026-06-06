package com.ansh.focus.service;

import com.ansh.focus.dto.CloudinaryUploadResult;
import com.ansh.focus.exception.CloudinaryUploadException;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class CloudinaryService {

	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
			"image/jpeg",
			"image/png",
			"image/gif",
			"image/webp"
	);

	private static final int THUMBNAIL_WIDTH = 300;
	private static final int THUMBNAIL_HEIGHT = 300;

	private final Cloudinary cloudinary;
	private final String baseFolder;

	public CloudinaryService(
			Cloudinary cloudinary,
			@Value("${cloudinary.folder:focus/groups}") String baseFolder
	) {
		this.cloudinary = cloudinary;
		this.baseFolder = baseFolder;
	}

	public CloudinaryUploadResult upload(MultipartFile file, UUID groupId) {
		@SuppressWarnings("unchecked")
		Map<String, Object> params = ObjectUtils.asMap(
				"folder", baseFolder + "/" + groupId,
				"resource_type", "image",
				"overwrite", false
		);

		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), params);
			String imageUrl = (String) result.get("secure_url");
			String publicId = (String) result.get("public_id");

			if (imageUrl == null || publicId == null) {
				throw new CloudinaryUploadException("Cloudinary upload succeeded but returned incomplete metadata");
			}

			String thumbnailUrl = buildThumbnailUrl(publicId);
			return new CloudinaryUploadResult(imageUrl, thumbnailUrl, publicId);
		} catch (IOException ex) {
			throw new CloudinaryUploadException("Failed to read image file for upload", ex);
		} catch (Exception ex) {
			throw new CloudinaryUploadException("Failed to upload image to Cloudinary: " + ex.getMessage(), ex);
		}
	}

	public String buildThumbnailUrl(String publicId) {
		return cloudinary.url()
				.secure(true)
				.transformation(new Transformation()
						.width(THUMBNAIL_WIDTH)
						.height(THUMBNAIL_HEIGHT)
						.crop("fill")
						.quality("auto"))
				.generate(publicId);
	}

	public static boolean isAllowedContentType(String contentType) {
		return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase());
	}
}
