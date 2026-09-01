package com.social_media.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_blocks")
public class UserBlock extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "target_user_id")
  private User targetUser;

  protected UserBlock() {}

  public UserBlock(User user, User targetUser) {
    this.user = user;
    this.targetUser = targetUser;
  }
}
