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
@Table(name = "friends")
public class Friend {
    @Id
    @Column(name = "id")
    private String id;

    @Embedded
    private UserFriend userFriend;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;
}
