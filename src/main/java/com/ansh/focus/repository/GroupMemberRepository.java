package com.ansh.focus.repository;

import com.ansh.focus.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {

	boolean existsByGroup_IdAndUser_Id(UUID groupId, Long userId);
}
