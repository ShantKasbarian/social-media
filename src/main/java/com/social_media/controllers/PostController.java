package com.social_media.controllers;

import com.social_media.converters.PostConverter;
import com.social_media.entities.User;
import com.social_media.models.PageDto;
import com.social_media.models.PostDto;
import com.social_media.services.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    private final PostConverter postConverter;

    public PostController(PostService postService, PostConverter postConverter) {
        this.postService = postService;
        this.postConverter = postConverter;
    }

    @GetMapping
    public ResponseEntity<PageDto<PostDto>> getPosts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                postService.getFriendsPosts(
                        (User) authentication.getPrincipal(),
                        PageRequest.of(page, size, Sort.by(Sort.Order.desc("postedTime")))
                )
        );
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<PageDto<PostDto>> getUserPosts(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                postService.getUserPosts(
                    userId,
                    PageRequest.of(page, size, Sort.by(Sort.Order.desc("postedTime")))
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
    public ResponseEntity<PostDto> getPostById(@PathVariable String postId) {
        return ResponseEntity.ok(
                postConverter.convertToModel(
                    postService.getPostById(postId)
                )
        );
    }
}
