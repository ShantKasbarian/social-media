package com.social_media.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "friend_requests")
public class FriendRequest {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    private User friend;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    public FriendRequest(User user, User friend, Status status) {
        this.user = user;
        this.friend = friend;
        this.status = status;
    }

    public enum Status {
        ACCEPTED,
        DECLINED,
        BLOCKED,
        PENDING
    }
}
