package com.social_media.repositories;

import com.social_media.entities.Friend;
import com.social_media.entities.FriendshipStatus;
import com.social_media.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, String> {
    @Query("SELECT COUNT(f) = 1 from Friend f where (f.userFriend.user.id = :userId and f.userFriend.friend.id = :friendId) or (f.userFriend.user.id = :friendId and f.userFriend.friend.id = :userId)")
    boolean existsByUserFriend_user_id_friend_id(@Param("userId") String userId, @Param("friendId") String friendId);

    @Query("from Friend f where (f.userFriend.friend = :friend OR f.userFriend.user = :friend) and f.status = :status ")
    Page<Friend> findByUserFriend_FriendAndStatus(User friend, FriendshipStatus status, Pageable pageable);
}
