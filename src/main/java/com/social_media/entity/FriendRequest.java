package com.social_media.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "friend_requests")
public class FriendRequest extends BaseEntity {
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "target_user_id")
  private User targetUser;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private Status status;

  protected FriendRequest() {}

  public FriendRequest(User user, User targetUser, Status status) {
    this.user = user;
    this.targetUser = targetUser;
    this.status = status;
  }

  public enum Status {
    ACCEPTED,
    PENDING
  }
}
