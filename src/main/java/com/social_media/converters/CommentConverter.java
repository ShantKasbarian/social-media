package com.social_media.converters;

import com.social_media.entities.Comment;
import com.social_media.entities.User;
import com.social_media.models.CommentDto;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class CommentConverter implements Converter<Comment, CommentDto> {
    @Override
    public Comment convertToEntity(CommentDto model) {
        Comment comment = new Comment();
        comment.setId(model.id());
        comment.setContent(model.content());
        return comment;
    }

    @Override
    public CommentDto convertToModel(Comment entity) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        User user = entity.getUser();

        return new CommentDto(
                entity.getId(),
                entity.getPost().getId(),
                entity.getContent(),
                user.getId(),
                user.getUsername(),
                entity.getCommentedTime().format(dateTimeFormatter)
        );
    }
}
