package com.social_media.controller;

import com.social_media.converter.ToEntityConverter;
import com.social_media.converter.ToModelConverter;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.PageDto;
import com.social_media.model.PostDto;
import com.social_media.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.social_media.controller.CommentController.TIME_PROPERTY;

@RestController
@RequestMapping("/posts")
@Slf4j
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    private final ToModelConverter<Post, PostDto> postToModelConverter;

    private final ToEntityConverter<Post, PostDto> postToEntityConverter;

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            Authentication authentication, @RequestBody @Valid PostDto postDto
    ) {
        log.info("/posts called with POST, creating new post");

        User user = (User) authentication.getPrincipal();
        Post post = postToEntityConverter.convertToEntity(postDto);

        PostDto responseDto = postToModelConverter.convertToModel(
                postService.createPost(user, post)
        );

        log.info("created post");

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable UUID id) {
        log.info("/posts/{} with GET called, fetching post with the specified id", id);

        var post = postToModelConverter.convertToModel(postService.getPostById(id));

        log.info("fetched post with id {}", id);

        return ResponseEntity.ok(post);
    }

    @PutMapping
    public ResponseEntity<PostDto> updatePost(
            Authentication authentication, @RequestBody @Valid PostDto postDto
    ) {
        UUID postId = postDto.id();

        log.info("/posts with PUT called, updating post with id {}", postId);

        User user = (User) authentication.getPrincipal();

        var post = postToModelConverter.convertToModel(
                postService.updatePost(user, postId, postDto.text())
        );

        log.info("updated post with id {}", postId);

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(Authentication authentication, @PathVariable UUID postId) {
        log.info("/posts/{} with DELETE called, deleting post with the specified id", postId);

        User user = (User) authentication.getPrincipal();
        postService.deletePost(user, postId);

        log.info("deleted post with id {}", postId);
    }

    @GetMapping
    public ResponseEntity<PageDto<Post, PostDto>> getPostsByUserIdAcceptedFriendRequests(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        User user = (User) authentication.getPrincipal();
        UUID id = user.getId();

        log.info("/posts with GET called, fetching posts by user with id {} and ACCEPTED friendRequest status", id);

        Pageable pageable = PageRequest.of(page, size);

        var posts = new PageDto<>(
                postService.getPostsByUserIdAcceptedFriendRequests(id, pageable),
                postToModelConverter
        );

        log.info("fetched posts by user with id {} and ACCEPTED friendRequest status", id);

        return ResponseEntity.ok(posts);
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<PageDto<Post, PostDto>> getUserPosts(
            Authentication authentication,
            @PathVariable UUID userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        log.info("/posts/users/{} with GET called, fetching posts of user with the specified id", userId);

        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(TIME_PROPERTY)));

        var posts = new PageDto<>(
                postService.getUserPosts(user, userId, pageable),
                postToModelConverter
        );

        log.info("fetched posts of user with id {}", userId);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/likes")
    public ResponseEntity<PageDto<Post, PostDto>> getUserLikedPosts(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        User user = (User) authentication.getPrincipal();
        UUID id = user.getId();

        log.info("/posts/likes with GET called, fetching liked posts by user id {}", id);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(TIME_PROPERTY)));

        var posts = new PageDto<>(
                postService.getUserLikedPosts(id, pageable), postToModelConverter
        );

        log.info("fetched liked posts by user with id {}", id);

        return ResponseEntity.ok(posts);
    }
}
