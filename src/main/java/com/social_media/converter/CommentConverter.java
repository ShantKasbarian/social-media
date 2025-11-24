package com.social_media.converter;

import com.social_media.entity.Comment;
import com.social_media.entity.User;
import com.social_media.model.CommentDto;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class CommentConverter implements
        ToEntityConverter<Comment, CommentDto>,
        ToModelConverter<Comment, CommentDto> {

    @Override
    public Comment convertToEntity(CommentDto model) {
        Comment comment = new Comment();
        comment.setId(model.id());
        comment.setContent(model.comment());
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
