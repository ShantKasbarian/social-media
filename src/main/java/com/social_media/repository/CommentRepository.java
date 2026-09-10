package com.social_media.repository;

import com.social_media.entity.Comment;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
  @Query(
      """
      FROM Comment c
      JOIN FETCH c.user
      WHERE c.post.id = :id
    """)
  Page<Comment> findByPostId(@Param("id") UUID id, Pageable pageable);
}
