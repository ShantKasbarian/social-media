package com.social_media.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "content")
    private String content;

    @Column(name = "commented_time")
    private String commentedTime;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;
}
