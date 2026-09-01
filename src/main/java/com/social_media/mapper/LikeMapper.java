package com.social_media.mapper;

import com.social_media.entity.Like;
import com.social_media.model.LikeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LikeMapper {
  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "post.id", target = "postId")
  LikeDto toModel(Like entity);
}
