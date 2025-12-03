package com.social_media.repository;

import com.social_media.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    @Query("FROM Post p WHERE p.user.id = :userId")
    Page<Post> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        FROM Post p
        LEFT JOIN User u ON u.id = p.user.id
        LEFT JOIN FriendRequest f ON f.user.id = u.id OR f.targetUser.id = u.id
        WHERE (f.user.id = :userId OR f.targetUser.id = :userId) AND
        p.user.id != :userId AND f.status = 'ACCEPTED'
        ORDER BY p.time DESC
    """)
    Page<Post> findByUserIdAcceptedFriendRequests(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        FROM Post p
        LEFT JOIN Like l ON l.post.id = p.id
        WHERE l.user.id = :userId
    """)
    Page<Post> findByUserIdLikes(@Param("userId") UUID userId, Pageable pageable);
}
