package com.social_media.repositories;

import com.social_media.entities.Like;
import com.social_media.entities.Post;
import com.social_media.entities.User;
import org.hibernate.annotations.processing.HQL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    Page<Post> findByUser(@Param("user") User user, Pageable pageable);
    
    @Query(
            value = "select p.* from posts p " +
                "left join users u on u.id = p.user_id " +
                "left join friends f on f.user_id = u.id or f.friend_id = u.id " +
                "where (f.user_id = :userId or f.friend_id = :userId) and p.user_id != :userId and f.status = 'ACCEPTED' " +
                "order by p.posted_time DESC", nativeQuery = true
    )
    Page<Post> findByUser_Friends(@Param("userId") String userId, Pageable pageable);

    @Query(
            "from Post p " +
                "left join Like l on l.post.id = p.id " +
                "where l.user = :user"
    )
    Page<Post> findByLikesUser(@Param("user") User user, Pageable pageable);
}
