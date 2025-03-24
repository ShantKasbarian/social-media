package com.social_media.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "friends")
public class Friend {
    @Id
    @Column(name = "id")
    private String id;

    @ManyToMany(mappedBy = "friends")
    private List<User> users;

    @Column(name = "status")
    private FriendshipStatus status;
}
