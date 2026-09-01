package com.social_media.mapper;

import com.social_media.entity.Post;
import com.social_media.model.PostDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "likesCount", target = "likes")
  PostDto toModel(Post entity);
}
