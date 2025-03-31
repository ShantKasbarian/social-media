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
import com.social_media.repositories.CommentRepository;
import com.social_media.repositories.LikeRepository;
import com.social_media.repositories.PostRepository;
import com.social_media.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostConverter postConverter;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentConverter commentConverter;

    @Mock
    private LikeRepository likeRepository;

    private User user;

    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("someone@example.com");
        user.setPassword("Password123+");
        user.setUsername("johnDoe");
        user.setName("John");
        user.setLastname("Doe");

        post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setUser(user);
        post.setPostedTime(LocalDateTime.now());
        post.setTitle("some title");
    }

    @Test
    void createPost() {
        when(postRepository.save(post)).thenReturn(post);

        Post response = postService.createPost(post, user);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(post.getTitle(), response.getTitle());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void createPostShouldThrowInvalidProvidedInfoExceptionWhenTitleIsNull() {
        post.setTitle(null);
        assertThrows(InvalidProvidedInfoException.class, () -> postService.createPost(post, user));
    }

    @Test
    void createPostShouldThrowInvalidProvidedInfoExceptionWhenTitleIsEmpty() {
        post.setTitle("");
        assertThrows(InvalidProvidedInfoException.class, () -> postService.createPost(post, user));
    }

    @Test
    void updatePost() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        when(postRepository.save(post)).thenReturn(post);

        String oldTitle = post.getTitle();
        String expectedTitle = "some title 2";

        Post response = postService.updatePost(post.getId(), expectedTitle, user);

        assertEquals(post.getId(), response.getId());
        assertNotEquals(oldTitle, response.getTitle());
        assertEquals(expectedTitle, response.getTitle());
        assertEquals(post.getPostedTime(), response.getPostedTime());
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void updatePostShouldThrowResourceNotFoundException() {
        when(postRepository.findById(post.getId()))
                .thenThrow(new ResourceNotFoundException("post not found"));

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(post.getId(), "some title 2", user));
    }

    @Test
    void updatePostShouldThrowRequestNotAllowedException() {
        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());

        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));

        assertThrows(RequestNotAllowedException.class, () -> postService.updatePost(post.getId(), "some title 2", user2));

    }

    @Test
    void updatePostShouldThrowInvalidProvidedInfoExceptionWhenTitleIsNull() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        assertThrows(InvalidProvidedInfoException.class, () -> postService.updatePost(post.getId(), null, user));
    }

    @Test
    void updatePostShouldThrowInvalidProvidedInfoExceptionWhenTitleIsEmpty() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        assertThrows(InvalidProvidedInfoException.class, () -> postService.updatePost(post.getId(), "", user));
    }

    @Test
    void deletePost() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        doNothing().when(postRepository).delete(post);

        postService.deletePost(user, post.getId());

        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void deletePostShouldThrowResourceNotFoundException() {
        when(postRepository.findById(post.getId()))
            .thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(user, post.getId()));
    }

    @Test
    void deletePostShouldThrowRequestNotAllowedException() {
        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());

        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        assertThrows(RequestNotAllowedException.class, () -> postService.deletePost(user2, post.getId()));
    }

    @Test
    void getFriendsPosts() {
        User user2 = new User();
        user2.setId(UUID.randomUUID().toString());
        user2.setEmail("someone2@example.com");
        user2.setPassword("Password123+");
        user2.setUsername("Jack");
        user2.setName("Jack");
        user2.setLastname("Smith");

        post.setUser(user2);

        Post post2 = new Post();
        post2.setId(UUID.randomUUID().toString());
        post2.setUser(user2);
        post2.setPostedTime(LocalDateTime.now());
        post2.setTitle("some title");

        List<Post> posts = new ArrayList<>();
        posts.add(post);
        posts.add(post2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> page = new PageImpl<>(
                posts, pageable, posts.size()
        );

        when(postRepository.findByUser_Friends(user.getId(), pageable))
                .thenReturn(page);

        PageDto<Post, PostDto> response = postService.getFriendsPosts(user, pageable);

        assertEquals(page.getContent().size(), response.getContent().size());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertEquals(page.getNumber(), response.getPageNo());
        assertFalse(response.isEmpty());
        verify(postRepository, times(1)).findByUser_Friends(user.getId(), pageable);
    }

    @Test
    void getUserPosts() {
        Post post2 = new Post();
        post2.setId(UUID.randomUUID().toString());
        post2.setUser(user);
        post2.setPostedTime(LocalDateTime.now());
        post2.setTitle("some title");

        List<Post> posts = new ArrayList<>();
        posts.add(post);
        posts.add(post2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> page = new PageImpl<>(
                posts, pageable, posts.size()
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(postRepository.findByUser(user, pageable))
                .thenReturn(page);

        PageDto<Post, PostDto> response = postService.getUserPosts(user.getId(), pageable);

        assertEquals(page.getContent().size(), response.getContent().size());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertEquals(page.getNumber(), response.getPageNo());
        assertFalse(response.isEmpty());
        verify(postRepository, times(1)).findByUser(user, pageable);
    }

    @Test
    void getUserPostsShouldThrowResourceNotFoundException() {
        when(postRepository.findById(post.getId())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> postService.getUserPosts(user.getId(), PageRequest.of(0, 10)));
    }

    @Test
    void getPostById() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));

        Post response = postService.getPostById(post.getId());

        assertEquals(post.getId(), response.getId());
        assertEquals(post.getTitle(), response.getTitle());
        assertEquals(post.getUser().getId(), response.getUser().getId());
        verify(postRepository, times(1)).findById(post.getId());
    }

    @Test
    void getPostByIdShouldThrowResourceNotFoundException() {
        when(postRepository.findById(post.getId())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(post.getId()));
    }

    @Test
    void likePost() {
        Like like = new Like(UUID.randomUUID().toString(), user, post);

        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        when(likeRepository.existsByPostAndUser(post, user)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        Like response = postService.likePost(post.getId(), user);

        assertNotNull(response);
        assertEquals(like.getId(), response.getId());
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void likePostShouldThrowResourceNotFoundException() {
        when(postRepository.findById(post.getId())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, () -> postService.likePost(post.getId(), user));
    }

    @Test
    void likePostShouldThrowResourceAlreadyExistsException() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        when(likeRepository.existsByPostAndUser(post, user)).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> postService.likePost(post.getId(), user));
    }

    @Test
    void removeLike() {
        Like like = new Like(UUID.randomUUID().toString(), user, post);

        when(postRepository.findById(post.getId())).thenReturn(Optional.ofNullable(post));
        when(likeRepository.findByPostAndUser(post, user)).thenReturn(Optional.of(like));
        doNothing().when(likeRepository).delete(like);

        postService.removeLike(post.getId(), user);

        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void removeLikeShouldThrowResourceNotFoundExceptionWhenUserHasNotLikedPost() {
        when(postRepository.findById(anyString())).thenReturn(Optional.ofNullable(post));
        when(likeRepository.findByPostAndUser(any(Post.class), any(User.class))).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> postService.removeLike(post.getId(), user));
    }

    @Test
    void removeLikeShouldThrowResourceNotFoundExceptionWhenPostDoesNotExist() {
        when(postRepository.findById(anyString())).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> postService.removeLike(post.getId(), user));
    }

    @Test
    void getUserLikedPosts() {
        List<Like> likesPost1 = new ArrayList<>();
        likesPost1.add(new Like(UUID.randomUUID().toString(), user, post));
        post.setLikes(likesPost1);

        List<Like> likesPost2 = new ArrayList<>();

        Post post2 = new Post();
        post2.setId(UUID.randomUUID().toString());
        post2.setTitle("some title 2");
        post2.setUser(user);
        post2.setPostedTime(LocalDateTime.now());

        likesPost2.add(new Like(UUID.randomUUID().toString(), user, post2));
        post2.setLikes(likesPost2);

        List<Post> posts = new ArrayList<>();
        posts.add(post);
        posts.add(post2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("postedTime")));

        Page<Post> page = new PageImpl<>(posts, pageable, posts.size());

        when(postRepository.findByLikesUser(user, pageable)).thenReturn(page);

        PageDto<Post, PostDto> response = postService.getUserLikedPosts(user, pageable);

        assertEquals(page.getContent().size(), response.getContent().size());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertEquals(page.getNumber(), response.getPageNo());
        verify(postRepository, times(1)).findByLikesUser(user, pageable);
    }

    @Test
    void getComments() {
        List<Comment> comments = new ArrayList<>();
        Comment comment1 = new Comment();
        comment1.setId(UUID.randomUUID().toString());
        comment1.setPost(post);
        comment1.setUser(user);
        comment1.setContent("some comment");

        comments.add(comment1);
        comments.add(comment1);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Comment> page = new PageImpl<>(comments, pageable, comments.size());

        when(commentRepository.findByPost_id(post.getId(), pageable)).thenReturn(page);

        PageDto<Comment, CommentDto> response = postService.getComments(post.getId(), pageable);

        assertEquals(page.getContent().size(), response.getContent().size());
        assertEquals(page.getTotalPages(), response.getTotalPages());
        assertEquals(page.getSize(), response.getPageSize());
        verify(commentRepository, times(1)).findByPost_id(post.getId(), pageable);
    }
}