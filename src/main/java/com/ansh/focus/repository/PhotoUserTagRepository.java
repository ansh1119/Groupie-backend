package com.ansh.focus.repository;

import com.ansh.focus.model.PhotoUserTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhotoUserTagRepository extends JpaRepository<PhotoUserTag, UUID> {

	List<PhotoUserTag> findByPhoto_IdOrderByCreatedAtAsc(UUID photoId);

	Optional<PhotoUserTag> findByPhoto_IdAndUser_Id(UUID photoId, Long userId);

	boolean existsByPhoto_IdAndUser_Id(UUID photoId, Long userId);

	void deleteByPhoto_IdAndUser_Id(UUID photoId, Long userId);
}
