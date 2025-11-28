package com.social_media.repository;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    Page<Post> findByUser(@Param("user") User user, Pageable pageable);

    @Query("""
        FROM Post p
        LEFT JOIN User u ON u.id = p.user.id
        LEFT JOIN FriendRequest f ON f.user.id = u.id OR f.targetUser.id = u.id
        WHERE (f.user.id = :userId OR f.targetUser.id = :userId) AND
        p.user.id != :userId AND f.status = 'ACCEPTED'
        ORDER BY p.time DESC
    """)
    Page<Post> findByUserAcceptedFriendRequests(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        FROM Post p
        LEFT JOIN Like l ON l.post.id = p.id
        WHERE l.user = :user
    """)
    Page<Post> findByUserLikes(@Param("user") User user, Pageable pageable);
}
