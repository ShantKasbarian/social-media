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
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final FriendRepository friendRepository;

    private final PostConverter postConverter;

    private final CommentRepository commentRepository;

    private final CommentConverter commentConverter;

    private final LikeRepository likeRepository;

    public Post createPost(Post post, User user) {
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            throw new InvalidProvidedInfoException("post must have a title");
        }

        post.setId(UUID.randomUUID().toString());
        post.setPostedTime(LocalDateTime.now());
        post.setUser(user);

        return postRepository.save(post);
    }

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

    public void deletePost(User user, String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RequestNotAllowedException("cannot delete the post of another user");
        }

        postRepository.delete(post);
    }

    public PageDto<PostDto> getFriendsPosts(User user, Pageable pageable) {
        Page<Post> posts = postRepository.findByUser_Friends(user.getId(), pageable);

        PageDto<PostDto> page = new PageDto<>();
        page.setContent(
                posts.getContent()
                        .stream()
                        .map(postConverter:: convertToModel)
                        .toList()
        );
        page.setPageNo(pageable.getPageNumber());
        page.setPageSize(posts.getSize());
        page.setTotalElements(posts.getContent().size());
        page.setTotalPages(posts.getTotalPages());
        page.setEmpty(posts.isEmpty());

        return page;
    }

    public PageDto<PostDto> getUserPosts(String userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        Page<Post> posts = postRepository.findByUser(user, pageable);

        PageDto<PostDto> page = new PageDto<>();
        page.setContent(
                posts.getContent()
                        .stream()
                        .map(postConverter:: convertToModel)
                        .toList()
        );

        page.setPageNo(pageable.getPageNumber());
        page.setPageSize(posts.getSize());
        page.setTotalElements(posts.getContent().size());
        page.setTotalPages(posts.getTotalPages());
        page.setEmpty(posts.isEmpty());

        return page;
    }

    public Post getPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));
    }

    public Post likePost(String id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new ResourceAlreadyExistsException("cannot like post more than once");
        }

        likeRepository.save(new Like(UUID.randomUUID().toString(), user, post));
        post.setLikes(likeRepository.findByPost(post));
        return post;
    }

    public PageDto<PostDto> getUserLikedPosts(User user, Pageable pageable) {
        Page<Post> posts = postRepository.findByLikesUser(user, pageable);

        PageDto<PostDto> page = new PageDto<>();

        page.setContent(
                posts.getContent()
                        .stream()
                        .map(postConverter:: convertToModel)
                        .toList()
        );

        page.setPageNo(pageable.getPageNumber());
        page.setPageSize(posts.getSize());
        page.setTotalElements(posts.getContent().size());
        page.setTotalPages(posts.getTotalPages());
        page.setEmpty(posts.isEmpty());

        return page;
    }

    public PageDto<CommentDto> getComments(String postId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPost_id(postId, pageable);

        PageDto<CommentDto> page = new PageDto<>();
        page.setContent(
                comments.getContent()
                        .stream()
                        .map(commentConverter:: convertToModel)
                        .toList()
        );

        page.setPageNo(pageable.getPageNumber());
        page.setPageSize(comments.getSize());
        page.setTotalElements(comments.getContent().size());
        page.setTotalPages(comments.getTotalPages());
        page.setEmpty(comments.isEmpty());

        return page;
    }
}
