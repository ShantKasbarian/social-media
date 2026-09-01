package com.social_media.mapper;

import com.social_media.entity.Comment;
import com.social_media.model.CommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
  @Mapping(source = "post.id", target = "postId")
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.username", target = "username")
  CommentDto toModel(Comment entity);
}
