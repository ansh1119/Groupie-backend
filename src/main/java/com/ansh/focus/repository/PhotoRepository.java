package com.ansh.focus.repository;

import com.ansh.focus.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {

	Page<Photo> findByGroup_IdOrderByCreatedAtDesc(UUID groupId, Pageable pageable);

	@Query("""
			SELECT p FROM Photo p
			JOIN PhotoUserTag t ON t.photo = p
			WHERE t.user.id = :userId
			ORDER BY p.createdAt DESC
			""")
	Page<Photo> findPhotosTaggedForUser(@Param("userId") Long userId, Pageable pageable);
}
