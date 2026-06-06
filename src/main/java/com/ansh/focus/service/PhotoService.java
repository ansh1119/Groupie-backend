package com.ansh.focus.service;

import com.ansh.focus.dto.CloudinaryUploadResult;
import com.ansh.focus.dto.MyPhotoPageResponse;
import com.ansh.focus.dto.MyPhotoResponse;
import com.ansh.focus.dto.PhotoPageResponse;
import com.ansh.focus.dto.PhotoResponse;
import com.ansh.focus.dto.PhotoUploadResponse;
import com.ansh.focus.exception.GroupNotFoundException;
import com.ansh.focus.exception.InvalidImageException;
import com.ansh.focus.exception.NotGroupMemberException;
import com.ansh.focus.model.Group;
import com.ansh.focus.model.Photo;
import com.ansh.focus.model.User;
import com.ansh.focus.repository.GroupMemberRepository;
import com.ansh.focus.repository.GroupRepository;
import com.ansh.focus.repository.PhotoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoService {

	private static final int MAX_PAGE_SIZE = 50;

	private final PhotoRepository photoRepository;
	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final CloudinaryService cloudinaryService;

	public PhotoService(
			PhotoRepository photoRepository,
			GroupRepository groupRepository,
			GroupMemberRepository groupMemberRepository,
			CloudinaryService cloudinaryService
	) {
		this.photoRepository = photoRepository;
		this.groupRepository = groupRepository;
		this.groupMemberRepository = groupMemberRepository;
		this.cloudinaryService = cloudinaryService;
	}

	@Transactional
	public PhotoUploadResponse uploadPhotos(UUID groupId, List<MultipartFile> files, User uploader) {
		Group group = getGroupAndEnsureMember(groupId, uploader);
		List<MultipartFile> validFiles = validateFiles(files);

		List<Photo> savedPhotos = new ArrayList<>();
		for (MultipartFile file : validFiles) {
			CloudinaryUploadResult uploadResult = cloudinaryService.upload(file, groupId);
			Photo photo = photoRepository.save(new Photo(
					group,
					uploader,
					uploadResult.imageUrl(),
					uploadResult.thumbnailUrl(),
					uploadResult.publicId()
			));
			savedPhotos.add(photo);
		}

		return PhotoUploadResponse.from(savedPhotos);
	}

	@Transactional(readOnly = true)
	public PhotoPageResponse getGroupPhotos(UUID groupId, User viewer, int page, int size) {
		getGroupAndEnsureMember(groupId, viewer);

		Pageable pageable = buildPageable(page, size);

		Page<PhotoResponse> photoPage = photoRepository
				.findByGroup_IdOrderByCreatedAtDesc(groupId, pageable)
				.map(PhotoResponse::fromListItem);

		return PhotoPageResponse.from(photoPage);
	}

	@Transactional(readOnly = true)
	public MyPhotoPageResponse getMyPhotos(User user, int page, int size) {
		Pageable pageable = buildPageable(page, size);

		Page<MyPhotoResponse> photoPage = photoRepository
				.findPhotosTaggedForUser(user.getId(), pageable)
				.map(MyPhotoResponse::from);

		return MyPhotoPageResponse.from(photoPage);
	}

	private Pageable buildPageable(int page, int size) {
		int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
		int pageNumber = Math.max(page, 0);
		return PageRequest.of(pageNumber, pageSize);
	}

	private Group getGroupAndEnsureMember(UUID groupId, User user) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new GroupNotFoundException("Group not found"));

		if (!groupMemberRepository.existsByGroup_IdAndUser_Id(groupId, user.getId())) {
			throw new NotGroupMemberException("You must be a member of this group to access its photos");
		}

		return group;
	}

	private List<MultipartFile> validateFiles(List<MultipartFile> files) {
		if (files == null || files.isEmpty()) {
			throw new InvalidImageException("At least one image file is required");
		}

		List<MultipartFile> validFiles = files.stream()
				.filter(file -> file != null && !file.isEmpty())
				.toList();

		if (validFiles.isEmpty()) {
			throw new InvalidImageException("At least one non-empty image file is required");
		}

		for (MultipartFile file : validFiles) {
			if (!CloudinaryService.isAllowedContentType(file.getContentType())) {
				throw new InvalidImageException(
						"Unsupported image type: " + file.getContentType()
								+ ". Allowed types: JPEG, PNG, GIF, WEBP"
				);
			}
		}

		return validFiles;
	}
}
