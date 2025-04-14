package com.social_media.converters;

import com.social_media.entities.Post;
import com.social_media.entities.User;
import com.social_media.models.PostDto;
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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
                entity.getPostedTime().format(dateTimeFormatter)
        );
    }
}
