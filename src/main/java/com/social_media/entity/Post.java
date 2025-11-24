package com.social_media.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @Column(name = "title", nullable = false)
    private String title;

    @OneToMany(mappedBy = "post")
    private List<Like> likes;

    @Column(name = "posted_time")
    private LocalDateTime postedTime;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @ManyToOne
    private User user;
}
