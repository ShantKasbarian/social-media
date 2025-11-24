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
    
    @Query(value = """
        SELECT p.* FROM posts p
        LEFT JOIN users u ON u.id = p.user_id
        LEFT JOIN friend_requests f ON f.user_id = u.id OR f.friend_id = u.id
        WHERE (f.user_id = :userId OR f.friend_id = :userId) AND
        p.user_id != :userId AND f.status = 'ACCEPTED'
        ORDER BY p.posted_time DESC
    """, nativeQuery = true)
    Page<Post> findByUser_Friends(@Param("userId") UUID userId, Pageable pageable);

    @Query("""
        FROM Post p
        LEFT JOIN Like l ON l.post.id = p.id
        WHERE l.user = :user
    """)
    Page<Post> findByLikesUser(@Param("user") User user, Pageable pageable);
}
