package com.social_media.converter;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.PostDto;
import org.springframework.stereotype.Component;

@Component
public class PostConverter implements ToModelConverter<Post, PostDto> {
  @Override
  public PostDto convertToModel(Post entity) {
    User user = entity.getUser();

    int likes = 0;

    if (entity.getLikes() != null) {
      likes = entity.getLikes().size();
    }

    return new PostDto(
        entity.getId(),
        user.getId(),
        user.getUsername(),
        entity.getText(),
        (long) likes,
        entity.getTime());
  }
}
