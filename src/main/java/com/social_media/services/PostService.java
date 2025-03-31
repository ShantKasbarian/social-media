package com.social_media.services;

import com.social_media.converters.CommentConverter;
import com.social_media.converters.PostConverter;
import com.social_media.entities.*;
import com.social_media.exceptions.InvalidProvidedInfoException;
import com.social_media.exceptions.RequestNotAllowedException;
import com.social_media.exceptions.ResourceAlreadyExistsException;
import com.social_media.exceptions.ResourceNotFoundException;
import com.social_media.models.CommentDto;
import com.social_media.models.PageDto;
import com.social_media.models.PostDto;
import com.social_media.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final PostConverter postConverter;

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    private final LikeRepository likeRepository;

    @Transactional
    public Post createPost(Post post, User user) {
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            throw new InvalidProvidedInfoException("post must have a title");
        }

        post.setId(UUID.randomUUID().toString());
        post.setPostedTime(LocalDateTime.now());
        post.setUser(user);

        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(String id, String title, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException("cannot modify the post of another user");
        }

        if (title == null || title.isEmpty()) {
            throw new InvalidProvidedInfoException("post must have a title");
        }

        post.setTitle(title);

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(User user, String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException("cannot delete the post of another user");
        }

        postRepository.delete(post);
    }

    public PageDto<Post, PostDto> getFriendsPosts(User user, Pageable pageable) {
        return new PageDto<>(
                postRepository.findByUser_Friends(user.getId(), pageable),
                postConverter
        );
    }

    public PageDto<Post, PostDto> getUserPosts(String userId, Pageable pageable) {
        return new PageDto<>(
                postRepository.findByUser(
                        userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("user not found")),
                        pageable
                ),
                postConverter
        );
    }

    public Post getPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));
    }

    @Transactional
    public Like likePost(String id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new ResourceAlreadyExistsException("cannot like post more than once");
        }

        return likeRepository.save(new Like(UUID.randomUUID().toString(), user, post));
    }

    @Transactional
    public void removeLike(String id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        likeRepository.delete(
                likeRepository.findByPostAndUser(post, user)
                        .orElseThrow(() -> new ResourceNotFoundException("you haven't liked this post"))
        );
    }

    public PageDto<Post, PostDto> getUserLikedPosts(User user, Pageable pageable) {
        return new PageDto<>(
                postRepository.findByLikesUser(user, pageable),
                postConverter
        );
    }

    public PageDto<Comment, CommentDto> getComments(String postId, Pageable pageable) {
        return new PageDto<>(
                commentRepository.findByPost_id(postId, pageable),
                commentConverter
        );
    }
}
