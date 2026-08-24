package com.social_media.converter;

import com.social_media.entity.Comment;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentConverter implements ToModelConverter<Comment, CommentDto> {

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
