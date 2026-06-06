package com.ansh.focus.service;

import com.ansh.focus.dto.AddGroupMemberRequest;
import com.ansh.focus.dto.CreateGroupRequest;
import com.ansh.focus.dto.GroupMemberResponse;
import com.ansh.focus.dto.GroupResponse;
import com.ansh.focus.exception.DuplicateGroupMemberException;
import com.ansh.focus.exception.GroupNotFoundException;
import com.ansh.focus.exception.NotGroupMemberException;
import com.ansh.focus.exception.UserNotFoundException;
import com.ansh.focus.model.Group;
import com.ansh.focus.model.GroupMember;
import com.ansh.focus.model.GroupRole;
import com.ansh.focus.model.User;
import com.ansh.focus.repository.GroupMemberRepository;
import com.ansh.focus.repository.GroupRepository;
import com.ansh.focus.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GroupService {

	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final UserRepository userRepository;

	public GroupService(
			GroupRepository groupRepository,
			GroupMemberRepository groupMemberRepository,
			UserRepository userRepository
	) {
		this.groupRepository = groupRepository;
		this.groupMemberRepository = groupMemberRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public GroupResponse createGroup(CreateGroupRequest request, User creator) {
		Group group = groupRepository.save(
				new Group(request.name(), request.description(), creator)
		);
		GroupMember ownerMembership = groupMemberRepository.save(
				new GroupMember(group, creator, GroupRole.OWNER)
		);
		return GroupResponse.from(group, List.of(ownerMembership));
	}

	@Transactional
	public GroupMemberResponse addMember(UUID groupId, AddGroupMemberRequest request) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new GroupNotFoundException("Group not found"));

		User userToAdd = userRepository.findByUsername(request.username().trim())
				.orElseThrow(() -> new UserNotFoundException("User not found: " + request.username()));

		if (groupMemberRepository.existsByGroup_IdAndUser_Id(groupId, userToAdd.getId())) {
			throw new DuplicateGroupMemberException("User is already a member of this group");
		}

		GroupMember membership = groupMemberRepository.save(
				new GroupMember(group, userToAdd, GroupRole.MEMBER)
		);
		return GroupMemberResponse.from(membership);
	}

	@Transactional(readOnly = true)
	public List<GroupResponse> listGroupsForUser(User user) {
		List<GroupMember> memberships = groupMemberRepository.findByUser_Id(user.getId());
		Map<UUID, Group> groupsById = new LinkedHashMap<>();

		for (GroupMember membership : memberships) {
			groupsById.putIfAbsent(membership.getGroup().getId(), membership.getGroup());
		}

		List<GroupResponse> responses = new ArrayList<>();
		for (Group group : groupsById.values()) {
			List<GroupMember> members = groupMemberRepository.findByGroup_Id(group.getId());
			responses.add(GroupResponse.from(group, members));
		}
		return responses;
	}

	@Transactional(readOnly = true)
	public GroupResponse getGroup(UUID groupId, User user) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new GroupNotFoundException("Group not found"));

		if (!groupMemberRepository.existsByGroup_IdAndUser_Id(groupId, user.getId())) {
			throw new NotGroupMemberException("You must be a member of this group to view its details");
		}

		List<GroupMember> members = groupMemberRepository.findByGroup_Id(groupId);
		return GroupResponse.from(group, members);
	}
}
