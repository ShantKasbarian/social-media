package com.social_media.controllers;

import com.social_media.converters.PostConverter;
import com.social_media.entities.Comment;
import com.social_media.entities.Like;
import com.social_media.entities.Post;
import com.social_media.entities.User;
import com.social_media.models.CommentDto;
import com.social_media.models.LikeDto;
import com.social_media.models.PageDto;
import com.social_media.models.PostDto;
import com.social_media.services.PostService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    private final PostConverter postConverter;

    @GetMapping
    public ResponseEntity<PageDto<Post, PostDto>> getPosts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                postService.getFriendsPosts(
                        (User) authentication.getPrincipal(),
                        PageRequest.of(page, size)
                )
        );
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<PageDto<Post, PostDto>> getUserPosts(
            Authentication authentication,
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                postService.getUserPosts(
                    userId,
                    PageRequest.of(page, size, Sort.by(Sort.Order.desc("postedTime"))),
                    (User) authentication.getPrincipal()
                )
        );
    }

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, Authentication authentication) {
        return new ResponseEntity<>(
                postConverter.convertToModel(
                    postService.createPost(
                            postConverter.convertToEntity(postDto),
                            (User) authentication.getPrincipal()
                    )
                ), HttpStatus.CREATED
        );
    }

    @PutMapping("/update")
    public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto, Authentication authentication) {
        return ResponseEntity.ok(
                postConverter.convertToModel(
                    postService.updatePost(
                            postDto.id(),
                            postDto.title(),
                            (User) authentication.getPrincipal()
                    )
                )
        );
    }

    @DeleteMapping("/{postId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(Authentication authentication, @PathVariable String postId) {
        postService.deletePost((User) authentication.getPrincipal(), postId);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable String postId, Authentication authentication) {
        return ResponseEntity.ok(
                postConverter.convertToModel(
                    postService.getPostById(postId, (User) authentication.getPrincipal())
                )
        );
    }

    @PutMapping("/{postId}/like")
    public ResponseEntity<LikeDto> likePost(@PathVariable String postId, Authentication authentication) {
        Like like = postService.likePost(postId, (User) authentication.getPrincipal());

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
    public void removeLike(@PathVariable String postId, Authentication authentication) {
        postService.removeLike(postId, (User) authentication.getPrincipal());
    }

    @GetMapping("/liked")
    public ResponseEntity<PageDto<Post, PostDto>> getUserLikedPosts(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                postService.getUserLikedPosts(
                        (User) authentication.getPrincipal(),
                        PageRequest.of(page, size, Sort.by(Sort.Order.desc("postedTime")))
                )
        );
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<PageDto<Comment, CommentDto>> getComments(
            Authentication authentication,
            @PathVariable String postId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                postService.getComments(
                        (User) authentication.getPrincipal(),
                        postId,
                        PageRequest.of(page, size, Sort.by(Sort.Order.desc("commentedTime")))
                )
        );
    }
}
