package com.social_media.converter;

import com.social_media.entity.Post;
import com.social_media.entity.User;
import com.social_media.model.PostDto;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class PostConverter implements
        ToEntityConverter<Post, PostDto>,
        ToModelConverter<Post, PostDto> {

    @Override
    public Post convertToEntity(PostDto model) {
        Post post = new Post();
        post.setId(model.id());
        post.setTitle(model.title());
        return post;
    }

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
                entity.getTitle(),
                (long) likes,
                entity.getPostedTime()
        );
    }
}
