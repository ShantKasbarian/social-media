package com.social_media.controller;

import com.social_media.converter.CommentConverter;
import com.social_media.converter.PostConverter;
import com.social_media.entity.Comment;
import com.social_media.entity.Like;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.model.LikeDto;
import com.social_media.model.PageDto;
import com.social_media.model.PostDto;
import com.social_media.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    private final PostConverter postConverter;

    private final CommentConverter commentConverter;

    @GetMapping
    public ResponseEntity<PageDto<Post, PostDto>> getPosts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                new PageDto<>(
                    postService.getFriendsPosts(
                            (User) authentication.getPrincipal(),
                            PageRequest.of(page, size)
                    ), postConverter
                )
        );
    }


    @GetMapping("/users/{userId}")
    public ResponseEntity<PageDto<Post, PostDto>> getUserPosts(
            Authentication authentication,
            @PathVariable UUID userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                new PageDto<>(
                    postService.getUserPosts(
                            (User) authentication.getPrincipal(), userId,
                        PageRequest.of(page, size, Sort.by(Sort.Order.desc("postedTime")))
                    ), postConverter
                )
        );
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, Authentication authentication) {
        return new ResponseEntity<>(
                postConverter.convertToModel(
                    postService.createPost(
                            (User) authentication.getPrincipal(),
                            postConverter.convertToEntity(postDto)
                    )
                ), HttpStatus.CREATED
        );
    }

    @PutMapping
    public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto, Authentication authentication) {
        return ResponseEntity.ok(
                postConverter.convertToModel(
                    postService.updatePost(
                            (User) authentication.getPrincipal(), postDto.id(),
                            postDto.title()
                    )
                )
        );
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(Authentication authentication, @PathVariable UUID postId) {
        postService.deletePost((User) authentication.getPrincipal(), postId);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(
            Authentication authentication, @PathVariable UUID postId
    ) {
        return ResponseEntity.ok(
                postConverter.convertToModel(
                    postService.getPostById((User) authentication.getPrincipal(), postId)
                )
        );
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeDto> likePost(
            Authentication authentication, @PathVariable UUID postId
    ) {
        Like like = postService.likePost((User) authentication.getPrincipal(), postId);

        return new ResponseEntity<>(
                new LikeDto(
                        like.getId(),
                        like.getUser().getId(),
                        like.getUser().getUsername(),
                        like.getPost().getId()
                ), HttpStatus.CREATED
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{postId}/dislike")
    public void removeLike(
            Authentication authentication, @PathVariable UUID postId
    ) {
        postService.removeLike((User) authentication.getPrincipal(), postId);
    }

    @GetMapping("/liked")
    public ResponseEntity<PageDto<Post, PostDto>> getUserLikedPosts(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                new PageDto<>(
                    postService.getUserLikedPosts(
                            (User) authentication.getPrincipal(),
                            PageRequest.of(page, size, Sort.by(Sort.Order.desc("postedTime")))
                    ), postConverter
                )
        );
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<PageDto<Comment, CommentDto>> getComments(
            Authentication authentication,
            @PathVariable UUID postId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                new PageDto<>(
                    postService.getComments(
                            (User) authentication.getPrincipal(),
                            postId,
                            PageRequest.of(page, size, Sort.by(Sort.Order.desc("commentedTime")))
                    ), commentConverter
                )
        );
    }
}
