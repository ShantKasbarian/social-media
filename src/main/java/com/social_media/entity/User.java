package com.social_media.entity;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "username", unique = true, nullable = false)
  private String username;

  @Column(name = "firstname")
  private String firstname;

  @Column(name = "lastname")
  private String lastname;

  @OneToMany(mappedBy = "user")
  private List<Post> posts;

  @OneToMany(mappedBy = "user")
  private List<Comment> comments;

  @OneToMany(mappedBy = "user")
  private List<Like> likes;

  @ManyToMany
  @JoinTable(
      name = "friend_requests",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "target_user_id"))
  private List<FriendRequest> friendRequests;

  protected User() {}

  public User(String email, String password, String username, String firstname, String lastname) {
    this.email = email;
    this.password = password;
    this.username = username;
    this.firstname = firstname;
    this.lastname = lastname;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("user"));
  }
}
