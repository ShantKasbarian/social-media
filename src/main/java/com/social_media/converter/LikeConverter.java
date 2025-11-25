package com.social_media.converter;

import com.social_media.entity.Like;
import com.social_media.entity.User;
import com.social_media.model.LikeDto;
import org.springframework.stereotype.Component;

@Component
public class LikeConverter implements ToModelConverter<Like, LikeDto> {
    @Override
    public LikeDto convertToModel(Like entity) {
        User user = entity.getUser();

        return new LikeDto(
                entity.getId(),
                user.getId(),
                user.getUsername(),
                entity.getPost().getId()
        );
    }
}
