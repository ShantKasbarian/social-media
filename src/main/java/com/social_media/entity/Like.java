package com.social_media.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "likes")
public class Like extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "post_id")
  private Post post;

  protected Like() {}

  public Like(User user, Post post) {
    this.user = user;
    this.post = post;
  }
}
