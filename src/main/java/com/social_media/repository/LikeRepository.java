package com.social_media.repository;

import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, String> {
    boolean existsByPostAndUser(Post post, User user);
    Optional<Like> findByPostAndUser(Post post, User user);
}
