package com.ansh.focus.service;

import com.ansh.focus.exception.NotGroupMemberException;
import com.ansh.focus.exception.PhotoNotFoundException;
import com.ansh.focus.model.Photo;
import com.ansh.focus.model.User;
import com.ansh.focus.repository.GroupMemberRepository;
import com.ansh.focus.repository.PhotoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PhotoAccessService {

	private final PhotoRepository photoRepository;
	private final GroupMemberRepository groupMemberRepository;

	public PhotoAccessService(PhotoRepository photoRepository, GroupMemberRepository groupMemberRepository) {
		this.photoRepository = photoRepository;
		this.groupMemberRepository = groupMemberRepository;
	}

	public Photo getPhotoAndEnsureMember(UUID photoId, User user) {
		Photo photo = photoRepository.findById(photoId)
				.orElseThrow(() -> new PhotoNotFoundException("Photo not found"));

		ensureGroupMember(photo.getGroup().getId(), user);
		return photo;
	}

	public void ensureGroupMember(UUID groupId, User user) {
		if (!groupMemberRepository.existsByGroup_IdAndUser_Id(groupId, user.getId())) {
			throw new NotGroupMemberException("You must be a member of this group to access its photos");
		}
	}
}
