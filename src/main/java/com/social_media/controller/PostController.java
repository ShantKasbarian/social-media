package com.social_media.controller;

import com.social_media.converter.CommentConverter;
import com.social_media.converter.ToEntityConverter;
import com.social_media.converter.ToModelConverter;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.PageDto;
import com.social_media.model.PostDto;
import com.social_media.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/posts")
@AllArgsConstructor
public class PostController {
    private static final String TIME_PROPERTY = "time";

    private final PostService postService;

    private final ToModelConverter<Post, PostDto> postToModelConverter;

    private final ToEntityConverter<Post, PostDto> postToEntityConverter;

    private final CommentConverter commentConverter;

    @PostMapping
    public ResponseEntity<PostDto> createPost(
            Authentication authentication, @RequestBody PostDto postDto
    ) {
        User user = (User) authentication.getPrincipal();

        var post = postToModelConverter.convertToModel(
                postService.createPost(user, postToEntityConverter.convertToEntity(postDto))
        );

        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable UUID postId) {
        var post = postToModelConverter.convertToModel(postService.getPostById(postId));

        return ResponseEntity.ok(post);
    }

    @PutMapping
    public ResponseEntity<PostDto> updatePost(
            Authentication authentication, @RequestBody PostDto postDto
    ) {
        User user = (User) authentication.getPrincipal();

        var post = postToModelConverter.convertToModel(
                postService.updatePost(user, postDto.id(), postDto.text())
        );

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(Authentication authentication, @PathVariable UUID postId) {
        User user = (User) authentication.getPrincipal();
        postService.deletePost(user, postId);
    }

    @GetMapping
    public ResponseEntity<PageDto<Post, PostDto>> getPosts(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        User user = (User) authentication.getPrincipal();

        var posts = new PageDto<>(
                postService.getFriendsPosts(user, PageRequest.of(page, size)),
                postToModelConverter
        );

        return ResponseEntity.ok(posts);
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<PageDto<Post, PostDto>> getUserPosts(
            Authentication authentication,
            @PathVariable UUID userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("time")));

        var posts = new PageDto<>(
                postService.getUserPosts(user, userId, pageable),
                postToModelConverter
        );

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/likes")
    public ResponseEntity<PageDto<Post, PostDto>> getUserLikedPosts(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        User user = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(TIME_PROPERTY)));

        var posts = new PageDto<>(
                postService.getUserLikedPosts(user, pageable), postToModelConverter
        );

        return ResponseEntity.ok(posts);
    }
}
