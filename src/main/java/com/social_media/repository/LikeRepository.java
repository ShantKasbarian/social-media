package com.social_media.repository;

import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    boolean existsByPostAndUser(Post post, User user);

    @Query("FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
    Optional<Like> findByUserIdPostId(UUID userId, UUID postId);
}
