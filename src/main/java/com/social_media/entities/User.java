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
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "lastname")
    private String lastname;

    @OneToMany(mappedBy = "user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Friend> friends;
}
