package com.social_media.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {
  @Column(name = "text", nullable = false)
  private String text;

  @Column(name = "time")
  private Instant time;

  @ManyToOne private User user;

  @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
  private List<Like> likes;

  @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
  private List<Comment> comments;

  protected Post() {}

  public Post(String text, Instant time, User user) {
    this.text = text;
    this.time = time;
    this.user = user;
  }
}
