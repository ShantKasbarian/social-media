package com.social_media.repositories;

import com.social_media.entities.Friend;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import com.social_media.entities.UserFriend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, String> {
    boolean existsByUserFriend(UserFriend userFriend);
    Optional<Friend> findByUserFriend(UserFriend userFriend);
    @Query("from Friend f where (f.userFriend.friend = :friend OR f.userFriend.user = :friend) and f.status = :status ")
    Page<Friend> findByUserFriend_FriendAndStatus(User friend, FriendshipStatus status, Pageable pageable);
}
