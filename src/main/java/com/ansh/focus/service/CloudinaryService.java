package com.ansh.focus.service;

import com.ansh.focus.dto.CloudinaryUploadResult;
import com.ansh.focus.exception.CloudinaryUploadException;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class CloudinaryService {

	private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

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
		String folder = baseFolder + "/" + groupId;
		log.debug(
				"Cloudinary upload starting: groupId={}, folder={}, originalFilename={}, size={}, contentType={}",
				groupId,
				folder,
				file.getOriginalFilename(),
				file.getSize(),
				file.getContentType()
		);

		@SuppressWarnings("unchecked")
		Map<String, Object> params = ObjectUtils.asMap(
				"folder", folder,
				"resource_type", "image",
				"overwrite", false
		);

		try {
			byte[] bytes = file.getBytes();
			log.debug("Read {} bytes from multipart file for Cloudinary upload", bytes.length);

			@SuppressWarnings("unchecked")
			Map<String, Object> result = cloudinary.uploader().upload(bytes, params);
			String imageUrl = (String) result.get("secure_url");
			String publicId = (String) result.get("public_id");

			if (imageUrl == null || publicId == null) {
				log.error(
						"Cloudinary returned incomplete metadata: secure_url={}, public_id={}, rawKeys={}",
						imageUrl,
						publicId,
						result.keySet()
				);
				throw new CloudinaryUploadException("Cloudinary upload succeeded but returned incomplete metadata");
			}

			String thumbnailUrl = buildThumbnailUrl(publicId);
			log.info("Cloudinary upload completed: publicId={}, imageUrl={}", publicId, imageUrl);
			return new CloudinaryUploadResult(imageUrl, thumbnailUrl, publicId);
		} catch (IOException ex) {
			log.error(
					"Failed to read multipart file bytes: originalFilename={}, size={}",
					file.getOriginalFilename(),
					file.getSize(),
					ex
			);
			throw new CloudinaryUploadException("Failed to read image file for upload", ex);
		} catch (Exception ex) {
			log.error(
					"Cloudinary API upload failed: groupId={}, originalFilename={}, message={}",
					groupId,
					file.getOriginalFilename(),
					ex.getMessage(),
					ex
			);
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
