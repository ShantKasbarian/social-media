package com.social_media.repositories;

import com.social_media.entities.Post;
import com.social_media.entities.User;
import org.hibernate.annotations.processing.HQL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, String> {
    Page<Post> findByUser(@Param("user") User user, Pageable pageable);

    @HQL(
            "from Post p " +
            "left join Friend f on f.friend = p.user " +
            "left join User u on u.id = f.user.id " +
            "where u = :user"
    )
    Page<Post> findByUser_Friends(@Param("user") User user, Pageable pageable);
}
