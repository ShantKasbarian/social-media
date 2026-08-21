package com.social_media.converter;

import com.social_media.entity.Comment;
import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import com.social_media.service.PostService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentConverter
    implements ToEntityConverter<Comment, CommentDto>, ToModelConverter<Comment, CommentDto> {
  private final PostService postService;

  @Override
  public Comment convertToEntity(CommentDto model) {
    UUID postId = model.postId();
    Post post = null;

    if (postId != null) {
      post = postService.getPostById(postId);
    }

    Comment comment = new Comment();
    comment.setId(model.id());
    comment.setText(model.text());
    comment.setPost(post);
    return comment;
  }

  @Override
  public CommentDto convertToModel(Comment entity) {
    User user = entity.getUser();

    return new CommentDto(
        entity.getId(),
        entity.getPost().getId(),
        entity.getText(),
        user.getId(),
        user.getUsername(),
        entity.getTime());
  }
}
