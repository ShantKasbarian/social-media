package com.social_media.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {
  @Column(name = "text", nullable = false)
  private String text;

  @Column(name = "time")
  private Instant time;

  @ManyToOne(fetch = FetchType.LAZY)
  private Post post;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  protected Comment() {}

  public Comment(String text, Instant time, Post post, User user) {
    this.text = text;
    this.time = time;
    this.post = post;
    this.user = user;
  }
}
