package com.social_media.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "likes")
    private long likes;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @ManyToOne
    private User user;
}
