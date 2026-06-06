package com.ansh.focus.service;

import com.ansh.focus.dto.PhotoTagResponse;
import com.ansh.focus.dto.TagPhotoRequest;
import com.ansh.focus.dto.TaggedUserResponse;
import com.ansh.focus.exception.InvalidTagTargetException;
import com.ansh.focus.exception.UserNotFoundException;
import com.ansh.focus.model.Photo;
import com.ansh.focus.model.PhotoUserTag;
import com.ansh.focus.model.TagType;
import com.ansh.focus.model.User;
import com.ansh.focus.repository.GroupMemberRepository;
import com.ansh.focus.repository.PhotoUserTagRepository;
import com.ansh.focus.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PhotoTagService {

	private final PhotoUserTagRepository photoUserTagRepository;
	private final PhotoAccessService photoAccessService;
	private final GroupMemberRepository groupMemberRepository;
	private final UserRepository userRepository;

	public PhotoTagService(
			PhotoUserTagRepository photoUserTagRepository,
			PhotoAccessService photoAccessService,
			GroupMemberRepository groupMemberRepository,
			UserRepository userRepository
	) {
		this.photoUserTagRepository = photoUserTagRepository;
		this.photoAccessService = photoAccessService;
		this.groupMemberRepository = groupMemberRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public PhotoTagResponse tagUsers(UUID photoId, TagPhotoRequest request, User tagger) {
		Photo photo = photoAccessService.getPhotoAndEnsureMember(photoId, tagger);
		UUID groupId = photo.getGroup().getId();

		Set<Long> uniqueUserIds = new LinkedHashSet<>(request.userIds());
		for (Long userId : uniqueUserIds) {
			if (photoUserTagRepository.existsByPhoto_IdAndUser_Id(photoId, userId)) {
				continue;
			}

			User taggedUser = userRepository.findById(userId)
					.orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

			if (!groupMemberRepository.existsByGroup_IdAndUser_Id(groupId, userId)) {
				throw new InvalidTagTargetException(
						"User " + userId + " is not a member of this photo's group"
				);
			}

			photoUserTagRepository.save(new PhotoUserTag(photo, taggedUser, tagger, TagType.MANUAL));
		}

		return getTags(photoId, tagger);
	}

	@Transactional(readOnly = true)
	public PhotoTagResponse getTags(UUID photoId, User viewer) {
		photoAccessService.getPhotoAndEnsureMember(photoId, viewer);

		List<TaggedUserResponse> tags = photoUserTagRepository.findByPhoto_IdOrderByCreatedAtAsc(photoId)
				.stream()
				.map(TaggedUserResponse::from)
				.toList();

		return PhotoTagResponse.of(photoId, tags);
	}

	@Transactional
	public void removeTag(UUID photoId, Long userId, User remover) {
		photoAccessService.getPhotoAndEnsureMember(photoId, remover);
		photoUserTagRepository.deleteByPhoto_IdAndUser_Id(photoId, userId);
	}
}
