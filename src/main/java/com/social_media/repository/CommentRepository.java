package com.social_media.repository;

import com.social_media.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query("SELECT COUNT(c) > 0 FROM Comment c WHERE c.id = :id AND c.user.id = :userId")
    boolean existsByIdUserId(UUID id, UUID userId);
    Page<Comment> findByPostId(UUID id, Pageable pageable);
}
