package com.social_media.repositories;

import com.social_media.entities.Like;
import com.social_media.entities.Post;
import com.social_media.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, String> {
    Long countLikesByPost_id(String postId);
    List<Like> findByPost(Post post);
    boolean existsByPostAndUser(Post post, User user);
}
